package seedu.fitchasers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.fitchasers.exceptions.FileNonexistent;
import seedu.fitchasers.storage.FileHandler;
import seedu.fitchasers.tagger.DefaultTagger;
import seedu.fitchasers.tagger.Tagger;
import seedu.fitchasers.workouts.Exercise;
import seedu.fitchasers.workouts.Workout;
import seedu.fitchasers.workouts.WorkoutManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorkoutManagerTest {
    private WorkoutManager manager;

    @BeforeEach
    void setup() throws FileNonexistent, IOException {
        Tagger tagger = new DefaultTagger();
        FileHandler fileHandler = new FileHandler();
        manager = new WorkoutManager(tagger, fileHandler);
        manager.addWorkout("n/TestWorkout d/01/11/25 t/1200");
    }

    @Test
    void addWorkout_validInput_addsWorkoutToList() {
        assertEquals(1, manager.getWorkouts().size());
        assertEquals("TestWorkout", manager.getWorkouts().get(0).getWorkoutName());
    }

    @Test
    void addExercise_validInput_addsExerciseToCurrentWorkout() throws IOException, FileNonexistent {
        manager.endWorkout("d/01/11/25 t/1230"); // End the current workout
        manager.addWorkout("n/TestWorkout2 d/01/11/25 t/1245"); // Start a new workout
        manager.addExercise("n/PushUp r/10");

        Workout w = manager.getWorkouts().get(1); // The second workout (index 1)
        assertEquals(1, w.getExercises().size());
        assertEquals("PushUp", w.getExercises().get(0).getName());
    }

    @Test
    void addSet_validInput_addsSetToCurrentExercise() throws IOException {
        manager.addExercise("n/Squat r/12"); // Add an exercise first
        manager.addSet("r/15");

        Workout w = manager.getWorkouts().get(0);
        Exercise ex = w.getExercises().get(0);
        assertEquals(2, ex.getNumSets()); // Original set + new set
        assertEquals(15, ex.getSets().get(1)); // The added set
    }

    @Test
    void addWorkout_validInput_addsWorkoutToCurrentSet() throws Exception {
        manager.endWorkout("d/01/11/25 t/1230"); // End the current workout

        manager.addWorkout("n/run d/01/11/25 t/1530");

        assertEquals(2, manager.getWorkouts().size());
        assertEquals("run", manager.getWorkouts().get(1).getWorkoutName());
    }

    @Test
    void deleteWorkout_acessingDeletedWorkout_indexOutOfBoundsException() throws IOException, FileNonexistent {
        manager.addWorkout("n/run d/01/11/25 t/1530");
        manager.deleteWorkout("run");
        assertThrows(IndexOutOfBoundsException.class,
                () -> manager.getWorkouts().get(1)); // Accessing deleted workout
    }

    @Test
    void removeWorkout_nonExistingWorkout_printsWorkoutNotFound() throws IOException {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        manager.deleteWorkout("swim");

        String output = outContent.toString().trim();
        assertTrue(output.contains("Workout not found: swim"));

        System.setOut(System.out); // reset stdout
    }
}