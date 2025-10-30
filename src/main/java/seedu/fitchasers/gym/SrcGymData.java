package seedu.fitchasers.gym;
import java.util.List;

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
