package seedu.fitchasers.gym;

import java.util.ArrayList;
import java.util.List;

//@@author nitin19011
/**
 * Represents a gym that contains a collection of workout machines.
 * <p>
 * Each gym has a name and a list of machines available for use.
 * This class provides methods to add machines and retrieve information
 * about the gym and its equipment.
 */
public class Gym {
    private final String name;
    private final List<Machine> machines;

    /**
     * Constructs a {@code Gym} object with the specified name.
     *
     * @param name The name of the gym.
     */
    public Gym(String name) {
        this.name = name;
        this.machines = new ArrayList<>();
    }

    /**
     * Adds a machine to the gym's list of available machines.
     *
     * @param machine The machine to add to the gym.
     */
    public void addMachine(Machine machine) {
        machines.add(machine);
    }

    /**
     * Returns the name of the gym.
     *
     * @return The gym's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the list of machines available in the gym.
     *
     * @return A list of {@code Machine} objects.
     */
    public List<Machine> getMachines() {
        return machines;
    }
}
