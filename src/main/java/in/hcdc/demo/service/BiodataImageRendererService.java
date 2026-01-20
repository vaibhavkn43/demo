package in.hcdc.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.hcdc.demo.config.AppStorageConfig;
import in.hcdc.demo.model.BiodataRequest;
import in.hcdc.demo.model.CustomField;
import in.hcdc.demo.model.MediaItem;
import in.hcdc.demo.model.layout.Layout;
import in.hcdc.demo.model.layout.LayoutMode;
import in.hcdc.demo.model.layout.LayoutModeConfig;
import in.hcdc.demo.model.layout.Section;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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

@Service
public class BiodataImageRendererService {

    private final ObjectMapper mapper = new ObjectMapper();
    private final MessageSource messageSource;

    private static final int LABEL_PADDING = 15;
    private static final int COLON_PADDING = 30;

    public BiodataImageRendererService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String renderBiodata(BiodataRequest form, String templateId) {

        try {
            /* =====================================================
           STEP 1: Load base template image
           ===================================================== */
            BufferedImage canvas = ImageIO.read(
                    getClass().getResourceAsStream(
                            "/templates-assets/biodata/" + templateId + "/base.png"
                    )
            );

            /* =====================================================
           STEP 2: Load layout JSON
           ===================================================== */
            Layout layout = mapper.readValue(
                    getClass().getResourceAsStream(
                            "/templates-assets/biodata/" + templateId + "/layout.json"
                    ),
                    Layout.class
            );

            /* =====================================================
           STEP 3: Graphics setup
           ===================================================== */
            Graphics2D g = canvas.createGraphics();
            g.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON
            );

            /* =====================================================
           STEP 4: Load base font
           ===================================================== */
            Font baseFont;
            try {
                baseFont = Font.createFont(
                        Font.TRUETYPE_FONT,
                        getClass().getResourceAsStream(
                                "/fonts/NotoSerifDevanagari-Regular.ttf"
                        )
                );
            } catch (Exception e) {
                baseFont = new Font("Serif", Font.PLAIN, layout.getFont().getSize());
            }

            g.setColor(Color.decode(layout.getFont().getColor()));

            FontMetrics baseFm = g.getFontMetrics(
                    baseFont.deriveFont(Font.PLAIN, (float) layout.getFont().getSize())
            );

            /* =====================================================
           STEP 5: Detect layout mode
           ===================================================== */
            LayoutMode mode = detectLayoutMode(form, layout, baseFm, canvas);

            LayoutModeConfig modeCfg
                    = layout.getLayoutModes().get(mode.name().toLowerCase());

            Font renderFont
                    = baseFont.deriveFont(Font.PLAIN, (float) modeCfg.getFontSize());

            g.setFont(renderFont);
            FontMetrics fm = g.getFontMetrics();

            int effectiveLineHeight = modeCfg.getLineHeight();
            int sectionGap = modeCfg.getSectionGap();

            /* =====================================================
           STEP 6: Render GOD image + mantra (auto-adjust)
           ===================================================== */
            int currentY = renderGodAndMantra(g, canvas, form, baseFont);

            /* =====================================================
           STEP 7: Detect profile photo presence
           ===================================================== */
            boolean profilePresent
                    = layout.getMedia() != null
                    && layout.getMedia().getProfile() != null
                    && layout.getMedia().getProfile().isEnabled()
                    && !isEmpty(form.getProfileImagePath());

            int profileBottomY = Integer.MAX_VALUE;

            if (profilePresent) {
                MediaItem profile = layout.getMedia().getProfile();

                int px = canvas.getWidth()
                        - layout.getContentArea().getRight()
                        - profile.getWidth();

                int py = layout.getContentArea().getTop()
                        + profile.getMarginTop();

                BufferedImage profileImg
                        = ImageIO.read(new File(form.getProfileImagePath()));

                g.drawImage(
                        profileImg.getScaledInstance(
                                profile.getWidth(),
                                profile.getHeight(),
                                Image.SCALE_SMOOTH
                        ),
                        px,
                        py,
                        null
                );

                profileBottomY = py + profile.getHeight();
            }

            /* =====================================================
           STEP 8: Render sections (AUTO FLOW)
           ===================================================== */
            for (Section section : layout.getSections()) {

                List<Map.Entry<String, String>> values
                        = extractSectionValues(section, form);

                if (values.isEmpty()) {
                    continue;
                }

                boolean sideFlowAllowed
                        = profilePresent && currentY < profileBottomY;

                int contentLeft = layout.getContentArea().getLeft();
                int contentRight = canvas.getWidth() - layout.getContentArea().getRight();

                if (sideFlowAllowed) {
                    contentRight -= layout.getMedia().getProfile().getWidth() + 20;
                }

                int availableWidth = contentRight - contentLeft;

                // Section title
                g.drawString(
                        section.getTitle(),
                        contentLeft,
                        currentY
                );

                currentY += effectiveLineHeight;

                int maxLabelWidth = 0;
                for (Map.Entry<String, String> e : values) {
                    maxLabelWidth = Math.max(
                            maxLabelWidth,
                            fm.stringWidth(e.getKey())
                    );
                }

                int labelX = contentLeft;
                int colonX = labelX + maxLabelWidth + LABEL_PADDING;
                int valueX = colonX + COLON_PADDING;

                for (Map.Entry<String, String> e : values) {

                    g.drawString(e.getKey(), labelX, currentY);
                    g.drawString(":", colonX, currentY);

                    currentY += drawWrappedValue(
                            g,
                            e.getValue(),
                            valueX,
                            currentY,
                            availableWidth - (valueX - labelX),
                            effectiveLineHeight,
                            fm
                    );
                }

                currentY += sectionGap;
            }

            /* =====================================================
           STEP 9: Render custom fields (full width)
           ===================================================== */
            for (CustomField cf : form.getCustomFields()) {

                if (isEmpty(cf.getLabel()) || isEmpty(cf.getValue())) {
                    continue;
                }

                currentY += drawWrappedText(
                        g,
                        cf.getLabel() + " : " + cf.getValue(),
                        layout.getContentArea().getLeft(),
                        currentY,
                        canvas.getWidth()
                        - layout.getContentArea().getLeft()
                        - layout.getContentArea().getRight(),
                        effectiveLineHeight,
                        fm
                );
            }

            /* =====================================================
           STEP 10: Watermark & branding
           ===================================================== */
            if (currentY < canvas.getHeight() - 300) {
                drawWatermark(g, canvas);
            }

            g.setFont(baseFont.deriveFont(Font.PLAIN, 12f));
            g.setColor(new Color(120, 120, 120));

            String brand = "VivahKala.in | 9022658566";
            int bw = g.getFontMetrics().stringWidth(brand);

            g.drawString(
                    brand,
                    canvas.getWidth() - bw - 20,
                    canvas.getHeight() - 20
            );

            g.dispose();

            /* =====================================================
           STEP 11: Save image
           ===================================================== */
            Path out = AppStorageConfig.IMAGE_DIR
                    .resolve("biodata-" + System.currentTimeMillis() + ".png");

            ImageIO.write(canvas, "png", out.toFile());

            return out.getFileName().toString();

        } catch (Exception e) {
            throw new RuntimeException("Failed to render biodata image", e);
        }
    }

    /* =====================================================
       Helper methods
       ===================================================== */
    private int renderGodAndMantra(
            Graphics2D g,
            BufferedImage canvas,
            BiodataRequest form,
            Font baseFont
    ) throws IOException {

        // =========================
        // 1. Skip if no god selected
        // =========================
        if (form.getGodImage() == null
                || form.getGodImage().equalsIgnoreCase("none")) {
            return 240; // much earlier start
        }

        // =========================
        // 2. Load god image
        // =========================
        BufferedImage godImg = loadImage(
                "/static/images/gods/" + form.getGodImage() + ".png"
        );

        // =========================
        // 3. Scale god image (SMALL)
        // =========================
        int maxGodHeight = 100; // 🔑 compact devotional size
        double scale = (double) maxGodHeight / godImg.getHeight();

        int scaledW = (int) (godImg.getWidth() * scale);
        int scaledH = (int) (godImg.getHeight() * scale);

        Image scaled = godImg.getScaledInstance(
                scaledW,
                scaledH,
                Image.SCALE_SMOOTH
        );

        // =========================
        // 4. Position god image (HIGHER)
        // =========================
        int x = canvas.getWidth() / 2 - scaledW / 2;
        int y = 80; // 🔑 higher placement

        g.drawImage(scaled, x, y, null);

        int currentY = y + scaledH + 12;

        // =========================
        // 5. Draw mantra (compact)
        // =========================
        if (!isEmpty(form.getMantra())) {

            Font mantraFont = baseFont.deriveFont(Font.PLAIN, 22f);
            g.setFont(mantraFont);

            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(form.getMantra());

            int textX = canvas.getWidth() / 2 - textWidth / 2;
            int textY = currentY + fm.getAscent();

            g.drawString(form.getMantra(), textX, textY);

            currentY = textY + 18; // tighter gap
        }

        // =========================
        // 6. Return content start Y
        // =========================
        return Math.max(currentY + 16, 240);
    }

    private LayoutMode detectLayoutMode(
            BiodataRequest form,
            Layout layout,
            FontMetrics fm,
            BufferedImage canvas
    ) {

        int godBlockHeight = 140;

        int availableHeight
                = canvas.getHeight()
                - layout.getContentArea().getBottom()
                - layout.getContentArea().getTop()
                - godBlockHeight;

        for (LayoutMode mode : LayoutMode.values()) {

            LayoutModeConfig cfg
                    = layout.getLayoutModes()
                            .get(mode.name().toLowerCase());

            int estimatedHeight
                    = estimateContentHeight(form, layout, cfg, fm);

            if (estimatedHeight <= availableHeight) {
                return mode;
            }
        }

        return LayoutMode.COMPACT;
    }

    private List<Map.Entry<String, String>> extractSectionValues(
            Section section,
            BiodataRequest form
    ) {

        List<Map.Entry<String, String>> list = new ArrayList<>();

        for (String fieldName : section.getFields()) {
            try {
                Field f = BiodataRequest.class.getDeclaredField(fieldName);
                f.setAccessible(true);
                Object v = f.get(form);

                if (v != null && !v.toString().isBlank()) {
                    list.add(Map.entry(
                            resolveLabel(fieldName),
                            v.toString()
                    ));
                }
            } catch (Exception ignored) {
            }
        }
        return list;
    }

    private int drawWrappedValue(
            Graphics2D g,
            String text,
            int x,
            int y,
            int maxWidth,
            int lineHeight,
            FontMetrics fm
    ) {

        int startY = y;
        for (String line : wrap(text, fm, maxWidth)) {
            g.drawString(line, x, y);
            y += lineHeight;
        }
        return y - startY;
    }

    private int drawWrappedText(
            Graphics2D g,
            String text,
            int x,
            int y,
            int maxWidth,
            int lineHeight,
            FontMetrics fm
    ) {
        int startY = y;
        for (String line : wrap(text, fm, maxWidth)) {
            g.drawString(line, x, y);
            y += lineHeight;
        }
        return y - startY;
    }

    private List<String> wrap(
            String text,
            FontMetrics fm,
            int maxWidth
    ) {

        List<String> lines = new ArrayList<>();
        StringBuilder cur = new StringBuilder();

        for (String w : text.split(" ")) {
            String t = cur.length() == 0 ? w : cur + " " + w;
            if (fm.stringWidth(t) > maxWidth) {
                lines.add(cur.toString());
                cur = new StringBuilder(w);
            } else {
                cur = new StringBuilder(t);
            }
        }
        if (!cur.isEmpty()) {
            lines.add(cur.toString());
        }
        return lines;
    }

    private void drawWatermark(Graphics2D g, BufferedImage c) {
        g.setComposite(
                AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER, 0.04f
                )
        );
        g.setFont(g.getFont().deriveFont(Font.BOLD, 120f));
        g.setColor(new Color(180, 160, 120));
        g.drawString(
                "शुभ विवाह",
                c.getWidth() / 2 - 260,
                c.getHeight() / 2
        );
        g.setComposite(AlphaComposite.SrcOver);
    }

    private BufferedImage loadImage(String path) throws IOException {
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) {
                throw new FileNotFoundException(path);
            }
            return ImageIO.read(is);
        }
    }

    private boolean isEmpty(String s) {
        return s == null || s.isBlank();
    }

    private String resolveLabel(String fieldName) {
        Locale locale = LocaleContextHolder.getLocale();
        try {
            return messageSource.getMessage(
                    "editor.form." + fieldName,
                    null,
                    locale
            );
        } catch (Exception e) {
            return fieldName;
        }
    }

    private int estimateContentHeight(
            BiodataRequest form,
            Layout layout,
            LayoutModeConfig mode,
            FontMetrics fm
    ) {

        int height = 0;

        for (Section section : layout.getSections()) {

            int sectionLines = 0;

            for (String fieldName : section.getFields()) {
                try {
                    Field f = BiodataRequest.class.getDeclaredField(fieldName);
                    f.setAccessible(true);
                    Object v = f.get(form);

                    if (v != null && !v.toString().isBlank()) {
                        int lines = Math.max(
                                1,
                                (fm.stringWidth(v.toString()) + 200) / section.getMaxWidth()
                        );
                        sectionLines += lines;
                    }
                } catch (Exception ignored) {
                }
            }

            if (sectionLines > 0) {
                height += mode.getLineHeight();               // section title
                height += sectionLines * mode.getLineHeight();
                height += mode.getSectionGap();
            }
        }

        return height;
    }

    private void renderProfilePhoto(
            Graphics2D g,
            BufferedImage canvas,
            Layout layout,
            BiodataRequest form
    ) throws IOException {

        MediaItem p = layout.getMedia().getProfile();

        if (p == null || !p.isEnabled() || isEmpty(form.getProfileImagePath())) {
            return;
        }

        BufferedImage img = ImageIO.read(new File(form.getProfileImagePath()));

        int x = canvas.getWidth()
                - p.getWidth()
                - layout.getContentArea().getRight();

        int y = layout.getContentArea().getTop() + p.getMarginTop();

        g.drawImage(
                img.getScaledInstance(p.getWidth(), p.getHeight(), Image.SCALE_SMOOTH),
                x,
                y,
                null
        );
    }

    private Rectangle calculateContentBounds(
            Layout layout,
            BiodataRequest form,
            BufferedImage canvas
    ) {

        int left = layout.getContentArea().getLeft();
        int right = canvas.getWidth() - layout.getContentArea().getRight();

        MediaItem profile = layout.getMedia().getProfile();

        if (profile != null && profile.isEnabled()
                && form.getProfileImagePath() != null) {

            // Reserve space on right
            right -= profile.getWidth() + 20;
        }

        return new Rectangle(left, 0, right - left, canvas.getHeight());
    }

    private int renderTopMedia(
            Graphics2D g,
            BufferedImage canvas,
            Layout layout,
            BiodataRequest form
    ) throws IOException {

        int y = layout.getContentArea().getTop();

        MediaItem god = layout.getMedia().getGod();

        if (god != null && god.isEnabled()
                && form.getGodImage() != null
                && !"none".equalsIgnoreCase(form.getGodImage())) {

            BufferedImage img = loadImage(
                    "/static/images/gods/" + form.getGodImage() + ".png"
            );

            int h = god.getHeight();
            int w = img.getWidth() * h / img.getHeight();

            int x = canvas.getWidth() / 2 - w / 2;

            g.drawImage(
                    img.getScaledInstance(w, h, Image.SCALE_SMOOTH),
                    x,
                    y,
                    null
            );

            y += h + god.getMarginBottom();
        }

        if (!isEmpty(form.getMantra())) {
            g.drawString(
                    form.getMantra(),
                    canvas.getWidth() / 2 - g.getFontMetrics().stringWidth(form.getMantra()) / 2,
                    y
            );
            y += 28;
        }

        return y;
    }

}
