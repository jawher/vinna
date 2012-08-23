package vinna.response;

public class RedirectResponse extends ResponseBuilder {
    private static enum Kind {
        MOVED_PERMANENTLY(301),
        FOUND(302),
        SEE_OTHER(303),
        NOT_MODIFIED(304),
        TEMPORARY_REDIRECT(307),
        PERMANENT_REDIRECT(308);

        public final int status;

        private Kind(int status) {
            this.status = status;
        }
    }

    public static RedirectResponse found(String location) {
        return new RedirectResponse(location, Kind.FOUND);
    }

    public static RedirectResponse seeOther(String location) {
        return new RedirectResponse(location, Kind.SEE_OTHER);
    }

    public static RedirectResponse temporary(String location) {
        return new RedirectResponse(location, Kind.TEMPORARY_REDIRECT);
    }

    public static RedirectResponse permanently(String location) {
        return new RedirectResponse(location, Kind.PERMANENT_REDIRECT);
    }

    public static RedirectResponse moved(String location) {
        return new RedirectResponse(location, Kind.MOVED_PERMANENTLY);
    }

    public static RedirectResponse notModified() {
        return new RedirectResponse(null, Kind.NOT_MODIFIED);
    }

    private RedirectResponse(String location, Kind kind) {
        super(kind.status);

        if (location != null) {
            location(location);
        }
    }
}
