package seedu.fitchasers;

import seedu.fitchasers.exceptions.FileNonexistent;
import seedu.fitchasers.exceptions.InvalidCommandException;
import seedu.fitchasers.tagger.Modality;
import seedu.fitchasers.tagger.MuscleGroup;

import java.io.IOException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * Main entry point for the FitChasers application.
 *
 * Handles user input commands, delegates operations to WorkoutManager,
 * and persists data through FileHandler.
 */
public class FitChasers {
    /**
     * Starts the FitChasers program.
     * Initializes all components, loads saved data if available,
     * and processes user input until the user exits.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) throws IOException {
        UI ui = new UI();
        FileHandler fileHandler = new FileHandler();

        ui.showGreeting();

        String savedName = null;
        try {
            savedName = fileHandler.loadUserName();
        } catch (IOException e) {
            ui.showError("Error reading saved username: " + e.getMessage());
        }

        Person person;
        if (savedName != null) {
            person = new Person(savedName);
            ui.showMessage("Welcome back, " + savedName + "!");
        } else {
            // Prompt for name if not saved
            ui.showMessage("Before we begin, please enter your name.");
            String userName = ui.enterName();
            while (userName == null || userName.trim().isEmpty()) {
                ui.showMessage("Name cannot be empty. Please enter your name:");
                userName = ui.enterName();
            }
            person = new Person(userName.trim());
            ui.showMessage("Nice to meet you, " + person.getName() + "! Let's get started.\n");

            try {
                fileHandler.saveUserName(person);
            } catch (IOException e) {
                ui.showError("Failed to save username: " + e.getMessage());
            }
        }

        WeightManager weightManager = new WeightManager(person);
        YearMonth currentMonth = YearMonth.now();
        ViewLog viewLog;
        List<Gym> gyms = StaticGymData.getNusGyms();
        DefaultTagger tagger = new DefaultTagger();
        WorkoutManager workoutManager = new WorkoutManager(tagger);

        try {
            fileHandler.loadWeightList(person);
            workoutManager.setWorkouts(fileHandler.loadMonthList(currentMonth));
            ui.showMessage("Loaded " + currentMonth + " workouts.");
        } catch (FileNonexistent e) {
            ui.showError("Seems like this is a new month!"
                    + "\nWould you like to create new workouts for this month? (Y/N)");
            if (ui.confirmationMessage()) {
                fileHandler.saveMonthList(currentMonth, new ArrayList<>());
                workoutManager.setWorkouts(new ArrayList<>());
            }
        } catch (IOException e) {
            ui.showError(e.getMessage());
        }

        viewLog = new ViewLog(ui, workoutManager);

        boolean isRunning = true;

        while (isRunning) {
            String input = ui.readCommand();
            if (input == null) {
                break;
            }
            if (input.trim().isEmpty()) {
                continue;
            }

            String[] parts = input.trim().split("\\s+", 2);
            String command = parts[0].toLowerCase();
            String argumentStr = (parts.length > 1) ? parts[1].trim() : "";

            try {
                switch (command) {

                case "/help":
                case "h":
                    ui.showHelp();
                    break;

                case "/my_name":
                case "n": {
                    if (argumentStr == null || !argumentStr.startsWith("n/")) {
                        ui.showMessage("Usage: /my_name n/YourName");
                        ui.showDivider();
                        break;
                    }
                    String newName = argumentStr.substring(2).trim();
                    if (newName.isEmpty()) {
                        ui.showMessage("Usage: /my_name n/YourName");
                        ui.showDivider();
                        break;
                    }

                    person.setName(newName);
                    ui.showMessage("Alright, I'll call you " + newName + " from now on.");

                    try {
                        fileHandler.saveUserName(person);
                        ui.showMessage("Your new name has been saved.");
                    } catch (IOException e) {
                        ui.showError("Failed to save username: " + e.getMessage());
                    }

                    ui.showDivider();
                    break;
                }

                case "/add_weight":
                case "aw":
                    weightManager.addWeight(argumentStr);
                    // Format: /add_weight w/WEIGHT d/DATE
                    ui.showDivider();
                    break;

                case "/view_weight":
                case "vw":
                    weightManager.viewWeights();
                    person.displayWeightGraphWithDates();
                    ui.showDivider();
                    break;

                case "/create_workout":
                case "cw":
                    // Format: /create_workout n/NAME d/DD/MM/YY t/HHmm
                    workoutManager.addWorkout(argumentStr);
                    ui.showDivider();
                    break;

                case "/add_exercise":
                case "ae":
                    // Format: /add_exercise n/NAME r/REPS
                    workoutManager.addExercise(argumentStr);
                    ui.showDivider();
                    break;

                case "/add_modality_tag":
                case "amot": {

                    String[] params = argumentStr.split("\\s+");
                    String mod = null;
                    String keyword = null;
                    for (String param : params) {
                        if (param.startsWith("m/")) {
                            mod = param.substring(2).toUpperCase();
                        }
                        if (param.startsWith("k/")) {
                            keyword = param.substring(2).toLowerCase();
                        }
                    }
                    if (mod != null && keyword != null) {
                        tagger.addModalityKeyword(Modality.valueOf(mod), keyword);
                        for (Workout w : workoutManager.getWorkouts()) {
                            Set<String> updatedTags = tagger.suggest(w);
                            w.setAutoTags(updatedTags);
                            System.out.println("Retagged workout " + w.getWorkoutName() + ": " + updatedTags);
                        }
                        ui.showMessage("Added keyword " + keyword + " to modality " + mod);
                    } else {
                        ui.showMessage("Usage: /add_modality_tag m/(CARDIO/ STRENGTH) k/keyword");
                    }
                    break;
                }

                case "/add_muscle_tag":
                case "amt": {
                    // Example: /add_muscle_tag m=legs k=lunges
                    String[] params = argumentStr.split("\\s+");
                    String mus = null;
                    String keyword = null;
                    for (String param : params) {
                        if (param.startsWith("m/")) {
                            mus = param.substring(2).toUpperCase();
                        }
                        if (param.startsWith("k/")) {
                            keyword = param.substring(2).toLowerCase();
                        }
                    }
                    if (mus != null && keyword != null) {
                        tagger.addMuscleKeyword(MuscleGroup.valueOf(mus), keyword);
                        for (Workout w : workoutManager.getWorkouts()) {
                            Set<String> updatedTags = tagger.suggest(w);
                            w.setAutoTags(updatedTags);
                            System.out.println("Retagged workout " + w.getWorkoutName() + ": " + updatedTags);
                        }
                        ui.showMessage("Added keyword " + keyword + " to muscle group " + mus);
                    } else {
                        ui.showMessage("Usage: /add_muscle_tag m/LEGS/ CHEST/... k/keyword");
                    }
                    break;
                }

                case "/gym_where":
                case "gw":{
                    String trimmedArg = argumentStr.trim();
                    try {
                        // Only proceed if argument starts with "n/"
                        if (trimmedArg.startsWith("n/") && trimmedArg.length() > 2) {
                            Set<String> gymsToSuggest = EquipmentDisplay.suggestGymsForExercise(gyms, argumentStr);
                            if (!gymsToSuggest.isEmpty()) {
                                ui.showMessage("You can do this workout at: " + String.join(", ",
                                        gymsToSuggest));
                            } else {
                                ui.showMessage("Sorry, no gyms found for that exercise.");
                            }
                        } else {
                            ui.showMessage("Usage: /gym_where n/exercise_name");
                        }
                    } catch (Exception e) {
                        ui.showMessage("An error occurred while searching for gyms. Please check your input " +
                                "and try again.");
                    }
                    ui.showDivider();
                    break;
                }

                case "/gym_page":
                case "gp": {
                    try {
                        String trimmedArg = argumentStr.trim();
                        if (trimmedArg.startsWith("p/") && trimmedArg.length() > 2) {
                            String pageNumStr = trimmedArg.substring(2).trim();
                            int pageNum = Integer.parseInt(pageNumStr);
                            if (pageNum >= 1 && pageNum <= gyms.size()) {
                                Gym gym = gyms.get(pageNum - 1);
                                EquipmentDisplay.showEquipmentForSingleGym(gym);
                            } else {
                                ui.showMessage("Invalid page number. Please enter a number between 1 and "
                                        + gyms.size());
                            }
                        } else {
                            ui.showMessage("Usage: /gym_page p/page_number (e.g. /gym_page p/1)");
                        }
                    } catch (NumberFormatException e) {
                        ui.showMessage("Usage: /gym_page page_number (must be an integer)");
                    }
                    break;
                }

                case "/override_workout_tag":
                case "owt": {
                    // Parse parameters
                    String[] params = argumentStr.split("\\s+");
                    Integer workoutId = null;
                    String newTag = null;

                    for (String param : params) {
                        if (param.startsWith("id/")) {
                            try {
                                workoutId = Integer.parseInt(param.substring(3));
                            } catch (NumberFormatException e) {
                                ui.showMessage("Invalid workout ID.");
                                break;
                            }
                        } else if (param.startsWith("newTag/")) {
                            newTag = param.substring(7);
                        }
                    }

                    if (workoutId != null && newTag != null) {
                        // Update in memory
                        workoutManager.overrideWorkoutTags(workoutId, newTag);
                        // Save changes persistently immediately after update
                        try {
                            fileHandler.saveMonthList(currentMonth, workoutManager.getWorkouts());
                            ui.showMessage("Workout tags saved successfully.");
                        } catch (IOException e) {
                            ui.showMessage("Error saving workout data: " + e.getMessage());
                        }
                    } else {
                        ui.showMessage("Usage: /override_workout_tag id/WORKOUT_ID newTag/NEW_TAG");
                    }

                    ui.showDivider();
                    break;
                }

                case "/add_set":
                case "as":
                    // Format: /add_set r/REPS
                    workoutManager.addSet(argumentStr);
                    ui.showDivider();
                    break;

                case "/end_workout":
                case "ew":
                    // Format: /end_workout d/DD/MM/YY t/HHmm
                    workoutManager.endWorkout(ui, argumentStr);
                    ui.showDivider();
                    break;

                case "/view_log":
                case "vl":
                    try{
                        viewLog.render(argumentStr); //#TODO select detailed or not
                    }catch (IndexOutOfBoundsException e){
                        ui.showError(e.getMessage());
                    }
                    ui.showDivider();
                    break;

                case "/open":
                case "o":
                    viewLog.openByIndex(Integer.parseInt(argumentStr));
                    break;

                case "/del_workout":
                case "d":
                    // Format: /del_workout WORKOUT_NAME
                    if(argumentStr.isEmpty()){
                        throw new InvalidCommandException("Workout deletion command requires a workout name or date. " +
                                "Please enter a valid command.");
                    } else if (argumentStr.contains("d/")) {
                        workoutManager.interactiveDeleteWorkout(argumentStr, ui);
                    } else{
                        workoutManager.deleteWorkout(argumentStr);
                    }
                    break;

                case "/exit":
                case "e":
                    ui.showMessage("Saving your progress...");
                    try {
                        fileHandler.saveWeightList(person);
                        fileHandler.saveMonthList(currentMonth, workoutManager.getWorkouts());
                        ui.showExitMessage();
                    } catch (IOException e) {
                        ui.showError("Failed to save workouts before exit.");
                    }
                    isRunning = false;
                    break;

                default:
                    ui.showError("That's not a thing, bestie. Try /help or h for the real moves!");
                    ui.showDivider();
                    break;
                }
            } catch (Exception e) {
                ui.showError("Something went wrong: " + e.getMessage());
                ui.showDivider();
            }
        }
    }
}
