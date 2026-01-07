package in.hcdc.demo.service;

import in.hcdc.demo.model.BiodataRequest;
import in.hcdc.demo.model.TemplateLayout;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author vaibhav
 */
@Service
public class BiodataImageService {

    @Value("${app.generated.dir}")
    private String generatedDir;

    private final TemplateLayoutService layoutService;

    public BiodataImageService(TemplateLayoutService layoutService) {
        this.layoutService = layoutService;
    }

    public String generateBiodataImage(BiodataRequest data,
            String templateId) throws Exception {

        // 1️⃣ ensure output directory exists
        Path outputDir = Paths.get(generatedDir);
        Files.createDirectories(outputDir);

        // 2️⃣ load base template image
        InputStream is = getClass().getClassLoader()
                .getResourceAsStream(
                        "static/images/templates/biodata/" + templateId + ".png"
                );

        if (is == null) {
            throw new IllegalStateException("Template image not found");
        }

        BufferedImage template = ImageIO.read(is);
        Graphics2D g = template.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 3️⃣ load layout JSON
        TemplateLayout layout = layoutService.load("biodata", templateId);

        System.out.println(
                getClass().getClassLoader().getResource("fonts/mangal.ttf")
        );

        // 4️⃣ load font from layout
        InputStream fontStream
                = getClass().getClassLoader()
                        .getResourceAsStream("fonts/mangal.ttf");

        if (fontStream == null) {
            throw new IllegalStateException("Font not found in classpath: fonts/mangal.ttf");
        }

        Font font = Font.createFont(Font.TRUETYPE_FONT, fontStream)
                .deriveFont((float) layout.getFont().getSize());

        g.setFont(font);
        g.setColor(Color.decode(layout.getFont().getColor()));

        // 5️⃣ map form data
        Map<String, String> dataMap = Map.of(
                "name", data.getName(),
                "birthDate", data.getBirthDate(),
                "birthPlace", data.getBirthPlace(),
                "birthTime", data.getBirthTime(),
                "religion", data.getReligion(),
                "caste", data.getCaste(),
                "height", data.getHeight(),
                "education", data.getEducation(),
                "address", data.getAddress(),
                "mobile", data.getMobile()
        );

        // 6️⃣ draw text using layout
        layout.getFields().forEach((field, pos) -> {
            String value = dataMap.get(field);
            if (value != null && !value.isBlank()) {
                g.drawString(value, pos.getX(), pos.getY());
            }
        });

        g.dispose();

        // 7️⃣ write output
        String fileName = "biodata-" + System.currentTimeMillis() + ".png";
        Path outputFile = outputDir.resolve(fileName);

        ImageIO.write(template, "png", outputFile.toFile());

        return "/generated/" + fileName;
    }
}
