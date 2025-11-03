package seedu.fitchasers;

import org.junit.jupiter.api.Test;
import seedu.fitchasers.workouts.Exercise;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@@author nitin19011
class ExerciseTest {

    @Test
    void constructor_validInput_createsExerciseWithOneSet() {
        Exercise ex = new Exercise("Push Up", 10);
        assertEquals("Push Up", ex.getName());
        assertEquals(1, ex.getNumSets());
        assertEquals(10, ex.getSets().get(0));
    }

    @Test
    void addSet_validInput_increasesSetCount() {
        Exercise ex = new Exercise("Squat", 12);
        ex.addSet(15);
        assertEquals(2, ex.getNumSets());
        assertEquals(15, ex.getSets().get(1));
    }

    @Test
    void toString_multipleSets_returnsCorrectSummary() {
        Exercise ex = new Exercise("Crunch", 10);
        ex.addSet(20);
        assertEquals("Crunch [2 sets]", ex.toString());
    }

    @Test
    void toDetailedString_multipleSets_returnsDetailedFormat() {
        Exercise ex = new Exercise("Pull Up", 8);
        ex.addSet(10);
        String output = ex.toDetailedString();
        assertTrue(output.contains("Pull Up:"));
        assertTrue(output.contains("Set 1 -> Reps: 8"));
        assertTrue(output.contains("Set 2 -> Reps: 10"));
    }

}
