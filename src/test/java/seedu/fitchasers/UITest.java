package seedu.fitchasers;

import org.junit.jupiter.api.*;
import seedu.fitchasers.ui.UI;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

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
        String s = new String(outContent.toByteArray(), StandardCharsets.UTF_8);
        outContent.reset();
        return stripAnsi(s);
    }

    private UI uiWithInput(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        return new UI();
    }

    // ---------- tests (no Mockito) ----------

    @Test
    void readCommand_trims_and_echoes_right_bubble() {
        UI ui = uiWithInput("   /help   \n");
        String cmd = ui.readCommand();
        String out = takeOutput();

        assertEquals("/help", cmd);
        assertTrue(out.contains("Enter command >"));
        assertTrue(out.contains("(You)"));
        assertTrue(out.contains("/help"));
    }

    @Test
    void readCommand_eof_returns_null() {
        System.setIn(new ByteArrayInputStream(new byte[0])); // EOF
        UI ui = new UI();
        String cmd = ui.readCommand();
        String out = takeOutput();

        assertNull(cmd);
        assertTrue(out.contains("Enter command >"));
    }

    @Test
    void enterName_rejects_empty_then_accepts_name() {
        UI ui = uiWithInput("\nAlice\n");
        String name = ui.enterName();
        String out = takeOutput();

        assertEquals("Alice", name);
        assertTrue(out.contains("Name cannot be empty"));
        assertTrue(out.contains("(You)"));
        assertTrue(out.contains("Alice"));
    }

    @Test
    void enterWeight_invalid_then_zero_then_valid() {
        // enterWeight tạo Scanner mới từ System.in — chỉ cần preload input
        UI ui = uiWithInput("abc\n0\n60.5\n");
        double w = ui.enterWeight();
        String out = takeOutput();

        assertEquals(60.5, w, 1e-9);
        assertTrue(out.contains("Please enter your initial weight"));
        assertTrue(out.contains("Invalid number"));
        assertTrue(out.contains("Weight must be a positive number"));
    }

    @Test
    void confirmation_yes_true() {
        UI ui = uiWithInput("Yes\n");
        boolean ok = ui.confirmationMessage();
        String out = takeOutput();

        assertTrue(ok);
        assertTrue(out.contains("(You)"));
        assertTrue(out.toLowerCase().contains("yes"));
    }

    @Test
    void confirmation_no_false() {
        UI ui = uiWithInput("no\n");
        boolean ok = ui.confirmationMessage();
        String out = takeOutput();

        assertFalse(ok);
        assertTrue(out.contains("(You)"));
        assertTrue(out.toLowerCase().contains("no"));
    }

    @Test
    void getDaySuffix_works_for_edges() {
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
    void showHelp_contains_key_commands() {
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
    void showGreeting_prints_banner_and_hint() {
        UI ui = uiWithInput("");
        ui.showGreeting();
        String out = takeOutput();

        assertTrue(out.toLowerCase().contains("fitchaser"));
        assertTrue(out.contains("Type /help or h to explore"));
    }

    @Test
    void displayDetailsOfWorkout_null_shows_error() {
        UI ui = uiWithInput("");
        ui.displayDetailsOfWorkout(null);
        String out = takeOutput();

        assertTrue(out.contains("[Oops!]"));
        assertTrue(out.contains("No workout found to display."));
    }
}
