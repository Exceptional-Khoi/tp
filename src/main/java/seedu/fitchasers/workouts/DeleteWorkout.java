package seedu.fitchasers.workouts;

import seedu.fitchasers.exceptions.FileNonexistent;
import seedu.fitchasers.exceptions.InvalidArgumentInput;
import seedu.fitchasers.parser.deleteworkout.DeleteParser;
import seedu.fitchasers.parser.deleteworkout.DeleteWorkoutArguments;
import seedu.fitchasers.storage.FileHandler;
import seedu.fitchasers.ui.UI;

import java.io.IOException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;

//@@author Exceptional-Khoi
/**
 * Handles the deletion of workouts from the user's workout log.
 * <p>
 * This class coordinates between the {@code UI}, {@code FileHandler}, and {@code WorkoutManager}
 * to parse deletion commands, confirm user actions, update files, and synchronize in-memory data.
 */
public class DeleteWorkout {
    private final UI ui;
    private final FileHandler fileHandler;
    private final WorkoutManager workoutManager;
    private final YearMonth currentLoadedMonth; // set this from your app when month context changes

    /**
     * Constructs a {@code DeleteWorkout} instance with the specified dependencies.
     *
     * @param ui The user interface for displaying messages and confirmations.
     * @param fileHandler The file handler responsible for loading and saving workout data.
     * @param workoutManager The workout manager that maintains in-memory workout lists.
     */
    public DeleteWorkout(UI ui, FileHandler fileHandler, WorkoutManager workoutManager) {
        this.ui = ui;
        this.fileHandler = fileHandler;
        this.workoutManager = workoutManager;
        this.currentLoadedMonth = workoutManager.getCurrentLoadedMonth(); // or inject/set later
    }

    /**
     * Executes the {@code /delete_workout} command.
     * <p>
     * Parses the user's input, loads workouts for the specified month,
     * displays details of the selected workout, requests confirmation,
     * performs deletion, saves the updated list, and refreshes in-memory data.
     *
     * @param args The raw command arguments provided by the user.
     * @throws InvalidArgumentInput If the arguments are invalid or incorrectly formatted.
     * @throws IOException If an I/O error occurs while accessing workout files.
     * @throws FileNonexistent If the workout file for the specified month does not exist.
     */
    public void execute(String args) throws InvalidArgumentInput, IOException, FileNonexistent {
        //format and package the Arguments nicely into DeleteWorkoutArguments
        DeleteWorkoutArguments parsedArgumentsForDelete =
                new DeleteParser().parse(args, workoutManager.getCurrentLoadedMonth());

        ArrayList<Workout> monthWorkouts =
                new ArrayList<>(fileHandler.loadMonthList(parsedArgumentsForDelete.yearMonth()));

        if (monthWorkouts.isEmpty()) {
            ui.showMessage("No workouts found for " + parsedArgumentsForDelete.yearMonth() + ".");
            ui.showMessage("Use /view_log to check available months.");
            return;
        }

        // If /view_log sorts, mirror the same sorting BEFORE using display index:
        monthWorkouts.sort(
                Comparator.comparing(
                        Workout::getWorkoutStartDateTime,
                        Comparator.nullsLast(Comparator.reverseOrder())   // start: desc, nulls last
                ).thenComparing(
                        Workout::getWorkoutEndDateTime,
                        Comparator.nullsLast(Comparator.reverseOrder())   // end: desc, nulls last
                )
        );  // ← NO .reversed()

        int displayIndex = parsedArgumentsForDelete.indexToDelete();
        if (displayIndex < 1 || displayIndex > monthWorkouts.size()) {
            ui.showMessage("Invalid workout ID: " + displayIndex);
            ui.showMessage("Please use a valid ID between 1 and " + monthWorkouts.size());
            ui.showMessage("Tip: /view_log m/" + parsedArgumentsForDelete.yearMonth().getMonthValue()
                    + " to see the list.");
            return;
        }

        Workout toDelete = monthWorkouts.get(displayIndex - 1);

        ui.showMessage("You are about to delete:");
        ui.displayDetailsOfWorkout(toDelete);
        ui.showMessage("Are you sure? (y/n)");
        boolean confirmed = ui.confirmationMessage();
        if (!confirmed) {
            ui.showMessage("Deletion cancelled.");
            return;
        }

        String deletedName = toDelete.getWorkoutName();
        monthWorkouts.remove(displayIndex - 1);

        // Save back (persist the same ordering you used)
        fileHandler.saveMonthList(parsedArgumentsForDelete.yearMonth(), monthWorkouts);

        // Update in-memory if this month is the active one
        if (parsedArgumentsForDelete.yearMonth().equals(currentLoadedMonth)) {
            workoutManager.setWorkouts(monthWorkouts, parsedArgumentsForDelete.yearMonth());
        }

        ui.showMessage("✓ Deleted workout: " + deletedName);
    }
}
