package seedu.fitchasers.gym;

import java.util.List;

//@@author nitin19011
/**
 * Provides predefined data for the SRC Gym, including its machines and targeted body parts.
 * <p>
 * This class serves as a static data source used to initialize a {@code Gym} object
 * representing the SRC Gym with a fixed set of workout machines and their corresponding tags.
 */
public class SrcGymData {
    public static Gym getGym() {
        Gym srcGym = new Gym("SRC Gym");
        srcGym.addMachine(new Machine("Rowing Machine", List.of("row", "rower", "cardio", "back")));
        srcGym.addMachine(new Machine("Lat Pulldown", List.of("back", "lat", "pull-up")));
        srcGym.addMachine(new Machine("Pull-up Bar", List.of("pull", "pull-up", "back")));
        srcGym.addMachine(new Machine("Shoulder Press", List.of("shoulders", "shoulder", "ohp", "overhead press",
                "strength", "press")));
        srcGym.addMachine(new Machine("Bicep Curl", List.of("arms", "bicep", "curl")));
        srcGym.addMachine(new Machine("Tricep Extension", List.of("arms", "tricep")));
        srcGym.addMachine(new Machine("Ab Crunch", List.of("core", "abs")));
        return srcGym;
    }
}
