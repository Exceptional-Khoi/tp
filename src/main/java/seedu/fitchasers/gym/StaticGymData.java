package seedu.fitchasers.gym;

import java.util.List;
import java.util.ArrayList;

//@@author nitin19011
/**
 * Provides static access to all predefined NUS gym data.
 * <p>
 * This class aggregates multiple {@code Gym} instances, each representing
 * a specific NUS gym location (e.g., UTown, SRC, USC). It serves as a central
 * data provider for retrieving all available gym configurations used in the application.
 */
public class StaticGymData {

    /**
     * Returns a list of all predefined NUS gyms, each populated with its respective machines.
     *
     * @return A list of {@code Gym} objects representing different NUS gyms.
     */
    public static List<Gym> getNusGyms() {
        List<Gym> gyms = new ArrayList<>();
        gyms.add(UTownGymData.getGym());
        gyms.add(SrcGymData.getGym());
        gyms.add(UscGymData.getGym());
        return gyms;
    }
}
