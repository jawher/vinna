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

    public ServerError internalError() {
        return new ServerError(Kind.INTERNAL_ERROR);
    }

    public ServerError notImplemented() {
        return new ServerError(Kind.NOT_IMPLEMENTED);
    }

    public ServerError badGateway() {
        return new ServerError(Kind.BAD_GATEWAY);
    }

    public ServerError serviceUnavailable() {
        return new ServerError(Kind.SERVICE_UNAVAILABLE);
    }

    public ServerError gatewayTimeout() {
        return new ServerError(Kind.GATEWAY_TIMEOUT);
    }

    private ServerError(Kind kind) {
        super(kind.status);
    }
}
