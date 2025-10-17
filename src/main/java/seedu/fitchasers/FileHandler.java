package seedu.fitchasers;

import seedu.fitchasers.Exceptions.FileNonexistent;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

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

    private static final Path DATA_DIR = Paths.get("data", "workouts");
    private final UI ui = new UI();



    /**
     * Ensures that the save file and its parent directory exist.
     *
     * @throws IOException if directory or file creation fails
     */
    private static void ensureDataDir() throws IOException {
        Files.createDirectories(DATA_DIR);
    }

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
        Path filePath = DATA_DIR.resolve(filename);

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
        Path filePath = DATA_DIR.resolve(filename);

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
}
