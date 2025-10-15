package seedu.fitchasers;

import java.util.Scanner;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Manages workout sessions for the FitChasers application.
 *
 * Handles creation, deletion, and viewing of workouts,
 * as well as adding exercises and sets within each workout.
 */
public class WorkoutManager {
    private static final int ARRAY_OFFSET = 1;
    private final ArrayList<Workout> workouts = new ArrayList<>();
    private Workout currentWorkout = null;
    private final UI ui = new UI();

    /**
     * Constructs a new WorkoutManager instance.
     */
    public WorkoutManager() {
    }

    /**
     * Creates and adds a new workout to the list.
     *
     * Expected format: /create_workout n/NAME d/DD/MM/YY t/HHmm
     *
     * @param command the full user command containing workout details
     */
    public void addWorkout(String command) {
        String workoutName;
        if (command.contains("d/")) {
            workoutName = extractBetween(command, "n/", "d/").trim();
        } else if (command.contains("t/")) {
            workoutName = extractBetween(command, "n/", "t/").trim();
        } else {
            // If neither d/ nor t/ found, take the rest of the string after n/
            int nIndex = command.indexOf("n/");
            workoutName = command.substring(nIndex + 2).trim();
        }

        String dateStr = extractBetween(command, "d/", "t/").trim();
        String timeStr = "";
        if (command.contains("t/")) {
            timeStr = command.substring(command.indexOf("t/") + 2).trim();
        }

        if(dateStr.isEmpty()) {
            dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yy"));
            ui.showMessage("You missed out the date! Using current date: " + dateStr);
        }
        if(timeStr.isEmpty()) {
            timeStr = LocalTime.now().format(DateTimeFormatter.ofPattern("HHmm"));
            ui.showMessage("You missed out the time! Using current time: " + timeStr);
        }

        String dateTimeStr = dateStr + " " + timeStr;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy HHmm");

        try {
            LocalDateTime workoutDateTime = LocalDateTime.parse(dateTimeStr, formatter);
            Workout newWorkout = new Workout(workoutName, workoutDateTime);
            workouts.add(newWorkout);
            currentWorkout = newWorkout;
            ui.showMessage("Added workout: " + workoutName);
        } catch (Exception e) {
            ui.showMessage("Invalid date/time format. Use: d/DD/MM/YY t/HHmm");
        }
    }

    /**
     * Extracts text between two tokens.
     *
     * @param text the full string to search
     * @param startToken the start marker
     * @param endToken the end marker
     * @return the substring found between the tokens, or an empty string if not found
     */
    private String extractBetween(String text, String startToken, String endToken) {
        int start = text.indexOf(startToken) + startToken.length();
        int end = text.indexOf(endToken);
        if (start < startToken.length() || end == -1) {
            return "";
        }
        return text.substring(start, end);
    }

    /**
     * Extracts text after a specific token.
     *
     * @param text the full string to search
     * @param token the token to find
     * @return the text found after the token, or an empty string if not found
     */
    private String extractAfter(String text, String token) {
        int index = text.indexOf(token);
        if (index == -1) {
            return "";
        }
        return text.substring(index + token.length()).trim();
    }

    /**
     * Returns all workouts.
     *
     * @return the list of workouts
     */
    public ArrayList<Workout> getWorkouts() {
        return workouts;
    }

    /**
     * Deletes a workout by name.
     *
     * @param name the name of the workout to delete
     */
    public void deleteWorkout(String name) {
        for (Workout w : workouts) {
            if (w.getWorkoutName().equals(name)) {
                workouts.remove(w);
                ui.showMessage("Deleted workout: " + name);
                return;
            }
        }
        ui.showMessage("Workout not found: " + name);
    }

    /**
     * Adds an exercise to the current workout.
     *
     * Expected format: /add_exercise n/NAME r/REPS
     *
     * @param args the user command arguments
     */
    public void addExercise(String args) {
        if (currentWorkout == null) {
            ui.showMessage("No active workout. Use /create_workout first.");
            return;
        }

        String name = extractBetween(args, "n/", "r/").trim();
        String repsStr = extractAfter(args, "r/").trim();

        if (name.isEmpty() || repsStr.isEmpty()) {
            ui.showMessage("Usage: /add_exercise n/NAME r/REPS");
            return;
        }

        try {
            int reps = Integer.parseInt(repsStr);
            if (reps <= 0) {
                throw new NumberFormatException();
            }

            Exercise exercise = new Exercise(name, reps);
            currentWorkout.addExercise(exercise);
            ui.showMessage("Added exercise:\n" + exercise.toDetailedString());
        } catch (NumberFormatException e) {
            ui.showMessage("REPS must be a positive integer. Example: /add_exercise n/Push_Up r/12");
        }
    }

    /**
     * Adds a new set to the current exercise.
     *
     * Expected format: /add_set r/REPS
     *
     * @param args the user command arguments
     */
    public void addSet(String args) {
        if (currentWorkout == null) {
            ui.showMessage("No active workout. Use /create_workout first.");
            return;
        }

        Exercise currentExercise = currentWorkout.getCurrentExercise();
        if (currentExercise == null) {
            ui.showMessage("No exercise found. Use /add_exercise first.");
            return;
        }

        String repsStr = extractAfter(args, "r/");
        if (repsStr.isEmpty()) {
            ui.showMessage("Usage: /add_set r/REPS");
            return;
        }

        try {
            int reps = Integer.parseInt(repsStr);
            if (reps <= 0) {
                throw new NumberFormatException();
            }

            currentExercise.addSet(reps);
            ui.showMessage("Added set to exercise:\n" + currentExercise.toDetailedString());
        } catch (NumberFormatException e) {
            ui.showMessage("REPS must be a positive integer. Example: /add_set r/15");
        }
    }

    /**
     * Displays all workouts and their exercises in a formatted list.
     */
    public void viewWorkouts() {
        if (workouts.isEmpty()) {
            ui.showMessage("No workouts recorded yet!");
            return;
        }

        for (int i = 0; i < workouts.size(); i++) {
            Workout w = workouts.get(i);
            ui.showMessage("------------------------------------------------");
            ui.showMessage("[" + (i + ARRAY_OFFSET) + "]: " + w.getWorkoutName() +
                    " | " + w.getDuration() + " Min");

            if (w.getExercises().isEmpty()) {
                ui.showMessage("     No exercises added yet.");
            } else {
                for (int j = 0; j < w.getExercises().size(); j++) {
                    Exercise ex = w.getExercises().get(j);
                    ui.showMessage("     Exercise " + (j + 1) + ". " + ex);
                    for (int k = 0; k < ex.getSets().size(); k++) {
                        ui.showMessage("         Set " + (k + 1) + " -> Reps: " + ex.getSets().get(k));
                    }
                }
            }
        }
    }

    /**
     * Ends the current workout session by recording the end time and calculating duration.
     *
     * Accepts user input in the format: /end_workout d/DD/MM/YY t/HHmm
     * If either date or time is missing, uses the current date or time as default.
     * Validates that the end date and time are not before the workout's start.
     * If the user input is invalid (earlier than start), prompts for re-entry until valid.
     *
     * @param scanner Scanner for reading user input in the retry loop
     * @param initialArgs Initial command arguments containing end date/time details
     */
    public void endWorkout(Scanner scanner, String initialArgs) {
        if (currentWorkout == null) {
            ui.showMessage("No active workout.");
            return;
        }
        String args = initialArgs; // first time use what user passes in
        while (true) {
            String dateStr;
            if (args.contains("t/")) {
                dateStr = extractBetween(args, "d/", "t/").trim();
            } else if (args.contains("d/")) {
                int idx = args.indexOf("d/");
                if (idx != -1) {
                    dateStr = args.substring(idx + 2).trim();
                } else {
                    dateStr = "";
                }
            } else {
                dateStr = "";
            }

            String timeStr = extractAfter(args, "t/").trim();

            // If missing, use default values and notify user
            boolean usedDefaultDate = false;
            boolean usedDefaultTime = false;
            if (dateStr.isEmpty()) {
                dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yy"));
                ui.showMessage("You missed out the date! Using current date: " + dateStr);
                usedDefaultDate = true;
            }
            if (timeStr.isEmpty()) {
                timeStr = LocalTime.now().format(DateTimeFormatter.ofPattern("HHmm"));
                ui.showMessage("You missed out the time! Using current time: " + timeStr);
                usedDefaultTime = true;
            }

            String dateTimeStr = dateStr + " " + timeStr;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy HHmm");

            try {
                LocalDateTime endDateTime = LocalDateTime.parse(dateTimeStr, formatter);
                LocalDateTime startTimeTruncated =
                        currentWorkout.getWorkoutStartDateTime().truncatedTo(ChronoUnit.MINUTES);
                LocalDateTime endTimeTruncated = endDateTime.truncatedTo(ChronoUnit.MINUTES);

                // Check for invalid date
                if (endTimeTruncated.toLocalDate().isBefore(startTimeTruncated.toLocalDate())) {
                    ui.showMessage("End date must not be before start date of the workout!");
                    ui.showMessage("Please enter: /end_workout d/DD/MM/YY t/HHmm");
                    args = scanner.nextLine();
                    continue;
                }

                // Check for invalid time on same day
                if (!endTimeTruncated.isAfter(startTimeTruncated)) {
                    ui.showMessage("End time must be after the start time of the workout!");
                    ui.showMessage("Please enter: /end_workout d/DD/MM/YY t/HHmm");
                    args = scanner.nextLine();
                    continue;
                }

                currentWorkout.setWorkoutEndDateTime(endDateTime);
                int duration = currentWorkout.calculateDuration();
                currentWorkout.setDuration(duration);
                ui.showMessage(String.format(
                        "Workout '%s' ended. Duration: %d minute(s).",
                        currentWorkout.getWorkoutName(), duration
                ));
                currentWorkout = null;
                break;
            } catch (Exception e) {
                ui.showMessage("Invalid date/time format. Use: /end_workout d/DD/MM/YY t/HHmm");
                ui.showMessage("Please enter: /end_workout d/DD/MM/YY t/HHmm");
                args = scanner.nextLine();
            }
        }
    }

}
