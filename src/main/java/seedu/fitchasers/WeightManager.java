package seedu.fitchasers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Handles the recording and viewing of weight data for a person.
 */
public class WeightManager {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yy");

    private final Person currentUser;
    private final UI uiHandler = new UI();

    public WeightManager(Person person) {
        this.currentUser = person;
    }

    /**
     * Adds a new weight entry.
     * Command example: "/add_weight w/75.2 d/17/10/25"
     *
     * @param command full command string containing weight and date
     */
    public void addWeight(String command) {
        String weightString = extractBetween(command, "w/", "d/");
        String dateString = extractAfter(command, "d/");

        if (weightString.isEmpty() || dateString.isEmpty()) {
            uiHandler.showMessage("Invalid input. Correct format: /add_weight w/WEIGHT d/DATE");
            return;
        }

        try {
            double weightValue = Double.parseDouble(weightString);
            LocalDate entryDate = LocalDate.parse(dateString, DATE_FORMAT);

            WeightRecord weightRecord = new WeightRecord(weightValue, entryDate);
            currentUser.addWeightRecord(weightRecord);
            uiHandler.showMessage("New weight recorded: " + weightRecord);

        } catch (NumberFormatException nfe) {
            uiHandler.showMessage("Invalid weight. Please enter a number.");
        } catch (DateTimeParseException dtpe) {
            uiHandler.showMessage("Invalid date format. Use dd/MM/yy.");
        } catch (Exception e) {
            uiHandler.showMessage("Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Displays all weight records for the person.
     */
    public void viewWeights() {
        currentUser.displayWeightHistory();
    }

    // ----------------- Helper methods -----------------

    private String extractBetween(String text, String start, String end) {
        int startIndex = text.indexOf(start);
        int endIndex = text.indexOf(end);
        if (startIndex == -1 || endIndex == -1 || startIndex + start.length() >= endIndex) {
            return "";
        }
        return text.substring(startIndex + start.length(), endIndex).trim();
    }

    private String extractAfter(String text, String start) {
        int startIndex = text.indexOf(start);
        if (startIndex == -1 || startIndex + start.length() >= text.length()) {
            return "";
        }
        return text.substring(startIndex + start.length()).trim();
    }
}
