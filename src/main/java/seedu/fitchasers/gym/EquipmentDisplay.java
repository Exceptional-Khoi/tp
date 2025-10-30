package seedu.fitchasers.gym;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

//@@nitin19011
public class EquipmentDisplay {

    private static final Map<String, String> MACHINE_TO_EXERCISES = new HashMap<>();

    static {
        MACHINE_TO_EXERCISES.put("Deadlift Platform", "Deadlifts");
        MACHINE_TO_EXERCISES.put("Squat Rack", "Squats");
        MACHINE_TO_EXERCISES.put("Cycling Studio", "Cycling");
        MACHINE_TO_EXERCISES.put("Swimming Pool", "Swimming");
        MACHINE_TO_EXERCISES.put("Hypertrophy Machine", "Strength Training");
        MACHINE_TO_EXERCISES.put("Queenax Functional Trainer", "Functional Training");

        MACHINE_TO_EXERCISES.put("Treadmill", "Running, Jogging");
        MACHINE_TO_EXERCISES.put("Leg Curl", "Leg Curls");
        MACHINE_TO_EXERCISES.put("Leg Extension", "Leg Extensions");
        MACHINE_TO_EXERCISES.put("Bench Press", "Bench Press");
        MACHINE_TO_EXERCISES.put("Push-up Station", "Push-ups");
        MACHINE_TO_EXERCISES.put("Calf Raise", "Calf Raises");
        MACHINE_TO_EXERCISES.put("Abs Bench", "Ab Exercises");

        MACHINE_TO_EXERCISES.put("Rowing Machine", "Rowing");
        MACHINE_TO_EXERCISES.put("Lat Pulldown", "Lat Pulldowns");
        MACHINE_TO_EXERCISES.put("Pull-up Bar", "Pull-ups");
        MACHINE_TO_EXERCISES.put("Shoulder Press", "Shoulder Press");
        MACHINE_TO_EXERCISES.put("Bicep Curl", "Bicep Curls");
        MACHINE_TO_EXERCISES.put("Tricep Extension", "Tricep Extensions");
        MACHINE_TO_EXERCISES.put("Ab Crunch", "Ab Crunches");
    }

    private static String getExercisesForMachine(String machineName) {
        return MACHINE_TO_EXERCISES.getOrDefault(machineName, "Various exercises");
    }

    /**
     * Displays all machines in a given gym, printing a table with machine names and targeted body parts.
     *
     * @param gym The Gym object whose machines will be displayed.
     */
    public static String showEquipmentForSingleGym(Gym gym) {

        System.out.println("\n" + gym.getName());
        System.out.println("+" + "-".repeat(gym.getName().length()) + "+");

        int machineWidth = 20;
        int exerciseWidth = 25;

        for (Machine machine : gym.getMachines()) {
            machineWidth = Math.max(machineWidth, machine.getName().length());
            String exercises = getExercisesForMachine(machine.getName());
            exerciseWidth = Math.max(exerciseWidth, exercises.length());
        }

        String format = "| %-" + machineWidth + "s | %-" + exerciseWidth + "s |%n";
        String line = "+" + "-".repeat(machineWidth + 2) + "+"
                + "-".repeat(exerciseWidth + 2) + "+";

        System.out.println(line);  // NOW print the table
        System.out.printf(format, "Machine", "Exercises You Can Do");
        System.out.println(line);

        for (Machine machine : gym.getMachines()) {
            String exercises = getExercisesForMachine(machine.getName());
            System.out.printf(format, machine.getName(), exercises);
        }

        System.out.println(line);
        return gym.getName();
    }

    /**
     * Suggests gyms that have machines matching all tags corresponding to the given exercise.
     *
     * @param gyms       The list of Gym objects to check.
     * @param argumentStr The user input string containing the exercise to search for (e.g. "n/bench").
     * @return A set of gym names that have matching machines; empty set if none found.
     */
    public static Set<String> suggestGymsForExercise(List<Gym> gyms, String argumentStr) {
        String exerciseName = "";

        // Extract exercise name from n/ parameter
        String[] params = argumentStr.split("\\s+");
        for (String param : params) {
            if (param.startsWith("n/")) {
                exerciseName = param.substring(2).trim().toLowerCase();
                break;
            }
        }

        if (exerciseName.isEmpty()) {
            return new HashSet<>();
        }

        Set<String> gymsWithMatch = new HashSet<>();

        // For each gym, check if any machine's tags match the exercise name
        for (Gym gym : gyms) {
            for (Machine machine : gym.getMachines()) {
                List<String> tags = machine.getBodyPartsTargeted();

                // Check if ANY tag appears in the exercise name
                for (String tag : tags) {
                    if (exerciseName.contains(tag.toLowerCase())) {
                        gymsWithMatch.add(gym.getName());
                        break; // Found a match, move to next gym
                    }
                }
            }
        }

        return gymsWithMatch;
    }
}
