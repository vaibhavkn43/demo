package in.hcdc.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class Section {

    private String key;
    private String title;
    private int startX;
    private int startY;
    private int maxWidth;
    private int lineHeight;
    private List<String> fields;
    @JsonProperty(defaultValue = "20")
    private int sectionGap = 20; // DEFAULT GAP
}
