package seedu.fitchasers;

import seedu.fitchasers.ui.UI;
import seedu.fitchasers.exceptions.FileNonexistent;
import seedu.fitchasers.user.Person;
import seedu.fitchasers.user.WeightRecord;
import seedu.fitchasers.workouts.Workout;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    private Path dataDir = Paths.get("data");
    private final Path workoutDir = dataDir.resolve("workouts");
    private final UI ui = new UI();
    private final Map<YearMonth, ArrayList<Workout>> arrayByMonth = new HashMap<>();
    private final Set<YearMonth> onDiskMonths = new HashSet<>(); // discovered from directory

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
                    .map(n -> n.substring(9, 16))           // "YYYY-MM"
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
        try{
            ensureDataDir();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String filename = String.format("workouts_%s.dat", month); // e.g., workouts_2025-06.dat
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
     * Saves all weight entries of the given person into a serialized file.
     * <p>
     * The file is stored inside the {@link #dataDir} directory, with the filename format:
     * "weight_&lt;PersonName&gt;.dat". If the data directory does not exist, it will be created automatically.
     * </p>
     *
     * @param person the {@link Person} whose weight history will be saved
     * @throws IOException if an I/O error occurs while creating directories or writing the file
     */
    public void saveWeightList(Person person) throws IOException {
        ensureDataDir();
        Path filePath = dataDir.resolve("weight.dat");
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(filePath))) {
            out.writeObject(person.getWeightHistory());
            //ui.showMessage("Saved " + person.getWeightHistory().size() + " weight entries for " + person.getName());
        }
    }

    /**
     * Loads previously saved weight entries for the given person from a serialized file.
     * <p>
     * The file is expected to be located inside the {@link #dataDir} directory, with the filename format:
     * "weight_&lt;PersonName&gt;.dat". If no file is found, the method will simply show a message and return.
     * The loaded entries are set into the {@link Person}'s weight history.
     * </p>
     *
     * @param person the {@link Person} whose weight history will be loaded
     * @throws IOException if an I/O error occurs while reading the file or if the {@link WeightRecord} class
     *                     cannot be found during deserialization
     */
    @SuppressWarnings("unchecked")
    public void loadWeightList(Person person) throws IOException {
        ensureDataDir();
        Path filePath = dataDir.resolve("weight.dat");
        if (Files.notExists(filePath)) {
            //ui.showMessage("No previous weight data found for " + person.getName());
            return;
        }
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(filePath))) {
            List<WeightRecord> list = (List<WeightRecord>) in.readObject();
            person.setWeightHistory(new ArrayList<>(list));
            //ui.showMessage("Loaded " + list.size() + " weight entries for " + person.getName() + ".");
        } catch (ClassNotFoundException e) {
            throw new IOException("WeightRecord class not found", e);
        }
    }
    // ----------------- Username -----------------
    /**
     * Saves the person's name to a file for future sessions.
     */
    public void saveUserName(Person person) throws IOException {
        ensureDataDir();
        Path filePath = dataDir.resolve("username.dat");
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(filePath))) {
            out.writeObject(person.getName());
        }
    }

    /**
     * Loads the saved username from file.
     * Returns null if file doesn't exist.
     */
    public String loadUserName() throws IOException {
        ensureDataDir();
        Path filePath = dataDir.resolve("username.dat");
        if (Files.notExists(filePath)) {
            return null;
        }
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(filePath))) {
            return (String) in.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Failed to read username from file", e);
        }
    }
}
