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
    
    private String name;        // biodata, birthday , invitation
    private String title;      // Wedding biodata, birthday wishes, etc
    private List<Template> templates;
}
