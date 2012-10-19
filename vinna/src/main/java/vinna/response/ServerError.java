package vinna.response;

public class ServerError extends ResponseBuilder {
    private static enum Kind {
        INTERNAL_ERROR(500),
        NOT_IMPLEMENTED(501),
        BAD_GATEWAY(502),
        SERVICE_UNAVAILABLE(503),
        GATEWAY_TIMEOUT(504);

        public final int status;

        private Kind(int status) {
            this.status = status;
        }
    }

    public static ServerError internalError() {
        return new ServerError(Kind.INTERNAL_ERROR);
    }

    public static ServerError notImplemented() {
        return new ServerError(Kind.NOT_IMPLEMENTED);
    }

    public static ServerError badGateway() {
        return new ServerError(Kind.BAD_GATEWAY);
    }

    public static ServerError serviceUnavailable() {
        return new ServerError(Kind.SERVICE_UNAVAILABLE);
    }

    public static ServerError gatewayTimeout() {
        return new ServerError(Kind.GATEWAY_TIMEOUT);
    }

    private ServerError(Kind kind) {
        super(kind.status);
    }
}
