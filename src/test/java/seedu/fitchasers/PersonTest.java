package seedu.fitchasers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link Person} class.
 */
public class PersonTest {

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
    // Tests for constructor & getName()
    // ----------------------------------------------------
    @Test
    void constructor_setsNameCorrectly() {
        assertEquals("Alex", person.getName());
    }

    // ----------------------------------------------------
    // Tests for addWeightRecord() and getWeightHistory()
    // ----------------------------------------------------
    @Test
    void addWeightRecord_addsRecordToHistory() {
        WeightRecord record = new WeightRecord(70.5, LocalDate.of(2025, 10, 14));
        person.addWeightRecord(record);

        ArrayList<WeightRecord> history = person.getWeightHistory();
        assertEquals(1, history.size());
        assertEquals(70.5, history.get(0).getWeight());
    }

    // ----------------------------------------------------
    // Tests for getLatestWeight()
    // ----------------------------------------------------
    @Test
    void getLatestWeight_noRecords_returnsZero() {
        assertEquals(0, person.getLatestWeight());
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
        String output = outContent.toString().replace(',', '.'); // normalize decimal
        assertTrue(output.contains("Alex has no weight records yet."));
    }

    @Test
    void displayWeightHistory_withRecords_printsAllRecords() {
        person.addWeightRecord(new WeightRecord(68.0, LocalDate.of(2025, 10, 10)));
        person.addWeightRecord(new WeightRecord(69.0, LocalDate.of(2025, 10, 11)));

        person.displayWeightHistory();
        String output = outContent.toString().replace(',', '.'); // normalize decimal

        assertTrue(output.contains("Weight history for Alex"));
        assertTrue(output.contains("68.0"));
        assertTrue(output.contains("69.0"));
    }
}
