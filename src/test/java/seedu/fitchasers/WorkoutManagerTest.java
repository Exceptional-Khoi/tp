package seedu.fitchasers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorkoutManagerTest {
    //methodName_whatIsTheConditionYouAreTesting_Outcome(If 2 Paths Can Exclude)
    private WorkoutManager manager;

    @BeforeEach
    void setup() {
        Tagger tagger = new DefaultTagger();
        manager = new WorkoutManager(tagger);
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

            String endArgs = String.format("d/%s t/%s",
                    end.format(DateTimeFormatter.ofPattern("dd/MM/yy")),
                    end.format(DateTimeFormatter.ofPattern("HHmm")));

            UI dummyUI = new UI() {
                @Override public boolean confirmationMessage() {
                    return true;
                }
            };
            manager.endWorkout(dummyUI, endArgs);
        }

        manager.addWorkout("n/run d/15/10/25 t/0730");
        
        assertEquals(2, manager.getWorkouts().size());
        assertEquals("run", manager.getWorkouts().get(1).getWorkoutName());
    }


    @Test
    void deleteWorkout_acessingDeletedWorkout_indexOutOfBoundsException() {
        manager.deleteWorkout("run");
        assertThrows(IndexOutOfBoundsException.class,
                ()-> manager.getWorkouts().get(1));
    }

    @Test
    void removeWorkout_nonExistingWorkout_printsWorkoutNotFound() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        manager.deleteWorkout("swim");

        String output = outContent.toString().trim();
        assertTrue(output.contains("Workout not found: swim"));

        System.setOut(System.out); // reset stdout
    }

}
