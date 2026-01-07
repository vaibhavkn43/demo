package in.hcdc.demo.controller;

import in.hcdc.demo.model.BiodataRequest;
import in.hcdc.demo.model.Template;
import in.hcdc.demo.model.TemplateForm;
import in.hcdc.demo.model.TemplateFormFactory;
import in.hcdc.demo.service.TemplateService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import in.hcdc.demo.service.BiodataImageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Vaibhav
 */
@Controller
@RequestMapping("/editor")
public class EditorController {

    private final TemplateService templateService;
    private final BiodataImageService biodataImageService;
    private final TemplateFormFactory formFactory;

    public EditorController(TemplateService templateService,
            BiodataImageService biodataImageService,
            TemplateFormFactory formFactory) {
        this.templateService = templateService;
        this.biodataImageService = biodataImageService;
        this.formFactory = formFactory;
    }

    // OPEN EDITOR (biodata / birthday / invitation)
    @GetMapping("/{categoryName}/{templateId}")
    public String openEditor(@PathVariable String categoryName,
            @PathVariable String templateId,
            Model model) {

        Template template = templateService.getTemplateById(categoryName, templateId);
        TemplateForm form = formFactory.getForm(categoryName);
        if (template == null) {
            return "redirect:/dashboard";
        }

        model.addAttribute("template", template);
        model.addAttribute("categoryName", categoryName);
        model.addAttribute("form", form);
        model.addAttribute("content", "editors/" + categoryName + "-editor");

        return "layout/base";
    }

    @PostMapping("/{categoryName}/preview")
    public String preview(@PathVariable String categoryName,
            @RequestParam String templateId,
            HttpServletRequest request,
            Model model) throws Exception {

        TemplateForm form = formFactory.getForm(categoryName);

        ServletRequestDataBinder binder
                = new ServletRequestDataBinder(form);
        binder.bind(request);

        String imageUrl;

        switch (categoryName) {
            case "biodata" ->
                imageUrl = biodataImageService.generateBiodataImage(
                        (BiodataRequest) form, templateId
                );
            default ->
                throw new IllegalArgumentException(
                        "Unsupported category: " + categoryName
                );
        }

        model.addAttribute("imagePath", imageUrl);
        model.addAttribute("content", "preview/" + categoryName + "-preview");

        return "layout/base";
    }

}
