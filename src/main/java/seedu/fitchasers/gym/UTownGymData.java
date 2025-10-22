package seedu.fitchasers.gym;
import seedu.fitchasers.Gym;
import seedu.fitchasers.Machine;

import java.util.List;

public class UTownGymData {
    public static Gym getGym() {
        Gym utownGym = new Gym("UTown Gym");
        utownGym.addMachine(new Machine("Leg Curl", List.of("legs", "hamstrings", "strength")));
        utownGym.addMachine(new Machine("Leg Extension", List.of("legs", "quadriceps", "strength")));
        utownGym.addMachine(new Machine("Treadmill", List.of("cardio")));
        return utownGym;
    }
}

