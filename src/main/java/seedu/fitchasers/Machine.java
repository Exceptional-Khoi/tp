package seedu.fitchasers;

import java.util.ArrayList;
import java.util.List;

public class Machine {
    private String name;
    private List<String> bodyPartsTargeted;

    public Machine(String name, List<String> bodyPartsTargeted) {
        this.name = name;
        this.bodyPartsTargeted = bodyPartsTargeted;
    }

    public String getName() { return name; }
    public List<String> getBodyPartsTargeted() { return bodyPartsTargeted; }
}
