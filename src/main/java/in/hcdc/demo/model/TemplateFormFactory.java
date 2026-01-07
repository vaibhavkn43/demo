package in.hcdc.demo.model;

import org.springframework.stereotype.Component;

/**
 *
 * @author Vaibhav
 */
@Component
public class TemplateFormFactory {

    public TemplateForm getForm(String category) {

        return switch (category.toLowerCase()) {
            case "biodata" -> new BiodataRequest();
            case "birthday" -> new BirthdayRequest();   // future
            default -> throw new IllegalArgumentException(
                    "Unsupported category: " + category
            );
        };
    }
}

