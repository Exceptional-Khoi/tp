package seedu.fitchasers;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WeightManagerTest {

    private Person person;
    private WeightManager manager;
    private UI ui;

    @BeforeEach
    void setUp() {
        person = new Person("TestUser");
        manager = new WeightManager(person);
        ui = new UI();
    }

    @Test
    void addWeight_validInput_addsRecord() {
        manager.addWeight("/add_weight w/70.5 d/17/10/25");
        List<WeightRecord> history = person.getWeightHistory();
        assertEquals(1, history.size());
        assertEquals(70.5, history.get(0).getWeight());
        assertEquals(LocalDate.parse("17/10/25", DateTimeFormatter.ofPattern("dd/MM/yy")), history.get(0).getDate());
    }

    @Test
    void addWeight_invalidWeight_showsError() {
        manager.addWeight("/add_weight w/abc d/17/10/25");
        assertEquals(0, person.getWeightHistory().size());
    }

    @Test
    void addWeight_invalidDate_showsError() {
        manager.addWeight("/add_weight w/70.5 d/2025-10-17");
        assertEquals(0, person.getWeightHistory().size());
    }

    @Test
    void addWeight_missingWeight_showsError() {
        manager.addWeight("/add_weight w/ d/17/10/25");
        assertEquals(0, person.getWeightHistory().size());
    }

    @Test
    void addWeight_missingDate_showsError() {
        manager.addWeight("/add_weight w/70.5 d/");
        assertEquals(0, person.getWeightHistory().size());
    }

    @Test
    void addWeight_extraSpaces_trimsCorrectly() {
        manager.addWeight(" /add_weight w/  72.0   d/ 17/10/25 ");
        List<WeightRecord> history = person.getWeightHistory();
        assertEquals(1, history.size());
        assertEquals(72.0, history.get(0).getWeight());
    }

    @Test
    void addWeight_multipleRecords_allAdded() {
        manager.addWeight("/add_weight w/70.0 d/17/10/25");
        manager.addWeight("/add_weight w/71.0 d/18/10/25");
        assertEquals(2, person.getWeightHistory().size());
    }

    @Test
    void addWeight_invalidFormat_noSlash_showsError() {
        manager.addWeight("add_weight w70.0 d18/10/25");
        assertEquals(0, person.getWeightHistory().size());
    }

    @Test
    void addWeight_negativeWeight_accepts() {
        manager.addWeight("/add_weight w/-5 d/17/10/25");
        assertEquals(1, person.getWeightHistory().size());
        assertEquals(-5.0, person.getWeightHistory().get(0).getWeight());
    }

    @Test
    void addWeight_largeWeight_accepts() {
        manager.addWeight("/add_weight w/500 d/17/10/25");
        assertEquals(1, person.getWeightHistory().size());
        assertEquals(500.0, person.getWeightHistory().get(0).getWeight());
    }

    @Test
    void addWeight_invalidDateFormatWithSlashes_showsError() {
        manager.addWeight("/add_weight w/70 d/17-10-25");
        assertEquals(0, person.getWeightHistory().size());
    }

    @Test
    void addWeight_onlyCommand_showsError() {
        manager.addWeight("/add_weight");
        assertEquals(0, person.getWeightHistory().size());
    }

    @Test
    void addWeight_weightAtZero_accepts() {
        manager.addWeight("/add_weight w/0 d/17/10/25");
        assertEquals(1, person.getWeightHistory().size());
        assertEquals(0.0, person.getWeightHistory().get(0).getWeight());
    }

    @Test
    void addWeight_dateAtEdge_accepts() {
        manager.addWeight("/add_weight w/60 d/01/01/00");
        assertEquals(1, person.getWeightHistory().size());
    }

    @Test
    void addWeight_leadingTrailingSpaces() {
        manager.addWeight("/add_weight w/ 65.0 d/17/10/25 ");
        assertEquals(1, person.getWeightHistory().size());
    }

    @Test
    void addWeight_multipleSpacesBetweenCommand() {
        manager.addWeight("/add_weight   w/66 d/17/10/25");
        assertEquals(1, person.getWeightHistory().size());
    }

    @Test
    void addWeight_decimalWeight() {
        manager.addWeight("/add_weight w/72.75 d/17/10/25");
        assertEquals(1, person.getWeightHistory().size());
        assertEquals(72.75, person.getWeightHistory().get(0).getWeight());
    }

    @Test
    void addWeight_dateWithSingleDigitDayMonth() {
        manager.addWeight("/add_weight w/70.5 d/7/5/25");
        // Sẽ lỗi nếu format ko match dd/MM/yy, test này để kiểm tra lỗi format
        assertEquals(0, person.getWeightHistory().size());
    }

    @Test
    void addWeight_missingWeightAndDate() {
        manager.addWeight("/add_weight w/ d/");
        assertEquals(0, person.getWeightHistory().size());
    }
}
