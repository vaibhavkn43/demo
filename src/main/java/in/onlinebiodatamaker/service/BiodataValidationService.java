package in.onlinebiodatamaker.service;

import in.onlinebiodatamaker.model.BiodataRequest;
import in.onlinebiodatamaker.util.ValidationResult;
import org.springframework.stereotype.Service;
/**
 *
 * @author vaibh
 */
@Service
public class BiodataValidationService {

    public ValidationResult validate(BiodataRequest form) {

        ValidationResult result = new ValidationResult();

        // ========= REQUIRED FIELDS =========

        if (isEmpty(form.getName()))
            result.addMissing("validation.required.name");

        if (isEmpty(form.getBirthDate()))
            result.addMissing("validation.required.birthDate");

        if (isEmpty(form.getBirthPlace()))
            result.addMissing("validation.required.birthPlace");

        if (isEmpty(form.getReligion()))
            result.addMissing("validation.required.religion");

        if (isEmpty(form.getCaste()))
            result.addMissing("validation.required.caste");

        if (isEmpty(form.getComplexion()))
            result.addMissing("validation.required.complexion");

        if (isEmpty(form.getHeight()))
            result.addMissing("validation.required.height");

        if (isEmpty(form.getEducation()))
            result.addMissing("validation.required.education");

        if (isEmpty(form.getFatherName()))
            result.addMissing("validation.required.fatherName");

        if (isEmpty(form.getMotherName()))
            result.addMissing("validation.required.motherName");

        if (isEmpty(form.getRelatives()))
            result.addMissing("validation.required.relatives");

        if (isEmpty(form.getMobile()))
            result.addMissing("validation.required.mobile");

        if (isEmpty(form.getAddress()))
            result.addMissing("validation.required.address");


        // ========= FORMAT VALIDATION =========

        if (!isEmpty(form.getName()) && !form.getName().matches("^[\\p{L} .'-]{2,50}$")) {
            result.addInvalid("validation.invalid.name");
        }

        if (!isEmpty(form.getMobile()) && !form.getMobile().matches("^[6-9]\\d{9}$")) {
            result.addInvalid("validation.invalid.mobile");
        }

        if (!isEmpty(form.getHeight()) && !form.getHeight().matches("^\\d{2,3}\\s?(cm|ft|in)?$")) {
            result.addInvalid("validation.invalid.height");
        }

        if (!isEmpty(form.getBirthDate()) && !form.getBirthDate().matches("^\\d{2}/\\d{2}/\\d{4}$")) {
            result.addInvalid("validation.invalid.birthDate");
        }

        return result;
    }

    private boolean isEmpty(String v) {
        return v == null || v.trim().isEmpty();
    }
}
