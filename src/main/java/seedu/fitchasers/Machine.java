package seedu.fitchasers;

import java.util.List;

/**
 * Constructs a Machine with the given name and targeted body parts.
 */
public class Machine {
    private String name;
    private List<String> bodyPartsTargeted;

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
