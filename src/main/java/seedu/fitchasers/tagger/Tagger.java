package seedu.fitchasers.tagger;

import seedu.fitchasers.workouts.Workout;

import java.util.Set;

//@@author Kart04
/**
 * Defines the behavior for classes that can generate exercise tags for a given workout.
 * <p>
 * Implementations of this interface analyze a {@code Workout} object and return
 * a set of suggested tags that describe its characteristics (e.g., modality or muscle group).
 */
public interface Tagger {
    Set<String> suggest(Workout w);
}

