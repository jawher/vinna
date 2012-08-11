package vinna;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import vinna.helpers.MockedRequest;
import vinna.outcome.Outcome;
import vinna.route.RouteResolution;

import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static vinna.helpers.VinnaMatchers.eqColl;

public class DeclarativeControllersTest {


    private static class MockFactoryVinna<T> extends Vinna {
        public T controllerMock;

        private MockFactoryVinna(String routes) {
            super(new StringReader(routes));
        }

        private MockFactoryVinna(Reader routesReader) {
            super(routesReader);
        }

        @Override
        protected ControllerFactory controllerFactory() {
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

        public Outcome actionString(String param) {
            return null;
        }

        public Outcome actionInt(int param) {
            return null;
        }

        public Outcome actionInteger(Integer param) {
            return null;
        }


        public Outcome actionBool(boolean param) {
            return null;
        }

        public Outcome actionBoolean(Boolean param) {
            return null;
        }

        public Outcome actionColl(Collection<?> param) {
            return null;
        }

        public Outcome actionFloat(Float param) {
            return null;
        }

        public Outcome actionDouble(Double param) {
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
        try {
            resolution.callAction(app);
            verify(app.controllerMock).actionString("a");
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void passesAPathVarAsAnInt() {
        String route = "get /users/{id} Controller.actionInt({id})";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);

        MockedRequest mockedRequest = MockedRequest.get("/users/5").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);
        try {
            resolution.callAction(app);
            verify(app.controllerMock).actionInt(5);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void passesAPathVarAsAnInteger() {
        String route = "get /users/{id} Controller.actionInteger({id})";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/users/666").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);
        try {
            resolution.callAction(app);
            verify(app.controllerMock).actionInteger(666);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void passesAPathVarAsABoolForTrue() {
        String route = "get /users/{id} Controller.actionBool({id})";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/users/true").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);
        try {
            resolution.callAction(app);
            verify(app.controllerMock).actionBool(true);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void passesAPathVarAsABoolForFalse() {
        String route = "get /users/{id} Controller.actionBool({id})";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/users/false").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);
        try {
            resolution.callAction(app);
            verify(app.controllerMock).actionBool(false);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void passesAPathVarAsABooleanForTrue() {

        String route = "get /users/{id} Controller.actionBoolean({id})";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/users/true").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);
        try {
            resolution.callAction(app);
            verify(app.controllerMock).actionBoolean(Boolean.TRUE);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void passesAPathVarAsABooleanForFalse() {

        String route = "get /users/{id} Controller.actionBoolean({id})";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/users/false").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);
        try {
            resolution.callAction(app);
            verify(app.controllerMock).actionBoolean(Boolean.FALSE);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void passesAConstantString() {
        final String ohai = "Ohai";
        String route = "get /users Controller.actionString(\"" + ohai + "\")";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/users").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);
        try {
            resolution.callAction(app);
            verify(app.controllerMock).actionString(ohai);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void passesAConstantStringEvenWithOverload() {
        final String ohai = "Ohai";
        String route = "get /users AmbiguousController.action(\"" + ohai + "\")";
        MockFactoryVinna<AmbiguousController> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/users").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);
        try {
            resolution.callAction(app);
            verify(app.controllerMock).action(ohai);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Ignore("TODO")
    @Test
    public void passesAConstantBool() {
        String route = "get /users Controller.actionBool(true)";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/users").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);
        try {
            resolution.callAction(app);
            verify(app.controllerMock).actionBool(true);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void passesAConstantBoolean() {
        String route = "get /users Controller.actionBoolean(true)";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/users").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);
        try {
            resolution.callAction(app);
            verify(app.controllerMock).actionBoolean(true);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void passesAConstantInteger() {
        String route = "get /users Controller.actionInteger(42)";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/users").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);
        try {
            resolution.callAction(app);
            verify(app.controllerMock).actionInteger(42);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void passesAConstantFloat() {
        String route = "get /users Controller.actionFloat(42.7)";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/users").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);
        try {
            resolution.callAction(app);
            verify(app.controllerMock).actionFloat(42.7f);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void passesAConstantDouble() {
        String route = "get /users Controller.actionDouble(42.7)";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/users").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);
        try {
            resolution.callAction(app);
            verify(app.controllerMock).actionDouble(42.7);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void failsWithCollectionArgAndNoArgType() {
        String route = "get /users Controller.actionColl({req.param.names})";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);

        String[] params = new String[]{"Loulou", "Riri", "Fifi"};
        MockedRequest mockedRequest = MockedRequest.get("/users").param("names", params).build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        try {
            resolution.callAction(app);
            fail("Should have failed because no arg type was specified");
        } catch (RuntimeException e) {
            //Cool !
            //FIXME: replace with the concrete exception type when one is added
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void passesARequestQueryAsAStringCollection() {
        String route = "get /users Controller.actionColl({req.param.names: [String]})";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);

        String[] params = new String[]{"Loulou", "Riri", "Fifi"};
        MockedRequest mockedRequest = MockedRequest.get("/users").param("names", params).build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        try {
            resolution.callAction(app);
            verify(app.controllerMock).actionColl(argThat(eqColl(params)));
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void passesARequestQueryAsAnIntegerCollection() {
        String route = "get /users Controller.actionColl({req.param.ids: [Integer]})";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);

        String[] params = new String[]{"1", "2", "3"};
        MockedRequest mockedRequest = MockedRequest.get("/users").param("ids", params).build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        try {
            ArgumentCaptor<Collection> argument = ArgumentCaptor.forClass(Collection.class);
            resolution.callAction(app);
            verify(app.controllerMock).actionColl(argThat(eqColl(1, 2, 3)));
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    //TODO: test the other types (double, float, bigdecimal, etc.)

    public static class ControllerWithOverloadedAndDiffArgCountMethods {

        public Outcome action(Boolean param, int x) {
            return null;
        }

        public Outcome action(String param) {
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
        try {
            resolution.callAction(app);
            verify(app.controllerMock).action("w");
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static class AmbiguousController {

        public Outcome action(Boolean param) {
            return null;
        }

        public Outcome action(String param) {
            return null;
        }
    }

    @Test
    public void failsWithOverloadedActionsWithoutTypes() {
        String route = "GET /ambiguous/{id} AmbiguousController.action({id})";
        MockFactoryVinna<AmbiguousController> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/ambiguous/w").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);
        try {
            resolution.callAction(app);
            fail("Should fail due to ambiguous situation");
        } catch (RuntimeException e) {
            // TODO: to be updated when a custom and more precise exception is defined
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void handlesOverloadedActionsWithTypes() {
        String route = "GET /ambiguous/{id} AmbiguousController.action({id: String})";
        MockFactoryVinna<AmbiguousController> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/ambiguous/w").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);
        try {
            resolution.callAction(app);
            verify(app.controllerMock).action("w");
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void injectsPathVariablesInControllerId() {
        String route = "GET /{controller} {controller}.actionInteger(5)";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/Controller").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);
        try {
            resolution.callAction(app);
            verify(app.controllerMock).actionInteger(5);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void injectsPathVariablesInMethodName() {
        String route = "GET /{action} Controller.{action}(5)";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/actionInteger").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);
        try {
            resolution.callAction(app);
            verify(app.controllerMock).actionInteger(5);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void injectsPathVariablesInControllerIdAndMethodName() {
        String route = "GET /{controller}/{action} {controller}.{action}(5)";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/Controller/actionInteger").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);
        try {
            resolution.callAction(app);
            verify(app.controllerMock).actionInteger(5);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void injectsPathVariablesInControllerIdAndMethodNameInAFreeFormWay() {
        String route = "GET /{controller}/{action} {controller}ler.action{action}(5)";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/Control/Integer").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);
        try {
            resolution.callAction(app);
            verify(app.controllerMock).actionInteger(5);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void failsWhithAnUnknownVariablesInControllerId() {
        String route = "GET /{controller} {bad-controller}.actionInteger(5)";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/Controller").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);
        try {
            resolution.callAction(app);
            fail("Should have failed with an unknow variable");
        } catch (RuntimeException e) {
            //FIXME: replace with a concrete exception type when one is defined
            //all is good
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void failsWhithAnUnknownVariablesInMethodName() {
        String route = "GET /{action} Controller.{bad-action}(5)";
        MockFactoryVinna<Controller> app = new MockFactoryVinna<>(route);
        MockedRequest mockedRequest = MockedRequest.get("/stuff").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);
        try {
            resolution.callAction(app);
            fail("Should have failed with an unknow variable");
        } catch (RuntimeException e) {
            //FIXME: replace with a concrete exception type when one is defined
            //all is good
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    //TODO: moar tests !
}
