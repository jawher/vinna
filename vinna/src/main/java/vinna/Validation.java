package vinna;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Validation {
    public interface Validator {
        public static class ValidationError extends Exception {
            public final String errorMessageKey;
            public final String errorMessage;

            private ValidationError(String errorMessageKey, String errorMessage) {
                this.errorMessageKey = errorMessageKey;
                this.errorMessage = errorMessage;
            }

            public static ValidationError withKey(String key) {
                return new ValidationError(key, null);
            }

            public static ValidationError withMessage(String message) {
                return new ValidationError(null, message);
            }
        }

        public void validate(String value) throws ValidationError;
    }

    private Map<String, List<String>> errors = new HashMap<>();
    private Map<String, String> firstErrors = new HashMap<>();

    private void addError(String key, String value) {
        List<String> list = errors.get(key);
        if (list == null) {
            list = new ArrayList<>();
            errors.put(key, list);
        }
        list.add(value);
        if (!firstErrors.containsKey(key)) {
            firstErrors.put(key, value);
        }
    }

    public Validation required(String value, String name) {
        if (value == null || value.trim().isEmpty()) {
            addError(name, Messages.format("vinna.required", name));
        }
        return this;
    }

    public Validation longerThan(String value, int length, String name) {
        if (value == null || value.trim().length() < length) {
            addError(name, Messages.format("vinna.longerThan", name, length));
        }
        return this;
    }

    public Validation shorterThan(String value, int length, String name) {
        if (value == null || value.trim().length() > length) {
            addError(name, Messages.format("vinna.shorterThan", name, length));
        }
        return this;
    }

    public Validation custom(Validator validator, String value, String name) {
        try {
            validator.validate(value);
        } catch (Validator.ValidationError validationError) {
            addError(name, validationError.errorMessage == null ?
                    Messages.format(validationError.errorMessageKey, name) :
                    validationError.errorMessage);
        }

        return this;
    }

    //TODO: moar validations


    public Map<String, List<String>> getErrors() {
        return errors;
    }

    public Map<String, String> getFirstErrors() {
        return firstErrors;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
