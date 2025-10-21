package seedu.fitchasers;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class DefaultTagger implements Tagger {
    private static final Map<String, String> MODALITY = Map.ofEntries(
            Map.entry("run", "cardio"), Map.entry("jog", "cardio"),
            Map.entry("swim", "cardio"), Map.entry("cycle", "cardio"),
            Map.entry("treadmill", "cardio"), Map.entry("rower", "cardio"),
            Map.entry("lift", "strength"), Map.entry("deadlift", "strength"),
            Map.entry("squat", "strength"), Map.entry("bench", "strength"),
            Map.entry("press", "strength"), Map.entry("hypertrophy", "strength")
    );
    //#TODO add feature for user to key map manually then remap
    private static final Map<String, String> MUSCLES = Map.ofEntries(
            Map.entry("leg", "legs"), Map.entry("squat", "legs"),
            Map.entry("deadlift", "posterior-chain"),
            Map.entry("hamstring", "legs"), Map.entry("quad", "legs"), Map.entry("calf", "legs"),

            Map.entry("bench", "chest"), Map.entry("push-up", "chest"),

            Map.entry("row", "back"), Map.entry("pull-up", "back"), Map.entry("lat", "back"),
            Map.entry("back", "back"), Map.entry("lift", "back"), Map.entry("swim", "back"),

            Map.entry("ohp", "shoulders"), Map.entry("overhead press", "shoulders"), Map.entry("shoulder", "shoulders"),

            Map.entry("bicep", "arms"), Map.entry("tricep", "arms"), Map.entry("curl", "arms"),

            Map.entry("abs", "core"), Map.entry("core", "core"), Map.entry("plank", "core")
    );

    @Override
    public Set<String> suggest(Workout w) {
        String text = ((w.getWorkoutName() == null) ? "" : w.getWorkoutName()).toLowerCase();
        // If you later want to tag using exercise in workout then
        // text += " " + String.join(" ", w.getExercises());

        Set<String> tags = new LinkedHashSet<>();

        // Modality
        MODALITY.forEach((keyword, tag) -> { if (text.contains(keyword)) tags.add(tag); });

        // Muscle groups (can add multiple)
        MUSCLES.forEach((keyword, tag) -> { if (text.contains(keyword)) tags.add(tag); });

        // Higher-level patterns
        if (text.contains("push")) tags.add("push");
        if (text.contains("pull")) tags.add("pull");
        if (text.contains("leg day")) tags.add("legs");

        return tags;
    }
}
