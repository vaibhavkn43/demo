package in.onlinebiodatamaker.model;

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
public class Template {

    private String id;
    private String name;
    private String whichReligion;
    private boolean photo;
    private String emptyTemplateLocation;
    private String filledTemplateLocation;
}
