package seedu.fitchasers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link Person} class.
 */
class PersonTest {

    private Person person;
    private ByteArrayOutputStream outContent;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        person = new Person("Alex");
        outContent = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    // ----------------------------------------------------
    // Tests for constructor & setName()
    // ----------------------------------------------------
    @Test
    void constructor_setsNameCorrectly() {
        assertEquals("Alex", person.getName());
    }

    @Test
    void setName_updatesName() {
        person.setName("Bob");
        assertEquals("Bob", person.getName());
    }

    @Test
    void setName_nullOrEmpty_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> person.setName(null));
        assertThrows(IllegalArgumentException.class, () -> person.setName(""));
        assertThrows(IllegalArgumentException.class, () -> person.setName("   "));
    }

    // ----------------------------------------------------
    // Tests for addWeightRecord() and getWeightHistory()
    // ----------------------------------------------------
    @Test
    void addWeightRecord_addsRecordToHistory() {
        WeightRecord record = new WeightRecord(70.5, LocalDate.of(2025, 10, 14));
        person.addWeightRecord(record);

        List<WeightRecord> history = person.getWeightHistory();
        assertEquals(1, history.size());
        assertEquals(70.5, history.get(0).getWeight());
    }

    @Test
    void addWeightRecord_null_throwsException() {
        assertThrows(NullPointerException.class, () -> person.addWeightRecord(null));
    }

    @Test
    void getWeightHistory_returnsUnmodifiableList() {
        person.addWeightRecord(new WeightRecord(70.0, LocalDate.now()));
        List<WeightRecord> history = person.getWeightHistory();
        assertThrows(UnsupportedOperationException.class, () -> history.add(new WeightRecord(71.0, LocalDate.now())));
    }

    // ----------------------------------------------------
    // Tests for getLatestWeight()
    // ----------------------------------------------------
    @Test
    void getLatestWeight_noRecords_returnsMinusOne() {
        assertEquals(-1, person.getLatestWeight());
    }

    @Test
    void getLatestWeight_withRecords_returnsLastWeight() {
        person.addWeightRecord(new WeightRecord(70.5, LocalDate.of(2025, 10, 14)));
        person.addWeightRecord(new WeightRecord(71.0, LocalDate.of(2025, 10, 15)));
        assertEquals(71.0, person.getLatestWeight());
    }

    // ----------------------------------------------------
    // Tests for displayWeightHistory()
    // ----------------------------------------------------
    @Test
    void displayWeightHistory_noRecords_printsNoRecordsMessage() {
        person.displayWeightHistory();
        String output = outContent.toString();
        assertTrue(output.contains("Alex has no weight records yet."));
    }

    @Test
    void displayWeightHistory_withRecords_printsAllRecords() {
        WeightRecord r1 = new WeightRecord(68.0, LocalDate.of(2025, 10, 10));
        WeightRecord r2 = new WeightRecord(69.0, LocalDate.of(2025, 10, 11));
        person.addWeightRecord(r1);
        person.addWeightRecord(r2);

        person.displayWeightHistory();
        String output = outContent.toString();

        // Check that the weight numbers appear in the output (ignoring exact format)
        assertTrue(output.contains("68.0") || output.contains("68"));
        assertTrue(output.contains("69.0") || output.contains("69"));
    }

    // ----------------------------------------------------
    // Tests for removeLatestWeightRecord()
    // ----------------------------------------------------
    @Test
    void removeLatestWeightRecord_removesLastRecord() {
        person.addWeightRecord(new WeightRecord(68.0, LocalDate.of(2025, 10, 10)));
        person.addWeightRecord(new WeightRecord(69.0, LocalDate.of(2025, 10, 11)));

        assertTrue(person.removeLatestWeightRecord());
        assertEquals(1, person.getWeightHistorySize());
        assertEquals(68.0, person.getLatestWeight());
    }

    @Test
    void removeLatestWeightRecord_noRecords_returnsFalse() {
        assertFalse(person.removeLatestWeightRecord());
    }

    // ----------------------------------------------------
    // Tests for getWeightHistorySize()
    // ----------------------------------------------------
    @Test
    void getWeightHistorySize_returnsCorrectSize() {
        assertEquals(0, person.getWeightHistorySize());
        person.addWeightRecord(new WeightRecord(68.0, LocalDate.now()));
        assertEquals(1, person.getWeightHistorySize());
    }
}
