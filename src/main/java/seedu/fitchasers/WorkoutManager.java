package seedu.fitchasers;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class WorkoutManager{
    private static final int ARRAY_OFFSET = 1;
    private final ArrayList<Workout> workouts =  new ArrayList<>();
    private Workout currentWorkout = null;


    public void addWorkout(String command) {
        // Extract each argument
        String workoutName = extractBetween(command, "n/", "d/").trim();
        String dateStr = extractBetween(command, "d/", "t/").trim();
        String timeStr = command.substring(command.indexOf("t/") + 2).trim();

        // Combine date and time into one string
        String dateTimeStr = dateStr + " " + timeStr;

        // Parse the combined string into LocalDateTime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy HHmm");
        LocalDateTime workoutDateTime = LocalDateTime.parse(dateTimeStr, formatter);

        // Assuming Workout(String name, LocalDateTime dateTime)
        Workout newWorkout = new Workout(workoutName, workoutDateTime);
        workouts.add(newWorkout);
        currentWorkout = newWorkout;

        System.out.println("Added workout " + workoutName); //Starvou please update this
    }

    /**
     * Helper method to extract a substring between two tokens.
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
     * Extracts the substring that appears after the given token.
     */
    private String extractAfter(String text, String token) {
        int index = text.indexOf(token);
        if (index == -1) {
            return ""; // token not found
        }
        return text.substring(index + token.length()).trim();
    }

    public ArrayList<Workout> getWorkouts() {
        return workouts;
    }

    public void loadWorkoutFromFile(String workout){
        String name = workout.substring(0, workout.indexOf("|"));
        int duration = 0;
        try{
            Integer.parseInt(workout.substring(workout.indexOf("|")+1).trim());
        }catch(NumberFormatException e){
            System.out.println("Invalid workout format, file might be corrupted");
            return;
        }
        workouts.add(new Workout(name.trim(), duration));
    }

    public boolean removeWorkout(String name){
        for( Workout w : workouts){
            if(w.getWorkoutName().equals(name)){
                workouts.remove(w);
                return true;
            }
        }
        return false;
    }

    public void addExercise(String args) {
        if (currentWorkout == null) {
            System.out.println("No active workout. Use /create_workout first.");
            return;
        }
        String name = extractBetween(args, "n/", "r/").trim();
        String repsStr = extractAfter(args, "r/").trim();
        if (name.isEmpty() || repsStr.isEmpty()) {
            System.out.println("Usage: /add_exercise n/NAME r/REPS");
            return;
        }
        int reps;
        try {
            reps = Integer.parseInt(repsStr);
            if (reps <= 0){
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            System.out.println("REPS must be a positive integer. Example: /add_exercise n/Push_Up r/12");
            return;
        }

        Exercise exercise = new Exercise(name, reps);
        currentWorkout.addExercise(exercise);
        System.out.println("Added exercise to current workout: " + exercise);
    }

    public void viewWorkouts() {
        for (int i = 0; i < workouts.size(); i++) {
            Workout w = workouts.get(i);
            System.out.println("=============================================================");
            System.out.print("[" + (i + ARRAY_OFFSET) + "]: ");
            System.out.println(w.getWorkoutName() + " | " + w.getDuration());

            // Print exercises with numbering
            if (w.getExercises().isEmpty()) {
                System.out.println("     No exercises added yet.");
            } else {
                for (int j = 0; j < w.getExercises().size(); j++) {
                    System.out.println("     Exercise " + (j + 1) + ". " + w.getExercises().get(j));
                }
            }
            System.out.println("=============================================================");
        }
    }
}
