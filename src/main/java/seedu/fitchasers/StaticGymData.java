package seedu.fitchasers;

import java.util.*;

public class StaticGymData {
    public static List<Gym> getNusGyms() {
        List<Gym> gyms = new ArrayList<>();

        // UTown Gym: Focus on legs and cardio
        Gym utownGym = new Gym("UTown Gym");
        utownGym.addMachine(new Machine("Leg Curl", List.of("legs", "hamstrings", "strength")));
        utownGym.addMachine(new Machine("Leg Extension", List.of("legs", "quadriceps", "strength")));
        utownGym.addMachine(new Machine("Treadmill", List.of("cardio")));
        gyms.add(utownGym);

        // SRC Gym: Focus on chest and core
        Gym srcGym = new Gym("SRC Gym");
        srcGym.addMachine(new Machine("Chest Press", List.of("chest", "arms", "strength")));
        srcGym.addMachine(new Machine("Ab Crunch", List.of("core", "strength")));
        srcGym.addMachine(new Machine("Elliptical", List.of("cardio")));
        gyms.add(srcGym);

        // Sports Complex Gym: Focus on back and shoulders
        Gym sportsComplexGym = new Gym("Sports Complex Gym");
        sportsComplexGym.addMachine(new Machine("Lat Pulldown", List.of("back", "arms", "strength")));
        sportsComplexGym.addMachine(new Machine("Shoulder Press", List.of("shoulders", "arms", "strength")));
        sportsComplexGym.addMachine(new Machine("Rowing Machine", List.of("back", "cardio")));
        gyms.add(sportsComplexGym);

        // Add more gyms or machines as needed, but keep each tag unique to one gym

        return gyms;
    }
}
