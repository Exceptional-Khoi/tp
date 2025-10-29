package seedu.fitchasers.gym;
import java.util.List;

public class UscGymData {
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
