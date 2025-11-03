package seedu.fitchasers.user;

import seedu.fitchasers.exceptions.InvalidArgumentInput;
import seedu.fitchasers.storage.FileHandler;
import seedu.fitchasers.ui.Parser;
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
        // 0) Fast guard for empty input
        if (argumentStr == null || argumentStr.trim().isEmpty()) {
            ui.showMessage("Please enter valid arguments: /add_weight w/WEIGHT [d/DD/MM/YY]");
            return;
        }

        Double weightValue = null;
        LocalDate entryDate = null;

        try {
            // 1) Delegate primary parsing to Parser (preferred path)
            Parser.AddWeightArgs args = parser.parseAddWeight(argumentStr.trim());
            weightValue = args.weight;
            entryDate  = args.date;   // may be null if user omitted d/...

        } catch (InvalidArgumentInput ex) {
            // Parser-level validation error (bad tokens, invalid ranges, etc.)
            ui.showError(ex.getMessage());
            return;
        } catch (NumberFormatException ex) {
            // Make sure we keep the friendlier message from the old version 2
            ui.showMessage("Invalid weight. Please enter a number (e.g., 65 or 65.5).");
            return;
        } catch (Exception ex) {
            ui.showError("Unexpected error: " + ex.getMessage());
            return;
        }

        // 2) If date was omitted, offer to use today; otherwise prompt repeatedly (old v2 behavior)
        if (entryDate == null) {
            String todayStr = LocalDate.now().format(DATE_FORMAT);
            ui.showMessage("Looks like you missed the date. Use current date (" + todayStr + ")? " +
                    "(Y/N, or type /cancel to abort)");

            Boolean confirmed = parser.confirmationMessage();  // null => user aborted during confirm flow
            if (confirmed == null) {
                return; // user aborted confirm
            } else if (confirmed) {
                entryDate = LocalDate.now();
            } else {
                // Re-prompt until a valid date or /cancel
                while (true) {
                    ui.showMessage("Please provide a date in format dd/MM/yy or type /cancel to abort.");
                    String inputDate = parser.readInsideRightBubble("Enter date > ");
                    if (inputDate == null || inputDate.equalsIgnoreCase("/cancel")) {
                        ui.showMessage("Weight entry canceled.");
                        return;
                    }
                    try {
                        entryDate = LocalDate.parse(inputDate.trim(), DATE_FORMAT);
                        break; // valid date captured
                    } catch (DateTimeParseException e) {
                        ui.showError("Invalid date format. Use dd/MM/yy (e.g., 28/10/25) or type /cancel to abort.");
                    }
                }
            }
        }

        // 3) Business rules (preserve clear messages from v1 & v2)
        if (!isValidWeight(weightValue)) {
            ui.showError("Weight must be between 20 and 500 kg.");
            return;
        }

        if (entryDate.isAfter(LocalDate.now())) {
            ui.showError("The date you entered (" + entryDate.format(DATE_FORMAT)
                    + ") is in the future. Please use a valid date.");
            return;
        }

        // 4) Create or update record (keep both message styles)
        if (currentUser.hasWeightRecordOn(entryDate)) {
            // v2 had a “will be updated” message, v1 had a past-tense “has been updated”.
            // Keep the clearer two-step: notify + then perform + success info.
            ui.showMessage("A weight entry already exists for " + entryDate.format(DATE_FORMAT)
                    + ". The record will be updated.");
            currentUser.updateWeightRecord(entryDate, weightValue);
            ui.showMessage("A weight entry already exists for "
                    + entryDate.format(DATE_FORMAT) + ". The record has been updated.");
        } else {
            currentUser.addWeightRecord(new WeightRecord(weightValue, entryDate));
            ui.showMessage("Logging your weight... don't lie to me!\n"
                    + "Recorded weight " + weightValue + " kg for " + entryDate.format(DATE_FORMAT) + ".");
        }

        // 5) Persist to file (v1 & v2 behavior)
        try {
            new FileHandler().saveWeightList(currentUser);
        } catch (IOException ioe) {
            ui.showError("Failed to save weight data: " + ioe.getMessage());
        }
    }


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
