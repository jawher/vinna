package vinna.interceptor;

import vinna.VinnaContext;

public class InterceptorAdapter implements Interceptor {
    @Override
    public void beforeMatch(VinnaContext context) {
        // do nothing by default
    }

    @Override
    public void afterMatch(VinnaContext context) {
        // do nothing by default
    }

    @Override
    public void afterExecute(VinnaContext context) {
        // do nothing by default
    }
}
