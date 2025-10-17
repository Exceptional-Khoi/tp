package seedu.fitchasers;

import java.time.Duration;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Represents a workout session containing a name, duration, start/end times, and a list of exercises.
 */
public class Workout {
    private String workoutName;
    private int duration = 0;
    private LocalDateTime workoutStartDateTime;
    private LocalDateTime workoutEndDateTime;
    private final ArrayList<Exercise> exercises = new ArrayList<>();
    private Exercise currentExercise = null;

    public Workout(String workoutName, int duration) {
        this.workoutName = workoutName;
        this.duration = duration;
    }

    public Workout(String workoutName, LocalDateTime workoutStartDateTime) {
        this.workoutName = workoutName;
        this.workoutStartDateTime = workoutStartDateTime;
    }

    public Workout(String workoutName, LocalDateTime workoutStartDateTime, LocalDateTime workoutEndDateTime) {
        this.workoutName = workoutName;
        this.workoutStartDateTime = workoutStartDateTime;
        this.workoutEndDateTime = workoutEndDateTime;
        this.duration = calculateDuration();
    }


    public String getWorkoutName() {
        return workoutName;
    }

    public void setWorkoutName(String workoutName) {
        this.workoutName = workoutName;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * Adds an exercise to this workout and sets it as the current exercise.
     *
     * @param exercise The exercise to add.
     */
    public void addExercise(Exercise exercise) {
        exercises.add(exercise);
        currentExercise = exercise;
    }

    /**
     * Returns the list of exercises in this workout.
     *
     * @return List of exercises.
     */
    public ArrayList<Exercise> getExercises() {
        return exercises;
    }

    /**
     * Returns the current (most recently added) exercise.
     *
     * @return The current exercise, or null if none exists.
     */
    public Exercise getCurrentExercise() {
        return currentExercise;
    }

    public LocalDateTime getWorkoutStartDateTime() {
        return workoutStartDateTime;
    }

    public void setWorkoutStartDateTime(LocalDateTime workoutStartDateTime) {
        this.workoutStartDateTime = workoutStartDateTime;
    }

    public LocalDateTime getWorkoutEndDateTime() {
        return workoutEndDateTime;
    }

    public String getWorkoutDateString(){
        return formatWorkoutDate(workoutStartDateTime);
    }
    /**
     * Returns a formatted date string such as "Monday 30th of June"
     *
     * @param dateTime the LocalDateTime to format
     * @return the formatted date string
     */
    private static String formatWorkoutDate(LocalDateTime dateTime) {
        String dayOfWeek = dateTime.getDayOfWeek()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        int dayOfMonth = dateTime.getDayOfMonth();
        String month = dateTime.getMonth()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        String suffix = getDaySuffix(dayOfMonth);

        return String.format("%s %d%s of %s", dayOfWeek, dayOfMonth, suffix, month);
    }

    private static String getDaySuffix(int day) {
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

    public void setWorkoutEndDateTime(LocalDateTime workoutEndDateTime) {
        this.workoutEndDateTime = workoutEndDateTime;
    }

    public int calculateDuration() {
        if (workoutStartDateTime != null && workoutEndDateTime != null) {
            return (int) Duration.between(workoutStartDateTime, workoutEndDateTime).toMinutes();
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Workout Name: " + workoutName + ", Duration: " + duration + " has been created and saved.";
    }
}
