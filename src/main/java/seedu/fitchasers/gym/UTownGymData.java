package seedu.fitchasers.gym;
import java.util.List;

//@@nitin19011
public class UTownGymData {
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
