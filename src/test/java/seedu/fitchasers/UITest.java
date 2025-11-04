package seedu.fitchasers;

import seedu.fitchasers.ui.UI;
import seedu.fitchasers.user.Person;
import seedu.fitchasers.user.WeightManager;
import seedu.fitchasers.workouts.Exercise;
import seedu.fitchasers.workouts.Workout;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@@author bennyy117
class UITest {

    private final PrintStream realOut = System.out;
    private final InputStream realIn = System.in;
    private ByteArrayOutputStream outContent;

    private static String stripAnsi(String s) {
        if (s == null) {
            return "";
        }
        return s.replaceAll("\u001B\\[[;\\d]*m", "");
    }

    private void setInput(String data) {
        System.setIn(new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8)));
    }

    private String outPlain() {
        return stripAnsi(outContent.toString(StandardCharsets.UTF_8)).replaceAll("\\r\\n", "\n");
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

    // ===================================================================
    // 1. readCommand
    // ===================================================================
    @Test
    void readCommand_returnsTrimmedInput_andDoesNotEcho() {
        setInput("  hello world  \n");
        UI ui = new UI();
        assertEquals("hello world", ui.readCommand());
        String out = outPlain();
        assertTrue(out.contains("(You)"));
        assertTrue(out.contains("Enter command > "));
        assertFalse(out.contains("hello world"));
        assertTrue(out.contains("{^o^} FitChasers"));
    }

    @Test
    void readCommand_emptyInput_returnsEmptyString() {
        setInput("\n");
        UI ui = new UI();
        assertEquals("", ui.readCommand());
    }

    // ===================================================================
    // 2. enterSelection
    // ===================================================================
    @Test
    void enterSelection_emptyInput_showsErrorAndReturnsNull() {
        setInput("\n");
        UI ui = new UI();
        assertNull(ui.enterSelection());
        assertTrue(outPlain().contains("[Oops!] No input detected."));
    }

    @Test
    void enterSelection_validInput_returnsTrimmed() {
        setInput("1 2 3\n");
        UI ui = new UI();
        assertEquals("1 2 3", ui.enterSelection());
    }

    // ===================================================================
    // 3. enterName
    // ===================================================================
    @Test
    void enterName_empty_repromptsValid() {
        setInput("\n\n\nAlice\n");
        UI ui = new UI();
        assertEquals("Alice", ui.enterName());
        String out = outPlain();
        assertTrue(out.contains("Name cannot be empty"), "Should show empty name error");
    }

    @Test
    void enterName_tooLong_reprompts() {
        setInput("A".repeat(31) + "\nBob\n");
        UI ui = new UI();
        assertEquals("Bob", ui.enterName());
        assertTrue(outPlain().contains("Name is too long"));
    }

    @Test
    void enterName_invalidChars_reprompts() {
        setInput("Alice!\nBob\n");
        UI ui = new UI();
        assertEquals("Bob", ui.enterName());
        assertTrue(outPlain().contains("Name can only contain"));
    }

    // ===================================================================
    // 4. enterWeight
    // ===================================================================
    @Test
    void enterWeight_invalid_returnsValid() {
        setInput("abc\n-5\n0\n19.9\n500.1\n60.5\n");
        Person person = new Person("Test");
        WeightManager wm = new WeightManager(person);
        UI ui = new UI();
        assertEquals(60.5, ui.enterWeight(wm), 1e-9);
        String out = outPlain();
        assertTrue(out.contains("Invalid number"));
        assertTrue(out.contains("Weight must be a positive number"));
        assertTrue(out.contains("between 20 kg and 500 kg"));
    }

    @Test
    void enterWeight_eof_returnsMinusOne() {
        setInput("");
        Person person = new Person("Test");
        WeightManager wm = new WeightManager(person);
        UI ui = new UI();
        assertEquals(-1, ui.enterWeight(wm), 1e-9);
        assertTrue(outPlain().contains("No input detected"));
    }

    // ===================================================================
    // 5. showMessage, showError
    // ===================================================================
    @Test
    void showMessage_printsLeftBubble() {
        UI ui = new UI();
        ui.showMessage("Hello");
        String out = outPlain();
        assertTrue(out.contains("Hello"));
        assertTrue(out.contains("+"));
        assertTrue(out.contains("|"));
    }

    @Test
    void showError_prefixesOops() {
        UI ui = new UI();
        ui.showError("Test error");
        assertTrue(outPlain().contains("[Oops!] Test error"));
    }

    // ===================================================================
    // 6. showGreeting, showHelp, etc.
    // ===================================================================
    @Test
    void showGreeting_displaysAsciiAndHint() {
        UI ui = new UI();
        ui.showGreeting();
        String out = outPlain();
        assertTrue(out.contains("Type /help or h"));
        assertTrue(out.contains("crush your fitness goals"));
    }

    @Test
    void showQuickStartTutorial_displaysGuide() {
        UI ui = new UI();
        ui.showQuickStartTutorial();
        assertTrue(outPlain().contains("QUICK START"));
    }

    @Test
    void showHelp_displaysAllCommands() {
        UI ui = new UI();
        ui.showHelp();
        assertTrue(outPlain().contains("/exit"));
    }

    @Test
    void showExitMessage_displaysGoodbye() {
        UI ui = new UI();
        ui.showExitMessage();
        assertTrue(outPlain().contains("Catch you next time"));
    }

    // ===================================================================
    // 7. confirmationMessage
    // ===================================================================
    @Test
    void confirmationMessage_yesVariants_returnTrue() {
        for (String yes : List.of("y", "Y", "yes", "YES", "Yes")) {
            setInput(yes + "\n");
            UI ui = new UI();
            assertTrue(ui.confirmationMessage());
        }
    }

    @Test
    void confirmationMessage_noVariants_returnFalse() {
        for (String no : List.of("n", "N", "no", "NO", "No")) {
            setInput(no + "\n");
            UI ui = new UI();
            assertFalse(ui.confirmationMessage());
        }
    }

    @Test
    void confirmationMessage_invalid_reprompts() {
        setInput("maybe\ninvalid\nn\n");
        UI ui = new UI();
        assertFalse(ui.confirmationMessage());
        assertTrue(outPlain().contains("Please answer Y or N"));
    }

    @Test
    void confirmationMessageWithCancel_cancel_returnsNull() {
        setInput("/cancel\n");
        UI ui = new UI();
        assertNull(ui.confirmationMessageWithCancel());
    }

    @Test
    void confirmationMessageWithCancel_help_showsHelp() {
        setInput("/help\nn\n");
        UI ui = new UI();
        assertFalse(ui.confirmationMessage());
        assertTrue(outPlain().contains("/exit"));
    }

    // ===================================================================
    // 8. displayDetailsOfWorkout
    // ===================================================================
    @Test
    void displayDetailsOfWorkout_fullWorkout_displaysAll() {
        UI ui = new UI();
        LocalDateTime now = LocalDateTime.now();
        Workout w = new Workout("Leg Day", now.minusMinutes(90), now);
        Exercise e = new Exercise("Squat", 12);
        e.addSet(10);
        e.addSet(8);
        w.addExercise(e);

        ui.displayDetailsOfWorkout(w);
        String out = outPlain();

        assertTrue(out.contains("Leg Day"));
        assertTrue(out.contains("Duration"));
        assertTrue(out.contains("Squat"));
        assertTrue(out.contains("reps"));
    }

    @Test
    void displayDetailsOfWorkout_noExercises_showsNone() {
        UI ui = new UI();
        Workout w = new Workout("Empty", LocalDateTime.now(), LocalDateTime.now());
        ui.displayDetailsOfWorkout(w);
        String out = outPlain();

        assertTrue(out.contains("Empty"));
        assertTrue(out.contains("Duration"));
    }

    // ===================================================================
    // 9. getDaySuffix
    // ===================================================================
    @Test
    void getDaySuffix_allCases() {
        UI ui = new UI();
        assertEquals("st", ui.getDaySuffix(1));
        assertEquals("nd", ui.getDaySuffix(2));
        assertEquals("rd", ui.getDaySuffix(3));
        assertEquals("th", ui.getDaySuffix(4));
        assertEquals("th", ui.getDaySuffix(11));
        assertEquals("st", ui.getDaySuffix(21));
        assertEquals("st", ui.getDaySuffix(31));
    }

    // ===================================================================
    // 10. readInsideRightBubble
    // ===================================================================
    @Test
    void readInsideRightBubble_longInput_wrapsAndTrims() {
        String longInput = "A".repeat(200) + "\n";
        setInput(longInput);
        UI ui = new UI();
        String result = ui.readInsideRightBubble("Enter > ");
        assertEquals("A".repeat(200), result);
        String out = outPlain();
        assertTrue(out.contains("Enter > "));
        assertTrue(out.contains("{^o^} FitChasers"));
    }

    @Test
    void readInsideRightBubble_eof_returnsNull() {
        setInput("");
        UI ui = new UI();
        String result = ui.readInsideRightBubble("Enter > ");
        assertNull(result, "Should return null on EOF");

        String out = outPlain();
        assertTrue(out.contains("Enter > "), "UI prints prompt even on EOF (current behavior)");
    }

    // ===================================================================
    // 11. leftBubble – reflection
    // ===================================================================
    @Test
    void leftBubble_multiLine_wrapsCorrectly() throws Exception {
        var method = UI.class.getDeclaredMethod("leftBubble", String.class);
        method.setAccessible(true);
        UI ui = new UI();

        String message = "Short\n" + "A".repeat(200) + "\nLine with spaces   \n\n";

        String bubble = (String) method.invoke(ui, message);
        String plain = stripAnsi(bubble);

        List<String> lines = plain.lines().toList();
        assertTrue(lines.stream().anyMatch(l -> l.contains("Short")));
        assertTrue(lines.stream().anyMatch(l -> l.contains("A".repeat(50))));

        int maxWidth = 140;
        for (String line : lines) {
            if (line.contains("|")) {
                String content = line.substring(line.indexOf("|") + 1, line.lastIndexOf("|")).trim();
                if (!content.isEmpty()) {
                    assertTrue(content.length() <= maxWidth,
                            "Line too long: '" + content + "' (" + content.length() + " chars)");
                }
            }
        }
    }

    // ===================================================================
    // 12. private methods
    // ===================================================================
    @Test
    void privateMethods_viaReflection() throws Exception {
        var strip = UI.class.getDeclaredMethod("stripAnsi", String.class);
        var wrap = UI.class.getDeclaredMethod("wrapLine", String.class, int.class);
        var clamp = UI.class.getDeclaredMethod("clampNonNeg", int.class);
        strip.setAccessible(true);
        wrap.setAccessible(true);
        clamp.setAccessible(true);
        UI ui = new UI();

        assertEquals("hello", strip.invoke(ui, "\u001B[31mhello\u001B[0m"));

        @SuppressWarnings("unchecked")
        List<String> wrapped = (List<String>) wrap.invoke(ui, "ABCDEFGHIJ", 3);
        assertEquals(List.of("ABC", "DEF", "GHI", "J"), wrapped);

        assertEquals(0, clamp.invoke(ui, -5));
        assertEquals(10, clamp.invoke(ui, 10));
    }
}
