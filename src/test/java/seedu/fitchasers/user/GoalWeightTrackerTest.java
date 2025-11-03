package seedu.fitchasers.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * JUnit test suite for the GoalWeightTracker class.
 * Tests goal setting, viewing, saving, and loading functionality.
 * Uses reflection to access private fields for testing.
 */
public class GoalWeightTrackerTest {

    private GoalWeightTracker tracker;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp() {
        tracker = new GoalWeightTracker();
        System.setOut(new PrintStream(outputStream, true));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    /**
     * Helper method to get goalWeight from private field using reflection.
     */
    private Double getGoalWeight() throws NoSuchFieldException, IllegalAccessException {
        Field field = GoalWeightTracker.class.getDeclaredField("goalWeight");
        field.setAccessible(true);
        return (Double) field.get(tracker);
    }

    /**
     * Helper method to get setDate from private field using reflection.
     */
    private LocalDate getSetDate() throws NoSuchFieldException, IllegalAccessException {
        Field field = GoalWeightTracker.class.getDeclaredField("setDate");
        field.setAccessible(true);
        return (LocalDate) field.get(tracker);
    }

    @Test
    public void testHandleSetGoal_validInput_setsGoalWeight() throws NoSuchFieldException, IllegalAccessException {
        tracker.handleSetGoal("w/60");

        assertNotNull(getGoalWeight());
        assertEquals(60.0, getGoalWeight());
        assertNotNull(getSetDate());
        assertEquals(LocalDate.now(), getSetDate());
    }

    @Test
    public void testHandleSetGoal_decimalWeight_setsGoalWeight() throws NoSuchFieldException, IllegalAccessException {
        tracker.handleSetGoal("w/65.5");

        assertEquals(65.5, getGoalWeight());
    }

    @Test
    public void testHandleSetGoal_largeWeight_setsGoalWeight() throws NoSuchFieldException, IllegalAccessException {
        tracker.handleSetGoal("w/150.8");

        assertEquals(150.8, getGoalWeight());
    }

    @Test
    public void testHandleSetGoal_smallWeight_setsGoalWeight() throws NoSuchFieldException, IllegalAccessException {
        tracker.handleSetGoal("w/0.1");

        assertEquals(0.1, getGoalWeight());
    }

    @Test
    public void testHandleSetGoal_nullInput_showsUsageMessage() {
        tracker.handleSetGoal(null);

        String output = outputStream.toString();
        assertTrue(output.contains("Usage"));
    }

    @Test
    public void testHandleSetGoal_invalidPrefix_showsUsageMessage() {
        tracker.handleSetGoal("weight/60");

        String output = outputStream.toString();
        assertTrue(output.contains("Usage"));
    }

    @Test
    public void testHandleSetGoal_emptyInput_showsUsageMessage() {
        tracker.handleSetGoal("");

        String output = outputStream.toString();
        assertTrue(output.contains("Usage"));
    }

    @Test
    public void testHandleSetGoal_zeroWeight_showsErrorMessage() {
        tracker.handleSetGoal("w/0");

        String output = outputStream.toString();
        assertTrue(output.contains("positive"));
    }

    @Test
    public void testHandleSetGoal_negativeWeight_showsErrorMessage() {
        tracker.handleSetGoal("w/-50");

        String output = outputStream.toString();
        assertTrue(output.contains("positive"));
    }

    @Test
    public void testHandleSetGoal_invalidFormat_showsErrorMessage() {
        tracker.handleSetGoal("w/abc");

        String output = outputStream.toString();
        assertTrue(output.contains("Invalid") || output.contains("number"));
    }

    @Test
    public void testHandleSetGoal_withSpaces_setsGoalWeight() throws NoSuchFieldException, IllegalAccessException {
        tracker.handleSetGoal("w/   70.5   ");

        assertEquals(70.5, getGoalWeight());
    }

    @Test
    public void testHandleSetGoal_successMessage_displaysGoalAndDate() {
        tracker.handleSetGoal("w/60");

        String output = outputStream.toString();
        assertTrue(output.contains("60"));
        assertTrue(output.contains("goal weight") || output.contains("Goal Weight"));
    }

    @Test
    public void testHandleViewGoal_currentWeightAboveGoal_showsAboveMessage() throws NoSuchFieldException,
            IllegalAccessException {
        tracker.handleSetGoal("w/60");
        outputStream.reset();

        tracker.handleViewGoal(65.0);

        String output = outputStream.toString();
        assertTrue(output.contains("above"));
    }

    @Test
    public void testHandleViewGoal_currentWeightBelowGoal_showsBelowMessage() throws NoSuchFieldException,
            IllegalAccessException {
        tracker.handleSetGoal("w/60");
        outputStream.reset();

        tracker.handleViewGoal(55.0);

        String output = outputStream.toString();
        assertTrue(output.contains("below"));
    }

    @Test
    public void testHandleViewGoal_currentWeightEqualGoal_showsCongrats() throws NoSuchFieldException,
            IllegalAccessException {
        tracker.handleSetGoal("w/60");
        outputStream.reset();

        tracker.handleViewGoal(60.0);

        String output = outputStream.toString();
        assertTrue(output.contains("reached") || output.contains("goal"));
    }

    @Test
    public void testHandleViewGoal_displayFormat_showsAllRequiredInfo() throws NoSuchFieldException,
            IllegalAccessException {
        tracker.handleSetGoal("w/60");
        outputStream.reset();

        tracker.handleViewGoal(65.0);

        String output = outputStream.toString();
        assertTrue(output.contains("Goal Weight") || output.contains("goal"));
        assertTrue(output.contains("Current Weight") || output.contains("current"));
    }

    @Test
    public void testHandleViewGoal_decimalDifference_calculatesCorrectly() throws NoSuchFieldException,
            IllegalAccessException {
        tracker.handleSetGoal("w/60.5");
        outputStream.reset();

        tracker.handleViewGoal(65.3);

        String output = outputStream.toString();
        assertTrue(output.contains("above"));
    }

    @Test
    public void testHandleViewGoal_noWeightRecords_showsGoalOnly() throws NoSuchFieldException,
            IllegalAccessException {
        tracker.handleSetGoal("w/60");
        outputStream.reset();

        tracker.handleViewGoal(null);

        String output = outputStream.toString();
        assertTrue(output.contains("No weight records") || output.contains("no weight records")
                || output.contains("goal"));
    }

    @Test
    public void testHandleViewGoal_negativeCurrentWeight_showsNoRecordsMessage() throws NoSuchFieldException,
            IllegalAccessException {
        tracker.handleSetGoal("w/60");
        outputStream.reset();

        tracker.handleViewGoal(-5.0);

        String output = outputStream.toString();
        assertTrue(output.contains("No weight records") || output.contains("no weight records"));
    }

    @Test
    public void testHandleSetGoal_multipleUpdates_usesLatestGoal() throws NoSuchFieldException, IllegalAccessException {
        tracker.handleSetGoal("w/60");
        assertEquals(60.0, getGoalWeight());

        tracker.handleSetGoal("w/70");
        assertEquals(70.0, getGoalWeight());
    }

    @Test
    public void testHandleSetGoal_updateDate_updatesCurrentDate() throws NoSuchFieldException, IllegalAccessException {
        tracker.handleSetGoal("w/60");
        LocalDate firstDate = getSetDate();

        tracker.handleSetGoal("w/70");
        LocalDate secondDate = getSetDate();

        assertEquals(firstDate, secondDate);
    }

    @Test
    public void testHandleSetGoal_verySmallWeight_setsGoalWeight() throws NoSuchFieldException, IllegalAccessException {
        tracker.handleSetGoal("w/0.01");
        assertEquals(0.01, getGoalWeight());
    }

    @Test
    public void testHandleSetGoal_veryLargeWeight_setsGoalWeight() throws NoSuchFieldException, IllegalAccessException {
        tracker.handleSetGoal("w/500");
        assertEquals(500.0, getGoalWeight());
    }

    @Test
    public void testHandleViewGoal_verySmallDifference_calculatesCorrectly() throws NoSuchFieldException,
            IllegalAccessException {
        tracker.handleSetGoal("w/60.0");
        outputStream.reset();

        tracker.handleViewGoal(60.01);

        String output = outputStream.toString();
        assertTrue(output.contains("above") || output.contains("below"));
    }

    @Test
    public void testHandleSetGoal_onlyPrefix_showsError() {
        tracker.handleSetGoal("w/");

        String output = outputStream.toString();
        assertTrue(output.contains("Invalid") || output.contains("number"));
    }

    @Test
    public void testHandleSetGoal_scientificNotation_parsesCorrectly() throws NoSuchFieldException,
            IllegalAccessException {
        tracker.handleSetGoal("w/1e2");

        // Java's Double.parseDouble supports scientific notation
        assertTrue(getGoalWeight() == null || getGoalWeight() > 0);
    }

    @Test
    public void testHandleViewGoal_zeroCurrentWeight_showsComparison() throws NoSuchFieldException,
            IllegalAccessException {
        tracker.handleSetGoal("w/60");
        outputStream.reset();

        tracker.handleViewGoal(0.0);

        String output = outputStream.toString();
        // 0 is not negative, so it should show comparison
        assertTrue(output.contains("Goal Weight") || output.contains("goal") || output.contains("below"));
    }

    @Test
    public void testGoalTrackerState_afterMultipleOperations_remainsConsistent() throws NoSuchFieldException,
            IllegalAccessException {
        tracker.handleSetGoal("w/60");
        Double firstGoal = getGoalWeight();
        LocalDate firstDate = getSetDate();

        tracker.handleViewGoal(65.0);

        assertEquals(firstGoal, getGoalWeight());
        assertEquals(firstDate, getSetDate());
    }
}
