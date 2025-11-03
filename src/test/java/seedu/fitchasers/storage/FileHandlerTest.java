package seedu.fitchasers.storage;

import seedu.fitchasers.workouts.Workout;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Test stub for FileHandler used in unit tests.
 */

//@@author nitin19011
public class FileHandlerTest extends FileHandler {

    @Override
    public Map<YearMonth, ArrayList<Workout>> getArrayByMonth() {
        return new HashMap<>();
    }


    @Override
    public ArrayList<Workout> loadMonthList(YearMonth ym) {
        // Return an empty list, no file I/O during tests
        return new ArrayList<>();
    }
}
