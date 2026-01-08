package in.hcdc.demo.config;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
/**
 *
 * @author Vaibhav
 */
@Component
public class StorageInitializer {
    
    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(AppStorageConfig.IMAGE_DIR);
    }
}
