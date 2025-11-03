package seedu.fitchasers.gym;

import java.util.List;

//@@author nitin19011
/**
 * Provides predefined data for the UTown Gym, including its machines and targeted body parts.
 * <p>
 * This class serves as a static data source used to initialize a {@code Gym} object
 * representing the UTown Gym with a fixed set of workout machines and their corresponding tags.
 */
public class UTownGymData {

    /**
     * Returns a {@code Gym} object representing the UTown Gym, preloaded with its machines
     * and the body parts they target.
     *
     * @return A {@code Gym} instance populated with machines and their associated tags.
     */
    public static Gym getGym() {
        Gym utownGym = new Gym("UTown Gym");
        utownGym.addMachine(new Machine("Treadmill", List.of("cardio", "legs", "run", "jog", "treadmill")));
        utownGym.addMachine(new Machine("Leg Curl", List.of("legs", "hamstring")));
        utownGym.addMachine(new Machine("Leg Extension", List.of("legs", "quad")));
        utownGym.addMachine(new Machine("Bench Press", List.of("chest", "bench", "press", "strength")));
        utownGym.addMachine(new Machine("Push-up Station", List.of("chest", "push-up")));
        utownGym.addMachine(new Machine("Calf Raise", List.of("legs", "calf")));
        utownGym.addMachine(new Machine("Abs Bench", List.of("core", "abs", "plank")));
        return utownGym;
    }
}
