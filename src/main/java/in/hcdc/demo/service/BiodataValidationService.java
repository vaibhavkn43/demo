package in.hcdc.demo.service;

import in.hcdc.demo.model.BiodataRequest;
import in.hcdc.demo.util.ValidationResult;
import org.springframework.stereotype.Service;
/**
 *
 * @author vaibh
 */
@Service
public class BiodataValidationService {

    public ValidationResult validate(BiodataRequest form) {

        ValidationResult result = new ValidationResult();

        if (isEmpty(form.getName()))
            result.addMissing("validation.required.name");

        if (isEmpty(form.getBirthDate()) && isEmpty(form.getAge()))
            result.addMissing("validation.required.birth");

        if (isEmpty(form.getBirthPlace()))
            result.addMissing("validation.required.birthPlace");

        if (isEmpty(form.getReligion()))
            result.addMissing("validation.required.religion");

        if (isEmpty(form.getCaste()))
            result.addMissing("validation.required.caste");

        if (isEmpty(form.getHeight()))
            result.addMissing("validation.required.height");

        if (isEmpty(form.getEducation()))
            result.addMissing("validation.required.education");

        if (isEmpty(form.getProfession()))
            result.addMissing("validation.required.profession");

        if (isEmpty(form.getMobile()))
            result.addMissing("validation.required.mobile");

        if (isEmpty(form.getAddress()))
            result.addMissing("validation.required.address");

        return result;
    }

  private boolean isEmpty(String v) {
        return v == null || v.isBlank();
    }
}