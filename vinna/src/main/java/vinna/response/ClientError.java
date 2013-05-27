package vinna.response;

public class ClientError extends ResponseBuilder {
    private static enum Kind {
        BAD_REQUEST(400),
        UNAUTHORIZED(401),
        PAYEMENT_REQUIRED(402),
        FORBIDDEN(403),
        NOT_FOUND(404),
        METHOD_NOT_ALLOWED(405),
        NOT_ACCEPTABLE(406),
        PROXY_AUTHENTICATION_REQUIRED(407),
        REQUEST_TIMEOUT(408),
        CONFLICT(409),
        GONE(410),
        LENGTH_REQUIRED(411),
        PRECONDITION_FAILED(412),
        REQUEST_ENTITY_TOO_LARGE(413),
        REQUEST_URI_TOO_LONG(414),
        UNSUPPORTED_MEDIA_TYPE(415),
        REQUESTED_RANGE_UNSATISFIABLE(416),
        EXPECTATION_FAILED(417),
        TEA_POT(418),
        UPGRADE_REQUIRED(426),
        TOO_MANY_REQUESTS(429),
        CLIENT_HAS_CLOSED_CONNECTION(499);

        public final int status;

        private Kind(int status) {
            this.status = status;
        }
    }

    public static ClientError badRequest() {
        return new ClientError(Kind.BAD_REQUEST);
    }

    public static ClientError unauthorized() {
        return new ClientError(Kind.UNAUTHORIZED);
    }

    public static ClientError payementRequired() {
        return new ClientError(Kind.PAYEMENT_REQUIRED);
    }

    public static ClientError forbidden() {
        return new ClientError(Kind.FORBIDDEN);
    }

    public static ClientError notFound() {
        return new ClientError(Kind.NOT_FOUND);
    }

    public static ClientError methodNotAllowed() {
        return new ClientError(Kind.METHOD_NOT_ALLOWED);
    }

    public static ClientError notAcceptable() {
        return new ClientError(Kind.NOT_ACCEPTABLE);
    }

    public static ClientError proxyAuthenticationRequired() {
        return new ClientError(Kind.PROXY_AUTHENTICATION_REQUIRED);
    }

    public static ClientError requestTimeOut() {
        return new ClientError(Kind.REQUEST_TIMEOUT);
    }

    public static ClientError conflict() {
        return new ClientError(Kind.CONFLICT);
    }

    public static ClientError gone() {
        return new ClientError(Kind.GONE);
    }

    public static ClientError lenghtRequired() {
        return new ClientError(Kind.LENGTH_REQUIRED);
    }

    public static ClientError preconditionFailed() {
        return new ClientError(Kind.PRECONDITION_FAILED);
    }

    public static ClientError requestEntityTooLarge() {
        return new ClientError(Kind.REQUEST_ENTITY_TOO_LARGE);
    }

    public static ClientError requestUriTooLong() {
        return new ClientError(Kind.REQUEST_URI_TOO_LONG);
    }

    public static ClientError unsupportedMediaType() {
        return new ClientError(Kind.UNSUPPORTED_MEDIA_TYPE);
    }

    public static ClientError requestedRangeUnsatisfiable() {
        return new ClientError(Kind.REQUESTED_RANGE_UNSATISFIABLE);
    }

    public static ClientError expectationFailed() {
        return new ClientError(Kind.EXPECTATION_FAILED);
    }

    public static ClientError teaPot() {
        return new ClientError(Kind.TEA_POT);
    }

    public static ClientError upgradeRequired() {
        return new ClientError(Kind.UPGRADE_REQUIRED);
    }

    public static ClientError tooManyRequest() {
        return new ClientError(Kind.TOO_MANY_REQUESTS);
    }

    public static ClientError clientHasClosedConnection() {
        return new ClientError(Kind.CLIENT_HAS_CLOSED_CONNECTION);
    }

    private ClientError(Kind kind) {
        super(kind.status);
    }
}
