package vinna;

import bar.controllers.Bar;
import foo.controllers.Empty3;
import org.junit.Assert;
import org.junit.Test;
import vinna.controllers.Application;
import vinna.controllers.sub.Empty2;
import vinna.helpers.MockedRequest;
import vinna.route.RouteResolution;

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
            super(config);
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
        final Object controller = app.createController("sub.empty2", null);
        assertNotNull(controller);
        assertTrue(controller instanceof Empty2);
    }

    @Test
    public void test0ConfigSetsUpDefaultRoutesWithVinnaAsBasePkg() {
        SpyVinna<Application> app = new SpyVinna<>(Collections.<String, Object>emptyMap());

        MockedRequest mockedRequest = MockedRequest.get("/").build();
        RouteResolution resolution = app.match(mockedRequest);
        Assert.assertNotNull(resolution);

        resolution.callAction(app);

        verify(app.controllerSpy).index();
    }

    @Test
    public void testBasePkgSetsUpDefaultControllerFactoryWithCorrectPkg() {
        Vinna app = new Vinna(Collections.<String, Object>singletonMap(Vinna.BASE_PACKAGE, "foo"));
        final Object controller = app.createController("empty3", null);
        assertNotNull(controller);
        assertTrue(controller instanceof Empty3);
    }

    @Test
    public void testBasePkgSetsUpDefaultRoutesWhenNoCustomRoutesIsThere() {
        SpyVinna<foo.controllers.Application> app = new SpyVinna<>(Collections.<String, Object>singletonMap(Vinna.BASE_PACKAGE, "foo"));

        MockedRequest mockedRequest = MockedRequest.get("/").build();
        RouteResolution resolution = app.match(mockedRequest);
        Assert.assertNotNull(resolution);

        resolution.callAction(app);

        verify(app.controllerSpy).index();
    }

    @Test
    public void testBasePkgPicksUpCustomRoutes() {
        SpyVinna<Bar> app = new SpyVinna<>(Collections.<String, Object>singletonMap(Vinna.BASE_PACKAGE, "bar"));

        MockedRequest mockedRequest = MockedRequest.get("/").build();
        RouteResolution resolution = app.match(mockedRequest);
        Assert.assertNotNull(resolution);

        resolution.callAction(app);

        verify(app.controllerSpy).action();
    }

    @Test
    public void testBasePkgWithCustomRoutesDoesntSetUpDefaultRoutes() {
        Vinna app = new Vinna(Collections.<String, Object>singletonMap(Vinna.BASE_PACKAGE, "bar"));

        MockedRequest mockedRequest = MockedRequest.get("/garbage").build();
        RouteResolution resolution = app.match(mockedRequest);
        Assert.assertNull(resolution);
    }

    //moar tests !
}