package in.onlinebiodatamaker.controller;

import in.onlinebiodatamaker.model.BiodataRequest;
import in.onlinebiodatamaker.model.Template;
import in.onlinebiodatamaker.service.BiodataValidationService;
import in.onlinebiodatamaker.service.GodImageService;
import in.onlinebiodatamaker.service.TemplatesService;
import in.onlinebiodatamaker.util.TimeHandlerUtil;
import in.onlinebiodatamaker.util.ValidationResult;
import jakarta.servlet.http.HttpSession;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
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
import org.springframework.web.bind.annotation.RequestParam;

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

        model.addAttribute("templates", templatesService.getAllTemplates());
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
    public String preview(BiodataRequest form, Model model, Locale locale,
            HttpSession session) {

        ValidationResult result = biodataValidationService.validate(form);

        if (form.getBirthTime() != null && !form.getBirthTime().isEmpty()) {
            LocalTime time = LocalTime.parse(form.getBirthTime());
            form.setBirthTime(TimeHandlerUtil.toMarathiTime(time, locale));
        }

        if (form.getGodImage() == null || form.getGodImage().isEmpty()) {
            form.setGodImage("ganesh");
        }

        if (form.getMantra() == null || form.getMantra().isEmpty()) {
            form.setMantra("|| श्री गणेशाय नमः ||");
        }

        if (!result.isValid()) {
            model.addAttribute("missingKeys", result.getMissingKeys());
            model.addAttribute("invalidValues", result.getInvalidKeys());
            model.addAttribute("form", form);
            model.addAttribute("content", "form");
            return "layout/base";
        }

        model.addAttribute("data", form);

        // 🔥 use template object directly
        Template template = templatesService.getById(form.getTemplateId());
        model.addAttribute("template", template);
        Boolean isPaid = (Boolean) session.getAttribute("PAID");
        model.addAttribute("isPaid", isPaid != null && isPaid);
        model.addAttribute("content", "preview");
        return "layout/base";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("content", "static/about");
        return "layout/base";
    }

    @GetMapping("/privacy")
    public String privacy(Model model) {
        model.addAttribute("content", "static/privacy");
        return "layout/base";
    }

    @GetMapping("/terms")
    public String terms(Model model) {
        model.addAttribute("content", "static/terms");
        return "layout/base";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("content", "static/contact");
        return "layout/base";
    }

    @GetMapping("/how-it-works")
    public String howItWorks(Model model) {
        model.addAttribute("content", "static/how-it-works");
        return "layout/base";
    }

    @GetMapping("/templates")
    public String templatesPage(
            @RequestParam(value = "religion", required = false) String religion,
            Model model) {

        List<Template> templates = templatesService.getByReligion(religion);

        model.addAttribute("templates", templates);
        model.addAttribute("selectedReligion", religion);
        model.addAttribute("religions", templatesService.getAvailableReligions());
        model.addAttribute("content", "templates");
        return "layout/base";
    }

}
