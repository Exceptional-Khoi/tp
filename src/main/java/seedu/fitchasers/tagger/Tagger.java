package seedu.fitchasers.tagger;
import seedu.fitchasers.workouts.Workout;

import java.util.Set;

//@@author Kart04
public interface Tagger {
    Set<String> suggest(Workout w);
}

