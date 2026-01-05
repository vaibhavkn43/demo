package in.hcdc.demo.controller;

import in.hcdc.demo.model.BiodataRequest;
import in.hcdc.demo.model.Template;
import in.hcdc.demo.service.TemplateService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import in.hcdc.demo.service.BiodataImageService;
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
    private final BiodataImageService biodataImageService;

    public EditorController(TemplateService templateService,
            BiodataImageService biodataImageService) {
        this.templateService = templateService;
        this.biodataImageService = biodataImageService;
    }

    // ✅ OPEN BIODATA EDITOR
    // OPEN EDITOR (biodata / birthday / invitation)
    @GetMapping("/{categoryName}/{templateId}")
    public String openEditor(@PathVariable String categoryName,
            @PathVariable String templateId,
            Model model) {

        Template template = templateService.getTemplateById(categoryName, templateId);

        if (template == null) {
            return "redirect:/dashboard";
        }

        model.addAttribute("template", template);
        model.addAttribute("categoryName", categoryName);
        model.addAttribute("form", new BiodataRequest()); // later switch by category
        model.addAttribute("content", "editors/" + categoryName + "-editor");

        return "layout/base";
    }

    // ✅ BIODATA PREVIEW
    @PostMapping("/biodata/preview")
    public String biodataPreview(@ModelAttribute BiodataRequest form,
            Model model) throws Exception {

        String imageUrl = biodataImageService.generateBiodataImage(form);

        model.addAttribute("imageUrl", imageUrl);
        model.addAttribute("content", "preview/biodata-preview");
        return "layout/base";
    }
}
