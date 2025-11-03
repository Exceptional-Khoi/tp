package seedu.fitchasers.workouts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.fitchasers.exceptions.FileNonexistent;
import seedu.fitchasers.storage.FileHandler;
import seedu.fitchasers.ui.Parser;
import seedu.fitchasers.ui.UI;
import seedu.fitchasers.tagger.DefaultTagger;
import seedu.fitchasers.tagger.Tagger;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

//@@author Kart04
/**
 * Unit tests for {@link WorkoutManager}.
 * Compatible with the refactored Parser that has a no-arg constructor
 * and static UI/Scanner.
 */
class WorkoutManagerTest {

    private WorkoutManager manager;

    /** UI stub that suppresses console output from WorkoutManager itself. */
    private static class SilentUI extends UI {
        @Override public void printLeftHeader() { /* no output */ }
        @Override public void showMessage(String message) { /* no output */ }
        @Override public void showError(String error) { /* no output */ }
    }

    /**
     * Parser stub that always confirms and never reads real input.
     * Since Parser now has a no-arg constructor and internal static UI,
     * we just override the public methods to bypass prompts/printing.
     */
    private static class AlwaysYesParser extends Parser {
        @Override public Boolean confirmationMessage() { return true; }
        @Override public String readCommand() { return ""; }
        // If you ever need to bypass weight/name prompts in other tests,
        // override enterName()/enterWeight(...) similarly.
    }

    @BeforeEach
    void setup() throws Exception {
        Tagger tagger = new DefaultTagger();
        FileHandler fileHandler = new FileHandler();
        manager = new WorkoutManager(tagger, fileHandler);

        // 1) Inject a silent UI so manager.showMessage()/showError() don't print
        UI silentUi = new SilentUI();
        setField(manager, "ui", silentUi);

        // 2) Inject a Parser stub that always returns confirmation = true
        //    (only if WorkoutManager has a 'parser' field)
        try {
            Parser parser = new AlwaysYesParser();
            setField(manager, "parser", parser);
        } catch (NoSuchFieldException ignore) {
            // Safe to ignore if WorkoutManager does not yet have a 'parser' field.
            // In that case, confirmation may still be handled through UI or another path.
        }

        // 3) Create an initial valid workout using today's date (CI-friendly)
        var today = java.time.LocalDate.now();
        String dateStr = today.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yy"));
        String timeStr = "1400";
        manager.addWorkout("/create_workout n/TestWorkout d/" + dateStr + " t/" + timeStr);
    }

    /** Reflection helper to set private fields on the SUT. */
    private static void setField(Object target, String fieldName, Object value)
            throws NoSuchFieldException, IllegalAccessException {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
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
    void addWorkout_validInput_createsAdditionalWorkoutAfterEndingPrevious() throws Exception {
        Field field = manager.getClass().getDeclaredField("currentWorkout");
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

        var today = java.time.LocalDate.now();
        String dateStr = today.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yy"));
        manager.addWorkout("/create_workout n/run d/" + dateStr + " t/2030");

        assertEquals(2, manager.getWorkouts().size());
        assertEquals("run", manager.getWorkouts().get(1).getWorkoutName());
    }

    @Test
    void deleteWorkout_accessingDeletedWorkout_throwsIndexOutOfBoundsException()
            throws IOException, FileNonexistent {
        manager.addWorkout("/create_workout n/run d/01/01/25 t/1200");

        // Delete the second workout using index-based deletion (id/2)
        manager.deleteParser("id/2");

        // Accessing index 1 should throw IndexOutOfBoundsException since we only have 1 workout left
        assertThrows(IndexOutOfBoundsException.class,
                () -> manager.getWorkouts().get(1));
    }

    @Test
    void removeWorkout_nonExistingWorkout_printsWorkoutNotFound() throws IOException, FileNonexistent {
        // This test is tricky because the mock UI suppresses output.
        // A better approach would be to check the state of the application.
        // For now, let's ensure the list size doesn't change.
        int initialSize = manager.getWorkouts().size();

        // Try to delete a workout with invalid index (id/99 - doesn't exist)
        manager.deleteParser("id/99");

        // List size should remain the same
        assertEquals(initialSize, manager.getWorkouts().size());
    }
}
