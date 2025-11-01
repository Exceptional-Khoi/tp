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

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class WorkoutManagerTest {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HHmm");
    private WorkoutManager manager;
    /** Use the manager's currentLoadedMonth so dates always match CI environment */
    private static String dateInLoadedMonth(WorkoutManager m) {
        try {
            Field f = m.getClass().getDeclaredField("currentLoadedMonth");
            f.setAccessible(true);
            YearMonth ym = (YearMonth) f.get(m);
            LocalDate d = ym.atDay(1); // 01 of that month
            return d.format(DATE_FMT);
        } catch (Exception e) {
            // Fallback: today (still stable in most envs)
            return LocalDate.now().format(DATE_FMT);
        }
    }

    /** If there's an active workout, end it 1 minute later so it gets saved to the list */
    private static void endIfActive(WorkoutManager m) throws Exception {
        Field f = m.getClass().getDeclaredField("currentWorkout");
        f.setAccessible(true);
        Object cw = f.get(m);
        if (cw != null) {
            LocalDateTime start = (LocalDateTime) cw.getClass()
                    .getMethod("getWorkoutStartDateTime").invoke(cw);
            LocalDateTime end = start.plusMinutes(1);
            String args = "d/" + end.format(DATE_FMT) + " t/" + end.format(TIME_FMT);
            m.endWorkout(args);
        }
    }

    /** Get the active (current) workout via reflection to assert exercises/sets without persisting */
    private static Workout current(WorkoutManager m) throws Exception {
        Field f = m.getClass().getDeclaredField("currentWorkout");
        f.setAccessible(true);
        return (Workout) f.get(m);
    }

    @BeforeEach
    void setup() throws FileNonexistent, IOException {
        Tagger tagger = new DefaultTagger();
        FileHandler fileHandler = new FileHandler();
        manager = new WorkoutManager(tagger, fileHandler);

        String d = dateInLoadedMonth(manager);
        manager.addWorkout("n/TestWorkout d/" + d + " t/1400");
    }

    @Test
    void addWorkout_validInput_addsWorkoutToList() throws Exception {
        // Persist first so it appears in the stored list
        endIfActive(manager);

        assertEquals(1, manager.getWorkouts().size());
        assertEquals("TestWorkout", manager.getWorkouts().get(0).getWorkoutName());
    }

    @Test
    void addExercise_validInput_addsExerciseToCurrentWorkout() throws Exception {
        manager.addExercise("n/PushUp r/10");

        // Check the active workout (not the saved list)
        Workout w = current(manager);
        assertNotNull(w, "Expected an active workout after addWorkout");
        assertEquals(1, w.getExercises().size());
        assertEquals("PushUp", w.getExercises().get(0).getName());
    }

    @Test
    void addSet_validInput_addsSetToCurrentExercise() throws Exception {
        manager.addExercise("n/Squat r/12");
        manager.addSet("r/15");

        // Still in the active workout
        Workout w = current(manager);
        assertNotNull(w, "Expected an active workout after addWorkout");
        Exercise ex = w.getExercises().get(0);
        assertEquals(2, ex.getNumSets());
        assertEquals(15, ex.getSets().get(1));
    }

    @Test
    void addWorkout_validInput_addsWorkoutToCurrentSet() throws Exception {
        // End the first workout so it's saved into the list
        endIfActive(manager);

        String d = dateInLoadedMonth(manager);
        manager.addWorkout("n/run d/" + d + " t/0730");

        // End the second one too so list has 2
        endIfActive(manager);

        assertEquals(2, manager.getWorkouts().size());
        assertEquals("run", manager.getWorkouts().get(1).getWorkoutName());
    }

    @Test
    void deleteWorkout_acessingDeletedWorkout_indexOutOfBoundsException() throws IOException {
        manager.deleteWorkout("run");
        assertThrows(IndexOutOfBoundsException.class,
                () -> manager.getWorkouts().get(1));
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