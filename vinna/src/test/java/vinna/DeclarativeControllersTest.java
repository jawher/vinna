package vinna;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import vinna.exception.VuntimeException;
import vinna.helpers.MockedRequest;
import vinna.response.Response;
import vinna.route.RouteResolution;

import java.io.StringReader;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static vinna.helpers.VinnaMatchers.eqColl;

public class DeclarativeControllersTest {


    private static class MockFactoryVinna<T> extends Vinna {
        public T controllerMock;

        private MockFactoryVinna(String routes) {
            super(Collections.<String, Object>singletonMap("routes-def", routes));
        }

        @Override
        protected void routes(Map<String, Object> config) {
            loadRoutes(new StringReader((String) config.get("routes-def")));
        }

        @Override
        protected ControllerFactory controllerFactory(Map<String, Object> config) {
            return new ControllerFactory() {
                @Override
                public Object create(String id, Class<?> clazz) {
                    try {
                        clazz = Class.forName("vinna.DeclarativeControllersTest$" + id);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    controllerMock = (T) mock(clazz);
                    return controllerMock;
                }
            };
        }
    }

    public static class Controller {

        public Response actionString(String param) {
            return null;
        }

        public Response actionInt(int param) {
            return null;
        }

        public Response actionInteger(Integer param) {
            return null;
        }

        public Response actionBool(boolean param) {
            return null;
        }

        public Response actionBoolean(Boolean param) {
            return null;
        }

        public Response actionColl(Collection<?> param) {
            return null;
        }

        public Response actionFloat(Float param) {
            return null;
        }

        public Response actionDouble(Double param) {
            return null;
        }
    }

    @Test
    public void passesAPathVarAsAString() {
        String route = "get /users/{id} Controller.actionString({id})";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/users/a").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).actionString("a");
    }

    @Test
    public void passesAQueryVarAsAString() {
        String route = "get /users Controller.actionString({req.param.id})";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/users").param("id", "a").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).actionString("a");
    }

    @Test
    public void passesAHeaderAsAString() {
        String route = "get /users Controller.actionString({req.header.x-id})";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/users").header("x-id", "a").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);

        verify(app.controllerMock).actionString("a");
    }

    @Test
    public void passesAPathVarAsAnInt() {
        String route = "get /users/{id} Controller.actionInt({id})";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);

        MockedRequest mockedRequest = MockedRequest.get("/users/5").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).actionInt(5);
    }

    @Test
    public void passesAQueryVarAsAInt() {
        String route = "get /users Controller.actionInt({req.param.id})";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/users").param("id", "13").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).actionInt(13);
    }

    @Test
    public void passesAHeaderAsAInt() {
        String route = "get /users Controller.actionInt({req.header.x-id})";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/users").header("x-id", "27").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).actionInt(27);
    }

    @Test
    public void passesAPathVarAsAnInteger() {
        String route = "get /users/{id} Controller.actionInteger({id})";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/users/666").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).actionInteger(666);
    }

    @Test
    public void passesAQueryVarAsAInteger() {
        String route = "get /users Controller.actionInt({req.param.id})";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/users").param("id", "13").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).actionInt(13);
    }

    @Test
    public void passesAHeaderAsAInteger() {
        String route = "get /users Controller.actionInteger({req.header.x-id})";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/users").header("x-id", "27").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).actionInteger(27);
    }

    @Test
    public void passesAPathVarAsABoolForTrue() {
        String route = "get /users/{id} Controller.actionBool({id})";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/users/true").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).actionBool(true);
    }

    @Test
    public void passesAQueryVarAsBoolForTrue() {
        String route = "get /users Controller.actionBool({req.param.id})";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/users").param("id", "true").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).actionBool(true);
    }

    @Test
    public void passesAHeaderAsABoolForTrue() {
        String route = "get /users Controller.actionBool({req.header.x-id})";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/users").header("x-id", "true").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).actionBool(true);
    }

    @Test
    public void passesAPathVarAsABoolForFalse() {
        String route = "get /users/{id} Controller.actionBool({id})";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/users/false").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).actionBool(false);
    }

    @Test
    public void passesAQueryVarAsBoolForFalse() {
        String route = "get /users Controller.actionBool({req.param.id})";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/users").param("id", "false").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).actionBool(false);
    }

    @Test
    public void passesAHeaderAsABoolForFalse() {
        String route = "get /users Controller.actionBool({req.header.x-id})";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/users").header("x-id", "false").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).actionBool(false);
    }

    @Test
    public void passesAPathVarAsABooleanForTrue() {

        String route = "get /users/{id} Controller.actionBoolean({id})";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/users/true").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).actionBoolean(Boolean.TRUE);
    }

    @Test
    public void passesAQueryVarAsBooleanForTrue() {
        String route = "get /users Controller.actionBoolean({req.param.id})";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/users").param("id", "true").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).actionBoolean(true);
    }

    @Test
    public void passesAHeaderAsABooleanForTrue() {
        String route = "get /users Controller.actionBoolean({req.header.x-id})";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/users").header("x-id", "true").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).actionBoolean(true);
    }

    @Test
    public void passesAPathVarAsABooleanForFalse() {
        String route = "get /users/{id} Controller.actionBoolean({id})";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/users/false").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).actionBoolean(Boolean.FALSE);
    }

    @Test
    public void passesAQueryVarAsBooleanForFalse() {
        String route = "get /users Controller.actionBoolean({req.param.id})";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/users").param("id", "false").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).actionBoolean(false);
    }

    @Test
    public void passesAHeaderAsABooleanForFalse() {
        String route = "get /users Controller.actionBoolean({req.header.x-id})";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/users").header("x-id", "false").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).actionBoolean(false);
    }

    @Test
    public void passesAConstantString() {
        final String ohai = "Ohai";
        String route = "get /users Controller.actionString(\"" + ohai + "\")";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/users").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).actionString(ohai);
    }

    @Test
    public void passesAConstantStringEvenWithOverload() {
        final String ohai = "Ohai";
        String route = "get /users AmbiguousController.action(\"" + ohai + "\")";
        MockFactoryVinna<AmbiguousController> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/users").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).action(ohai);
    }

    @Ignore("TODO")
    @Test
    public void passesAConstantBool() {
        String route = "get /users Controller.actionBool(true)";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/users").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).actionBool(true);
    }

    @Test
    public void passesAConstantBoolean() {
        String route = "get /users Controller.actionBoolean(true)";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/users").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).actionBoolean(true);
    }

    @Test
    public void passesAConstantInteger() {
        String route = "get /users Controller.actionInteger(42)";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/users").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).actionInteger(42);
    }

    @Test
    public void passesAConstantFloat() {
        String route = "get /users Controller.actionFloat(42.7)";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/users").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).actionFloat(42.7f);
    }

    @Test
    public void passesAConstantDouble() {
        String route = "get /users Controller.actionDouble(42.7)";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/users").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).actionDouble(42.7);
    }

    @Test(expected = VuntimeException.class)
    public void failsWithCollectionArgAndNoArgType() {
        String route = "get /users Controller.actionColl({req.param.names})";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);

        String[] params = new String[]{"Loulou", "Riri", "Fifi"};
        MockedRequest mockedRequest = MockedRequest.get("/users").param("names", params).build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
    }

    @Test
    public void passesARequestQueryAsAStringCollection() {
        String route = "get /users Controller.actionColl({req.param.names: [String]})";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);

        String[] params = new String[]{"Loulou", "Riri", "Fifi"};
        MockedRequest mockedRequest = MockedRequest.get("/users").param("names", params).build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).actionColl(argThat(eqColl(params)));
    }

    @Test
    public void passesARequestQueryAsAnIntegerCollection() {
        String route = "get /users Controller.actionColl({req.param.ids: [Integer]})";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);

        String[] params = new String[]{"1", "2", "3"};
        MockedRequest mockedRequest = MockedRequest.get("/users").param("ids", params).build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        ArgumentCaptor<Collection> argument = ArgumentCaptor.forClass(Collection.class);
        resolution.callAction(app);
        verify(app.controllerMock).actionColl(argThat(eqColl(1, 2, 3)));
    }

    //TODO: test the other types (double, float, bigdecimal, etc.)

    public static class ControllerWithOverloadedAndDiffArgCountMethods {

        public Response action(Boolean param, int x) {
            return null;
        }

        public Response action(String param) {
            return null;
        }
    }

    @Test
    public void handlesOverloadedActionsIfDifferentArgCount() {
        String route = "GET /ambiguous/{id} ControllerWithOverloadedAndDiffArgCountMethods.action({id})";
        MockFactoryVinna<ControllerWithOverloadedAndDiffArgCountMethods> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/ambiguous/w").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).action("w");
    }

    public static class AmbiguousController {

        public Response action(Boolean param) {
            return null;
        }

        public Response action(String param) {
            return null;
        }
    }

    @Test(expected = VuntimeException.class)
    public void failsWithOverloadedActionsWithoutTypes() {
        String route = "GET /ambiguous/{id} AmbiguousController.action({id})";
        MockFactoryVinna<AmbiguousController> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/ambiguous/w").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
    }

    @Test
    public void handlesOverloadedActionsWithTypes() {
        String route = "GET /ambiguous/{id} AmbiguousController.action({id: String})";
        MockFactoryVinna<AmbiguousController> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/ambiguous/w").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).action("w");

    }

    @Test
    public void injectsPathVariablesInControllerId() {
        String route = "GET /{controller} {controller}.actionInteger(5)";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/Controller").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).actionInteger(5);

    }

    @Test
    public void injectsPathVariablesInMethodName() {
        String route = "GET /{action} Controller.{action}(5)";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/actionInteger").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).actionInteger(5);

    }

    @Test
    public void injectsPathVariablesInControllerIdAndMethodName() {
        String route = "GET /{controller}/{action} {controller}.{action}(5)";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/Controller/actionInteger").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).actionInteger(5);

    }

    @Test
    public void injectsPathVariablesInControllerIdAndMethodNameInAFreeFormWay() {
        String route = "GET /{controller}/{action} {controller}ler.action{action}(5)";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/Control/Integer").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).actionInteger(5);

    }

    @Test(expected = VuntimeException.class)
    public void failsWhithAnUnknownVariablesInControllerId() {
        String route = "GET /{controller} {bad-controller}.actionInteger(5)";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/Controller").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);

    }

    @Test(expected = VuntimeException.class)
    public void failsWhithAnUnknownVariablesInMethodName() {
        String route = "GET /{action} Controller.{bad-action}(5)";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/stuff").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);

    }

    //TODO: moar tests !
}
