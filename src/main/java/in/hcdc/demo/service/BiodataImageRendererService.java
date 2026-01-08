package in.hcdc.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.hcdc.demo.config.AppStorageConfig;
import in.hcdc.demo.model.BiodataRequest;
import in.hcdc.demo.model.CustomField;
import in.hcdc.demo.model.Layout;
import in.hcdc.demo.model.Section;
import in.hcdc.demo.model.TemplateRenderer;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Service;

/**
 *
 * @author Vaibhav This service does everything:
 *
 * Load PNG
 *
 * Load layout.json
 *
 * Auto-flow sections
 *
 * Wrap text
 *
 * Render custom fields
 *
 * save image
 */
@Service
public class BiodataImageRendererService {

    private final ObjectMapper mapper = new ObjectMapper();

    public String renderBiodata(BiodataRequest form, String templateId) {
        try {

            /* =====================================================
               STEP 1: Load PNG template
               ===================================================== */
            BufferedImage canvas = ImageIO.read(
                    getClass().getResourceAsStream(
                            "/templates-assets/biodata/" + templateId + ".png"
                    )
            );

            /* =====================================================
               STEP 2: Load layout.json
               ===================================================== */
            Layout layout = mapper.readValue(
                    getClass().getResourceAsStream(
                            "/templates-assets/biodata/" + templateId + "-layout.json"
                    ),
                    Layout.class
            );

            /* =====================================================
               STEP 3: Create Graphics2D
               ===================================================== */
            Graphics2D g = canvas.createGraphics();
            g.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON
            );

            // Load font
            Font font = new Font(layout.getFont().getFamily(), Font.PLAIN, layout.getFont().getSize());
            g.setFont(font);
            g.setColor(Color.decode(layout.getFont().getColor()));
            FontMetrics fm = g.getFontMetrics();

            /* =====================================================
               STEP 4: Render SECTIONS (auto-flow)
               ===================================================== */
            for (Section section : layout.getSections()) {

                // collect non-empty fields
                List<Map.Entry<String, String>> values
                        = extractSectionValues(section, form);

                if (values.isEmpty()) {
                    continue; // skip entire section
                }

                int currentY = section.getStartY();

                // draw section title
                g.drawString(section.getTitle(), section.getStartX(), currentY);
                currentY += section.getLineHeight();

                // draw each field
                for (Map.Entry<String, String> entry : values) {
                    currentY += drawWrappedText(
                            g,
                            entry.getKey() + " : " + entry.getValue(),
                            section.getStartX(),
                            currentY,
                            section.getMaxWidth(),
                            section.getLineHeight(),
                            fm
                    );
                }
            }

            /* =====================================================
               STEP 5: Render CUSTOM FIELDS
               ===================================================== */
            int y = layout.getCustomFields().getStartY();

            for (CustomField cf : form.getCustomFields()) {
                if (isEmpty(cf.getLabel()) || isEmpty(cf.getValue())) {
                    continue;
                }

                y += drawWrappedText(
                        g,
                        cf.getLabel() + " : " + cf.getValue(),
                        layout.getCustomFields().getStartX(),
                        y,
                        layout.getCustomFields().getMaxWidth(),
                        layout.getCustomFields().getLineHeight(),
                        fm
                );
            }

            g.dispose();

            /* =====================================================
               STEP 6: Save Image
               ===================================================== */
            Path outputPath = AppStorageConfig.IMAGE_DIR
                    .resolve("biodata-" + System.currentTimeMillis() + ".png");

            ImageIO.write(canvas, "png", outputPath.toFile());

            return "/images/" + outputPath.getFileName();

        } catch (Exception e) {
            throw new RuntimeException("Failed to render biodata image", e);
        }
    }

    /* =====================================================
       Helper: Extract section values using reflection
       ===================================================== */
    private List<Map.Entry<String, String>> extractSectionValues(
            Section section, BiodataRequest form) {

        List<Map.Entry<String, String>> list = new ArrayList<>();

        for (String fieldName : section.getFields()) {
            try {
                Field field = BiodataRequest.class.getDeclaredField(fieldName);
                field.setAccessible(true);
                Object value = field.get(form);

                if (value != null && !value.toString().isBlank()) {
                    list.add(Map.entry(resolveLabel(fieldName), value.toString()));
                }
            } catch (Exception ignored) {
            }
        }
        return list;
    }

    /* =====================================================
       Helper: Draw wrapped text and return Y consumed
       ===================================================== */
    private int drawWrappedText(
            Graphics2D g,
            String text,
            int x,
            int y,
            int maxWidth,
            int lineHeight,
            FontMetrics fm) {

        List<String> lines = wrapText(text, fm, maxWidth);
        int startY = y;

        for (String line : lines) {
            g.drawString(line, x, y);
            y += lineHeight;
        }

        return y - startY;
    }

    /* =====================================================
       Helper: Word wrapping
       ===================================================== */
    private List<String> wrapText(
            String text, FontMetrics fm, int maxWidth) {

        List<String> lines = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (String word : text.split(" ")) {
            String test = current.length() == 0
                    ? word
                    : current + " " + word;

            if (fm.stringWidth(test) > maxWidth) {
                lines.add(current.toString());
                current = new StringBuilder(word);
            } else {
                current = new StringBuilder(test);
            }
        }

        if (!current.isEmpty()) {
            lines.add(current.toString());
        }

        return lines;
    }

    private boolean isEmpty(String s) {
        return s == null || s.isBlank();
    }

    private String resolveLabel(String fieldName) {
        // Later you can map this via messages.properties
        return switch (fieldName) {
            case "name" ->
                "नाव";
            case "birthDate" ->
                "जन्म तारीख";
            case "birthPlace" ->
                "जन्म स्थळ";
            case "education" ->
                "शिक्षण";
            default ->
                fieldName;
        };
    }
}
