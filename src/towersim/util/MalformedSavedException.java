package towersim.util;

/**
 * Exception thrown when a save file is in an invalid format or contains incorrect data.
 */
public class MalformedSavedException extends Exception {
    /**
     * Constructs a new MalformedSaveException with no detail message or cause.
     */
    public MalformedSavedException() {
        super();
    }

    /**
     * Constructs a MalformedSaveException that contains a helpful detail message explaining why
     * the exception occurred.
     * @param message detail message
     */
    public MalformedSavedException(String message) {
        super(message);
    }

    /**
     * Constructs a MalformedSaveException that stores the underlying cause of the exception.
     * @param cause throwable that caused this exception
     */
    public MalformedSavedException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a MalformedSaveException that contains a helpful detail message explaining why
     * the exception occurred and the underlying cause of the exception.
     * @param message detail message
     * @param cause throwable that caused this exception
     */
    public MalformedSavedException(String message, Throwable cause) {
        super(message, cause);
    }
}
