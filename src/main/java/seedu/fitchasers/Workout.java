package seedu.fitchasers;

import java.time.Duration;
import java.util.ArrayList;
import java.time.LocalDateTime;

/**
 * Represents a workout session containing a name, duration, start/end times, and a list of exercises.
 */
public class Workout{
    private String workoutName;
    private int duration = 0;
    private LocalDateTime workoutStartDateTime;
    private LocalDateTime workoutEndDateTime;
    private final ArrayList<Exercise> exercises = new ArrayList<>();
    private Exercise lastAddedExercise = null;

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

    public Exercise getLastAddedExercise() {
        return lastAddedExercise;
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

    public void addExercise(Exercise exercise) {
        exercises.add(exercise);
        lastAddedExercise = exercise;
    }

    public java.util.List<Exercise> getExercises() {
        return exercises;
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
