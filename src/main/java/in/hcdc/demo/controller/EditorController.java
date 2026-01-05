package in.hcdc.demo.controller;

import in.hcdc.demo.model.Template;
import in.hcdc.demo.service.TemplateService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import in.hcdc.demo.model.TemplateForm;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 *
 * @author Vaibhav
 */

@Controller
@RequestMapping("/editor")
public class EditorController {

    private final TemplateService templateService;

    public EditorController(TemplateService templateService) {
        this.templateService = templateService;
    }

    @GetMapping("/{categoryKey}/{templateId}")
    public String openEditor(@PathVariable String categoryKey,
            @PathVariable String templateId,
            Model model) {

        Template template = templateService
                .getCategoryByKey(categoryKey)
                .getTemplates()
                .stream()
                .filter(t -> t.getId().equals(templateId))
                .findFirst()
                .orElse(null);

        if (template == null) {
            return "redirect:/dashboard";
        }

        model.addAttribute("content", "editor");
        model.addAttribute("template", template);
        model.addAttribute("form", new TemplateForm());
        model.addAttribute("categoryKey", categoryKey);

        return "layout/base";
    }

    @PostMapping("/preview/{categoryKey}")
    public String generatePreview(@PathVariable String categoryKey,
            @ModelAttribute TemplateForm form,
            Model model) {

        model.addAttribute("content", "preview");
        model.addAttribute("form", form);
        model.addAttribute("categoryKey", categoryKey);

        return "layout/base";
    }
}
