package seedu.fitchasers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.fitchasers.workouts.Exercise;
import seedu.fitchasers.workouts.Workout;

import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;


class WorkoutTest {
    private Workout workout;

    @BeforeEach
    void setUp() {
        workout = new Workout("Pushups", 20);
    }

    @Test
    void constructor_nameAndDuration_setsFieldsCorrectly() {
        Workout w = new Workout("Pushups", 30);
        assertEquals("Pushups", w.getWorkoutName());
        assertEquals(30, w.getDuration());
        assertTrue(w.getExercises().isEmpty());
    }


    @Test
    void constructor_nameAndStartDate_setsFieldsCorrectly() {
        LocalDateTime start = LocalDateTime.of(2025, 10, 14, 12, 0);
        Workout w = new Workout("Cycling", start);
        assertEquals("Cycling", w.getWorkoutName());
        assertEquals(start, w.getWorkoutStartDateTime());
        assertNull(w.getWorkoutEndDateTime());
    }

    @Test
    void constructor_allFields_setsDurationAndEndCorrectly() {
        LocalDateTime start = LocalDateTime.of(2025, 10, 14, 12, 0);
        LocalDateTime end = LocalDateTime.of(2025, 10, 14, 12, 30);
        Workout w = new Workout("Run", start, end);
        assertEquals(30, w.getDuration()); // 30 mins
        assertEquals(start, w.getWorkoutStartDateTime());
        assertEquals(end, w.getWorkoutEndDateTime());
    }

    @Test
    void setWorkoutName_andGet_returnsUpdatedName() {
        workout.setWorkoutName("Situps");
        assertEquals("Situps", workout.getWorkoutName());
    }

    @Test
    void setDuration_andGet_returnsUpdatedDuration() {
        workout.setDuration(45);
        assertEquals(45, workout.getDuration());
    }

    @Test
    void addExercise_addsExerciseToListAndUpdatesCurrent() {
        Exercise e = new Exercise("Squat", 10);
        workout.addExercise(e);
        assertEquals(1, workout.getExercises().size());
        assertEquals(e, workout.getCurrentExercise());
    }

    @Test
    void getExercises_returnsCorrectList() {
        Exercise e1 = new Exercise("Squat", 10);
        Exercise e2 = new Exercise("Jump", 5);
        workout.addExercise(e1);
        workout.addExercise(e2);
        assertEquals(2, workout.getExercises().size());
        assertTrue(workout.getExercises().contains(e1));
        assertTrue(workout.getExercises().contains(e2));
    }

    @Test
    void calculateDuration_withStartAndEnd_returnsCorrectDuration() {
        LocalDateTime start = LocalDateTime.of(2025, 10, 14, 12, 0);
        LocalDateTime end = LocalDateTime.of(2025, 10, 14, 13, 0);
        workout.setWorkoutStartDateTime(start);
        workout.setWorkoutEndDateTime(end);
        assertEquals(60, workout.calculateDuration());
    }

    @Test
    void calculateDuration_withMissingEnd_returnsZero() {
        LocalDateTime start = LocalDateTime.of(2025, 10, 14, 12, 0);
        workout.setWorkoutStartDateTime(start);
        workout.setWorkoutEndDateTime(null);
        assertEquals(0, workout.calculateDuration());
    }

    @Test
    void calculateDuration_withMissingStart_returnsZero() {
        workout.setWorkoutStartDateTime(null);
        workout.setWorkoutEndDateTime(LocalDateTime.of(2025, 10, 14, 12, 30));
        assertEquals(0, workout.calculateDuration());
    }

    @Test
    void toString_returnsProperFormat() {
        workout.setWorkoutName("Pushups");
        workout.setDuration(15);
        String result = workout.toString();
        assertTrue(result.contains("Workout Name: Pushups"));
        assertTrue(result.contains("Duration: 15"));
    }

    //edge case
    @Test
    void setNegativeDuration_isAcceptedAsStored() {
        workout.setDuration(-10);
        assertEquals(-10, workout.getDuration()); // as per your implementation
    }


}
