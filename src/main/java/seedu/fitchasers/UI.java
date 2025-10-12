package seedu.fitchasers;

import java.util.Scanner;

public class UI {
    private final Scanner scanner;
    private final String YELLOW = "\u001B[33m";
    private final String RESET = "\u001B[0m";

    public UI() {
        this.scanner = new Scanner(System.in);
    }

    //1. Input: Read user commands
    public String readCommand() {
        System.out.print("> ");
        return scanner.nextLine().trim();
    }

    //2. Output: Display messages and command results
    public void showMessage(String message) {
        showDivider();
        System.out.println(message);
        showDivider();
    }

    public void showError(String error) {
        showDivider();
        System.out.println("[Oops!] " + error);
        showDivider();
    }

    //3. Greeting messages
    public void showGreeting() {
        // ANSI escape codes for color
        final String BLUE_BOLD = "\u001B[1;34m";
        final String CYAN = "\u001B[36m";
        final String GREEN = "\u001B[32m";

        showDivider();

        System.out.println(BLUE_BOLD +
                " _______  __  .___________.  ______  __    __       ___           _______. _______ .______      \n" +
                "|   ____||  | |           | /      ||  |  |  |     /   \\         /       ||   ____||   _  \\     \n" +
                "|  |__   |  | `---|  |----`|  ,----'|  |__|  |    /  ^  \\       |   (----`|  |__   |  |_)  |    \n" +
                "|   __|  |  |     |  |     |  |     |   __   |   /  /_\\  \\       \\   \\    |   __|  |      /     \n" +
                "|  |     |  |     |  |     |  `----.|  |  |  |  /  _____  \\  .----)   |   |  |____ |  |\\  \\----.\n" +
                "|__|     |__|     |__|      \\______||__|  |__| /__/     \\__\\ |_______/    |_______|| _| `._____|\n" +
                RESET);

        System.out.println(CYAN + "Your virtual gym buddy’s clocked in and ready to make you strong!" + RESET);
        System.out.println(YELLOW + "--------------------------------------------------" + RESET);

        System.out.println(CYAN + "Type " + YELLOW + "/help" + CYAN + " to explore all available commands!" + RESET);
        System.out.println(CYAN + "Let's crush your fitness goals together!" + RESET);

        showDivider();
    }

    public void showExitMessage() {
        System.out.println("Catch you next time, champ — don’t ghost your gains!");
    }

    //4. /help command
    public void showHelp() {
        showDivider();

        System.out.println("\nAvailable Commands:");
        System.out.println("/help                        - View all commands");
        System.out.println("/add_weight w/WEIGHT d/DATE  - Record your current weight");
        System.out.println("/create_workout n/NAME d/DATE t/TIME - Create new workout");
        System.out.println("/add_exercise n/NAME r/REPS  - Add exercise to workout");
        System.out.println("/end_workout d/DATE t/TIME   - End the workout session");
        System.out.println("/view_log                    - View workout history");
        System.out.println("/del_workout n/NAME          - Delete a workout");
        System.out.println("/exit                        - Exit the app");

        showDivider();
    }

    //5. User-friendly CLI interface
    public void showDivider() {
        System.out.println(YELLOW + "--------------------------------------------------" + RESET);
    }
}
