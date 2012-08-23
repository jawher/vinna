package vinna.exception;

public class InternalVinnaException extends RuntimeException {

    public InternalVinnaException() {
    }

    public InternalVinnaException(String msg) {
        super(msg);
    }

    public InternalVinnaException(String msg, Throwable e) {
        super(msg, e);
    }
}
