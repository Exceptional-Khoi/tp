package seedu.fitchasers.user;

import seedu.fitchasers.FileHandler;
import seedu.fitchasers.ui.UI;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Handles the setting, saving, loading, and viewing of a user's goal weight.
 * <p>
 * This class allows the user to define a target goal weight, stores it with the date
 * it was set, and provides feedback comparing the goal with the current weight.
 * </p>
 * <p>
 * Data is persisted in a file named {@code goal.dat}, managed by {@link FileHandler}.
 * </p>
 */
public class GoalWeightTracker {

    /** Formatter for displaying dates in dd/MM/yy format. */
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yy");

    /** UI instance used to show messages to the user. */
    private final UI ui = new UI();

    /** The user's goal weight in kilograms. */
    private Double goalWeight = null;

    /** The date when the goal weight was set. */
    private LocalDate setDate = null;

    /**
     * Constructs a new {@code GoalWeightTracker} and loads any previously saved goal weight.
     */
    public GoalWeightTracker() {
        loadGoal();
    }

    /**
     * Handles user input for setting a new goal weight.
     * <p>
     * Example command: {@code /set_goal w/60}
     * </p>
     * <ul>
     *     <li>Validates that the input starts with {@code w/}.</li>
     *     <li>Ensures the weight is a positive number.</li>
     *     <li>Saves the goal and displays confirmation.</li>
     * </ul>
     *
     * @param input the user's raw input string (e.g., {@code "w/60"})
     */
    public void handleSetGoal(String input) {
        if (input == null || !input.startsWith("w/")) {
            ui.showMessage("Usage: /set_goal w/TARGET_WEIGHT (e.g., /set_goal w/60)");
            return;
        }

        String weightStr = input.substring(2).trim();
        double target;
        try {
            target = Double.parseDouble(weightStr);
            if (target <= 0) {
                ui.showMessage("Goal weight must be a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            ui.showMessage("Invalid weight. Please enter a number (e.g., 60 or 60.5).");
            return;
        }

        this.goalWeight = target;
        this.setDate = LocalDate.now();
        ui.showMessage(String.format(
                "Your new goal weight of %.1f kg has been set on %s.",
                goalWeight, setDate.format(DF)));

        saveGoal();
    }

    /**
     * Displays the user's goal weight and progress relative to their current weight.
     * <p>
     * If no goal has been set, the user is informed accordingly.
     * If there are no weight records yet, only the goal weight is shown.
     * </p>
     *
     * @param currentWeight the user's most recent recorded weight (maybe {@code null})
     */
    public void handleViewGoal(Double currentWeight) {
        if (goalWeight == null) {
            ui.showMessage("You have not set a goal weight yet.");
            return;
        }

        if (currentWeight == null || currentWeight < 0) {
            ui.showMessage("No weight records yet. Your goal is " + goalWeight + " kg.");
            return;
        }

        double diff = currentWeight - goalWeight;
        String status;
        if (diff > 0) {
            status = String.format("You're %.1f kg above your goal.", diff);
        } else if (diff < 0) {
            status = String.format("You're %.1f kg below your goal.", -diff);
        } else {
            status = "Congrats! You've reached your goal!";
        }

        ui.showMessage(String.format(
                "Goal Weight: %.1f kg (set on %s)\nCurrent Weight: %.1f kg\n%s",
                goalWeight, setDate.format(DF), currentWeight, status));
    }

    /**
     * Saves the goal weight and the date it was set to a persistent storage file.
     * <p>
     * If an error occurs during saving, an error message is displayed to the user.
     * </p>
     */
    private void saveGoal() {
        try {
            FileHandler fh = new FileHandler();
            fh.saveGoal(goalWeight, setDate);
        } catch (IOException e) {
            ui.showMessage("Failed to save goal weight: " + e.getMessage());
        }
    }

    /**
     * Loads the goal weight and date from the persistent storage file, if available.
     * <p>
     * If no goal data exists or an I/O error occurs, the tracker remains unset.
     * </p>
     */
    private void loadGoal() {
        try {
            FileHandler fh = new FileHandler();
            Double[] data = fh.loadGoal();
            if (data != null) {
                this.goalWeight = data[0];
                this.setDate = LocalDate.ofEpochDay(data[1].longValue());
            }
        } catch (IOException e) {
            ui.showMessage("No previous goal weight found.");
        }
    }
}
