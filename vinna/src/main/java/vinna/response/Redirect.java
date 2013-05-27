package vinna.response;

public class Redirect extends ResponseBuilder {
    private static enum Kind {
        MULTIPLE_CHOICE(300),
        MOVED_PERMANENTLY(301),
        FOUND(302),
        SEE_OTHER(303),
        NOT_MODIFIED(304),
        USE_PROXY(305),
        TEMPORARY_REDIRECT(307),
        PERMANENT_REDIRECT(308),
        TOO_MANY_REDIRECT(310);

        public final int status;

        private Kind(int status) {
            this.status = status;
        }
    }

    public static Redirect multipleChoice(String location) {
        return new Redirect(location, Kind.MULTIPLE_CHOICE);
    }

    public static Redirect moved(String location) {
        return new Redirect(location, Kind.MOVED_PERMANENTLY);
    }

    public static Redirect found(String location) {
        return new Redirect(location, Kind.FOUND);
    }

    public static Redirect seeOther(String location) {
        return new Redirect(location, Kind.SEE_OTHER);
    }

    public static Redirect notModified() {
        return new Redirect(null, Kind.NOT_MODIFIED);
    }

    public static Redirect userProxy() {
        return new Redirect(null, Kind.USE_PROXY);
    }

    public static Redirect temporary(String location) {
        return new Redirect(location, Kind.TEMPORARY_REDIRECT);
    }

    public static Redirect permanently(String location) {
        return new Redirect(location, Kind.PERMANENT_REDIRECT);
    }

    public static Redirect tooManyRedirect(String location) {
        return new Redirect(location, Kind.TOO_MANY_REDIRECT);
    }

    private Redirect(String location, Kind kind) {
        super(kind.status);
        redirect(location);
    }
}
