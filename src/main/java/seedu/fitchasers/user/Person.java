package seedu.fitchasers.user;

import seedu.fitchasers.ui.UI;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Represents a person using the FitChasers app.
 * Stores the person's name and their weight history, maintaining chronological order.
 */
public class Person {

    private final UI ui = new UI();
    private String name;
    private final ArrayList<WeightRecord> weightHistory = new ArrayList<>();

    /**
     * Constructs a new Person with the given name.
     * Initializes an empty weight history.
     *
     * @param name The name of the person
     * @throws IllegalArgumentException if name is null or empty
     */
    public Person(String name) {
        setName(name);
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
     * @param name The new name
     * @throws IllegalArgumentException if name is null or empty
     */
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
        this.name = name.trim();
    }

    /**
     * Adds a weight record to the person's weight history, maintaining chronological order.
     *
     * @param record The WeightRecord to add
     * @throws NullPointerException if record is null
     */
    public void addWeightRecord(WeightRecord record) {
        Objects.requireNonNull(record, "WeightRecord cannot be null.");
        insertSorted(record);
    }

    /**
     * Inserts a WeightRecord into the sorted weightHistory list.
     * Maintains chronological order (earliest to latest).
     *
     * @param record The record to insert
     */
    private void insertSorted(WeightRecord record) {
        int index = 0;
        while (index < weightHistory.size() &&
                weightHistory.get(index).getDate().isBefore(record.getDate())) {
            index++;
        }
        weightHistory.add(index, record);
    }

    /**
     * Returns an unmodifiable copy of the weight history.
     *
     * @return A List of WeightRecord objects
     */
    public List<WeightRecord> getWeightHistory() {
        return List.copyOf(weightHistory);
    }

    /**
     * Displays the weight history in chronological order.
     * Prints a message if there are no records.
     */
    public void displayWeightHistory() {
        if (weightHistory.isEmpty()) {
            ui.showMessage(name + " has no weight records yet.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Here's your weight, you've been killing it lately!\n");
        for (int i = 0; i < weightHistory.size(); i++) {
            sb.append("  ").append(weightHistory.get(i));
            if (i < weightHistory.size() - 1) {
                sb.append("\n");
            }
        }
        ui.showMessage(sb.toString());
    }

    /**
     * Returns the most recent weight recorded for the person.
     * Since the list is sorted, the last element is the latest.
     *
     * @return The latest weight, or -1 if no records exist
     */
    public double getLatestWeight() {
        return weightHistory.isEmpty() ? -1 : weightHistory.get(weightHistory.size() - 1).getWeight();
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

    /**
     * Displays a line graph of weight progression with dates.
     * Uses the sorted weightHistory directly.
     */
    public void displayWeightGraphWithDates() {
        if (weightHistory.isEmpty()) {
            ui.showMessage("No weight records to display.");
            return;
        }

        List<Double> weights = weightHistory.stream()
                .map(WeightRecord::getWeight)
                .collect(Collectors.toList());

        List<String> dates = weightHistory.stream()
                .map(r -> r.getDate().format(DateTimeFormatter.ofPattern("dd/MM")))
                .collect(Collectors.toList());

        int height = 10;
        int spacing = 12;
        int maxWidthColumns = 12;

        List<Double> displayWeights = new ArrayList<>();
        List<String> displayDates = new ArrayList<>();

        if (weights.size() > maxWidthColumns) {
            double step = (double) (weights.size() - 1) / (maxWidthColumns - 1);
            for (int i = 0; i < maxWidthColumns; i++) {
                int idx = (int) Math.round(i * step);
                displayWeights.add(weights.get(idx));
                displayDates.add(dates.get(idx));
            }
        } else {
            displayWeights.addAll(weights);
            displayDates.addAll(dates);
        }

        double min = Collections.min(displayWeights);
        double max = Collections.max(displayWeights);
        int width = (displayWeights.size() - 1) * spacing + 1;

        if (min == max) {
            max = min + 1;
        }

        char[][] grid = new char[height][width];
        for (int i = 0; i < height; i++) {
            Arrays.fill(grid[i], ' ');
        }

        int[] y = new int[displayWeights.size()];
        for (int i = 0; i < displayWeights.size(); i++) {
            double normalized = (displayWeights.get(i) - min) / (max - min);
            y[i] = height - 1 - (int) Math.round(normalized * (height - 1));
        }

        for (int i = 0; i < displayWeights.size() - 1; i++) {
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
        for (int i = 0; i < displayWeights.size(); i++) {
            int x = i * spacing;
            int yy = y[i];
            if (yy >= 0 && yy < height && x < width) {
                grid[yy][x] = '\u25CF';
                isWeightPoint[yy][x] = true;
            }
        }

        final String reset = "\u001B[0m";
        final String orange = "\u001B[1m\u001B[38;5;208m";

        ui.showMessage("Weight Progress Graph for " + name + ":");

        if (displayWeights.size() == 1) {
            double w = displayWeights.get(0);
            System.out.printf("%6.1f | %s\u25CF%s\n", w, orange, reset);
            System.out.print("        ");
            for (int j = 0; j < displayDates.get(0).length(); j++) {
                System.out.print('_');
            }
            System.out.println();
            System.out.print("        ");
            System.out.println(displayDates.get(0));
            return;
        }

        for (int i = 0; i < height; i++) {
            double label = max - (max - min) * i / (height - 1);
            System.out.printf("%6.1f | ", label);
            System.out.print(" ");
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
        for (int i = 0; i < displayDates.size(); i++) {
            int x = i * spacing;
            if (x < width) {
                System.out.print(displayDates.get(i));
                int extra = spacing - displayDates.get(i).length();
                for (int k = 0; k < extra && x + k + displayDates.get(i).length() < width; k++) {
                    System.out.print(' ');
                }
            }
        }
        System.out.println("\n");
    }

    /**
     * Replaces the current weight history with a new list, sorted chronologically.
     *
     * @param history The new list of weight records
     */
    public void setWeightHistory(List<WeightRecord> history) {
        this.weightHistory.clear();
        List<WeightRecord> sorted = new ArrayList<>(history);
        sorted.sort(Comparator.comparing(WeightRecord::getDate));
        this.weightHistory.addAll(sorted);
    }

    /**
     * Checks if there is at least one weight record on the given date.
     *
     * @param date the date to check
     * @return true if a record exists for that date, false otherwise
     */
    public boolean hasWeightRecordOn(LocalDate date) {
        return weightHistory.stream().anyMatch(r -> r.getDate().equals(date));
    }

    /**
     * Updates the weight record for the given date if it exists.
     * If multiple records exist for the same date, only the first is kept,
     * the rest are removed. The updated record is re-inserted to maintain order.
     *
     * @param date the date to update
     * @param newWeight the new weight value
     */
    public void updateWeightRecord(LocalDate date, double newWeight) {
        WeightRecord mainRecord = null;
        List<WeightRecord> duplicates = new ArrayList<>();

        for (WeightRecord record : weightHistory) {
            if (record.getDate().equals(date)) {
                if (mainRecord == null) {
                    mainRecord = record;
                    mainRecord.setWeight(newWeight);
                } else {
                    duplicates.add(record);
                }
            }
        }

        if (mainRecord != null) {
            weightHistory.removeAll(duplicates);
            weightHistory.remove(mainRecord);
            insertSorted(mainRecord);
        }
    }
}
