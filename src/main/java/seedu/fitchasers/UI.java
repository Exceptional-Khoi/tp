package seedu.fitchasers;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;

public class UI {
    // ===== ANSI colors =====
    private static final String RESET = "\u001B[0m";
    private static final String BLUE_BOLD = "\u001B[1;34m";
    private static final String WHITE = "\u001B[97m";;
    private static final String CYAN = "\u001B[36m";
    private static final String MAGENTA = "\u001B[35m";

    // Background colors (256-color mode)
    private static final String BG_FITCHASER = "\u001B[48;5;236m";   // xám đậm cho FitChaser
    private static final String BG_USER = "\u001B[48;5;39m";   // xanh navy cho You

    private static final int CONSOLE_WIDTH = 80; // chiều rộng terminal

    private final Scanner scanner;

    public UI() {
        this.scanner = new Scanner(System.in);
    }

    // -----------------------------
    // Input
    // -----------------------------
    public String readCommand() {
        showDivider();
        System.out.print(MAGENTA + "Enter command> " + RESET);
        if (!scanner.hasNextLine()) {
            return null;
        }
        showDivider();

        String input = scanner.nextLine();
        assert input != null : "User input should never be null";

        System.out.println(rightBubble("You", input));

        return input.trim();
    }

    public String enterName() {
        String name = "";
        while (name.isEmpty()) {
            System.out.print(rightBubble("You", "Enter your name: "));
            if (scanner.hasNextLine()) {
                name = scanner.nextLine().trim();
                if (name.isEmpty()) {
                    showMessage("Name cannot be empty. Please try again.");
                } else {
                    System.out.println(rightBubble("You",  name));
                }
            } else {
                return null;
            }
        }
        showDivider();
        return name;
    }

    // -----------------------------
    // Output
    // -----------------------------
    public void showMessage(String message) {
        System.out.println(leftBubble("🤖 FitChaser", message));
    }

    public void showError(String error) {
        System.out.println(leftBubble("🤖 FitChaser", "[Oops!] " + error));
    }


    public void showGreeting() {
        System.out.println(BLUE_BOLD + """
                 ▄▄▄▄▄▄   ▀      ▄      ▄▄▄  █                                       
                 █      ▄▄▄    ▄▄█▄▄  ▄▀   ▀ █ ▄▄    ▄▄▄    ▄▄▄    ▄▄▄    ▄ ▄▄   ▄▄▄ 
                 █▄▄▄▄▄   █      █    █      █▀  █  ▀   █  █   ▀  █▀  █   █▀  ▀ █   ▀
                 █        █      █    █      █   █  ▄▀▀▀█   ▀▀▀▄  █▀▀▀▀   █      ▀▀▀▄
                 █      ▄▄█▄▄    ▀▄▄   ▀▄▄▄▀ █   █  ▀▄▄▀█  ▀▄▄▄▀  ▀█▄▄▀   █     ▀▄▄▄▀
                """ + RESET);

        showMessage("Your virtual gym buddy's clocked in and ready to make you strong!");
        showMessage("Type /help or h to explore all available commands!");
        showMessage("Let's crush your fitness goals together!");
    }

    public void showExitMessage() {
        showMessage("Catch you next time, champ — don't ghost your gains!");
        showDivider();
    }

    public void showHelp() {
        showMessage("""
        /help (h) - View all commands
        /my_name (n) n/NAME - Set your display name
        /add_weight (aw) w/WEIGHT d/DATE - Record your weight
        /view_weight (vw) - View your recorded weights
        /gym_where n/EXERCISE - Find gyms for your exercise
        /gym_page - Find available gyms in NUS
        /create_workout (cw) n/NAME d/DATE t/TIME - Create a new workout
        /exit (e) - Save progress and exit the app
        """);
    }

    public void showDivider() {
        System.out.println(WHITE + "--------------------------------------------------" + RESET);
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
            showMessage("No workout found to display.");
            return;
        }

        showMessage("Here you go bestie! These are the workout details!");

        showMessage("Name       : " + workout.getWorkoutName());
        showMessage("Date       : " + workout.getWorkoutDateString());

        int totalMinutes = workout.getDuration();
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        String durationStr = (hours > 0)
                ? String.format("%dh %dm", hours, minutes)
                : String.format("%dm", minutes);
        showMessage("Duration   : " + durationStr);

        if (workout.getWorkoutStartDateTime() != null && workout.getWorkoutEndDateTime() != null) {
            showMessage("Start Time : " + workout.getWorkoutStartDateTime());
            showMessage("End Time   : " + workout.getWorkoutEndDateTime());
        }

        Set<String> tagsToDisplay = workout.getAllTags();
        if (tagsToDisplay.isEmpty()) {
            showMessage("Tags       : -");
        } else {
            showMessage("Tags       : " + String.join(", ", tagsToDisplay));
        }

        ArrayList<Exercise> exercises = workout.getExercises();
        if (exercises.isEmpty()) {
            showMessage("Exercises  : (none added)");
        } else {
            showMessage("Exercises  : ");
            int i = 1;
            for (Exercise e : exercises) {
                showMessage(String.format("  %d. %s", i++, e.toString()));
            }
        }
    }

    static String getDaySuffix(int day) {
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

    // ------------------ Chat Bubble Formatters ------------------

    private String leftBubble(String sender, String message) {
        String cleanMsg = stripAnsi(message);
        int padding = 2;
        int width = Math.min(cleanMsg.length() + padding * 2, CONSOLE_WIDTH - 6);

        String top = "╭" + "─".repeat(width) + "╮";
        String contentLine = String.format("│%s%s%s│",
                " ".repeat(padding),
                WHITE + cleanMsg + RESET,
                " ".repeat(width - cleanMsg.length() - padding)
        );
        String bottom = "╰" + "─".repeat(width) + "╯";

        return String.format("%s%s%s\n%s\n%s\n%s",
                CYAN, sender, RESET,
                top, contentLine, bottom
        );
    }

    private String rightBubble(String sender, String message) {
        String cleanMsg = stripAnsi(message);
        int padding = 2;
        int width = Math.min(cleanMsg.length() + padding * 2, CONSOLE_WIDTH - 6);

        String top = BLUE_BOLD + "╭" + "─".repeat(width) + "╮" + RESET;
        String contentLine = String.format(
                "%s│%s%s%s│%s",
                BLUE_BOLD,                                  // viền trái xanh
                " ".repeat(padding),
                BLUE_BOLD + cleanMsg,                       // chữ xanh (không RESET giữa chừng)
                " ".repeat(width - cleanMsg.length() - padding),
                RESET                                       // RESET sau khi in viền phải
        );
        String bottom = BLUE_BOLD + "╰" + "─".repeat(width) + "╯" + RESET;


        int pad = Math.max(0, CONSOLE_WIDTH - width - 6);
        return " ".repeat(pad) + CYAN + "(" + sender + ")" + RESET + "\n"
                + " ".repeat(pad) + top + "\n"
                + " ".repeat(pad) + contentLine + "\n"
                + " ".repeat(pad) + bottom;
    }


    private static String stripAnsi(String input) {
        return input.replaceAll("\u001B\\[[;\\d]*m", "");
    }
}

