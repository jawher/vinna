package vinna;

import org.junit.Test;
import vinna.outcome.Outcome;
import vinna.request.MockedRequest;
import vinna.route.RouteResolution;

import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ControllersTest {


    private static class MockFactoryVinna<T> extends Vinna {
        public T controllerMock;

        @Override
        protected ControllerFactory controllerFactory() {
            return new ControllerFactory() {
                @Override
                public Object create(String id, Class<?> clazz) {
                    controllerMock = (T) mock(clazz);
                    return controllerMock;
                }
            };
        }
    }

    public static class StringArgController {

        public Outcome action(String param) {
            return null;
        }
    }

    public static class IntArgController {

        public Outcome action(int param) {
            return null;
        }
    }

    public static class IntegerArgController {

        public Outcome action(Integer param) {
            return null;
        }
    }

    public static class BoolArgController {

        public Outcome action(boolean param) {
            return null;
        }
    }

    public static class BooleanArgController {

        public Outcome action(Boolean param) {
            return null;
        }
    }


    @Test
    public void passesAPathVarAsAString() {
        MockFactoryVinna<StringArgController> app = new MockFactoryVinna<StringArgController>() {
            @Override
            protected void routes() {
                get("/users/{id}").withController(StringArgController.class).action(param("id").asString());
            }
        };
        MockedRequest mockedRequest = MockedRequest.get("/users/a").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);
        try {
            resolution.callAction();
            verify(app.controllerMock).action("a");
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void passesAPathVarAsAnInt() {
        MockFactoryVinna<IntArgController> app = new MockFactoryVinna<IntArgController>() {
            @Override
            protected void routes() {
                get("/users/{id}").withController(IntArgController.class).action(param("id").asInt());
            }
        };
        MockedRequest mockedRequest = MockedRequest.get("/users/5").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);
        try {
            resolution.callAction();
            verify(app.controllerMock).action(5);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void passesAPathVarAsAnInteger() {
        MockFactoryVinna<IntegerArgController> app = new MockFactoryVinna<IntegerArgController>() {
            @Override
            protected void routes() {
                get("/users/{id}").withController(IntegerArgController.class).action(param("id").asInt());
            }
        };
        MockedRequest mockedRequest = MockedRequest.get("/users/666").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);
        try {
            resolution.callAction();
            verify(app.controllerMock).action(666);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void passesAPathVarAsABoolForTrue() {
        MockFactoryVinna<BoolArgController> app = new MockFactoryVinna<BoolArgController>() {
            @Override
            protected void routes() {
                get("/users/{id}").withController(BoolArgController.class).action(param("id").asBoolean());
            }
        };
        MockedRequest mockedRequest = MockedRequest.get("/users/true").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);
        try {
            resolution.callAction();
            verify(app.controllerMock).action(true);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void passesAPathVarAsABoolForFalse() {
        MockFactoryVinna<BoolArgController> app = new MockFactoryVinna<BoolArgController>() {
            @Override
            protected void routes() {
                get("/users/{id}").withController(BoolArgController.class).action(param("id").asBoolean());
            }
        };
        MockedRequest mockedRequest = MockedRequest.get("/users/false").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);
        try {
            resolution.callAction();
            verify(app.controllerMock).action(false);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void passesAPathVarAsABooleanForTrue() {
        MockFactoryVinna<BooleanArgController> app = new MockFactoryVinna<BooleanArgController>() {
            @Override
            protected void routes() {
                get("/users/{id}").withController(BooleanArgController.class).action(param("id").asBoolean());
            }
        };
        MockedRequest mockedRequest = MockedRequest.get("/users/true").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);
        try {
            resolution.callAction();
            verify(app.controllerMock).action(Boolean.TRUE);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void passesAPathVarAsABooleanForFalse() {
        MockFactoryVinna<BooleanArgController> app = new MockFactoryVinna<BooleanArgController>() {
            @Override
            protected void routes() {
                get("/users/{id}").withController(BooleanArgController.class).action(param("id").asBoolean());
            }
        };
        MockedRequest mockedRequest = MockedRequest.get("/users/false").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);
        try {
            resolution.callAction();
            verify(app.controllerMock).action(Boolean.FALSE);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }


    //TODO: moar tests !
}
