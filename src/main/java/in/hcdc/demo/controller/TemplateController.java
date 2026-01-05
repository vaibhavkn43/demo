package in.hcdc.demo.controller;

import in.hcdc.demo.model.Category;
import in.hcdc.demo.service.TemplateService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Vaibhav
 */
@Controller
@RequestMapping("/templates")
public class TemplateController {

    private final TemplateService templateService;

    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    @GetMapping("/{categoryKey}")
    public String viewCategoryTemplates(@PathVariable String categoryKey,
            Model model) {

        Category category = templateService.getCategoryByName(categoryKey);

        if (category == null) {
            return "redirect:/dashboard";
        }

        model.addAttribute("content", "category-templates");
        model.addAttribute("category", category);
        return "layout/base";
    }
}
