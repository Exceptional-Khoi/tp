package seedu.fitchasers.workouts;

import seedu.fitchasers.ui.UI;

import java.time.Duration;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

//@@author Exceptional-Khoi
/**
 * Represents a workout session consisting of one or more exercises.
 * <p>
 * Each workout has a name, start and end times, duration, and associated tags.
 * Tags can be added manually by the user or generated automatically based on
 * workout content. The class also provides methods for managing exercises and
 * computing workout duration.
 */
public class Workout {
    private static final UI ui = new UI();
    private final ArrayList<Exercise> exercises = new ArrayList<>();
    private String workoutName;
    private int duration = 0;
    private LocalDateTime workoutStartDateTime = null;
    private LocalDateTime workoutEndDateTime = null;
    private Exercise currentExercise = null;
    private Set<String> manualTags = new LinkedHashSet<>(); // tags the user edits manually
    private Set<String> autoTags = new LinkedHashSet<>();

    /**
     * Constructs a {@code Workout} with the specified name and duration.
     *
     * @param workoutName The name of the workout.
     * @param duration The duration of the workout in minutes.
     */
    public Workout(String workoutName, int duration) {
        this.workoutName = workoutName;
        this.duration = duration;
    }

    /**
     * Constructs a {@code Workout} with the specified name and start time.
     *
     * @param workoutName The name of the workout.
     * @param workoutStartDateTime The start time of the workout.
     */
    public Workout(String workoutName, LocalDateTime workoutStartDateTime) {
        this.workoutName = workoutName;
        this.workoutStartDateTime = workoutStartDateTime;
    }

    /**
     * Constructs a {@code Workout} with the specified name, start time, and end time.
     * Automatically calculates the duration based on the time difference.
     *
     * @param workoutName The name of the workout.
     * @param workoutStartDateTime The start time of the workout.
     * @param workoutEndDateTime The end time of the workout.
     */
    public Workout(String workoutName, LocalDateTime workoutStartDateTime, LocalDateTime workoutEndDateTime) {
        this.workoutName = workoutName;
        this.workoutStartDateTime = workoutStartDateTime;
        this.workoutEndDateTime = workoutEndDateTime;
        this.duration = calculateDuration();
    }

    /**
     * Returns the set of auto tags.
     * Auto tags are those generated automatically based on workout content or keywords.
     *
     * @return the {@code Set<String>} of auto tags
     */
    public Set<String> getAutoTags() {
        return autoTags;
    }

    /**
     * Sets the manual tags to a defensive copy of the given set.
     * This replaces any existing manual tags with the provided set.
     *
     * @param tags a set of tags to assign as manual tags
     */
    public void setManualTags(Set<String> tags) {
        this.manualTags = new LinkedHashSet<>(tags);
    }

    /**
     * Sets the auto tags to a defensive copy of the given set.
     * This replaces any existing auto tags with the provided set.
     *
     * @param tags a set of tags to assign as auto tags
     */
    public void setAutoTags(Set<String> tags) {
        this.autoTags = new LinkedHashSet<>(tags);
    }

    /**
     * Returns a set containing the union of manual tags
     * and automatically generated (auto) tags for the workout.
     * <p>
     * Manual tags represent user-edited tags, while auto tags
     * are suggested based on workout content or keywords.
     *
     * @return a new {@code Set<String>} containing all manual and auto tags
     */
    public Set<String> getAllTags() {
        Set<String> out = new LinkedHashSet<>(manualTags);

        for (String autoTag : autoTags) {
            if (!manualTags.contains(autoTag)) {
                out.add(autoTag);
            }
        }
        return out;
    }

    /**
     * Returns the set of tags that appear in both manual and auto tag lists.
     *
     * @return A {@code Set<String>} containing conflicting tags.
     */
    public Set<String> getConflictingTags() {
        Set<String> conflicts = new LinkedHashSet<>();
        for (String manualTag : manualTags) {
            if (autoTags.contains(manualTag)) {
                conflicts.add(manualTag);
            }
        }
        return conflicts;
    }

    /**
     * Returns the workout name.
     *
     * @return The workout name.
     */
    public String getWorkoutName() {
        return workoutName;
    }

    /**
     * Sets the workout name.
     *
     * @param workoutName The new name for the workout.
     */
    public void setWorkoutName(String workoutName) {
        this.workoutName = workoutName;
    }

    //@@author bennyy117
    /**
     * Returns the workout duration in minutes.
     *
     * @return The duration of the workout.
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Sets the workout duration in minutes.
     *
     * @param duration The duration to set.
     */
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

    /**
     * Returns the start date and time of this workout.
     *
     * @return The workout start time.
     */
    public LocalDateTime getWorkoutStartDateTime() {
        return workoutStartDateTime;
    }

    /**
     * Sets the start date and time of this workout.
     *
     * @param workoutStartDateTime The start time to set.
     */
    public void setWorkoutStartDateTime(LocalDateTime workoutStartDateTime) {
        this.workoutStartDateTime = workoutStartDateTime;
    }

    /**
     * Returns the end date and time of this workout.
     *
     * @return The workout end time.
     */
    public LocalDateTime getWorkoutEndDateTime() {
        return workoutEndDateTime;
    }

    /**
     * Returns the formatted workout date string, e.g. "Monday 30th of June".
     *
     * @return The formatted date string.
     */
    public String getWorkoutDateString() {
        return formatWorkoutDate(workoutStartDateTime);
    }

    /**
     * Returns a formatted date string such as "Monday 30th of June"
     *
     * @param dateTime the LocalDateTime to format
     * @return the formatted date string
     */
    private static String formatWorkoutDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "Unknown date";
        }
        String dayOfWeek = dateTime.getDayOfWeek()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        int dayOfMonth = dateTime.getDayOfMonth();
        String month = dateTime.getMonth()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        String suffix = ui.getDaySuffix(dayOfMonth);

        return String.format("%s %d%s of %s", dayOfWeek, dayOfMonth, suffix, month);
    }

    /**
     * Sets the end date and time of this workout.
     *
     * @param workoutEndDateTime The end time to set.
     */
    public void setWorkoutEndDateTime(LocalDateTime workoutEndDateTime) {
        this.workoutEndDateTime = workoutEndDateTime;
    }

    /**
     * Calculates and returns the workout duration in minutes
     * based on the start and end times.
     *
     * @return The duration in minutes, or 0 if the times are incomplete.
     */
    public int calculateDuration() {
        if (workoutStartDateTime != null && workoutEndDateTime != null) {
            return (int) Duration.between(workoutStartDateTime, workoutEndDateTime).toMinutes();
        }
        return 0;
    }

    /**
     * Returns a summary string describing the workout.
     *
     * @return A string representation of the workout.
     */
    @Override
    public String toString() {
        return "Workout Name: " + workoutName + ", Duration: " + duration + " has been created and saved.";
    }
}
