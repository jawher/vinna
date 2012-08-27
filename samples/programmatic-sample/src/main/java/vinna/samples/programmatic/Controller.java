package vinna.samples.programmatic;

import vinna.response.Response;
import vinna.response.StringResponse;

import java.util.Collection;
import java.util.Map;

public class Controller {

    public Response index() {
        return new StringResponse("hello here");
    }

    public Response test(int id) {
        return new StringResponse("Your id is " + id);
    }

    public Response test2(String inject, Integer id) {
        return new StringResponse("Your id is " + id + "\n" + inject);
    }

    public Response test2(String inject, boolean yo, Integer id) {
        return new StringResponse("Your id is " + id + "\n" + inject + "\n" + yo);
    }

    public Response writeHeaders(Collection<String> headers) {
        if (headers == null || headers.size() == 0) {
            return new StringResponse("no header");
        }
        StringBuilder builder = new StringBuilder();
        for (String header : headers) {
            builder.append(header);
            builder.append("\n");
        }
        return new StringResponse(builder.toString());
    }

    public Response writeHeader(String header) {
        return new StringResponse(header);
    }

    public Response printHeaders(Map<String, Collection<String>> headers) {
        if (headers == null || headers.size() == 0) {
            return new StringResponse("no header");
        }
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Collection<String>> entry : headers.entrySet()) {
            builder.append(entry.getKey() + ":" + entry.getValue() + "\n");
        }
        return new StringResponse(builder.toString());
    }

    public Response params(String a, String b, Integer c) {
        return new StringResponse(a + ":" + b + ":" + c);
    }

    public Response paramCollection(Collection<String> params) {
        if (params == null || params.size() == 0) {
            return new StringResponse("no param");
        }
        StringBuilder builder = new StringBuilder();
        for (String param : params) {
            builder.append(param + "\n");
        }
        return new StringResponse(builder.toString());
    }

    public Response post(String title, String description) {
        return new StringResponse(title + " : " + description);
    }
}
