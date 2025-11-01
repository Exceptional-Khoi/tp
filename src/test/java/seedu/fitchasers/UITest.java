package seedu.fitchasers;

import seedu.fitchasers.ui.UI;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.fitchasers.user.Person;
import seedu.fitchasers.user.WeightManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UIConsoleTest {

    private final PrintStream realOut = System.out;
    private final InputStream realIn = System.in;

    private ByteArrayOutputStream outContent;

    // ===== Helpers =====

    // Strip ANSI without regex or '*'
    private static String stripAnsi(String s) {
        if (s == null) {
            return "";
        }
        StringBuilder b = new StringBuilder(s.length());
        boolean esc = false;
        boolean bracket = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!esc) {
                if (c == 27) { // ESC
                    esc = true;
                    bracket = false;
                } else {
                    b.append(c);
                }
            } else {
                if (!bracket && c == '[') {
                    bracket = true;
                }
                if (bracket && c == 'm') {
                    esc = false; // end of CSI ... m
                }
            }
        }
        return b.toString();
    }

    private void setInput(String data) {
        System.setIn(new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8)));
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
        System.setIn(realIn);
    }

    // ===== Tests =====

    @Test
    void readCommand_returnsTrimmedInput_andDoesNotEchoInput() {
        // Input provided to stdin:
        final String input = "  dalgjagl  \n";
        setInput(input);
        UI ui = new UI();

        String cmd = ui.readCommand();

        assertEquals("dalgjagl", cmd,
                "Returned command must equal trimmed user input of: " +
                        input.replace("\n", "\\n"));

        String out = outPlain();
        assertTrue(out.contains("(You)"),
                "Right bubble header should appear. Input was: " +
                        input.replace("\n", "\\n"));
        assertTrue(out.contains("Enter command > "),
                "Prompt should be printed. Input was: " +
                        input.replace("\n", "\\n"));
        assertFalse(out.contains("dalgjagl"),
                "Program output must not echo typed characters. Input was: " +
                        input.replace("\n", "\\n"));
        assertTrue(out.contains("{^o^} FitChaser"),
                "Left header should reappear after closing bubble.");
    }

    @Test
    void enterSelection_emptyInput_showsErrorAndReturnsNull() {
        // Input provided to stdin:
        final String input = "\n";
        setInput(input);
        UI ui = new UI();

        String sel = ui.enterSelection();

        assertNull(sel, "Should return null when input is empty. Input was: " + input.replace("\n", "\\n"));
        String out = outPlain();
        assertTrue(out.contains("[Oops!] No input detected."),
                "Should display no-input error. Input was: " +
                        input.replace("\n", "\\n"));
    }

    @Test
    void enterName_repromptsOnEmpty_thenReturnsTrimmedName() {
        // Input provided to stdin:
        final String input = "\n   Alice  \n";
        setInput(input);
        UI ui = new UI();

        String name = ui.enterName();

        assertEquals("Alice", name,
                "Should return trimmed name from input: " +
                        input.replace("\n", "\\n"));
        String out = outPlain();
        assertTrue(out.contains("Name cannot be empty. Please try again!"),
                "Should show error for first empty line. Full input sequence: " + input.replace("\n", "\\n"));
    }

    @Test
    void enterWeight_validatesUntilPositiveNumber() {
        // Simulated user input sequence:
        final String input = "abc\n-3\n60.5\n";
        setInput(input);

        Person person = new Person("TestUser");
        WeightManager weightManager = new WeightManager(person);
        UI ui = new UI();

        // Call updated enterWeight method
        double w = ui.enterWeight(weightManager);

        assertEquals(60.5, w, 1e-9,
                "Should accept final valid number from input sequence: " +
                        input.replace("\n", "\\n"));

        String out = outPlain();
        assertTrue(out.contains("Please enter your initial weight (in kg)."),
                "Should prompt for weight. Input sequence: " + input.replace("\n", "\\n"));
        assertTrue(out.contains("Invalid number. Please enter a valid weight"),
                "Should warn for non-numeric 'abc'. Input sequence: " + input.replace("\n", "\\n"));
        assertTrue(out.contains("Weight must be a positive number"),
                "Should warn for non-positive '-3'. Input sequence: " + input.replace("\n", "\\n"));
    }

    @Test
    void confirmationMessage_acceptsYesVariants() {
        // Input provided to stdin:
        final String input = "YeS\n";
        setInput(input);
        UI ui = new UI();

        assertTrue(ui.confirmationMessage(), "Should accept yes variant from input: " + input.replace("\n", "\\n"));
    }

    @Test
    void confirmationMessage_acceptsNoVariants_afterInvalid() {
        // Input provided to stdin:
        final String input = "maybe\nn\n";
        setInput(input);
        UI ui = new UI();

        boolean result = ui.confirmationMessage();
        assertFalse(result,
                "Should return false after 'maybe' then 'n'. Input sequence: " +
                        input.replace("\n", "\\n"));

        String out = outPlain();
        assertTrue(out.contains("Please answer Y or N (yes/no)."),
                "Should re-prompt after invalid 'maybe'. Input sequence: " + input.replace("\n", "\\n"));
    }

    @Test
    void showMessage_printsLeftBubbleWithContent() {
        UI ui = new UI();

        ui.showMessage("Hello, world!");

        String out = outPlain();
        assertTrue(out.contains("Hello, world!"), "Bubble should contain message text 'Hello, world!'");
        assertTrue(out.contains("+") && out.contains("|"),
                "Should render a framed bubble.");
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
    void showGreeting_containsHelpHint() {
        UI ui = new UI();

        ui.showGreeting();

        String out = outPlain();
        assertTrue(out.contains("Type /help or h to explore"),
                "Greeting should include the help hint");
        assertTrue(out.contains("+") && out.contains("|"),
                "Should show the left bubble under the ASCII title");
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
