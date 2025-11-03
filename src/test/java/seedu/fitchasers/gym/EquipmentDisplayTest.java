package seedu.fitchasers.gym;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;

public class EquipmentDisplayTest {

    @Test
    void testShowEquipmentForSingleGym_formatting() {
        Gym gym = new Gym("Test Gym");
        Machine m1 = new Machine("Deadlift Platform", List.of("legs", "posterior-chain"));
        Machine m2 = new Machine("Bench Press", List.of("chest", "strength"));

        gym.addMachine(m1);
        gym.addMachine(m2);

        String result = EquipmentDisplay.showEquipmentForSingleGym(gym);
        assertEquals("Test Gym", result);
    }

    @Test
    void testShowEquipmentForSingleGym_emptyMachines() {
        Gym gym = new Gym("Empty Gym");

        String result = EquipmentDisplay.showEquipmentForSingleGym(gym);
        assertEquals("Empty Gym", result);
    }

    @Test
    void testSuggestGymsForExercise_emptyGymList_returnsEmpty() {
        Set<String> gyms = EquipmentDisplay.suggestGymsForExercise(List.of(), "n/bench");
        assertTrue(gyms.isEmpty());
    }

    @Test
    void testSuggestGymsForExercise_noMatch_returnsEmpty() {
        Gym gym = new Gym("Gym1");
        Machine m = new Machine("Treadmill", List.of("cardio"));
        gym.addMachine(m);

        Set<String> gyms = EquipmentDisplay.suggestGymsForExercise(List.of(gym), "n/bench");
        assertTrue(gyms.isEmpty());
    }

    @Test
    void testSuggestGymsForExercise_matchGym_returnsGymName() {
        Gym gym = new Gym("Gym1");
        Machine m = new Machine("Bench Press Machine", List.of("chest", "bench"));
        gym.addMachine(m);

        Set<String> gyms = EquipmentDisplay.suggestGymsForExercise(List.of(gym), "n/bench");
        assertTrue(gyms.contains("Gym1"));
    }

    @Test
    void testSuggestGymsForExercise_caseInsensitiveMatch() {
        Gym gym = new Gym("Gym1");
        Machine m = new Machine("Bench Press Machine", List.of("Chest", "Bench"));
        gym.addMachine(m);

        Set<String> gyms = EquipmentDisplay.suggestGymsForExercise(List.of(gym), "n/BENCH");
        assertTrue(gyms.contains("Gym1"));
    }

    @Test
    void testSuggestGymsForExercise_noExerciseParam_returnsEmpty() {
        Gym gym = new Gym("Gym1");
        Machine m = new Machine("Bench Press Machine", List.of("Chest", "Bench"));
        gym.addMachine(m);

        Set<String> gyms = EquipmentDisplay.suggestGymsForExercise(List.of(gym), "");
        assertTrue(gyms.isEmpty());
    }
}
