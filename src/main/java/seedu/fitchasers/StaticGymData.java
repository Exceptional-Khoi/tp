package seedu.fitchasers;

import seedu.fitchasers.gym.UTownGymData;
import seedu.fitchasers.gym.SrcGymData;
import seedu.fitchasers.gym.UscGymData;

import java.util.*;

public class StaticGymData {
    public static List<Gym> getNusGyms() {
        List<Gym> gyms = new ArrayList<>();
        gyms.add(UTownGymData.getGym());
        gyms.add(SrcGymData.getGym());
        gyms.add(UscGymData.getGym());
        // Add more gyms or machines as needed, but keep each tag unique to one gym

        return gyms;
    }
}
