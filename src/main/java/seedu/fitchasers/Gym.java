package seedu.fitchasers;

import java.util.ArrayList;
import java.util.List;

public class Gym {
    private String name;
    private List<Machine> machines;

    public Gym(String name) {
        this.name = name;
        this.machines = new ArrayList<>();
    }

    public void addMachine(Machine machine) {
        machines.add(machine);
    }

    public String getName() { return name; }
    public List<Machine> getMachines() { return machines; }
}

class Machine {
    private String name;
    private List<String> bodyPartsTargeted;

    public Machine(String name, List<String> bodyPartsTargeted) {
        this.name = name;
        this.bodyPartsTargeted = bodyPartsTargeted;
    }

    public String getName() { return name; }
    public List<String> getBodyPartsTargeted() { return bodyPartsTargeted; }
}

