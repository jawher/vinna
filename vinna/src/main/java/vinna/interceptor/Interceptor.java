package vinna.interceptor;

import vinna.http.VinnaRequestWrapper;
import vinna.http.VinnaResponseWrapper;
import vinna.response.Response;

public interface Interceptor {

    public void beforeMatch(VinnaRequestWrapper request);

    public Response afterMatch(VinnaRequestWrapper request, boolean hasMatched);

    public void beforeExecute(VinnaRequestWrapper request, VinnaResponseWrapper response);

    public void afterExecute(VinnaRequestWrapper request, VinnaResponseWrapper response);
}
