package vinna;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import vinna.outcome.Outcome;
import vinna.request.MockedRequest;
import vinna.route.RouteResolution;

import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;

import static org.junit.Assert.*;
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

    private static class MockFactoryDeclarativeVinna<T> extends Vinna {
        public T controllerMock;

        private MockFactoryDeclarativeVinna(Reader routesReader) {
            super(routesReader);
        }

        @Override
        protected ControllerFactory controllerFactory() {
            return new ControllerFactory() {
                @Override
                public Object create(String id, Class<?> clazz) {
                    try {
                        clazz = Class.forName(id);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
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

    public static class CollectionArgController {

        public Outcome action(Collection<?> param) {
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
            resolution.callAction(app);
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
            resolution.callAction(app);
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
            resolution.callAction(app);
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
            resolution.callAction(app);
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
            resolution.callAction(app);
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
            resolution.callAction(app);
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
            resolution.callAction(app);
            verify(app.controllerMock).action(Boolean.FALSE);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void passesARequestQueryAsAStringCollection() {
        MockFactoryVinna<CollectionArgController> app = new MockFactoryVinna<CollectionArgController>() {
            @Override
            protected void routes() {
                get("/users").withController(CollectionArgController.class).action(req.param("names").asCollection(String.class));
            }
        };

        String[] params = new String[]{"Loulou", "Riri", "Fifi"};
        MockedRequest mockedRequest = MockedRequest.get("/users").param("names", params).build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        try {
            ArgumentCaptor<Collection> argument = ArgumentCaptor.forClass(Collection.class);
            resolution.callAction(app);
            verify(app.controllerMock).action(argument.capture());

            assertEquals(params.length, argument.getValue().size());
            Iterator iterator = argument.getValue().iterator();
            for (String param : params) {
                assertEquals(param, iterator.next());
            }
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void passesARequestQueryAsAnIntegerCollection() {
        MockFactoryVinna<CollectionArgController> app = new MockFactoryVinna<CollectionArgController>() {
            @Override
            protected void routes() {
                get("/users").withController(CollectionArgController.class).action(req.param("ids").asCollection(Integer.class));
            }
        };

        String[] params = new String[]{"1", "2", "3"};
        MockedRequest mockedRequest = MockedRequest.get("/users").param("ids", params).build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        try {
            ArgumentCaptor<Collection> argument = ArgumentCaptor.forClass(Collection.class);
            resolution.callAction(app);
            verify(app.controllerMock).action(argument.capture());

            assertEquals(params.length, argument.getValue().size());
            Iterator iterator = argument.getValue().iterator();
            for (String param : params) {
                assertEquals(Integer.parseInt(param), iterator.next());
            }
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
    public void handlesOverloadedActionsIfDifferentArgCountInDeclarativeMode() {
        String route = "GET /ambiguous/{id} vinna.ControllersTest$ControllerWithOverloadedAndDiffArgCountMethods.action({id})";
        MockFactoryDeclarativeVinna<ControllerWithOverloadedAndDiffArgCountMethods> app = new MockFactoryDeclarativeVinna<ControllerWithOverloadedAndDiffArgCountMethods>(new StringReader(route));
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

    public static class Two1ArgController {

        public Outcome action(Boolean param) {
            return null;
        }

        public Outcome action(String param) {
            return null;
        }
    }

    @Test
    public void failsWithTwoActionsWithTheSameNameAndParamCountInDeclarativeMode() {
        String route = "GET /ambiguous/{id} vinna.ControllersTest$Two1ArgController.action({id})";
        MockFactoryDeclarativeVinna<Two1ArgController> app = new MockFactoryDeclarativeVinna<Two1ArgController>(new StringReader(route));
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

    //TODO: moar tests !
}
