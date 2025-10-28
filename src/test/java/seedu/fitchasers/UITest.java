package seedu.fitchasers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.fitchasers.ui.UI;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UITest {

    private PrintStream originalOut;
    private InputStream originalIn;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        originalIn = System.in;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void tearDown() throws IOException {
        System.setOut(originalOut);
        System.setIn(originalIn);
        outContent.close();
    }

    // ---------- helpers ----------
    private static String stripAnsi(String s) {
        return s.replaceAll("\u001B\\[[;\\d]*m", "");
    }

    private String takeOutput() {
        String s = outContent.toString(StandardCharsets.UTF_8);
        outContent.reset();
        return stripAnsi(s);
    }

    private UI uiWithInput(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        return new UI();
    }

    // ---------- tests (no Mockito) ----------

    @Test
    void readCommand_eofReturnsNull() {
        System.setIn(new ByteArrayInputStream(new byte[0])); // EOF
        UI ui = new UI();
        String cmd = ui.readCommand();
        String out = takeOutput();

        assertNull(cmd);
        assertTrue(out.contains("Enter command >"));
    }

    @Test
    void enterWeight_invalidThenZeroThenValid() {
        UI ui = uiWithInput("abc\n0\n60.5\n");
        double w = ui.enterWeight();
        String out = takeOutput();

        assertEquals(60.5, w, 1e-9);
        assertTrue(out.contains("Please enter your initial weight"));
        assertTrue(out.contains("Invalid number"));
        assertTrue(out.contains("Weight must be a positive number"));
    }

    @Test
    void getDaySuffix_worksForEdges() {
        UI ui = uiWithInput("");
        assertEquals("st", ui.getDaySuffix(1));
        assertEquals("nd", ui.getDaySuffix(2));
        assertEquals("rd", ui.getDaySuffix(3));
        assertEquals("th", ui.getDaySuffix(4));
        assertEquals("th", ui.getDaySuffix(11));
        assertEquals("th", ui.getDaySuffix(12));
        assertEquals("th", ui.getDaySuffix(13));
        assertEquals("st", ui.getDaySuffix(21));
        assertEquals("st", ui.getDaySuffix(31));
    }

    @Test
    void showHelp_containsKeyCommands() {
        UI ui = uiWithInput("");
        ui.showHelp();
        String out = takeOutput();

        assertTrue(out.contains("/help (h)"));
        assertTrue(out.contains("/create_workout"));
        assertTrue(out.contains("/view_log"));
        assertTrue(out.contains("/add_weight"));
        assertTrue(out.contains("/gym_where"));
    }

    @Test
    void showGreeting_printsBannerAndHint() {
        UI ui = uiWithInput("");
        ui.showGreeting();
        String out = takeOutput();

        assertTrue(out.toLowerCase().contains("fitchaser"));
        assertTrue(out.contains("Type /help or h to explore"));
    }

    @Test
    void displayDetailsOfWorkout_nullShowsError() {
        UI ui = uiWithInput("");
        ui.displayDetailsOfWorkout(null);
        String out = takeOutput();

        assertTrue(out.contains("[Oops!]"));
        assertTrue(out.contains("No workout found to display."));
    }
}
