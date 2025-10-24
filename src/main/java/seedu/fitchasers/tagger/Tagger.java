package seedu.fitchasers.tagger;
import seedu.fitchasers.workouts.Workout;

import java.util.Set;

public interface Tagger {
    Set<String> suggest(Workout w);
}

