package seedu.fitchasers;

import seedu.fitchasers.exceptions.FileNonexistent;
import seedu.fitchasers.exceptions.InvalidArgumentInput;
import seedu.fitchasers.ui.Parser;
import seedu.fitchasers.ui.UI;
import seedu.fitchasers.workouts.ViewLog;
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
import seedu.fitchasers.storage.FileHandler;

import java.io.IOException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


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
    private static final Parser parser = new Parser();
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

    public static void main(String[] args) throws IOException, FileNonexistent {
        ui.printLeftHeader();
        initVariables();
        ui.showGreeting();
        try {
            viewLog.render(argumentStr);
        } catch (IndexOutOfBoundsException | InvalidArgumentInput e) {
            ui.showError(e.getMessage());
        }
        workoutManager.initWorkouts();
        while (isRunning) {
            input = parser.readCommand();
            if (input == null) {
                break;
            }

            if (input.trim().isEmpty()) {
                ui.showMessage("Please enter a command, or type /help or h for options.");
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
                    parser.handleHelp(command, argumentStr);
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
                //@@author Kart04
                case "/del_workout":
                case "dw":
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

//    private static void delMethod() throws InvalidCommandException, IOException, InvalidArgumentInput, FileNonexistent {
//        // Format: /del_workout WORKOUT_NAME
//        if (argumentStr.isEmpty()) {
//            throw new InvalidCommandException("Workout deletion command requires a workout name or date. " +
//                    "Please enter a valid command.");
//        } else if (argumentStr.contains("-m")) {
//            // Use the Parser's DTO instead of the removed ViewLog.Parsed
//            Parser.ViewLogArgs p = parser.parseViewLog(argumentStr);
//            workoutManager.setWorkouts(fileHandler.loadMonthList(p.ym), p.ym);
//            // In the new DTO, the old "extractedArg" is the "page" field
//            workoutManager.deleteWorkoutByIndex(p.page);
//        } else {
//            workoutManager.deleteWorkout(argumentStr);
//        }
//    }

    private static void delMethod() throws InvalidCommandException, IOException, InvalidArgumentInput, FileNonexistent {
        if (argumentStr == null) throw new InvalidCommandException(
                "Usage:\n  /del_workout <WORKOUT_NAME>\n  /del_workout -m <MONTH> <INDEX>"
        );

        Parser.DelArgs a = parser.parseDel(argumentStr);

        if (a.byMonth) {
            // Load the specified month and delete by 1-based index
            workoutManager.setWorkouts(fileHandler.loadMonthList(a.ym), a.ym);
            if (!workoutManager.deleteWorkoutByIndex(a.index1Based - 1)) {
                ui.showMessage("No workout found at index " + a.index1Based + " in " + a.ym + ".");
                return;
            }
            fileHandler.saveMonthList(a.ym, workoutManager.getWorkouts());
            ui.showMessage("Deleted workout #" + a.index1Based + " in " + a.ym + ".");
            return;
        }

        // Exact-name delete in the currently loaded month (old behavior)
        final String name = a.targetName.trim();
        if (workoutManager.deleteWorkout(name)) {
            // Persist to whatever month is currently loaded in memory (your old code saved to currentMonth)
            fileHandler.saveMonthList(currentMonth, workoutManager.getWorkouts());
            ui.showMessage("Deleted workout \"" + name + "\".");
        } else {
            ui.showMessage("No workout named \"" + name + "\" in the current month.");
        }
    }


//    private static void owtMethod() throws FileNonexistent, IOException {
//        // Parse parameters
//        String[] params = argumentStr.split("\\s+");
//        Integer workoutId = null;
//        String newTag = null;
//
//        for (String param : params) {
//            if (param.startsWith("id/")) {
//                try {
//                    workoutId = Integer.parseInt(param.substring(3));
//                } catch (NumberFormatException e) {
//                    ui.showMessage("Invalid workout ID.");
//                    return;
//                }
//            } else if (param.startsWith("newTag/")) {
//                newTag = param.substring(7);
//            }
//        }
//
//        if (workoutId != null && newTag != null) {
//            // Validate empty tag
//            if (newTag.trim().isEmpty()) {
//                ui.showMessage("Tag cannot be empty.");
//                return;
//            }
//
//            // Validate workout ID
//            if (workoutId <= 0 || workoutId > workoutManager.getWorkouts().size()) {
//                ui.showMessage("Invalid workout ID. Use valid ID between 1 and " +
//                        workoutManager.getWorkouts().size());
//                return;
//            }
//
//            Workout workout = viewLog.getWorkoutByDisplayId(workoutId, currentMonth);
//            if (workout == null) {
//                ui.showMessage("Invalid workout ID.");
//                return;
//            }
//
//            Set<String> oldTags = workout.getAllTags();
//
//            ui.showMessage("Current tags: " + String.join(", ", oldTags));
//            ui.showMessage("Change to: " + newTag + "?");
//            ui.showMessage("Are you sure? (y/n)");
//
//            if (!parser.confirmationMessage()) {
//                ui.showMessage("Tag change cancelled.");
//                return;
//            }
//
//            Set<String> autoTagsThatWillBeOverridden = tagger.suggest(workout);
//            if (!autoTagsThatWillBeOverridden.isEmpty()) {
//                ui.showMessage("WARNING: This will override auto generated tags: " + String.join(", ",
//                        autoTagsThatWillBeOverridden));
//                ui.showMessage("Continue with override? (y/n)");
//
//                if (!parser.confirmationMessage()) {
//                    ui.showMessage("Override cancelled.");
//                    return;
//                }
//            }
//
//            workoutManager.overrideWorkoutTags(workout, newTag);
//
//
//
//            try {
//                fileHandler.saveMonthList(currentMonth, workoutManager.getWorkouts());
//
//                ArrayList<Workout> reloadedWorkouts = fileHandler.getWorkoutsForMonth(currentMonth);
//                workoutManager.setWorkouts(reloadedWorkouts);
//
//                ui.showMessage("Workout tags updated successfully.");
//                ui.showMessage("  New tags: " + newTag);
//
//                Set<String> conflicts = workoutManager.checkForOverriddenTags(reloadedWorkouts.get(workoutId - 1));
//
//                if (!conflicts.isEmpty()) {
//                    ui.showMessage("WARNING: These manual tags override auto-tags: " + conflicts);
//                }
//
//            } catch (IOException e) {
//                ui.showMessage("Error saving workout data: " + e.getMessage());
//            } catch (FileNonexistent e) {
//                throw new RuntimeException(e);
//            }
//
//        } else {
//            ui.showMessage("Usage: /override_workout_tag id/WORKOUT_ID newTag/NEW_TAG");
//        }
//    }

    private static void owtMethod() throws FileNonexistent, IOException, InvalidArgumentInput {
        if (argumentStr == null) { ui.showMessage("Usage: /override_workout_tag id/ID newTag/TAG [-m M| -ym Y M]"); return; }
        Parser.OverrideTagArgs a = parser.parseOverrideWorkoutTag(argumentStr);

        // Resolve month context and workout
        workoutManager.setWorkouts(fileHandler.loadMonthList(a.ym), a.ym);
        if (a.displayId <= 0 || a.displayId > workoutManager.getWorkouts().size()) {
            ui.showMessage("Invalid workout ID. Use 1.." + workoutManager.getWorkouts().size());
            return;
        }
        Workout w = viewLog.getWorkoutByDisplayId(a.displayId, a.ym);
        if (w == null) { ui.showMessage("Invalid workout ID."); return; }

        if (!a.confirmChange) {
            ui.showMessage("Tag change cancelled.");
            return;
        }

        // If your logic needs to check auto-tag conflicts before overriding:
        Set<String> autoToBeOverridden = tagger.suggest(w);
        if (!autoToBeOverridden.isEmpty() && !a.confirmOverrideAuto) {
            ui.showMessage("Override cancelled (auto-tags present).");
            return;
        }

        workoutManager.overrideWorkoutTags(w, a.newTag);
        fileHandler.saveMonthList(a.ym, workoutManager.getWorkouts());

        ui.showMessage("Workout tags updated. New tags: " + a.newTag);

        // Optionally warn if conflicts remain
        Set<String> conflicts = workoutManager.checkForOverriddenTags(w);
        if (!conflicts.isEmpty()) {
            ui.showMessage("WARNING: These manual tags override auto-tags: " + conflicts);
        }
    }


//    private static void gpMethod() {
//        try {
//            String trimmedArg = argumentStr.trim();
//            Gym selectedGym = null;
//
//            if (trimmedArg.startsWith("p/") && trimmedArg.length() > 2) {
//                String input = trimmedArg.substring(2).trim();
//                if (input.isEmpty()) {  // ADD THIS
//                    ui.showMessage("Please provide a gym number or name.");
//                    listAvailableGyms();
//                    return;
//                }
//
//                try {
//                    int pageNum = Integer.parseInt(input);
//                    if (pageNum >= 1 && pageNum <= gyms.size()) {
//                        selectedGym = gyms.get(pageNum - 1);
//                    }
//                } catch (NumberFormatException e) {
//                    selectedGym = findGymByName(input);
//                }
//
//                if (selectedGym != null) {
//                    EquipmentDisplay.showEquipmentForSingleGym(selectedGym);
//                } else {
//                    ui.showMessage("Invalid gym. Use number (1-" + gyms.size() + ") or gym name (e.g., SRC Gym)");
//                    listAvailableGyms();
//                }
//            } else {
//                ui.showMessage("Usage: /gym_page p/page_number_or_gym_name");
//                ui.showMessage("Example: /gym_page p/1 OR /gym_page p/SRC Gym");
//                listAvailableGyms();
//            }
//        } catch (Exception e) {
//            ui.showMessage("Error: " + e.getMessage());
//        }
//    }

    private static void gpMethod() {
        try {
            Parser.GymPageArgs a = parser.parseGymPage(argumentStr == null ? "" : argumentStr);
            Gym selected = null;

            if (a.pageNumber != null) {
                int p = a.pageNumber;
                if (p >= 1 && p <= gyms.size()) selected = gyms.get(p - 1);
            } else if (a.gymName != null) {
                selected = findGymByName(a.gymName);
            }

            if (selected != null) {
                EquipmentDisplay.showEquipmentForSingleGym(selected);
            } else {
                ui.showMessage("Invalid gym. Use number (1-" + gyms.size() + ") or gym name (e.g., SRC Gym)");
                listAvailableGyms();
            }
        } catch (InvalidArgumentInput e) {
            ui.showMessage(e.getMessage());
            listAvailableGyms();
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

//    private static void gwMethod() {
//        String trimmedArg = argumentStr.trim();
//        try {
//            if (trimmedArg.startsWith("n/") && trimmedArg.length() > 2) {
//                Set<String> gymsToSuggest = EquipmentDisplay.suggestGymsForExercise(gyms, argumentStr);
//                if (!gymsToSuggest.isEmpty()) {
//                    ui.showMessage("You can do this workout at: " + String.join(", ",
//                            gymsToSuggest));
//                } else {
//                    ui.showMessage("Sorry, no gyms found for that exercise.");
//                }
//            } else {
//                ui.showMessage("Usage: /gym_where n/exercise_name");
//            }
//        } catch (Exception e) {
//            ui.showMessage("An error occurred while searching for gyms. Please check your input " +
//                    "and try again.");
//        }
//    }

    private static void gwMethod() {
        try {
            Parser.GymWhereArgs a = parser.parseGymWhere(argumentStr == null ? "" : argumentStr);
            Set<String> gymsToSuggest = EquipmentDisplay.suggestGymsForExercise(gyms, "n/" + a.exerciseName);
            if (!gymsToSuggest.isEmpty()) {
                ui.showMessage("You can do this workout at: " + String.join(", ", gymsToSuggest));
            } else {
                ui.showMessage("Sorry, no gyms found for that exercise.");
            }
        } catch (InvalidArgumentInput e) {
            ui.showMessage(e.getMessage());
        } catch (Exception e) {
            ui.showMessage("An error occurred while searching for gyms.");
        }
    }

//    private static void amtMethod() {
//        String[] params = argumentStr.split("\\s+");
//        String mus = null;
//        String keyword = null;
//        for (String param : params) {
//            if (param.startsWith("m/")) {
//                mus = param.substring(2).toUpperCase();
//            }
//            if (param.startsWith("k/")) {
//                keyword = param.substring(2).toLowerCase();
//            }
//        }
//        if (mus != null && mus.trim().isEmpty()) {
//            ui.showMessage("Muscle cannot be empty. Use: LEGS, POSTERIOR_CHAIN, CHEST, BACK, SHOULDERS, ARMS, CORE");
//            return;
//        }
//
//        if (keyword != null && keyword.trim().isEmpty()) {
//            ui.showMessage("Keyword cannot be empty. Example: /add_muscle_tag m/CARDIO k/running");
//            return;
//        }
//        if (mus != null && keyword != null) {
//            try {
//                MuscleGroup muscleGroup = MuscleGroup.valueOf(mus);
//                tagger.addMuscleKeyword(muscleGroup, keyword);
//
//                for (Workout w : workoutManager.getWorkouts()) {
//                    Set<String> updatedTags = tagger.suggest(w);
//                    w.setAutoTags(updatedTags);
//                    ui.showMessage("Retagged workout " + w.getWorkoutName() + ": " + updatedTags);
//                }
//                try {
//                    fileHandler.saveMonthList(currentMonth, workoutManager.getWorkouts());
//                    ui.showMessage("Added keyword " + keyword + " to muscle group " + mus);
//                } catch (IOException e) {
//                    ui.showMessage("Error saving changes: " + e.getMessage());
//                }
//            } catch (IllegalArgumentException e) {
//                ui.showMessage("Invalid muscle group. Valid options: LEGS, POSTERIOR_CHAIN, CHEST, BACK, " +
//                        "SHOULDERS, ARMS, CORE");
//            }
//        } else {
//            ui.showMessage("Usage: /add_muscle_tag m/LEGS/ CHEST/... k/keyword");
//        }
//    }

    private static void amtMethod() {
        try {
            Parser.AddMuscleTagArgs a = parser.parseAddMuscleTag(argumentStr == null ? "" : argumentStr);
            MuscleGroup mg = MuscleGroup.valueOf(a.muscleEnum.toUpperCase());

            tagger.addMuscleKeyword(mg, a.keyword);
            for (Workout w : workoutManager.getWorkouts()) {
                Set<String> updated = tagger.suggest(w);
                w.setAutoTags(updated);
            }
            fileHandler.saveMonthList(currentMonth, workoutManager.getWorkouts());
            ui.showMessage("Added keyword '" + a.keyword + "' to muscle group " + mg);

        } catch (IllegalArgumentException e) {
            ui.showMessage("Invalid muscle group. Valid: LEGS, POSTERIOR_CHAIN, CHEST, BACK, SHOULDERS, ARMS, CORE");
        } catch (InvalidArgumentInput e) {
            ui.showMessage(e.getMessage());
        } catch (IOException e) {
            ui.showMessage("Error saving changes: " + e.getMessage());
        }
    }

//    private static void amotMethod() {
//        String[] params = argumentStr.split("\\s+");
//        String mod = null;
//        String keyword = null;
//
//        for (String param : params) {
//            if (param.startsWith("m/")) {
//                mod = param.substring(2).toUpperCase();
//            }
//            if (param.startsWith("k/")) {
//                keyword = param.substring(2).toLowerCase();
//            }
//        }
//
//        if (mod != null && mod.trim().isEmpty()) {
//            ui.showMessage("Modality cannot be empty. Use: CARDIO or STRENGTH");
//            return;
//        }
//
//        if (keyword != null && keyword.trim().isEmpty()) {
//            ui.showMessage("Keyword cannot be empty. Example: /add_modality_tag m/CARDIO k/running");
//            return;
//        }
//
//        if (mod != null && keyword != null) {
//            try {
//                Modality modality = Modality.valueOf(mod);
//                tagger.addModalityKeyword(modality, keyword);
//
//                // Check ONLY workouts that contain this keyword
//                StringBuilder conflicts = new StringBuilder();
//                List<Workout> affectedWorkouts = new ArrayList<>();
//
//                for (Workout w : workoutManager.getWorkouts()) {
//                    String workoutText = w.getWorkoutName().toLowerCase();
//                    if (workoutText.contains(keyword.toLowerCase())) {
//                        affectedWorkouts.add(w);
//                        if (workoutManager.hasConflictingModality(w, mod)) {
//                            String existing = workoutManager.getConflictingModality(w);
//                            conflicts.append("\n - ").append(w.getWorkoutName())
//                                    .append(" is already tagged to ").append(existing);
//                        }
//                    }
//                }
//
//                if (!conflicts.isEmpty()) {
//                    ui.showMessage("CANNOT ADD KEYWORD: Conflicting modality tags detected:");
//                    ui.showMessage(conflicts.toString());
//                    ui.showMessage("\nTo change these tags, first remove the old keyword or manually edit the tag.");
//                    return;
//                }
//
//                for (Workout w : affectedWorkouts) {
//                    Set<String> updatedTags = tagger.suggest(w);
//                    w.setAutoTags(updatedTags);
//                    ui.showMessage("Retagged: " + w.getWorkoutName() + " → " + updatedTags);
//                }
//
//                try {
//                    fileHandler.saveMonthList(currentMonth, workoutManager.getWorkouts());
//                    ui.showMessage("Added keyword '" + keyword + "' to modality " + mod);
//                } catch (IOException e) {
//                    ui.showMessage("Error saving changes: " + e.getMessage());
//                }
//            } catch (IllegalArgumentException e) {
//                ui.showMessage("Invalid modality. Valid options: CARDIO, STRENGTH");
//            }
//
//        } else {
//            ui.showMessage("Usage: /add_modality_tag m/(CARDIO/STRENGTH) k/keyword");
//        }
//    }

    private static void amotMethod() {
        try {
            Parser.AddModalityTagArgs a = parser.parseAddModalityTag(argumentStr == null ? "" : argumentStr);
            Modality modality = Modality.valueOf(a.modalityEnum.toUpperCase());

            // Conflict preview limited to workouts containing the keyword
            String kw = a.keyword.toLowerCase();
            StringBuilder conflicts = new StringBuilder();
            List<Workout> affected = new ArrayList<>();
            for (Workout w : workoutManager.getWorkouts()) {
                if (w.getWorkoutName().toLowerCase().contains(kw)) {
                    affected.add(w);
                    if (workoutManager.hasConflictingModality(w, modality.name())) {
                        String existing = workoutManager.getConflictingModality(w);
                        conflicts.append("\n - ").append(w.getWorkoutName()).append(" already tagged ").append(existing);
                    }
                }
            }
            if (!conflicts.isEmpty()) {
                ui.showMessage("CANNOT ADD KEYWORD: Conflicting modality tags detected:" + conflicts +
                        "\nTo change these tags, first remove the old keyword or manually edit the tag.");
                return;
            }

            tagger.addModalityKeyword(modality, kw);
            for (Workout w : affected) {
                Set<String> updated = tagger.suggest(w);
                w.setAutoTags(updated);
            }
            fileHandler.saveMonthList(currentMonth, workoutManager.getWorkouts());
            ui.showMessage("Added keyword '" + a.keyword + "' to modality " + modality);

        } catch (IllegalArgumentException e) {
            ui.showMessage("Invalid modality. Valid: CARDIO, STRENGTH");
        } catch (InvalidArgumentInput e) {
            ui.showMessage(e.getMessage());
        } catch (IOException e) {
            ui.showMessage("Error saving changes: " + e.getMessage());
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

    private static void initVariables() throws IOException, FileNonexistent {
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
            String userName = parser.enterName();
            person = new Person(userName);
            try {
                fileHandler.saveUserName(person);
                ui.showMessage("Your name has been saved.");
            } catch (IOException e) {
                ui.showError("Failed to save username: " + e.getMessage());
            }

            // Prompt for initial weight
            WeightManager tempWeightManager = new WeightManager(person);
            double initialWeight = parser.enterWeight(tempWeightManager);
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
        } catch (IOException e) {
            ui.showError(e.getMessage());
        } catch (FileNonexistent e) {
            fileHandler.saveMonthList(currentMonth, new ArrayList<>());
        }

        viewLog = new ViewLog(ui, workoutManager, fileHandler, parser);
        goalTracker = new GoalWeightTracker();
    }
}
