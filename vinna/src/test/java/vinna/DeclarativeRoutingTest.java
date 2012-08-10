package vinna;

import org.junit.Test;
import vinna.outcome.Outcome;
import vinna.helpers.MockedRequest;

import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class DeclarativeRoutingTest {

    public static class NoOpcontroller {
        public Outcome process() {
            return null;
        }
    }

    private Vinna oneRouteApp(final String verb, final String path) {
        return new Vinna(new StringReader(verb + " " + path + " foo.bar()"));
    }

    private Vinna oneRouteAppWithAConstraint(final String verb, final String path, final String var) {
        return new Vinna(new StringReader(verb + " " + path + " foo.bar()\n  "+var));
    }

    private Vinna oneRouteAppWithAConstraint(final String verb, final String path, final String var, final String pattern) {
        return new Vinna(new StringReader(verb + " " + path + " foo.bar()\n  "+var+" : "+pattern));
    }

    private Vinna app(final String path) {
        try {
            return new Vinna(new InputStreamReader(DeclarativeRoutingTest.class.getResourceAsStream("routing/" + path), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void failsWithAPostVerbInRouteButNotInRequest() {
        Vinna app = oneRouteApp("post", "/users");
        MockedRequest mockedRequest = MockedRequest.get("/users").build();
        assertNull(app.match(mockedRequest));
    }

    @Test
    public void failsWithAGetVerbInRouteButNotInRequest() {
        Vinna app = oneRouteApp("get", "u/sers");
        MockedRequest mockedRequest = MockedRequest.post("/users").build();
        assertNull(app.match(mockedRequest));
    }

    @Test
    public void matchesAConstantPathWithPostVerb() {
        Vinna app = oneRouteApp("post", "/users");
        MockedRequest mockedRequest = MockedRequest.post("/users").build();
        assertNotNull(app.match(mockedRequest));
    }

    @Test
    public void matchesAConstantPath() {
        Vinna app = oneRouteApp("get", "/users");
        MockedRequest mockedRequest = MockedRequest.get("/users").build();
        assertNotNull(app.match(mockedRequest));
    }

    @Test
    public void matchesAConstantPathEvenWhenRequestHasParams() {
        Vinna app = oneRouteApp("get", "/users");
        MockedRequest mockedRequest = MockedRequest.get("/users").param("a", "b").build();
        assertNotNull(app.match(mockedRequest));
    }

    @Test
    public void matchesAMultiSegmentConstantPath() {
        Vinna app = oneRouteApp("get", "/site/users");
        MockedRequest mockedRequest = MockedRequest.get("/site/users").build();
        assertNotNull(app.match(mockedRequest));
    }

    @Test
    public void matchesARelativeConstantPath() {
        Vinna app = oneRouteApp("get", "site/users");

        MockedRequest mockedRequest = MockedRequest.get("/prefix/site/users").build();
        assertNotNull(app.match(mockedRequest));
    }

    @Test
    public void failsWithTrailingSlashInRouteButNotInRequest() {
        Vinna app = oneRouteApp("get", "/users/");
        MockedRequest mockedRequest = MockedRequest.get("/users").build();
        assertNull(app.match(mockedRequest));
    }

    @Test
    public void failsWithTrailingSlashInRequestButNotInRoute() {
        Vinna app = oneRouteApp("get", "/users");
        MockedRequest mockedRequest = MockedRequest.get("/users/").build();
        assertNull(app.match(mockedRequest));
    }

    @Test
    public void matchesAPathWithATrailingVariable() {
        Vinna app = oneRouteApp("get", "/users/{id}");
        MockedRequest mockedRequest = MockedRequest.get("/users/5").build();
        assertNotNull(app.match(mockedRequest));
    }

    @Test
    public void matchesAPathWithAPrefixingVariable() {
        Vinna app = oneRouteApp("get", "{domain}/users");
        MockedRequest mockedRequest = MockedRequest.get("/vinna/users").build();
        assertNotNull(app.match(mockedRequest));
    }

    @Test
    public void matchesAPathWithAVariableInTheMiddle() {
        Vinna app = oneRouteApp("get", "/users/{id}/ohai");

        MockedRequest mockedRequest = MockedRequest.get("/users/5/ohai").build();
        assertNotNull(app.match(mockedRequest));
    }

    @Test
    public void matchesAPathWithMultipleVariable() {
        Vinna app = oneRouteApp("get", "{domain}/users/{id}/ohai");
        MockedRequest mockedRequest = MockedRequest.get("/vinna/users/5/ohai").build();
        assertNotNull(app.match(mockedRequest));
    }

    @Test
    public void failsWithAPathWithATrailingVariableAndANonMatchingRequest() {
        Vinna app = oneRouteApp("get", "/users/{id}");
        MockedRequest mockedRequest = MockedRequest.get("/users").build();
        assertNull(app.match(mockedRequest));
    }

    @Test
    public void failsWithAPathWithAPrefixingVariableAndANonMatchingRequest() {
        Vinna app = oneRouteApp("get", "{domain}/users");
        MockedRequest mockedRequest = MockedRequest.get("/vinna").build();
        assertNull(app.match(mockedRequest));
    }

    @Test
    public void failsWithAPathWithAVariableInTheMiddleAndANonMatchingRequest() {
        Vinna app = oneRouteApp("get", "/users/{id}/ohai");
        MockedRequest mockedRequest = MockedRequest.get("/users/5").build();
        assertNull(app.match(mockedRequest));

        MockedRequest mockedRequest2 = MockedRequest.get("/users/ohai").build();
        assertNull(app.match(mockedRequest2));
    }

    @Test
    public void matchesWithAPathWithAVariableWithAPattern() {
        Vinna app = oneRouteAppWithAConstraint("get", "/users/{id}", "id", "\\d+");
        MockedRequest mockedRequest = MockedRequest.get("/users/5").build();
        assertNotNull(app.match(mockedRequest));
    }

    @Test
    public void failsWithAPathWithAVariableWithAPatternAndANonMatchingRequest() {
        Vinna app = oneRouteAppWithAConstraint("get", "/users/{id}", "req.param.id", "\\d+");
        MockedRequest mockedRequest = MockedRequest.get("/users/abc").build();
        assertNull(app.match(mockedRequest));
    }

    @Test
    public void failsWithARouteWithMandatoryPathParamAndNonMatchingRequest() {
        Vinna app = oneRouteAppWithAConstraint("get", "/users", "req.param.id");
        MockedRequest mockedRequest = MockedRequest.get("/users").build();
        assertNull(app.match(mockedRequest));

        MockedRequest mockedRequest2 = MockedRequest.get("/users").param("uid", "5").build();
        assertNull(app.match(mockedRequest2));
    }

    @Test
    public void matchesWithARouteWithMandatoryPathParam() {
        Vinna app = oneRouteAppWithAConstraint("get", "/users", "req.param.id");
        MockedRequest mockedRequest = MockedRequest.get("/users").param("id", "5").build();
        assertNotNull(app.match(mockedRequest));
    }

    @Test
    public void matchesARouteWithAMandatoryParamWithAPattern() {
        Vinna app = oneRouteAppWithAConstraint("get", "/users", "req.param.id", "\\d+");
        MockedRequest mockedRequest = MockedRequest.get("/users").param("id", "5").build();
        assertNotNull(app.match(mockedRequest));
    }

    @Test
    public void failsARouteWithAMandatoryParamWithAPatternAndNonMatchingRequest() {
        Vinna app = oneRouteAppWithAConstraint("get", "/users", "req.param.id", "\\d+");
        MockedRequest mockedRequest = MockedRequest.get("/users").param("id", "five").build();
        assertNull(app.match(mockedRequest));
    }

    @Test
    public void matchesWithARouteWithAMandatoryHeader() {
        Vinna app = oneRouteAppWithAConstraint("get", "/users", "req.header.X-Vinna");
        MockedRequest mockedRequest = MockedRequest.get("/users").header("X-Vinna", "Let's work instead of playing").build();
        assertNotNull(app.match(mockedRequest));
    }

    @Test
    public void failsWithARouteWithAMandatoryHeaderAndNonMatchingRequest() {
        Vinna app = oneRouteAppWithAConstraint("get", "/users", "req.header.X-Vinna");
        MockedRequest mockedRequest = MockedRequest.get("/users").build();
        assertNull(app.match(mockedRequest));
    }

    @Test
    public void matchesARouteWithAMandatoryHeaderWithAPattern() {
        Vinna app = oneRouteAppWithAConstraint("get", "/users", "req.header.X-Vinna", "\\d+");
        MockedRequest mockedRequest = MockedRequest.get("/users").header("X-Vinna", "5").build();
        assertNotNull(app.match(mockedRequest));
    }

    @Test
    public void failsARouteWithAMandatoryHeaderWithAPatternAndNonMatchingRequest() {
        Vinna app = oneRouteAppWithAConstraint("get", "/users", "req.header.X-Vinna", "\\d+");
        MockedRequest mockedRequest = MockedRequest.get("/users").header("X-Vinna", "five").build();
        assertNull(app.match(mockedRequest));
    }

    //TODO: moar test !
}