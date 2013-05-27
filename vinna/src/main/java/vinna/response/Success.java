package vinna.response;

public class Success extends ResponseBuilder {
    private static enum Kind {
        OK(200),
        CREATED(201),
        ACCEPTED(202),
        NON_AUTHORITATIVE_INFORMATION(203),
        NO_CONTENT(204),
        RESET_CONTENT(205),
        PARTIAL_CONTENT(206);

        public final int status;

        private Kind(int status) {
            this.status = status;
        }
    }

    public static Success ok() {
        return new Success(Kind.OK);
    }

    public static Success created() {
        return new Success(Kind.CREATED);
    }

    public static Success accepted() {
        return new Success(Kind.ACCEPTED);
    }

    public static Success nonAuthoritativeInformation() {
        return new Success(Kind.NON_AUTHORITATIVE_INFORMATION);
    }

    public static Success noContent() {
        return new Success(Kind.NO_CONTENT);
    }

    public static Success resetContent() {
        return new Success(Kind.RESET_CONTENT);
    }

    public static Success partialContent() {
        return new Success(Kind.PARTIAL_CONTENT);
    }

    private Success(Kind kind) {
        super(kind.status);
    }
}
