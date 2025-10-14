package seedu.fitchasers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Manages weight records for a specific person.
 * Provides functionality to add a new weight and view weight history.
 */
public class WeightManager {

    /** The person whose weight records are being managed */
    private final Person person;
    private final UI ui = new UI();

    /**
     * Constructs a WeightManager for the given person.
     *
     * @param person The person whose weights will be managed
     */
    public WeightManager(Person person) {
        this.person = person;
    }

    /**
     * Adds a weight record for the person based on the input command string.
     * Expected format: "/add_weight w/WEIGHT d/DATE"
     * Example: "/add_weight w/81.5 d/19/10/25"
     *
     * @param args The command string containing weight and date information
     */
    public void addWeight(String args) {
        // Format: /add_weight w/81.5 d/19/10/25
        String weightStr = extractBetween(args, "w/", "d/").trim();
        String dateStr = args.substring(args.indexOf("d/") + 2).trim();

        try {
            double weight = Double.parseDouble(weightStr);
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yy"));

            WeightRecord record = new WeightRecord(weight, date);
            person.addWeightRecord(record);
            ui.showMessage("Recorded new weight: " + record);
        } catch (Exception e) {
            ui.showMessage("Invalid format. Example: /add_weight w/81.5 d/19/10/25");
        }
    }

    /**
     * Displays all weight records for the person to the console.
     */
    public void viewWeights() {
        person.displayWeightHistory();
    }

    /**
     * Extracts a substring between two delimiters from the given text.
     *
     * @param text  The full string to extract from
     * @param start The starting delimiter
     * @param end   The ending delimiter
     * @return The substring between the start and end delimiters, or empty string if not found
     */
    private String extractBetween(String text, String start, String end) {
        int startIndex = text.indexOf(start) + start.length();
        int endIndex = text.indexOf(end);
        if (startIndex < start.length() || endIndex == -1) {
            return "";
        }
        return text.substring(startIndex, endIndex);
    }
}
