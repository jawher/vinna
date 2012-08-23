package vinna.response;

public class RedirectResponse extends ResponseBuilder {
    public enum Kind {
        MOVED_PERMANENTLY(301),
        FOUND(302),
        SEE_OTHER(303),
        NOT_MODIFIED(304),
        TEMPORARY_REDIRECT(307),
        PERMANENT_REDIRECT(308);

        public final int status;

        Kind(int status) {
            this.status = status;
        }
    }

    public RedirectResponse(String location) {
        this(location, Kind.FOUND);
    }

    public RedirectResponse(String location, Kind kind) {
        super(kind.status);
        location(location);
    }
}
