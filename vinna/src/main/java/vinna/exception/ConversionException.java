package vinna.exception;

public class ConversionException extends InternalVinnaException {

    public ConversionException(String msg) {
        super(msg);
    }

    public ConversionException(String msg, Throwable e) {
        super(msg, e);
    }
}
