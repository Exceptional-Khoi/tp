package seedu.fitchasers.workouts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.fitchasers.storage.FileHandlerTest;
import seedu.fitchasers.exceptions.InvalidArgumentInput;
import seedu.fitchasers.ui.UI;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.time.YearMonth;
import java.time.YearMonth;

public class ViewLogTest {

    private ViewLog viewLog;

    @BeforeEach
    public void setUp() throws IOException {
        FileHandlerTest fileHandlerStub = new FileHandlerTest();
        WorkoutManager workoutManager = new WorkoutManager(null, fileHandlerStub);
        viewLog = new ViewLog(new UI(), workoutManager, fileHandlerStub);
    }

    @Test
    public void testParseArgs_emptyInput_defaultsToCurrentMonthAndPageOne() throws InvalidArgumentInput {
        ViewLog.Parsed parsed = viewLog.parseArgs("");
        YearMonth now = YearMonth.now();
        assertEquals(now, parsed.ym());
        assertEquals(1, parsed.extractedArg());
        assertFalse(parsed.detailed());
    }

    @Test
    public void testParseArgs_monthFlag_onlyMonthParsed() throws InvalidArgumentInput {
        ViewLog.Parsed parsed = viewLog.parseArgs("m/10");
        YearMonth now = YearMonth.now();
        assertEquals(YearMonth.of(now.getYear(), 10), parsed.ym());
        assertEquals(1, parsed.extractedArg());
    }

    @Test
    public void testParseArgs_monthFlagWithPage_correctlyParsesBoth() throws InvalidArgumentInput {
        ViewLog.Parsed parsed = viewLog.parseArgs("m/10 2");
        YearMonth now = YearMonth.now();
        assertEquals(YearMonth.of(now.getYear(), 10), parsed.ym());
        assertEquals(2, parsed.extractedArg());
    }

    @Test
    public void testParseArgs_yearMonthFlag_valid() throws InvalidArgumentInput {
        ViewLog.Parsed parsed = viewLog.parseArgs("ym/10/25");
        assertEquals(YearMonth.of(2025, 10), parsed.ym());
    }

    @Test
    public void testParseArgs_yearMonthFlagWithPage_valid() throws InvalidArgumentInput {
        ViewLog.Parsed parsed = viewLog.parseArgs("ym/10/25 4");
        assertEquals(YearMonth.of(2025, 10), parsed.ym());
        assertEquals(4, parsed.extractedArg());
    }

    @Test
    public void testParseArgs_detailedFlag_setsTrue() throws InvalidArgumentInput {
        ViewLog.Parsed parsed = viewLog.parseArgs("detailed/");
        assertTrue(parsed.detailed());
    }

    @Test
    public void testParseArgs_pageFlag_validPage() throws InvalidArgumentInput {
        ViewLog.Parsed parsed = viewLog.parseArgs("pg/3");
        assertEquals(3, parsed.extractedArg());
    }

    @Test
    public void testParseArgs_combineMonthAndYearMonth_throws() {
        assertThrows(InvalidArgumentInput.class, () -> {
            viewLog.parseArgs("m/10 ym/10/25");
        });
    }

    @Test
    public void testParseArgs_invalidMonth_throws() {
        assertThrows(InvalidArgumentInput.class, () -> {
            viewLog.parseArgs("m/13");
        });
    }

    @Test
    public void testParseArgs_invalidTokens_throws() {
        assertThrows(InvalidArgumentInput.class, () -> {
            viewLog.parseArgs("invalid-input");
        });
    }
}
