package seedu.fitchasers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WorkoutManagerTest {

    private WorkoutManager manager;

    @BeforeEach
    void setup() {
        manager = new WorkoutManager();
        manager.addWorkout("n/TestWorkout d/25/10/25 t/1400");
    }

    @Test
    void addWorkout_validInput_addsWorkoutToList() {
        assertEquals(1, manager.getWorkouts().size());
        assertEquals("TestWorkout", manager.getWorkouts().get(0).getWorkoutName());
    }

    @Test
    void addExercise_validInput_addsExerciseToCurrentWorkout() {
        manager.addExercise("n/PushUp r/10");
        Workout w = manager.getWorkouts().get(0);
        assertEquals(1, w.getExercises().size());
        assertEquals("PushUp", w.getExercises().get(0).getName());
    }

    @Test
    void addSet_validInput_addsSetToCurrentExercise() {
        manager.addExercise("n/Squat r/12");
        manager.addSet("r/15");

        Workout w = manager.getWorkouts().get(0);
        Exercise ex = w.getExercises().get(0);
        assertEquals(2, ex.getNumSets());
        assertEquals(15, ex.getSets().get(1));
    }

}