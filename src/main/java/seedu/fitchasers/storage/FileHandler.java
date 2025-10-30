package seedu.fitchasers.storage;

import seedu.fitchasers.ui.UI;
import seedu.fitchasers.exceptions.FileNonexistent;
import seedu.fitchasers.user.Person;
import seedu.fitchasers.user.WeightRecord;
import seedu.fitchasers.workouts.Workout;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Handles the permanent storage of workout and exercise data.
 * Saves to and loads from: data/save.txt
 * <p>
 * If the "data" folder does not exist, it will be created automatically.
 * <p>
 * File format:
 * Each workout starts with "WORKOUT" and ends with "END_WORKOUT".
 * Exercises are listed between, with all set repetitions joined by commas.
 * <p>
 * Example:
 * WORKOUT | Chest Day | 45
 * EXERCISE | Push Ups | 15,15,12
 * EXERCISE | Bench Press | 12,10,8
 * END_WORKOUT
 */
public class FileHandler {

    private final Path dataDir = Paths.get("data");
    private final Path workoutDir = dataDir.resolve("workouts");
    private final UI ui = new UI();
    private final Map<YearMonth, ArrayList<Workout>> arrayByMonth = new HashMap<>();
    private final Set<YearMonth> onDiskMonths = new HashSet<>();

    /**
     * Initilize index for lazy loading
     *
     * @throws IOException if directory or file creation fails
     */
    public void initIndex() throws IOException {
        ensureDataDir();
        try (var stream = Files.list(dataDir)) {
            stream.map(p -> p.getFileName().toString())
                    .filter(n -> n.startsWith("workouts_") && n.endsWith(".dat"))
                    .map(n -> n.substring(9, 16))
                    .forEach(s -> onDiskMonths.add(YearMonth.parse(s)));
        }
    }

    public Map<YearMonth, ArrayList<Workout>> getArrayByMonth() {
        return arrayByMonth;
    }

    public ArrayList<Workout> getWorkoutsForMonth(YearMonth targetMonth) {
        return arrayByMonth.computeIfAbsent(targetMonth, month -> {
            try {
                return loadMonthList(month);
            } catch (Exception e) {
                return new ArrayList<>();
            }
        });
    }

    /**
     * Ensures that the save file and its parent directory exist.
     *
     * @throws IOException if directory or file creation fails
     */
    private void ensureDataDir() throws IOException {
        Files.createDirectories(dataDir);
        Files.createDirectories(workoutDir);
    }

    // ----------------- Workout -----------------
    /**
     * Saves the given month's workout list into a serialized file inside /data/workouts/
     *
     * @param month the month of the workout list
     * @param list  the list of workouts to save
     * @throws IOException if saving fails
     */
    public void saveMonthList(YearMonth month, ArrayList<Workout> list) throws IOException {
        try {
            ensureDataDir();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String filename = String.format("workouts_%s.dat", month);
        Path filePath = workoutDir.resolve(filename);

        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(filePath))) {
            out.writeObject(list);
            ui.showMessage("Saved " + list.size() + " workouts for " + month + ".");
        }
    }

    /**
     * Loads the given month's workout list from a serialized file inside /data/workouts/.
     *
     * @param month the month of the workout list
     * @return the loaded workout list (empty if not found)
     * @throws IOException if loading fails
     */
    @SuppressWarnings("unchecked")
    public ArrayList<Workout> loadMonthList(YearMonth month) throws IOException, FileNonexistent {
        ensureDataDir();

        String filename = String.format("workouts_%s.dat", month);
        Path filePath = workoutDir.resolve(filename);

        if (Files.notExists(filePath)) {
            throw new FileNonexistent("No save file found for " + month);
        }

        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(filePath))) {
            return (ArrayList<Workout>) in.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Workout class not found when reading file. " +
                    "Something might've corrupted it", e);
        }
    }

    // ----------------- Weight -----------------
    /**
     * Saves all weight entries of the given person into a text file.
     *
     * @param person the {@link Person} whose weight history will be saved
     * @throws IOException if an I/O error occurs while creating directories or writing the file
     */
    public void saveWeightList(Person person) throws IOException {
        ensureDataDir();
        Path filePath = dataDir.resolve("weight.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (WeightRecord wr : person.getWeightHistory()) {
                writer.write(wr.getDate() + "," + wr.getWeight());
                writer.newLine();
            }
        }
    }

    /**
     * Loads previously saved weight entries for the given person from a text file.
     *
     * @param person the {@link Person} whose weight history will be loaded
     * @throws IOException if an I/O error occurs while reading the file
     */
    public void loadWeightList(Person person) throws IOException {
        ensureDataDir();
        Path filePath = dataDir.resolve("weight.txt");
        if (Files.notExists(filePath)) {
            return;
        }

        List<WeightRecord> list = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                LocalDate date = LocalDate.parse(parts[0]);
                double weight = Double.parseDouble(parts[1]);
                list.add(new WeightRecord(weight, date));
            }
        }
        person.setWeightHistory(list);
    }

    // ----------------- Goal -----------------
    /**
     * Saves the user's goal weight and the date it was set to a text file named {@code goal.txt}.
     *
     * @param goalWeight the target goal weight to be saved (in kilograms)
     * @param setDate    the {@link LocalDate} when the goal weight was set
     * @throws IOException if an I/O error occurs while writing to the file
     */
    public void saveGoal(double goalWeight, LocalDate setDate) throws IOException {
        ensureDataDir();
        Path filePath = dataDir.resolve("goal.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write(goalWeight + "," + setDate);
        }
    }

    /**
     * Loads the saved goal weight and the date it was set from the text file {@code goal.txt}.
     *
     * @return a {@code Double[]} array containing [goalWeight, epochDay], or {@code null} if no data file exists
     * @throws IOException if an I/O error occurs while reading the file
     */
    public Double[] loadGoal() throws IOException {
        ensureDataDir();
        Path filePath = dataDir.resolve("goal.txt");
        if (Files.notExists(filePath)) {
            return null;
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String[] parts = reader.readLine().split(",");
            double goal = Double.parseDouble(parts[0]);
            long epochDay = LocalDate.parse(parts[1]).toEpochDay();
            return new Double[]{goal, (double) epochDay};
        }
    }

    // ----------------- Username -----------------
    /**
     * Saves the person's name to a text file for future sessions.
     */
    public void saveUserName(Person person) throws IOException {
        ensureDataDir();
        Path filePath = dataDir.resolve("username.txt");
        Files.writeString(filePath, person.getName());
    }

    /**
     * Loads the saved username from text file.
     * Returns null if file doesn't exist.
     */
    public String loadUserName() throws IOException {
        ensureDataDir();
        Path filePath = dataDir.resolve("username.txt");
        if (Files.notExists(filePath)) {
            return null;
        }
        return Files.readString(filePath).trim();
    }
}
