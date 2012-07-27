package vinna.route;

public class Parameters {
    public enum Type {VARIABLE, CONSTANT}

    public final Type type;
    public final String value;

    public Parameters(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    public final int toInt() {
        return 0;
    }

    public final String toString() {
        return "";
    }

    public final boolean toBoolean() {
        return false;
    }
}
