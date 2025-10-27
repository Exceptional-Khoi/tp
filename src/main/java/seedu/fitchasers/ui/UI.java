package seedu.fitchasers.ui;

import seedu.fitchasers.workouts.Exercise;
import seedu.fitchasers.workouts.Workout;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * The {@code UI} class handles all user interactions for the FitChaser application.
 * Combines the robustness of the classic UI logic with a modern "chat bubble" interface.
 */
public class UI {
    // ====== Color and Style Constants ======
    private static final String RESET = "\u001B[0m";
    private static final String CYAN = "\u001B[36m";
    private static final String LIGHT_YELLOW = "\u001B[38;5;187m";
    private static final String MAGENTA = "\u001B[35m";
    private static final String WHITE = "\u001B[97m";
    private static final int CONSOLE_WIDTH = 150;

    // Chat Bubble Layout Constants
    private static final int PADDING = 2;
    private static final int FRAME_OVERHEAD = 6;

    private final Scanner scanner;

    public UI() {
        this.scanner = new Scanner(System.in);
    }

    // -----------------------------
    // Input
    // -----------------------------
    public String readCommand() {
        System.out.print(MAGENTA + "Enter command > " + RESET);
        if (!scanner.hasNextLine()) {
            return null; // EOF
        }

        String input = scanner.nextLine();
        assert input != null : "User input should never be null";

        // Display user input as chat bubble on right
        System.out.println(rightBubble("You", input));

        return input.trim();
    }

    public String enterName() {
        String name = "";
        while (name.isEmpty()) {
            System.out.print(MAGENTA + "Enter your name: " + RESET);
            if (scanner.hasNextLine()) {
                name = scanner.nextLine().trim();
                if (name.isEmpty()) {
                    showError("Name cannot be empty. Please try again!");
                } else {
                    System.out.println(rightBubble("You", name));
                }
            } else {
                return null;
            }
        }
        return name;
    }

    /**
     * Prompts the user to enter their initial weight in kilograms.
     * Ensures valid numeric input greater than zero.
     *
     * @return the user's initial weight as a double
     */
    public double enterWeight() {
        double weight = -1;
        while (weight <= 0) {
            showMessage("Please enter your initial weight (in kg).");
            System.out.print(MAGENTA + "Enter your weight: " + RESET);
            String input = new java.util.Scanner(System.in).nextLine().trim();
            try {
                weight = Double.parseDouble(input);
                if (weight <= 0) {
                    showError("Weight must be a positive number. Try again!");
                }
            } catch (NumberFormatException e) {
                showError("Invalid number. Please enter a valid weight (e.g., 60.5).");
            }
        }
        return weight;
    }

    // -----------------------------
    // Output
    // -----------------------------
    public void showMessage(String message) {
        assert message != null : "Message cannot be null";
        System.out.println(leftBubble("^_^ FitChaser", message));
    }

    public void showError(String error) {
        assert error != null : "Error message cannot be null";
        System.out.println(leftBubble("^_^ FitChaser", "[Oops!] " + error));
    }

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

    public void showExitMessage() {
        showMessage("Catch you next time, champ — don't ghost your gains!");
    }

    public void showHelp() {
        showMessage("""
        /help (h)                                 - View all available commands

        --- USER PROFILE ---
        /my_name (n) n/NAME                       - Set or change your display name
                                                    e.g. /my_name n/Nitin

        --- WEIGHT TRACKING ---
        /add_weight (aw) w/WEIGHT d/DATE          - Record your weight
                                                    e.g. /add_weight w/81.5 d/19/10/25
        /view_weight (vw)                         - View your recorded weights and graph

        --- WORKOUT CREATION & LOGGING ---
        /create_workout (cw) n/NAME d/DATE t/TIME - Create a new workout
                                                    e.g. /create_workout n/PushDay d/20/10/25 t/1900
        /add_exercise (ae) n/NAME r/REPS          - Add an exercise to current workout
                                                    e.g. /add_exercise n/Squat r/12
        /add_set (as) r/REPS                      - Add another set to the latest exercise
                                                    e.g. /add_set r/10
        /end_workout (ew) d/DATE t/TIME           - End and save current workout
                                                    e.g. /end_workout d/20/10/25 t/2030

        --- WORKOUT LOG MANAGEMENT ---
        /view_log (vl)                            - View your workout history
        /open (o) INDEX                           - Open detailed view of a workout
                                                    e.g. /open 1
        /del_workout (d) NAME                     - Delete a workout by name
                                                    e.g. /del_workout PushDay
        /del_workout (d) d/DATE                   - Delete a workout by date
                                                    e.g. /del_workout d/20/10/25

        --- TAGGING SYSTEM ---
        /add_modality_tag (amot) m/(CARDIO/STRENGTH) k/KEYWORD
                                                    - Add a keyword for a workout modality
                                                    e.g. /add_modality_tag m/CARDIO k/hiking
        /add_muscle_tag (amt) m/MUSCLE k/KEYWORD  - Add a keyword for a muscle group
                                                    e.g. /add_muscle_tag m/LEGS k/lunges
        /override_workout_tag (owt) id/INDEX newTag/NEW_TAG
                                                    - Manually override a workout’s tag
                                                    e.g. /override_workout_tag id/1 newTag/LEG_DAY

        --- GYM FINDER ---
        /gym_where (gw) n/EXERCISE                - Suggest NUS gyms with equipment for the exercise
                                                    e.g. /gym_where n/squat
        /gym_page (gp) p/PAGE_NUMBER              - View available NUS gym pages
                                                    e.g. /gym_page p/1

        --- SYSTEM ---
        /exit (e)                                 - Save all progress and exit the app """);
    }

    public boolean confirmationMessage() {
        if (!scanner.hasNextLine()) {
            return false;
        }
        String confirmation = scanner.nextLine().trim().toLowerCase();
        assert confirmation != null : "Confirmation input must not be null";
        System.out.println(rightBubble("You", confirmation));
        return confirmation.equals("y") || confirmation.equals("yes");
    }

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

    // ================== Chat Bubble Logic ==================
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

    private String leftBubble(String sender, String message) {
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
        sb.append(LIGHT_YELLOW).append(sender).append(RESET).append("\n");
        sb.append(top).append("\n");
        for (String l : lines) {
            int spaces = clampNonNeg(innerWidth - l.length() - PADDING);
            sb.append("│")
                    .append(" ".repeat(PADDING))
                    .append(WHITE).append(l).append(RESET)
                    .append(" ".repeat(spaces))
                    .append("│\n");
        }
        sb.append(bottom);
        return sb.toString();
    }

    private String rightBubble(String sender, String message) {
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

        String top = CYAN + "+" + "-".repeat(clampNonNeg(innerWidth)) + "+" + RESET;
        String bottom = CYAN + "+" + "-".repeat(clampNonNeg(innerWidth)) + "+" + RESET;

        int pad = clampNonNeg(CONSOLE_WIDTH - innerWidth - 6);

        StringBuilder sb = new StringBuilder();
        sb.append(" ".repeat(pad))
                .append(LIGHT_YELLOW).append("(").append(sender).append(")").append(RESET).append("\n");
        sb.append(" ".repeat(pad)).append(top).append("\n");
        for (String l : lines) {
            int spaces = clampNonNeg(innerWidth - l.length() - PADDING);
            sb.append(" ".repeat(pad))
                    .append(CYAN).append("│").append(RESET)
                    .append(CYAN).append(" ".repeat(PADDING)).append(l)
                    .append(" ".repeat(spaces))
                    .append("│").append(RESET)
                    .append("\n");
        }
        sb.append(" ".repeat(pad)).append(bottom);
        return sb.toString();
    }
}
