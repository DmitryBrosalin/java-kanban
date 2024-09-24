package exceptions;

public class NoParentEpicException extends RuntimeException {
    public NoParentEpicException(final String message) {
        super(message);
    }
}
