package in.hcdc.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *
 * @author Vaibhav
 */
@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Value("${app.generated.dir}")
    private String generatedDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/generated/**")
                .addResourceLocations("file:" + generatedDir + "/");
    }
}

