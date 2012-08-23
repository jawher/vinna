package vinna.response;

import vinna.exception.VuntimeException;
import vinna.http.VinnaRequestWrapper;
import vinna.http.VinnaResponseWrapper;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Forward implements Response {

    private final String path;
    private final Map<String, Object> attributes;

    public Forward(String path) {
        this.path = path;
        this.attributes = new HashMap<>();
    }

    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public void execute(VinnaRequestWrapper request, VinnaResponseWrapper response) throws IOException, ServletException {
        for (Map.Entry<String, Object> param : attributes.entrySet()) {
            request.setAttribute(param.getKey(), param.getValue());
        }

        RequestDispatcher requestDispatcher = request.getRequestDispatcher(path);
        if (requestDispatcher != null) {
            requestDispatcher.forward(request, response);
        } else {
            throw new VuntimeException("cannot retrieve requestDispatcher");
        }

    }
}
