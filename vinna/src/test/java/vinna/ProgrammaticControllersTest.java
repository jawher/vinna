package vinna;

import org.junit.Test;
import org.mockito.Mockito;
import vinna.exception.VuntimeException;
import vinna.helpers.MockedRequest;
import vinna.outcome.Outcome;
import vinna.route.RouteResolution;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static vinna.helpers.VinnaMatchers.eqColl;

public class ProgrammaticControllersTest {

    private static class MockFactoryVinna<T> extends Vinna {
        public T controllerMock;

        @Override
        protected ControllerFactory controllerFactory() {
            return new ControllerFactory() {
                @Override
                public Object create(String id, Class<?> clazz) {
                    if (clazz == null) {
                        try {
                            clazz = Class.forName(id);
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
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

    public static class StringArgAndIntegerArgController {

        public Outcome action(String param) {
            return null;
        }

        public Outcome action(Integer param) {
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

    public static class LongPrimitiveArgController {

        public Outcome action(long param) {
            return null;
        }
    }

    public static class LongArgController {

        public Outcome action(Long param) {
            return null;
        }
    }

    public static class ShortPrimitiveArgController {

        public Outcome action(short param) {
            return null;
        }
    }

    public static class ShortArgController {

        public Outcome action(Short param) {
            return null;
        }
    }

    public static class BytePrimitiveArgController {

        public Outcome action(byte param) {
            return null;
        }
    }

    public static class ByteArgController {

        public Outcome action(Byte param) {
            return null;
        }
    }

    public static class FloatPrimitiveArgController {

        public Outcome action(float param) {
            return null;
        }
    }

    public static class FloatArgController {

        public Outcome action(Float param) {
            return null;
        }
    }

    public static class DoublePrimitiveArgController {

        public Outcome action(double param) {
            return null;
        }
    }

    public static class DoubleArgController {

        public Outcome action(Double param) {
            return null;
        }
    }

    public static class BigDecimalArgController {

        public Outcome action(BigDecimal param) {
            return null;
        }
    }

    public static class BigIntegerArgController {

        public Outcome action(BigInteger param) {
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

        resolution.callAction(app);
        verify(app.controllerMock).action("a");

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
        resolution.callAction(app);
        verify(app.controllerMock).action(5);

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

        resolution.callAction(app);
        verify(app.controllerMock).action(666);

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

        resolution.callAction(app);
        verify(app.controllerMock).action(true);

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

        resolution.callAction(app);
        verify(app.controllerMock).action(false);

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

        resolution.callAction(app);
        verify(app.controllerMock).action(Boolean.TRUE);

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

        resolution.callAction(app);
        verify(app.controllerMock).action(Boolean.FALSE);

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

        resolution.callAction(app);
        verify(app.controllerMock).action(argThat(eqColl(params)));

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

        resolution.callAction(app);
        verify(app.controllerMock).action(argThat(eqColl(1, 2, 3)));

    }

    @Test
    public void passesARequestQueryAsAnInteger() {
        MockFactoryVinna<IntegerArgController> app = new MockFactoryVinna<IntegerArgController>() {
            @Override
            protected void routes() {
                get("/users").withController(IntegerArgController.class).action(req.param("id").asInt());
            }
        };

        MockedRequest mockedRequest = MockedRequest.get("/users").param("id", "1").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).action(1);

    }

    @Test
    public void passesARequestHeaderAsAnInteger() {
        MockFactoryVinna<IntegerArgController> app = new MockFactoryVinna<IntegerArgController>() {
            @Override
            protected void routes() {
                get("/users").withController(IntegerArgController.class).action(req.header("x-id").asInt());
            }
        };

        MockedRequest mockedRequest = MockedRequest.get("/users").header("x-id", "1").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).action(1);

    }

    @Test
    public void passesARequestHeaderAsAnIntegerCollection() {
        MockFactoryVinna<CollectionArgController> app = new MockFactoryVinna<CollectionArgController>() {
            @Override
            protected void routes() {
                get("/users").withController(CollectionArgController.class).action(req.header("x-ids").asCollection(Integer.class));
            }
        };

        String[] params = new String[]{"1", "2", "3"};
        MockedRequest mockedRequest = MockedRequest.get("/users").header("x-ids", params).build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).action(argThat(eqColl(1, 2, 3)));

    }

    @Test
    public void passesAPathVarAsALongPrimitive() {
        MockFactoryVinna<LongPrimitiveArgController> app = new MockFactoryVinna<LongPrimitiveArgController>() {
            @Override
            protected void routes() {
                get("/users/{id}").withController(LongPrimitiveArgController.class).action(param("id").asLong());
            }
        };
        MockedRequest mockedRequest = MockedRequest.get("/users/666").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).action(666);

    }

    @Test
    public void passesAPathVarAsALong() {
        MockFactoryVinna<LongArgController> app = new MockFactoryVinna<LongArgController>() {
            @Override
            protected void routes() {
                get("/users/{id}").withController(LongArgController.class).action(param("id").asLong());
            }
        };
        MockedRequest mockedRequest = MockedRequest.get("/users/666").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).action(Long.parseLong("666"));

    }

    @Test
    public void passesAPathVarAsAShortPrimitive() {
        MockFactoryVinna<ShortPrimitiveArgController> app = new MockFactoryVinna<ShortPrimitiveArgController>() {
            @Override
            protected void routes() {
                get("/users/{id}").withController(ShortPrimitiveArgController.class).action(param("id").asShort());
            }
        };
        MockedRequest mockedRequest = MockedRequest.get("/users/666").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).action(Short.parseShort("666"));

    }

    @Test
    public void passesAPathVarAsAShort() {
        MockFactoryVinna<ShortArgController> app = new MockFactoryVinna<ShortArgController>() {
            @Override
            protected void routes() {
                get("/users/{id}").withController(ShortArgController.class).action(param("id").asShort());
            }
        };
        MockedRequest mockedRequest = MockedRequest.get("/users/666").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).action(Short.parseShort("666"));

    }

    @Test
    public void passesAPathVarAsABytePrimitive() {
        MockFactoryVinna<BytePrimitiveArgController> app = new MockFactoryVinna<BytePrimitiveArgController>() {
            @Override
            protected void routes() {
                get("/users/{id}").withController(BytePrimitiveArgController.class).action(param("id").asByte());
            }
        };
        MockedRequest mockedRequest = MockedRequest.get("/users/42").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).action(Byte.parseByte("42"));

    }

    @Test
    public void passesAPathVarAsAByte() {
        MockFactoryVinna<ByteArgController> app = new MockFactoryVinna<ByteArgController>() {
            @Override
            protected void routes() {
                get("/users/{id}").withController(ByteArgController.class).action(param("id").asByte());
            }
        };
        MockedRequest mockedRequest = MockedRequest.get("/users/42").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).action(Byte.parseByte("42"));

    }

    @Test
    public void passesAPathVarAsAFloatPrimitive() {
        MockFactoryVinna<FloatPrimitiveArgController> app = new MockFactoryVinna<FloatPrimitiveArgController>() {
            @Override
            protected void routes() {
                get("/users/{id}").withController(FloatPrimitiveArgController.class).action(param("id").asFloat());
            }
        };
        MockedRequest mockedRequest = MockedRequest.get("/users/42.007").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).action(Float.parseFloat("42.007"));
    }

    @Test
    public void passesAPathVarAsAFloat() {
        MockFactoryVinna<FloatArgController> app = new MockFactoryVinna<FloatArgController>() {
            @Override
            protected void routes() {
                get("/users/{id}").withController(FloatArgController.class).action(param("id").asFloat());
            }
        };
        MockedRequest mockedRequest = MockedRequest.get("/users/42.007").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).action(Float.parseFloat("42.007"));

    }

    @Test
    public void passesAPathVarAsADoublePrimitive() {
        MockFactoryVinna<DoublePrimitiveArgController> app = new MockFactoryVinna<DoublePrimitiveArgController>() {
            @Override
            protected void routes() {
                get("/users/{id}").withController(DoublePrimitiveArgController.class).action(param("id").asDouble());
            }
        };
        MockedRequest mockedRequest = MockedRequest.get("/users/42.0d").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).action(Double.parseDouble("42.0d"));

    }

    @Test
    public void passesAPathVarAsADouble() {
        MockFactoryVinna<DoubleArgController> app = new MockFactoryVinna<DoubleArgController>() {
            @Override
            protected void routes() {
                get("/users/{id}").withController(DoubleArgController.class).action(param("id").asDouble());
            }
        };
        MockedRequest mockedRequest = MockedRequest.get("/users/42.0d").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).action(Double.parseDouble("42.0d"));

    }

    @Test
    public void passesAPathVarAsABigDecimal() {
        MockFactoryVinna<BigDecimalArgController> app = new MockFactoryVinna<BigDecimalArgController>() {
            @Override
            protected void routes() {
                get("/users/{id}").withController(BigDecimalArgController.class).action(param("id").asBigDecimal());
            }
        };
        MockedRequest mockedRequest = MockedRequest.get("/users/10").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).action(BigDecimal.TEN);

    }

    @Test
    public void passesAPathVarAsABigInteger() {
        MockFactoryVinna<BigIntegerArgController> app = new MockFactoryVinna<BigIntegerArgController>() {
            @Override
            protected void routes() {
                get("/users/{id}").withController(BigIntegerArgController.class).action(param("id").asBigInteger());
            }
        };
        MockedRequest mockedRequest = MockedRequest.get("/users/10").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).action(BigInteger.TEN);

    }

    @Test(expected = VuntimeException.class)
    public void passesAPathVarAsAIntWithANullValue() {
        MockFactoryVinna<IntArgController> app = new MockFactoryVinna<IntArgController>() {
            @Override
            protected void routes() {
                get("/users").withController(IntArgController.class).action(req.param("id").asInt());
            }
        };
        MockedRequest mockedRequest = MockedRequest.get("/users").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);

    }

    @Test
    public void passesAPathVarAsAIntegerWithANullValue() {
        MockFactoryVinna<IntegerArgController> app = new MockFactoryVinna<IntegerArgController>() {
            @Override
            protected void routes() {
                get("/users").withController(IntegerArgController.class).action(req.param("id").asInt());
            }
        };
        MockedRequest mockedRequest = MockedRequest.get("/users").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).action(null);

    }

    @Test
    public void passesAPathVarToAMethodDefinedByWithMethod() {
        MockFactoryVinna<StringArgController> app = new MockFactoryVinna<StringArgController>() {
            @Override
            protected void routes() {
                get("/users/{id}").withControllerId("vinna.ProgrammaticControllersTest$StringArgController").withMethod("action({id})");
            }
        };
        MockedRequest mockedRequest = MockedRequest.get("/users/a").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock).action("a");

    }

    @Test
    public void passesAPathIntegerVarToAMethodWithAmbiguousMethod() {
        MockFactoryVinna<StringArgAndIntegerArgController> app = new MockFactoryVinna<StringArgAndIntegerArgController>() {
            @Override
            protected void routes() {
                get("/users/{id: \\d+}").withControllerId("vinna.ProgrammaticControllersTest$StringArgAndIntegerArgController")
                        .withMethod("action({id:Integer})");
            }
        };
        MockedRequest mockedRequest = MockedRequest.get("/users/5").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock, Mockito.never()).action(anyString());
        verify(app.controllerMock).action(5);

    }

    @Test
    public void passesAPathStringVarToAMethodWithAmbiguousMethod() {
        MockFactoryVinna<StringArgAndIntegerArgController> app = new MockFactoryVinna<StringArgAndIntegerArgController>() {
            @Override
            protected void routes() {
                get("/users/{id}").withControllerId("vinna.ProgrammaticControllersTest$StringArgAndIntegerArgController")
                        .withMethod("action({id:String})");
            }
        };
        MockedRequest mockedRequest = MockedRequest.get("/users/abc").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock, Mockito.never()).action(anyInt());
        verify(app.controllerMock).action("abc");

    }

    @Test(expected = VuntimeException.class)
    public void passesAPathVarToAMethodWithUnsolvableAmbiguousMethod() {
        MockFactoryVinna<StringArgAndIntegerArgController> app = new MockFactoryVinna<StringArgAndIntegerArgController>() {
            @Override
            protected void routes() {
                get("/users/{id}").withControllerId("vinna.ProgrammaticControllersTest$StringArgAndIntegerArgController")
                        .withMethod("action({id})");
            }
        };
        MockedRequest mockedRequest = MockedRequest.get("/users/abc").build();
        RouteResolution resolution = app.match(mockedRequest);
        assertNotNull(resolution);

        resolution.callAction(app);
        verify(app.controllerMock, Mockito.never()).action(anyInt());
        verify(app.controllerMock, Mockito.never()).action(anyString());

    }

    //TODO: moar tests !
}
