    package seedu.fitchasers;

    import java.io.File;
    import java.io.FileWriter;
    import java.io.IOException;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.nio.file.Paths;
    import java.util.List;
    import java.util.Scanner;

    /**
     * Handles the permanent storage of the data
     * Can store and retrieve data stored under data folder -> save.txt
     * if folder doesnt exist it will create it in order for safe relative path
     * <p>
     * save.txt is rewritten every time it saves and not appended, save format follows how
     * the file should be typed into CMT
     * <p>
     * for example if a todo task was present it will save it as
     *  todo [Description]
     *  marked/unmarked [task position]
     */

    public class FileHandler {

        private static final Path FILE_PATH = Paths.get("data", "save.txt");
        private final UI ui;

        /**
         * Ensures a directory relative to the save file is created
         * Creates the save file too
         *
         * @throws IOException when handle method fails to create the file
         */

        FileHandler(UI ui) {
            this.ui = ui;
        }

        private static void ensureFile() throws IOException {
            Files.createDirectories(FILE_PATH.getParent());
            if (Files.notExists(FILE_PATH)) {
                Files.createFile(FILE_PATH); // empty file
            }
        }

        /**
         * Loads workouts and exercises from the save file into the given WorkoutManager.
         * Each workout starts with "WORKOUT" and ends with "END_WORKOUT".
         * Exercises include all sets joined by commas.
         *
         * @param workoutManager WorkoutManager to populate.
         * @throws IOException if reading the file fails.
         */
        public void loadFileContentArray(WorkoutManager workoutManager) throws IOException {
            ensureFile();
            List<String> lines = Files.readAllLines(FILE_PATH);

            Workout currentWorkout = null;

            for (String line : lines) {
                if (line.startsWith("WORKOUT")) {
                    String[] parts = line.split("\\|");
                    String name = parts[1].trim();
                    int duration = Integer.parseInt(parts[2].trim());
                    currentWorkout = new Workout(name, duration);
                    workoutManager.getWorkouts().add(currentWorkout);
                } else if (line.startsWith("EXERCISE") && currentWorkout != null) {
                    String[] parts = line.split("\\|");
                    String exName = parts[1].trim();
                    String[] repsList = parts[2].trim().split(",");
                    Exercise exercise = new Exercise(exName, Integer.parseInt(repsList[0]));
                    // add additional sets if any
                    for (int i = 1; i < repsList.length; i++) {
                        exercise.addSet(Integer.parseInt(repsList[i]));
                    }
                    currentWorkout.addExercise(exercise);
                } else if (line.startsWith("END_WORKOUT")) {
                    currentWorkout = null;
                }
            }
        }


        /**
         * Saves all workout data to the save file in the specified format.
         * Each workout entry is written with its name and duration, followed by
         * all exercises and their corresponding sets (joined by commas).
         * The file is rewritten entirely each time this method is called.
         *
         * @param workouts List of Workout objects to be saved.
         * @throws IOException if an I/O error occurs while creating or writing to the file.
         */
        public void saveFile(List<Workout> workouts) throws IOException {
            ensureFile();
            try (FileWriter fw = new FileWriter(FILE_PATH.toFile())) {
                for (Workout w : workouts) {
                    fw.write("WORKOUT | " + w.getWorkoutName() + " | " + w.getDuration() + "\n");
                    for (Exercise ex : w.getExercises()) {
                        // join all reps from each set with commas
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
        }
    }
