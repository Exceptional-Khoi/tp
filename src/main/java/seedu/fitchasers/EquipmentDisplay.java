package seedu.fitchasers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EquipmentDisplay {
    public static void showEquipmentByGym(List<Gym> gyms) {
        System.out.println("| Gym        | Machine        | Body Parts Targeted        |");
        System.out.println("|------------|---------------|----------------------------|");
        for (Gym gym : gyms) {
            for (Machine machine : gym.getMachines()) {
                String bodyParts = String.join(", ", machine.getBodyPartsTargeted());
                System.out.printf("| %-10s | %-13s | %-26s |\n",
                        gym.getName(), machine.getName(), bodyParts);
            }
        }
    }

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

        // Filter out modality tags
        Set<String> filteredTags = new HashSet<>();
        for (String tag : tags) {
            if (!tag.equals("strength") && !tag.equals("cardio")) {
                filteredTags.add(tag);
            }
        }

        // Only match gyms where at least one machine hits ALL tags
        Set<String> gymsWithAllTags = new HashSet<>();
        for (Gym gym : gyms) {
            for (Machine machine : gym.getMachines()) {
                if (new HashSet<>(machine.getBodyPartsTargeted()).containsAll(filteredTags)) {
                    gymsWithAllTags.add(gym.getName());
                    break; // no need to check other machines in this gym
                }
            }
        }

        return gymsWithAllTags;
    }



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
