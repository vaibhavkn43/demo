package in.hcdc.demo.controller;

import in.hcdc.demo.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Vaibhav
 */
@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private TemplateService templateService;

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("content", "dashboard");
        model.addAttribute("categories", templateService.getDashboardCategories());
        return "layout/base";
    }

}
