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

public class DeleteWorkout {
    private final UI ui;
    private final FileHandler fileHandler;
    private final WorkoutManager workoutManager;
    private YearMonth currentLoadedMonth; // set this from your app when month context changes

    public DeleteWorkout(UI ui, FileHandler fileHandler, WorkoutManager workoutManager) {
        this.ui = ui;
        this.fileHandler = fileHandler;
        this.workoutManager = workoutManager;
        this.currentLoadedMonth = workoutManager.getCurrentLoadedMonth(); // or inject/set later
    }

    /**
     * Entry point for /delete_workout ... args.
     * Parses, loads the month list, shows details, confirms, deletes, saves.
     */
    public void execute(String args) throws InvalidArgumentInput, IOException, FileNonexistent {
        //format and package the Arguments nicely into DeleteWorkoutArguments
        DeleteWorkoutArguments parsedArgumentsForDelete = new DeleteParser().parse(args);

        // IMPORTANT: the ID must match how your /view_log displayed the list.
        // If /view_log sorts by endDateTime desc, sort here the same way before indexing.
        ArrayList<Workout> monthWorkouts =
                new ArrayList<>(fileHandler.loadMonthList(parsedArgumentsForDelete.yearMonth()));

        if (monthWorkouts.isEmpty()) {
            ui.showMessage("No workouts found for " + parsedArgumentsForDelete.yearMonth() + ".");
            ui.showMessage("Use /view_log to check available months.");
            return;
        }

        // If /view_log sorts, mirror the same sorting BEFORE using display index:
        monthWorkouts.sort(Comparator.comparing(
                Workout::getWorkoutEndDateTime,
                Comparator.nullsLast(Comparator.naturalOrder())
        ).thenComparing(
                Workout::getWorkoutStartDateTime,
                Comparator.nullsLast(Comparator.naturalOrder())
        ));  // ← NO .reversed()

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

    public void setCurrentLoadedMonth(YearMonth ym) {
        this.currentLoadedMonth = ym;
    }
}
