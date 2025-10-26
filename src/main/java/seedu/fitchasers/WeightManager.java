package seedu.fitchasers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Handles the recording and viewing of weight data for a person.
 */
public class WeightManager {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yy");

    private final Person currentUser;
    private final UI uiHandler = new UI();
    private final FileHandler fileHandler = new FileHandler();

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
        String weightString;
        String dateString = extractAfter(command, "d/");

        int wIdx = command.indexOf("w/");
        int dIdx = command.indexOf("d/");

        if (wIdx != -1 && dIdx != -1 && dIdx > wIdx) {
            weightString = command.substring(wIdx + 2, dIdx).trim();
        } else if (wIdx != -1) {
            weightString = command.substring(wIdx + 2).trim();
        } else {
            weightString = "";
        }

        // If date is missing, offer to use today's date
        if (dateString.isEmpty()) {
            String todayStr = LocalDate.now().format(DATE_FORMAT);
            uiHandler.showMessage("Looks like you missed the date. Use current date (" + todayStr + ")? (Y/N)");
            if (uiHandler.confirmationMessage()) {
                dateString = todayStr;
            } else {
                uiHandler.showMessage("Please provide a date in format d/DD/MM/YY.");
                return;
            }
        }

        if (weightString.isEmpty()) {
            uiHandler.showMessage("Invalid input. Correct format: /add_weight w/WEIGHT d/DATE");
            return;
        }

        try {
            double weightValue = Double.parseDouble(weightString);
            LocalDate entryDate = LocalDate.parse(dateString, DATE_FORMAT);

            // Check if date is in the future
            if (entryDate.isAfter(LocalDate.now())) {
                uiHandler.showMessage("The date you entered (" + entryDate.format(DATE_FORMAT)
                        + ") is in the future. Please re-enter a valid date that is not in the future");
                return;
            }

            // Check if there's already a weight record for this date
            List<WeightRecord> weightList = currentUser.getWeightHistory();
            if (weightList != null) {
                for (int i = 0; i < weightList.size(); i++) {
                    WeightRecord record = weightList.get(i);
                    if (record.getDate().equals(entryDate)) {
                        uiHandler.showMessage("A weight record already exists for "
                                + entryDate.format(DATE_FORMAT) + ". Overwrite it? (Y/N)");
                        if (!uiHandler.confirmationMessage()) {
                            uiHandler.showMessage("Weight entry cancelled. Please choose another date.");
                            return;
                        }
                        weightList.remove(i); // overwrite confirmed
                        break;
                    }
                }
            }

            // Add record if confirmed
            WeightRecord weightRecord = new WeightRecord(weightValue, entryDate);
            currentUser.addWeightRecord(weightRecord);
            try {
                fileHandler.saveWeightList(currentUser);
                uiHandler.showMessage("Weight saved successfully!");
            } catch (IOException e) {
                uiHandler.showError("Failed to save weight: " + e.getMessage());
            }
            uiHandler.showMessage("Logging your weight... don't lie to me!");
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
