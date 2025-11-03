package seedu.fitchasers.parser.deleteworkout;

import java.time.YearMonth;

/**
 * Parsed args for /delete_workout
 */
public record DeleteWorkoutArguments(int indexToDelete, YearMonth yearMonth) {

}
