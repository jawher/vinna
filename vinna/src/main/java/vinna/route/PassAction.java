package vinna.route;

import vinna.exception.PassException;
import vinna.response.Response;

public class PassAction implements RouteResolution.Action {
    public static final PassAction INSTANCE = new PassAction();

    private PassAction() {
    }

    @Override
    public Response execute(Environment environment) {
        throw new PassException();
    }

    @Override
    public String toString() {
        return "pass";
    }
}
