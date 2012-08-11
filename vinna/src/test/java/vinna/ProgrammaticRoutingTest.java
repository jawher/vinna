package vinna;

import org.junit.Test;
import vinna.helpers.MockedRequest;
import vinna.outcome.Outcome;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ProgrammaticRoutingTest {

    public static class NoOpcontroller {
        public Outcome process() {
            return null;
        }

        public Outcome process(String param) {
            return null;
        }

        public String process(int param) {
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

    private Vinna oneRouteAppWithParam(final String path, final String param) {
        return oneRouteAppWithParamAndPattern(path, param, null);
    }

    private Vinna oneRouteAppWithParamAndPattern(final String path, final String param, final String pattern) {
        return new Vinna() {
            @Override
            protected void routes() {
                get(path).hasParam(param, pattern).withController(NoOpcontroller.class).process();
            }
        };
    }

    private Vinna oneRouteAppWithHeader(final String path, final String header) {
        return oneRouteAppWithHeaderAndPattern(path, header, null);
    }

    private Vinna oneRouteAppWithHeaderAndPattern(final String path, final String header, final String pattern) {
        return new Vinna() {
            @Override
            protected void routes() {
                get(path).hasHeader(header, pattern).withController(NoOpcontroller.class).process();
            }
        };
    }

    private Vinna onePostRouteApp(final String path) {
        return new Vinna() {
            @Override
            protected void routes() {
                post(path).withController(NoOpcontroller.class).process();
            }
        };
    }

    @Test
    public void failsWithAPostVerbInRouteButNotInRequest() {
        Vinna app = onePostRouteApp("/users");
        MockedRequest mockedRequest = MockedRequest.get("/users").build();
        assertNull(app.match(mockedRequest));
    }

    @Test
    public void failsWithAGetVerbInRouteButNotInRequest() {
        Vinna app = oneRouteApp("/users");
        MockedRequest mockedRequest = MockedRequest.post("/users").build();
        assertNull(app.match(mockedRequest));
    }

    @Test
    public void matchesAConstantPathWithPostVerb() {
        Vinna app = onePostRouteApp("/users");
        MockedRequest mockedRequest = MockedRequest.post("/users").build();
        assertNotNull(app.match(mockedRequest));
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

    @Test
    public void matchesWithAPathWithAVariableWithAPattern() {
        Vinna app = oneRouteApp("/users/{id: \\d+}");
        MockedRequest mockedRequest = MockedRequest.get("/users/5").build();
        assertNotNull(app.match(mockedRequest));
    }

    @Test
    public void failsWithAPathWithAVariableWithAPatternAndANonMatchingRequest() {
        Vinna app = oneRouteApp("/users/{id: \\d+}");
        MockedRequest mockedRequest = MockedRequest.get("/users/abc").build();
        assertNull(app.match(mockedRequest));
    }

    @Test
    public void failsWithARouteWithMandatoryPathParamAndNonMatchingRequest() {
        Vinna app = oneRouteAppWithParam("/users", "id");
        MockedRequest mockedRequest = MockedRequest.get("/users").build();
        assertNull(app.match(mockedRequest));

        MockedRequest mockedRequest2 = MockedRequest.get("/users").param("uid", "5").build();
        assertNull(app.match(mockedRequest2));
    }

    @Test
    public void matchesWithARouteWithMandatoryPathParam() {
        Vinna app = oneRouteAppWithParam("/users", "id");
        MockedRequest mockedRequest = MockedRequest.get("/users").param("id", "5").build();
        assertNotNull(app.match(mockedRequest));
    }

    @Test
    public void matchesARouteWithAMandatoryParamWithAPattern() {
        Vinna app = oneRouteAppWithParamAndPattern("/users", "id", "\\d+");
        MockedRequest mockedRequest = MockedRequest.get("/users").param("id", "5").build();
        assertNotNull(app.match(mockedRequest));
    }

    @Test
    public void failsARouteWithAMandatoryParamWithAPatternAndNonMatchingRequest() {
        Vinna app = oneRouteAppWithParamAndPattern("/users", "id", "\\d+");
        MockedRequest mockedRequest = MockedRequest.get("/users").param("id", "five").build();
        assertNull(app.match(mockedRequest));
    }

    @Test
    public void matchesWithARouteWithAMandatoryHeader() {
        Vinna app = oneRouteAppWithHeader("/users", "X-Vinna");
        MockedRequest mockedRequest = MockedRequest.get("/users").header("X-Vinna", "Let's work instead of playing").build();
        assertNotNull(app.match(mockedRequest));
    }

    @Test
    public void failsWithARouteWithAMandatoryHeaderAndNonMatchingRequest() {
        Vinna app = oneRouteAppWithHeader("/users", "X-Vinna");
        MockedRequest mockedRequest = MockedRequest.get("/users").build();
        assertNull(app.match(mockedRequest));
    }

    @Test
    public void matchesARouteWithAMandatoryHeaderWithAPattern() {
        Vinna app = oneRouteAppWithHeaderAndPattern("/users", "X-Vinna", "\\d+");
        MockedRequest mockedRequest = MockedRequest.get("/users").header("X-Vinna", "5").build();
        assertNotNull(app.match(mockedRequest));
    }

    @Test
    public void failsARouteWithAMandatoryHeaderWithAPatternAndNonMatchingRequest() {
        Vinna app = oneRouteAppWithHeaderAndPattern("/users", "X-Vinna", "\\d+");
        MockedRequest mockedRequest = MockedRequest.get("/users").header("X-Vinna", "five").build();
        assertNull(app.match(mockedRequest));
    }

    @Test
    public void matchesAConstantRouteDefinedByWithMethod() {
        Vinna app = new Vinna() {
            @Override
            protected void routes() {
                get("/users").withControllerId("vinna.ProgrammaticRoutingTest.NoOpcontroller").withMethod("process()");
            }
        };
        MockedRequest mockedRequest = MockedRequest.get("/users").build();
        assertNotNull(app.match(mockedRequest));
    }

    @Test(expected = IllegalStateException.class)
    public void failsToBuildARouteByPassingParametersWithoutUsingApi() {
        new Vinna() {
            @Override
            protected void routes() {
                get("/users").withController(NoOpcontroller.class).process("passing an arg");
            }
        };
    }

    @Test(expected = IllegalStateException.class)
    public void failsToBuildARouteByUsingAMethodNotReturningOutcome() {
        new Vinna() {
            @Override
            protected void routes() {
                get("/users").withController(NoOpcontroller.class).process(constant(0));
            }
        };
    }

    @Test(expected = RuntimeException.class)
    public void failsToCallTwiceWithControllerIdMethod() {
        new Vinna() {
            @Override
            protected void routes() {
                get("/users").withControllerId("controllerId").withControllerId("controllerId").withMethod("method()");
            }
        };
    }

    @Test(expected = RuntimeException.class)
    public void failsWithIncorrectMethodPattern() {
        new Vinna() {
            @Override
            protected void routes() {
                get("/users").withControllerId("controllerId").withMethod("i am not a method");
            }
        };
    }

    //TODO: moar test !
}
