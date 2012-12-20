package vinna;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Validation {
    private Map<String, List<String>> errors = new HashMap<>();
    private Map<String, String> firstErrors = new HashMap<>();

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
}
