package seedu.fitchasers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.fitchasers.exceptions.InvalidArgumentInput;
import seedu.fitchasers.storage.FileHandler;
import seedu.fitchasers.ui.UI;
import seedu.fitchasers.workouts.DeleteWorkout;
import seedu.fitchasers.workouts.Workout;
import seedu.fitchasers.workouts.WorkoutManager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit 5 tests for DeleteWorkout without Mockito.
 * Uses simple in-memory fakes for UI, FileHandler, and WorkoutManager.
 */
class DeleteWorkoutTest {

    /** Fake UI that records messages and returns a configurable confirmation. */
    static class FakeUI extends UI {
        public boolean nextConfirm = true; // default: confirm
        final ArrayList<String> messages = new ArrayList<>();

        @Override
        public void showMessage(String s) {
            messages.add(s);
        }

        @Override
        public void displayDetailsOfWorkout(Workout w) {
            messages.add("[DETAILS] " + (w == null ? "(null)" : w.getWorkoutName()));
        }

        @Override
        public Boolean confirmationMessage() {
            return nextConfirm;
        }
    }

    /** Fake FileHandler that keeps per-month lists in memory only. */
    static class FakeFileHandler extends FileHandler {
        final Map<YearMonth, ArrayList<Workout>> store = new HashMap<>();

        static class SaveCall {
            final YearMonth ym;
            final ArrayList<Workout> listCopy;
            SaveCall(YearMonth ym, ArrayList<Workout> listCopy) {
                this.ym = ym;
                this.listCopy = listCopy;
            }


            public Workout getWorkout(int i) {
                return listCopy.get(i);
            }

            public int getSize() {
                return listCopy.size();
            }
        }

        SaveCall lastSave;

        @Override
        public ArrayList<Workout> loadMonthList(YearMonth month) {
            return new ArrayList<>(store.getOrDefault(month, new ArrayList<>()));
        }

        @Override
        public void saveMonthList(YearMonth ym, ArrayList<Workout> list) throws IOException {
            ArrayList<Workout> copy = new ArrayList<>(list); // snapshot for assertions
            store.put(ym, copy);
            lastSave = new SaveCall(ym, copy);
        }
    }

    /** Fake WorkoutManager that exposes/records only what DeleteWorkout uses. */
    static class FakeWorkoutManager extends WorkoutManager {
        YearMonth lastSetMonth = null;
        ArrayList<Workout> lastSetList = null;
        private YearMonth current = YearMonth.now();


        FakeWorkoutManager(FakeFileHandler fhh) throws IOException {
            super(null,fhh);
        }

        @Override
        public YearMonth getCurrentLoadedMonth() {
            return current;
        }

        void setCurrentLoadedMonth(YearMonth ym) {
            this.current = ym;
        }

        @Override
        public void setWorkouts(ArrayList<Workout> monthWorkouts, YearMonth ym) {
            lastSetMonth = ym;
            lastSetList = new ArrayList<>(monthWorkouts);
        }

        void setCreationDate(YearMonth set){
            super.creationDate = set;
        }
    }

    /* ---------------- Test fixtures ---------------- */

    private FakeUI fakeUI;
    private FakeFileHandler fh;
    private FakeWorkoutManager fakeWorkoutManager;
    private DeleteWorkout sut; // System Under Test

    @BeforeEach
    void setup() throws IOException {
        fakeUI = new FakeUI();
        fh = new FakeFileHandler();
        fakeWorkoutManager = new FakeWorkoutManager(fh);
        sut = new DeleteWorkout(fakeUI, fh, fakeWorkoutManager);
        fakeWorkoutManager.setCreationDate(YearMonth.now());
    }

    private static Workout makeWorkout(String name,
                                       int y, int m, int d,
                                       int sh, int sm,
                                       int eh, int em) {
        Workout w = new Workout(name, 1);
        w.setWorkoutName(name);
        w.setWorkoutStartDateTime(LocalDateTime.of(y, m, d, sh, sm));
        w.setWorkoutEndDateTime(LocalDateTime.of(y, m, d, eh, em));
        return w;
    }

    /* ---------------- Tests ---------------- */

    @Test
    void execute_confirmedDeletion_whenCurrentMonth() throws Exception {
        YearMonth ym = YearMonth.now();
        fakeWorkoutManager.setCurrentLoadedMonth(ym);
        // Order in file doesn’t matter; DeleteWorkout sorts by end then start (ascending in your code)
        fh.store.put(ym, new ArrayList<>(List.of(
                makeWorkout("A", 2025,11,1,10,0,11,0),
                makeWorkout("B", 2025,11,2,10,0,12,0),
                makeWorkout("C", 2025,11,3, 9,0, 9,30)
        )));
        fakeUI.nextConfirm = true;

        // Delete display index 2 (after ascending sort by end/start, that points to "B")
        sut.execute("id/2");

        // Saved once with list missing "B"
        assertEquals(2, fh.lastSave.getSize());
        FakeFileHandler.SaveCall sc = fh.lastSave;
        assertEquals(ym, sc.ym);
        List<String> names = sc.listCopy.stream().map(Workout::getWorkoutName).toList();
        assertEquals(List.of("C", "A"), names);
        // In-memory update because deleting current month
        assertEquals(ym, fakeWorkoutManager.lastSetMonth);
        assertNotNull(fakeWorkoutManager.lastSetList);
        assertEquals(List.of("C", "A"), fakeWorkoutManager.lastSetList.stream().map(Workout::getWorkoutName).toList());

        // UI showed details and success
        assertTrue(fakeUI.messages.stream().anyMatch(s -> s.startsWith("[DETAILS] ")));
        assertTrue(fakeUI.messages.stream().anyMatch(s -> s.contains("Deleted workout")));
    }

    @Test
    void execute_cancelledDeletion_doesNothing() throws Exception {
        YearMonth ym =YearMonth.of(2025, 11);
        fh.store.put(ym, new ArrayList<>(List.of(
                makeWorkout("OnlyOne", 2025,11,1,10,0,11,0)
        )));

        //Cancel deletion
        fakeUI.nextConfirm = false;
        sut.execute("id/1 m/11");
        assertNull(fakeWorkoutManager.lastSetMonth, "Should have been canceled so no lastSetMonth");
        // No save, no in-memory update
        assertEquals("OnlyOne" , fh.store.get(ym).get(0).getWorkoutName());
    }

    @Test
    void execute_invalidIndex_noSave() throws Exception {
        YearMonth ym = YearMonth.of(2025, 10);
        fh.store.put(ym, new ArrayList<>(List.of(
                makeWorkout("OnlyOne", 2025,10,1,10,0,11,0)
        )));
        fakeUI.nextConfirm = true; // shouldn’t be asked, but harmless

        sut.execute("id/3 m/10");

        // No save
        assertNull(fh.lastSave);
        // Error shown
        assertTrue(fakeUI.messages.stream().anyMatch(s -> s.startsWith("Invalid workout ID")));
        assertEquals(1, fh.store.get(ym).size());           // unchanged
        assertEquals("OnlyOne", fh.store.get(ym).get(0).getWorkoutName());

    }

    @Test
    void execute_emptyMonth_noSave() throws Exception {
        YearMonth ym = YearMonth.of(2025, 10);
        // No data added -> empty
        sut.execute("id/1 m/10");

        assertTrue(fakeUI.messages.stream().anyMatch(s -> s.equals("No workouts found for 2025-10.")));
        assertNull(fh.lastSave);
    }

    @Test
    void execute_differentCurrentMonth_noInMemoryUpdate() throws Exception {
        YearMonth target = YearMonth.of(2025, 10); // ym/10/26 (strict MM/YY → 2026)
        fakeWorkoutManager.setCurrentLoadedMonth(YearMonth.of(2026, 10)); // different than target

        fh.store.put(target, new ArrayList<>(List.of(
                makeWorkout("Target", 2025,10,1,10,0,10,30)
        )));
        fakeUI.nextConfirm = true;

        sut.execute("id/1 ym/10/25");

        // Saved in 2026-10
        assertNotNull(fh.lastSave);
        assertEquals(target, fh.lastSave.ym);

        // No in-memory update because current month != target month
        assertNull(fakeWorkoutManager.lastSetMonth);
    }

    @Test
    void execute_badArgs_throwInvalidArgumentInput() {
        // Missing id
        assertThrows(InvalidArgumentInput.class, () -> sut.execute("m/10"));

        // Malformed ym (you said delete expects ym/MM/YY, so this is invalid)
        assertThrows(InvalidArgumentInput.class, () -> sut.execute("id/1 ym/2024/10"));
    }

}

