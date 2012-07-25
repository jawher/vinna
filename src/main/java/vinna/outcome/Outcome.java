package vinna.outcome;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Outcome {
    public void execute(HttpServletRequest request, HttpServletResponse response);
}
