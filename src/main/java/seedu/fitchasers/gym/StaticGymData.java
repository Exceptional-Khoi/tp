package seedu.fitchasers.gym;

import java.util.List;
import java.util.ArrayList;

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
