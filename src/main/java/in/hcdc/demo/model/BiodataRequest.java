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

    // ======================
    // Personal Information
    // ======================
    private String name;
    private String gender;
    private String birthDate;
    private String birthTime;
    private String birthPlace;
    private String age;
    private String religion;
    private String caste;
    private String subCaste;
    private String gotra;

    // ======================
    // Horoscope (optional)
    // ======================
    private String rashi;
    private String nakshatra;
    private String gan;
    private String nadi;
    private String mangal;

    // ======================
    // Physical Information
    // ======================
    private String height;
    private String weight;
    private String complexion;
    private String bloodGroup;
    private String disability;

    // ======================
    // Education & Profession
    // ======================
    private String education;
    private String profession;
    private String income;

    // ======================
    // Family Information
    // ======================
    private String fatherName;
    private String fatherOccupation;
    private String motherName;
    private String motherOccupation;
    private String brothers;
    private String sisters;

    // ======================
    // Address & Contact
    // ======================
    private String address;
    private String mobile;
    private String alternateMobile;
    private String email;
}
