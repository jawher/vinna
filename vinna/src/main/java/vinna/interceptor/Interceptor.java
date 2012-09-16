package vinna.interceptor;

import vinna.http.VinnaRequestWrapper;
import vinna.http.VinnaResponseWrapper;
import vinna.response.Response;

public interface Interceptor {

    public void beforeMatch(VinnaRequestWrapper request, VinnaResponseWrapper response);

    public Response afterMatch(VinnaRequestWrapper request, VinnaResponseWrapper response, boolean hasMatched);

    public void beforeExecute(VinnaRequestWrapper request, VinnaResponseWrapper response);

    public void afterExecute(VinnaRequestWrapper request, VinnaResponseWrapper response);
}
