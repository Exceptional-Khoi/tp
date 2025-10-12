package seedu.fitchasers;

/**
 * Represents an exercise with a name and a number of repetitions.
 */
public class Exercise {
    private final String name;
    private final int reps;

    /**
     * Constructs an Exercise instance with the specified name and repetitions.
     *
     * @param name The name of the exercise.
     * @param reps The number of repetitions for the exercise.
     */
    public Exercise(String name, Integer reps) {
        this.name = name;
        this.reps = reps;
    }

    /**
     * Returns the name of the exercise.
     *
     * @return The name of the exercise.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the number of repetitions for the exercise.
     *
     * @return The number of repetitions.
     */
    public Integer getReps() {
        return reps;
    }

    /**
     * Returns a string representation of the exercise.
     * @return A string describing the exercise.
     */
    @Override
    public String toString() {
        return (reps == 0) ? name : name + " (" + reps + " reps)";
    }
}
