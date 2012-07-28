package vinna;

import org.junit.Test;
import vinna.outcome.Outcome;
import vinna.request.MockedRequest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class RoutingTests {

    private static class NoOpcontroller {
        public NoOpcontroller() {
        }

        public Outcome process() {
            return null;
        }
    }

    private Vinna oneRouteApp(final String path) {
        return new Vinna() {
            @Override
            protected void routes() {
                get(path).withController(NoOpcontroller.class).process();
            }
        };
    }

    @Test
    public void matchesAConstantPath() {
        Vinna app = oneRouteApp("/users");
        MockedRequest mockedRequest = MockedRequest.get("/users").build();
        assertNotNull(app.match(mockedRequest));
    }

    @Test
    public void matchesAConstantPathEvenWhenRequestHasParams() {
        Vinna app = oneRouteApp("/users");
        MockedRequest mockedRequest = MockedRequest.get("/users").param("a", "b").build();
        assertNotNull(app.match(mockedRequest));
    }

    @Test
    public void matchesAMultiSegmentConstantPath() {
        Vinna app = oneRouteApp("/site/users");
        MockedRequest mockedRequest = MockedRequest.get("/site/users").build();
        assertNotNull(app.match(mockedRequest));
    }

    @Test
    public void matchesARelativeConstantPath() {
        Vinna app = oneRouteApp("site/users");
        MockedRequest mockedRequest = MockedRequest.get("/prefix/site/users").build();
        assertNotNull(app.match(mockedRequest));
    }

    @Test
    public void failsWithTrailingSlashInRouteButNotInRequest() {
        Vinna app = oneRouteApp("/users/");
        MockedRequest mockedRequest = MockedRequest.get("/users").build();
        assertNull(app.match(mockedRequest));
    }

    @Test
    public void failsWithTrailingSlashInRequestButNotInRoute() {
        Vinna app = oneRouteApp("/users");
        MockedRequest mockedRequest = MockedRequest.get("/users/").build();
        assertNull(app.match(mockedRequest));
    }

    @Test
    public void matchesAPathWithATrailingVariable() {
        Vinna app = oneRouteApp("/users/{id}");
        MockedRequest mockedRequest = MockedRequest.get("/users/5").build();
        assertNotNull(app.match(mockedRequest));
    }

    @Test
    public void matchesAPathWithAPrefixingVariable() {
        Vinna app = oneRouteApp("{domain}/users");
        MockedRequest mockedRequest = MockedRequest.get("/vinna/users").build();
        assertNotNull(app.match(mockedRequest));
    }

    @Test
    public void matchesAPathWithAVariableInTheMiddle() {
        Vinna app = oneRouteApp("/users/{id}/ohai");
        MockedRequest mockedRequest = MockedRequest.get("/users/5/ohai").build();
        assertNotNull(app.match(mockedRequest));
    }

    @Test
    public void matchesAPathWithMultipleVariable() {
        Vinna app = oneRouteApp("{domain}/users/{id}/ohai");
        MockedRequest mockedRequest = MockedRequest.get("/vinna/users/5/ohai").build();
        assertNotNull(app.match(mockedRequest));
    }

    @Test
    public void failsWithAPathWithATrailingVariableAndANonMatchingRequest() {
        Vinna app = oneRouteApp("/users/{id}");
        MockedRequest mockedRequest = MockedRequest.get("/users").build();
        assertNull(app.match(mockedRequest));
    }

    @Test
    public void failsWithAPathWithAPrefixingVariableAndANonMatchingRequest() {
        Vinna app = oneRouteApp("{domain}/users");
        MockedRequest mockedRequest = MockedRequest.get("/vinna").build();
        assertNull(app.match(mockedRequest));
    }

    @Test
    public void failsWithAPathWithAVariableInTheMiddleAndANonMatchingRequest() {
        Vinna app = oneRouteApp("/users/{id}/ohai");
        MockedRequest mockedRequest = MockedRequest.get("/users/5").build();
        assertNull(app.match(mockedRequest));

        MockedRequest mockedRequest2 = MockedRequest.get("/users/ohai").build();
        assertNull(app.match(mockedRequest2));
    }

    //TODO: moar test !
}
