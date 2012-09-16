package vinna.interceptor;

import vinna.http.VinnaRequestWrapper;
import vinna.http.VinnaResponseWrapper;
import vinna.response.Response;

public class InterceptorAdapter implements Interceptor {
    @Override
    public void beforeMatch(VinnaRequestWrapper request, VinnaResponseWrapper response) {
        // do nothing by default
    }

    @Override
    public Response afterMatch(VinnaRequestWrapper request, VinnaResponseWrapper response, boolean hasMatched) {
        // do nothing by default
        return null;
    }

    @Override
    public void beforeExecute(VinnaRequestWrapper request, VinnaResponseWrapper response) {
        // do nothing by default
    }

    @Override
    public void afterExecute(VinnaRequestWrapper request, VinnaResponseWrapper response) {
        // do nothing by default
    }
}
