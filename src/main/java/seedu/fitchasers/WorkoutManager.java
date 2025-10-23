package seedu.fitchasers;

import java.time.format.ResolverStyle;
import java.util.LinkedHashSet;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Set;

/**
 * Manages workout sessions for the FitChasers application.
 *
 * Handles creation, deletion, and viewing of workouts,
 * as well as adding exercises and sets within each workout.
 */
public class WorkoutManager {
    private static final int ARRAY_OFFSET = 1;
    private ArrayList<Workout> workouts = new ArrayList<>();
    private Workout currentWorkout = null;
    private final UI ui = new UI();
    private final Tagger tagger = new DefaultTagger();

    public void setWorkouts(ArrayList<Workout> workouts) {
        this.workouts = workouts;
    }

    /**
     * Creates and adds a new workout to the list.
     *
     * Expected format: /create_workout n/NAME d/DD/MM/YY t/HHmm
     *
     * @param command the full user command containing workout details
     */
    public void addWorkout(String command) {
        assert workouts != null : "workouts list should be initialized";

        if (currentWorkout != null) {
            ui.showMessage("You currently have an active workout: '"
                    + currentWorkout.getWorkoutName() + "'.");
            ui.showMessage("Please end the active workout first with: /end_workout d/DD/MM/YY t/HHmm");
            return;
        }

        assert currentWorkout == null : "No active workout expected before creating a new one";

        if (command == null || !command.contains("n/")) {
            ui.showMessage("Invalid format. Use: /create_workout n/WorkoutName d/DD/MM/YY t/HHmm");
            return;
        }

        String workoutName;
        int nIdx = command.indexOf("n/");
        int afterN = nIdx + 2;

        // Find first marker after n/
        int dIdx = command.indexOf("d/", afterN);
        int tIdx = command.indexOf("t/", afterN);

        // pick the nearest positive marker after n/
        int nextMarker = -1;
        if (dIdx != -1 && tIdx != -1) {
            nextMarker = Math.min(dIdx, tIdx);
        } else if (dIdx != -1) {
            nextMarker = dIdx;
        } else if (tIdx != -1) {
            nextMarker = tIdx;
        }

        if (nextMarker != -1) {
            workoutName = command.substring(afterN, nextMarker).trim();
        } else {
            workoutName = command.substring(afterN).trim();
        }

        if (workoutName.isEmpty()) {
            ui.showMessage("Workout name cannot be empty. Use: /create_workout n/WorkoutName d/DD/MM/YY t/HHmm");
            return;
        }

        assert !workoutName.isEmpty() : "workoutName should be non-empty after validation";

        // Extract raw date/time strings if present (first token after marker)
        String dateStr = "";
        if (dIdx != -1) {
            String tail = command.substring(dIdx + 2).trim();
            String[] toks = tail.split("\\s+");
            if (toks.length > 0) dateStr = toks[0];
        }

        String timeStr = "";
        if (tIdx != -1) {
            String tail = command.substring(tIdx + 2).trim();
            String[] toks = tail.split("\\s+");
            if (toks.length > 0) timeStr = toks[0];
        }

        // Strict formatters & validate provided pieces first
        DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yy")
                .withResolverStyle(ResolverStyle.SMART);
        DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HHmm")
                .withResolverStyle(ResolverStyle.SMART);

        LocalDate date = null;
        LocalTime time = null;

        if (!dateStr.isEmpty()) {
            try {
                date = LocalDate.parse(dateStr, DATE_FMT);
            } catch (Exception ex) {
                ui.showMessage("Invalid date. Use d/DD/MM/YY (e.g., d/23/10/25).");
                return;
            }
        }

        if (!timeStr.isEmpty()) {
            try {
                time = LocalTime.parse(timeStr, TIME_FMT);
            } catch (Exception ex) {
                ui.showMessage("Invalid time. Use t/HHmm (e.g., t/1905).");
                return;
            }
        }

        // Prompt ONLY for missing ones
        if (date == null) {
            String todayStr = LocalDate.now().format(DATE_FMT);
            ui.showMessage("Looks like you missed the date. Use current date (" + todayStr + ")? (Y/N)");
            if (ui.confirmationMessage()) {
                date = LocalDate.now();
            } else {
                ui.showMessage("Please provide a date in format d/DD/MM/YY.");
                return;
            }
        }

        if (time == null) {
            String nowStr = LocalTime.now().format(TIME_FMT);
            ui.showMessage("Looks like you missed the time. Use current time (" + nowStr + ")? (Y/N)");
            if (ui.confirmationMessage()) {
                time = LocalTime.now();
            } else {
                ui.showMessage("Please provide a time in format t/HHmm.");
                return;
            }
        }

        LocalDateTime workoutDateTime = LocalDateTime.of(date, time);

        try {
            Workout newWorkout = new Workout(workoutName, workoutDateTime);

            // merge auto-tags if you have a tagger
            Set<String> mergedTags = new LinkedHashSet<>(newWorkout.getTags());
            mergedTags.addAll(tagger.suggest(newWorkout));
            newWorkout.setTags(mergedTags);

            workouts.add(newWorkout);
            currentWorkout = newWorkout;

            ui.showMessage("New workout sesh incoming!");
            ui.showMessage("Added workout: " + workoutName);
        } catch (Exception e) {
            ui.showMessage("Something went wrong creating the workout. Please try again.");
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

    public int getWorkoutSize() {
        return workouts.size();
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
                ui.showMessage("Deleting " + w.getWorkoutName() + " | " +
                        w.getWorkoutDateString() + "? T.T Are you sure, bestie? (Type y/yes to confirm)");
                if (ui.confirmationMessage()) {
                    workouts.remove(w);
                    ui.showMessage("Workout deleted successfully!");
                } else {
                    ui.showMessage("Okay, I didn’t delete it.");
                }
                return;
            }
        }
        ui.showMessage("Workout not found: " + name);
    }

    public void deleteWorkoutByIndex(int index) {
        if(index < 0 || index >= workouts.size()) {
            ui.showMessage("Invalid workout index: " + index + "Please try again.:(");
            return;
        }
        Workout w = workouts.get(index);
        ui.showMessage("Deleting " + w.getWorkoutName() + " | " + w.getWorkoutName() + "|");
        ui.showMessage("You sure you want to delete this?(y/n)");
        if(ui.confirmationMessage()) {
            ui.showMessage("Deleted workout: " + w.getWorkoutName());
            workouts.remove(index);
        }else{
            ui.showMessage("Okay, deletion aborted.");
        }
    }

    private ArrayList<Workout> getWorkoutsByDate(LocalDate date) {
        ArrayList<Workout> filteredWorkout = new ArrayList<>();
        for (Workout w : workouts) {
            LocalDateTime startDateTime = w.getWorkoutStartDateTime();
            if (startDateTime != null && startDateTime.toLocalDate().equals(date)) {
                filteredWorkout.add(w);
            }
        }
        return filteredWorkout;
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
            ui.showMessage("Invalid format. Please use: /add_exercise n/NAME r/REPS");
            return;
        }

        try {
            int reps = Integer.parseInt(repsStr);
            if (reps <= 0) {
                throw new NumberFormatException();
            }

            Exercise exercise = new Exercise(name, reps);
            currentWorkout.addExercise(exercise);
            ui.showMessage("Adding that spicy new exercise!");
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
            ui.showMessage("Adding a new set to your exercise!");
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

    public void showWorkoutsWIthIndices(ArrayList<Workout> list) {
        if(list.isEmpty()) {
            ui.showMessage("No workouts recorded for this selection!");
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            Workout w = list.get(i);
            ui.showMessage((i+1) + ". " + w.getWorkoutName() + " - " + w.getWorkoutDateString());
        }
    }

    public void interactiveDeleteWorkout(String command, UI ui) {
        ArrayList<Workout> targetList = workouts;

        if(command.contains("d/")){
            String dateStr = extractAfter(command, "d/").trim();
            String[] dateTokens = dateStr.split("\\s+");
            String datePart = dateTokens[0];
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
            try {
                LocalDate date = LocalDate.parse(datePart, formatter);
                targetList = getWorkoutsByDate(date);
            } catch (Exception e) {
                ui.showError("Invalid date format! (Use d/DD/MM/YY)");
                return;
            }
        }

        if (targetList.isEmpty()) {
            ui.showMessage("No workouts found for the given date.");
            return;
        }

        showWorkoutsWIthIndices(targetList);
        ui.showMessage("Enter the number/numbers of the workout to be deleted:");
        ui.showMessage("> ");
        String selection = ui.readCommand();

        String[] tokens = selection.split("\\s+");
        ArrayList<Integer> indicesToDelete = new ArrayList<>();
        for(String token :tokens){
            try{
                int index = Integer.parseInt(token) - 1;
                if(index >= 0 && index < targetList.size()){
                    indicesToDelete.add(index);
                }
            }catch(NumberFormatException ignored){
                ui.showError("An unexpected error occurred: " + ignored.getMessage());
            }
        }

        if(indicesToDelete.isEmpty()) {
            ui.showMessage("No valid indices entered. Nothing deleted.");
            return;
        }

        for(int i = indicesToDelete.size() - 1; i >= 0; i--) {
            Workout w = targetList.get(indicesToDelete.get(i));
            workouts.remove(w);
            ui.showMessage("Delete: " + w.getWorkoutName());
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
     * @param ui UI for reading user input in the retry loop
     * @param initialArgs Initial command arguments containing end date/time details
     */
    public void endWorkout(UI ui, String initialArgs) {
        if (currentWorkout == null) {
            ui.showMessage("No active workout.");
            return;
        }

        final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yy")
                .withResolverStyle(ResolverStyle.SMART);
        final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HHmm")
                .withResolverStyle(ResolverStyle.SMART);

        String args = (initialArgs == null) ? "" : initialArgs.trim();

        while (true) {
            // Extract raw tokens
            String dateStr = "";
            String timeStr = "";

            int dIdx = args.indexOf("d/");
            int tIdx = args.indexOf("t/");
            if (dIdx != -1) {
                String tail = args.substring(dIdx + 2).trim();
                String[] toks = tail.split("\\s+");
                if (toks.length > 0) dateStr = toks[0].trim();
            }
            if (tIdx != -1) {
                String tail = args.substring(tIdx + 2).trim();
                String[] toks = tail.split("\\s+");
                if (toks.length > 0) timeStr = toks[0].trim();
            }

            LocalDate date = null;
            LocalTime time = null;

            // Validate provided pieces first
            if (!dateStr.isEmpty()) {
                try {
                    date = LocalDate.parse(dateStr, DATE_FMT);
                } catch (Exception ex) {
                    ui.showMessage("[Error] Invalid date. Use d/DD/MM/YY (e.g., d/23/10/25).");
                    ui.showMessage("Please enter: /end_workout d/DD/MM/YY t/HHmm");
                    args = ui.readCommand();
                    if (args == null) return; // EOF safety
                    args = args.trim();
                    continue;
                }
            }
            if (!timeStr.isEmpty()) {
                try {
                    time = LocalTime.parse(timeStr, TIME_FMT);
                } catch (Exception ex) {
                    ui.showMessage("[Error] Invalid time. Use t/HHmm (e.g., t/1905).");
                    ui.showMessage("Please enter: /end_workout d/DD/MM/YY t/HHmm");
                    args = ui.readCommand();
                    if (args == null) return; // EOF safety
                    args = args.trim();
                    continue;
                }
            }

            // Prompt ONLY for missing pieces
            if (date == null) {
                String todayStr = LocalDate.now().format(DATE_FMT);
                ui.showMessage("Looks like you missed the date. Use current date (" + todayStr + ")? (Y/N)");
                if (ui.confirmationMessage()) {
                    date = LocalDate.now();
                } else {
                    ui.showMessage("Please enter: /end_workout d/DD/MM/YY t/HHmm");
                    args = ui.readCommand();
                    if (args == null) return;
                    args = args.trim();
                    continue;
                }
            }
            if (time == null) {
                String nowStr = LocalTime.now().format(TIME_FMT);
                ui.showMessage("Looks like you missed the time. Use current time (" + nowStr + ")? (Y/N)");
                if (ui.confirmationMessage()) {
                    time = LocalTime.now();
                } else {
                    ui.showMessage("Please enter: /end_workout d/DD/MM/YY t/HHmm");
                    args = ui.readCommand();
                    if (args == null) return;
                    args = args.trim();
                    continue;
                }
            }

            // Combine & validate
            LocalDateTime endDateTime = LocalDateTime.of(date, time).truncatedTo(ChronoUnit.MINUTES);
            LocalDateTime startTime = currentWorkout.getWorkoutStartDateTime().truncatedTo(ChronoUnit.MINUTES);

            if (!endDateTime.isAfter(startTime)) {
                ui.showMessage("End time must be after the start time of the workout!");
                ui.showMessage("Please enter: /end_workout d/DD/MM/YY t/HHmm");
                args = ui.readCommand();
                if (args == null) return;
                args = args.trim();
                continue;
            }

            currentWorkout.setWorkoutEndDateTime(endDateTime);
            int duration = currentWorkout.calculateDuration();
            currentWorkout.setDuration(duration);

            ui.showMessage("Workout wrapped! Time to refuel!");
            ui.showMessage(String.format("Workout '%s' ended. Duration: %d minute(s).",
                    currentWorkout.getWorkoutName(), duration));

            currentWorkout = null;
            break;
        }
    }
}
