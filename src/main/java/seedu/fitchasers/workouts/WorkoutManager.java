package seedu.fitchasers.workouts;

import seedu.fitchasers.FileHandler;
import seedu.fitchasers.exceptions.InvalidArgumentInput;
import seedu.fitchasers.tagger.Tagger;
import seedu.fitchasers.ui.UI;

import java.io.IOException;
import java.time.YearMonth;
import java.time.format.ResolverStyle;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Manages workout sessions for the FitChasers application.
 * <p>
 * Handles creation, deletion, and viewing of workouts,
 * as well as adding exercises and sets within each workout.
 */
public class WorkoutManager {
    private static final int ARRAY_OFFSET = 1;
    private static final int MAX_EXERCISE_NAME_LEN = 32;
    private static final int MAX_REPS = 1000;
    private ArrayList<Workout> workouts = new ArrayList<>();
    private Workout currentWorkout = null;
    private final UI ui = new UI();
    private final Tagger tagger;
    private LocalDateTime workoutDateTime;
    private String workoutName;
    private YearMonth monthOfWorkout;
    private YearMonth currentLoadedMonth;
    private final Map<YearMonth, ArrayList<Workout>> workoutsByMonth;
    private final Set<YearMonth> loadedMonths = new HashSet<>();
    private final FileHandler fileHandler;

    private static final Pattern NAME_ALLOWED = Pattern.compile("[A-Za-z0-9 _-]+");
    private static final Pattern NAME_ILLEGAL_FINDER = Pattern.compile("[^A-Za-z0-9 _-]");
    private static final Pattern BOUND_PREFIX = Pattern.compile("(^|\\s)([A-Za-z])/");
    private static final Pattern NEXT_PREFIX = Pattern.compile("\\s+[nr]/");
    private static final Pattern REPS_TOKEN = Pattern.compile("^\\d{1,4}$");

    public WorkoutManager(Tagger tagger, FileHandler fileHandler) {
        this.tagger = tagger;
        this.fileHandler = fileHandler;
        this.workoutsByMonth = fileHandler.getArrayByMonth();
        this.currentLoadedMonth = YearMonth.now();
    }

    public void setWorkouts(ArrayList<Workout> workouts, YearMonth monthOfArrayList) {
        this.workouts = workouts;
        currentLoadedMonth = monthOfArrayList;
    }

    /**
     * Creates and adds a new workout to the list.
     * <p>
     * Expected format: /create_workout n/NAME d/DD/MM/YY t/HHmm
     *
     * @param command the full user command containing workout details
     */
    public void addWorkout(String command) {
        workoutName = "";
        try {
            formatInputForWorkout(command);
        } catch (InvalidArgumentInput e) {
            return;
        }
        monthOfWorkout = YearMonth.from(workoutDateTime);
        if (!currentLoadedMonth.equals(monthOfWorkout)) {
            setWorkouts(fileHandler.getWorkoutsForMonth(monthOfWorkout), monthOfWorkout);
        }

        try {
            Workout newWorkout = new Workout(workoutName, workoutDateTime);

            // merge auto-tags if you have a tagger
            Set<String> suggestedTags = tagger.suggest(newWorkout);
            newWorkout.setAutoTags(suggestedTags);
            workouts.add(newWorkout);
            currentWorkout = newWorkout;
            ui.showMessage("New workout sesh incoming!");
            ui.showMessage("Tags generated for workout: " + suggestedTags + "\n" + "Added workout: " + workoutName);
            fileHandler.saveMonthList(currentLoadedMonth, workouts);

        } catch (Exception e) {
            ui.showMessage("Something went wrong creating the workout. Please try again.");
        }
    }

    private void formatInputForWorkout(String command) throws InvalidArgumentInput {
        assert workouts != null : "workouts list should be initialized";
        if (currentWorkout != null) {
            ui.showMessage("You currently have an active workout: '" + currentWorkout.getWorkoutName() + "'.");
            ui.showMessage("Please end the active workout first with: /end_workout d/DD/MM/YY t/HHmm");
            throw new InvalidArgumentInput("");
        }

        assert currentWorkout == null : "No active workout expected before creating a new one";

        if (command == null || !command.contains("n/")) {
            ui.showMessage("Invalid format. Use: /create_workout n/WorkoutName d/DD/MM/YY t/HHmm");
            throw new InvalidArgumentInput("");
        }

        int nameIndex = command.indexOf("n/");
        int afterNameIndex = nameIndex + 2;

        // Find first marker after n/
        int dIndex = command.indexOf("d/", afterNameIndex);
        int tIndex = command.indexOf("t/", afterNameIndex);

        // pick the nearest positive marker after n/
        int nextMarker = -1;
        if (dIndex != -1 && tIndex != -1) {
            nextMarker = Math.min(dIndex, tIndex);
        } else if (dIndex != -1) {
            nextMarker = dIndex;
        } else if (tIndex != -1) {
            nextMarker = tIndex;
        }

        if (nextMarker != -1) {
            workoutName = command.substring(afterNameIndex, nextMarker).trim();
        } else {
            workoutName = command.substring(afterNameIndex).trim();
        }

        if (workoutName.isEmpty()) {
            ui.showMessage("Workout name cannot be empty. Use: /create_workout n/WorkoutName d/DD/MM/YY t/HHmm");
            throw new InvalidArgumentInput("");
        }

        assert !workoutName.isEmpty() : "workoutName should be non-empty after validation";

        // Extract raw date/time strings if present (first token after marker)
        String dateStr = "";
        if (dIndex != -1) {
            String tail = command.substring(dIndex + 2).trim();
            String[] token = tail.split("\\s+");
            if (token.length > 0) {
                dateStr = token[0];
            }
        }

        String timeStr = "";
        if (tIndex != -1) {
            String tail = command.substring(tIndex + 2).trim();
            String[] toks = tail.split("\\s+");
            if (toks.length > 0) {
                timeStr = toks[0];
            }
        }

        // Strict formatters & validate provided pieces first
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yy").withResolverStyle(ResolverStyle.SMART);
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HHmm").withResolverStyle(ResolverStyle.SMART);

        LocalDate date = null;
        LocalTime time = null;

        if (!dateStr.isEmpty()) {
            try {
                date = LocalDate.parse(dateStr, dateFmt);
            } catch (Exception ex) {
                ui.showMessage("Invalid date. Use d/DD/MM/YY (e.g., d/23/10/25).");
                throw new InvalidArgumentInput("");
            }
        }

        if (!timeStr.isEmpty()) {
            try {
                time = LocalTime.parse(timeStr, timeFmt);
            } catch (Exception ex) {
                ui.showMessage("Invalid time. Use t/HHmm (e.g., t/1905).");
                throw new InvalidArgumentInput("");
            }
        }

        // Prompt ONLY for missing ones
        if (date == null) {
            String todayStr = LocalDate.now().format(dateFmt);
            ui.showMessage("Looks like you missed the date. Use current date (" + todayStr + ")? (Y/N)");
            if (ui.confirmationMessage()) {
                date = LocalDate.now();
            } else {
                ui.showMessage("Please provide a date in format d/DD/MM/YY.");
                throw new InvalidArgumentInput("");
            }
        }

        if (time == null) {
            String nowStr = LocalTime.now().format(timeFmt);
            ui.showMessage("Looks like you missed the time. Use current time (" + nowStr + ")? (Y/N)");
            if (ui.confirmationMessage()) {
                time = LocalTime.now();
            } else {
                ui.showMessage("Please provide a time in format t/HHmm.");
                throw new InvalidArgumentInput("");
            }
        }
        workoutDateTime = LocalDateTime.of(date, time);

        if (date.isAfter(LocalDate.now())) {
            ui.showMessage("The date you entered (" + date.format(dateFmt) + ") is in the future. Are you sure? (Y/N)");
            if (!ui.confirmationMessage()) {
                ui.showMessage("Please re-enter the correct date.");
                throw new InvalidArgumentInput("");
            }
        }

        if (date.isEqual(LocalDate.now()) && time.isAfter(LocalTime.now())) {
            ui.showMessage("The time you entered (" + time.format(timeFmt) + ") is in the future. Are you sure? (Y/N)");
            if (!ui.confirmationMessage()) {
                ui.showMessage("Please re-enter the correct time.");
                throw new InvalidArgumentInput("");
            }
        }

        // Check if any existing workout already has the same date/time
        for (Workout w : workouts) {
            LocalDateTime existingStart = w.getWorkoutStartDateTime();
            if (existingStart != null) {
                LocalDate existingDate = existingStart.toLocalDate();
                LocalTime existingTime = existingStart.toLocalTime();

                if (existingDate.equals(date) && existingTime.equals(time)) {
                    ui.showMessage("A workout already exists at this date and time (" + existingDate.format(dateFmt) + " " + existingTime.format(timeFmt) + "). " + "Continue anyway? (Y/N)");
                    if (!ui.confirmationMessage()) {
                        ui.showMessage("Workout creation cancelled. Please pick a different time or date.");
                        throw new InvalidArgumentInput("");
                    }
                    break;
                }
            }
        }
        workoutDateTime = LocalDateTime.of(date, time);
    }

    /**
     * Extracts text between two tokens.
     *
     * @param text       the full string to search
     * @param startToken the start marker
     * @param endToken   the end marker
     * @return the substring found between the tokens, or an empty string if not found
     */
    private String extractBetween(String text, String startToken, String endToken) {
        int start = text.indexOf(startToken) + startToken.length();
        int end = text.indexOf(endToken);
        if (start < startToken.length() || end == -1) {
            return "";
        }
        return text.substring(start, end);
    }

    /**
     * Extracts text after a specific token.
     *
     * @param text  the full string to search
     * @param token the token to find
     * @return the text found after the token, or an empty string if not found
     */
    private String extractAfter(String text, String token) {
        int index = text.indexOf(token);
        if (index == -1) {
            return "";
        }
        return text.substring(index + token.length()).trim();
    }

    public int getWorkoutSize() {
        return workouts.size();
    }

    /**
     * Returns all workouts.
     *
     * @return the list of workouts
     */
    public ArrayList<Workout> getWorkouts() {
        return workouts;
    }

    /**
     * Deletes a workout by name.
     *
     * @param name the name of the workout to delete
     */
    public void deleteWorkout(String name) throws IOException {
        for (Workout w : workouts) {
            if (w.getWorkoutName().equals(name)) {
                ui.showMessage("Deleting " + w.getWorkoutName() + " | " + w.getWorkoutDateString() + "? T.T Are you sure, bestie? (Type y/yes to confirm)");
                if (ui.confirmationMessage()) {
                    workouts.remove(w);
                    fileHandler.saveMonthList(currentLoadedMonth, workouts);
                    ui.showMessage("Workout deleted successfully!");
                } else {
                    ui.showMessage("Okay, I didn’t delete it.");
                }
                return;
            }
        }
        ui.showMessage("Workout not found: " + name);
    }

    public void deleteWorkoutByIndex(int index) throws IOException {
        if (index < 0 || index >= workouts.size()) {
            ui.showMessage("Invalid workout index: " + index + "Please try again.:(");
            return;
        }
        Workout w = workouts.get(index);
        ui.showMessage("Deleting " + w.getWorkoutName() + " | " + w.getWorkoutName() + "|");
        ui.showMessage("You sure you want to delete this?(y/n)");
        if (ui.confirmationMessage()) {
            ui.showMessage("Deleted workout: " + w.getWorkoutName());
            workouts.remove(index);
            fileHandler.saveMonthList(currentLoadedMonth, workouts);
        } else {
            ui.showMessage("Okay, deletion aborted.");
        }
    }

    private ArrayList<Workout> getWorkoutsByDate(LocalDate date) {
        ArrayList<Workout> filteredWorkout = new ArrayList<>();
        for (Workout w : workouts) {
            LocalDateTime startDateTime = w.getWorkoutStartDateTime();
            if (startDateTime != null && startDateTime.toLocalDate().equals(date)) {
                filteredWorkout.add(w);
            }
        }
        return filteredWorkout;
    }

    /**
     * Adds an exercise to the current workout.
     * <p>
     * Expected format: /add_exercise n/NAME r/REPS
     *
     * @param args the user command arguments
     */
    public void addExercise(String args) {
        if (currentWorkout == null) {
            ui.showMessage("No active workout. Use /create_workout first.");
            return;
        }

        if (args == null || args.trim().isEmpty()) {
            ui.showMessage("Missing information. Use: /add_exercise n/NAME r/REPS (e.g., /add_exercise n/PushUp r/12)");
            return;
        }

        String s = args.trim();

        if (s.isEmpty()) {
            ui.showMessage("Missing information. Use: /add_exercise n/NAME r/REPS");
            ui.showMessage("NAME: letters/digits/space/-/_ (max 32)\nREPS: 1–1000");
            return;
        }

        // Must have exactly one n/ and one r/
        if (countToken(s, "n/") != 1 || countToken(s, "r/") != 1) {
            ui.showMessage("Please provide exactly one n/ and one r/ in this order: n/NAME r/REPS");
            return;
        }

        // Enforce order n/ ... r/
        int nIdx = s.indexOf("n/");
        int rIdx = s.indexOf("r/");
        if (nIdx == -1 || rIdx == -1 || rIdx < nIdx) {
            ui.showMessage("Order must be n/ then r/. Example: /add_exercise n/Bench Press r/12");
            return;
        }

        // Extract name slice (raw + trimmed), then validate name BEFORE scanning for other prefixes
        Slice nameSlice = extractSlice(s, nIdx);
        String name = nameSlice.valueTrimmed;

        // Specific, user-friendly name errors
        if (name.trim().isEmpty()) {
            ui.showMessage("Exercise name is missing after n/. Example: n/Bench Press");
            return;
        }
        if (name.length() > MAX_EXERCISE_NAME_LEN) {
            ui.showMessage("Name too long (" + name.length() + "). Max allowed is " + MAX_EXERCISE_NAME_LEN + " characters.");
            return;
        }
        if (!NAME_ALLOWED.matcher(name).matches()) {
            Character bad = findFirstIllegalNameChar(name);
            if (bad != null) {
                // Show the first problematic character clearly, including slashes
                String shown = (bad == '\\') ? "\\\\" : String.valueOf(bad);
                ui.showMessage("“" + shown + "” is not allowed in the exercise name.");
            }
            ui.showMessage("Allowed characters: letters, digits, spaces, hyphen (-), underscore (_).");
            return;
        }

        // Now extract reps slice
        Slice repsSlice = extractSlice(s, rIdx);

        // If there are spaces immediately after r/, guide them explicitly
        if (!repsSlice.valueRaw.isEmpty() && Character.isWhitespace(repsSlice.valueRaw.charAt(0))) {
            ui.showMessage("Remove spaces between r/ and the number. Example: r/12 (not r/ 12)");
            return;
        }

        String repsStr = repsSlice.valueTrimmed;
        Integer reps = parseRepsSafe(repsStr);
        if (reps == null) {
            ui.showMessage("Invalid reps. Use a whole number between 1 and 1000. Example: r/12");
            return;
        }

        // No extra junk after reps
        if (!onlyWhitespaceAfter(s, repsSlice.endIndex)) {
            ui.showMessage("Unexpected text after reps. Use exactly: /add_exercise n/NAME r/REPS");
            return;
        }

        // Also check there are no stray flag-like prefixes OUTSIDE the parsed ranges
        // (ignore n/.. inside [nIdx, nameSlice.endIndex) and r/.. inside [rIdx, repsSlice.endIndex))
        Matcher stray = BOUND_PREFIX.matcher(s);
        while (stray.find()) {
            int pos = stray.start(2);
            char p = stray.group(2).charAt(0);

            boolean insideName = pos >= nIdx && pos < nameSlice.endIndex;
            boolean insideReps = pos >= rIdx && pos < repsSlice.endIndex;

            if (!insideName && !insideReps) {
                if (p != 'n' && p != 'r') {
                    ui.showMessage("Unsupported flag \"" + p + "/\" found. Only n/ and r/ are allowed.");
                    return;
                }
            }
        }

        Exercise exercise = new Exercise(name, reps);
        currentWorkout.addExercise(exercise);

        ui.showMessage("Adding that spicy new exercise!");
        ui.showMessage("Added exercise:\n" + exercise.toDetailedString());
    }

    /**
     * Adds a new set to the current exercise.
     * <p>
     * Expected format: /add_set r/REPS
     *
     * @param args the user command arguments
     */
    public void addSet(String args) {
        if (currentWorkout == null) {
            ui.showMessage("No active workout. Use /create_workout first.");
            return;
        }

        Exercise currentExercise = currentWorkout.getCurrentExercise();
        if (currentExercise == null) {
            ui.showMessage("No exercises yet. Add an exercise first with /add_exercise n/NAME r/REPS");
            return;
        }

        assert !(currentWorkout.getExercises().isEmpty() && currentExercise != null)
                : "Invariant violated: empty list but currentExercise not null";

        if (args == null || args.trim().isEmpty()) {
            ui.showMessage("Missing information. Use: /add_set r/REPS");
            ui.showMessage("REPS: 1–1000");
            return;
        }

        String s = args.trim();

        // Exactly one r/ and NO other flags at token boundaries
        if (countToken(s, "r/") != 1) {
            ui.showMessage("Provide exactly one r/. Usage: /add_set r/REPS");
            return;
        }

        // Reject any other boundary flags like n/, x/, etc.
        java.util.regex.Matcher stray = BOUND_PREFIX.matcher(s);
        while (stray.find()) {
            char p = stray.group(2).charAt(0); // letter before '/'
            if (p != 'r') {
                ui.showMessage("Only r/ is allowed for this command. Usage: /add_set r/REPS");
                return;
            }
        }

        // Extract reps
        int rIdx = s.indexOf("r/");
        Slice repsSlice = extractSlice(s, rIdx);

        // If user typed spaces right after r/, treat as a generic invalid reps format (no niche msg)
        if (!repsSlice.valueRaw.isEmpty() && Character.isWhitespace(repsSlice.valueRaw.charAt(0))) {
            ui.showMessage("Invalid reps. Use a whole number between 1 and 1000. Example: /add_set r/15");
            return;
        }

        String repsStr = repsSlice.valueTrimmed;
        Integer reps = parseRepsSafe(repsStr);
        if (reps == null) {
            ui.showMessage("Invalid reps. Use a whole number between 1 and 1000. Example: /add_set r/15");
            return;
        }

        // No extra junk after reps (e.g., "r/ 1 2", "r/12 extra")
        if (!onlyWhitespaceAfter(s, repsSlice.endIndex)) {
            ui.showMessage("Unexpected text after reps. Use exactly: /add_set r/REPS");
            return;
        }

        currentExercise.addSet(reps);

        ui.showMessage("Adding a new set to your exercise!");
        ui.showMessage("Added set to exercise:\n" + currentExercise.toDetailedString());
    }

    /**
     * Displays all workouts and their exercises in a formatted list.
     */
    public void viewWorkouts() {
        if (workouts.isEmpty()) {
            ui.showMessage("No workouts recorded yet!");
            return;
        }

        for (int i = 0; i < workouts.size(); i++) {
            Workout w = workouts.get(i);
            ui.showMessage("------------------------------------------------");
            ui.showMessage("[" + (i + ARRAY_OFFSET) + "]: " + w.getWorkoutName() + " | " + w.getDuration() + " Min");

            if (w.getExercises().isEmpty()) {
                ui.showMessage("     No exercises added yet.");
            } else {
                for (int j = 0; j < w.getExercises().size(); j++) {
                    Exercise ex = w.getExercises().get(j);
                    ui.showMessage("     Exercise " + (j + 1) + ". " + ex);
                    for (int k = 0; k < ex.getSets().size(); k++) {
                        ui.showMessage("         Set " + (k + 1) + " -> Reps: " + ex.getSets().get(k));
                    }
                }
            }
        }
    }

    public void showWorkoutsWIthIndices(ArrayList<Workout> list) {
        if (list.isEmpty()) {
            ui.showMessage("No workouts recorded for this selection!");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            Workout w = list.get(i);
            sb.append(i + 1).append(". ").append(w.getWorkoutName()).append(" - ").append(w.getWorkoutDateString());

            if (i < list.size() - 1) {
                sb.append('\n');
            }
        }
        ui.showMessage(sb.toString());
    }

    public void interactiveDeleteWorkout(String command, UI ui) throws IOException {
        ArrayList<Workout> targetList = workouts;

        if (command.contains("d/")) {
            String dateStr = extractAfter(command, "d/").trim();
            String[] dateTokens = dateStr.split("\\s+");
            String datePart = dateTokens[0];
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
            try {
                LocalDate date = LocalDate.parse(datePart, formatter);
                targetList = getWorkoutsByDate(date);
            } catch (Exception e) {
                ui.showError("Invalid date format! (Use d/DD/MM/YY)");
                return;
            }
        }

        if (targetList.isEmpty()) {
            ui.showMessage("No workouts found for the given date.");
            return;
        }

        showWorkoutsWIthIndices(targetList);
        String selection = ui.enterSelection();

        String[] tokens = selection.split("\\s+");
        ArrayList<Integer> indicesToDelete = new ArrayList<>();
        for (String token : tokens) {
            try {
                int index = Integer.parseInt(token) - 1;
                if (index >= 0 && index < targetList.size()) {
                    indicesToDelete.add(index);
                }
            } catch (NumberFormatException ignored) {
                ui.showError("An unexpected error occurred: " + ignored.getMessage());
            }
        }

        if (indicesToDelete.isEmpty()) {
            ui.showMessage("No valid indices entered. Nothing deleted.");
            return;
        }

        for (int i = indicesToDelete.size() - 1; i >= 0; i--) {
            Workout w = targetList.get(indicesToDelete.get(i));
            workouts.remove(w);
            fileHandler.saveMonthList(currentLoadedMonth, workouts);
            ui.showMessage("Delete: " + w.getWorkoutName());
        }
    }

    /**
     * Overrides the manual tags of a workout with a new single tag and clears all auto tags.
     * This effectively replaces any existing manual and automatic tags with the specified tag.
     *
     * @param workoutId the ID/index of the workout to update (1-based index assumed)
     * @param newTag    the new tag to set as the manual tag for the workout
     */
    public void overrideWorkoutTags(int workoutId, String newTag) {
        Workout workout = workouts.get(workoutId - 1);
        Set<String> newTagsSet = new LinkedHashSet<>();
        newTagsSet.add(newTag.toLowerCase().trim());
        workout.setManualTags(newTagsSet);
        workout.setAutoTags(new LinkedHashSet<>());
    }

    /**
     * Ends the current workout session by recording the end time and calculating duration.
     * <p>
     * Accepts user input in the format: /end_workout d/DD/MM/YY t/HHmm
     * If either date or time is missing, uses the current date or time as default.
     * Validates that the end date and time are not before the workout's start.
     * If the user input is invalid (earlier than start), prompts for re-entry until valid.
     *
     * @param ui          UI for reading user input in the retry loop
     * @param initialArgs Initial command arguments containing end date/time details
     */
    public void endWorkout(UI ui, String initialArgs) {
        if (currentWorkout == null) {
            ui.showMessage("No active workout.");
            return;
        }
        final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yy").withResolverStyle(ResolverStyle.SMART);
        final DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HHmm").withResolverStyle(ResolverStyle.SMART);

        String args = (initialArgs == null) ? "" : initialArgs.trim();

        // Extract raw tokens
        String dateStr = "";
        String timeStr = "";

        // Must have exactly one d/ and one t/
        if (countToken(args, "d/") != 1 || countToken(args, "t/") != 1) {
            ui.showMessage("Provide exactly one d/ and one t/ in this order: d/DATE t/TIME");
            return;
        }

        // Enforce order: d/ before t/
        int dIdx = args.indexOf("d/");
        int tIdx = args.indexOf("t/");
        if (dIdx == -1 || tIdx == -1 || tIdx < dIdx) {
            ui.showMessage("Order must be d/ then t/. Example: /end_workout d/29/10/25 t/1800");
            return;
        }

        // Reject any other boundary flags (only d/ and t/ allowed)
        java.util.regex.Matcher stray = BOUND_PREFIX.matcher(args);
        while (stray.find()) {
            char p = stray.group(2).charAt(0); // letter before '/'
            if (p != 'd' && p != 't') {
                ui.showMessage("Only d/ and t/ are allowed. Usage: /end_workout d/DD/MM/YY t/HHmm");
                return;
            }
        }

        // Extract date and time slices
        Slice dateSlice = extractSlice(args, dIdx);
        Slice timeSlice = extractSlice(args, tIdx);

        // If user typed spaces immediately after d/ or t/, treat as invalid format (reuse your generic msgs)
        if (!dateSlice.valueRaw.isEmpty() && Character.isWhitespace(dateSlice.valueRaw.charAt(0))) {
            ui.showMessage("[Error] Invalid date. Use d/DD/MM/YY (e.g., d/23/10/25).");
            ui.showMessage("Please enter: /end_workout d/DD/MM/YY t/HHmm");
            return;
        }
        if (!timeSlice.valueRaw.isEmpty() && Character.isWhitespace(timeSlice.valueRaw.charAt(0))) {
            ui.showMessage("[Error] Invalid time. Use t/HHmm (e.g., t/1905).");
            ui.showMessage("Please enter: /end_workout d/DD/MM/YY t/HHmm");
            return;
        }

        dateStr = dateSlice.valueTrimmed;
        timeStr = timeSlice.valueTrimmed;

        // No extra junk after t/
        if (!onlyWhitespaceAfter(args, timeSlice.endIndex)) {
            ui.showMessage("Unexpected text after time. Use exactly: /end_workout d/DD/MM/YY t/HHmm");
            return;
        }

        LocalDate date = null;
        LocalTime time = null;

        // Validate provided pieces first
        if (!dateStr.isEmpty()) {
            try {
                date = LocalDate.parse(dateStr, dateFmt);
            } catch (Exception ex) {
                ui.showMessage("[Error] Invalid date. Use d/DD/MM/YY (e.g., d/23/10/25).");
                ui.showMessage("Please enter: /end_workout d/DD/MM/YY t/HHmm");
                return;
            }
        }
        if (!timeStr.isEmpty()) {
            try {
                time = LocalTime.parse(timeStr, timeFmt);
            } catch (Exception ex) {
                ui.showMessage("[Error] Invalid time. Use t/HHmm (e.g., t/1905).");
                ui.showMessage("Please enter: /end_workout d/DD/MM/YY t/HHmm");
                return;
            }
        }

        // Prompt ONLY for missing pieces
        if (date == null) {
            String todayStr = LocalDate.now().format(dateFmt);
            ui.showMessage("Looks like you missed the date. Use current date (" + todayStr + ")? (Y/N)");
            if (ui.confirmationMessage()) {
                date = LocalDate.now();
            } else {
                ui.showMessage("Please enter: /end_workout d/DD/MM/YY t/HHmm");
                return;
            }
        }
        if (time == null) {
            String nowStr = LocalTime.now().format(timeFmt);
            ui.showMessage("Looks like you missed the time. Use current time (" + nowStr + ")? (Y/N)");
            if (ui.confirmationMessage()) {
                time = LocalTime.now();
            } else {
                ui.showMessage("Please enter: /end_workout d/DD/MM/YY t/HHmm");
                return;
            }
        }

        LocalDateTime endDateTime = LocalDateTime.of(date, time).truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime startTime = currentWorkout.getWorkoutStartDateTime().truncatedTo(ChronoUnit.MINUTES);

        if (!endDateTime.isAfter(startTime)) {
            ui.showMessage("End time must be after the start time of the workout!");
            ui.showMessage("Please enter: /end_workout d/DD/MM/YY t/HHmm");
            return;
        }

        currentWorkout.setWorkoutEndDateTime(endDateTime);
        int duration = currentWorkout.calculateDuration();
        currentWorkout.setDuration(duration);

        try {
            YearMonth monthToSave = YearMonth.from(startTime);
            fileHandler.saveMonthList(monthToSave, workouts);
            currentLoadedMonth = monthToSave;
        } catch (IOException ioe) {
            ui.showMessage("[Oops] Failed to save updated workout: " + ioe.getMessage());
        }

        ui.showMessage("Workout wrapped! Time to refuel!");
        ui.showMessage(String.format("Workout '%s' ended. Duration: %d minute(s).", currentWorkout.getWorkoutName(), duration));

        currentWorkout = null;
    }

    // Small holder for token extraction
    private static final class Slice {
        final String valueTrimmed;
        final int endIndex;       // end position in original string (exclusive)
        final String valueRaw;    // substring between token and next prefix/end (untrimmed)
        Slice(String valueTrimmed, String valueRaw, int endIndex) {
            this.valueTrimmed = valueTrimmed;
            this.valueRaw = valueRaw;
            this.endIndex = endIndex;
        }
    }

    // Count occurrences of a token like "n/" or "r/"
    private static int countToken(String s, String token) {
        int count = 0, idx = 0;
        while ((idx = s.indexOf(token, idx)) != -1) { count++; idx += token.length(); }
        return count;
    }

    // Extract the slice for a token (e.g., starting at index of 'n' in "n/..."),
    // capturing the raw substring and where it ends in the original string.
    private static Slice extractSlice(String s, int tokenStart) {
        int valueStart = tokenStart + 2;
        if (valueStart > s.length()) return new Slice("", "", valueStart);

        Matcher m = NEXT_PREFIX.matcher(s);
        int next = -1;
        while (m.find()) {
            int boundaryStart = m.start();
            if (boundaryStart > valueStart) {
                next = boundaryStart;
                break;
            }
        }

        String raw = (next == -1) ? s.substring(valueStart) : s.substring(valueStart, next);
        return new Slice(raw.trim(), raw, (next == -1 ? s.length() : next));
    }


    private static boolean isValidName(String name) {
        if (name == null) return false;
        String t = name.trim();
        if (t.isEmpty() || t.length() > MAX_EXERCISE_NAME_LEN) return false;
        return NAME_ALLOWED.matcher(t).matches();
    }

    private static Character findFirstIllegalNameChar(String name) {
        Matcher m = NAME_ILLEGAL_FINDER.matcher(name);
        return m.find() ? name.charAt(m.start()) : null;
    }

    // Safe parse reps: only digits, length<=4, range 1..MAX_REPS
    private static Integer parseRepsSafe(String repsStr) {
        String t = repsStr.trim();
        if (!REPS_TOKEN.matcher(t).matches()) return null;
        int val = Integer.parseInt(t);
        if (val < 1 || val > MAX_REPS) return null;
        return val;
    }

    private static boolean onlyWhitespaceAfter(String s, int endIndex) {
        for (int i = endIndex; i < s.length(); i++) if (!Character.isWhitespace(s.charAt(i))) return false;
        return true;
    }
}
