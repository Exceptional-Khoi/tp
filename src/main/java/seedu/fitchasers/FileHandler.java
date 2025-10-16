package seedu.fitchasers;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Handles the permanent storage of workout and exercise data.
 * Saves to and loads from: data/save.txt
 *
 * If the "data" folder does not exist, it will be created automatically.
 *
 * File format:
 * Each workout starts with "WORKOUT" and ends with "END_WORKOUT".
 * Exercises are listed between, with all set repetitions joined by commas.
 *
 * Example:
 * WORKOUT | Chest Day | 45
 * EXERCISE | Push Ups | 15,15,12
 * EXERCISE | Bench Press | 12,10,8
 * END_WORKOUT
 */

public class FileHandler {

    private static final Path FILE_PATH = Paths.get("data", "save.txt");
    private final UI ui = new UI();


    /**
     * Constructs a FileHandler with a reference to the UI for user feedback.
     */
    public FileHandler() {
    }


    /**
     * Ensures that the save file and its parent directory exist.
     *
     * @throws IOException if directory or file creation fails
     */
    private static void ensureFile() throws IOException {
        Files.createDirectories(FILE_PATH.getParent());
        if (Files.notExists(FILE_PATH)) {
            Files.createFile(FILE_PATH); // Create empty save file
        }
    }

    /**
     * Loads all workout and exercise data from save.txt into the given WorkoutManager.
     *
     * Expected format:
     * WORKOUT | Name | Duration
     * EXERCISE | Name | reps,reps,reps
     * END_WORKOUT
     *
     * @param workoutManager the WorkoutManager to populate
     * @throws IOException if reading the save file fails
     */
    public void loadFileContentArray(WorkoutManager workoutManager, Person person) throws IOException {
        ensureFile();
        List<String> lines = Files.readAllLines(FILE_PATH);

        Workout currentWorkout = null;

        for (String line : lines) {
            if (line.startsWith("USER")) {
                try {
                    String[] parts = line.split("\\|");
                    String userName = parts[1].trim();
                    person.setName(userName);
                    ui.showMessage("Welcome back, " + userName + "!");
                } catch (Exception ignored) {}
            } else if (line.startsWith("WORKOUT")) {
                // existing logic
            } else if (line.startsWith("EXERCISE") && currentWorkout != null) {
                // existing logic
            } else if (line.startsWith("END_WORKOUT")) {
                currentWorkout = null;
            }
        }
    }

    /**
     * Saves all workout data to save.txt in the specified format.
     *
     * Each save overwrites the entire file.
     *
     * @param workouts list of workouts to be saved
     * @throws IOException if writing fails
     */
    public void saveFile(Person person, List<Workout> workouts) throws IOException {
        ensureFile();
        try (FileWriter fw = new FileWriter(FILE_PATH.toFile())) {
            fw.write("USER | " + person.getName() + "\n"); // header line
            for (Workout w : workouts) {
                fw.write("WORKOUT | " + w.getWorkoutName() + " | " + w.getDuration() + "\n");
                for (Exercise ex : w.getExercises()) {
                    StringBuilder setsStr = new StringBuilder();
                    for (int i = 0; i < ex.getSets().size(); i++) {
                        setsStr.append(ex.getSets().get(i));
                        if (i < ex.getSets().size() - 1) {
                            setsStr.append(",");
                        }
                    }
                    fw.write("EXERCISE | " + ex.getName() + " | " + setsStr + "\n");
                }
                fw.write("END_WORKOUT\n");
            }
        }
        ui.showMessage("Successfully saved " + workouts.size() + " workout(s) for " + person.getName());
    }
}
