package seedu.fitchasers.user;

import seedu.fitchasers.exceptions.InvalidArgumentInput;
import seedu.fitchasers.storage.FileHandler;
import seedu.fitchasers.ui.Parser;
import seedu.fitchasers.ui.UI;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

//@@bennyy117
/**
 * Handles the recording and viewing of weight data for a person.
 */
public class WeightManager {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yy");

    private final Person currentUser;
    private final UI ui = new UI();
    private final Parser parser = new Parser();

    public WeightManager(Person person) {
        this.currentUser = person;
    }

    public static final class AddResult {
        public enum Action { CREATED, UPDATED }
        public final Action action;
        public final double weight;
        public final LocalDate date;
        public final String message; // optional convenience
        public AddResult(Action action, double weight, LocalDate date, String message) {
            this.action = action; this.weight = weight; this.date = date; this.message = message;
        }
    }

    public void addWeight(String argumentStr) {
        try {
            // 1) If the user typed nothing
            if (argumentStr == null || argumentStr.trim().isEmpty()) {
                ui.showMessage("Please enter valid arguments: /add_weight w/WEIGHT [d/DD/MM/YY]");
                return;
            }

            // 2) Delegate all parsing & optional confirmation to Parser
            Parser.AddWeightArgs args = parser.parseAddWeight(argumentStr.trim());

            double weightValue = args.weight;
            LocalDate entryDate = args.date;

            // 3) Validate business rules
            if (!isValidWeight(weightValue)) {
                ui.showError("Weight must be between 20 and 500 kg.");
                return;
            }

            if (entryDate.isAfter(LocalDate.now())) {
                ui.showError("The date you entered (" + entryDate.format(DATE_FORMAT)
                        + ") is in the future. Please use a valid date.");
                return;
            }

            // 4) Create or update record
            if (currentUser.hasWeightRecordOn(entryDate)) {
                currentUser.updateWeightRecord(entryDate, weightValue);
                ui.showMessage("A weight entry already exists for "
                        + entryDate.format(DATE_FORMAT) + ". The record has been updated.");
            } else {
                currentUser.addWeightRecord(new WeightRecord(weightValue, entryDate));
                ui.showMessage("Logging your weight... don't lie to me!\n"
                        + "Recorded weight " + weightValue + " kg for " + entryDate.format(DATE_FORMAT) + ".");
            }

            // 5) Save to file
            try {
                new FileHandler().saveWeightList(currentUser);
            } catch (IOException ioe) {
                ui.showError("Failed to save weight data: " + ioe.getMessage());
            }

        } catch (InvalidArgumentInput ex) {
            ui.showError(ex.getMessage());
        } catch (Exception ex) {
            ui.showError("Unexpected error: " + ex.getMessage());
        }
    }
    
//    /**
//     * Adds a new weight entry.
//     * Command examples:
//     *   /add_weight w/65
//     *   /add_weight w/65 d/25/10/25
//     *
//     * @param command full command string containing weight and optionally a date
//     */
//    public void addWeight(String command) {
//        if (command == null || command.trim().isEmpty()) {
//            ui.showMessage("Please enter a valid command: /add_weight w/WEIGHT [d/DATE]");
//            return;
//        }
//
//        String weightString = extractBetween(command);
//        String dateString = extractAfter(command);
//
//        if (weightString.isEmpty()) {
//            ui.showMessage("Missing weight value. Example: /add_weight w/65 d/10/10/25");
//            return;
//        }
//
//        double weightValue;
//        try {
//            weightValue = Double.parseDouble(weightString.trim());
//            if (!isValidWeight(weightValue)) {
//                return; // error message handled in helper
//            }
//        } catch (NumberFormatException e) {
//            ui.showMessage("Invalid weight. Please enter a number (e.g., 65 or 65.5).");
//            return;
//        }
//
//        if (dateString.isEmpty()) {
//            String todayStr = LocalDate.now().format(DATE_FORMAT);
//            ui.showMessage("Looks like you missed the date. Use current date (" + todayStr + ")? (Y/N)");
//            if (parser.confirmationMessage()) {
//                dateString = todayStr;
//            } else {
//                ui.showMessage("Please provide a date in format dd/MM/yy.");
//                return;
//            }
//        }
//
//        LocalDate entryDate;
//        try {
//            entryDate = LocalDate.parse(dateString.trim(), DATE_FORMAT);
//        } catch (DateTimeParseException e) {
//            ui.showMessage("Invalid date format. Use dd/MM/yy (e.g., 28/10/25).");
//            return;
//        }
//
//        // Check if date is in the future
//        if (entryDate.isAfter(LocalDate.now())) {
//            ui.showMessage("The date you entered (" + entryDate.format(DATE_FORMAT)
//                    + ") is in the future. Please use a valid date.");
//            return;
//        }
//
//        // If record for the same date already exists, update and remove duplicates
//        if (currentUser.hasWeightRecordOn(entryDate)) {
//            ui.showMessage("A weight entry already exists for " + entryDate.format(DATE_FORMAT)
//                    + ". The record will be updated.");
//            currentUser.updateWeightRecord(entryDate, weightValue);
//        } else {
//            WeightRecord weightRecord = new WeightRecord(weightValue, entryDate);
//            currentUser.addWeightRecord(weightRecord);
//        }
//
//        ui.showMessage("Logging your weight... don't lie to me!\n"
//                        + "Recorded weight " + weightValue + " kg for " + entryDate.format(DATE_FORMAT) + ".");
//
//        try {
//            FileHandler fileHandler = new FileHandler();
//            fileHandler.saveWeightList(currentUser);
//        } catch (IOException e) {
//            ui.showMessage("Failed to save weight data: " + e.getMessage());
//        }
//    }

    /**
     * Displays all weight records for the current user.
     */
    public void viewWeights() {
        currentUser.displayWeightHistory();
    }

//    // ----------------- Helper methods -----------------
//
//    private String extractBetween(String text) {
//        int startIndex = text.indexOf("w/");
//        int endIndex = text.indexOf("d/");
//        if (startIndex == -1) {
//            return "";
//        }
//        if (endIndex == -1 || endIndex < startIndex) {
//            return text.substring(startIndex + "w/".length()).trim();
//        }
//        return text.substring(startIndex + "w/".length(), endIndex).trim();
//    }
//
//    private String extractAfter(String text) {
//        int startIndex = text.indexOf("d/");
//        if (startIndex == -1) {
//            return "";
//        }
//        return text.substring(startIndex + "d/".length()).trim();
//    }

//    /**
//     * Validates that the weight is a positive number within a realistic range.
//     *
//     * @param weight the weight value to validate
//     * @return true if valid, false otherwise (also prints error messages)
//     */
//    public boolean isValidWeight(double weight) {
//        if (weight <= 0) {
//            ui.showMessage("Weight must be a positive number.");
//            return false;
//        }
//
//        if (weight < 20 || weight > 500) {
//            ui.showMessage("Weight must be between 20 kg and 500 kg.");
//            return false;
//        }
//
//        return true;
//    }

    public boolean isValidWeight(double weight) {
        return weight > 0 && weight >= 20 && weight <= 500;
    }
}
