package in.hcdc.demo.model.layout;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import in.hcdc.demo.model.CustomFieldsLayout;
import in.hcdc.demo.model.FontConfig;
import in.hcdc.demo.model.MediaConfig;
import java.awt.Canvas;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
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
public class Layout {

    private List<Section> sections;
    private CustomFieldsLayout customFields;
    private FontConfig font;
    private ContentArea contentArea;
    private Map<String, LayoutModeConfig> layoutModes;
    private MediaConfig media;

}
