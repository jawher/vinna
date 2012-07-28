package vinna.request;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import vinna.Vinna;
import vinna.outcome.Outcome;
import vinna.outcome.StringOutcome;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;

public class RequestTest {

    private VinnApp vinnApp;

    private static class Ctrl {

        public Ctrl() {
            // NOp
        }

        public Outcome process() {
            return new StringOutcome("toto");
        }
    }

    private class VinnApp extends Vinna {

        public VinnApp(String path) {
            try {
                get(path).withController(Ctrl.class).process();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Before
    public void prepareApp() {
        vinnApp = new VinnApp("/beer/{id: \\d+}");
    }

    @Test
    public void checkPathMatchingSuccess() {
        MockedRequest mockedRequest = MockedRequest.get("/beer/109").param("foudre", "tonnerre").build();
        Assert.assertNotNull(vinnApp.match(mockedRequest));
    }

    @Test
    public void checkPathMatchingFailure() {
        MockedRequest mockedRequest = MockedRequest.get("/beer/NaN").param("foudre", "tonnerre").build();
        Assert.assertNull(vinnApp.match(mockedRequest));
    }
}
