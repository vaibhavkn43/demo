package in.hcdc.demo.controller;

import in.hcdc.demo.model.BiodataRequest;
import java.util.List;
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

@GetMapping("/dashboard")
public String dashboard(Model model) {
    List<String> templates = List.of("t1", "t2", "t3", "t4", "t5", "t6");
    model.addAttribute("templates", templates);
    
    // Change this from "~{dashboard :: content}" to just "dashboard"
    model.addAttribute("content", "dashboard"); 
    return "layout/base";
}

@GetMapping("/editor/{templateId}")
public String openForm(@PathVariable String templateId, Model model) {
    model.addAttribute("templateId", templateId);
    model.addAttribute("form", new BiodataRequest());
    
    // Change this from "~{form :: content}" to just "form"
    model.addAttribute("content", "form");
    return "layout/base";
}

    @PostMapping("/preview")
    public String preview(BiodataRequest form, Model model) {
        model.addAttribute("data", form);
        model.addAttribute("templateId", form.getTemplateId());
        return "preview";
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
