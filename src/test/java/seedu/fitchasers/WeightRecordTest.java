package seedu.fitchasers;

import org.junit.jupiter.api.Test;
import seedu.fitchasers.user.WeightRecord;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the {@link WeightRecord} class.
 * These tests verify that weight and date values are stored and formatted correctly.
 */

//@@author Exceptional-Khoi
public class WeightRecordTest {

    /**
     * Tests that the constructor correctly initializes the weight and date fields.
     */
    @Test
    public void testConstructorAndGetters() {
        double expectedWeight = 70.5;
        LocalDate expectedDate = LocalDate.of(2025, 10, 14);

        WeightRecord record = new WeightRecord(expectedWeight, expectedDate);

        assertEquals(expectedWeight, record.getWeight(),
                "Weight should match the value provided in the constructor.");
        assertEquals(expectedDate, record.getDate(),
                "Date should match the value provided in the constructor.");
    }

    /**
     * Tests that the toString() method returns the expected formatted string.
     * Accepts both '.' and ',' as decimal separators depending on system locale.
     */
    @Test
    public void testToStringFormatting() {
        LocalDate date = LocalDate.of(2025, 10, 14);
        WeightRecord record = new WeightRecord(70.5, date);

        String expected = "Date: " + date.format(DateTimeFormatter.ofPattern("dd/MM/yy"))
                + " | Weight: 70.5 kg";

        // Replace ',' with '.' to avoid locale formatting issues
        String actual = record.toString().replace(',', '.');

        assertEquals(expected, actual,
                "toString() should return a properly formatted record string.");
    }

    /**
     * Tests that toString() includes both date and weight information.
     */
    @Test
    public void testToStringContainsDateAndWeight() {
        LocalDate date = LocalDate.of(2025, 1, 1);
        WeightRecord record = new WeightRecord(60.0, date);
        String output = record.toString();

        String formattedDate = date.format(DateTimeFormatter.ofPattern("dd/MM/yy"));

        // Normalize any locale differences
        output = output.replace(',', '.');

        assertTrue(output.contains(formattedDate), "toString() should include the formatted date.");
        assertTrue(output.contains("60.0"), "toString() should include the weight value.");
    }
}
