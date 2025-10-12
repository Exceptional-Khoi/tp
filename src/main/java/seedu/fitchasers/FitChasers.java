package seedu.fitchasers;

import java.util.Scanner;

public class FitChasers {
    /**
     * Main entry-point for the java.duke.Duke application.
     */
    public static void main(String[] args) {
        UI ui = new UI();
        ui.showGreeting();

        boolean isRunning = true;
        while (isRunning) {
            String command = ui.readCommand();

            switch (command.toLowerCase()) {

                case "/help":
                    ui.showHelp();
                    break;

                case "/add_weight":
                    ui.showMessage("Logging your weight... don’t lie to me!");
                    // TODO: integrate weight-logging logic here
                    break;

                case "/create_workout":
                    ui.showMessage("New workout sesh incoming!");
                    // TODO: integrate workout creation logic
                    break;

                case "/add_exercise":
                    ui.showMessage("Adding that spicy new exercise!");
                    // TODO: integrate exercise addition logic
                    break;

                case "/end_workout":
                    ui.showMessage("Workout wrapped! Time to refuel!");
                    // TODO: integrate workout end logic
                    break;

                case "/view_log":
                    ui.showMessage("Here’s your workout glow-up history!");
                    // TODO: integrate log viewing logic
                    break;

                case "/del_workout":
                    ui.showMessage("Deleting that workout? T.T Are you sure, bestie?");
                    // TODO: integrate workout deletion logic
                    break;

                case "/exit":
                    ui.showExitMessage();
                    isRunning = false;
                    break;

                default:
                    ui.showError("That’s not a thing, bestie. Try /help for the real moves!");
            }
        }
    }
}
