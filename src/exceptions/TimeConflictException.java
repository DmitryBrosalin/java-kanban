package exceptions;

public class TimeConflictException extends RuntimeException {
    public TimeConflictException(final String message) {
        super(message);
    }
}
