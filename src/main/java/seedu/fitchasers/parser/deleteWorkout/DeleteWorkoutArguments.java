package seedu.fitchasers.parser.deleteWorkout;
import java.time.YearMonth;

/** Parsed args for /delete_workout */
public record DeleteWorkoutArguments(int indexToDelete, YearMonth yearMonth) {
}