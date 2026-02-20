package in.onlinebiodatamaker.controller;

import in.onlinebiodatamaker.model.BiodataRequest;
import in.onlinebiodatamaker.service.BiodataValidationService;
import in.onlinebiodatamaker.service.GodImageService;
import in.onlinebiodatamaker.service.TemplatesService;
import in.onlinebiodatamaker.util.ValidationResult;
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
    @Autowired
    private TemplatesService templatesService;

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {

        model.addAttribute("templates", templatesService.getTemplateIds());
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
        model.addAttribute("content", "preview");
        return "layout/base";
    }
}
