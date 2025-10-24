package seedu.fitchasers;

import seedu.fitchasers.tagger.Modality;
import seedu.fitchasers.tagger.MuscleGroup;

import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.Set;

public class DefaultTagger implements Tagger {
    private final EnumMap<Modality, Set<String>> modalityKeywords = new EnumMap<>(Modality.class);
    private final EnumMap<MuscleGroup, Set<String>> muscleKeywords = new EnumMap<>(MuscleGroup.class);

    public DefaultTagger() {
        // Modality keywords
        modalityKeywords.put(Modality.CARDIO, new LinkedHashSet<>(Set.of(
                "run", "jog", "swim", "cycle", "treadmill", "rower"
        )));
        modalityKeywords.put(Modality.STRENGTH, new LinkedHashSet<>(Set.of(
                "lift", "deadlift", "squat", "bench", "press", "hypertrophy"
        )));

        // Muscle group keywords
        muscleKeywords.put(MuscleGroup.LEGS, new LinkedHashSet<>(Set.of(
                "leg", "squat", "hamstring", "quad", "calf", "leg day"
        )));
        muscleKeywords.put(MuscleGroup.POSTERIOR_CHAIN, new LinkedHashSet<>(Set.of("deadlift")));
        muscleKeywords.put(MuscleGroup.CHEST, new LinkedHashSet<>(Set.of("bench", "push-up")));
        muscleKeywords.put(MuscleGroup.BACK, new LinkedHashSet<>(Set.of(
                "row", "pull-up", "lat", "back", "lift", "swim"
        )));
        muscleKeywords.put(MuscleGroup.SHOULDERS, new LinkedHashSet<>(Set.of(
                "ohp", "overhead press", "shoulder"
        )));
        muscleKeywords.put(MuscleGroup.ARMS, new LinkedHashSet<>(Set.of("bicep", "tricep", "curl")));
        muscleKeywords.put(MuscleGroup.CORE, new LinkedHashSet<>(Set.of("abs", "core", "plank")));
    }

    @Override
    public Set<String> suggest(Workout w) {
        String text = ((w.getWorkoutName() == null) ? "" : w.getWorkoutName()).toLowerCase();
        Set<String> tags = new LinkedHashSet<>();

        // Modality
        for (var entry : modalityKeywords.entrySet()) {
            for (String keyword : entry.getValue()) {
                if (text.contains(keyword)) {
                    tags.add(entry.getKey().name().toLowerCase());
                }
            }
        }

        // Muscle groups (can add multiple)
        for (var entry : muscleKeywords.entrySet()) {
            for (String keyword : entry.getValue()) {
                if (text.contains(keyword)) {
                    tags.add(entry.getKey().name().toLowerCase().replace('_', '-'));
                }
            }
        }

        // Custom patterns
        if (text.contains("push")){
            tags.add("push");
        }
        if (text.contains("pull")){
            tags.add("pull");
        }

        return tags;
    }

    public void addModalityKeyword(Modality modality, String keyword) {
        modalityKeywords.computeIfAbsent(modality, k -> new LinkedHashSet<>()).add(keyword.toLowerCase());
    }
    public void addMuscleKeyword(MuscleGroup muscle, String keyword) {
        muscleKeywords.computeIfAbsent(muscle, k -> new LinkedHashSet<>()).add(keyword.toLowerCase());
    }
}
