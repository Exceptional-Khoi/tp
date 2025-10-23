package seedu.fitchasers.gym;
import seedu.fitchasers.Gym;
import seedu.fitchasers.Machine;
import java.util.List;

public class UscGymData {
    public static Gym getGym() {
        Gym uscGym = new Gym("USC Gym");
        uscGym.addMachine(new Machine("Deadlift Platform", List.of("strength", "posterior-chain", "back")));
        uscGym.addMachine(new Machine("Squat Rack", List.of("strength", "squat", "legs", "leg")));
        uscGym.addMachine(new Machine("Cycling Studio", List.of("cardio", "cycle")));
        uscGym.addMachine(new Machine("Swimming Pool", List.of("cardio", "swim", "back")));
        uscGym.addMachine(new Machine("Hypertrophy Machine", List.of("strength", "hypertrophy")));
        uscGym.addMachine(new Machine("Queenax Functional Trainer", List.of("core", "shoulders", "arms")));
        return uscGym;
    }
}
