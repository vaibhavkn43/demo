package in.hcdc.demo.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Vaibhav
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Category {

    private String name;        // biodata, birthday
    private String titleKey;    // category.wedding, category.birthday
    private List<Template> templates;

    public List<Template> getDashboardTemplates() {
        return templates.size() > 4 ? templates.subList(0, 4) : templates;
    }
}

