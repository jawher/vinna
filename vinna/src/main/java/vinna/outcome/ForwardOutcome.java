package vinna.outcome;

import vinna.exception.VuntimeException;
import vinna.request.Request;
import vinna.response.Response;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ForwardOutcome implements Outcome {

    private final String path;
    private final Map<String, Object> attributes;

    public ForwardOutcome(String path) {
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
    public void execute(Request request, Response response) throws IOException, ServletException {
        for (Map.Entry<String, Object> param : attributes.entrySet()) {
            request.setAttribute(param.getKey(), param.getValue());
        }

        RequestDispatcher requestDispatcher = request.getRequestDispatcher(path);
        if (requestDispatcher != null) {
            requestDispatcher.forward(request.getHttpServletRequest(), response.getHttpServletResponse());
        } else {
            throw new VuntimeException("cannot retrieve requestDispatcher");
        }

    }
}
