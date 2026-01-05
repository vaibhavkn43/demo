package in.hcdc.demo.service;

import in.hcdc.demo.model.BiodataRequest;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
/**
 *
 * @author vaibhav
 */
@Service
public class BiodataImageService {

    public String generateBiodataImage(BiodataRequest data) throws Exception {

        InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream(
                        "static/images/templates/wedding/biodata.png");

        BufferedImage template = ImageIO.read(is);

        Graphics2D g = template.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setFont(new Font("Mangal", Font.PLAIN, 32));
        g.setColor(new Color(60, 0, 0));

        int x = 300;
        int y = 650;
        int gap = 48;

        y = drawLine(g, "नाव : ", data.getName(), x, y, gap);
        y = drawLine(g, "जन्म तारीख : ", data.getBirthDate(), x, y, gap);
        y = drawLine(g, "जन्म स्थळ : ", data.getBirthPlace(), x, y, gap);
        y = drawLine(g, "जन्म वेळ : ", data.getBirthTime(), x, y, gap);
        y = drawLine(g, "धर्म : ", data.getReligion(), x, y, gap);
        y = drawLine(g, "जात : ", data.getCaste(), x, y, gap);
        y = drawLine(g, "उंची : ", data.getHeight(), x, y, gap);
        y = drawLine(g, "शिक्षण : ", data.getEducation(), x, y, gap);
        y = drawLine(g, "पत्ता : ", data.getAddress(), x, y, gap);
        y = drawLine(g, "मोबाईल : ", data.getMobile(), x, y, gap);

        g.dispose();

        String fileName = "biodata-" + System.currentTimeMillis() + ".png";
        Path output = Path.of(
                "src/main/resources/static/generated/",
                fileName
        );

        ImageIO.write(template, "png", output.toFile());

        return "/generated/" + fileName;
    }

    private int drawLine(Graphics2D g, String label, String value,
                         int x, int y, int gap) {
        if (value == null || value.isBlank()) {
            return y;
        }
        g.drawString(label + value, x, y);
        return y + gap;
    }
}
