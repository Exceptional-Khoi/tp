package seedu.fitchasers.gym;

import java.util.List;

//@@author nitin19011
/**
 * Provides predefined data for the USC Gym, including its machines and targeted body parts.
 * <p>
 * This class serves as a static data source used to initialize a {@code Gym} object
 * representing the USC Gym with a fixed set of workout machines and their corresponding tags.
 */
public class UscGymData {

    /**
     * Returns a {@code Gym} object representing the USC Gym, preloaded with its machines
     * and the body parts they target.
     *
     * @return A {@code Gym} instance populated with machines and their associated tags.
     */
    public static Gym getGym() {
        Gym uscGym = new Gym("USC Gym");
        uscGym.addMachine(new Machine("Deadlift Platform",
                List.of("strength", "posterior-chain", "back", "deadlift")));
        uscGym.addMachine(new Machine("Squat Rack",
                List.of("squat", "strength", "legs")));
        uscGym.addMachine(new Machine("Cycling Studio",
                List.of("cycle", "cardio")));
        uscGym.addMachine(new Machine("Swimming Pool",
                List.of("swim", "cardio", "back")));
        return uscGym;
    }
}
