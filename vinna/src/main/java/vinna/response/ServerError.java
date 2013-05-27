package vinna.response;

public class ServerError extends ResponseBuilder {
    private static enum Kind {
        INTERNAL_ERROR(500),
        NOT_IMPLEMENTED(501),
        BAD_GATEWAY(502),
        SERVICE_UNAVAILABLE(503),
        GATEWAY_TIMEOUT(504),
        HTTP_VERSION_NOT_SUPPORTED(505),
        VARIANT_ALSO_NEGOCIATE(506),
        INSUFFICIENT_STORAGE(507),
        LOOP_DETECTED(508),
        BANDWIDTH_LIMIT_EXCEEDED(509),
        NOT_EXTENDED(510);

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

    public static ServerError httpVersionNotSupported() {
        return new ServerError(Kind.HTTP_VERSION_NOT_SUPPORTED);
    }

    public static ServerError variantAlsoNegociate() {
        return new ServerError(Kind.VARIANT_ALSO_NEGOCIATE);
    }

    public static ServerError insufficientStorage() {
        return new ServerError(Kind.INSUFFICIENT_STORAGE);
    }

    public static ServerError loopDetected() {
        return new ServerError(Kind.LOOP_DETECTED);
    }

    public static ServerError bandwidthLimitExceeded() {
        return new ServerError(Kind.BANDWIDTH_LIMIT_EXCEEDED);
    }

    public static ServerError notExtended() {
        return new ServerError(Kind.NOT_EXTENDED);
    }

    private ServerError(Kind kind) {
        super(kind.status);
    }
}
