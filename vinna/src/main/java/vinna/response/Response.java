package vinna.response;

import vinna.http.VinnaRequestWrapper;
import vinna.http.VinnaResponseWrapper;

import javax.servlet.ServletException;
import java.io.IOException;

public interface Response {
    public void execute(VinnaRequestWrapper request, VinnaResponseWrapper response) throws IOException, ServletException;
}
