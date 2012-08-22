package vinna.response;

import vinna.exception.VuntimeException;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

public class StringResponse extends AbstractResponse {

    public StringResponse(String content) {
        this(content, "UTF-8");
    }

    public StringResponse(String content, String encoding) {
        super(200);
        try {
            body(new ByteArrayInputStream(content.getBytes(encoding)));
        } catch (UnsupportedEncodingException e) {
            throw new VuntimeException("Invalid encoding", e);
        }
    }
}
