package vinna.interceptor;

import vinna.VinnaContext;

// TODO: split this interface ?
public interface Interceptor {

    public void beforeMatch(VinnaContext context);

    public void afterMatch(VinnaContext context);

    public void afterExecute(VinnaContext context);
}
