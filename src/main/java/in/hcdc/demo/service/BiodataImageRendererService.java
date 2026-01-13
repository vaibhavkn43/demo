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
import java.util.Locale;
import java.util.Map;
import javax.imageio.ImageIO;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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

    private final MessageSource messageSource;

    // ⭐ CHANGE: Typography paddings
    private static final int LABEL_PADDING = 20;
    private static final int COLON_PADDING = 15;

    public BiodataImageRendererService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String renderBiodata(BiodataRequest form, String templateId) {

        System.out.println("form::"+form);
        try {

            /* =====================================================
               STEP 1: Load PNG template
               ===================================================== */
            BufferedImage canvas = ImageIO.read(
                    getClass().getResourceAsStream(
                            "/templates-assets/biodata/"+templateId + File.separator + templateId + ".png"
                    )
            );

            /* =====================================================
               STEP 2: Load layout.json
               ===================================================== */
            Layout layout = mapper.readValue(
                    getClass().getResourceAsStream(
                            "/templates-assets/biodata/"+templateId + File.separator + templateId + "-layout.json"
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

            // ⭐ CHANGE: Better Marathi font with fallback
            Font font;
            try {
                font = Font.createFont(
                        Font.TRUETYPE_FONT,
                        getClass().getResourceAsStream(
                                "/fonts/NotoSerifDevanagari-Regular.ttf"
                        )
                ).deriveFont(Font.PLAIN, layout.getFont().getSize());
            } catch (Exception e) {
                try {
                    font = Font.createFont(
                            Font.TRUETYPE_FONT,
                            getClass().getResourceAsStream("/fonts/Mangal.ttf")
                    ).deriveFont(Font.PLAIN, layout.getFont().getSize());
                } catch (Exception ex) {
                    font = new Font("Serif", Font.PLAIN, layout.getFont().getSize());
                }
            }

            g.setFont(font);
            g.setColor(Color.decode(layout.getFont().getColor()));
            FontMetrics fm = g.getFontMetrics();

            /* =====================================================
               STEP 4: Render SECTIONS (FLOW BASED)
               ===================================================== */
            int currentY = layout.getSections().get(0).getStartY();

            for (Section section : layout.getSections()) {

                List<Map.Entry<String, String>> values
                        = extractSectionValues(section, form);

                if (values.isEmpty()) {
                    continue;
                }

                // section title
                g.drawString(section.getTitle(), section.getStartX(), currentY);
                currentY += section.getLineHeight();

                // section fields
                // ⭐ CHANGE: Calculate max label width for this section
                int maxLabelWidth = 0;
                for (Map.Entry<String, String> entry : values) {
                    maxLabelWidth = Math.max(
                            maxLabelWidth,
                            fm.stringWidth(entry.getKey())
                    );
                }

// ⭐ CHANGE: Define column positions
                int labelX = section.getStartX();
                int colonX = labelX + maxLabelWidth + LABEL_PADDING;
                int valueX = colonX + COLON_PADDING;

// ⭐ CHANGE: Draw each row (label | colon | value)
                for (Map.Entry<String, String> entry : values) {

                    // Label
                    g.drawString(entry.getKey(), labelX, currentY);

                    // Colon (aligned vertically!)
                    g.drawString(":", colonX, currentY);

                    // Value (wrapped under same column)
                    currentY += drawWrappedValue(
                            g,
                            entry.getValue(),
                            valueX,
                            currentY,
                            section.getMaxWidth() - (valueX - labelX),
                            (int) (section.getLineHeight() * 1.1), // ⭐ better Marathi spacing
                            fm
                    );
                }

                // gap after each section
                currentY += section.getSectionGap();
            }

            /* =====================================================
               STEP 5: Render CUSTOM FIELDS (CONTINUE FLOW)
               ===================================================== */
            for (CustomField cf : form.getCustomFields()) {

                if (isEmpty(cf.getLabel()) || isEmpty(cf.getValue())) {
                    continue;
                }

                currentY += drawWrappedText(
                        g,
                        cf.getLabel() + " : " + cf.getValue(),
                        layout.getCustomFields().getStartX(),
                        currentY,
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
       Extract section values using reflection
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
       Draw wrapped text
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
       Word wrapping
       ===================================================== */
    private List<String> wrapText(String text, FontMetrics fm, int maxWidth) {

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

    // ⭐ CHANGE: Wrap ONLY value text (not label/colon)
    private int drawWrappedValue(
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

    private boolean isEmpty(String s) {
        return s == null || s.isBlank();
    }

    private String resolveLabel(String fieldName) {
        Locale locale = LocaleContextHolder.getLocale();
        String key = "editor.form." + fieldName;

        try {
            return messageSource.getMessage(key, null, locale);
        } catch (Exception e) {
            // fallback: show field name if key missing
            return fieldName;
        }
    }

}
