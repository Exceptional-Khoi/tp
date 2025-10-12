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

    /**
     * Ensures a directory relative to the save file is created
     * Creates the save file too
     *
     * @throws IOException when handle method fails to create the file
     */

    private static void ensureFile() throws IOException {
        Files.createDirectories(FILE_PATH.getParent());
        if (Files.notExists(FILE_PATH)) {
            Files.createFile(FILE_PATH); // empty file
        }
    }

    /**
     * main method that loads taskslists from the save.txt
     *
     * @throws IOException when handle method fails to save the file
     */
    public void loadFileContentArray(WorkoutManager workoutManager) throws IOException {
        try {
            ensureFile();
        } catch (Exception e) {
            System.out.println("Error in FileHandler");
        }
        File f = new File(FILE_PATH.toUri()); // create a File for the given file path
        Scanner s = new Scanner(f); // create a Scanner using the File as the source
        while (s.hasNext()) {
            workoutManager.loadWorkoutFromFile(s.nextLine());
        }
    }


    /**
     * Saves current tasklist into save.txt
     *
     * @param workoutLists takes arraylist and populate with save data
     * @throws IOException when handle method fails to save the file
     */
    public void saveFile(List<Workout> workoutLists) throws IOException {
        try {
            ensureFile();
        } catch (Exception e) {
            System.out.println("Error in FileHandler");
        }
        FileWriter fw = new FileWriter(FILE_PATH.toFile()); // create a File for the given file path
        int i = 0;
        for (Workout workout : workoutLists) {
            fw.write(workout.getWorkoutName() + " | " + workout.getDuration() + "\n");
            i++;
        }
        fw.close();
    }
}
