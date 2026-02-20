package in.onlinebiodatamaker.util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vaibhav
 */
public class ValidationResult {

    private boolean valid = true;
    private List<String> missingKeys = new ArrayList<>();
    private List<String> invalidKeys = new ArrayList<>();

    public void addMissing(String messageKey) {
        valid = false;
        missingKeys.add(messageKey);
    }

    public boolean isValid() {
        return valid;
    }

    public List<String> getMissingKeys() {
        return missingKeys;
    }

    public void addInvalid(String key) {
        invalidKeys.add(key);
    }

    public List<String> getInvalidKeys() {
        return invalidKeys;
    }
}
