package seedu.fitchasers;

import java.io.IOException;
import java.util.Scanner;


/**
 * Main entry point for the FitChasers application.
 *
 * Handles user input commands, delegates operations to WorkoutManager,
 * and persists data through FileHandler.
 */
public class FitChasers {
    /**
     * Starts the FitChasers program.
     * Initializes all components, loads saved data if available,
     * and processes user input until the user exits.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        UI ui = new UI();
        WorkoutManager workoutManager = new WorkoutManager();
        FileHandler fileHandler = new FileHandler();
        Person person = new Person("Nary");
        WeightManager weightManager = new WeightManager(person);
        Scanner scanner = new Scanner(System.in);

        // Attempt to load persistent data
        try {
            fileHandler.loadFileContentArray(workoutManager, person);
        } catch (IOException e) {
            ui.showError("Could not load saved data. Starting fresh!");
        }

        ui.showGreeting();

        boolean isRunning = true;

        while (isRunning) {
            String input = ui.readCommand();
            if (input == null) {
                break;
            }
            if (input.trim().isEmpty()) {
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

                case "/my_name": {
                    if (argumentStr == null || !argumentStr.startsWith("n/")) {
                        ui.showMessage("Usage: /my_name n/YourName");
                        ui.showDivider();
                        break;
                    }
                    String newName = argumentStr.substring(2).trim();
                    if (newName.isEmpty()) {
                        ui.showMessage("Usage: /my_name n/YourName");
                        ui.showDivider();
                        break;
                    }
                    person.setName(newName);
                    ui.showMessage("Alright, I'll call you " + newName + " from now on.");
                    ui.showDivider();
                    break;
                }

                case "/add_weight":
                    ui.showMessage("Logging your weight... don't lie to me!");
                    weightManager.addWeight(argumentStr);
                    // Format: /add_weight w/WEIGHT d/DATE
                    ui.showDivider();
                    break;

                case "/view_weight":
                    ui.showMessage("Here's your weight, you've been killin' it lately!");
                    weightManager.viewWeights();
                    ui.showDivider();
                    break;

                case "/create_workout":
                    ui.showMessage("New workout sesh incoming!");
                    // Format: /create_workout n/NAME d/DD/MM/YY t/HHmm
                    workoutManager.addWorkout(argumentStr);
                    ui.showDivider();
                    break;

                case "/add_exercise":
                    ui.showMessage("Adding that spicy new exercise!");
                    // Format: /add_exercise n/NAME r/REPS
                    workoutManager.addExercise(argumentStr);
                    ui.showDivider();
                    break;

                case "/add_set":
                    ui.showMessage("Adding a new set to your exercise!");
                    // Format: /add_set r/REPS
                    workoutManager.addSet(argumentStr);
                    ui.showDivider();
                    break;

                case "/end_workout":
                    ui.showMessage("Workout wrapped! Time to refuel!");
                    // Format: /end_workout d/DD/MM/YY t/HHmm
                    workoutManager.endWorkout(scanner, argumentStr);
                    ui.showDivider();
                    break;

                case "/view_log":
                    ui.showMessage("Here's your workout glow-up history!");
                    workoutManager.viewWorkouts();
                    ui.showDivider();
                    break;

                case "/del_workout":
                    ui.showMessage("Deleting that workout? T.T Are you sure, bestie?");
                    // Format: /del_workout WORKOUT_NAME
                    workoutManager.deleteWorkout(argumentStr);
                    ui.showDivider();
                    break;

                case "/exit":
                    ui.showMessage("Saving your progress...");
                    try {
                        fileHandler.saveFile(person, workoutManager.getWorkouts());
                        ui.showExitMessage();
                    } catch (IOException e) {
                        ui.showError("Failed to save workouts before exit.");
                    }
                    isRunning = false;
                    break;

                default:
                    ui.showError("That's not a thing, bestie. Try /help for the real moves!");
                    ui.showDivider();
                    break;
                }
            } catch (Exception e) {
                ui.showError("Something went wrong: " + e.getMessage());
                ui.showDivider();
            }
        }
    }
}
