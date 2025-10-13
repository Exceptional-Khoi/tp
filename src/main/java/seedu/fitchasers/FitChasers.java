package seedu.fitchasers;

import java.io.IOException;

/**
 * Main entry point for the FitChasers application.
 *
 * Handles user input commands, delegates operations to WorkoutManager,
 * and persists data through FileHandler.
 */
public class FitChasers {

    public static void main(String[] args) {
        UI ui = new UI();
        WorkoutManager workoutManager = new WorkoutManager(ui);
        FileHandler fileHandler = new FileHandler(ui);

        // Attempt to load persistent data
        try {
            fileHandler.loadFileContentArray(workoutManager);
        } catch (IOException e) {
            ui.showError("Could not load saved data. Starting fresh!");
        }

        ui.showGreeting();

        boolean isRunning = true;

        while (isRunning) {
            String input = ui.readCommand();
            if (input == null || input.trim().isEmpty()) {
                continue;
            }

            String[] parts = input.trim().split("\\s+", 2);
            String command = parts[0].toLowerCase();
            String argumentStr = (parts.length > 1) ? parts[1] : "";

            try {
                switch (command) {
                    case "/help":
                        ui.showHelp();
                        break;

                    case "/add_weight":
                        ui.showMessage("Logging your weight... don’t lie to me!");
                        // Format: /add_weight w/WEIGHT d/DATE
                        // Example: /add_weight w/81.5 d/19/10/25
                        // (Feature placeholder for future implementation)
                        break;

                    case "/create_workout":
                        ui.showMessage("New workout sesh incoming!");
                        // Format: /create_workout n/NAME d/DD/MM/YY t/HHmm
                        workoutManager.addWorkout(argumentStr);
                        break;

                    case "/add_exercise":
                        ui.showMessage("Adding that spicy new exercise!");
                        // Format: /add_exercise n/NAME r/REPS
                        workoutManager.addExercise(argumentStr);
                        break;

                    case "/add_set":
                        ui.showMessage("Adding a new set to your exercise!");
                        // Format: /add_set r/REPS
                        workoutManager.addSet(argumentStr);
                        break;

                    case "/end_workout":
                        ui.showMessage("Workout wrapped! Time to refuel!");
                        // Format: /end_workout d/DD/MM/YY t/HHmm
                        workoutManager.endWorkout(argumentStr);
                        break;

                    case "/view_log":
                        ui.showMessage("Here’s your workout glow-up history!");
                        workoutManager.viewWorkouts();
                        break;

                    case "/del_workout":
                        ui.showMessage("🗑 Deleting that workout? 😭 Are you sure, bestie?");
                        // Format: /del_workout WORKOUT_NAME
                        // (Feature placeholder — can hook into workoutManager.removeWorkout())
                        break;

                    case "/exit":
                        ui.showMessage("Saving your progress...");
                        try {
                            fileHandler.saveFile(workoutManager.getWorkouts());
                            ui.showExitMessage();
                        } catch (IOException e) {
                            ui.showError("Failed to save workouts before exit.");
                        }
                        isRunning = false;
                        break;

                    default:
                        ui.showError("That’s not a thing, bestie. Try /help for the real moves!");
                        break;
                }
            } catch (Exception e) {
                ui.showError("Something went wrong: " + e.getMessage());
            }
        }
    }
}
