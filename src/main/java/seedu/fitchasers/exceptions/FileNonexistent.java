package seedu.fitchasers.exceptions;

//@@author Exceptional-Khoi
/**
 * Exception thrown when a file required by the program does not exist.
 * <p>
 * This exception is used to indicate missing data or configuration files
 * during loading or saving operations in the application.
 */
public class FileNonexistent extends Exception {
    public FileNonexistent(String message) {
        super(message);
    }
}
