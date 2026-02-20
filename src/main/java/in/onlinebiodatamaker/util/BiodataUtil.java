package in.onlinebiodatamaker.util;

import in.onlinebiodatamaker.config.AppStorageConfig;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Vaibhav
 */
@Service
public class BiodataUtil {

    public String storeProfileImage(MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            return null;
        }

        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());

        String name = "profile-" + System.currentTimeMillis() + "." + ext;

        Path target = AppStorageConfig.PROFILE_DIR.resolve(name);

        Files.copy(file.getInputStream(), target);

        return target.toString(); // store path
    }

}
