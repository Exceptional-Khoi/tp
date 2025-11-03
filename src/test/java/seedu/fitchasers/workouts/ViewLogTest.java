package seedu.fitchasers.workouts;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import seedu.fitchasers.exceptions.FileNonexistent;
import seedu.fitchasers.exceptions.InvalidArgumentInput;
import seedu.fitchasers.storage.FileHandler;
import seedu.fitchasers.tagger.DefaultTagger;
import seedu.fitchasers.tagger.Tagger;
import seedu.fitchasers.ui.UI;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * JUnit test suite for the ViewLog class.
 * Tests parsing, rendering, pagination, and core functionality.
 */
public class ViewLogTest {

    private ViewLog viewLog;
    private WorkoutManager mockWorkoutManager;
    private FileHandler mockFileHandler;
    private UI mockUI;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    public void setUp() throws IOException {
        mockUI = new UI();
        mockFileHandler = new FileHandler();  // Now a class-level field
        Tagger mockTagger = new DefaultTagger();
        mockWorkoutManager = new WorkoutManager(mockTagger, mockFileHandler); // throws IOException
        viewLog = new ViewLog(mockUI, mockWorkoutManager, mockFileHandler);

        System.setOut(new PrintStream(outputStream, true));
    }

    @AfterEach
    public void tearDown() {
        // Restore original System.out
        System.setOut(originalOut);
    }

    /* ===================== PARSING TESTS ===================== */

    @Test
    public void testParseArgs_emptyInput_returnsCurrentMonthPageOne() throws InvalidArgumentInput {
        ViewLog.Parsed result = viewLog.parseArgs("");

        assertEquals(YearMonth.now(), result.ym());
        assertEquals(1, result.extractedArg());
        assertFalse(result.detailed());
    }

    @Test
    public void testParseArgs_nullInput_returnsCurrentMonthPageOne() throws InvalidArgumentInput {
        ViewLog.Parsed result = viewLog.parseArgs(null);

        assertEquals(YearMonth.now(), result.ym());
        assertEquals(1, result.extractedArg());
        assertFalse(result.detailed());
    }

    @Test
    public void testParseArgs_monthFlag_correctlyParsesMonth() throws InvalidArgumentInput {
        ViewLog.Parsed result = viewLog.parseArgs("-m 10");

        assertEquals(YearMonth.of(YearMonth.now().getYear(), 10), result.ym());
        assertEquals(1, result.extractedArg());
        assertFalse(result.detailed());
    }

    @Test
    public void testParseArgs_monthFlagWithPage_correctlyParsesMonthAndPage() throws InvalidArgumentInput {
        ViewLog.Parsed result = viewLog.parseArgs("-m 10 2");

        assertEquals(YearMonth.of(YearMonth.now().getYear(), 10), result.ym());
        assertEquals(2, result.extractedArg());
        assertFalse(result.detailed());
    }

    @Test
    public void testParseArgs_yearMonthFlag_correctlyParsesYearAndMonth() throws InvalidArgumentInput {
        ViewLog.Parsed result = viewLog.parseArgs("-ym 2024 10");

        assertEquals(YearMonth.of(2024, 10), result.ym());
        assertEquals(1, result.extractedArg());
        assertFalse(result.detailed());
    }

    @Test
    public void testParseArgs_yearMonthFlagWithPage_correctlyParsesAll() throws InvalidArgumentInput {
        ViewLog.Parsed result = viewLog.parseArgs("-ym 2024 10 3");

        assertEquals(YearMonth.of(2024, 10), result.ym());
        assertEquals(3, result.extractedArg());
        assertFalse(result.detailed());
    }

    @Test
    public void testParseArgs_detailedFlag_setsDetailedTrue() throws InvalidArgumentInput {
        ViewLog.Parsed result = viewLog.parseArgs("-m 10 -d");

        assertEquals(YearMonth.of(YearMonth.now().getYear(), 10), result.ym());
        assertTrue(result.detailed());
    }

    @Test
    public void testParseArgs_detailedLongFlag_setsDetailedTrue() throws InvalidArgumentInput {
        ViewLog.Parsed result = viewLog.parseArgs("-m 10 --detailed");

        assertTrue(result.detailed());
    }

    @Test
    public void testParseArgs_invalidMonth_throwsException() {
        InvalidArgumentInput exception = assertThrows(InvalidArgumentInput.class, () -> {
            viewLog.parseArgs("-m 13");
        });

        assertEquals("Month must be between 1 and 12.", exception.getMessage());
    }

    @Test
    public void testParseArgs_monthZero_throwsException() {
        InvalidArgumentInput exception = assertThrows(InvalidArgumentInput.class, () -> {
            viewLog.parseArgs("-m 0");
        });

        assertEquals("Month must be between 1 and 12.", exception.getMessage());
    }

    @Test
    public void testParseArgs_negativeMonth_throwsException() {
        InvalidArgumentInput exception = assertThrows(InvalidArgumentInput.class, () -> {
            viewLog.parseArgs("-m -1");
        });

        assertEquals("Month must be between 1 and 12.", exception.getMessage());
    }

    @Test
    public void testParseArgs_invalidYear_throwsException() {
        InvalidArgumentInput exception = assertThrows(InvalidArgumentInput.class, () -> {
            viewLog.parseArgs("-ym 1969 10");
        });

        assertEquals("Year must be between 1970 and 2100.", exception.getMessage());
    }

    @Test
    public void testParseArgs_yearTooHigh_throwsException() {
        InvalidArgumentInput exception = assertThrows(InvalidArgumentInput.class, () -> {
            viewLog.parseArgs("-ym 2101 10");
        });

        assertEquals("Year must be between 1970 and 2100.", exception.getMessage());
    }

    @Test
    public void testParseArgs_missingMonthAfterFlag_throwsException() {
        assertThrows(InvalidArgumentInput.class, () -> {
            viewLog.parseArgs("-m");
        });
    }

    @Test
    public void testParseArgs_missingYearAfterFlag_throwsException() {
        assertThrows(InvalidArgumentInput.class, () -> {
            viewLog.parseArgs("-ym");
        });
    }

    @Test
    public void testParseArgs_missingMonthAfterYear_throwsException() {
        assertThrows(InvalidArgumentInput.class, () -> {
            viewLog.parseArgs("-ym 2024");
        });
    }

    @Test
    public void testParseArgs_combinedMAndYm_throwsException() {
        InvalidArgumentInput exception = assertThrows(InvalidArgumentInput.class, () -> {
            viewLog.parseArgs("-m 10 -ym 2024 11");
        });

        assertTrue(exception.getMessage().contains("Cannot combine"));
    }

    @Test
    public void testParseArgs_unknownFlag_throwsException() {
        InvalidArgumentInput exception = assertThrows(InvalidArgumentInput.class, () -> {
            viewLog.parseArgs("-x");
        });

        assertEquals("Unknown flag: -x", exception.getMessage());
    }

    @Test
    public void testParseArgs_unexpectedToken_throwsException() {
        InvalidArgumentInput exception = assertThrows(InvalidArgumentInput.class, () -> {
            viewLog.parseArgs("hello");
        });

        assertEquals("Unexpected token: hello", exception.getMessage());
    }

    @Test
    public void testParseArgs_negativePageNumber_throwsException() {
        assertThrows(InvalidArgumentInput.class, () -> {
            viewLog.parseArgs("-m 10 -1");
        });
    }

    @Test
    public void testParseArgs_zeroPageNumber_throwsException() {
        assertThrows(InvalidArgumentInput.class, () -> {
            viewLog.parseArgs("-m 10 0");
        });
    }

    @Test
    public void testParseArgs_barePageNumber_correctlyParsesPage() throws InvalidArgumentInput {
        ViewLog.Parsed result = viewLog.parseArgs("5");

        assertEquals(YearMonth.now(), result.ym());
        assertEquals(5, result.extractedArg());
    }

    /* ===================== PAGINATION TESTS ===================== */

    @Test
    public void testComputeTotalPages_emptyList_returnsZero() {
        int totalPages = ViewLog.computeTotalPages(0, 10);
        assertEquals(0, totalPages);
    }

    @Test
    public void testComputeTotalPages_exactlyOnePage_returnsOne() {
        int totalPages = ViewLog.computeTotalPages(10, 10);
        assertEquals(1, totalPages);
    }

    @Test
    public void testComputeTotalPages_lessThanOnePage_returnsOne() {
        int totalPages = ViewLog.computeTotalPages(5, 10);
        assertEquals(1, totalPages);
    }

    @Test
    public void testComputeTotalPages_multiplePages_returnsCorrectCount() {
        int totalPages = ViewLog.computeTotalPages(25, 10);
        assertEquals(3, totalPages);
    }

    @Test
    public void testComputeTotalPages_exactlyTwoPages_returnsTwo() {
        int totalPages = ViewLog.computeTotalPages(20, 10);
        assertEquals(2, totalPages);
    }

    @Test
    public void testComputeTotalPages_onePastFullPage_returnsCorrectCount() {
        int totalPages = ViewLog.computeTotalPages(21, 10);
        assertEquals(3, totalPages);
    }

    @Test
    public void testOpenByIndex_indexZero_throwsException() {
        InvalidArgumentInput exception = assertThrows(InvalidArgumentInput.class, () -> {
            viewLog.openByIndex(0);
        });

        assertTrue(exception.getMessage().contains("out of bounds"));
    }

    @Test
    public void testOpenByIndex_negativeIndex_throwsException() {
        InvalidArgumentInput exception = assertThrows(InvalidArgumentInput.class, () -> {
            viewLog.openByIndex(-1);
        });

        assertTrue(exception.getMessage().contains("out of bounds"));
    }

    @Test
    public void testOpenByIndex_indexTooLarge_throwsException() {
        InvalidArgumentInput exception = assertThrows(InvalidArgumentInput.class, () -> {
            viewLog.openByIndex(999);
        });

        assertTrue(exception.getMessage().contains("out of bounds"));
    }

    @Test
    public void testGetWorkoutByDisplayId_invalidId_returnsNull() throws FileNonexistent, IOException {
        Workout result = viewLog.getWorkoutByDisplayId(0, YearMonth.now());
        assertNull(result);
    }

    @Test
    public void testGetWorkoutByDisplayId_negativeId_returnsNull() throws FileNonexistent, IOException {
        Workout result = viewLog.getWorkoutByDisplayId(-1, YearMonth.now());
        assertNull(result);
    }

    @Test
    public void testParseArgs_januaryMonth_validBoundary() throws InvalidArgumentInput {
        ViewLog.Parsed result = viewLog.parseArgs("-m 1");
        assertEquals(1, result.ym().getMonthValue());
    }

    @Test
    public void testParseArgs_decemberMonth_validBoundary() throws InvalidArgumentInput {
        ViewLog.Parsed result = viewLog.parseArgs("-m 12");
        assertEquals(12, result.ym().getMonthValue());
    }

    @Test
    public void testParseArgs_year1970_validBoundary() throws InvalidArgumentInput {
        ViewLog.Parsed result = viewLog.parseArgs("-ym 1970 1");
        assertEquals(1970, result.ym().getYear());
    }

    @Test
    public void testParseArgs_year2100_validBoundary() throws InvalidArgumentInput {
        ViewLog.Parsed result = viewLog.parseArgs("-ym 2100 1");
        assertEquals(2100, result.ym().getYear());
    }

    @Test
    public void testParseArgs_largePage_acceptsLargePageNumber() throws InvalidArgumentInput {
        ViewLog.Parsed result = viewLog.parseArgs("-m 10 9999");
        assertEquals(9999, result.extractedArg());
    }

    @Test
    public void testParseArgs_monthAndDetailedFlags_correctlyParsesBoth() throws InvalidArgumentInput {
        ViewLog.Parsed result = viewLog.parseArgs("-m 5 -d");

        assertEquals(5, result.ym().getMonthValue());
        assertTrue(result.detailed());
    }

    @Test
    public void testParseArgs_yearMonthPageDetailed_correctlyParsesAll() throws InvalidArgumentInput {
        ViewLog.Parsed result = viewLog.parseArgs("-ym 2023 7 2 -d");

        assertEquals(2023, result.ym().getYear());
        assertEquals(7, result.ym().getMonthValue());
        assertEquals(2, result.extractedArg());
        assertTrue(result.detailed());
    }

    @Test
    public void testParseArgs_detailedFlagBeforeMonth_correctlyParsesAll() throws InvalidArgumentInput {
        ViewLog.Parsed result = viewLog.parseArgs("-d -m 6");

        assertEquals(6, result.ym().getMonthValue());
        assertTrue(result.detailed());
    }
}
