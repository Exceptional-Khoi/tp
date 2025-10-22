package seedu.fitchasers.gym;
import seedu.fitchasers.Gym;
import seedu.fitchasers.Machine;

import java.util.List;

public class UscGymData {
    public static Gym getGym() {
        Gym UscGymData = new Gym("USC Gym");
        UscGymData.addMachine(new Machine("Lat Pulldown", List.of("back", "arms", "strength")));
        UscGymData.addMachine(new Machine("Shoulder Press", List.of("shoulders", "arms", "strength")));
        UscGymData.addMachine(new Machine("Rowing Machine", List.of("back", "cardio")));
        UscGymData.addMachine(new Machine("Pull-up Bar", List.of("back", "arms", "strength")));
        UscGymData.addMachine(new Machine("Ab Crunch", List.of("core", "strength")));
        UscGymData.addMachine(new Machine("Queenax Functional Trainer", List.of("core", "shoulders", "arms", "strength")));
        UscGymData.addMachine(new Machine("Yoga Studio", List.of("core")));
        UscGymData.addMachine(new Machine("Cycling Studio", List.of("cardio")));
        return UscGymData;
    }
}

