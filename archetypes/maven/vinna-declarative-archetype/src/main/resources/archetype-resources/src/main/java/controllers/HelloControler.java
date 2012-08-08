package ${package}.controllers;

import vinna.outcome.Outcome;
import vinna.outcome.StringOutcome;

public class HelloControler {

    public Outcome sayHello(String name, String ohai) {
        return new StringOutcome(String.format("%s %s !", (ohai == null ? "Ohai" : ohai), name));
    }
}