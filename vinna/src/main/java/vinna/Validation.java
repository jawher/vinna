package vinna;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vinna.exception.ConfigException;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.*;

public class Validation {
    private static final Logger logger = LoggerFactory.getLogger(Validation.class);

    private Map<String, List<String>> errors = new HashMap<>();
    private Map<String, String> firstErrors = new HashMap<>();

    private Validator validator;

    public Validation() {
        // NOp
    }

    public Validation(Validator validator) {
        this.validator = validator;
    }

    public Validation validate(Object object) {
        if (validator == null) {
            try {
                validator = LazyValidator.VALIDATOR;
            } catch (NoClassDefFoundError e) {
                logger.error("javax.validation.Validation is not available.");
                throw new ConfigException("javax.validation.Validation is not available. For using JSR303 and Bean Validation you have to add an implementation of this JSR on your classpath.");
            }
        }

        Set<ConstraintViolation<Object>> violations = validator.validate(object);
        for (ConstraintViolation<Object> violation : violations) {
            addError(violation.getPropertyPath().toString(), violation.getMessage());
        }
        return this;
    }

    public Validation addError(String name, String message) {
        List<String> list = errors.get(name);
        if (list == null) {
            list = new ArrayList<>();
            errors.put(name, list);
        }
        list.add(message);
        if (!firstErrors.containsKey(name)) {
            firstErrors.put(name, message);
        }
        return this;
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

    public boolean hasErrors(String name) {
        return errors.containsKey(name);
    }

    private static class LazyValidator {
        private static final Validator VALIDATOR = javax.validation.Validation.buildDefaultValidatorFactory().getValidator();
    }
}
