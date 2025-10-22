package seedu.fitchasers;
//import seedu.fitchasers.Machine;

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