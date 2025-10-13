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
                    //workoutManager.addWeight(argumentStr);
                    break;

                case "/create_workout":
                    ui.showMessage("New workout sesh incoming!");
                    workoutManager.addWorkout(argumentStr);
                    break;

                case "/add_exercise":
                    ui.showMessage("Adding that spicy new exercise!");
                    workoutManager.addExercise(argumentStr);
                    break;

                case "/end_workout":
                    ui.showMessage("Workout wrapped! Time to refuel!");
                    workoutManager.endWorkout(argumentStr);
                    break;

                case "/view_log":
                    ui.showMessage("Here’s your workout glow-up history!");
                    workoutManager.viewWorkouts();
                    break;

                case "/del_workout":
                    ui.showMessage("Deleting that workout? 😭 Are you sure, bestie?");
                    //workoutManager.deleteWorkout(argumentStr);
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
