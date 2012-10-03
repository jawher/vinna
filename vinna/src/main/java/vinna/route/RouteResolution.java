package vinna.route;

import vinna.Vinna;
import vinna.http.Request;
import vinna.response.Response;

import java.util.Map;

public class RouteResolution {
    public final Action action;
    public final Map<String, String> matchedVars;

    public RouteResolution(Action action, Map<String, String> matchedVars) {
        this.action = action;
        this.matchedVars = matchedVars;
    }

    public Response callAction(Request request, Vinna vinna) {
        Action.Environment env = new Action.Environment(request, matchedVars, vinna);
        return action.execute(env);
    }

    public static interface Action {
        public class Environment {
            public final Map<String, String> matchedVars;
            public final Request request;
            public final Vinna vinna;

            public Environment(Request request, Map<String, String> matchedVars, Vinna vinna) {
                this.matchedVars = matchedVars;
                this.request = request;
                this.vinna = vinna;
            }
        }

        Response execute(Environment environment);


    }
}
