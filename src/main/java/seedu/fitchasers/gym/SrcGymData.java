package seedu.fitchasers.gym;
import seedu.fitchasers.Gym;
import seedu.fitchasers.Machine;

import java.util.List;

public class SrcGymData {
    public static Gym getGym() {
        Gym SrcGymData = new Gym("SRC Gym");
        SrcGymData.addMachine(new Machine("Chest Press", List.of("chest", "arms", "strength")));
        SrcGymData.addMachine(new Machine("Ab Crunch", List.of("core", "strength")));
        SrcGymData.addMachine(new Machine("Elliptical", List.of("cardio")));
        return SrcGymData;
    }
}

