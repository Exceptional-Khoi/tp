package seedu.fitchasers.workouts;

import seedu.fitchasers.storage.FileHandler;
import seedu.fitchasers.exceptions.FileNonexistent;
import seedu.fitchasers.exceptions.InvalidArgumentInput;
import seedu.fitchasers.tagger.Modality;
import seedu.fitchasers.tagger.Tagger;
import seedu.fitchasers.ui.UI;

import java.io.IOException;
import java.time.YearMonth;
import java.time.format.ResolverStyle;
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

//@@author ZhongBaode

/**
 * Manages workout sessions for the FitChasers application.
 * <p>
 * Handles creation, deletion, and viewing of workouts,
 * as well as adding exercises and sets within each workout.
 */
public class WorkoutManager {
    private static final int MAX_EXERCISE_NAME_LEN = 32;
    private static final int MAX_REPS = 1000;
    private static final Pattern NAME_ALLOWED = Pattern.compile("[A-Za-z0-9 _-]+");
    private static final Pattern NAME_ILLEGAL_FINDER = Pattern.compile("[^A-Za-z0-9 _-]");
    private static final Pattern BOUND_PREFIX = Pattern.compile("(^|\\s)([A-Za-z])/");
    private static final Pattern NEXT_PREFIX = Pattern.compile("\\s+[A-Za-z]/");
    private static final Pattern REPS_TOKEN = Pattern.compile("^\\d{1,4}$");
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yy").withResolverStyle(ResolverStyle.SMART);
    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("HHmm").withResolverStyle(ResolverStyle.SMART);
    protected YearMonth creationDate;

    private ArrayList<Workout> workouts = new ArrayList<>();
    private Workout currentWorkout = null;
    private final UI ui = new UI();
    private final Tagger tagger;
    private LocalDateTime workoutDateTime;
    private String workoutName;
    private YearMonth currentLoadedMonth;
    private final Map<YearMonth, ArrayList<Workout>> workoutsByMonth;
    private final FileHandler fileHandler;
    private int nameIndex;
    private int afterNameIndex = 2;
    private LocalDate date = null;
    private LocalTime time = null;
    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yy")
            .withResolverStyle(ResolverStyle.SMART);
    private final DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HHmm")
            .withResolverStyle(ResolverStyle.SMART);

    public WorkoutManager(Tagger tagger, FileHandler fileHandler) throws IOException {
        this.tagger = tagger;
        this.fileHandler = fileHandler;
        this.workoutsByMonth = fileHandler.getArrayByMonth();
        this.currentLoadedMonth = YearMonth.now();
        this.creationDate = fileHandler.getCreationMonth();
    }

    public void initWorkouts() {
        for (Workout workout : this.workouts) {
            while (workout.getWorkoutEndDateTime() == null) {
                ui.showError("Looks like you forgot to end the previous workout, please enter it now!");
                ui.showMessage("[IMPORTANT] You cannot continue using the app unless you enter it ;) "+
                        "\n Tip: Enter '/end_workout' it will ask you if you want to use today's date" +
                        "\n Else: Enter '/end_workout d/<DD/MM/YY> t/<HHMM>' e.g. ew d/03/11/25 t/1200" );
                currentWorkout = workout;
                endWorkout(ui.readCommand());
            }
        }
    }

    public void setWorkouts(ArrayList<Workout> workouts) {
        this.workouts = workouts;
    }

    public void setWorkouts(ArrayList<Workout> workouts, YearMonth monthOfArrayList) {
        this.workouts = workouts;
        currentLoadedMonth = monthOfArrayList;
    }

    public YearMonth getCreationDate() {
        return creationDate;
    }
    /**
     * Adds a new workout session from the user's command input.
     * <p>
     * Parses the command to extract the workout name, date, and time, then creates
     * a new Workout object. If the workout belongs to a different month, the method
     * loads that month's data first. It also generates suggested tags and saves
     * the workout to file.
     * <p>
     * Displays messages to the user and handles invalid or missing input safely.
     *
     * @param command The full user command, for example "/create_workout n/PushDay d/20/10/25 t/1900".
     */
    public void addWorkout(String command) throws FileNonexistent, IOException {

        // ensure workouts is not null before adding
        if (workouts == null) {
            workouts = new ArrayList<>();
        }

        workoutName = "";
        try {
            if (command.contains("d/") || command.contains("t/")) {
                formatInputForWorkoutStrict(command);
            } else {
                formatInputForWorkout(command);
            }
        } catch (InvalidArgumentInput e) {
            return;
        }
        YearMonth monthOfWorkout = YearMonth.from(workoutDateTime);
        if (!currentLoadedMonth.equals(monthOfWorkout)) {
            // Check if workout month is before the month app was first started
            if (monthOfWorkout.isBefore(creationDate)) {
                ui.showMessage("FitChasers was first booted on "
                        + creationDate.getMonth().name().toLowerCase().substring(0, 1).toUpperCase()
                        + creationDate.getMonth().name().toLowerCase().substring(1)
                        + " of " + creationDate.getYear() + ".");
                ui.showMessage("Please start your fitness logging from then!");
                return; // stop creating workout
            }

            // Only load if valid
            setWorkouts(fileHandler.loadMonthList(monthOfWorkout), monthOfWorkout);
        }

        // Reject if the new start time falls inside any existing workout on the same day
        Workout conflict = findOverlappingWorkout(workoutDateTime);
        if (conflict != null) {
            LocalDateTime s = conflict.getWorkoutStartDateTime();
            LocalDateTime e = conflict.getWorkoutEndDateTime();
            String startStr = s.toLocalTime().format(TIME_FMT);
            String endStr = (e == null) ? "ongoing" : e.toLocalTime().format(TIME_FMT);
            ui.showMessage("[Error] Cannot create overlapping workout. "
                    + "Conflicts with \"" + conflict.getWorkoutName() + "\" (" + startStr + "–" + endStr + ").");
            return;
        }

        try {
            Workout newWorkout = new Workout(workoutName, workoutDateTime);

            // merge auto-tags if you have a tagger
            Set<String> suggestedTags = tagger.suggest(newWorkout);
            newWorkout.setAutoTags(suggestedTags);
            workouts.add(newWorkout);
            currentWorkout = newWorkout;
            ui.showMessage("New workout sesh incoming!");
            ui.showMessage("Tags generated for workout: " + (suggestedTags == null || suggestedTags.isEmpty()
                    ? "none"
                    : String.join(", ", suggestedTags)) + "\n"
                            + "Added workout: " + workoutName);
            fileHandler.saveMonthList(currentLoadedMonth,workouts);

        } catch (Exception e) {
            ui.showMessage("Something went wrong creating the workout. Please try again.");
        }
    }

    /**
     * Strict parser for /create_workout that enforces:
     * - exactly one n/, one d/, one t/
     * - order n/ then d/ then t/
     * - name character policy identical to addExercise
     * - no spaces immediately after d/ or t/
     * - no extra garbage after time
     * - no unsupported flags
     */
    private void formatInputForWorkoutStrict(String command) throws InvalidArgumentInput, IOException {
        assert workouts != null : "workouts list should be initialized";

        if (currentWorkout != null) {
            ui.showMessage("You currently have an active workout: '" + currentWorkout.getWorkoutName() + "'.");
            ui.showMessage("Please end the active workout first with: /end_workout d/DD/MM/YY t/HHmm");
            throw new InvalidArgumentInput("");
        }

        if (command == null || command.isBlank()) {
            ui.showMessage("Missing information. Use: /create_workout n/NAME d/DD/MM/YY t/HHmm");
            throw new InvalidArgumentInput("");
        }

        final String s = command.trim();

        // Must have exactly one n/, one d/, one t/
        if (countToken(s, "n/") != 1 || countToken(s, "d/") != 1 || countToken(s, "t/") != 1) {
            ui.showMessage("Please provide exactly one n/, one d/, and one t/ in this order: n/NAME d/DATE t/TIME");
            throw new InvalidArgumentInput("");
        }

        // Enforce order n/ ... d/ ... t/
        final int nIdx = s.indexOf("n/");
        final int dIdx = s.indexOf("d/");
        final int tIdx = s.indexOf("t/");
        if (nIdx < 0 || dIdx < 0 || tIdx < 0 || !(nIdx < dIdx && dIdx < tIdx)) {
            ui.showMessage("Order must be n/ then d/ then t/. Example: /create_workout n/Push Day d/20/10/25 t/1900");
            throw new InvalidArgumentInput("");
        }

        // ---- Extract and validate name (exactly like addExercise) ----
        Slice nameSlice = extractSlice(s, nIdx);
        String name = nameSlice.valueTrimmed;

        if (name.isEmpty()) {
            ui.showMessage("Workout name is missing after n/. Example: n/Leg Day");
            throw new InvalidArgumentInput("");
        }
        if (!isValidName(name)) {
            Character bad = findFirstIllegalNameChar(name);
            if (bad != null) {
                String shown = (bad == '\\') ? "\\\\" : String.valueOf(bad);
                ui.showMessage("'" + shown + "' is not allowed in the workout name.");
            } else {
                ui.showMessage("Name too long or invalid.");
            }
            ui.showMessage("Allowed characters: letters, digits, spaces, hyphen (-), underscore (_). Max 32 chars.");
            throw new InvalidArgumentInput("");
        }

        // ---- Extract and validate date ----
        Slice dateSlice = extractSlice(s, dIdx);

        if (!dateSlice.valueRaw.isEmpty() && Character.isWhitespace(dateSlice.valueRaw.charAt(0))) {
            ui.showMessage("Remove spaces between d/ and the date. Example: d/23/10/25 (not d/ 23/10/25)");
            throw new InvalidArgumentInput("");
        }

        LocalDate parsedDate;
        try {
            parsedDate = LocalDate.parse(dateSlice.valueTrimmed, DATE_FMT);
        } catch (Exception ex) {
            ui.showMessage("Invalid date. Use d/DD/MM/YY (e.g., d/23/10/25).");
            throw new InvalidArgumentInput("");
        }

        // ---- Extract and validate time ----
        Slice timeSlice = extractSlice(s, tIdx);

        if (!timeSlice.valueRaw.isEmpty() && Character.isWhitespace(timeSlice.valueRaw.charAt(0))) {
            ui.showMessage("Remove spaces between t/ and the time. Example: t/1905 (not t/ 1905)");
            throw new InvalidArgumentInput("");
        }

        LocalTime parsedTime;
        try {
            parsedTime = LocalTime.parse(timeSlice.valueTrimmed, TIME_FMT);
        } catch (Exception ex) {
            ui.showMessage("Invalid time. Use t/HHmm (e.g., t/1905).");
            throw new InvalidArgumentInput("");
        }

        // No extra junk after time
        if (!onlyWhitespaceAfter(s, timeSlice.endIndex)) {
            ui.showMessage("Unexpected text after time. Use exactly: /create_workout n/NAME d/DD/MM/YY t/HHmm");
            throw new InvalidArgumentInput("");
        }

        // Reject stray unsupported flags outside parsed regions
        Matcher stray = BOUND_PREFIX.matcher(s);
        while (stray.find()) {
            int pos = stray.start(2);
            char p = stray.group(2).charAt(0);

            boolean inName = pos >= nIdx && pos < nameSlice.endIndex;
            boolean inDate = pos >= dIdx && pos < dateSlice.endIndex;
            boolean inTime = pos >= tIdx && pos < timeSlice.endIndex;

            if (!inName && !inDate && !inTime) {
                if (p != 'n' && p != 'd' && p != 't') {
                    ui.showMessage("Unsupported flag \"" + p + "/\" found. Only n/, d/, and t/ are allowed.");
                    throw new InvalidArgumentInput("");
                }
            }
        }

        // Finalize fields
        this.workoutName = name;
        this.date = parsedDate;
        this.time = parsedTime;
        this.workoutDateTime = LocalDateTime.of(parsedDate, parsedTime);

        // Future/past confirmations + month file bootstrap (reuse your existing logic)
        checkPastFutureDate(parsedDate,parsedTime);

        // Duplicate date/time check (unchanged from your version)
        for (Workout w : workouts) {
            LocalDateTime existingStart = w.getWorkoutStartDateTime();
            if (existingStart == null) {
                continue;
            }
            if (existingStart.toLocalDate().equals(parsedDate)
                    && existingStart.toLocalTime().equals(parsedTime)) {
                ui.showMessage("A workout already exists at this date and time ("
                        + existingStart.toLocalDate().format(DATE_FMT) + " "
                        + existingStart.toLocalTime().format(TIME_FMT) + "). Continue anyway? (Y/N)");
                if (!ui.confirmationMessage()) {
                    ui.showMessage("Workout creation cancelled. Please pick a different time or date.");
                    throw new InvalidArgumentInput("");
                }
                break;
            }
        }
    }

    /**
     * Parses and validates the user's workout creation command.
     * <p>
     * Extracts the workout name, date, and time from the command string.
     * Prompts the user if date or time is missing, confirms if inputs are in the future,
     * and checks for duplicate workouts at the same date and time.
     * <p>
     * Updates internal fields with validated data and throws an exception if
     * the command format or values are invalid.
     *
     * @param command The full user command, e.g. "/create_workout n/PushDay d/20/10/25 t/1900".
     * @throws InvalidArgumentInput if the input format or values are invalid.
     */
    private void formatInputForWorkout(String command) throws InvalidArgumentInput, IOException {
        assert workouts != null : "workouts list should be initialized";
        if (currentWorkout != null) {
            ui.showMessage("You currently have an active workout: '"
                    + currentWorkout.getWorkoutName() + "'.");
            ui.showMessage("Please end the active workout first with: /end_workout d/DD/MM/YY t/HHmm");
            throw new InvalidArgumentInput("");
        }

        assert currentWorkout == null : "No active workout expected before creating a new one";

        if (command == null || !command.contains("n/")) {
            ui.showMessage("Invalid format. Use: /create_workout n/WorkoutName d/DD/MM/YY t/HHmm");
            throw new InvalidArgumentInput("");
        }

        nameIndex = command.indexOf("n/");
        afterNameIndex += nameIndex;

        // Find first marker after n/
        int dIndex = command.indexOf("d/", afterNameIndex);
        int tIndex = command.indexOf("t/", afterNameIndex);
        // pick the nearest positive marker after n/
        workoutName = extractWorkoutNameFromRaw(command, dIndex, tIndex);
        date = extractDateFromRaw(command, dIndex);
        time = extractTimeFromRaw(command, tIndex);
        promptIfDateOrTimeMissing();
        workoutDateTime = LocalDateTime.of(date, time);
        checkPastFutureDate(date, time);

        // Check if any existing workout already has the same date/time
        for (Workout w : workouts) {
            LocalDateTime existingStart = w.getWorkoutStartDateTime();
            if (existingStart != null) {
                LocalDate existingDate = existingStart.toLocalDate();
                LocalTime existingTime = existingStart.toLocalTime();

                if (existingDate.equals(date) && existingTime.equals(time)) {
                    ui.showMessage("A workout already exists at this date and time ("
                            + existingDate.format(dateFmt) + " " + existingTime.format(timeFmt) + "). " +
                            "Continue anyway? (Y/N)");
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

    public String extractWorkoutNameFromRaw(String command, int dIndex, int tIndex) throws InvalidArgumentInput {
        String workoutName;
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
        return workoutName;
    }

    public LocalTime extractTimeFromRaw(String command, int tIndex) throws InvalidArgumentInput {
        String timeStr = "";
        LocalTime time = null;
        if (tIndex != -1) {
            String tail = command.substring(tIndex + 2).trim();
            String[] toks = tail.split("\\s+");
            if (toks.length > 0) {
                timeStr = toks[0];
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
        return time;
    }

    public LocalDate extractDateFromRaw(String command, int dIndex) throws InvalidArgumentInput {
        String dateStr = "";
        LocalDate date = null;
        if (dIndex != -1) {
            String tail = command.substring(dIndex + 2).trim();
            String[] token = tail.split("\\s+");
            if (token.length > 0) {
                dateStr = token[0];
            }
        }

        if (!dateStr.isEmpty()) {
            try {
                date = LocalDate.parse(dateStr, DATE_FMT);
            } catch (Exception ex) {
                ui.showMessage("Invalid date. Use d/DD/MM/YY (e.g., d/23/10/25).");
                throw new InvalidArgumentInput("");
            }
        }
        return date;
    }

    private void promptIfDateOrTimeMissing() throws InvalidArgumentInput {
        if (date == null) {
            String todayStr = LocalDate.now().format(DATE_FMT);
            ui.showMessage("Looks like you missed the date. Use current date (" + todayStr + ")? (Y/N)");
            if (ui.confirmationMessage()) {
                date = LocalDate.now();
            } else {
                ui.showMessage("Please provide a date in format d/DD/MM/YY.");
                throw new InvalidArgumentInput("");
            }
        }

        if (time == null) {
            String nowStr = LocalTime.now().format(TIME_FMT);
            ui.showMessage("Looks like you missed the time. Use current time (" + nowStr + ")? (Y/N)");
            if (ui.confirmationMessage()) {
                time = LocalTime.now();
            } else {
                ui.showMessage("Please provide a time in format t/HHmm.");
                throw new InvalidArgumentInput("");
            }
        }
    }

    private void checkPastFutureDate(LocalDate date, LocalTime time)
            throws InvalidArgumentInput, IOException {
        if (date.isAfter(LocalDate.now())) {
            ui.showMessage("The date you entered (" + date.format(DATE_FMT)
                    + ") is in the future. Are you sure? (Y/N)");
            if (!ui.confirmationMessage()) {
                ui.showMessage("Please re-enter the correct date.");
                throw new InvalidArgumentInput("");
            }
            if (!fileHandler.checkFileExists(YearMonth.from(date))) {
                fileHandler.saveMonthList(YearMonth.from(date), new ArrayList<>());
            }
        }

        if (date.isEqual(LocalDate.now()) && time.isAfter(LocalTime.now())) {
            ui.showMessage("The time you entered (" + time.format(TIME_FMT)
                    + ") is in the future. Are you sure? (Y/N)");
            if (!ui.confirmationMessage()) {
                ui.showMessage("Please re-enter the correct time.");
                throw new InvalidArgumentInput("");
            }
        }
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
     * Adds an exercise to the current workout.
     * <p>
     * Expected format: /add_exercise n/NAME r/REPS
     *
     * @param args the user command arguments
     */
    public void addExercise(String args) throws IOException {
        if (currentWorkout == null) {
            ui.showMessage("No active workout. Use /create_workout first.");
            return;
        }

        if (args == null || args.trim().isEmpty()) {
            ui.showMessage("Missing information. Use: /add_exercise n/NAME r/REPS (e.g., /add_exercise n/PushUp r/12)");
            return;
        }

        String s = args.trim();

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
        if (!isValidName(name)) {
            Character bad = findFirstIllegalNameChar(name);
            if (bad != null) {
                String shown = (bad == '\\') ? "\\\\" : String.valueOf(bad);
                ui.showMessage("'" + shown + "' is not allowed in the exercise name.");
            } else {
                ui.showMessage("Name too long or invalid.");
            }
            ui.showMessage("Allowed characters: letters, digits, spaces, hyphen (-), underscore (_). Max 32 chars.");
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
        fileHandler.saveMonthList(currentLoadedMonth, workouts);
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
     * Overrides the manual tags of a workout with a new single tag and clears all auto tags.
     * This effectively replaces any existing manual and automatic tags with the specified tag.
     *
     * @param newTag the new tag to set as the manual tag for the workout
     */
    public void overrideWorkoutTags(Workout workout, String newTag) {
        Set<String> newTagsSet = new LinkedHashSet<>();
        newTagsSet.add(newTag.toLowerCase().trim());
        workout.setManualTags(newTagsSet);
        workout.setAutoTags(new LinkedHashSet<>());
    }

    public boolean hasConflictingModality(Workout w, String mod) {
        Set<String> tags = w.getAllTags();
        for (String tag : tags) {
            if (!tag.equalsIgnoreCase(mod) && isModalityTag(tag)) {
                return true;
            }
        }
        return false;
    }

    public Set<String> checkForOverriddenTags(Workout w) {
        return w.getConflictingTags();
    }

    private boolean isModalityTag(String tag) {
        try {
            Modality.valueOf(tag.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public String getConflictingModality(Workout w) {
        Set<String> autoTags = w.getAutoTags();
        if (autoTags.contains("cardio")) {
            return "cardio";
        }
        if (autoTags.contains("strength")) {
            return "strength";
        }
        return null;
    }

    /**
     * Ends the current workout session by recording the end time and calculating duration.
     * Usage: /end_workout d/DD/MM/YY t/HHmm
     * - Allows at most one d/ and one t/ (order: d/ then t/ if both present)
     * - Prompts for missing date/time (defaults to now with confirmation)
     * - Validates end > start
     * - Rejects if the end time would overlap another workout that starts on the same day:
     * otherStart in [current.start, proposedEnd)
     */
    public void endWorkout(String initialArgs) {
        if (currentWorkout == null) {
            ui.showMessage("No active workout.");
            return;
        }

        final String usage = "Please enter: /end_workout d/DD/MM/YY t/HHmm";
        final String args = (initialArgs == null) ? "" : initialArgs.trim();

        // allow 0 or 1 of each; reject duplicates
        int dCount = countToken(args, "d/");
        int tCount = countToken(args, "t/");
        if (dCount > 1 || tCount > 1) {
            ui.showMessage("Too many date/time flags. Use at most one d/ and one t/.");
            ui.showMessage("Usage: /end_workout d/DD/MM/YY t/HHmm");
            return;
        }

        int dIdx = args.indexOf("d/");
        int tIdx = args.indexOf("t/");
        // enforce order only if both provided
        if (dIdx != -1 && tIdx != -1 && tIdx < dIdx) {
            ui.showMessage("Order must be d/ then t/. Example: /end_workout d/29/10/25 t/1800");
            return;
        }

        // reject any other flags like n/, r/, x/, etc.
        Matcher stray = BOUND_PREFIX.matcher(args);
        while (stray.find()) {
            char p = stray.group(2).charAt(0);
            if (p != 'd' && p != 't') {
                ui.showMessage("Only d/ and t/ are allowed. Usage: /end_workout d/DD/MM/YY t/HHmm");
                return;
            }
        }

        // extract slices if present
        Slice dateSlice = null;
        Slice timeSlice = null;
        String dateStr = "";
        String timeStr = "";

        if (dIdx != -1) {
            dateSlice = extractSlice(args, dIdx);
            if (!dateSlice.valueRaw.isEmpty() && Character.isWhitespace(dateSlice.valueRaw.charAt(0))) {
                ui.showMessage("[Error] Invalid date. Use d/DD/MM/YY (e.g., d/23/10/25)." +
                        " \n Tip: Single digit date needs a 0 infront! e.g 02/11/26");
                ui.showMessage(usage);
                return;
            }
            dateStr = dateSlice.valueTrimmed;
        }

        if (tIdx != -1) {
            timeSlice = extractSlice(args, tIdx);
            if (!timeSlice.valueRaw.isEmpty() && Character.isWhitespace(timeSlice.valueRaw.charAt(0))) {
                ui.showMessage("[Error] Invalid time. Use t/HHmm (e.g., t/1905).");
                ui.showMessage(usage);
                return;
            }
            timeStr = timeSlice.valueTrimmed;
        }

        // trailing junk after the last provided token
        int lastEnd = (timeSlice != null) ? timeSlice.endIndex : (dateSlice != null ? dateSlice.endIndex : -1);
        if (lastEnd != -1 && !onlyWhitespaceAfter(args, lastEnd)) {
            ui.showMessage("Unexpected text after time/date. Use exactly: /end_workout d/DD/MM/YY t/HHmm");
            return;
        }

        // parse provided parts
        LocalDate date = null;
        LocalTime time = null;

        if (!dateStr.isEmpty()) {
            try {
                date = LocalDate.parse(dateStr, DATE_FMT);
            } catch (Exception ex) {
                ui.showMessage("[Error] Invalid date. Use d/DD/MM/YY (e.g., d/23/10/25)." +
                        " \nTip: Single digit date needs a 0 infront! e.g 02/11/26");
                ui.showMessage(usage);
                return;
            }
        }
        if (!timeStr.isEmpty()) {
            try {
                time = LocalTime.parse(timeStr, TIME_FMT);
            } catch (Exception ex) {
                ui.showMessage("[Error] Invalid time. Use t/HHmm (e.g., t/1905).");
                ui.showMessage(usage);
                return;
            }
        }

        // prompt ONLY for missing pieces (same UX as your create flow)
        if (date == null) {
            String todayStr = LocalDate.now().format(DATE_FMT);
            ui.showMessage("Looks like you missed the date. Use current date (" + todayStr + ")? (Y/N)");
            if (ui.confirmationMessage()) {
                date = LocalDate.now();
            } else {
                ui.showMessage(usage);
                return;
            }
        }
        if (time == null) {
            String nowStr = LocalTime.now().format(TIME_FMT);
            ui.showMessage("Looks like you missed the time. Use current time (" + nowStr + ")? (Y/N)");
            if (ui.confirmationMessage()) {
                time = LocalTime.now();
            } else {
                ui.showMessage(usage);
                return;
            }
        }

        // compute & validate chronology
        LocalDateTime proposedEnd = LocalDateTime.of(date, time).truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime startTime = currentWorkout.getWorkoutStartDateTime().truncatedTo(ChronoUnit.MINUTES);

        if (!proposedEnd.isAfter(startTime)) {
            ui.showMessage("End time must be after the start time of the workout!");
            ui.showMessage(usage);
            return;
        }

        // --- Overlap guard against later workouts on the SAME DAY ---
        for (Workout w : workouts) {
            if (w == currentWorkout) {
                continue;
            }
            LocalDateTime otherStart = w.getWorkoutStartDateTime();
            if (otherStart == null) {
                continue;
            }
            if (!otherStart.toLocalDate().equals(startTime.toLocalDate())) {
                continue;
            }

            // conflict if another workout starts at/after our start and before our proposed end
            if (!otherStart.isBefore(startTime) && otherStart.isBefore(proposedEnd)) {
                String startStr = otherStart.toLocalTime().format(TIME_FMT);
                LocalDateTime otherEndDT = w.getWorkoutEndDateTime();
                String endStr = (otherEndDT == null)
                        ? "ongoing"
                        : otherEndDT.toLocalTime().format(TIME_FMT);

                ui.showMessage("[Error] End time overlaps another workout: \""
                        + w.getWorkoutName() + "\" (" + startStr + "–" + endStr + ").");
                ui.showMessage("Please enter a valid date and time");
                return;
            }
        }

        // persist
        currentWorkout.setWorkoutEndDateTime(proposedEnd);
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
        ui.showMessage(String.format("Workout '%s' ended. Duration: %d minute(s).",
                currentWorkout.getWorkoutName(), duration));

        currentWorkout = null;
    }

    public YearMonth getCurrentLoadedMonth() {
        return currentLoadedMonth;
    }

    public void deleteParser(String argumentStr) {
        try {
            new DeleteWorkout(ui, fileHandler, this).execute(argumentStr);
        } catch (InvalidArgumentInput | FileNonexistent e) {
            ui.showError(e.getMessage());
        } catch (IOException e) {
            ui.showError("Failed to save changes: " + e.getMessage());
        }
    }

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
        int count = 0;
        int idx = 0;
        while ((idx = s.indexOf(token, idx)) != -1) {
            count++;
            idx += token.length();
        }
        return count;
    }

    // Extract the slice for a token (e.g., starting at index of 'n' in "n/..."),
    // capturing the raw substring and where it ends in the original string.
    private static Slice extractSlice(String s, int tokenStart) {
        int valueStart = tokenStart + 2; // skip "x/"
        if (valueStart > s.length()) {
            return new Slice("", "", valueStart);
        }

        Matcher m = NEXT_PREFIX.matcher(s);
        int next = -1;
        while (m.find()) {
            int boundaryStart = m.start(); // includes the whitespace before the next token
            if (boundaryStart > valueStart) { // ensure it's after our value actually begins
                next = boundaryStart;
                break;
            }
        }

        String raw = (next == -1) ? s.substring(valueStart) : s.substring(valueStart, next);
        return new Slice(raw.trim(), raw, (next == -1 ? s.length() : next));
    }


    private static boolean isValidName(String name) {
        if (name == null) {
            return false;
        }
        String t = name.trim();
        if (t.isEmpty() || t.length() > MAX_EXERCISE_NAME_LEN) {
            return false;
        }
        return NAME_ALLOWED.matcher(t).matches();
    }

    private static Character findFirstIllegalNameChar(String name) {
        Matcher m = NAME_ILLEGAL_FINDER.matcher(name);
        return m.find() ? name.charAt(m.start()) : null;
    }

    // Safe parse reps: only digits, length<=4, range 1..MAX_REPS
    private static Integer parseRepsSafe(String repsStr) {
        String t = repsStr.trim();
        if (!REPS_TOKEN.matcher(t).matches()) {
            return null;
        }
        int val = Integer.parseInt(t);
        if (val < 1 || val > MAX_REPS) {
            return null;
        }
        return val;
    }

    private static boolean onlyWhitespaceAfter(String s, int endIndex) {
        for (int i = endIndex; i < s.length(); i++) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Finds an existing workout that the given start time would overlap with.
     * Overlap rule: same calendar day AND existingStart <= newStart < existingEnd.
     * If an existing workout has no end time, treat it as ongoing (i.e., overlap if newStart >= existingStart).
     *
     * @param newStart proposed start time for the new workout
     * @return the conflicting workout, or null if none
     */
    private Workout findOverlappingWorkout(LocalDateTime newStart) {
        for (Workout w : workouts) {
            LocalDateTime s = w.getWorkoutStartDateTime();
            if (s == null) {
                continue;
            }
            if (!s.toLocalDate().equals(newStart.toLocalDate())) {
                continue; // only compare within the same day
            }

            LocalDateTime e = w.getWorkoutEndDateTime();
            if (e == null) {
                // treat as ongoing session from s onwards
                if (!newStart.isBefore(s)) {
                    return w;
                }
            } else {
                // overlap if s <= newStart < e
                boolean startsDuring = !newStart.isBefore(s) && newStart.isBefore(e);
                if (startsDuring) {
                    return w;
                }
            }
        }
        return null;
    }

}
