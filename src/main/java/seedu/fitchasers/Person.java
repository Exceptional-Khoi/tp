package seedu.fitchasers;

import java.util.ArrayList;

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
     */
    public Person(String name) {
        this.name = name;
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

    public void setName(String newName) {
        this.name = newName;
    }

    /**
     * Adds a weight record to the person's weight history.
     *
     * @param record The WeightRecord to add
     */
    public void addWeightRecord(WeightRecord record) {
        weightHistory.add(record);
    }

    /**
     * Returns the full weight history of the person.
     *
     * @return An ArrayList of WeightRecord objects
     */
    public ArrayList<WeightRecord> getWeightHistory() {
        return weightHistory;
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
     * @return The latest weight, or 0 if no records exist
     */
    public double getLatestWeight() {
        if (weightHistory.isEmpty()) {
            return 0;
        }
        return weightHistory.get(weightHistory.size() - 1).getWeight();
    }
}
