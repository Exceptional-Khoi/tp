package seedu.fitchasers;

import seedu.fitchasers.exceptions.CorruptedDataException;
import seedu.fitchasers.exceptions.FileNonexistent;
import seedu.fitchasers.ui.UI;
import seedu.fitchasers.workouts.ViewLog;
import seedu.fitchasers.gym.EquipmentDisplay;
import seedu.fitchasers.gym.Gym;
import seedu.fitchasers.gym.StaticGymData;
import seedu.fitchasers.tagger.DefaultTagger;
import seedu.fitchasers.tagger.Modality;
import seedu.fitchasers.tagger.MuscleGroup;
import seedu.fitchasers.user.GoalWeightTracker;
import seedu.fitchasers.user.Person;
import seedu.fitchasers.user.WeightManager;
import seedu.fitchasers.workouts.Workout;
import seedu.fitchasers.workouts.WorkoutManager;
import seedu.fitchasers.storage.FileHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//@@author nitin19011
/**
 * Main entry point for the FitChasers application.
 * <p>
 * Handles user input commands, delegates operations to WorkoutManager,
 * and persists data through FileHandler.
 */
public class FitChasers {
    /**
     * Starts the FitChasers program.
     * Initializes all components, loads saved data if available,
     * and processes user input until the user exits.
     */
    private static Person person;
    private static String savedName = null;
    private static final UI ui = new UI();
    private static final FileHandler fileHandler = new FileHandler();
    private static final YearMonth currentMonth = YearMonth.now();
    private static ViewLog viewLog;
    private static final DefaultTagger tagger = new DefaultTagger();
    private static final List<Gym> gyms = StaticGymData.getNusGyms();
    private static WorkoutManager workoutManager;
    private static boolean isRunning = true;
    private static String argumentStr = "";
    private static WeightManager weightManager;
    private static GoalWeightTracker goalTracker;
    private static boolean isWeightFileCorrupted = false;

    /**
     * The main entry point for the FitChasers application.
     * <p>
     * Initializes core components, displays the greeting interface, and continuously
     * listens for user input commands until termination. Commands are parsed and executed
     * according to their respective handlers (e.g., workouts, weight tracking, goals, gyms).
     * Invalid or malformed commands trigger user-friendly error messages.
     *
     * @param args The command-line arguments (not used in this application).
     * @throws IOException If an I/O error occurs during file operations.
     */
    public static void main(String[] args) throws IOException {
        ui.printLeftHeader();
        initVariables();
        ui.showGreeting();
        try {
            //viewLog.render(argumentStr);
            System.out.println();
        } catch (IndexOutOfBoundsException e) {
            ui.showError(e.getMessage());
        }
        workoutManager.initWorkouts();
        while (isRunning) {
            String input = ui.readCommand();

            if (input == null) {
                break;
            }

            if (input.trim().isEmpty()) {
                ui.showMessage("Please enter a command, or type /help or h for options.");
                continue;
            }

            if (isWeightFileCorrupted) {
                String[] parts = input.trim().split("\\s+", 2);
                String cmd = parts[0].toLowerCase();

                boolean isCommandAllowed = cmd.equals("/clear_weights")
                        || cmd.equals("/exit") || cmd.equals("e");

                if (!isCommandAllowed) {
                    ui.showError("Action disabled due to corrupted weight file.");
                    ui.showMessage("Please use '/clear_weights' or '/exit' and manually resolve the weight.txt file.");
                    continue;
                }
            }

            String[] parts = input.trim().split("\\s+", 2);
            String command = parts[0].toLowerCase();
            argumentStr = (parts.length > 1) ? parts[1].trim() : "";

            try {
                switch (command) {

                case "/help":
                case "h":
                case "help": {
                    if (!argumentStr.isEmpty()) {
                        ui.showError("The /help command doesn't take any arguments.\n"
                                + "Just type '/help' or 'h' to see all available commands.");
                    } else if (command.equals("help")) {
                        ui.showMessage("Did you mean '/help'? Type '/help' or 'h' to see all available commands.");
                    } else {
                        ui.showHelp();
                    }
                    break;
                }

                case "/rename":
                case "rn":
                    renameMethod();
                    break;

                case "/add_weight":
                case "aw":
                    weightManager.addWeight(argumentStr);
                    // Format: /add_weight w/WEIGHT d/DATE
                    break;

                case "/view_weight":
                case "vw":
                    viewWeightMethod(weightManager);
                    break;

                case "/clear_weights":
                    handleClearWeights();
                    break;

                case "/set_goal":
                case "sg":
                    goalTracker.handleSetGoal(argumentStr);
                    break;

                case "/view_goal":
                case "vg":
                    goalTracker.handleViewGoal(person.getLatestWeight());
                    break;

                case "/create_workout":
                case "cw":
                    // Format: /create_workout n/NAME d/DD/MM/YY t/HHmm
                    workoutManager.addWorkout(argumentStr);
                    break;

                case "/add_exercise":
                case "ae":
                    // Format: /add_exercise n/NAME r/REPS
                    workoutManager.addExercise(argumentStr);
                    break;

                case "/add_modality_tag":
                case "amot":
                    amotMethod();
                    break;

                case "/add_muscle_tag":
                case "amt":
                    amtMethod();
                    break;

                case "/gym_where":
                case "gw":
                    gwMethod();
                    break;

                case "/gym_page":
                case "gp":
                    gpMethod();
                    break;

                case "/override_workout_tag":
                case "owt":
                    owtMethod();
                    break;

                case "/add_set":
                case "as":
                    // Format: /add_set r/REPS
                    workoutManager.addSet(argumentStr);
                    break;

                case "/end_workout":
                case "ew":
                    // Format: /end_workout d/DD/MM/YY t/HHmm
                    workoutManager.endWorkout(argumentStr);
                    break;

                case "/view_log":
                case "vl":
                    try {
                        viewLog.render(argumentStr);
                    } catch (IndexOutOfBoundsException e) {
                        ui.showError(e.getMessage());
                    }
                    break;

                case "/open":
                case "o":
                    viewLog.openByIndex(Integer.parseInt(argumentStr));
                    break;

                case "/delete_workout":
                case "dw":
                    workoutManager.deleteParser(argumentStr);
                    break;

                case "/exit":
                case "e":
                    exitMethod();
                    break;

                default:
                    ui.showError("That's not a thing, bestie. Try /help or h for the real moves!");
                    break;
                }
            } catch (Exception e) {
                ui.showError(e.getMessage());
            }
        }
    }

    private static void handleClearWeights() {
        ui.showMessage("WARNING: This will delete all your weight records permanently.");
        ui.showMessage("Are you sure you want to proceed? (y/n)");

        if (ui.confirmationMessage()) {
            try {
                person.setWeightHistory(new ArrayList<>());
                Path weightFile = FileHandler.DATA_DIRECTORY.resolve("weight.txt");
                Files.deleteIfExists(weightFile);
                isWeightFileCorrupted = false;
                ui.showMessage("All weight data has been cleared. You can now use all commands.");
            } catch (IOException e) {
                ui.showError("Failed to clear weight data: " + e.getMessage());
            }
        } else {
            ui.showMessage("Operation cancelled. Your weight data is safe.");
        }
    }

    private static void exitMethod() {
        ui.showMessage("Saving your progress...");
        try {
            fileHandler.saveWeightList(person);
            ui.showExitMessage();
        } catch (IOException e) {
            ui.showError("Failed to save workouts before exit.");
        }
        isRunning = false;
    }

    //@@author bennyy117
    private static void owtMethod() throws FileNonexistent, IOException {
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
                    return;
                }
            } else if (param.startsWith("newTag/")) {
                newTag = param.substring(7);
            }
        }

        if (workoutId != null && newTag != null) {
            // Validate empty tag
            if (newTag.trim().isEmpty()) {
                ui.showMessage("Tag cannot be empty.");
                return;
            }

            // Validate workout ID
            if (workoutId <= 0 || workoutId > workoutManager.getWorkouts().size()) {
                ui.showMessage("Invalid workout ID. Use valid ID between 1 and " +
                        workoutManager.getWorkouts().size());
                return;
            }

            Workout workout = viewLog.getWorkoutByDisplayId(workoutId, currentMonth);
            if (workout == null) {
                ui.showMessage("Invalid workout ID.");
                return;
            }

            Set<String> oldTags = workout.getAllTags();

            ui.showMessage("Current tags: " + String.join(", ", oldTags));
            ui.showMessage("Change to: " + newTag + "?");
            ui.showMessage("Are you sure? (y/n)");

            if (!ui.confirmationMessage()) {
                ui.showMessage("Tag change cancelled.");
                return;
            }

            Set<String> autoTagsThatWillBeOverridden = tagger.suggest(workout);
            if (!autoTagsThatWillBeOverridden.isEmpty()) {
                ui.showMessage("WARNING: This will override auto generated tags: " + String.join(", ",
                        autoTagsThatWillBeOverridden));
                ui.showMessage("Continue with override? (y/n)");

                if (!ui.confirmationMessage()) {
                    ui.showMessage("Override cancelled.");
                    return;
                }
            }

            workoutManager.overrideWorkoutTags(workout, newTag);

            try {
                fileHandler.saveMonthList(currentMonth, workoutManager.getWorkouts());

                ArrayList<Workout> reloadedWorkouts = fileHandler.getWorkoutsForMonth(currentMonth);
                workoutManager.setWorkouts(reloadedWorkouts);

                ui.showMessage("✓ Workout tags updated successfully.");
                ui.showMessage("  New tags: " + newTag);

                Set<String> conflicts = workoutManager.checkForOverriddenTags(reloadedWorkouts.get(workoutId - 1));

                if (!conflicts.isEmpty()) {
                    ui.showMessage("WARNING: These manual tags override auto-tags: " + conflicts);
                }

            } catch (IOException e) {
                ui.showMessage("Error saving workout data: " + e.getMessage());
            } catch (FileNonexistent e) {
                throw new RuntimeException(e);
            }

        } else {
            ui.showMessage("Usage: /override_workout_tag id/WORKOUT_ID newTag/NEW_TAG");
        }
    }

    //@@author Exceptional-Khoi
    private static void gpMethod() {
        try {
            String trimmedArg = argumentStr.trim();
            Gym selectedGym = null;

            if (trimmedArg.startsWith("p/") && trimmedArg.length() > 2) {
                String input = trimmedArg.substring(2).trim();
                if (input.isEmpty()) {  // ADD THIS
                    ui.showMessage("Please provide a gym number or name.");
                    listAvailableGyms();
                    return;
                }

                try {
                    int pageNum = Integer.parseInt(input);
                    if (pageNum >= 1 && pageNum <= gyms.size()) {
                        selectedGym = gyms.get(pageNum - 1);
                    }
                } catch (NumberFormatException e) {
                    selectedGym = findGymByName(input);
                }

                if (selectedGym != null) {
                    EquipmentDisplay.showEquipmentForSingleGym(selectedGym);
                } else {
                    ui.showMessage("Invalid gym. Use number (1-" + gyms.size() + ") or gym name (e.g., SRC Gym)");
                    listAvailableGyms();
                }
            } else {
                ui.showMessage("Usage: /gym_page p/page_number_or_gym_name");
                ui.showMessage("Example: /gym_page p/1 OR /gym_page p/SRC Gym");
                listAvailableGyms();
            }
        } catch (Exception e) {
            ui.showMessage("Error: " + e.getMessage());
        }
    }

    private static Gym findGymByName(String gymName) {
        String searchName = gymName.toLowerCase().trim();
        for (Gym gym : gyms) {
            if (gym.getName().toLowerCase().contains(searchName)) {
                return gym;
            }
        }
        return null;
    }

    private static void listAvailableGyms() {
        ui.showMessage("Available gyms:");
        for (int i = 0; i < gyms.size(); i++) {
            ui.showMessage("  " + (i + 1) + ". " + gyms.get(i).getName());
        }
    }

    private static void gwMethod() {
        String trimmedArg = argumentStr.trim();
        try {
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
    }

    private static void amtMethod() {
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
        if (mus != null && mus.trim().isEmpty()) {
            ui.showMessage("Muscle cannot be empty. Use: LEGS, POSTERIOR_CHAIN, CHEST, BACK, SHOULDERS, ARMS, CORE");
            return;
        }

        if (keyword != null && keyword.trim().isEmpty()) {
            ui.showMessage("Keyword cannot be empty. Example: /add_muscle_tag m/CARDIO k/running");
            return;
        }
        if (mus != null && keyword != null) {
            try {
                MuscleGroup muscleGroup = MuscleGroup.valueOf(mus);
                tagger.addMuscleKeyword(muscleGroup, keyword);

                for (Workout w : workoutManager.getWorkouts()) {
                    Set<String> updatedTags = tagger.suggest(w);
                    w.setAutoTags(updatedTags);
                    ui.showMessage("Retagged workout " + w.getWorkoutName() + ": " + updatedTags);
                }
                try {
                    fileHandler.saveMonthList(currentMonth, workoutManager.getWorkouts());
                    ui.showMessage("Added keyword " + keyword + " to muscle group " + mus);
                } catch (IOException e) {
                    ui.showMessage("Error saving changes: " + e.getMessage());
                }
            } catch (IllegalArgumentException e) {
                ui.showMessage("Invalid muscle group. Valid options: LEGS, POSTERIOR_CHAIN, CHEST, BACK, " +
                        "SHOULDERS, ARMS, CORE");
            }
        } else {
            ui.showMessage("Usage: /add_muscle_tag m/LEGS/ CHEST/... k/keyword");
        }
    }

    //@@author Kart04
    private static void amotMethod() {
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

        if (mod != null && mod.trim().isEmpty()) {
            ui.showMessage("Modality cannot be empty. Use: CARDIO or STRENGTH");
            return;
        }

        if (keyword != null && keyword.trim().isEmpty()) {
            ui.showMessage("Keyword cannot be empty. Example: /add_modality_tag m/CARDIO k/running");
            return;
        }

        if (mod != null && keyword != null) {
            try {
                Modality modality = Modality.valueOf(mod);
                tagger.addModalityKeyword(modality, keyword);

                // Check ONLY workouts that contain this keyword
                StringBuilder conflicts = new StringBuilder();
                List<Workout> affectedWorkouts = new ArrayList<>();

                for (Workout w : workoutManager.getWorkouts()) {
                    String workoutText = w.getWorkoutName().toLowerCase();
                    if (workoutText.contains(keyword.toLowerCase())) {
                        affectedWorkouts.add(w);
                        if (workoutManager.hasConflictingModality(w, mod)) {
                            String existing = workoutManager.getConflictingModality(w);
                            conflicts.append("\n - ").append(w.getWorkoutName())
                                    .append(" is already tagged to ").append(existing);
                        }
                    }
                }

                if (!conflicts.isEmpty()) {
                    ui.showMessage("CANNOT ADD KEYWORD: Conflicting modality tags detected:");
                    ui.showMessage(conflicts.toString());
                    ui.showMessage("\nTo change these tags, first remove the old keyword or manually edit the tag.");
                    return;
                }

                for (Workout w : affectedWorkouts) {
                    Set<String> updatedTags = tagger.suggest(w);
                    w.setAutoTags(updatedTags);
                    ui.showMessage("Retagged: " + w.getWorkoutName() + " → " + updatedTags);
                }

                try {
                    fileHandler.saveMonthList(currentMonth, workoutManager.getWorkouts());
                    ui.showMessage("✓ Added keyword '" + keyword + "' to modality " + mod);
                } catch (IOException e) {
                    ui.showMessage("Error saving changes: " + e.getMessage());
                }
            } catch (IllegalArgumentException e) {
                ui.showMessage("Invalid modality. Valid options: CARDIO, STRENGTH");
            }

        } else {
            ui.showMessage("Usage: /add_modality_tag m/(CARDIO/STRENGTH) k/keyword");
        }
    }

    private static void viewWeightMethod(WeightManager weightManager) {
        if (person.getWeightHistorySize() == 0) {
            ui.showMessage(person.getName() + " has no weight records yet.");
            return;
        }
        weightManager.viewWeights();
        person.displayWeightGraphWithDates();
    }

    private static void renameMethod() {
        if (argumentStr == null || !argumentStr.startsWith("n/")) {
            ui.showMessage("Usage: /my_name n/YourName");
            return;
        }
        String newName = argumentStr.substring(2).trim();
        if (newName.isEmpty()) {
            ui.showMessage("Usage: /my_name n/YourName");
            ui.showMessage("You didn’t enter any name after 'n/'. Example: /my_name n/Nary");
            return;
        }

        if (newName.length() > 30) {
            ui.showMessage("Name is too long. Maximum is 30 characters.");
            return;
        }

        if (!newName.matches("^[a-zA-Z0-9 _-]+$")) {
            ui.showMessage("Name can only contain letters, numbers, spaces, " +
                    "underscores (_), or dashes (-).");
            return;
        }

        person.setName(newName);
        ui.showMessage("Alright, I'll call you " + newName + " from now on.");

        try {
            fileHandler.saveUserName(person);
            ui.showMessage("Your new name has been saved.");
        } catch (IOException e) {
            ui.showError("Failed to save username: " + e.getMessage());
        }
    }

    private static void initVariables() throws IOException {
        try {
            savedName = fileHandler.loadUserName();
        } catch (IOException e) {
            ui.showError("Error reading saved username: " + e.getMessage());
        }

        if (savedName != null) {
            person = new Person(savedName);
            ui.showMessage("Welcome back, " + savedName + "!");
        } else {
            // Prompt for name if not saved
            ui.showMessage("Before we begin, please enter your name.");
            String userName = ui.enterName();
            person = new Person(userName);
            try {
                fileHandler.saveUserName(person);
                fileHandler.saveCreationMonth(YearMonth.now());
                ui.showMessage("Your name has been saved.");
            } catch (IOException e) {
                ui.showError("Failed to save username: " + e.getMessage());
            }

            // Prompt for initial weight
            WeightManager tempWeightManager = new WeightManager(person);
            double initialWeight = ui.enterWeight(tempWeightManager);
            if (initialWeight > 0) {
                String todayStr = java.time.LocalDate.now()
                        .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yy"));
                String command = "w/" + initialWeight + " d/" + todayStr;

                tempWeightManager.addWeight(command);
                try {
                    fileHandler.saveWeightList(person);
                } catch (IOException e) {
                    ui.showError("Failed to save initial weight: " + e.getMessage());
                }
            }

            ui.showMessage("Nice to meet you, " + person.getName() + "! Let's get started!");
            ui.showQuickStartTutorial();

            try {
                fileHandler.saveUserName(person);
            } catch (IOException e) {
                ui.showError("Failed to save username: " + e.getMessage());
            }
        }

        weightManager = new WeightManager(person);
        workoutManager = new WorkoutManager(tagger, fileHandler);
        fileHandler.initIndex();

        try {
            fileHandler.loadWeightList(person);
            workoutManager.setWorkouts(fileHandler.loadMonthList(currentMonth), currentMonth);
        } catch (CorruptedDataException e) {
            isWeightFileCorrupted = true;
            ui.showError("CRITICAL ERROR: Your weight.txt file is corrupted or has been tampered with.");
            ui.showError(e.getMessage());
            ui.showMessage("Please fix the file manually or type '/clear_weights' to delete all weight data and start fresh.");
        } catch (IOException e) {
            ui.showError(e.getMessage());
        } catch (FileNonexistent e) {
            fileHandler.saveMonthList(currentMonth, new ArrayList<>());
        }

        viewLog = new ViewLog(ui, workoutManager, fileHandler);
        goalTracker = new GoalWeightTracker();
    }
}
