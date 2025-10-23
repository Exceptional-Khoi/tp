package seedu.fitchasers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.time.LocalDate;
import java.util.Map;
import java.util.LinkedHashMap;
import java.time.format.DateTimeFormatter;

/**
 * Represents a person using the FitChasers app.
 * Stores the person's name and their weight history.
 */
public class Person implements Serializable {

    private final UI ui = new UI();
    /** The name of the person */
    private String name;

    /** The list of weight records for the person */
    private final ArrayList<WeightRecord> weightHistory;

    /**
     * Constructs a new Person with the given name.
     * Initializes an empty weight history.
     *
     * @param name The name of the person
     * @throws IllegalArgumentException if name is null or empty
     */
    public Person(String name) {
        setName(name); // reuse setter để kiểm tra name
        this.weightHistory = new ArrayList<>();
    }

    /**
     * Returns the name of the person.
     *
     * @return The person's name
     */
    public String getName() {
        return name;
    }

    /**
     * Updates the name of the person.
     *
     * @param newName The new name
     * @throws IllegalArgumentException if newName is null or empty
     */
    public void setName(String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
        this.name = newName.trim();
    }

    /**
     * Adds a weight record to the person's weight history.
     *
     * @param record The WeightRecord to add
     * @throws NullPointerException if record is null
     */
    public void addWeightRecord(WeightRecord record) {
        Objects.requireNonNull(record, "WeightRecord cannot be null.");
        weightHistory.add(record);
    }

    /**
     * Returns an unmodifiable copy of the weight history.
     *
     * @return A List of WeightRecord objects
     */
    public List<WeightRecord> getWeightHistory() {
        return Collections.unmodifiableList(new ArrayList<>(weightHistory));
    }

    /**
     * Displays the weight history in the console.
     * Prints a message if there are no records.
     */
    public void displayWeightHistory() {
        if (weightHistory.isEmpty()) {
            ui.showMessage(name + " has no weight records yet.");
            return;
        }
        ui.showMessage("Here's your weight, you've been killin' it lately!");
        ui.showMessage("Weight history for " + name + ":");
        for (WeightRecord record : weightHistory) {
            ui.showMessage("  " + record);
        }
    }

    /**
     * Returns the most recent weight recorded for the person.
     *
     * @return The latest weight, or -1 if no records exist
     */
    public double getLatestWeight() {
        if (weightHistory.isEmpty()) {
            return -1; // dùng -1 để phân biệt không có dữ liệu
        }
        return weightHistory.get(weightHistory.size() - 1).getWeight();
    }

    /**
     * Returns the number of weight records.
     *
     * @return the size of the weight history
     */
    public int getWeightHistorySize() {
        return weightHistory.size();
    }

    /**
     * Removes the most recent weight record.
     *
     * @return true if a record was removed, false if no records exist
     */
    public boolean removeLatestWeightRecord() {
        if (weightHistory.isEmpty()) {
            return false;
        }
        weightHistory.remove(weightHistory.size() - 1);
        return true;
    }

    public void displayWeightGraphWithDates() {
        if (weightHistory.isEmpty()) {
            System.out.println(name + " has no weight records yet.");
            return;
        }

        List<Double> weights = new ArrayList<>();
        List<String> dates = new ArrayList<>();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM");

        Map<LocalDate, WeightRecord> latestPerDay = new LinkedHashMap<>();
        for (WeightRecord r : weightHistory) {
            latestPerDay.put(r.getDate(), r);
        }

        for (WeightRecord r : latestPerDay.values()) {
            weights.add(r.getWeight());
            dates.add(r.getDate().format(df));
        }


        double min = Collections.min(weights);
        double max = Collections.max(weights);

        int height = 10;
        int spacing = 12;
        int width = (weights.size() - 1) * spacing + 1;

        char[][] grid = new char[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                grid[i][j] = ' ';
            }
        }

        int[] y = new int[weights.size()];
        for (int i = 0; i < weights.size(); i++) {
            double normalized = (weights.get(i) - min) / (max - min);
            y[i] = height - 1 - (int) Math.round(normalized * (height - 1));
        }

        for (int i = 0; i < weights.size() - 1; i++) {
            int x1 = i * spacing;
            int y1 = y[i];
            int x2 = (i + 1) * spacing;
            int y2 = y[i + 1];
            int dx = x2 - x1;
            int dy = y2 - y1;
            int steps = Math.max(Math.abs(dx), Math.abs(dy));
            for (int s = 0; s <= steps; s++) {
                int x = x1 + s * dx / steps;
                int yy = y1 + s * dy / steps;
                if (yy >= 0 && yy < height && x >= 0 && x < width) {
                    grid[yy][x] = '+';
                }
            }
        }

        boolean[][] isWeightPoint = new boolean[height][width];
        for (int i = 0; i < weights.size(); i++) {
            int x = i * spacing;
            int yy = y[i];
            if (yy >= 0 && yy < height && x < width) {
                grid[yy][x] = '\u25CF';
                isWeightPoint[yy][x] = true;
            }
        }

        final String reset = "\u001B[0m";
        final String orange = "\u001B[1m\u001B[38;5;208m";

        System.out.println("\nWeight Progress Graph for " + name + ":");

        for (int i = 0; i < height; i++) {
            double label = max - (max - min) * i / (height - 1);
            System.out.printf("%6.1f | ", label);
            for (int j = 0; j < width; j++) {
                if (isWeightPoint[i][j]) {
                    System.out.print(orange + "\u25CF" + reset);
                } else {
                    System.out.print(grid[i][j]);
                }
            }
            System.out.println();
        }

        System.out.print("        ");
        for (int j = 0; j < width + 4; j++) {
            System.out.print('_');
        }
        System.out.println();

        System.out.print("        ");
        for (int i = 0; i < dates.size(); i++) {
            int x = i * spacing;
            if (x < width) {
                System.out.print(dates.get(i));
                int extra = spacing - dates.get(i).length();
                for (int k = 0; k < extra && x + k + dates.get(i).length() < width; k++) {
                    System.out.print(' ');
                }
            }
        }
        System.out.println("\n");
    }
}
