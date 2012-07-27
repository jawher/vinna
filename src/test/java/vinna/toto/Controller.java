package vinna.toto;

import vinna.outcome.Outcome;
import vinna.outcome.StringOutcome;

/**
 * @author lpereira
 */
public class Controller {

    public Outcome noArgsProcessing() {
        return new StringOutcome("toto");
    }
}
