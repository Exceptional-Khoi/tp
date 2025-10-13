package seedu.fitchasers;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class WorkoutManager {
    private static final int ARRAY_OFFSET = 1;
    private final ArrayList<Workout> workouts = new ArrayList<>();
    private Workout currentWorkout = null;
    private final UI ui;


    public WorkoutManager(UI ui) {
        this.ui = ui;
    }


    // ===============================
    // ADD WORKOUT
    // ===============================
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


    // ===============================
    // HELPER METHODS
    // ===============================
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


    // ===============================
    // WORKOUT MANAGEMENT
    // ===============================
    public ArrayList<Workout> getWorkouts() {
        return workouts;
    }


    public void loadWorkoutFromFile(String workout) {
        String name = workout.substring(0, workout.indexOf("|"));
        try {
            int duration = Integer.parseInt(workout.substring(workout.indexOf("|") + 1).trim());
            workouts.add(new Workout(name.trim(), duration));
        } catch (NumberFormatException e) {
            ui.showMessage("Invalid workout format, file might be corrupted.");
        }
    }


    public boolean removeWorkout(String name) {
        for (Workout w : workouts) {
            if (w.getWorkoutName().equals(name)) {
                workouts.remove(w);
                ui.showMessage("Removed workout: " + name);
                return true;
            }
        }
        ui.showMessage("Workout not found: " + name);
        return false;
    }


    // ===============================
    // ADD EXERCISE
    // ===============================
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
            if (reps <= 0) throw new NumberFormatException();


            Exercise exercise = new Exercise(name, reps);
            currentWorkout.addExercise(exercise);
            ui.showMessage("Added exercise:\n" + exercise.toDetailedString());
        } catch (NumberFormatException e) {
            ui.showMessage("REPS must be a positive integer. Example: /add_exercise n/Push_Up r/12");
        }
    }


    // ===============================
    // ADD SET
    // ===============================
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
            if (reps <= 0) throw new NumberFormatException();


            currentExercise.addSet(reps);
            ui.showMessage("Added set to exercise:\n" + currentExercise.toDetailedString());
        } catch (NumberFormatException e) {
            ui.showMessage("REPS must be a positive integer. Example: /add_set r/15");
        }
    }


    // ===============================
    // VIEW WORKOUTS
    // ===============================
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


    // ===============================
    // END WORKOUT
    // ===============================
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
