package in.hcdc.demo.service;

import in.hcdc.demo.model.Category;
import in.hcdc.demo.model.Template;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author Vaibhav
 */
@Service
public class TemplateService {

    private final TemplateConfigService templateConfigService;

    public TemplateService(TemplateConfigService configService) {
        this.templateConfigService = configService;
    }

    public List<Category> getDashboardCategories() {
        return templateConfigService.getCategories();
    }

    public Category getCategoryByName(String key) {
        return templateConfigService.getCategoryByName(key);
    }

    public Template getTemplateById(String categoryName, String templateId) {
        Category category = getCategoryByName(categoryName);
        if (category == null) {
            return null;
        }

        return category.getTemplates()
                .stream()
                .filter(t -> t.getId().equals(templateId))
                .findFirst()
                .orElse(null);
    }
}
