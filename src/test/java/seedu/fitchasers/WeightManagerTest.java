package seedu.fitchasers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WeightManagerTest {

    private Person testUser;
    private WeightManager manager;

    @BeforeEach
    void setUp() {
        testUser = new Person("TestUser");
        manager = new WeightManager(testUser);
    }

    @Test
    void addWeightValidInput() {
        manager.addWeight("/add_weight w/70.5 d/17/10/25");
        List<WeightRecord> history = testUser.getWeightHistory();
        assertEquals(1, history.size());
        assertEquals(70.5, history.get(0).getWeight());
        assertEquals(LocalDate.parse("17/10/25", DateTimeFormatter.ofPattern("dd/MM/yy")),
                history.get(0).getDate());
    }

    @Test
    void addWeightInvalidWeight() {
        manager.addWeight("/add_weight w/abc d/17/10/25");
        assertEquals(0, testUser.getWeightHistory().size());
    }

    @Test
    void addWeightInvalidDate() {
        manager.addWeight("/add_weight w/70.5 d/2025-10-17");
        assertEquals(0, testUser.getWeightHistory().size());
    }

    @Test
    void addWeightMissingWeight() {
        manager.addWeight("/add_weight w/ d/17/10/25");
        assertEquals(0, testUser.getWeightHistory().size());
    }

    @Test
    void addWeightMissingDate() {
        manager.addWeight("/add_weight w/70.5 d/");
        assertEquals(0, testUser.getWeightHistory().size());
    }

    @Test
    void addWeightExtraSpaces() {
        manager.addWeight(" /add_weight w/  72.0   d/ 17/10/25 ");
        List<WeightRecord> history = testUser.getWeightHistory();
        assertEquals(1, history.size());
        assertEquals(72.0, history.get(0).getWeight());
    }

    @Test
    void addWeightMultipleRecords() {
        manager.addWeight("/add_weight w/70.0 d/17/10/25");
        manager.addWeight("/add_weight w/71.0 d/18/10/25");
        assertEquals(2, testUser.getWeightHistory().size());
    }

    @Test
    void addWeightInvalidFormat() {
        manager.addWeight("add_weight w70.0 d18/10/25");
        assertEquals(0, testUser.getWeightHistory().size());
    }

    @Test
    void addWeightNegativeWeight() {
        manager.addWeight("/add_weight w/-5 d/17/10/25");
        List<WeightRecord> history = testUser.getWeightHistory();
        assertEquals(1, history.size());
        assertEquals(-5.0, history.get(0).getWeight());
    }

    @Test
    void addWeightLargeWeight() {
        manager.addWeight("/add_weight w/500 d/17/10/25");
        List<WeightRecord> history = testUser.getWeightHistory();
        assertEquals(1, history.size());
        assertEquals(500.0, history.get(0).getWeight());
    }

    @Test
    void addWeightDecimalWeight() {
        manager.addWeight("/add_weight w/72.75 d/17/10/25");
        List<WeightRecord> history = testUser.getWeightHistory();
        assertEquals(1, history.size());
        assertEquals(72.75, history.get(0).getWeight());
    }

    @Test
    void addWeightZeroWeight() {
        manager.addWeight("/add_weight w/0 d/17/10/25");
        List<WeightRecord> history = testUser.getWeightHistory();
        assertEquals(1, history.size());
        assertEquals(0.0, history.get(0).getWeight());
    }

    @Test
    void addWeightEdgeDate() {
        manager.addWeight("/add_weight w/60 d/01/01/00");
        assertEquals(1, testUser.getWeightHistory().size());
    }

    @Test
    void addWeightOnlyCommand() {
        manager.addWeight("/add_weight");
        assertEquals(0, testUser.getWeightHistory().size());
    }
}
