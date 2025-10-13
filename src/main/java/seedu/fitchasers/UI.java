package seedu.fitchasers;

import java.util.Scanner;

public class UI {

    // =============================
    // 1. Static constants (color codes)
    // =============================
    private static final String RESET = "\u001B[0m";
    private static final String BLUE_BOLD = "\u001B[1;34m";
    private static final String CYAN = "\u001B[36m";
    private static final String GREEN = "\u001B[92m";
    private static final String MAGENTA = "\u001B[35m";
    private static final String WHITE_BOLD = "\u001B[1;37m";

    // =============================
    // 2. Instance variables
    // =============================
    private final Scanner scanner;

    // =============================
    // 3. Constructor
    // =============================
    public UI() {
        this.scanner = new Scanner(System.in);
    }

    // =============================
    // 4. Input Handling
    // =============================
    public String readCommand() {
        System.out.print(MAGENTA + "Enter command" + RESET + " > ");
        return scanner.nextLine().trim();
    }

    // =============================
    // 5. Output Methods
    // =============================
    public void showMessage(String message) {
        System.out.println(GREEN + message + RESET);
        showDivider();
    }

    public void showError(String error) {
        System.out.println(MAGENTA + "[Oops!] " + RESET + error);
        showDivider();
    }

    // =============================
    // 6. Greeting Messages
    // =============================
    public void showGreeting() {
        showDivider();

        System.out.println(BLUE_BOLD
                + " _______  __  .___________.  ______  __    __       ___           _______. _______ .______      \n"
                + "|   ____||  | |           | /      ||  |  |  |     /   \\         /       ||   ____||   _  \\     \n"
                + "|  |__   |  | `---|  |----`|  ,----'|  |__|  |    /  ^  \\       |   (----`|  |__   |  |_)  |    \n"
                + "|   __|  |  |     |  |     |  |     |   __   |   /  /_\\  \\       \\   \\    |   __|  |      /     \n"
                + "|  |     |  |     |  |     |  `----.|  |  |  |  /  _____  \\  .----)   |   |  |____ |  |\\  \\----.\n"
                + "|__|     |__|     |__|      \\______||__|  |__| /__/     \\__\\ |_______/    |_______|| _| `._____|\n"
                + RESET);

        System.out.println(CYAN
                + "Your virtual gym buddy’s clocked in and ready to make you strong!"
                + RESET);

        showDivider();

        System.out.println(CYAN + "Type " + WHITE_BOLD + "/help" + CYAN
                + " to explore all available commands!" + RESET);
        System.out.println(CYAN + "Let's crush your fitness goals together!" + RESET);

        showDivider();
    }

    public void showExitMessage() {
        System.out.println(CYAN
                + "Catch you next time, champ — don’t ghost your gains!" + RESET);
        showDivider();
    }

    // =============================
    // 7. Help Command
    // =============================
    public void showHelp() {
        System.out.println("\n" + CYAN + "Available Commands:" + RESET);
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

    // =============================
    // 8. UI Utilities
    // =============================
    public void showDivider() {
        System.out.println(WHITE_BOLD
                + "--------------------------------------------------" + RESET);
    }
}
