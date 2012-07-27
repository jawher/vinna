package vinna.toto;

import org.junit.Assert;
import org.junit.Test;
import vinna.Vinna;
import vinna.route.Route;
import vinna.route.RouteBuilder;

import java.lang.reflect.InvocationTargetException;

/**
 * @author lpereira
 */
public class TotoAppTest extends Vinna {

    private RouteBuilder routeBuilder;

    public TotoAppTest() {
        try {
            routeBuilder = get("/toto");
            routeBuilder.withController(Controller.class).noArgsProcessing();
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

    @Test
    public void checkRouteIsRegistered() {
        TotoAppTest toto = new TotoAppTest();
        Assert.assertFalse(toto.getRoutes().isEmpty());
    }

    @Test
    public void checkRouteIsVerb() {
        TotoAppTest toto = new TotoAppTest();
        Route route = toto.getRoutes().iterator().next();
        Assert.assertTrue(route.hasVerb("get"));
    }


    @Test
    public void checkRouteResolutionSuccess() {
        TotoAppTest toto = new TotoAppTest();
        Route route = toto.getRoutes().iterator().next();
        Assert.assertNotNull(route.match("/toto"));
    }

    @Test
    public void checkRouteResolutionFailure() {
        TotoAppTest toto = new TotoAppTest();
        Route route = toto.getRoutes().iterator().next();
        Assert.assertNull(route.match("/titi"));
    }
}
