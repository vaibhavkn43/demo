package in.onlinebiodatamaker.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

/**
 *
 * @author admin
 */
@Service
public class GodImageService {

    public List<String> getGodImages() {

        List<String> images = new ArrayList<>();

        try {
            File folder = new ClassPathResource("static/images/gods").getFile();

            for (File file : folder.listFiles()) {
                if (file.getName().endsWith(".png")) {
                    images.add(file.getName().replace(".png", ""));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return images;
    }
}

