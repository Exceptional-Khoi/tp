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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorkoutManagerTest {
    //methodName_whatIsTheConditionYouAreTesting_Outcome(If 2 Paths Can Exclude)
    private WorkoutManager manager;

    @BeforeEach
    void setup() throws FileNonexistent, IOException {
        Tagger tagger = new DefaultTagger();
        FileHandler fileHandler = new FileHandler();

        // Ensure the FileHandler index and current-month file exist (mirror app "first boot" behaviour).
        // This prevents addWorkout from rejecting creations due to missing first-boot month in CI.
        fileHandler.initIndex(); // build on-disk month index (no-op if already present)
        YearMonth now = YearMonth.now();
        fileHandler.saveMonthList(now, new ArrayList<>()); // create/overwrite current month file

        manager = new WorkoutManager(tagger, fileHandler);

        // Use today's date for the initial workout so tests are robust to CI date
        java.time.LocalDate today = java.time.LocalDate.now();
        String dateStr = today.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yy"));
        // choose a time that's safely not in the future
        String timeStr = "1400";
        manager.addWorkout("n/TestWorkout d/" + dateStr + " t/" + timeStr);
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

            String endArgs = String.format("d/%s t/%s",
                    end.format(DateTimeFormatter.ofPattern("dd/MM/yy")),
                    end.format(DateTimeFormatter.ofPattern("HHmm")));

            manager.endWorkout(endArgs);
        }

        // create second workout on the same day but at a different time to avoid future-date prompt
        java.time.LocalDate today = java.time.LocalDate.now();
        String dateStr = today.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yy"));
        manager.addWorkout("n/run d/" + dateStr + " t/0730");

        assertEquals(2, manager.getWorkouts().size());
        assertEquals("run", manager.getWorkouts().get(1).getWorkoutName());
    }


    @Test
    void deleteWorkout_acessingDeletedWorkout_indexOutOfBoundsException() throws IOException {
        manager.deleteWorkout("run");
        assertThrows(IndexOutOfBoundsException.class,
                ()-> manager.getWorkouts().get(1));
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
