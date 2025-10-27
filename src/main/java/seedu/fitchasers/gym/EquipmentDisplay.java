package seedu.fitchasers.gym;

import seedu.fitchasers.workouts.Workout;
import seedu.fitchasers.tagger.DefaultTagger;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EquipmentDisplay {
    /**
     * Displays all machines in a given gym, printing a table with machine names and targeted body parts.
     *
     * @param gym The Gym object whose machines will be displayed.
     */
    public static String showEquipmentForSingleGym(Gym gym) {
        int gymWidth = gym.getName().length();
        int machineWidth = 7;
        int bodyWidth = 20;

        for (Machine machine : gym.getMachines()) {
            machineWidth = Math.max(machineWidth, machine.getName().length());
            int bodyLen = String.join(", ", machine.getBodyPartsTargeted()).length();
            bodyWidth = Math.max(bodyWidth, bodyLen);
        }

        String format = "| %-" + gymWidth + "s | %-" + machineWidth + "s | %-" + bodyWidth + "s |%n";
        String line = "+" + "-".repeat(gymWidth + 2) + "+"
                + "-".repeat(machineWidth + 2) + "+"
                + "-".repeat(bodyWidth + 2) + "+";

        StringBuilder sb = new StringBuilder();
        sb.append(line).append("\n");
        sb.append(String.format(format, "Gym", "Machine", "Body Parts Targeted"));
        sb.append(line).append("\n");

        for (Machine machine : gym.getMachines()) {
            String bodyParts = String.join(", ", machine.getBodyPartsTargeted());
            sb.append(String.format(format, gym.getName(), machine.getName(), bodyParts));
        }

        sb.append(line);
        return sb.toString();
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
        String[] params = argumentStr.split("\\s+");
        for (String param : params) {
            if (param.startsWith("n/")) {
                exerciseName = param.substring(2).trim();
                break;
            }
        }

        Workout tempWorkout = new Workout(exerciseName, 0);

        Set<String> tags = new DefaultTagger().suggest(tempWorkout);

        Set<String> filteredTags = new HashSet<>(tags);

        if (filteredTags.isEmpty()) {
            return new HashSet<>();
        }

        Set<String> gymsWithAllTags = new HashSet<>();
        for (Gym gym : gyms) {
            for (Machine machine : gym.getMachines()) {
                if (new HashSet<>(machine.getBodyPartsTargeted()).containsAll(filteredTags)) {
                    gymsWithAllTags.add(gym.getName());
                    break;
                }
            }
        }
        return gymsWithAllTags;
    }

    /**
     * Suggests gyms that have at least one machine matching any tag in the provided set.
     *
     * @param gyms The list of Gym objects to check.
     * @param tags The set of tags to match against machines.
     * @return A set of gym names that have matching machines.
     */
    public static Set<String> suggestGymsForTags(List<Gym> gyms, Set<String> tags) {
        Set<String> matchingGyms = new HashSet<>();
        for (String tag : tags) {
            for (Gym gym : gyms) {
                for (Machine machine : gym.getMachines()) {
                    for (String bodyPart : machine.getBodyPartsTargeted()) {
                        if (bodyPart.equalsIgnoreCase(tag)) {
                            matchingGyms.add(gym.getName());
                        }
                    }
                }
            }
        }
        return matchingGyms;
    }
}
