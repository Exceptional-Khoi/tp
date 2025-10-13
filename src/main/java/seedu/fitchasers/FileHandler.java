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
    private final UI ui;


    /**
     * Constructs a FileHandler with a reference to the UI for user feedback.
     */
    public FileHandler(UI ui) {
        this.ui = ui;
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
    public void loadFileContentArray(WorkoutManager workoutManager) throws IOException {
        ensureFile();
        List<String> lines = Files.readAllLines(FILE_PATH);
        Workout currentWorkout = null;


        for (String line : lines) {
            if (line.startsWith("WORKOUT")) {
                try {
                    String[] parts = line.split("\\|");
                    String name = parts[1].trim();
                    int duration = Integer.parseInt(parts[2].trim());
                    currentWorkout = new Workout(name, duration);
                    workoutManager.getWorkouts().add(currentWorkout);
                } catch (Exception e) {
                    ui.showMessage("Skipping malformed workout entry: " + line);
                }


            } else if (line.startsWith("EXERCISE") && currentWorkout != null) {
                try {
                    String[] parts = line.split("\\|");
                    String exName = parts[1].trim();
                    String[] repsList = parts[2].trim().split(",");


                    Exercise exercise = new Exercise(exName, Integer.parseInt(repsList[0]));
                    for (int i = 1; i < repsList.length; i++) {
                        exercise.addSet(Integer.parseInt(repsList[i]));
                    }
                    currentWorkout.addExercise(exercise);
                } catch (Exception e) {
                    ui.showMessage("Skipping malformed exercise entry: " + line);
                }


            } else if (line.startsWith("END_WORKOUT")) {
                currentWorkout = null;
            }
        }
        ui.showMessage("Loaded " + workoutManager.getWorkouts().size() + " workout(s) from file.");
    }


    /**
     * Saves all workout data to save.txt in the specified format.
     *
     * Each save overwrites the entire file.
     *
     * @param workouts list of workouts to be saved
     * @throws IOException if writing fails
     */
    public void saveFile(List<Workout> workouts) throws IOException {
        ensureFile();
        try (FileWriter fw = new FileWriter(FILE_PATH.toFile())) {
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
        ui.showMessage("Successfully saved " + workouts.size() + " workout(s) to file.");
    }
}
