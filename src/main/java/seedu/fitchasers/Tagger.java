package seedu.fitchasers;
import java.util.Set;

public interface Tagger {
    Set<String> suggest(Workout w);
}

