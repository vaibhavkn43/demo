package in.hcdc.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.hcdc.demo.model.Category;
import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 *
 * @author Vaibhav
 */
@Service
public class TemplateConfigService {

    private List<Category> categories;

    @PostConstruct
    public void loadTemplates() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream("templates-config/templates.json");

        categories = Arrays.asList(
                mapper.readValue(is, Category[].class)
        );
    }

    public List<Category> getCategories() {
        return categories;
    }

    public Category getCategoryByName(String name) {
        return categories.stream()
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
