package seedu.fitchasers.ui;

import seedu.fitchasers.workouts.Exercise;
import seedu.fitchasers.workouts.Workout;
import seedu.fitchasers.user.WeightManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

//@@Exceptional-Khoi
/**
 * Handles all user interactions for the FitChasers application.
 * Provides an enhanced text-based interface with chat-bubble visuals
 * for improved readability and user experience.
 */
public class UI {
    private static final String RESET = "\u001B[0m";
    private static final String LIGHT_YELLOW = "\u001B[38;5;187m";
    private static final int CONSOLE_WIDTH = 150;
    private static final String BOT_HEADER = "{^o^} FitChasers";
    private static final String BOLD_WHITE = "\u001B[1;97m";
    private static final String BOLD_RESET = "\u001B[0m";
    private static final String BOLD_BRIGHT_PURPLE = "\u001B[1;38;5;183m";
    private static final int PADDING = 2;
    private static final int FRAME_OVERHEAD = 6;
    private final Scanner scanner;

    /**
     * Constructs a {@code UI} instance and initializes the input scanner.
     */
    public UI() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Prints the header of the left chat bubble.
     */
    public void printLeftHeader() {
        System.out.println(LIGHT_YELLOW + BOT_HEADER + RESET);
    }

    /**
     * Reads a general command input from the user.
     *
     * @return the command entered by the user
     */
    public String readCommand() {
        return readInsideRightBubble("Enter command > ");
    }

    /**
     * Prompts the user to enter one or more indices for deletion.
     *
     * @return the trimmed input string containing indices,
     *         or {@code null} if no input is provided
     */
    public String enterSelection() {
        String s = readInsideRightBubble("Enter the index(es) to be deleted > ");
        if (s == null || s.isEmpty()) {
            showError("No input detected.");
            return null;
        }
        return s.trim();
    }

    /**
     * Prompts the user to enter their name.
     * Ensures that the name is not empty.
     *
     * @return the trimmed username, or {@code null} if input is terminated
     */
    public String enterName() {
        while (true) {
            String name = readInsideRightBubble("Enter your name > ");
            if (name == null) {
                return null;
            }
            name = name.trim();

            if (name.isEmpty()) {
                showError("Name cannot be empty. Please try again!");
                continue;
            }

            if (name.length() > 30) {
                showError("Name is too long. Maximum is 30 characters.");
                continue;
            }

            if (!name.matches("^[a-zA-Z0-9 _-]+$")) {
                showError("Name can only contain letters, numbers, spaces, underscores (_), or dashes (-).");
                continue;
            }

            return name;
        }
    }

    /**
     * Prompts the user to enter their initial weight in kilograms.
     * Validates numeric input greater than zero.
     *
     * @return the user's initial weight as a double, or -1 if input fails
     */
    public double enterWeight(WeightManager weightManager) {
        double weight = -1;
        while (true) {
            showMessage("Please enter your initial weight (in kg).");
            String ans = readInsideRightBubble("Enter your weight > ");
            if (ans == null) {
                showError("No input detected. Exiting weight entry.");
                return -1;
            }

            String input = ans.trim();
            try {
                weight = Double.parseDouble(input);
                if (weightManager.isValidWeight(weight)) {
                    return weight;
                }
            } catch (NumberFormatException e) {
                showError("Invalid number. Please enter a valid weight (e.g., 60.5).");
            }
        }
    }

    /**
     * Displays a standard message inside a left-aligned chat bubble.
     *
     * @param message the message text to display
     */
    public void showMessage(String message) {
        assert message != null : "Message cannot be null";
        System.out.println(leftBubble(message));
    }

    /**
     * Displays an error message inside a left-aligned bubble.
     *
     * @param error the error text to display
     */
    public void showError(String error) {
        assert error != null : "Error message cannot be null";
        System.out.println(leftBubble("[Oops!] " + error));
    }

    /**
     * Displays the startup greeting and introduction message.
     */
    public void showGreeting() {
        String[] purpleShades = {
            "\u001B[38;5;93m",
            "\u001B[38;5;129m",
            "\u001B[38;5;135m",
            "\u001B[38;5;141m",
            "\u001B[38;5;147m"
        };
        System.out.println(purpleShades[0] +
                " ▄▄▄▄▄▄   ▀      ▄      ▄▄▄  █                                       " + RESET);
        System.out.println(purpleShades[1] +
                " █      ▄▄▄    ▄▄█▄▄  ▄▀   ▀ █ ▄▄    ▄▄▄    ▄▄▄    ▄▄▄    ▄ ▄▄   ▄▄▄ " + RESET);
        System.out.println(purpleShades[2] +
                " █▄▄▄▄▄   █      █    █      █▀  █  ▀   █  █   ▀  █▀  █   █▀  ▀ █   ▀" + RESET);
        System.out.println(purpleShades[3] +
                " █        █      █    █      █   █  ▄▀▀▀█   ▀▀▀▄  █▀▀▀▀   █      ▀▀▀▄" + RESET);
        System.out.println(purpleShades[4] +
                " █      ▄▄█▄▄    ▀▄▄   ▀▄▄▄▀ █   █  ▀▄▄▀█  ▀▄▄▄▀  ▀█▄▄▀   █     ▀▄▄▄▀\n" + RESET);

        showMessage("""
          Your virtual gym buddy's clocked in and ready to make you strong!
          Type /help or h to explore all available commands!
          Let's crush your fitness goals together!""");
    }

    /**
     * Shows a one-page quick-start tutorial for first-time users.
     * Guides the user to create a workout, add an exercise, end it, and view the log.
     */
    public void showQuickStartTutorial() {
        showMessage("""
                ====================  FITCHASERS • QUICK START  ====================
                
                Let's get your very first workout into the log in under a minute!
                
                1) Create your first workout
                   Type:
                     /create_workout n/My First Workout!
                   Press Enter.
                   When prompted about date and time, type:
                     Y
                     Y
                   (This accepts today's date and the current time.)
                
                2) Add an exercise to this workout
                   Type:
                     /add_exercise n/First Exercise Eva r/12
                   Press Enter.
                
                3) End and save the workout
                   Type:
                     /end_workout
                   Press Enter.
                   When prompted about date and time, type:
                     Y
                     Y
                   (Accept current date/time again.)
                
                4) View your workout history
                   Type:
                     /view_log
                   Press Enter to see the workout you just created!
                
                --------------------------------------------------------------------
                Tips:
                • You can also use short aliases: /create_workout (cw), /add_exercise (ae),
                  /end_workout (ew), /view_log (vl)
                • Need a full command reference? Type /help
                ====================================================================
                """);
    }
    /**
     * Displays the exit message upon program termination.
     */
    public void showExitMessage() {
        showMessage("Catch you next time, champ — don't ghost your gains!");
    }

    /**
     * Displays the help message listing all available commands.
     */
    public void showHelp() {
        showMessage("""
        /help (h)                                 - View all available commands
        
        ~~~ USER PROFILE ~~~
        /rename (rn) n/NAME                       - Set or change your display name
                                                   e.g. /rename n/Nitin
        
        ~~~ WEIGHT TRACKING ~~~
        /add_weight (aw) w/WEIGHT d/DATE          - Record your weight
                                                   e.g. /add_weight w/75 d/30/10/25
        /view_weight (vw)                         - View your recorded weights
        /set_goal (sg) g/GOAL_WEIGHT              - Set a goal weight to track progress
                                                   e.g. /set_goal g/70.0
        /view_goal (vg)                           - View your current goal and progress
        
        ~~~ WORKOUT CREATION & LOGGING ~~~
        /create_workout (cw) n/NAME d/DATE t/TIME - Create a new workout
                                                   e.g. /create_workout n/Chest Day d/30/10/25 t/1430
        /add_exercise (ae) n/NAME r/REPS          - Add an exercise to current workout
                                                   e.g. /add_exercise n/Squat r/12
        /add_set (as) r/REPS                      - Add another set to the latest exercise
                                                   e.g. /add_set r/10
        /end_workout (ew) d/DATE t/TIME           - End and save current workout
                                                   e.g. /end_workout d/30/10/25 t/1500
        
        ~~~ WORKOUT LOG MANAGEMENT ~~~
        /view_log (vl)                            - View your workout history
        /open (o) INDEX                           - Open detailed view of a workout
                                                   e.g. /open 1
        /delete_workout (dw) id/INDEX             - Delete a workout index
                                                   e.g. /delete_workout id/8
        
        ~~~ TAGGING SYSTEM ~~~
        /add_modality_tag (amot) m/MODALITY k/KEYWORD
                                                   - Add a keyword for a workout modality
                                                   e.g. /add_modality_tag m/CARDIO k/running
        /add_muscle_tag (amt) m/MUSCLE_GROUP k/KEYWORD
                                                   - Add a keyword for a muscle group
                                                   e.g. /add_muscle_tag m/LEGS k/lunges
        /override_workout_tag (owt) id/INDEX newTag/TAG_NAME
                                                   - Manually override a workout’s tag
                                                   e.g. /override_workout_tag id/1 newTag/strength
        
        ~~~ GYM FINDER ~~~
        /gym_where (gw) n/EXERCISE                - Suggest NUS gyms with equipment for the exercise
                                                   e.g. /gym_where n/squat
        /gym_page (gp) p/PAGE_OR_NAME             - View available NUS gym pages or by gym name
                                                   e.g. /gym_page p/1
                                                   e.g. /gym_page p/SRC Gym
        
        ~~~ SYSTEM ~~~
        /exit (e)                                 - Save all progress and exit the app
            """);
    }

    /**
     * Prompts for a yes/no confirmation from the user.
     * Users can also type "/cancel" to abort the operation.
     *
     * @return {@code true} if confirmed (Y/Yes),
     *         {@code false} if declined (N/No),
     *         {@code null} if cancelled
     */
    public Boolean confirmationMessage() {
        while (true) {
            String ans = readInsideRightBubble("Confirm (Y/N or /cancel) > ");
            if (ans == null) {
                return false;
            }
            String lower = ans.trim().toLowerCase();

            if (lower.equals("y") || lower.equals("yes")) {
                return true;
            }
            if (lower.equals("n") || lower.equals("no")) {
                return false;
            }
            if (lower.equals("/cancel")) {
                showMessage("Action cancelled.");
                return null;
            }
            showError("Please answer Y or N (yes/no), or type /cancel to abort.");
        }
    }

    /**
     * Displays detailed information about a given workout.
     *
     * @param workout the workout instance to display
     */
    public void displayDetailsOfWorkout(Workout workout) {
        if (workout == null) {
            showError("No workout found to display.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Here you go bestie! These are the workout details!\n\n");
        sb.append(String.format("Name       : %s%n", workout.getWorkoutName()));
        sb.append(String.format("Date       : %s%n", workout.getWorkoutDateString()));

        int totalMinutes = workout.getDuration();
        assert totalMinutes >= 0 : "Workout duration must not be negative";
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        String durationStr = (hours > 0)
                ? String.format("%dh %dm", hours, minutes)
                : String.format("%dm", minutes);
        sb.append(String.format("Duration   : %s%n", durationStr));

        if (workout.getWorkoutStartDateTime() != null && workout.getWorkoutEndDateTime() != null) {
            sb.append(String.format("Start Time : %s%n", workout.getWorkoutStartDateTime()));
            sb.append(String.format("End Time   : %s%n", workout.getWorkoutEndDateTime()));
        }

        Set<String> tagsToDisplay = workout.getAllTags();
        if (tagsToDisplay == null || tagsToDisplay.isEmpty()) {
            sb.append("Tags       : -\n");
        } else {
            sb.append("Tags       : ").append(String.join(", ", tagsToDisplay)).append("\n");
        }

        var exercises = workout.getExercises();
        if (exercises == null || exercises.isEmpty()) {
            sb.append("\nExercises  : (none added)\n");
        } else {
            sb.append("\nExercises:\n");
            int i = 1;
            for (Exercise e : exercises) {
                sb.append(String.format("  %d. %s%n", i++, e.toString()));
            }
        }
        showMessage(sb.toString().trim());
    }

    /**
     * Returns the ordinal suffix for a given day.
     *
     * @param day the day number (1–31)
     * @return the suffix ("st", "nd", "rd", or "th")
     */
    public String getDaySuffix(int day) {
        assert day >= 1 && day <= 31 : "Day should be between 1 and 31";
        if (day >= 11 && day <= 13) {
            return "th";
        }
        return switch (day % 10) {
        case 1 -> "st";
        case 2 -> "nd";
        case 3 -> "rd";
        default -> "th";
        };
    }

    private static String stripAnsi(String input) {
        return input == null ? "" : input.replaceAll("\u001B\\[[;\\d]*m", "");
    }

    private static List<String> wrapLine(String s, int maxWidth) {
        List<String> out = new ArrayList<>();
        if (s == null) {
            s = "";
        }
        if (maxWidth <= 0) {
            out.add(s);
            return out;
        }
        int i = 0;
        while (i < s.length()) {
            int end = Math.min(i + maxWidth, s.length());
            out.add(s.substring(i, end));
            i = end;
        }
        if (s.isEmpty()) {
            out.add("");
        }
        return out;
    }

    private static int clampNonNeg(int v) {
        return Math.max(0, v);
    }

    private String leftBubble(String message) {
        String[] rawLines = stripAnsi(message).split("\\R", -1);
        List<String> lines = new ArrayList<>();
        int contentMax = Math.max(1, CONSOLE_WIDTH - FRAME_OVERHEAD - PADDING * 2);
        for (String raw : rawLines) {
            lines.addAll(wrapLine(raw, contentMax));
        }
        int innerWidth = 0;
        for (String l : lines) {
            innerWidth = Math.max(innerWidth, l.length() + PADDING * 2);
        }
        innerWidth = Math.min(innerWidth, Math.max(1, CONSOLE_WIDTH - FRAME_OVERHEAD));
        String top = "+" + "-".repeat(clampNonNeg(innerWidth)) + "+";
        String bottom = "+" + "-".repeat(clampNonNeg(innerWidth)) + "+";
        StringBuilder sb = new StringBuilder();
        sb.append(top).append("\n");
        for (String l : lines) {
            int spaces = clampNonNeg(innerWidth - l.length() - PADDING);
            sb.append("|")
                    .append(" ".repeat(PADDING))
                    .append(BOLD_WHITE).append(l).append(BOLD_RESET)
                    .append(" ".repeat(spaces))
                    .append("|\n");
        }
        sb.append(bottom);
        return sb.toString();
    }

    public String readInsideRightBubble(String prompt) {
        int innerWidth = Math.max(1, (int) (CONSOLE_WIDTH * 3.0 / 5) - FRAME_OVERHEAD);
        int pad = clampNonNeg(CONSOLE_WIDTH - innerWidth - 6);

        String top = BOLD_BRIGHT_PURPLE + "+" + "-".repeat(innerWidth) + "+" + RESET;
        String bottom = BOLD_BRIGHT_PURPLE + "+" + "-".repeat(innerWidth) + "+" + RESET;
        String leftPrefix = " ".repeat(pad) + BOLD_BRIGHT_PURPLE +
                "|" + RESET + BOLD_BRIGHT_PURPLE + " ".repeat(PADDING) + RESET;
        System.out.println(" ".repeat(pad) + LIGHT_YELLOW + "(You)" + RESET);
        System.out.println(" ".repeat(pad) + top);
        System.out.print(leftPrefix + BOLD_BRIGHT_PURPLE + prompt + BOLD_RESET);
        System.out.flush();
        if (!scanner.hasNextLine()) {
            System.out.println();
            System.out.println(" ".repeat(pad) + bottom);
            return null;
        }
        String input = scanner.nextLine();
        String trimmed = input.trim();
        System.out.println(" ".repeat(pad) + bottom);
        printLeftHeader();
        return trimmed;
    }
}
