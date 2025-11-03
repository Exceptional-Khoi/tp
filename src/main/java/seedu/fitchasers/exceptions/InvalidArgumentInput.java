package seedu.fitchasers.exceptions;

//@@author Exceptional-Khoi
/**
 * Exception thrown when a command or method receives an invalid argument.
 * <p>
 * This exception is used to indicate improper or malformed user input
 * that violates expected command syntax or parameter rules.
 */
public class InvalidArgumentInput extends Exception {
    public InvalidArgumentInput(String message) {
        super(message);
    }
}
