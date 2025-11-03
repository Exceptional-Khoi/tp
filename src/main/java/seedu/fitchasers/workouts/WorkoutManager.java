package seedu.fitchasers.workouts;

import seedu.fitchasers.storage.FileHandler;
import seedu.fitchasers.exceptions.FileNonexistent;
import seedu.fitchasers.exceptions.InvalidArgumentInput;
import seedu.fitchasers.tagger.Modality;
import seedu.fitchasers.tagger.Tagger;
import seedu.fitchasers.ui.Parser;
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

import static seedu.fitchasers.FitChasers.parser;

//@@ZhongBaode

/**
 * Manages workout sessions for the FitChasers application.
 * <p>
 * Handles creation, deletion, and viewing of workouts,
 * as well as adding exercises and sets within each workout.
 */
public class WorkoutManager {
//    private static final int MAX_EXERCISE_NAME_LEN = 32;
//    private static final Pattern NAME_ALLOWED = Pattern.compile("[A-Za-z0-9 _-]+");
//    private static final Pattern NAME_ILLEGAL_FINDER = Pattern.compile("[^A-Za-z0-9 _-]");
//    private static final Pattern BOUND_PREFIX = Pattern.compile("(^|\\s)([A-Za-z])/");
//    private static final Pattern NEXT_PREFIX = Pattern.compile("\\s+[A-Za-z]/");
//    private int nameIndex;
//    private int afterNameIndex = 2;
    private LocalDate date = null;
    private LocalTime time = null;
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yy").withResolverStyle(ResolverStyle.SMART);
    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("HHmm").withResolverStyle(ResolverStyle.SMART);


    private ArrayList<Workout> workouts = new ArrayList<>();
    private Workout currentWorkout = null;
    private final UI ui = new UI();
    private final Tagger tagger;
    private LocalDateTime workoutDateTime;
    private String workoutName;
    private YearMonth currentLoadedMonth;
    private final Map<YearMonth, ArrayList<Workout>> workoutsByMonth;
    private final FileHandler fileHandler;
    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yy")
            .withResolverStyle(ResolverStyle.SMART);
    private final DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HHmm")
            .withResolverStyle(ResolverStyle.SMART);

    public WorkoutManager(Tagger tagger, FileHandler fileHandler) {
        this.tagger = tagger;
        this.fileHandler = fileHandler;
        this.workoutsByMonth = fileHandler.getArrayByMonth();
        this.currentLoadedMonth = YearMonth.now();
    }

    public void initWorkouts() {
        for (Workout workout : this.workouts) {
            while (workout.getWorkoutEndDateTime() == null) {
                ui.showMessage("Looks like you forgot to end the previous workout, please enter it now!");
                currentWorkout = workout;
                endWorkout(parser.readCommand());
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
        if (workouts == null) {
            workouts = new ArrayList<>();
        }

        Parser.CreateWorkoutArgs parsed;
        try {
            // Let the command parser decide strict vs flexible automatically
            // (it enforces strict if d/ or t/ appears).
            Parser.ParseOutcome out = parser.parse(command);
            if (out.type != Parser.CommandType.CREATE_WORKOUT) {
                ui.showMessage("Invalid command. Use: /create_workout n/NAME d/DD/MM/YY t/HHmm");
                return;
            }
            parsed = (Parser.CreateWorkoutArgs) out.payload;
        } catch (InvalidArgumentInput e) {
            ui.showMessage(e.getMessage());
            return;
        }

        // You had a rule: disallow creating while another workout is active
        if (currentWorkout != null) {
            ui.showMessage("You currently have an active workout: '" + currentWorkout.getWorkoutName() + "'.");
            ui.showMessage("Please end the active workout first with: /end_workout d/DD/MM/YY t/HHmm");
            return;
        }

        // Fill defaults only if FLEX path omitted them (date/time could be null in FLEX)
        LocalDate date = (parsed.date != null) ? parsed.date : LocalDate.now();
        LocalTime time = (parsed.time != null) ? parsed.time : LocalTime.now();

        // Optional: preserve your interactive confirmations for missing pieces
        if (!parsed.strict) {
            if (parsed.date == null) {
                String todayStr = LocalDate.now().format(DATE_FMT);
                ui.showMessage("Looks like you missed the date. Use current date (" + todayStr + ")? (Y/N)");
                Boolean ok = parser.confirmationMessage();
                if (ok == null) return;       // only "Action cancelled." is shown
                if (!ok) return;
            }
            if (parsed.time == null) {
                String nowStr = LocalTime.now().format(TIME_FMT);
                ui.showMessage("Looks like you missed the time. Use current time (" + nowStr + ")? (Y/N)");
                Boolean ok = parser.confirmationMessage();
                if (ok == null) return;
                if (!ok) return;
            }
        }

        this.workoutName = parsed.name;
        this.workoutDateTime = LocalDateTime.of(date, time);

        // Handle month data loading like before
        YearMonth monthOfWorkout = YearMonth.from(workoutDateTime);
        if (!currentLoadedMonth.equals(monthOfWorkout)) {
            // Check if workout month is before the month app was first started
            if (monthOfWorkout.isBefore(currentLoadedMonth)) {
                ui.showMessage("FitChasers was first booted on "
                        + currentLoadedMonth.getMonth().name().substring(0,1).toUpperCase()
                        + currentLoadedMonth.getMonth().name().substring(1).toLowerCase()
                        + " of " + currentLoadedMonth.getYear() + ".");
                ui.showMessage("Please start your fitness logging from then!");
                return;
            }
            setWorkouts(fileHandler.loadMonthList(monthOfWorkout), monthOfWorkout);
        }

        // Duplicate exact start → ask to continue (same UX)
        for (Workout w : workouts) {
            LocalDateTime sdt = w.getWorkoutStartDateTime();
            if (sdt != null && sdt.toLocalDate().equals(date) && sdt.toLocalTime().equals(time)) {
                ui.showMessage("A workout already exists at this date and time ("
                        + sdt.toLocalDate().format(DATE_FMT) + " " + sdt.toLocalTime().format(TIME_FMT) + "). Continue anyway? (Y/N)");
                Boolean okDup = parser.confirmationMessage();
                if (okDup == null) return;
                if (!okDup) {
                    ui.showMessage("Workout creation cancelled. Please pick a different time or date.");
                    return;
                }
                break;
            }
        }

        // Future checks (same as your previous behavior)
        try {
            checkPastFutureDate(date, time);
        } catch (InvalidArgumentInput e) {
            ui.showMessage(e.getMessage());
            return;
        }

        // Overlap guard
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

        // Create, tag, persist
        try {
            Workout newWorkout = new Workout(workoutName, workoutDateTime);
            Set<String> suggestedTags = tagger.suggest(newWorkout);
            newWorkout.setAutoTags(suggestedTags);
            workouts.add(newWorkout);
            currentWorkout = newWorkout;

            ui.showMessage("New workout sesh incoming!");
            ui.showMessage("Tags generated for workout: " + ((suggestedTags == null || suggestedTags.isEmpty())
                    ? "none" : String.join(", ", suggestedTags)) + "\n" + "Added workout: " + workoutName);

            fileHandler.saveMonthList(currentLoadedMonth, workouts);
        } catch (Exception e) {
            ui.showMessage("Something went wrong creating the workout. Please try again.");
        }
    }

//    /**
//     * Strict parser for /create_workout that enforces:
//     * - exactly one n/, one d/, one t/
//     * - order n/ then d/ then t/
//     * - name character policy identical to addExercise
//     * - no spaces immediately after d/ or t/
//     * - no extra garbage after time
//     * - no unsupported flags
//     */
//    private void formatInputForWorkoutStrict(String command) throws InvalidArgumentInput, IOException {
//        assert workouts != null : "workouts list should be initialized";
//
//        if (currentWorkout != null) {
//            ui.showMessage("You currently have an active workout: '" + currentWorkout.getWorkoutName() + "'.");
//            ui.showMessage("Please end the active workout first with: /end_workout d/DD/MM/YY t/HHmm");
//            throw new InvalidArgumentInput("");
//        }
//
//        if (command == null || command.isBlank()) {
//            ui.showMessage("Missing information. Use: /create_workout n/NAME d/DD/MM/YY t/HHmm");
//            throw new InvalidArgumentInput("");
//        }
//
//        final String s = command.trim();
//
//        // Must have exactly one n/, one d/, one t/
//        if (countToken(s, "n/") != 1 || countToken(s, "d/") != 1 || countToken(s, "t/") != 1) {
//            ui.showMessage("Please provide exactly one n/, one d/, and one t/ in this order: n/NAME d/DATE t/TIME");
//            throw new InvalidArgumentInput("");
//        }
//
//        // Enforce order n/ ... d/ ... t/
//        final int nIdx = s.indexOf("n/");
//        final int dIdx = s.indexOf("d/");
//        final int tIdx = s.indexOf("t/");
//        if (nIdx < 0 || dIdx < 0 || tIdx < 0 || !(nIdx < dIdx && dIdx < tIdx)) {
//            ui.showMessage("Order must be n/ then d/ then t/. Example: /create_workout n/Push Day d/20/10/25 t/1900");
//            throw new InvalidArgumentInput("");
//        }
//
//        // ---- Extract and validate name (exactly like addExercise) ----
//        Slice nameSlice = extractSlice(s, nIdx);
//        String name = nameSlice.valueTrimmed;
//
//        if (name.isEmpty()) {
//            ui.showMessage("Workout name is missing after n/. Example: n/Leg Day");
//            throw new InvalidArgumentInput("");
//        }
//        if (!isValidName(name)) {
//            Character bad = findFirstIllegalNameChar(name);
//            if (bad != null) {
//                String shown = (bad == '\\') ? "\\\\" : String.valueOf(bad);
//                ui.showMessage("'" + shown + "' is not allowed in the workout name.");
//            } else {
//                ui.showMessage("Name too long or invalid.");
//            }
//            ui.showMessage("Allowed characters: letters, digits, spaces, hyphen (-), underscore (_). Max 32 chars.");
//            throw new InvalidArgumentInput("");
//        }
//
//        // ---- Extract and validate date ----
//        Slice dateSlice = extractSlice(s, dIdx);
//
//        if (!dateSlice.valueRaw.isEmpty() && Character.isWhitespace(dateSlice.valueRaw.charAt(0))) {
//            ui.showMessage("Remove spaces between d/ and the date. Example: d/23/10/25 (not d/ 23/10/25)");
//            throw new InvalidArgumentInput("");
//        }
//
//        LocalDate parsedDate;
//        try {
//            parsedDate = LocalDate.parse(dateSlice.valueTrimmed, DATE_FMT);
//        } catch (Exception ex) {
//            ui.showMessage("Invalid date. Use d/DD/MM/YY (e.g., d/23/10/25).");
//            throw new InvalidArgumentInput("");
//        }
//
//        // ---- Extract and validate time ----
//        Slice timeSlice = extractSlice(s, tIdx);
//
//        if (!timeSlice.valueRaw.isEmpty() && Character.isWhitespace(timeSlice.valueRaw.charAt(0))) {
//            ui.showMessage("Remove spaces between t/ and the time. Example: t/1905 (not t/ 1905)");
//            throw new InvalidArgumentInput("");
//        }
//
//        LocalTime parsedTime;
//        try {
//            parsedTime = LocalTime.parse(timeSlice.valueTrimmed, TIME_FMT);
//        } catch (Exception ex) {
//            ui.showMessage("Invalid time. Use t/HHmm (e.g., t/1905).");
//            throw new InvalidArgumentInput("");
//        }
//
//        // No extra junk after time
//        if (!onlyWhitespaceAfter(s, timeSlice.endIndex)) {
//            ui.showMessage("Unexpected text after time. Use exactly: /create_workout n/NAME d/DD/MM/YY t/HHmm");
//            throw new InvalidArgumentInput("");
//        }
//
//        // Reject stray unsupported flags outside parsed regions
//        Matcher stray = BOUND_PREFIX.matcher(s);
//        while (stray.find()) {
//            int pos = stray.start(2);
//            char p = stray.group(2).charAt(0);
//
//            boolean inName = pos >= nIdx && pos < nameSlice.endIndex;
//            boolean inDate = pos >= dIdx && pos < dateSlice.endIndex;
//            boolean inTime = pos >= tIdx && pos < timeSlice.endIndex;
//
//            if (!inName && !inDate && !inTime) {
//                if (p != 'n' && p != 'd' && p != 't') {
//                    ui.showMessage("Unsupported flag \"" + p + "/\" found. Only n/, d/, and t/ are allowed.");
//                    throw new InvalidArgumentInput("");
//                }
//            }
//        }
//
//        // Finalize fields
//        this.workoutName = name;
//        this.date = parsedDate;
//        this.time = parsedTime;
//        this.workoutDateTime = LocalDateTime.of(parsedDate, parsedTime);
//
//        // Future/past confirmations + month file bootstrap (reuse your existing logic)
//        checkPastFutureDate(parsedDate,parsedTime);
//
//        // Duplicate date/time check (unchanged from your version)
//        for (Workout w : workouts) {
//            LocalDateTime existingStart = w.getWorkoutStartDateTime();
//            if (existingStart == null) {
//                continue;
//            }
//            if (existingStart.toLocalDate().equals(parsedDate)
//                    && existingStart.toLocalTime().equals(parsedTime)) {
//                ui.showMessage("A workout already exists at this date and time ("
//                        + existingStart.toLocalDate().format(DATE_FMT) + " "
//                        + existingStart.toLocalTime().format(TIME_FMT) + "). Continue anyway? (Y/N)");
//                if (!parser.confirmationMessage()) {
//                    ui.showMessage("Workout creation cancelled. Please pick a different time or date.");
//                    throw new InvalidArgumentInput("");
//                }
//                break;
//            }
//        }
//    }

//    /**
//     * Parses and validates the user's workout creation command.
//     * <p>
//     * Extracts the workout name, date, and time from the command string.
//     * Prompts the user if date or time is missing, confirms if inputs are in the future,
//     * and checks for duplicate workouts at the same date and time.
//     * <p>
//     * Updates internal fields with validated data and throws an exception if
//     * the command format or values are invalid.
//     *
//     * @param command The full user command, e.g. "/create_workout n/PushDay d/20/10/25 t/1900".
//     * @throws InvalidArgumentInput if the input format or values are invalid.
//     */
//    private void formatInputForWorkout(String command) throws InvalidArgumentInput, IOException {
//        assert workouts != null : "workouts list should be initialized";
//        if (currentWorkout != null) {
//            ui.showMessage("You currently have an active workout: '"
//                    + currentWorkout.getWorkoutName() + "'.");
//            ui.showMessage("Please end the active workout first with: /end_workout d/DD/MM/YY t/HHmm");
//            throw new InvalidArgumentInput("");
//        }
//
//        assert currentWorkout == null : "No active workout expected before creating a new one";
//
//        if (command == null || !command.contains("n/")) {
//            ui.showMessage("Invalid format. Use: /create_workout n/WorkoutName d/DD/MM/YY t/HHmm");
//            throw new InvalidArgumentInput("");
//        }
//
//        nameIndex = command.indexOf("n/");
//        afterNameIndex += nameIndex;
//
//        // Find first marker after n/
//        int dIndex = command.indexOf("d/", afterNameIndex);
//        int tIndex = command.indexOf("t/", afterNameIndex);
//        // pick the nearest positive marker after n/
//        workoutName = extractWorkoutNameFromRaw(command, dIndex, tIndex);
//        date = extractDateFromRaw(command, dIndex);
//        time = extractTimeFromRaw(command, tIndex);
//        promptIfDateOrTimeMissing();
//        workoutDateTime = LocalDateTime.of(date, time);
//        checkPastFutureDate(date, time);
//
//        // Check if any existing workout already has the same date/time
//        for (Workout w : workouts) {
//            LocalDateTime existingStart = w.getWorkoutStartDateTime();
//            if (existingStart != null) {
//                LocalDate existingDate = existingStart.toLocalDate();
//                LocalTime existingTime = existingStart.toLocalTime();
//
//                if (existingDate.equals(date) && existingTime.equals(time)) {
//                    ui.showMessage("A workout already exists at this date and time ("
//                            + existingDate.format(dateFmt) + " " + existingTime.format(timeFmt) + "). " +
//                            "Continue anyway? (Y/N)");
//                    if (!parser.confirmationMessage()) {
//                        ui.showMessage("Workout creation cancelled. Please pick a different time or date.");
//                        throw new InvalidArgumentInput("");
//                    }
//                    break;
//                }
//            }
//        }
//        workoutDateTime = LocalDateTime.of(date, time);
//    }

//    public String extractWorkoutNameFromRaw(String command, int dIndex, int tIndex) throws InvalidArgumentInput {
//        String workoutName;
//        int nextMarker = -1;
//        if (dIndex != -1 && tIndex != -1) {
//            nextMarker = Math.min(dIndex, tIndex);
//        } else if (dIndex != -1) {
//            nextMarker = dIndex;
//        } else if (tIndex != -1) {
//            nextMarker = tIndex;
//        }
//
//        if (nextMarker != -1) {
//            workoutName = command.substring(afterNameIndex, nextMarker).trim();
//        } else {
//            workoutName = command.substring(afterNameIndex).trim();
//        }
//
//        if (workoutName.isEmpty()) {
//            ui.showMessage("Workout name cannot be empty. Use: /create_workout n/WorkoutName d/DD/MM/YY t/HHmm");
//            throw new InvalidArgumentInput("");
//        }
//        return workoutName;
//    }
//
//    public LocalTime extractTimeFromRaw(String command, int tIndex) throws InvalidArgumentInput {
//        String timeStr = "";
//        LocalTime time = null;
//        if (tIndex != -1) {
//            String tail = command.substring(tIndex + 2).trim();
//            String[] toks = tail.split("\\s+");
//            if (toks.length > 0) {
//                timeStr = toks[0];
//            }
//        }
//        if (!timeStr.isEmpty()) {
//            try {
//                time = LocalTime.parse(timeStr, timeFmt);
//            } catch (Exception ex) {
//                ui.showMessage("Invalid time. Use t/HHmm (e.g., t/1905).");
//                throw new InvalidArgumentInput("");
//            }
//        }
//        return time;
//    }
//
//    public LocalDate extractDateFromRaw(String command, int dIndex) throws InvalidArgumentInput {
//        String dateStr = "";
//        LocalDate date = null;
//        if (dIndex != -1) {
//            String tail = command.substring(dIndex + 2).trim();
//            String[] token = tail.split("\\s+");
//            if (token.length > 0) {
//                dateStr = token[0];
//            }
//        }
//
//        if (!dateStr.isEmpty()) {
//            try {
//                date = LocalDate.parse(dateStr, DATE_FMT);
//            } catch (Exception ex) {
//                ui.showMessage("Invalid date. Use d/DD/MM/YY (e.g., d/23/10/25).");
//                throw new InvalidArgumentInput("");
//            }
//        }
//        return date;
//    }

//    private void promptIfDateOrTimeMissing() throws InvalidArgumentInput {
//        if (date == null) {
//            String todayStr = LocalDate.now().format(DATE_FMT);
//            ui.showMessage("Looks like you missed the date. Use current date (" + todayStr + ")? (Y/N)");
//            if (parser.confirmationMessage()) {
//                date = LocalDate.now();
//            } else {
//                ui.showMessage("Please provide a date in format d/DD/MM/YY.");
//                throw new InvalidArgumentInput("");
//            }
//        }
//
//        if (time == null) {
//            String nowStr = LocalTime.now().format(TIME_FMT);
//            ui.showMessage("Looks like you missed the time. Use current time (" + nowStr + ")? (Y/N)");
//            if (parser.confirmationMessage()) {
//                time = LocalTime.now();
//            } else {
//                ui.showMessage("Please provide a time in format t/HHmm.");
//                throw new InvalidArgumentInput("");
//            }
//        }
//    }

    private void checkPastFutureDate(LocalDate date, LocalTime time)
            throws InvalidArgumentInput, IOException {
        if (date.isAfter(LocalDate.now())) {
            ui.showMessage("The date you entered (" + date.format(DATE_FMT)
                    + ") is in the future. Are you sure? (Y/N)");
            if (!parser.confirmationMessage()) {
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
            if (!parser.confirmationMessage()) {
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
     * Handles the complete workout deletion flow by display ID.
     * Parses the command, validates input, shows details, confirms, and deletes.
     *
     * @param raw the raw command string from user (e.g., "id/8")
     * @throws IOException if saving fails
     * @throws FileNonexistent if the month file doesn't exist
     */
    public void handleDeleteWorkout(String raw) throws IOException, FileNonexistent {
        Parser.DelArgs d;
        try {
            d = parser.parseDel(raw);
        } catch (InvalidArgumentInput e) {
            ui.showMessage(e.getMessage());
            return;
        }

        if (d.byMonth) {
            // Form: -m <MONTH> <INDEX>
            YearMonth ym = d.ym;
            int displayIndex = d.index1Based;

            ArrayList<Workout> monthWorkouts = fileHandler.loadMonthList(ym);
            if (displayIndex < 1 || displayIndex > monthWorkouts.size()) {
                ui.showMessage("Invalid workout ID: " + displayIndex);
                ui.showMessage("Please use a valid ID between 1 and " + monthWorkouts.size());
                ui.showMessage("Use /view_log -m " + ym.getMonthValue() + " to see IDs.");
                return;
            }

            Workout w = monthWorkouts.get(displayIndex - 1);
            ui.showMessage("You are about to delete this workout:");
            ui.displayDetailsOfWorkout(w);
            ui.showMessage("Are you sure you want to delete this workout? (Press 'Y' to confirm, 'N' to cancel)");

            Boolean ok = parser.confirmationMessage();
            if (ok == null) return;                      // only "Action cancelled." line
            if (!ok) { ui.showMessage("Deletion cancelled. Workout preserved."); return; }

            monthWorkouts.remove(displayIndex - 1);
            fileHandler.saveMonthList(ym, monthWorkouts);

            if (currentLoadedMonth.equals(ym)) {
                this.workouts = monthWorkouts;
            }

            ui.showMessage("Deleted workout: " + w.getWorkoutName());
            ui.showMessage("Updated workout list saved successfully.");
            return;
        }

        // Form: <WORKOUT_NAME>  (exact name match)
        String target = d.targetName;
        ArrayList<Workout> monthWorkouts = fileHandler.loadMonthList(currentLoadedMonth);

        // Find exact name in the currently loaded month list (same behavior as your old delete-by-name)
        int idx = -1;
        for (int i = 0; i < monthWorkouts.size(); i++) {
            if (monthWorkouts.get(i).getWorkoutName().equals(target)) {
                idx = i; break;
            }
        }

        if (idx == -1) {
            ui.showMessage("No workout named \"" + target + "\" found in "
                    + currentLoadedMonth.getMonth().toString().substring(0,1).toUpperCase()
                    + currentLoadedMonth.getMonth().toString().substring(1).toLowerCase()
                    + " " + currentLoadedMonth.getYear() + ".");
            ui.showMessage("Tip: /view_log to see the list with IDs, then delete by ID via:");
            ui.showMessage("  /del_workout id/<ID>");
            ui.showMessage("  /del_workout id/<ID> -m <MONTH>");
            ui.showMessage("  /del_workout -m <MONTH> <ID>");
            ui.showMessage("  /del_workout -ym <YEAR> <MONTH> <ID>");
            return;
        }

        Workout w = monthWorkouts.get(idx);
        ui.showMessage("You are about to delete this workout:");
        ui.displayDetailsOfWorkout(w);
        ui.showMessage("Are you sure you want to delete this workout? (Press 'Y' to confirm, 'N' to cancel)");

        if (!parser.confirmationMessage()) {
            ui.showMessage("Deletion cancelled. Workout preserved.");
            return;
        }

        monthWorkouts.remove(idx);
        fileHandler.saveMonthList(currentLoadedMonth, monthWorkouts);
        this.workouts = monthWorkouts;

        ui.showMessage("✓ Deleted workout: " + w.getWorkoutName());
        ui.showMessage("Updated workout list saved successfully.");
    }


    /**
     * Deletes a workout by its display index from ViewLog.
     * The index corresponds to the ID shown in /view_log output.
     * Prompts user for confirmation before deletion.
     *
     * @param displayIndex the 1-based index from view_log
     * @param month the YearMonth of the workout list
     * @throws IOException if saving fails
     * @throws FileNonexistent if the month file doesn't exist
     */
    private void deleteWorkoutByDisplayId(int displayIndex, YearMonth month) throws IOException, FileNonexistent {
        // Load the correct month's workouts
        ArrayList<Workout> monthWorkouts = fileHandler.loadMonthList(month);

        // Validate index
        if (displayIndex < 1 || displayIndex > monthWorkouts.size()) {
            ui.showMessage("Invalid workout ID: " + displayIndex);
            ui.showMessage("Please use a valid ID between 1 and " + monthWorkouts.size());
            ui.showMessage("Use /view_log to see all workout IDs.");
            return;
        }

        // Get the workout (convert 1-based to 0-based)
        Workout workoutToDelete = monthWorkouts.get(displayIndex - 1);

        // Show workout details
        ui.showMessage("You are about to delete this workout:");
        ui.displayDetailsOfWorkout(workoutToDelete);
        ui.showMessage("Are you sure you want to delete this workout? (Press 'Y' to confirm, 'N' to cancel)");

        // Confirm deletion
        if (!parser.confirmationMessage()) {
            ui.showMessage("Deletion cancelled. Workout preserved.");
            return;
        }

        // Store workout name before deletion
        String deletedWorkoutName = workoutToDelete.getWorkoutName();

        // Remove the workout
        monthWorkouts.remove(displayIndex - 1);

        // Save updated list
        fileHandler.saveMonthList(month, monthWorkouts);

        // Update current workouts if we're viewing the same month
        if (currentLoadedMonth.equals(month)) {
            this.workouts = monthWorkouts;
        }

        ui.showMessage("✓ Deleted workout: " + deletedWorkoutName);
        ui.showMessage("Updated workout list saved successfully.");
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

        Parser.AddExerciseArgs a;
        try {
            a = parser.parseAddExercise(args);
        } catch (InvalidArgumentInput e) {
            ui.showMessage(e.getMessage());
            return;
        }

        Exercise exercise = new Exercise(a.name, a.reps);
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

        Parser.AddSetArgs a;
        try {
            a = parser.parseAddSet(args);
        } catch (InvalidArgumentInput e) {
            ui.showMessage(e.getMessage());
            return;
        }

        currentExercise.addSet(a.reps);
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

        Parser.EndWorkoutArgs a;
        try {
            a = parser.parseEndWorkout(initialArgs);
        } catch (InvalidArgumentInput e) {
            ui.showMessage(e.getMessage());
            return;
        }

        LocalDate date = a.date;
        LocalTime time = a.time;

        // Prompt for missing pieces (same UX you had)
        if (date == null) {
            String todayStr = LocalDate.now().format(DATE_FMT);
            ui.showMessage("Looks like you missed the date. Use current date (" + todayStr + ")? (Y/N)");
            Boolean ok = parser.confirmationMessage();
            if (ok == null) return;
            if (!ok) { ui.showMessage("Please enter: /end_workout d/DD/MM/YY t/HHmm"); return; }
            date = LocalDate.now();
        }
        if (time == null) {
            String nowStr = LocalTime.now().format(TIME_FMT);
            ui.showMessage("Looks like you missed the time. Use current time (" + nowStr + ")? (Y/N)");
            Boolean ok = parser.confirmationMessage();
            if (ok == null) return;
            if (!ok) { ui.showMessage("Please enter: /end_workout d/DD/MM/YY t/HHmm"); return; }
            time = LocalTime.now();
        }

        LocalDateTime proposedEnd = LocalDateTime.of(date, time).truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime startTime = currentWorkout.getWorkoutStartDateTime().truncatedTo(ChronoUnit.MINUTES);

        if (!proposedEnd.isAfter(startTime)) {
            ui.showMessage("End time must be after the start time of the workout!");
            ui.showMessage("Please enter: /end_workout d/DD/MM/YY t/HHmm");
            return;
        }

        // Overlap guard against later workouts on the SAME DAY
        for (Workout w : workouts) {
            if (w == currentWorkout) continue;
            LocalDateTime otherStart = w.getWorkoutStartDateTime();
            if (otherStart == null) continue;
            if (!otherStart.toLocalDate().equals(startTime.toLocalDate())) continue;
            if (!otherStart.isBefore(startTime) && otherStart.isBefore(proposedEnd)) {
                String startStr = otherStart.toLocalTime().format(TIME_FMT);
                LocalDateTime otherEndDT = w.getWorkoutEndDateTime();
                String endStr = (otherEndDT == null) ? "ongoing" : otherEndDT.toLocalTime().format(TIME_FMT);
                ui.showMessage("[Error] End time overlaps another workout: \"" + w.getWorkoutName()
                        + "\" (" + startStr + "–" + endStr + ").");
                ui.showMessage("Please enter a valid date and time");
                return;
            }
        }

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

//    // Count occurrences of a token like "n/" or "r/"
//    private static int countToken(String s, String token) {
//        int count = 0;
//        int idx = 0;
//        while ((idx = s.indexOf(token, idx)) != -1) {
//            count++;
//            idx += token.length();
//        }
//        return count;
//    }

//    // Extract the slice for a token (e.g., starting at index of 'n' in "n/..."),
//    // capturing the raw substring and where it ends in the original string.
//    private static Slice extractSlice(String s, int tokenStart) {
//        int valueStart = tokenStart + 2; // skip "x/"
//        if (valueStart > s.length()) {
//            return new Slice("", "", valueStart);
//        }
//
//        Matcher m = NEXT_PREFIX.matcher(s);
//        int next = -1;
//        while (m.find()) {
//            int boundaryStart = m.start(); // includes the whitespace before the next token
//            if (boundaryStart > valueStart) { // ensure it's after our value actually begins
//                next = boundaryStart;
//                break;
//            }
//        }
//
//        String raw = (next == -1) ? s.substring(valueStart) : s.substring(valueStart, next);
//        return new Slice(raw.trim(), raw, (next == -1 ? s.length() : next));
//    }


//    private static boolean isValidName(String name) {
//        if (name == null) {
//            return false;
//        }
//        String t = name.trim();
//        if (t.isEmpty() || t.length() > MAX_EXERCISE_NAME_LEN) {
//            return false;
//        }
//        return NAME_ALLOWED.matcher(t).matches();
//    }
//
//    private static Character findFirstIllegalNameChar(String name) {
//        Matcher m = NAME_ILLEGAL_FINDER.matcher(name);
//        return m.find() ? name.charAt(m.start()) : null;
//    }

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
