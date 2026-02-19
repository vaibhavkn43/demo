package in.hcdc.demo.controller;

import in.hcdc.demo.model.BiodataRequest;
import in.hcdc.demo.service.BiodataValidationService;
import in.hcdc.demo.service.GodImageService;
import in.hcdc.demo.util.ValidationResult;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 *
 * @author Vaibhav
 */
@Controller
public class DashboardController {

    @Autowired
    private BiodataValidationService biodataValidationService;
    @Autowired
    private GodImageService godImageService;
    @Autowired
    private MessageSource messageSource;

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        List<String> templates = List.of("t1", "t2", "t3", "t4", "t5", "t6");
        model.addAttribute("templates", templates);

        // Change this from "~{dashboard :: content}" to just "dashboard"
        model.addAttribute("content", "dashboard");
        return "layout/base";
    }

    @GetMapping("/editor/{templateId}")
    public String openForm(@PathVariable String templateId, Model model, Locale locale) {
        model.addAttribute("templateId", templateId);
        model.addAttribute("form", new BiodataRequest());
        model.addAttribute("godImages", godImageService.getGodImages());
        String list = messageSource.getMessage("mantra.list", null, locale);
        List<String> keys = Arrays.asList(list.split(","));
        model.addAttribute("mantraKeys", keys);
        model.addAttribute("content", "form");
        return "layout/base";
    }

    @PostMapping("/preview")
    public String preview(BiodataRequest form, Model model) {

        ValidationResult result = biodataValidationService.validate(form);

        if (!result.isValid()) {
            model.addAttribute("missingKeys", result.getMissingKeys());
            model.addAttribute("invalidValues", result.getInvalidKeys());
            model.addAttribute("form", form);
            model.addAttribute("content", "form");
            return "layout/base";
        }

        model.addAttribute("data", form);
        model.addAttribute("templateId", form.getTemplateId());

        // 🔥 IMPORTANT FIX
        model.addAttribute("content", "preview");

        return "layout/base";
    }

//    @GetMapping
//    public String dashboard(Model model) {
//        List<Category> categories = templateService.getDashboardCategories();
//        Map<String, Category> categoryMap = categories.stream()
//                .collect(Collectors.toMap(Category::getName, c -> c));
//        model.addAttribute("categoryMap", categoryMap);
//        model.addAttribute("content", "dashboard");
//        return "layout/base";
//    }
}
