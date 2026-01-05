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

    public List<Category> getDashboardCategories() {

        Category wedding = new Category();
        wedding.setName("biodata");
        wedding.setTitle("Wedding Biodata");
        wedding.setTemplates(List.of(
                new Template("w1", "/images/templates/wedding/bio1.png"),
                new Template("w2", "/images/templates/wedding/2.png"),
                new Template("w3", "/images/templates/wedding/3.png")
        ));

        Category birthday = new Category();
        birthday.setName("birthday");
        birthday.setTitle("Birthday Cards");
        birthday.setTemplates(List.of(
                new Template("b1", "/images/templates/birthday/1.png"),
                new Template("b2", "/images/templates/birthday/2.png"),
                new Template("b3", "/images/templates/birthday/3.png")
        ));

        return List.of(wedding, birthday);
    }

    public Category getCategoryByName(String key) {

        return getDashboardCategories()
                .stream()
                .filter(cat -> cat.getName().equalsIgnoreCase(key))
                .findFirst()
                .orElse(null);
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
