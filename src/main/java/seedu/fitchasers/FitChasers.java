package seedu.fitchasers;

import java.io.IOException;

public class FitChasers {
    /**
     * Main entry-point for the FitChasers application.
     */
    public static void main(String[] args) throws IOException {
        UI ui = new UI();
        WorkoutManager workoutManager = new WorkoutManager(ui);
        FileHandler fileHandler = new FileHandler(ui);

        // Load persistent data if any
        fileHandler.loadFileContentArray(workoutManager);

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

            switch (command) {
                case "/help":
                    ui.showHelp();
                    break;

                case "/add_weight":
                    ui.showMessage("Logging your weight... don’t lie to me!");
                    // Format: /add_weight w/WEIGHT d/DATE (e.g., /add_weight w/81.5 d/19/10/25)
                    // workoutManager.addWeight(argumentStr);
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
                    ui.showMessage("Deleting that workout? 😭 Are you sure, bestie?");
                    // Format: /del_workout WORKOUT_NAME
                    // workoutManager.deleteWorkout(argumentStr);
                    break;

                case "/exit":
                    fileHandler.saveFile(workoutManager.getWorkouts());
                    ui.showExitMessage();
                    isRunning = false;
                    break;

                default:
                    ui.showError("That’s not a thing, bestie. Try /help for the real moves!");
                    break;
            }
        }
    }
}
