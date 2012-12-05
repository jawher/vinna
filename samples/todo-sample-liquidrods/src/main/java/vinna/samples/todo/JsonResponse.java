package vinna.samples.todo;

import com.fasterxml.jackson.databind.ObjectMapper;
import vinna.response.ResponseBuilder;

import javax.servlet.ServletOutputStream;
import java.io.IOException;

public class JsonResponse extends ResponseBuilder {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private Object entity;

    public JsonResponse() {
        this(200);
    }

    public JsonResponse(int status) {
        super(status);
        type("application/json");
    }

    public ResponseBuilder entity(Object entity) {
        this.entity = entity;
        return this;
    }

    @Override
    protected void writeBody(ServletOutputStream out) throws IOException {
        MAPPER.writeValue(out, entity);
    }
}
