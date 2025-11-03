package seedu.fitchasers.workouts;

import seedu.fitchasers.ui.UI;

import java.time.Duration;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

//@@author ZhongBaode
/**
 * Represents a workout session containing a name, duration, start/end times, and a list of exercises.
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

    /**
     * Returns a defensive copy of the set of manual tags.
     * Manual tags are those explicitly assigned or edited by the user.
     *
     * @return a new {@code Set<String>} containing the manual tags
     */
    public Set<String> getManualTags() {
        return new LinkedHashSet<>(manualTags);
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
     *
     * Manual tags represent user-edited tags, while auto tags
     * are suggested based on workout content or keywords.
     *
     * @return a new {@code Set<String>} containing all manual and auto tags
     */
    public Set<String> getAllTags() {
        //return new LinkedHashSet<>(manualTags);
        Set<String> out = new LinkedHashSet<>(manualTags);

        // Only add autoTags that DON'T conflict with manualTags
        for (String autoTag : autoTags) {
            if (!manualTags.contains(autoTag)) {
                out.add(autoTag);
            }
        }
        return out;
    }

    public Set<String> getConflictingTags() {
        Set<String> conflicts = new LinkedHashSet<>();
        for (String manualTag : manualTags) {
            if (autoTags.contains(manualTag)) {
                conflicts.add(manualTag);
            }
        }
        return conflicts;
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
