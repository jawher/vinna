package vinna.samples.todo;

import com.fasterxml.jackson.databind.ObjectMapper;
import vinna.route.ActionArgument;
import vinna.route.RouteResolution;

import java.io.IOException;

public class JacksonArgument implements ActionArgument {

    private final ObjectMapper mapper;

    public JacksonArgument() {
        this.mapper = new ObjectMapper();
    }

    @Override
    public Object resolve(RouteResolution.Action.Environment env, Class<?> targetType) {

        try {
            return mapper.readValue(env.request.getInputStream(), targetType);
        } catch (IOException e) {
            // FIXME:
            return null;
        }
    }

    @Override
    public boolean compatibleWith(Class<?> type) {
        return true;
    }

    public <T> T asT() {
        return null;
    }
}
