package in.hcdc.demo.config;


import java.nio.file.Path;
import java.nio.file.Paths;
/**
 *
 * @author Vaibhav
 */
public class AppStorageConfig {

    public static final Path BASE_DIR =Paths.get(System.getProperty("user.home"), ".templateMaker");

    public static final Path IMAGE_DIR = BASE_DIR.resolve("images");
    
    public static final Path PROFILE_DIR = BASE_DIR.resolve("uploads/profile");
}
