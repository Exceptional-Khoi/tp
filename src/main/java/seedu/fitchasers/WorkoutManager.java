package seedu.fitchasers;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class WorkoutManager {
    private static final int ARRAY_OFFSET = 1;
    private final ArrayList<Workout> workouts = new ArrayList<>();
    private Workout currentWorkout = null;
    private final UI ui = new UI();


    public WorkoutManager() {
    }

    /**
     * Parses workout details from a command string, validates the date/time,
     * creates a new Workout, and adds it to the workouts list.
     * Displays a success message using the UI, or an error message
     * if the input is invalid.
     *
     * Expected command format: n/WORKOUT_NAME d/DD/MM/YY t/HHmm
     *
     * @param command The user command containing workout name, date, and time.
     *               Example: "n/PushUps d/14/10/25 t/1900"
     */
    public void addWorkout(String command) {
        String workoutName = extractBetween(command, "n/", "d/").trim();
        String dateStr = extractBetween(command, "d/", "t/").trim();
        String timeStr = command.substring(command.indexOf("t/") + 2).trim();

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
     * Extracts and returns the substring found between two specified tokens
     * within the given text. If either token is not found, or the indices
     * are invalid, returns an empty string.
     *
     * Example: extractBetween("n/Run d/15/10/25 t/0730", "n/", "d/") returns "Run"
     *
     * @param text The input string to search within.
     * @param startToken The token marking the start of the desired substring.
     * @param endToken The token marking the end of the desired substring.
     * @return The substring between startToken and endToken, or an empty string if not found.
     */
    private String extractBetween(String text, String startToken, String endToken) {
        int start = text.indexOf(startToken) + startToken.length();
        int end = text.indexOf(endToken);
        if (start < startToken.length() || end == -1) {
            return "";
        }
        return text.substring(start, end);
    }


    private String extractAfter(String text, String token) {
        int index = text.indexOf(token);
        if (index == -1) {
            return "";
        }
        return text.substring(index + token.length()).trim();
    }

    public ArrayList<Workout> getWorkouts() {
        return workouts;
    }


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
     * Parses exercise details from the given argument string, validates the input,
     * and adds a new Exercise to the current workout if valid.
     * Displays appropriate messages for success, missing/invalid input, or if no workout is active.
     *
     * Expected argument format: n/EXERCISE_NAME r/REPS
     *
     * @param args The user input containing exercise name and repetitions.
     *             Example: "n/Push_Up r/12"
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
     * Parses the number of repetitions from the given argument string and adds a new set
     * to the current exercise in the active workout, if valid.
     * Displays appropriate messages for success, missing/invalid input, or if no workout/exercise is active.
     *
     * Expected argument format: r/REPS
     *
     * @param args The user input containing the number of repetitions for the set.
     *             Example: "r/15"
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
     * Displays a detailed summary of all recorded workouts, including their names,
     * durations, exercises, and sets. If no workouts exist, shows a message indicating
     * that no workouts have been recorded.
     *
     * Each workout is listed with its index, name, and duration. For each workout,
     * all exercises and their sets (with repetitions) are displayed in a structured format.
     */
    public void viewWorkouts() {
        if (workouts.isEmpty()) {
            ui.showMessage("No workouts recorded yet!");
            return;
        }


        for (int i = 0; i < workouts.size(); i++) {
            Workout w = workouts.get(i);
            ui.showMessage("------------------------------------------------");
            ui.showMessage("[" + (i + ARRAY_OFFSET) + "]: " + w.getWorkoutName() + " | " + w.getDuration() + " Min");


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
     * Ends the current workout session by parsing the end date and time from the given argument string,
     * validating the input, and updating the workout's end time and duration.
     * Displays appropriate messages for success, missing/invalid input, or if no workout is active.
     *
     * Expected argument format: d/DD/MM/YY t/HHmm
     *
     * @param args The user input containing the end date and time for the workout.
     *             Example: "d/14/10/25 t/2100"
     */
    public void endWorkout(String args) {
        if (currentWorkout == null) {
            ui.showMessage("No active workout.");
            return;
        }


        String dateStr = extractBetween(args, "d/", "t/").trim();
        String timeStr = extractAfter(args, "t/").trim();


        if (dateStr.isEmpty() || timeStr.isEmpty()) {
            ui.showMessage("Please provide an end date and time in format: d/DD/MM/YY t/HHmm");
            return;
        }


        String dateTimeStr = dateStr + " " + timeStr;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy HHmm");


        try {
            LocalDateTime endDateTime = LocalDateTime.parse(dateTimeStr, formatter);


            if (currentWorkout.getWorkoutStartDateTime() == null ||
                    !endDateTime.isAfter(currentWorkout.getWorkoutStartDateTime())) {
                ui.showMessage("End time must be after the start time of the workout!");
                return;
            }


            currentWorkout.setWorkoutEndDateTime(endDateTime);
            int duration = currentWorkout.calculateDuration();
            currentWorkout.setDuration(duration);


            ui.showMessage(String.format("🏁 Workout '%s' ended. Duration: %d minute(s).",
                    currentWorkout.getWorkoutName(), duration));
            currentWorkout = null;
        } catch (Exception e) {
            ui.showMessage("Invalid date/time format. Use: d/DD/MM/YY t/HHmm");
        }
    }
}
