package in.hcdc.demo.controller;

import in.hcdc.demo.model.BiodataRequest;
import in.hcdc.demo.model.Template;
import in.hcdc.demo.model.TemplateForm;
import in.hcdc.demo.model.TemplateFormFactory;
import in.hcdc.demo.service.BiodataImageRendererService;
import in.hcdc.demo.service.BiodataValidationService;
import in.hcdc.demo.service.TemplateService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author Vaibhav
 */
@Controller
@RequestMapping("/editor")
public class EditorController {

    private final TemplateService templateService;
    private final TemplateFormFactory formFactory;
    private final BiodataImageRendererService biodataImageRendererService;
    private final BiodataValidationService validationService;

    public EditorController(TemplateService templateService,
            TemplateFormFactory formFactory,
            BiodataImageRendererService biodataImageRendererService,
            BiodataValidationService validationService) {
        this.templateService = templateService;
        this.formFactory = formFactory;
        this.biodataImageRendererService = biodataImageRendererService;
        this.validationService = validationService;
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
    public String preview(
            @PathVariable String categoryName,
            @RequestParam String templateId,
            HttpServletRequest request,
            Model model) {

        TemplateForm form = formFactory.getForm(categoryName);

        ServletRequestDataBinder binder = new ServletRequestDataBinder(form);
        binder.bind(request);

        if ("biodata".equals(categoryName)) {

            BiodataRequest biodata = (BiodataRequest) form;

            var validation = validationService.validate(biodata);

            if (!validation.isValid()) {

                model.addAttribute("validationErrors", validation.getMissingKeys());
                model.addAttribute("content", "validation/minimum-validation");
                return "layout/base";
            }

            String imagePath = biodataImageRendererService
                    .renderBiodata(biodata, templateId);

            model.addAttribute("imagePath", imagePath);
        }

        model.addAttribute("categoryName", categoryName);
        model.addAttribute("templateId", templateId);
        model.addAttribute("content", "preview/image-preview");

        return "layout/base";
    }

//    @PostMapping("/{categoryName}/preview")
//    public String preview(
//            @PathVariable String categoryName,
//            @RequestParam String templateId,
//            HttpServletRequest request,
//            Model model) {
//
//        // 1️⃣ Create correct form dynamically
//        TemplateForm form = formFactory.getForm(categoryName);
//
//        // 2️⃣ Bind request parameters
//        ServletRequestDataBinder binder = new ServletRequestDataBinder(form);
//        binder.bind(request);
//
//        // 3️⃣ Pass everything to view
//        model.addAttribute("form", form);
//        model.addAttribute("templateId", templateId);
//        model.addAttribute("categoryName", categoryName);
//
//        model.addAttribute("content", "preview/" + categoryName + "-preview");
//        return "layout/base";
//    }
//    @PostMapping("/{categoryName}/preview")
//    public String preview(@PathVariable String categoryName,
//            @RequestParam String templateId,
//            HttpServletRequest request,
//            Model model) throws Exception {
//
//        TemplateForm form = formFactory.getForm(categoryName);
//
//        ServletRequestDataBinder binder
//                = new ServletRequestDataBinder(form);
//        binder.bind(request);
//
//        String imageUrl;
//
//        switch (categoryName) {
//            case "biodata" ->
//                imageUrl = biodataImageService.generateBiodataImage(
//                        (BiodataRequest) form, templateId
//                );
//            default ->
//                throw new IllegalArgumentException(
//                        "Unsupported category: " + categoryName
//                );
//        }
//
//        model.addAttribute("imagePath", imageUrl);
//        model.addAttribute("content", "preview/" + categoryName + "-preview");
//
//        return "layout/base";
//    }
}
