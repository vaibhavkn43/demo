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
    
    private String key;        // wedding, birthday
    private String title;      // Wedding Cards
    private List<Template> templates;
}
