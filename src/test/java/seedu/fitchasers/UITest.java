package seedu.fitchasers;

import org.junit.jupiter.api.*;
import seedu.fitchasers.ui.UI;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class UITest {
    private final PrintStream realOut = System.out;
    private ByteArrayOutputStream outContent;

    // Strip ANSI without regex
    private static String stripAnsi(String s) {
        if (s == null) return "";
        StringBuilder b = new StringBuilder(s.length());
        boolean esc = false, bracket = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!esc) {
                if (c == 27) { esc = true; bracket = false; }
                else b.append(c);
            } else {
                if (!bracket && c == '[') bracket = true;
                if (bracket && c == 'm') esc = false;
            }
        }
        return b.toString();
    }

    private String outPlain() {
        return stripAnsi(outContent.toString(StandardCharsets.UTF_8));
    }

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void tearDown() {
        System.setOut(realOut);
    }

    @Test
    void printLeftHeader_printsBotHeader() {
        UI ui = new UI();
        ui.printLeftHeader();
        String out = outPlain();
        assertTrue(out.contains("{^o^} FitChasers"),
                "Header should contain the bot header text.");
    }

    @Test
    void showMessage_printsLeftBubbleWithContent() {
        UI ui = new UI();
        ui.showMessage("Hello, world!");

        String out = outPlain();
        assertTrue(out.contains("Hello, world!"), "Bubble should contain message text.");
        assertTrue(out.contains("+") && out.contains("|"),
                "Should render a framed left bubble.");
    }

    @Test
    void showError_prefixesOops_andPrintsBubble() {
        UI ui = new UI();
        ui.showError("Something went wrong.");

        String out = outPlain();
        assertTrue(out.contains("[Oops!] Something went wrong."),
                "Error bubble should be prefixed with [Oops!]");
    }

    @Test
    void showGreeting_containsHelpHint_andBubbleAfterAsciiArt() {
        UI ui = new UI();
        ui.showGreeting();

        String out = outPlain();
        assertTrue(out.contains("Type /help or h to explore"),
                "Greeting should include the help hint.");
        // left bubble frame present
        assertTrue(out.contains("+") && out.contains("|"),
                "Should show a left bubble after the ASCII art title.");
    }

    @Test
    void showHelp_containsKeySections() {
        UI ui = new UI();
        ui.showHelp();

        String out = outPlain();
        assertTrue(out.contains("/help (h)"),
                "Help should list /help.");
        assertTrue(out.contains("~~~ USER PROFILE ~~~"),
                "Help should include USER PROFILE section.");
        assertTrue(out.contains("~~~ WEIGHT TRACKING ~~~"),
                "Help should include WEIGHT TRACKING section.");
        assertTrue(out.contains("~~~ SYSTEM ~~~"),
                "Help should include SYSTEM section.");
    }

    @Test
    void getDaySuffix_basicCases() {
        UI ui = new UI();

        assertEquals("st", ui.getDaySuffix(1));
        assertEquals("nd", ui.getDaySuffix(2));
        assertEquals("rd", ui.getDaySuffix(3));
        assertEquals("th", ui.getDaySuffix(4));
        assertEquals("th", ui.getDaySuffix(11));
        assertEquals("th", ui.getDaySuffix(12));
        assertEquals("th", ui.getDaySuffix(13));
        assertEquals("st", ui.getDaySuffix(21));
        assertEquals("nd", ui.getDaySuffix(22));
        assertEquals("rd", ui.getDaySuffix(23));
        assertEquals("th", ui.getDaySuffix(24));
        assertEquals("st", ui.getDaySuffix(31));
    }
}
