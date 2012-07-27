package vinna.route;

public class ActionArgument {
    public enum Type {VARIABLE, CONSTANT}

    public final Type type;
    public final String value;

    public ActionArgument(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    public final int toInt() {
        return 42;
    }

    public final String toString() {
        return "42";
    }

    public final boolean toBoolean() {
        return false;
    }
}
