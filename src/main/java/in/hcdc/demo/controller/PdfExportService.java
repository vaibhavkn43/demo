package in.hcdc.demo.controller;

import java.nio.file.Path;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

/**
 *
 * @author Vaibhav
 */
@Service
public class PdfExportService {

    public Path createPdfFromImage(Path imagePath) throws Exception {

        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        PDImageXObject image =
                PDImageXObject.createFromFile(imagePath.toString(), doc);

        PDPageContentStream cs =
                new PDPageContentStream(doc, page);

        float pageWidth = page.getMediaBox().getWidth();
        float scale = pageWidth / image.getWidth();

        cs.drawImage(
                image,
                0,
                page.getMediaBox().getHeight() - image.getHeight() * scale,
                image.getWidth() * scale,
                image.getHeight() * scale
        );

        cs.close();

        Path pdfPath = imagePath
                .getParent()
                .resolve(imagePath.getFileName().toString()
                        .replace(".png", ".pdf"));

        doc.save(pdfPath.toFile());
        doc.close();

        return pdfPath;
    }
}

