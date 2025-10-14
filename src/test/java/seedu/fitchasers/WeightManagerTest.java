package seedu.fitchasers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link WeightManager} class.
 */
class WeightManagerTest {

    private Person person;
    private WeightManager manager;
    private ByteArrayOutputStream outContent;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        person = new Person("Alex");
        manager = new WeightManager(person);
        outContent = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    // ----------------------------------------------------
    // Tests for addWeight()
    // ----------------------------------------------------
    @Test
    void addWeight_validInput_addsWeightRecordAndShowsMessage() {
        manager.addWeight("/add_weight w/81.5 d/19/10/25");

        // verify that a record was added
        assertEquals(1, person.getWeightHistory().size());
        WeightRecord record = person.getWeightHistory().get(0);
        assertEquals(81.5, record.getWeight());
        assertEquals(LocalDate.of(2025, 10, 19), record.getDate());

        // verify printed output contains confirmation message
        String output = outContent.toString();
        assertTrue(output.contains("Recorded new weight"));
        assertTrue(output.contains("81.5"));
    }

    @Test
    void addWeight_invalidFormat_showsErrorMessage() {
        manager.addWeight("/add_weight wrong_format");
        String output = outContent.toString();
        assertTrue(output.contains("Invalid format"));
    }

    @Test
    void addWeight_missingDate_showsErrorMessage() {
        manager.addWeight("/add_weight w/80.0");
        String output = outContent.toString();
        assertTrue(output.contains("Invalid format"));
        assertEquals(0, person.getWeightHistory().size());
    }

    // ----------------------------------------------------
    // Tests for viewWeights()
    // ----------------------------------------------------
    @Test
    void viewWeights_noRecords_printsNoRecordMessage() {
        manager.viewWeights();
        String output = outContent.toString();
        assertTrue(output.contains("has no weight records yet."));
    }

    @Test
    void viewWeights_withRecords_printsAllWeights() {
        person.addWeightRecord(new WeightRecord(70.0, LocalDate.of(2025, 10, 10)));
        person.addWeightRecord(new WeightRecord(71.2, LocalDate.of(2025, 10, 11)));

        manager.viewWeights();
        String output = outContent.toString();

        assertTrue(output.contains("Weight history for Alex"));
        assertTrue(output.contains("70.0"));
        assertTrue(output.contains("71.2"));
    }

    // ----------------------------------------------------
    // Indirect test for private extractBetween() via addWeight()
    // ----------------------------------------------------
    @Test
    void addWeight_incorrectOrder_returnsError() {
        assertThrows(StringIndexOutOfBoundsException.class, () ->
                manager.addWeight("/add_weight d/19/10/25 w/80.0")
        );
    }
}
