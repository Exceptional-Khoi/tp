package seedu.fitchasers.user;

import seedu.fitchasers.FileHandler;
import seedu.fitchasers.ui.UI;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Handles the recording and viewing of weight data for a person.
 */
public class WeightManager {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yy");

    private final Person currentUser;
    private final UI ui = new UI();

    public WeightManager(Person person) {
        this.currentUser = person;
    }

    /**
     * Adds a new weight entry.
     * Command examples:
     *   /add_weight w/65
     *   /add_weight w/65 d/25/10/25
     *
     * @param command full command string containing weight and optionally a date
     */
    public void addWeight(String command) {
        if (command == null || command.trim().isEmpty()) {
            ui.showMessage("Please enter a valid command: /add_weight w/WEIGHT [d/DATE]");
            return;
        }

        String weightString = extractBetween(command, "w/", "d/");
        String dateString = extractAfter(command, "d/");

        if (weightString.isEmpty()) {
            ui.showMessage("Missing weight value. Example: /add_weight w/65");
            return;
        }

        double weightValue;
        try {
            weightValue = Double.parseDouble(weightString.trim());
            if (weightValue <= 0) {
                ui.showMessage("Weight must be a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            ui.showMessage("Invalid weight. Please enter a number (e.g., 65 or 65.5).");
            return;
        }

        LocalDate entryDate;
        if (dateString.isEmpty()) {
            entryDate = LocalDate.now();
        } else {
            try {
                entryDate = LocalDate.parse(dateString.trim(), DATE_FORMAT);
            } catch (DateTimeParseException e) {
                ui.showMessage("Invalid date format. Use dd/MM/yy (e.g., 28/10/25).");
                return;
            }
        }

        // Check if date is in the future
        if (entryDate.isAfter(LocalDate.now())) {
            ui.showMessage("The date you entered (" + entryDate.format(DATE_FORMAT)
                    + ") is in the future. Please use a valid date.");
            return;
        }

        // If record for the same date already exists, update and remove duplicates
        if (currentUser.hasWeightRecordOn(entryDate)) {
            ui.showMessage("A weight entry already exists for " + entryDate.format(DATE_FORMAT)
                    + ". The record will be updated.");
            currentUser.updateWeightRecord(entryDate, weightValue);
        } else {
            WeightRecord weightRecord = new WeightRecord(weightValue, entryDate);
            currentUser.addWeightRecord(weightRecord);
        }

        ui.showMessage("Logging your weight... don't lie to me!\n"
                        + "Recorded weight " + weightValue + " kg for " + entryDate.format(DATE_FORMAT) + ".");

        try {
            FileHandler fileHandler = new FileHandler();
            fileHandler.saveWeightList(currentUser);
        } catch (IOException e) {
            ui.showMessage("Failed to save weight data: " + e.getMessage());
        }
    }

    /**
     * Displays all weight records for the current user.
     */
    public void viewWeights() {
        currentUser.displayWeightHistory();
    }

    // ----------------- Helper methods -----------------

    private String extractBetween(String text, String start, String end) {
        int startIndex = text.indexOf(start);
        int endIndex = text.indexOf(end);
        if (startIndex == -1) {
            return "";
        }
        if (endIndex == -1 || endIndex < startIndex) {
            return text.substring(startIndex + start.length()).trim();
        }
        return text.substring(startIndex + start.length(), endIndex).trim();
    }

    private String extractAfter(String text, String start) {
        int startIndex = text.indexOf(start);
        if (startIndex == -1) {
            return "";
        }
        return text.substring(startIndex + start.length()).trim();
    }
}
