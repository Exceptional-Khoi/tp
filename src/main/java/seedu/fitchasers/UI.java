package seedu.fitchasers;

import java.util.Scanner;

/**
 * The {@code UI} class handles all user interactions for the FitChaser application.
 * It manages console input/output, provides formatted messages, and displays
 * helpful prompts to guide the user through commands.
 *
 * <p>This class focuses purely on user interface concerns — it does not perform
 * business logic or data management.</p>
 */
public class UI {
    private static final String RESET = "\u001B[0m";
    private static final String BLUE_BOLD = "\u001B[1;34m";
    private static final String CYAN = "\u001B[36m";
    private static final String GREEN = "\u001B[92m";
    private static final String MAGENTA = "\u001B[35m";
    private static final String WHITE_BOLD = "\u001B[1;37m";

    /** Scanner instance for reading user input from the console. */
    private final Scanner scanner;

    /**
     * Constructs a new {@code UI} object and initializes a {@link Scanner}
     * for reading user input.
     */
    public UI() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Prompts the user to enter a command and reads the input line.
     *
     * @return the trimmed command string entered by the user.
     */
    public String readCommand() {
        System.out.print(MAGENTA + "Enter command" + RESET + " > ");
        return scanner.nextLine().trim();
    }

    /**
     * Displays a standard informational message in green color,
     * followed by a divider for readability.
     *
     * @param message the message to display.
     */
    public void showMessage(String message) {
        System.out.println(GREEN + message + RESET);
        showDivider();
    }

    /**
     * Displays an error message prefixed with "[Oops!]" in magenta color,
     * followed by a divider.
     *
     * @param error the error message to display.
     */
    public void showError(String error) {
        System.out.println(MAGENTA + "[Oops!] " + RESET + error);
        showDivider();
    }

    /**
     * Displays a greeting message when the application starts.
     * It introduces the app and guides the user on how to get help.
     */
    public void showGreeting() {
        showDivider();

        System.out.println(BLUE_BOLD
                + "FITCHASER"
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

    /**
     * Displays a farewell message when the user exits the application.
     */
    public void showExitMessage() {
        System.out.println(CYAN
                + "Catch you next time, champ — don’t ghost your gains!" + RESET);
        showDivider();
    }

    /**
     * Displays all available commands and their usage.
     */
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

    /**
     * Displays a visual divider line to separate sections in the console output.
     */
    public void showDivider() {
        System.out.println(WHITE_BOLD
                + "--------------------------------------------------" + RESET);
    }
}
