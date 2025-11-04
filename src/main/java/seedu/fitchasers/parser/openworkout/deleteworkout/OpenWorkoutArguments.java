package seedu.fitchasers.parser.openworkout.deleteworkout;

import java.time.YearMonth;

/**
 * Parsed args for /delete_workout
 */
public record OpenWorkoutArguments(int indexToOpen, YearMonth yearMonth) {

}
