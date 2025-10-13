package seedu.fitchasers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single weight record for a person.
 * Each record stores the weight in kilograms and the date of measurement.
 */
public class WeightRecord {

    /** The weight in kilograms */
    private final double weight;

    /** The date when this weight was recorded */
    private final LocalDate date;

    /**
     * Constructs a new WeightRecord with the specified weight and date.
     *
     * @param weight The weight in kilograms
     * @param date   The date of the measurement
     */
    public WeightRecord(double weight, LocalDate date) {
        this.weight = weight;
        this.date = date;
    }

    /**
     * Returns the weight of this record.
     *
     * @return The weight in kilograms
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Returns the date when this weight was recorded.
     *
     * @return The date of the measurement
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns a string representation of this weight record.
     * Format: "Date: dd/MM/yy | Weight: XX.X kg"
     *
     * @return A formatted string representing the weight record
     */
    @Override
    public String toString() {
        return String.format("Date: %s | Weight: %.1f kg",
                date.format(DateTimeFormatter.ofPattern("dd/MM/yy")), weight);
    }
}
