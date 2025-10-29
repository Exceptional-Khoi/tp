package seedu.fitchasers;

import seedu.fitchasers.ui.UI;
import seedu.fitchasers.workouts.WorkoutManager;
import seedu.fitchasers.user.Person;
import seedu.fitchasers.user.WeightRecord;
import seedu.fitchasers.workouts.Workout;
import seedu.fitchasers.workouts.Exercise;

import java.io.FileWriter;
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

    private final Path FILE_PATH = Paths.get("data");
    private final Path workoutDir = FILE_PATH.resolve("workouts");
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
        try (var stream = Files.list(FILE_PATH)) {
            stream.map(p -> p.getFileName().toString())
                    .filter(n -> n.startsWith("workouts_") && n.endsWith(".dat"))
                    .map(n -> n.substring(9, 16))           // "YYYY-MM"
                    .forEach(s -> onDiskMonths.add(YearMonth.parse(s)));
        }
    }

    public Map<YearMonth, ArrayList<Workout>> getArrayByMonth() {
        return arrayByMonth;
    }

    /**
     * Ensures that the save file and its parent directory exist.
     *
     * @throws IOException if directory or file creation fails
     */
    private void ensureDataDir() throws IOException {
        Files.createDirectories(FILE_PATH);
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
     * Loads all workout and exercise data from save.txt into the given WorkoutManager.
     * <p>
     * Expected format:
     * WORKOUT | Name | Duration
     * EXERCISE | Name | reps,reps,reps
     * END_WORKOUT
     *
     * @param workoutManager the WorkoutManager to populate
     * @throws IOException if reading the save file fails
     */
    public void loadFileContentArray(YearMonth targetMonth,WorkoutManager workoutManager, Person person) throws IOException {
        ensureDataDir();
        List<String> lines = Files.readAllLines(FILE_PATH);

        Workout currentWorkout = null;

        for (String line : lines) {
            if (line.startsWith("USER")) {
                try {
                    String[] parts = line.split("\\|");
                    if (parts.length < 2) {
                        throw new IllegalArgumentException("Malformed USER line: " + line);
                    }
                    String userName = parts[1].trim();
                    person.setName(userName);
                } catch (Exception e) {
                    ui.showError("Failed to read user name from save file. Using default name instead.");
                }

            } else if (line.startsWith("WORKOUT")) {
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
     * <p>
     * Each save overwrites the entire file.
     *
     * @param workouts list of workouts to be saved
     * @throws IOException if writing fails
     */
    public void saveFile(Person person, List<Workout> workouts) throws IOException {
        ensureDataDir();
        try (FileWriter fw = new FileWriter(FILE_PATH.toFile())) {
            fw.write("USER | " + person.getName() + "\n");
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

                Set<String>workoutTags = w.getAllTags();
                StringBuilder setsStr = new StringBuilder();

                for (String workoutTag : workoutTags){
                    setsStr.append(workoutTag);
                    setsStr.append(",");
                }
                fw.write("TAGS | " + setsStr + '\n');
                fw.write("END_WORKOUT\n");
            }
        }
        ui.showMessage("Successfully saved " + workouts.size() + " workout(s) to file.");
    }

    /**
     * Loads the saved goal weight and the date it was set from the data file {@code goal.dat}.
     * <p>
     * If the file does not exist, this method returns {@code null}.
     * Otherwise, it reads two values in the following order:
     * <ul>
     *     <li>The goal weight as a {@code double}</li>
     *     <li>The date the goal was set, represented as a {@code long} epoch day value</li>
     * </ul>
     * The method then returns both values as an array of {@link Double} objects,
     * where index 0 is the goal weight, and index 1 is the epoch day of the goal date.
     * </p>
     *
     * @return a {@code Double[]} array containing [goalWeight, epochDay], or {@code null} if no data file exists
     * @throws IOException if an I/O error occurs while reading from the file
     */
    public Double[] loadGoal() throws IOException {
        ensureDataDir();
        Path filePath = FILE_PATH.resolve("goal.dat");
        if (Files.notExists(filePath)) {
            return null;
        }

        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(filePath))) {
            double goal = in.readDouble();
            long epochDay = in.readLong();
            return new Double[]{goal, (double) epochDay};
        }
    }

    // ----------------- Username -----------------
    /**
     * Saves the person's name to a file for future sessions.
     */
    public void saveUserName(Person person) throws IOException {
        ensureDataDir();
        Path filePath = FILE_PATH.resolve("username.dat");
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
        Path filePath = FILE_PATH.resolve("username.dat");
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
