package seedu.fitchasers.gym;

import java.util.List;

/**
 * Constructs a Machine with the given name and targeted body parts.
 */
public class Machine {
    private final String name;
    private final List<String> bodyPartsTargeted;

    public Machine(String name, List<String> bodyPartsTargeted) {
        this.name = name;
        this.bodyPartsTargeted = bodyPartsTargeted;
    }

    public String getName() {
        return name;
    }

    public List<String> getBodyPartsTargeted() {
        return bodyPartsTargeted;
    }
}
