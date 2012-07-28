package vinna.outcome;

import vinna.request.Request;

import javax.servlet.http.HttpServletResponse;

public interface Outcome {
    public void execute(Request request, HttpServletResponse response);
}
