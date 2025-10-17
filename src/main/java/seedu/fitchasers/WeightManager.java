package seedu.fitchasers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Handles the recording and viewing of weight data for a person.
 */
public class WeightManager {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yy");

    private final Person person;
    private final UI ui = new UI();

    public WeightManager(Person person) {
        this.person = person;
    }

    /**
     * Adds a new weight entry.
     * Command example: "/add_weight w/75.2 d/17/10/25"
     *
     * @param command full command string containing weight and date
     */
    public void addWeight(String command) {
        String weightPart = getBetween(command, "w/", "d/");
        String datePart = getAfter(command, "d/");

        if (weightPart.isEmpty() || datePart.isEmpty()) {
            ui.showMessage("Invalid input. Correct format: /add_weight w/WEIGHT d/DATE");
            return;
        }

        try {
            double weight = Double.parseDouble(weightPart);
            LocalDate date = LocalDate.parse(datePart, FORMATTER);

            WeightRecord record = new WeightRecord(weight, date);
            person.addWeightRecord(record);
            ui.showMessage("New weight recorded: " + record);

        } catch (NumberFormatException nfe) {
            ui.showMessage("Invalid weight. Please enter a number.");
        } catch (DateTimeParseException dtpe) {
            ui.showMessage("Invalid date format. Use dd/MM/yy.");
        } catch (Exception e) {
            ui.showMessage("Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Prints all recorded weights.
     */
    public void viewWeights() {
        person.displayWeightHistory();
    }

    // ----------------- Utility methods -----------------

    private String getBetween(String text, String start, String end) {
        int s = text.indexOf(start);
        int e = text.indexOf(end);
        if (s == -1 || e == -1 || s + start.length() >= e) {
            return "";
        }
        return text.substring(s + start.length(), e).trim();
    }

    private String getAfter(String text, String start) {
        int s = text.indexOf(start);
        if (s == -1 || s + start.length() >= text.length()) {
            return "";
        }
        return text.substring(s + start.length()).trim();
    }
}
