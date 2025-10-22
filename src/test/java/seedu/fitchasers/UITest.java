package seedu.fitchasers;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * Unit tests for the {@link UI} class.
 */
class UITest {

    private UI ui;
    private ByteArrayOutputStream outContent;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        ui = new UI();
        outContent = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    // ----------------------------------------------------
    // Tests for showMessage()
    // ----------------------------------------------------
    @Test
    void showMessage_printsMessageWithResetCode() {
        ui.showMessage("Test message");
        String output = outContent.toString();
        assertTrue(output.contains("Test message"));
        assertTrue(output.contains("\u001B[0m"), "Should reset color at end");
    }

    // ----------------------------------------------------
    // Tests for showError()
    // ----------------------------------------------------
    @Test
    void showError_printsOopsPrefixAndMessage() {
        ui.showError("Invalid input");
        String output = outContent.toString();
        assertTrue(output.contains("[Oops!]"));
        assertTrue(output.contains("Invalid input"));
    }

    // ----------------------------------------------------
    // Tests for showGreeting()
    // ----------------------------------------------------
    @Test
    void showGreeting_containsFitchaserTitleAndHelpHint() {
        ui.showGreeting();
        String output = outContent.toString();
        assertTrue(output.toLowerCase().contains("virtual gym buddy"),
                "Greeting should mention the virtual gym buddy introduction");
        assertTrue(output.contains("/help"), "Greeting should guide user to /help command");
    }

    // ----------------------------------------------------
    // Tests for showExitMessage()
    // ----------------------------------------------------
    @Test
    void showExitMessage_containsFarewellMessage() {
        ui.showExitMessage();
        String output = outContent.toString();
        assertTrue(output.contains("Catch you next time"));
        assertTrue(output.contains("champ"), "Should contain motivational farewell");
    }

    // ----------------------------------------------------
    // Tests for showHelp()
    // ----------------------------------------------------
    @Test
    void showHelp_displaysAllMainCommands() {
        ui.showHelp();
        String output = outContent.toString();

        assertAll(
                () -> assertTrue(output.contains("/help")),
                () -> assertTrue(output.contains("/add_weight")),
                () -> assertTrue(output.contains("/create_workout")),
                () -> assertTrue(output.contains("/add_exercise")),
                () -> assertTrue(output.contains("/exit"))
        );
    }

    // ----------------------------------------------------
    // Tests for showDivider()
    // ----------------------------------------------------
    @Test
    void showDivider_printsLineOfDashes() {
        ui.showDivider();
        String output = outContent.toString();
        assertTrue(output.contains("--------------------------------------------------"));
    }

    // ----------------------------------------------------
    // Tests for readCommand()
    // ----------------------------------------------------
    @Test
    void readCommand_readsAndReturnsTrimmedInput() {
        String simulatedInput = "   /exit   \n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
        ui = new UI(); // recreate with the new System.in
        String command = ui.readCommand();
        assertEquals("/exit", command);
    }
}
