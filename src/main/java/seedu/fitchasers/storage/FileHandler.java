package seedu.fitchasers.storage;

import seedu.fitchasers.exceptions.CorruptedFileError;
import seedu.fitchasers.exceptions.FileNonexistent;
import seedu.fitchasers.ui.UI;
import seedu.fitchasers.user.Person;
import seedu.fitchasers.user.WeightRecord;
import seedu.fitchasers.workouts.Exercise;
import seedu.fitchasers.workouts.Workout;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

//@@author Kart04
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
 */
public class FileHandler {

    public static final Path DATA_DIRECTORY = Paths.get("data");
    private final Path workoutDir = DATA_DIRECTORY.resolve("workouts");
    private final UI ui = new UI();
    private final Map<YearMonth, ArrayList<Workout>> arrayByMonth = new HashMap<>();
    private final Set<YearMonth> onDiskMonths = new HashSet<>();

    /**
     * Initilize index for lazy loading
     *
     * @throws IOException if directory or file creation fails
     */
    public void initIndex() throws IOException, FileNonexistent {
        ensureDataDir();
        onDiskMonths.clear();
        try (var stream = Files.list(workoutDir)) {
            stream.map(p -> p.getFileName().toString())
                    .filter(n -> n.startsWith("workouts_"))
                    .forEach(name -> {
                        if (name.endsWith(".txt")) {
                            String ym = name.substring("workouts_".length(), "workouts_".length() + 7);
                            try {
                                onDiskMonths.add(YearMonth.parse(ym));
                            } catch (Exception ignore) {
                                System.out.println("Skipping workout " + name
                                        + " because file does not conform to standards ");
                            }
                        }
                    });
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
        Files.createDirectories(DATA_DIRECTORY);
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
        ensureDataDir();

        String filename = String.format("workouts_%s.txt", month); // e.g., workouts_2025-10.txt
        Path filePath = workoutDir.resolve(filename);

        try (BufferedWriter bw = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            for (Workout w : list) {
                writeWorkoutBlock(bw, w);
                bw.newLine();
            }
        }

        ui.showMessage("Saved " + list.size() + " workouts for " + month);
    }

    public boolean checkFileExists(YearMonth month) throws IOException {
        ensureDataDir();
        Path txt = workoutDir.resolve(String.format("workouts_%s.txt", month));
        return Files.exists(txt);
    }
    /**
     * Loads the given month's workouts from the human-readable text file.
     * If .txt is absent but legacy .dat exists, migrate once: load .dat, save as .txt, return data.
     */
    public ArrayList<Workout> loadMonthList(YearMonth month) throws IOException, FileNonexistent {
        Path txt = workoutDir.resolve(String.format("workouts_%s.txt", month));
        if (checkFileExists(month)) {
            return readMonthFromTxt(txt);
        }

        throw new FileNonexistent("No save file found for " + month);
    }

    /**
     * Gets the workouts for a specific month from disk.
     * This bypasses the cache and always reads fresh data from the file.
     *
     * @param month the YearMonth to load workouts for
     * @return a fresh ArrayList of workouts for that month
     * @throws IOException if reading fails
     * @throws FileNonexistent if no file exists for that month
     */
    public ArrayList<Workout> getWorkoutsForMonth(YearMonth month) throws IOException, FileNonexistent {
        // Always read directly from file, don't use cache
        Path txt = workoutDir.resolve(String.format("workouts_%s.txt", month));
        if (!checkFileExists(month)) {
            throw new FileNonexistent("No save file found for " + month);
        }
        return readMonthFromTxt(txt);  // Always fresh from disk
    }

    private void writeWorkoutBlock(BufferedWriter bw, Workout workout) throws IOException {
        final String name = workout.getWorkoutName();
        final int duration = workout.getDuration();
        final LocalDateTime start = workout.getWorkoutStartDateTime();
        final Set<String> autoTags = workout.getAutoTags();
        final Set<String> manualTags = workout.getManualTags();
        String endTime;
        final List<Exercise> exercises  = workout.getExercises();
        if( workout.getWorkoutEndDateTime() == null){
            endTime = "Unended";
        } else {
            endTime = workout.getWorkoutEndDateTime().toString();
        }
        bw.write("WORKOUT");
        bw.newLine();
        bw.write("Name: " + name);
        bw.newLine();
        bw.write("Start: " + start);
        bw.newLine();
        bw.write("End: " + endTime);
        bw.newLine();
        bw.write("DurationMin: " + duration);
        bw.newLine();
        bw.write("AutoTags: ");
        for(String tag : autoTags){
            bw.write(tag);
            bw.write(',');
        }
        bw.newLine();
        bw.write("ManualTags: ");
        for(String tag : manualTags){
            bw.write(tag);
            bw.write(',');
        }
        bw.newLine();
        bw.write("EXERCISES:");
        bw.newLine();
        for (Exercise exercise : exercises) {
            for (int i = 0; i < exercise.getSets().size(); i++) {
                String exerciseName = exercise.getName();
                bw.write("  - " + exerciseName + " | " + exercise.getSets().get(i));
                bw.newLine();
            }
        }
        bw.write("END_WORKOUT");
        bw.newLine();
    }

    private ArrayList<Workout> readMonthFromTxt(Path txt) throws IOException {
        ArrayList<Workout> list = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(txt, StandardCharsets.UTF_8)) {
            String line;
            List<String> block = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue; // ignore empties/comments
                }
                if ("WORKOUT".equals(trimmed)) {
                    block.clear();
                }
                block.add(line);
                if ("END_WORKOUT".equals(trimmed)) {
                    Workout w;
                    try {
                        w = parseWorkoutBlock(block);
                    } catch (CorruptedFileError e) {
                        corruptedFileErrorHandling();
                        w = null;
                    }
                    if (w != null) {
                        list.add(w);
                    }
                    block.clear();
                }
            }
        }
        return list;
    }

    private Workout parseWorkoutBlock(List<String> lines) throws CorruptedFileError {
        String name = null;
        LocalDateTime start = null;
        LocalDateTime end = null;
        Integer duration = null;
        Set<String> autoTags = new HashSet<>();
        Set<String> manualTags = new HashSet<>();

        List<SetLine> setLines = new ArrayList<>();

        boolean inSets = false;

        for (String raw : lines) {
            String line = raw.trim();
            if (line.equals("WORKOUT") || line.equals("END_WORKOUT")) {
                continue;
            }

            if (line.startsWith("Name:")) {
                name = line.substring(5).trim();
                continue;
            }

            if (line.startsWith("Start:")) {
                String v = line.substring(6).trim();
                try {
                    if (!v.isEmpty()){
                        start = LocalDateTime.parse(v);
                    }
                }catch(Throwable e){
                    throw new CorruptedFileError();
                }
                continue;
            }

            if (line.startsWith("End:")) {
                String v = line.substring(4).trim();
                try{
                    if (!v.isEmpty()){
                        end = LocalDateTime.parse(v);
                    }
                }catch(Throwable e){
                    if(v.equalsIgnoreCase("unended")){
                        end = null;
                    } else {
                        throw new CorruptedFileError();
                    }
                }
                continue;
            }

            if (line.startsWith("DurationMin:")) {
                String durationString = line.substring("DurationMin:".length()).trim();
                try {
                    duration = Integer.parseInt(durationString);
                } catch (NumberFormatException ignore) {
                    throw new CorruptedFileError();
                }
                continue;
            }

            if (line.startsWith("AutoTags:")) {
                autoTags = parseTagList(line.substring("AutoTags:".length()).trim());
                continue;
            }

            if (line.startsWith("ManualTags:")) {
                manualTags = parseTagList(line.substring("ManualTags:".length()).trim());
                continue;
            }

            if (line.startsWith("EXERCISES:")) {
                inSets = true;
                continue;
            }

            if (inSets && line.startsWith("-")) {
                // "- Name | 12"
                String body = line.substring(2).trim();
                String[] parts = body.split("\\|", 2);
                String setName = parts[0].trim();
                Integer reps = null;
                if (parts.length == 2) {
                    String repStr = parts[1].trim();
                    if (!repStr.isEmpty()) {
                        try {
                            reps = Integer.parseInt(repStr);
                        } catch (NumberFormatException e) {
                            throw new CorruptedFileError();
                        }
                    }
                }
                setLines.add(new SetLine(setName, reps));
            }
        }

        // Instantiate Workout with parsed values.
        Workout w = new Workout(name, start, end); // e.g., ctor computes duration
        w.setAutoTags(autoTags);
        w.setManualTags(manualTags);
        // Add sets
        for (SetLine s : setLines) {
            final String n = s.name == null ? "" : s.name;
            final Integer r = s.reps == null ? 0 : s.reps;
            w.addExercise(new Exercise(n, r));
        }
        return w;
    }

    private Set<String> parseTagList(String raw) {
        Set<String> out = new LinkedHashSet<>();
        if (raw == null || raw.isEmpty()) {
            return out;
        }
        for (String part : raw.split(",")) {
            String t = part.trim();
            if (!t.isEmpty()) {
                out.add(t);
            }
        }
        return out;
    }

    private record SetLine(String name, Integer reps) {
    }


    /** Ask the user to input a valid End date-time and return it. */
    private void corruptedFileErrorHandling() {
        ui.showMessage("Invalid End date/time found in file. Skipping workout");
    }

    /**
     * Loads previously saved weight entries for the given person from a text file.
     *
     * @param person the {@link Person} whose weight history will be loaded
     * @throws IOException if an I/O error occurs while reading the file
     */
    public void loadWeightList(Person person) throws IOException {
        ensureDataDir();
        Path filePath = DATA_DIRECTORY.resolve("weight.txt");
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
        Path filePath = DATA_DIRECTORY.resolve("goal.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write(goalWeight + "," + setDate);
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
        Path filePath = DATA_DIRECTORY.resolve("weight.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (WeightRecord wr : person.getWeightHistory()) {
                writer.write(wr.getDate() + "," + wr.getWeight());
                writer.newLine();
            }
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
        Path filePath = DATA_DIRECTORY.resolve("goal.txt");
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
        Path filePath = DATA_DIRECTORY.resolve("username.txt");
        Files.writeString(filePath, person.getName());
    }

    public void saveCreationMonth(YearMonth yearMonth) throws IOException {
        ensureDataDir();
        Path filePath = DATA_DIRECTORY.resolve("creationDate.txt");
        Files.writeString(filePath, yearMonth.toString());
    }

    public YearMonth getCreationMonth() throws IOException {
        ensureDataDir();
        Path filePath = DATA_DIRECTORY.resolve("creationDate.txt");
        if (Files.notExists(filePath)) {
            ui.showError("Creation File Not Found!! Using Today's Date as Creation Date. \n" +
                    "This means you may not be able to add workout before today!");
            saveCreationMonth(YearMonth.now());
            return YearMonth.now();
        }
        try{
            return YearMonth.parse(Files.readString(filePath).trim());
        } catch (DateTimeParseException e) {
            ui.showError("Creation Date File Got Corrupted!! Using Today's Date as Creation Date. \n" +
                    "This means you may not be able to add workout before today!");
            return YearMonth.now();
        }
    }
    /**
     * Loads the saved username from text file.
     * Returns null if file doesn't exist.
     */
    public String loadUserName() throws IOException {
        ensureDataDir();
        Path filePath = DATA_DIRECTORY.resolve("username.txt");
        if (Files.notExists(filePath)) {
            return null;
        }
        return Files.readString(filePath).trim();
    }
}
