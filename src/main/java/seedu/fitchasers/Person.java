package seedu.fitchasers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a person using the FitChasers app.
 * Stores the person's name and their weight history.
 */
public class Person {

    /** The name of the person */
    private String name;

    /** The list of weight records for the person */
    private final ArrayList<WeightRecord> weightHistory;

    /**
     * Constructs a new Person with the given name.
     * Initializes an empty weight history.
     *
     * @param name The name of the person
     * @throws IllegalArgumentException if name is null or empty
     */
    public Person(String name) {
        setName(name); // reuse setter để kiểm tra name
        this.weightHistory = new ArrayList<>();
    }

    /**
     * Returns the name of the person.
     *
     * @return The person's name
     */
    public String getName() {
        return name;
    }

    /**
     * Updates the name of the person.
     *
     * @param newName The new name
     * @throws IllegalArgumentException if newName is null or empty
     */
    public void setName(String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
        this.name = newName.trim();
    }

    /**
     * Adds a weight record to the person's weight history.
     *
     * @param record The WeightRecord to add
     * @throws NullPointerException if record is null
     */
    public void addWeightRecord(WeightRecord record) {
        Objects.requireNonNull(record, "WeightRecord cannot be null.");
        weightHistory.add(record);
    }

    /**
     * Returns an unmodifiable copy of the weight history.
     *
     * @return A List of WeightRecord objects
     */
    public List<WeightRecord> getWeightHistory() {
        return Collections.unmodifiableList(new ArrayList<>(weightHistory));
    }

    /**
     * Displays the weight history in the console.
     * Prints a message if there are no records.
     */
    public void displayWeightHistory() {
        if (weightHistory.isEmpty()) {
            System.out.println(name + " has no weight records yet.");
            return;
        }
        System.out.println("Weight history for " + name + ":");
        for (WeightRecord record : weightHistory) {
            System.out.println("  " + record);
        }
    }

    /**
     * Returns the most recent weight recorded for the person.
     *
     * @return The latest weight, or -1 if no records exist
     */
    public double getLatestWeight() {
        if (weightHistory.isEmpty()) {
            return -1; // dùng -1 để phân biệt không có dữ liệu
        }
        return weightHistory.get(weightHistory.size() - 1).getWeight();
    }

    /**
     * Returns the number of weight records.
     *
     * @return the size of the weight history
     */
    public int getWeightHistorySize() {
        return weightHistory.size();
    }

    /**
     * Removes the most recent weight record.
     *
     * @return true if a record was removed, false if no records exist
     */
    public boolean removeLatestWeightRecord() {
        if (weightHistory.isEmpty()) {
            return false;
        }
        weightHistory.remove(weightHistory.size() - 1);
        return true;
    }
}
