package vinna.samples.declarative.controllers;

import vinna.response.Response;
import vinna.response.StringResponse;

import java.util.Collection;

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
        }
        return new StringResponse(builder.toString());
    }

    public Response writeHeader(String header) {
        return new StringResponse(header);
    }
}
