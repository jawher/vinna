package vinna;

import bar.controllers.Bar;
import foo.controllers.Empty3;
import org.junit.Assert;
import org.junit.Test;
import vinna.controllers.Application;
import vinna.controllers.Baz;
import vinna.controllers.sub.Empty2;
import vinna.helpers.MockedRequest;
import vinna.route.RouteResolution;

import java.io.StringReader;
import java.util.Collections;
import java.util.Map;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class VinnaConfigTest {

    private static class SpyVinna<T> extends Vinna {
        public T controllerSpy;

        private SpyVinna(Map<String, Object> config) {
            init(config);
        }

        @Override
        protected ControllerFactory controllerFactory(Map<String, Object> config) {
            final ControllerFactory realFactory = super.controllerFactory(config);
            return new ControllerFactory() {
                @Override
                public Object create(String id, Class<?> clazz) {
                    controllerSpy = (T) spy(realFactory.create(id, clazz));
                    return controllerSpy;
                }
            };
        }
    }

    @Test
    public void test0ConfigSetsUpDefaultControllerFactoryWithVinnaAsBasePkg() {
        Vinna app = new Vinna();
        app.init(Collections.<String, Object>emptyMap());
        final Object controller = app.getControllerFactory().create("sub.empty2", null);
        assertNotNull(controller);
        assertTrue(controller instanceof Empty2);
    }

    @Test
    public void test0ConfigSetsUpDefaultRoutesWithVinnaAsBasePkg() {
        SpyVinna<Application> app = new SpyVinna<>(Collections.<String, Object>emptyMap());

        MockedRequest mockedRequest = MockedRequest.get("/").build();
        RouteResolution resolution = app.getRouter().match(mockedRequest);
        Assert.assertNotNull(resolution);

        resolution.callAction(mockedRequest, app);

        verify(app.controllerSpy).index();
    }

    @Test
    public void testBasePkgSetsUpDefaultControllerFactoryWithCorrectPkg() {
        Vinna app = new Vinna();
        app.init(Collections.<String, Object>singletonMap(Vinna.BASE_PACKAGE, "foo"));
        final Object controller = app.getControllerFactory().create("empty3", null);
        assertNotNull(controller);
        assertTrue(controller instanceof Empty3);
    }

    @Test
    public void testBasePkgSetsUpDefaultRoutesWhenNoCustomRoutesIsThere() {
        SpyVinna<foo.controllers.Application> app = new SpyVinna<>(Collections.<String, Object>singletonMap(Vinna.BASE_PACKAGE, "foo"));

        MockedRequest mockedRequest = MockedRequest.get("/").build();
        RouteResolution resolution = app.getRouter().match(mockedRequest);
        Assert.assertNotNull(resolution);

        resolution.callAction(mockedRequest, app);

        verify(app.controllerSpy).index();
    }

    @Test
    public void testBasePkgPicksUpCustomRoutes() {
        SpyVinna<Bar> app = new SpyVinna<>(Collections.<String, Object>singletonMap(Vinna.BASE_PACKAGE, "bar"));

        MockedRequest mockedRequest = MockedRequest.get("/").build();
        RouteResolution resolution = app.getRouter().match(mockedRequest);
        Assert.assertNotNull(resolution);

        resolution.callAction(mockedRequest, app);

        verify(app.controllerSpy).action();
    }

    @Test
    public void testBasePkgWithCustomRoutesDoesntSetUpDefaultRoutes() {
        Vinna app = new Vinna();
        app.init(Collections.<String, Object>singletonMap(Vinna.BASE_PACKAGE, "bar"));

        MockedRequest mockedRequest = MockedRequest.get("/garbage").build();
        RouteResolution resolution = app.getRouter().match(mockedRequest);
        Assert.assertNull(resolution);
    }

    @Test
    public void testCustomRoutes() {
        SpyVinna<Baz> app = new SpyVinna<>(Collections.<String, Object>singletonMap(Vinna.ROUTES, "vinna/routes1"));

        MockedRequest mockedRequest = MockedRequest.get("/custom1").build();
        RouteResolution resolution = app.getRouter().match(mockedRequest);
        Assert.assertNotNull(resolution);

        resolution.callAction(mockedRequest, app);

        verify(app.controllerSpy).action();
    }

    @Test
    public void testMultipleCustomRoutes() {
        SpyVinna<Baz> app = new SpyVinna<>(Collections.<String, Object>singletonMap(Vinna.ROUTES, "vinna/routes1, vinna/routes2"));

        MockedRequest mockedRequest = MockedRequest.get("/custom1").build();
        RouteResolution resolution = app.getRouter().match(mockedRequest);
        Assert.assertNotNull(resolution);
        resolution.callAction(mockedRequest, app);
        verify(app.controllerSpy).action();

        mockedRequest = MockedRequest.get("/custom2").build();
        resolution = app.getRouter().match(mockedRequest);
        Assert.assertNotNull(resolution);
        resolution.callAction(mockedRequest, app);
        verify(app.controllerSpy).action();
    }

    @Test
    public void testCustomRoutesDoesntSetUpDefaultRoutes() {
        SpyVinna<Baz> app = new SpyVinna<>(Collections.<String, Object>singletonMap(Vinna.ROUTES, "vinna/routes1"));

        MockedRequest mockedRequest = MockedRequest.get("/garbage").build();
        RouteResolution resolution = app.getRouter().match(mockedRequest);
        Assert.assertNull(resolution);
    }

    public static class MyControllerFactory implements ControllerFactory {

        @Override
        public Object create(String id, Class<?> clazz) {
            return new Baz();
        }
    }

    @Test
    public void testCustomControllerFactory() {
        Vinna app = new Vinna();
        app.init(Collections.<String, Object>singletonMap(Vinna.CONTROLLER_FACTORY, "vinna.VinnaConfigTest$MyControllerFactory"));

        final Object controller = app.getControllerFactory().create("anything", null);
        assertTrue(controller instanceof Baz);
    }

    @Test
    public void testMixingDeclarativeAndProgrammaticRoutes() {
        Vinna app = new Vinna() {
            @Override
            protected void routes(Map<String, Object> config) {
                loadRoutes(new StringReader("GET /declarative foo.bar()"));
                get("/programmatic").withController(Baz.class).action();
            }
        };
        app.init(Collections.<String, Object>emptyMap());

        MockedRequest mockedRequest = MockedRequest.get("/declarative").build();
        RouteResolution resolution = app.getRouter().match(mockedRequest);
        Assert.assertNotNull(resolution);

        mockedRequest = MockedRequest.get("/programmatic").build();
        resolution = app.getRouter().match(mockedRequest);
        Assert.assertNotNull(resolution);
    }

    //moar tests !
}
