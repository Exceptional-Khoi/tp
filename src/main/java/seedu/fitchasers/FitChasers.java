package seedu.fitchasers;

import seedu.fitchasers.ui.UI;
import seedu.fitchasers.ui.ViewLog;
import seedu.fitchasers.exceptions.InvalidCommandException;
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

import java.io.IOException;
import java.time.YearMonth;
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
    private static String command;
    private static String argumentStr;
    private static String input;
    private static WeightManager weightManager;
    private static GoalWeightTracker goalTracker;

    public static void main(String[] args) throws IOException {
        initVariables();
        ui.showGreeting();

        while (isRunning) {
            input = ui.readCommand();
            if (input == null) {
                break;
            }
            if (input.trim().isEmpty()) {
                continue;
            }

            String[] parts = input.trim().split("\\s+", 2);
            command = parts[0].toLowerCase();
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

                case "/rename": {
                    renameMethod();
                    break;
                }

                case "/add_weight":
                case "aw":
                    weightManager.addWeight(argumentStr);
                    // Format: /add_weight w/WEIGHT d/DATE
                    break;

                case "/view_weight":
                case "vw":
                    viewWeightMethod(weightManager);
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
                //@@author Kart04
                case "/add_modality_tag":
                case "amot": {
                    amotMethod();
                    break;
                }

                case "/add_muscle_tag":
                case "amt": {
                    amtMethod();
                    break;
                }


                case "/gym_where":
                case "gw": {
                    gwMethod();
                    break;
                }

                case "/gym_page":
                case "gp": {
                    gpMethod();
                    break;
                }

                case "/override_workout_tag":
                case "owt": {
                    owtMethod();
                    break;
                }
                //@@author
                case "/add_set":
                case "as":
                    // Format: /add_set r/REPS
                    workoutManager.addSet(argumentStr);
                    break;

                case "/end_workout":
                case "ew":
                    // Format: /end_workout d/DD/MM/YY t/HHmm
                    workoutManager.endWorkout(ui, argumentStr);
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
                //@@author Kart04
                case "/del_workout":
                case "d":
                    delMethod();
                    break;
                //@@author
                case "/exit":
                case "e":
                    exitMethod();
                    break;

                default:
                    ui.showError("That's not a thing, bestie. Try /help or h for the real moves!");
                    break;
                }
            } catch (Exception e) {
                ui.showError("Something went wrong in main: " + e.getMessage());
            }
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

    private static void delMethod() throws InvalidCommandException, IOException {
        // Format: /del_workout WORKOUT_NAME
        if (argumentStr.isEmpty()) {
            throw new InvalidCommandException("Workout deletion command requires a workout name or date. " +
                    "Please enter a valid command.");
        } else if (argumentStr.contains("d/")) {
            workoutManager.interactiveDeleteWorkout(argumentStr, ui);
        } else {
            workoutManager.deleteWorkout(argumentStr);
        }
    }

    private static void owtMethod() {
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
    }

    private static void gpMethod() {
        try {
            String trimmedArg = argumentStr.trim();
            if (trimmedArg.startsWith("p/") && trimmedArg.length() > 2) {
                String pageNumStr = trimmedArg.substring(2).trim();
                int pageNum = Integer.parseInt(pageNumStr);
                if (pageNum >= 1 && pageNum <= gyms.size()) {
                    Gym gym = gyms.get(pageNum - 1);
                    String table = EquipmentDisplay.showEquipmentForSingleGym(gym);
                    ui.showMessage(table);
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
    }

    private static void gwMethod() {
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
    }

    private static void amtMethod() {
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
                ui.showMessage("Retagged workout " + w.getWorkoutName() + ": " + updatedTags);
            }
            ui.showMessage("Added keyword " + keyword + " to muscle group " + mus);
        } else {
            ui.showMessage("Usage: /add_muscle_tag m/LEGS/ CHEST/... k/keyword");
        }
    }

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
        if (mod != null && keyword != null) {
            tagger.addModalityKeyword(Modality.valueOf(mod), keyword);
            for (Workout w : workoutManager.getWorkouts()) {
                Set<String> updatedTags = tagger.suggest(w);
                w.setAutoTags(updatedTags);
                ui.showMessage("Retagged workout " + w.getWorkoutName() + ": " + updatedTags);
            }
            ui.showMessage("Added keyword " + keyword + " to modality " + mod);
        } else {
            ui.showMessage("Usage: /add_modality_tag m/(CARDIO/ STRENGTH) k/keyword");
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
                ui.showMessage("Your name has been saved.");
            } catch (IOException e) {
                ui.showError("Failed to save username: " + e.getMessage());
            }

            // Prompt for initial weight
            double initialWeight = ui.enterWeight();
            if (initialWeight > 0) {
                WeightManager weightManager = new WeightManager(person);

                String todayStr = java.time.LocalDate.now()
                        .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yy"));
                String command = "w/" + initialWeight + " d/" + todayStr;

                weightManager.addWeight(command);
                try {
                    fileHandler.saveWeightList(person);
                } catch (IOException e) {
                    ui.showError("Failed to save initial weight: " + e.getMessage());
                }
            }

            ui.showMessage("Nice to meet you, " + person.getName() + " Let's get started!");

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
            workoutManager.setWorkouts(fileHandler.getWorkoutsForMonth(currentMonth), currentMonth);
        } catch (IOException e) {
            ui.showError(e.getMessage());
        }

        viewLog = new ViewLog(ui, workoutManager, fileHandler);
        goalTracker = new GoalWeightTracker();
    }
}
