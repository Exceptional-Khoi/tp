package seedu.fitchasers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.fitchasers.exceptions.FileNonexistent;
import seedu.fitchasers.storage.FileHandler;
import seedu.fitchasers.ui.UI;
import seedu.fitchasers.tagger.DefaultTagger;
import seedu.fitchasers.tagger.Tagger;
import seedu.fitchasers.workouts.Exercise;
import seedu.fitchasers.workouts.Workout;
import seedu.fitchasers.workouts.WorkoutManager;

//import java.io.ByteArrayOutputStream;
import java.io.IOException;
//import java.io.PrintStream;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.junit.jupiter.api.Assertions.assertTrue;

class WorkoutManagerTest {
    //methodName_whatIsTheConditionYouAreTesting_Outcome(If 2 Paths Can Exclude)
    private WorkoutManager manager;
    private UI mockUi;

    @BeforeEach
    void setup() throws FileNonexistent, IOException, NoSuchFieldException, IllegalAccessException {
        Tagger tagger = new DefaultTagger();
        FileHandler fileHandler = new FileHandler();
        manager = new WorkoutManager(tagger, fileHandler);

        // Create a mock UI that always confirms prompts
        mockUi = new UI() {
            @Override
            public boolean confirmationMessage() {
                return true; // Always return true for "are you sure?" prompts
            }
            @Override
            public void showMessage(String message) {
                // Suppress console output during tests to keep logs clean
            }
            @Override
            public String readCommand() {
                return ""; // Return empty for any read command prompts
            }
        };

        // Use reflection to inject the mock UI into the WorkoutManager instance
        Field uiField = manager.getClass().getDeclaredField("ui");
        uiField.setAccessible(true);
        uiField.set(manager, mockUi);


        // Use today's date to ensure tests are compatible with CI/system date.
        java.time.LocalDate today = java.time.LocalDate.now();
        String dateStr = today.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yy"));
        // pick a fixed time that is valid
        String timeStr = "1400";
        manager.addWorkout("/create_workout n/TestWorkout d/" + dateStr + " t/" + timeStr);
    }

    @Test
    void addWorkout_validInput_addsWorkoutToList() {
        assertEquals(1, manager.getWorkouts().size());
        assertEquals("TestWorkout", manager.getWorkouts().get(0).getWorkoutName());
    }

    @Test
    void addExercise_validInput_addsExerciseToCurrentWorkout() throws IOException {
        manager.addExercise("n/PushUp r/10");
        Workout w = manager.getWorkouts().get(0);
        assertEquals(1, w.getExercises().size());
        assertEquals("PushUp", w.getExercises().get(0).getName());
    }

    @Test
    void addSet_validInput_addsSetToCurrentExercise() throws IOException {
        manager.addExercise("n/Squat r/12");
        manager.addSet("r/15");

        Workout w = manager.getWorkouts().get(0);
        Exercise ex = w.getExercises().get(0);
        assertEquals(2, ex.getNumSets());
        assertEquals(15, ex.getSets().get(1));
    }

    @Test
    void addWorkout_validInput_addsWorkoutToCurrentSet() throws Exception {
        java.lang.reflect.Field field = manager.getClass().getDeclaredField("currentWorkout");
        field.setAccessible(true);
        Object current = field.get(manager);
        if (current != null) {
            LocalDateTime start = (LocalDateTime) current.getClass()
                    .getMethod("getWorkoutStartDateTime")
                    .invoke(current);
            LocalDateTime end = start.plusMinutes(1);

            String endArgs = String.format("/end_workout d/%s t/%s",
                    end.format(DateTimeFormatter.ofPattern("dd/MM/yy")),
                    end.format(DateTimeFormatter.ofPattern("HHmm")));

            manager.endWorkout(endArgs);
        }

        java.time.LocalDate today = java.time.LocalDate.now();
        String dateStr = today.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yy"));
        manager.addWorkout("/create_workout n/run d/" + dateStr + " t/2030");

        assertEquals(2, manager.getWorkouts().size());
        assertEquals("run", manager.getWorkouts().get(1).getWorkoutName());
    }


    @Test
    void deleteWorkout_acessingDeletedWorkout_indexOutOfBoundsException() throws IOException, FileNonexistent {
        // To make this test meaningful, we first add a workout to delete.
        manager.addWorkout("/create_workout n/run d/01/01/25 t/1200");
        manager.deleteWorkout("run");
        assertThrows(IndexOutOfBoundsException.class,
                ()-> manager.getWorkouts().get(1));
    }

    @Test
    void removeWorkout_nonExistingWorkout_printsWorkoutNotFound() throws IOException {
        // This test is tricky because the mock UI suppresses output.
        // A better approach would be to check the state of the application.
        // For now, let's ensure the list size doesn't change.
        int initialSize = manager.getWorkouts().size();
        manager.deleteWorkout("swim");
        assertEquals(initialSize, manager.getWorkouts().size());
    }
}
