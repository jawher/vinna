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
            } catch (InvocationTargetException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (NoSuchMethodException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (IllegalAccessException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (InstantiationException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    @Before
    public void prepareApp() {
        vinnApp = new VinnApp("/beer/{id: \\d+}");
    }

    @Test
    public void checkPathMatchingSuccess() {
        MockedRequest mockedRequest = new MockedRequest();
        mockedRequest.path = "/beer/109";
        mockedRequest.verb = "get";
        mockedRequest.param = Collections.<String, Collection<String>>singletonMap("foudre", Collections.<String>singletonList("tonnerre"));
        Assert.assertNotNull(vinnApp.match(mockedRequest));
    }

    public void checkPathMatchingFailure() {
        MockedRequest mockedRequest = new MockedRequest();
        mockedRequest.path = "/beer/NaN";
        mockedRequest.verb = "get";
        mockedRequest.param = Collections.<String, Collection<String>>singletonMap("foudre", Collections.<String>singletonList("tonnerre"));
        Assert.assertNull(vinnApp.match(mockedRequest));
    }
}
