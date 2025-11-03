package seedu.fitchasers.exceptions;

/**
 * Represents an error thrown when a data file is found to be corrupted or tampered with.
 */
public class CorruptedDataException extends Exception {
    public CorruptedDataException(String message) {
        super(message);
    }
}
