package be.crydust.tokenreplacer;

final class ReadConfigFailed extends Exception {
    public ReadConfigFailed() {
        super();
    }

    public ReadConfigFailed(String message) {
        super(message);
    }

    public ReadConfigFailed(String message, Throwable cause) {
        super(message, cause);
    }

    public ReadConfigFailed(Throwable cause) {
        super(cause);
    }
}
