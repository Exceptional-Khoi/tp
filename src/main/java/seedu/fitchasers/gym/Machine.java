package seedu.fitchasers.gym;

import java.util.List;

//@@author nitin19011
/**
 * Represents a workout machine and the body parts it targets.
 * <p>
 * Each machine has a name and a list of body parts that can be trained
 * using it. This class is primarily used within {@code Gym} objects
 * to describe available equipment.
 */
public class Machine {
    private final String name;
    private final List<String> bodyPartsTargeted;

    /**
     * Constructs a {@code Machine} with the specified name and targeted body parts.
     *
     * @param name              The name of the machine.
     * @param bodyPartsTargeted A list of body parts targeted by this machine.
     */
    public Machine(String name, List<String> bodyPartsTargeted) {
        this.name = name;
        this.bodyPartsTargeted = bodyPartsTargeted;
    }

    /**
     * Returns the name of the machine.
     *
     * @return The machine's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the list of body parts targeted by this machine.
     *
     * @return A list of targeted body parts.
     */
    public List<String> getBodyPartsTargeted() {
        return bodyPartsTargeted;
    }
}
