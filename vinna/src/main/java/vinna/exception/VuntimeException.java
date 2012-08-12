package vinna.exception;

public class VuntimeException extends RuntimeException {

    public VuntimeException() {
        super();
    }

    public VuntimeException(String message) {
        super(message);
    }

    public VuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public VuntimeException(Throwable cause) {
        super(cause);
    }
}
