package seedu.fitchasers;

import java.io.IOException;
import java.util.Scanner;

public class FitChasers {
    /**
     * Main entry-point for the FitChasers application.
     */
    public static void main(String[] args) throws IOException {
        WorkoutManager workoutManager = new WorkoutManager();
        FileHandler fileHandler = new FileHandler();
        Scanner sc = new Scanner(System.in);
        String command;
        String argumentStr = "";

        //Load Persistant data if any
        fileHandler.loadFileContentArray(workoutManager);

        System.out.println("Hello welcome to fit chaser"); //Welcome Message @Starvou

        while (sc.hasNextLine()) {
            final String line = sc.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }

            final String[] parts = line.split("\\s+", 2);
            command = parts[0];
            if (parts.length > 1) {
                argumentStr = parts[1];
            }
            command = command.toLowerCase();

            switch (command) {
            case "/help":
                System.out.println("Helppp"); //Starvou
                break;

            case "/create_workout":
                // Format: /create_workout n/NAME d/DD/MM/YY t/HHmm
                // e.g., /create_workout n/Morning_Workout d/25/10/25 t/1400
                workoutManager.addWorkout(argumentStr);
                break;

            case "/view_log":
                // Format: /view_log
                workoutManager.viewWorkouts();
                break;

            case "/add_exercise":
                // Format: /add_exercise n/NAME r/REPS   e.g., /add_exercise n/Push_Up r/10
                workoutManager.addExercise(argumentStr);
                break;
                /*
            case "/add_weight":
                // Format: /add_weight w/WEIGHT d/DATE   e.g., /add_weight w/81.5 d/19/10/25
                // Delegate full args string; WorkoutManager should parse flags.
                // Nary
                workoutManager.addWeight(argumentStr);
                break;

            case "/add_sets":
                // Format: /add_sets e/EXERCISE_NAME s/SETS  e.g., /add_sets e/Push_Up s/2
                workoutManager.addSets(argumentStr);
                break;

            case "/add_reps":
                // Format: /add_reps e/EXERCISE_NAME i/SET_INDEX r/REPS  e.g., /add_reps e/Push_Up i/2 r/12
                workoutManager.addReps(argumentStr);
                break;

            case "/del_sets":
                // Format: /del_sets e/EXERCISE_NAME i/SET_INDEX  e.g., /del_sets e/Push_Up i/2
                workoutManager.deleteSets(argumentStr);
                break;

            case "/del_exercise":
                // Format: /del_exercise EXERCISE_NAME  e.g., /del_exercise Push_Up
                workoutManager.deleteExercise(argumentStr);
                break;

            case "/end_workout":
                // Format: /end_workout d/DD/MM/YY t/HHmm  e.g., /end_workout d/25/10/25 t/1800
                workoutManager.endWorkout(argumentStr);
                break;

            case "/view_duration":
                // Format: /view_duration WORKOUT_NAME
                workoutManager.viewDuration(argumentStr);
                break;

            case "/del_workout":
                // Format: /del_workout WORKOUT_NAME
                workoutManager.deleteWorkout(argumentStr);
                break;



            */

            case "/end_workout":
                // Format: /end_workout d/DD/MM/YY t/HHmm, e.g. /end_workout d/25/10/25 t/1800
                workoutManager.endWorkout(argumentStr);
                break;

            case "/exit":
                fileHandler.saveFile(workoutManager.getWorkouts());
                return;
            default:
                System.out.println("Unknown command. Type /help for the list of commands.");
            }
        }

    }
}
