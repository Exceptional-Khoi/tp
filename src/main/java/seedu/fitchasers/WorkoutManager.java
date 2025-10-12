package seedu.fitchasers;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class WorkoutManager
{
    private static final int ARRAY_OFFSET = 1;
    private final ArrayList<Workout> workouts =  new ArrayList<>();

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
        workouts.add(new Workout(workoutName, workoutDateTime));
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

    public boolean removeWorkout(String name)
    {
        for( Workout w : workouts){
            if(w.getWorkoutName().equals(name)){
                workouts.remove(w);
                return true;
            }
        }
        return false;
    }

    public void viewWorkouts(){
        for(int i = 0; i < workouts.size(); i++){
            System.out.println("=============================================================");
            System.out.print("[" + (i + ARRAY_OFFSET) + "]: ");
            System.out.println(workouts.get(i).getWorkoutName() + " | " + workouts.get(i).getDuration());
            System.out.println("=============================================================");
        }
    }
}
