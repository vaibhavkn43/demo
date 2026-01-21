package in.hcdc.demo.controller;

import in.hcdc.demo.config.AppStorageConfig;
import in.hcdc.demo.model.BiodataRequest;
import in.hcdc.demo.model.Template;
import in.hcdc.demo.model.TemplateForm;
import in.hcdc.demo.model.TemplateFormFactory;
import in.hcdc.demo.service.BiodataImageRendererService;
import in.hcdc.demo.service.BiodataValidationService;
import in.hcdc.demo.service.TemplateService;
import in.hcdc.demo.util.BiodataUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
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
import java.util.List;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

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
    private final PdfExportService pdfExportService;
    private final BiodataUtil biodataUtil;

    public EditorController(TemplateService templateService,
            TemplateFormFactory formFactory,
            BiodataImageRendererService biodataImageRendererService,
            PdfExportService pdfExportService,
            BiodataValidationService validationService,
            BiodataUtil biodataUtil) {
        this.templateService = templateService;
        this.formFactory = formFactory;
        this.biodataImageRendererService = biodataImageRendererService;
        this.validationService = validationService;
        this.pdfExportService = pdfExportService;
        this.biodataUtil = biodataUtil;
    }

    @GetMapping("/{categoryName}/{templateId}")
    public String openEditor(
            @PathVariable String categoryName,
            @PathVariable String templateId,
            Model model,
            HttpSession session
    ) {

        Template template = templateService.getTemplateById(categoryName, templateId);
        if (template == null) {
            return "redirect:/dashboard";
        }

        // ✅ STEP 1: Try to get form from session
        String sessionKey = "BIO_FORM_" + categoryName;
        TemplateForm form = (TemplateForm) session.getAttribute(sessionKey);

        // ✅ STEP 2: If not present, create new form (existing behavior)
        if (form == null) {
            form = formFactory.getForm(categoryName);
        }

        // ✅ STEP 3: Bind model as before
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
            @RequestParam(required = false) MultipartFile profileImage,
            HttpServletRequest request,
            Model model,
            HttpSession session) {

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
            String contentType = profileImage.getContentType();
            if (!List.of("image/jpeg", "image/png").contains(contentType)) {
                throw new IllegalArgumentException("Only JPG/PNG allowed");
            }

            if (profileImage != null && !profileImage.isEmpty()) {
                try {
                    String storedPath = biodataUtil.storeProfileImage(profileImage);
                    biodata.setProfileImagePath(storedPath);
                    biodata.setShowProfileImage(true);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to store profile image", e);
                }
            }
            String imageFile = biodataImageRendererService
                    .renderBiodata(biodata, templateId);
            model.addAttribute("imagePath", "/images/" + imageFile);
            model.addAttribute("imageFile", imageFile);

        }

        model.addAttribute("categoryName", categoryName);
        model.addAttribute("templateId", templateId);
        model.addAttribute("content", "preview/image-preview");
        session.setAttribute("BIO_FORM_" + categoryName, form);

        return "layout/base";
    }

    @GetMapping("/download/png/{fileName}")
    public ResponseEntity<Resource> downloadPng(@PathVariable String fileName)
            throws Exception {

        Path file = AppStorageConfig.IMAGE_DIR.resolve(fileName);
        Resource resource = new UrlResource(file.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header("Content-Disposition",
                        "attachment; filename=\"biodata.png\"")
                .body(resource);
    }

    @GetMapping("/download/pdf/{fileName}")
    public ResponseEntity<Resource> downloadPdf(
            @PathVariable String fileName) throws Exception {

        Path imagePath
                = AppStorageConfig.IMAGE_DIR.resolve(fileName);

        Path pdfPath
                = pdfExportService.createPdfFromImage(imagePath);

        Resource resource = new UrlResource(pdfPath.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header("Content-Disposition",
                        "attachment; filename=\"biodata.pdf\"")
                .body(resource);
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
