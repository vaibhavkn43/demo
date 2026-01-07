package in.hcdc.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.hcdc.demo.model.TemplateLayout;
import java.io.File;
import java.io.InputStream;
import org.springframework.stereotype.Service;

/**
 *
 * @author Vaibhav
 */
@Service
public class TemplateLayoutService {

    private final ObjectMapper mapper = new ObjectMapper();

    public TemplateLayout load(String category, String templateId)
            throws Exception {

        String path = "template-layouts/" + category + "-layouts" + File.separator + category + "-" + templateId + ".json";
             

        InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream(path);

        if (is == null) {
            throw new IllegalStateException("Layout not found: " + path);
        }

        return mapper.readValue(is, TemplateLayout.class);
    }
}
