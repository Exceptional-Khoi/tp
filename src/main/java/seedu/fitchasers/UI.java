package seedu.fitchasers;

import java.util.Scanner;

/**
 * The {@code UI} class handles all user interactions for the FitChaser application.
 * It manages console input/output, provides formatted messages, and displays
 * helpful prompts to guide the user through commands.
 *
 */
public class UI {
    private static final String RESET = "\u001B[0m";
    private static final String BLUE_BOLD = "\u001B[1;34m";
    private static final String CYAN = "\u001B[36m";
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
        System.out.println(WHITE_BOLD + message + RESET);
    }

    /**
     * Displays an error message prefixed with "[Oops!]" in magenta color,
     * followed by a divider.
     *
     * @param error the error message to display.
     */
    public void showError(String error) {
        System.out.println(MAGENTA + "[Oops!] " + RESET + error);
    }

    /**
     * Displays a greeting message when the application starts.
     * It introduces the app and guides the user on how to get help.
     */
    public void showGreeting() {

        System.out.println(BLUE_BOLD + """
        +------------------------------------------------------+
        |                      FITCHASER                       |
        +------------------------------------------------------+
        """ + RESET);

        System.out.println(CYAN
                + "Your virtual gym buddy’s clocked in and ready to make you strong!"
                + RESET);

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
        System.out.println("/help                                - View all commands");
        System.out.println("/add_weight w/WEIGHT d/DATE          - Record your current weight (e.g. /add_weight w/81.5 d/19/10/25)");
        System.out.println("/create_workout n/NAME d/DATE t/TIME - Create a new workout session (e.g. /create_workout n/PushDay d/20/10/25 t/1900)");
        System.out.println("/add_exercise n/NAME r/REPS          - Add an exercise to the current workout (e.g. /add_exercise n/Squat r/12)");
        System.out.println("/add_set r/REPS                      - Add a new set to the current exercise (e.g. /add_set r/10)");
        System.out.println("/end_workout d/DATE t/TIME           - End the current workout (e.g. /end_workout d/20/10/25 t/2030)");
        System.out.println("/view_log                            - View your workout history");
        System.out.println("/del_workout NAME                    - Delete a workout by name (e.g. /del_workout PushDay)");
        System.out.println("/exit                                - Save progress and exit the app");

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
