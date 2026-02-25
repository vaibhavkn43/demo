package in.onlinebiodatamaker.util;

import in.onlinebiodatamaker.config.AppStorageConfig;
import in.onlinebiodatamaker.model.BiodataRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
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

    public BiodataRequest normalize(BiodataRequest form) {
        form.setFatherName(mergeWithBracket(form.getFatherName(), form.getFatherOccupation()));
        form.setMotherName(mergeWithBracket(form.getMotherName(), form.getMotherOccupation()));
        form.setCaste(mergeWithBracket(form.getCaste(), form.getSubCaste()));
        form.setRelatives(normalizeNameList(form.getRelatives()));
        form.setMama(normalizeCommaString(form.getMama()));
        form.setKaka(normalizeCommaString(form.getKaka()));
        form.setAlternateMobile(normalizeCommaString(form.getAlternateMobile()));
        return form;
    }

    private String mergeWithBracket(String main, String extra) {

        if (main == null || main.trim().isEmpty()) {
            return main;
        }

        if (extra == null || extra.trim().isEmpty()) {
            return main.trim();
        }

        return main.trim() + " (" + extra.trim() + ")";
    }

    private String normalizeCommaString(String value) {

        if (value == null || value.trim().isEmpty()) {
            return value;
        }

        // Split only on comma (not spaces)
        String[] parts = value.split(",");

        List<String> clean = new ArrayList<>();

        for (String p : parts) {
            if (p != null && !p.trim().isEmpty()) {
                clean.add(p.trim());
            }
        }

        return String.join(", ", clean);
    }

    public String normalizeNameList(String value) {

        if (value == null || value.trim().isEmpty()) {
            return value;
        }

        // Step 1: Replace commas with space so everything becomes one stream
        value = value.replace(",", " ");

        // Step 2: Split by space
        String[] tokens = value.trim().split("\\s+");

        List<String> clean = new ArrayList<>();

        for (String t : tokens) {
            if (!t.trim().isEmpty()) {
                clean.add(t.trim());
            }
        }

        // Step 3: Join with comma
        return String.join(", ", clean);
    }
}
