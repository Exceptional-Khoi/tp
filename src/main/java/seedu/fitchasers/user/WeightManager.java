package seedu.fitchasers.user;

import seedu.fitchasers.storage.FileHandler;
import seedu.fitchasers.ui.UI;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

//@@author bennyy117
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

        String weightString = extractBetween(command);
        String dateString = extractAfter(command);

        if (weightString.isEmpty()) {
            ui.showMessage("Missing weight value. Example: /add_weight w/65 d/10/10/25");
            return;
        }

        double weightValue;
        try {
            weightValue = Double.parseDouble(weightString.trim());
            if (!isValidWeight(weightValue)) {
                return;
            }
        } catch (NumberFormatException e) {
            ui.showMessage("Invalid weight. Please enter a number (e.g., 65 or 65.5).");
            return;
        }

        if (dateString.isEmpty()) {
            String todayStr = LocalDate.now().format(DATE_FORMAT);
            ui.showMessage("Looks like you missed the date. Use current date (" + todayStr + ")? " +
                    "(Y/N, or type /cancel to abort)");

            Boolean confirmed = ui.confirmationMessage();
            if (confirmed == null) {
                return;
            } else if (confirmed) {
                dateString = todayStr;
            } else {
                while (true) {
                    ui.showMessage("Please provide a date in format dd/MM/yy or type /cancel to abort.");
                    String inputDate = ui.readInsideRightBubble("Enter date > ");
                    if (inputDate == null || inputDate.equalsIgnoreCase("/cancel")) {
                        ui.showMessage("Weight entry canceled.");
                        return;
                    }
                    try {
                        LocalDate.parse(inputDate.trim(), DATE_FORMAT);
                        dateString = inputDate.trim();
                        break;
                    } catch (DateTimeParseException e) {
                        ui.showError("Invalid date format. Use dd/MM/yy (e.g., 28/10/25) or type /cancel to abort.");
                    }
                }
            }
        }

        LocalDate entryDate;
        try {
            entryDate = LocalDate.parse(dateString.trim(), DATE_FORMAT);
        } catch (DateTimeParseException e) {
            ui.showMessage("Invalid date format. Use dd/MM/yy (e.g., 28/10/25).");
            return;
        }

        if (entryDate.isAfter(LocalDate.now())) {
            ui.showMessage("The date you entered (" + entryDate.format(DATE_FORMAT)
                    + ") is in the future. Please use a valid date.");
            return;
        }

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

    private String extractBetween(String text) {
        int startIndex = text.indexOf("w/");
        int endIndex = text.indexOf("d/");
        if (startIndex == -1) {
            return "";
        }
        if (endIndex == -1 || endIndex < startIndex) {
            return text.substring(startIndex + "w/".length()).trim();
        }
        return text.substring(startIndex + "w/".length(), endIndex).trim();
    }

    private String extractAfter(String text) {
        int startIndex = text.indexOf("d/");
        if (startIndex == -1) {
            return "";
        }
        return text.substring(startIndex + "d/".length()).trim();
    }

    /**
     * Validates that the weight is a positive number within a realistic range.
     *
     * @param weight the weight value to validate
     * @return true if valid, false otherwise (also prints error messages)
     */
    public boolean isValidWeight(double weight) {
        if (weight <= 0) {
            ui.showMessage("Weight must be a positive number.");
            return false;
        }

        if (weight < 20 || weight > 500) {
            ui.showMessage("Weight must be between 20 kg and 500 kg.");
            return false;
        }

        return true;
    }
}
