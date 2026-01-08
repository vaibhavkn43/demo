package in.hcdc.demo.model;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author vaibhav
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BiodataRequest extends TemplateForm {
    
    private String name;
    private String birthDate;
    private String birthPlace;
    private String birthTime;
    private String religion;
    private String caste;
    private String height;
    private String education;
    private String address;
    private String mobile;
}
