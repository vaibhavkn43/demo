package in.hcdc.demo.controller;


import in.hcdc.demo.config.AppStorageConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
/**
 *
 * @author Vaibhav
 */
@Controller
public class ImageController {

   private static final Path IMAGE_DIR = AppStorageConfig.IMAGE_DIR;


    @GetMapping("/images/{fileName}")
    public ResponseEntity<Resource> serveImage(
            @PathVariable String fileName) throws MalformedURLException {

        Path imagePath = IMAGE_DIR.resolve(fileName);

        Resource resource = new UrlResource(imagePath.toUri());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(resource);
    }
}
