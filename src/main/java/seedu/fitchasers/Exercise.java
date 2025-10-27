package seedu.fitchasers;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents an exercise with a name and multiple sets, each having a number of reps.
 */
public class Exercise implements Serializable {
    private final String name;
    private final ArrayList<Integer> sets; // each element = reps for one set

    /**
     * Constructs an Exercise with one initial set.
     *
     * @param name The name of the exercise.
     * @param reps The number of reps for the first set.
     */
    public Exercise(String name, int reps) {
        this.name = name;
        this.sets = new ArrayList<>();
        this.sets.add(reps);
    }

    /**
     * Adds a new set to this exercise.
     *
     * @param reps The number of reps for the new set.
     */
    public void addSet(int reps) {
        sets.add(reps);
    }

    /**
     * Returns the exercise name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the total number of sets.
     */
    public int getNumSets() {
        return sets.size();
    }

    /**
     * Returns the list of reps for each set.
     */
    public ArrayList<Integer> getSets() {
        return sets;
    }

    /**
     * Returns a detailed multi-line description of this exercise.
     */
    public String toDetailedString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(":\n");
        for (int i = 0; i < sets.size(); i++) {
            sb.append("Set ").append(i + 1).append(" -> Reps: ").append(sets.get(i));
            if (i < sets.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Returns a summary line like "Squat [3 sets]"
     */
    @Override
    public String toString() {
        return name + " [" + sets.size() + " sets]";
    }
}
