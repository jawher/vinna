package vinna.response;

import vinna.exception.VuntimeException;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

// FIXME rename me
public class StringResponse extends ResponseBuilder {

    public StringResponse(String content) {
        this(content, "UTF-8");
    }

    public StringResponse(String content, String encoding) {
        super(200);
        if (content != null) {
            try {
                body(new ByteArrayInputStream(content.getBytes(encoding)));
            } catch (UnsupportedEncodingException e) {
                throw new VuntimeException("Invalid encoding", e);
            }
        }
    }
}
