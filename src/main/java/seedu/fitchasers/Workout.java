package seedu.fitchasers;

import java.util.ArrayList;
import java.time.LocalDateTime;

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

    @Override
    public String toString() {
        return "Workout Name: " + workoutName + ", Duration: " + duration + " has been created and saved.";
    }
}
