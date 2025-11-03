package seedu.fitchasers.ui;

import seedu.fitchasers.exceptions.InvalidArgumentInput;
import seedu.fitchasers.user.WeightManager;

import java.util.Scanner;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses and validates all console input for FitChasers.
 * <p>
 * This class owns the input {@link Scanner} and renders the right-aligned
 * "user" chat bubble prompts. It delegates display of informational and error
 * messages to {@link UI}.
 * Usage:
 * <pre>
 *   UI ui = new UI();                // handles left-bubble outputs
 *   Parser parser = new Parser(ui);  // handles all inputs
 *   String cmd = parser.readCommand();
 * </pre>
 */
public class Parser {

    // --- Styling kept local so UI doesn't need to expose internals ---
    private static final String RESET = "\u001B[0m";
    private static final String LIGHT_YELLOW = "\u001B[38;5;187m";
    private static final String BOLD_BRIGHT_PURPLE = "\u001B[1;38;5;183m";
    private static final String BOLD_RESET = "\u001B[0m";

    // Layout should match UI so bubbles line up nicely
    private static final int CONSOLE_WIDTH = 150;
    private static final int PADDING = 2;
    private static final int FRAME_OVERHEAD = 6;

    private static final UI ui = new UI();
    private static final Scanner scanner = new Scanner(System.in);

    private static final int MAX_EXERCISE_NAME_LEN = 32;
    private static final int MAX_REPS = 1000;

    private static final Pattern NAME_ALLOWED = Pattern.compile("[A-Za-z0-9 _-]+");
    private static final Pattern NAME_ILLEGAL_FINDER = Pattern.compile("[^A-Za-z0-9 _-]");
    private static final Pattern NEXT_PREFIX = Pattern.compile("\\s+[A-Za-z]/");
    private static final Pattern BOUND_PREFIX = Pattern.compile("(^|\\s)([A-Za-z])/");
    private static final Pattern REPS_TOKEN = Pattern.compile("^\\d{1,4}$");
    private static final Pattern INT_TOKEN = Pattern.compile("^-?\\d+$");

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yy").withResolverStyle(ResolverStyle.SMART);
    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("HHmm").withResolverStyle(ResolverStyle.SMART);

    /**
     * Reads a general command input from the user.
     *
     * @return the command entered by the user (trimmed), or {@code null} if no line is available
     */
    public String readCommand() {
        return readInsideRightBubble("Enter command > ");
    }

//    /**
//     * Prompts the user to enter one or more indices for deletion.
//     *
//     * @return the trimmed input string containing indices, or {@code null} if no input is provided
//     */
//    public String enterSelection() {
//        String s = readInsideRightBubble("Enter the index(es) to be deleted > ");
//        if (s == null || s.isEmpty()) {
//            ui.showError("No input detected.");
//            return null;
//        }
//        return s.trim();
//    }

    /**
     * Prompts the user to enter their name. Ensures non-empty, reasonable format.
     *
     * @return the trimmed username, or {@code null} if input is terminated
     */
    public String enterName() {
        while (true) {
            String name = readInsideRightBubble("Enter your name > ");
            if (name == null) {
                return null;
            }
            name = name.trim();

            if (name.isEmpty()) {
                ui.showError("Name cannot be empty. Please try again!");
                continue;
            }
            if (name.length() > 30) {
                ui.showError("Name is too long. Maximum is 30 characters.");
                continue;
            }
            if (!name.matches("^[a-zA-Z0-9 _-]+$")) {
                ui.showError("Name can only contain letters, numbers, spaces, underscores (_), or dashes (-).");
                continue;
            }
            return name;
        }
    }

    /**
     * Prompts the user to enter their initial weight in kilograms.
     * Validates numeric input using {@link WeightManager#isValidWeight(double)}.
     *
     * @return the user's initial weight as a double, or -1 if input fails/terminates
     */
    public double enterWeight(WeightManager weightManager) {
        double weight;
        while (true) {
            ui.showMessage("Please enter your initial weight (in kg).");
            String ans = readInsideRightBubble("Enter your weight > ");
            if (ans == null) {
                ui.showError("No input detected. Exiting weight entry.");
                return -1;
            }
            String input = ans.trim();
            try {
                weight = Double.parseDouble(input);
                if (weightManager.isValidWeight(weight)) {
                    return weight;
                }
                // If invalid per WeightManager, let WeightManager dictate range/logic via its error messaging upstream
                ui.showError("That weight is out of valid range. Please try again.");
            } catch (NumberFormatException e) {
                ui.showError("Invalid number. Please enter a valid weight (e.g., 60.5).");
            }
        }
    }

    /**
     * Prompts for a yes/no confirmation from the user.
     *
     * @return {@code true} if confirmed (Y/Yes), {@code false} if No or input terminates
     */
    public Boolean confirmationMessage() {
        while (true) {
            String ans = readInsideRightBubble("Confirm (Y/N or /cancel) > ");
            if (ans == null) {
                return false;
            }

            String lower = ans.trim().toLowerCase();

            if (lower.equals("y") || lower.equals("yes")) {
                return true;
            }
            if (lower.equals("n") || lower.equals("no")) {
                return false;
            }
            if (lower.equals("/cancel")) {
                ui.showMessage("Action cancelled.");
                return null;
            }

            ui.showError("Please answer Y or N (yes/no), or type /cancel to abort.");
        }
    }


    public String readInsideRightBubble(String prompt) {
        int innerWidth = Math.max(1, (int) (CONSOLE_WIDTH * 3.0 / 5) - FRAME_OVERHEAD);
        int pad = clampNonNeg(CONSOLE_WIDTH - innerWidth - 6);

        String top = BOLD_BRIGHT_PURPLE + "+" + "-".repeat(innerWidth) + "+" + RESET;
        String bottom = BOLD_BRIGHT_PURPLE + "+" + "-".repeat(innerWidth) + "+" + RESET;
        String leftPrefix = " ".repeat(pad) + BOLD_BRIGHT_PURPLE + "|" + RESET
                + BOLD_BRIGHT_PURPLE + " ".repeat(PADDING) + RESET;

        System.out.println(" ".repeat(pad) + LIGHT_YELLOW + "(You)" + RESET);
        System.out.println(" ".repeat(pad) + top);
        System.out.print(leftPrefix + BOLD_BRIGHT_PURPLE + prompt + BOLD_RESET);
        System.out.flush();

        if (!scanner.hasNextLine()) {
            System.out.println();
            System.out.println(" ".repeat(pad) + bottom);
            return null;
        }

        String input = scanner.nextLine();
        String trimmed = input.trim();
        System.out.println(" ".repeat(pad) + bottom);
        ui.printLeftHeader(); // keep the conversational rhythm the same as before
        return trimmed;
    }

    private static int clampNonNeg(int v) {
        return Math.max(0, v);
    }

    public void handleHelp(String command, String argumentStr) {
        if (!argumentStr.isEmpty()) {
            ui.showError("The /help command doesn't take any arguments.\n"
                    + "Just type '/help' or 'h' to see all available commands.");
        } else if (command.equals("help")) {
            ui.showMessage("Did you mean '/help'? Type '/help' or 'h' to see all available commands.");
        } else {
            ui.showHelp();
        }
    }

    // ---- public enums & DTOs ----
    public enum CommandType { CREATE_WORKOUT, ADD_EXERCISE, ADD_SET, END_WORKOUT, ADD_WEIGHT, SET_GOAL, VIEW_LOG, OPEN}

    public static final class ParseOutcome {
        public final CommandType type;
        public final Object payload; // one of *Args
        public ParseOutcome(CommandType type, Object payload) {
            this.type = type; this.payload = payload;
        }
    }

    public static final class CreateWorkoutArgs {
        /** Non-empty, validated name. */
        public final String name;
        /** Nullable in FLEX mode; non-null in STRICT mode. */
        public final LocalDate date;
        /** Nullable in FLEX mode; non-null in STRICT mode. */
        public final LocalTime time;
        public final boolean strict; // true if strict path was enforced
        public CreateWorkoutArgs(String name, LocalDate date, LocalTime time, boolean strict) {
            this.name = name; this.date = date; this.time = time; this.strict = strict;
        }
    }

    public static final class AddExerciseArgs {
        public final String name;
        public final int reps;
        public AddExerciseArgs(String name, int reps) { this.name = name; this.reps = reps; }
    }

    public static final class AddSetArgs {
        public final int reps;
        public AddSetArgs(int reps) { this.reps = reps; }
    }

    public static final class EndWorkoutArgs {
        /** Nullable pieces; let caller decide defaults/confirmation. */
        public final LocalDate date;
        public final LocalTime time;
        public EndWorkoutArgs(LocalDate date, LocalTime time) { this.date = date; this.time = time; }
    }

    public ParseOutcome parse(String fullLine) throws InvalidArgumentInput {
        if (fullLine == null || fullLine.isBlank()) throw err("Empty command.");
        String s = fullLine.trim();

        // If it looks like just args (starts with n/), assume create_workout
        if (s.startsWith("n/") || s.startsWith("d/") || s.startsWith("t/")) {
            return new ParseOutcome(CommandType.CREATE_WORKOUT, parseCreateWorkout("/create_workout " + s));
        }

        // otherwise normal routing
        if (s.startsWith("/create_workout")) {
            return new ParseOutcome(CommandType.CREATE_WORKOUT, parseCreateWorkout(s));
        } else if (s.startsWith("/add_exercise")) {
            return new ParseOutcome(CommandType.ADD_EXERCISE, parseAddExercise(argsOnly(s)));
        } else if (s.startsWith("/add_set")) {
            return new ParseOutcome(CommandType.ADD_SET, parseAddSet(argsOnly(s)));
        } else if (s.startsWith("/end_workout")) {
            return new ParseOutcome(CommandType.END_WORKOUT, parseEndWorkout(argsOnly(s)));
        } else if (s.startsWith("/add_weight")) {
        return new ParseOutcome(CommandType.ADD_WEIGHT, parseAddWeight(argsOnly(s)));
        }  else if (s.startsWith("/set_goal")) {
        return new ParseOutcome(CommandType.SET_GOAL, parseSetGoal(argsOnly(s)));
        } else if (s.equals("vl") || s.startsWith("vl ")) {
            return new ParseOutcome(CommandType.VIEW_LOG, parseViewLog(argsOnly(s)));
        } else if (s.startsWith("/view_log")) {
            throw err("'/view_log' is deprecated. Use one of:\n" +
                    "  vl\n" +
                    "  vl pg/<page>\n" +
                    "  vl m/<month>\n" +
                    "  vl ym/<month>/<year>");
        } else if (s.startsWith("/open")) {
            return new ParseOutcome(CommandType.OPEN, parseOpen(argsOnly(s)));
        }


        throw err("Unknown command. Type /help or h to see supported commands.");
    }




    // ---- create_workout ----
    private CreateWorkoutArgs parseCreateWorkout(String full) throws InvalidArgumentInput {
        // strip command keyword
        String args = full.substring("/create_workout".length()).trim();
        // Determine STRICT vs FLEX: if user provided any d/ or t/ → STRICT (like your requirement)
        boolean wantsStrict = args.contains("d/") || args.contains("t/");

        return wantsStrict
                ? parseCreateWorkoutStrict(args)
                : parseCreateWorkoutFlex(args);
    }

    private CreateWorkoutArgs parseCreateWorkoutStrict(String args) throws InvalidArgumentInput {
        if (args.isBlank()) {
            throw err("Missing information. Use: /create_workout n/NAME d/DD/MM/YY t/HHmm");
        }
        final String s = args;

        requireExactlyOne(s, "n/");
        requireExactlyOne(s, "d/");
        requireExactlyOne(s, "t/");
        int nIdx = s.indexOf("n/"), dIdx = s.indexOf("d/"), tIdx = s.indexOf("t/");
        if (!(nIdx < dIdx && dIdx < tIdx)) {
            throw err("Order must be n/ then d/ then t/. Example: /create_workout n/Push Day d/20/10/25 t/1900");
        }

        Slice nameSlice = extractSlice(s, nIdx);
        String name = nameSlice.valueTrimmed;
        if (name.isEmpty()) throw err("Workout name is missing after n/. Example: n/Leg Day");
        validateName(name);

        Slice dateSlice = extractSlice(s, dIdx);
        if (startsWithSpace(dateSlice)) {
            throw err("Remove spaces between d/ and the date. Example: d/23/10/25 (not d/ 23/10/25)");
        }
        LocalDate date = parseDate(dateSlice.valueTrimmed);

        Slice timeSlice = extractSlice(s, tIdx);
        if (startsWithSpace(timeSlice)) {
            throw err("Remove spaces between t/ and the time. Example: t/1905 (not t/ 1905)");
        }
        LocalTime time = parseTime(timeSlice.valueTrimmed);

        // trailing junk
        ensureNoJunkAfter(s, timeSlice.endIndex,
                "Unexpected text after time. Use exactly: /create_workout n/NAME d/DD/MM/YY t/HHmm");

        // unsupported flags outside parsed regions
        ensureOnlyAllowedFlags(s, new Range(nIdx, nameSlice.endIndex),
                new Range(dIdx, dateSlice.endIndex),
                new Range(tIdx, timeSlice.endIndex),
                'n','d','t');

        return new CreateWorkoutArgs(name, date, time, true);
    }

    private CreateWorkoutArgs parseCreateWorkoutFlex(String args) throws InvalidArgumentInput {
        // n/ required; d/, t/ optional
        if (!args.contains("n/")) {
            throw err("Invalid format. Use: /create_workout n/WorkoutName d/DD/MM/YY t/HHmm");
        }
        int nIdx = args.indexOf("n/");
        int afterName = nIdx + 2;
        int dIdx = args.indexOf("d/", afterName);
        int tIdx = args.indexOf("t/", afterName);

        String name = sliceBetween(args, afterName, nearestPositive(afterName, dIdx, tIdx)).trim();
        if (name.isEmpty()) {
            throw err("Workout name cannot be empty. Use: /create_workout n/WorkoutName d/DD/MM/YY t/HHmm");
        }
        validateName(name);

        LocalDate date = null;
        LocalTime time = null;

        if (dIdx != -1) {
            Slice d = extractSlice(args, dIdx);
            if (!d.valueTrimmed.isEmpty()) date = parseDate(d.valueTrimmed);
        }
        if (tIdx != -1) {
            Slice t = extractSlice(args, tIdx);
            if (!t.valueTrimmed.isEmpty()) time = parseTime(t.valueTrimmed);
        }

        // Note: In FLEX, we don't forbid extra text explicitly, since other flags
        // are legal only if they are n/d/t and were already consumed; but still
        // protect against alien flags:
        ensureOnlyAllowedFlags(args,
                (dIdx == -1) ? null : new Range(dIdx, extractSlice(args, dIdx).endIndex),
                (tIdx == -1) ? null : new Range(tIdx, extractSlice(args, tIdx).endIndex),
                new Range(nIdx, nIdx + 2 + name.length()),
                'n','d','t');

        return new CreateWorkoutArgs(name, date, time, false);
    }

    // ---- add_exercise ----
    public AddExerciseArgs parseAddExercise(String args) throws InvalidArgumentInput {
        if (blank(args)) {
            throw err("Missing information. Use: /add_exercise n/NAME r/REPS (e.g., /add_exercise n/PushUp r/12)");
        }
        String s = args.trim();
        requireExactlyOne(s, "n/");
        requireExactlyOne(s, "r/");
        int nIdx = s.indexOf("n/"), rIdx = s.indexOf("r/");
        if (rIdx < nIdx) {
            throw err("Order must be n/ then r/. Example: /add_exercise n/Bench Press r/12");
        }

        Slice nameSlice = extractSlice(s, nIdx);
        String name = nameSlice.valueTrimmed;
        if (name.isEmpty()) throw err("Exercise name is missing after n/. Example: n/Bench Press");
        validateName(name);

        Slice repsSlice = extractSlice(s, rIdx);
        if (startsWithSpace(repsSlice)) {
            throw err("Remove spaces between r/ and the number. Example: r/12 (not r/ 12)");
        }
        int reps = parseReps(repsSlice.valueTrimmed);

        ensureNoJunkAfter(s, repsSlice.endIndex,
                "Unexpected text after reps. Use exactly: /add_exercise n/NAME r/REPS");

        ensureOnlyAllowedFlags(s, new Range(nIdx, nameSlice.endIndex),
                new Range(rIdx, repsSlice.endIndex),
                'n','r');

        return new AddExerciseArgs(name, reps);
    }

    // ---- add_set ----
    public AddSetArgs parseAddSet(String args) throws InvalidArgumentInput {
        if (blank(args)) throw err("Missing information. Use: /add_set r/REPS");
        String s = args.trim();
        requireExactlyOne(s, "r/");

        // reject other flags
        Matcher stray = BOUND_PREFIX.matcher(s);
        while (stray.find()) {
            char p = stray.group(2).charAt(0);
            if (p != 'r') throw err("Only r/ is allowed for this command. Usage: /add_set r/REPS");
        }

        int rIdx = s.indexOf("r/");
        Slice repsSlice = extractSlice(s, rIdx);
        if (startsWithSpace(repsSlice)) {
            throw err("Invalid reps. Use a whole number between 1 and 1000. Example: /add_set r/15");
        }
        int reps = parseReps(repsSlice.valueTrimmed);

        ensureNoJunkAfter(s, repsSlice.endIndex,
                "Unexpected text after reps. Use exactly: /add_set r/REPS");

        return new AddSetArgs(reps);
    }

    // ---- end_workout ----
    public EndWorkoutArgs parseEndWorkout(String args) throws InvalidArgumentInput {
        String s = (args == null) ? "" : args.trim();

        requireAtMostOne(s, "d/");
        requireAtMostOne(s, "t/");

        int dIdx = s.indexOf("d/");
        int tIdx = s.indexOf("t/");
        if (dIdx != -1 && tIdx != -1 && tIdx < dIdx) {
            throw err("Order must be d/ then t/. Example: /end_workout d/29/10/25 t/1800");
        }

        // reject other flags
        Matcher stray = BOUND_PREFIX.matcher(s);
        while (stray.find()) {
            char p = stray.group(2).charAt(0);
            if (p != 'd' && p != 't') {
                throw err("Only d/ and t/ are allowed. Usage: /end_workout d/DD/MM/YY t/HHmm");
            }
        }

        Slice dateSlice = (dIdx != -1) ? extractSlice(s, dIdx) : null;
        Slice timeSlice = (tIdx != -1) ? extractSlice(s, tIdx) : null;

        if (dateSlice != null && startsWithSpace(dateSlice)) {
            throw err("Invalid date. Use d/DD/MM/YY (e.g., d/23/10/25).\nUsage: /end_workout d/DD/MM/YY t/HHmm");
        }
        if (timeSlice != null && startsWithSpace(timeSlice)) {
            throw err("Invalid time. Use t/HHmm (e.g., t/1905).\nUsage: /end_workout d/DD/MM/YY t/HHmm");
        }

        int lastEnd = (timeSlice != null) ? timeSlice.endIndex : (dateSlice != null ? dateSlice.endIndex : -1);
        if (lastEnd != -1 && !onlyWhitespaceAfter(s, lastEnd)) {
            throw err("Unexpected text after time/date. Use exactly: /end_workout d/DD/MM/YY t/HHmm");
        }

        LocalDate date = (dateSlice == null || dateSlice.valueTrimmed.isEmpty())
                ? null : parseDate(dateSlice.valueTrimmed);
        LocalTime time = (timeSlice == null || timeSlice.valueTrimmed.isEmpty())
                ? null : parseTime(timeSlice.valueTrimmed);

        return new EndWorkoutArgs(date, time);
    }

    // ---- helpers: slices, flags, formats, errors ----
    private static final class Slice {
        final String valueTrimmed;
        final String valueRaw;
        final int endIndex;
        Slice(String valueTrimmed, String valueRaw, int endIndex) {
            this.valueTrimmed = valueTrimmed; this.valueRaw = valueRaw; this.endIndex = endIndex;
        }
    }
    private record Range(int start, int end) {}

    private static Slice extractSlice(String s, int tokenStart) {
        int valueStart = tokenStart + 2; // skip "x/"
        if (valueStart > s.length()) return new Slice("", "", valueStart);
        Matcher m = NEXT_PREFIX.matcher(s);
        int next = -1;
        while (m.find()) {
            int boundaryStart = m.start();
            if (boundaryStart > valueStart) { next = boundaryStart; break; }
        }
        String raw = (next == -1) ? s.substring(valueStart) : s.substring(valueStart, next);
        return new Slice(raw.trim(), raw, (next == -1 ? s.length() : next));
    }

    private static void ensureOnlyAllowedFlags(String s, Range r1, Range r2, Range r3, char... allowed) throws InvalidArgumentInput {
        Range[] ranges = new Range[] { r1, r2, r3 };
        Matcher stray = BOUND_PREFIX.matcher(s);
        while (stray.find()) {
            int pos = stray.start(2);
            char p = stray.group(2).charAt(0);
            boolean insideKnown = false;
            for (Range r : ranges) {
                if (r == null) continue;
                if (pos >= r.start && pos < r.end) { insideKnown = true; break; }
            }
            if (!insideKnown && !isAllowed(p, allowed)) {
                throw err("Unsupported flag \"" + p + "/\" found.");
            }
        }
    }
    private static boolean isAllowed(char c, char... allowed) {
        for (char a : allowed) if (a == c) return true;
        return false;
    }

    private static void ensureNoJunkAfter(String s, int end, String msg) throws InvalidArgumentInput {
        if (!onlyWhitespaceAfter(s, end)) throw err(msg);
    }

    private static boolean onlyWhitespaceAfter(String s, int endIndex) {
        for (int i = endIndex; i < s.length(); i++) if (!Character.isWhitespace(s.charAt(i))) return false;
        return true;
    }
    private static boolean startsWithSpace(Slice slice) {
        return !slice.valueRaw.isEmpty() && Character.isWhitespace(slice.valueRaw.charAt(0));
    }

    private static void validateName(String name) throws InvalidArgumentInput {
        String t = name.trim();
        if (t.isEmpty() || t.length() > MAX_EXERCISE_NAME_LEN) {
            throw err("Name too long or invalid. Allowed: letters, digits, spaces, hyphen (-), underscore (_). Max 32 chars.");
        }
        if (!NAME_ALLOWED.matcher(t).matches()) {
            Character bad = findFirstIllegalNameChar(t);
            String shown = (bad != null && bad == '\\') ? "\\\\" : String.valueOf(bad);
            throw err("“" + shown + "” is not allowed in the name. Allowed: letters, digits, spaces, -, _.");
        }
    }
    private static Character findFirstIllegalNameChar(String name) {
        Matcher m = NAME_ILLEGAL_FINDER.matcher(name);
        return m.find() ? name.charAt(m.start()) : null;
    }

    private static int parseReps(String repsStr) throws InvalidArgumentInput {
        String t = repsStr.trim();
        if (!REPS_TOKEN.matcher(t).matches()) {
            throw err("Invalid reps. Use a whole number between 1 and 1000. Example: r/12");
        }
        int val = Integer.parseInt(t);
        if (val < 1 || val > MAX_REPS) {
            throw err("Invalid reps. Use a whole number between 1 and 1000. Example: r/12");
        }
        return val;
    }
    private static LocalDate parseDate(String text) throws InvalidArgumentInput {
        try { return LocalDate.parse(text, DATE_FMT); }
        catch (Exception ex) { throw err("Invalid date. Use d/DD/MM/YY (e.g., d/23/10/25)."); }
    }
    private static LocalTime parseTime(String text) throws InvalidArgumentInput {
        try { return LocalTime.parse(text, TIME_FMT); }
        catch (Exception ex) { throw err("Invalid time. Use t/HHmm (e.g., t/1905)."); }
    }

    private static String argsOnly(String s) {
        int firstSpace = s.indexOf(' ');
        return (firstSpace == -1) ? "" : s.substring(firstSpace + 1);
    }
    private static String sliceBetween(String s, int startIncl, int endExclIfPositive) {
        return (endExclIfPositive == -1) ? s.substring(startIncl) : s.substring(startIncl, endExclIfPositive);
    }
    private static int nearestPositive(int afterNameIndex, int dIdx, int tIdx) {
        int next = -1;
        if (dIdx != -1 && tIdx != -1)       next = Math.min(dIdx, tIdx);
        else if (dIdx != -1)                next = dIdx;
        else if (tIdx != -1)                next = tIdx;
        return next;
    }
    private static boolean blank(String s) { return s == null || s.isBlank(); }
    private static void requireExactlyOne(String s, String token) throws InvalidArgumentInput {
        int c = 0, i = 0;
        while ((i = s.indexOf(token, i)) != -1) { c++; i += token.length(); }
        if (c != 1) throw err("Please provide exactly one " + token + ".");
    }
    private static void requireAtMostOne(String s, String token) throws InvalidArgumentInput {
        int c = 0, i = 0;
        while ((i = s.indexOf(token, i)) != -1) { c++; i += token.length(); }
        if (c > 1) throw err("Too many " + token + " flags.");
    }
    private static InvalidArgumentInput err(String msg) { return new InvalidArgumentInput(msg); }

    private static void ensureOnlyAllowedFlags(String s, Range r1, Range r2, char... allowed)
            throws InvalidArgumentInput {
        ensureOnlyAllowedFlags(s, r1, r2, null, allowed);
    }

    private static boolean isInt(String s) {
        return s != null && INT_TOKEN.matcher(s).matches();
    }

    public static final class AddWeightArgs {
        public final double weight;
        public final LocalDate date;
        public final boolean usedTodayFallback;
        public AddWeightArgs(double weight, LocalDate date, boolean usedTodayFallback) {
            this.weight = weight; this.date = date; this.usedTodayFallback = usedTodayFallback;
        }
    }

    public AddWeightArgs parseAddWeight(String args) throws InvalidArgumentInput {
        // Expected formats:
        //   /add_weight w/65
        //   /add_weight w/65 d/25/10/25
        if (blank(args)) {
            throw err("Missing info. Use: /add_weight w/WEIGHT [d/DD/MM/YY]");
        }
        String s = args.trim();

        requireExactlyOne(s, "w/");
        requireAtMostOne(s, "d/");

        int wIdx = s.indexOf("w/");
        Slice wSlice = extractSlice(s, wIdx);
        if (startsWithSpace(wSlice)) {
            throw err("Remove spaces after w/. Example: w/65 (not w/ 65)");
        }
        final String wText = wSlice.valueTrimmed;
        if (wText.isEmpty()) throw err("Missing weight after w/. Example: w/65");

        double weightVal;
        try {
            weightVal = Double.parseDouble(wText);
        } catch (NumberFormatException ex) {
            throw err("Invalid weight. Enter a number (e.g., 65 or 65.5).");
        }

        // (Optional) date
        int dIdx = s.indexOf("d/");
        LocalDate date = null;
        boolean usedToday = false;
        if (dIdx != -1) {
            Slice dSlice = extractSlice(s, dIdx);
            if (startsWithSpace(dSlice)) {
                throw err("Invalid date. Use d/DD/MM/YY (e.g., d/28/10/25).");
            }
            if (!dSlice.valueTrimmed.isEmpty()) {
                date = parseDate(dSlice.valueTrimmed);
            }
            ensureNoJunkAfter(s, dSlice.endIndex,
                    "Unexpected text after date. Use exactly: /add_weight w/WEIGHT [d/DD/MM/YY]");
            ensureOnlyAllowedFlags(s, new Range(wIdx, wSlice.endIndex),
                    new Range(dIdx, dSlice.endIndex), 'w','d');
        } else {
            // No date provided -> ask here (Parser owns interactivity)
            String todayStr = LocalDate.now().format(DATE_FMT);
            ui.showMessage("Looks like you missed the date. Use current date (" + todayStr + ")? (Y/N)");
            if (confirmationMessage()) {
                date = LocalDate.now();
                usedToday = true;
            } else {
                throw err("Please provide a date using d/DD/MM/YY.");
            }
            ensureOnlyAllowedFlags(s, new Range(wIdx, wSlice.endIndex), null, 'w');
            ensureNoJunkAfter(s, wSlice.endIndex,
                    "Unexpected trailing text. Use exactly: /add_weight w/WEIGHT [d/DD/MM/YY]");
        }

        return new AddWeightArgs(weightVal, date, usedToday);
    }

    public static final class SetGoalArgs {
        public final double targetWeight;
        /** Optional backdated set date; if null, logic layer can default to today */
        public final LocalDate date;
        public SetGoalArgs(double targetWeight, LocalDate date) {
            this.targetWeight = targetWeight; this.date = date;
        }
    }

    public SetGoalArgs parseSetGoal(String args) throws InvalidArgumentInput {
        // Expected:
        //   /set_goal w/60
        //   /set_goal w/60 d/25/10/25   (optional backdate)
        if (blank(args)) throw err("Missing info. Use: /set_goal w/TARGET_WEIGHT [d/DD/MM/YY]");
        String s = args.trim();

        requireExactlyOne(s, "w/");
        requireAtMostOne(s, "d/");

        int wIdx = s.indexOf("w/");
        Slice wSlice = extractSlice(s, wIdx);
        if (startsWithSpace(wSlice)) {
            throw err("Remove spaces after w/. Example: w/60 (not w/ 60)");
        }
        if (wSlice.valueTrimmed.isEmpty()) throw err("Missing weight after w/. Example: w/60");

        double target;
        try {
            target = Double.parseDouble(wSlice.valueTrimmed);
        } catch (NumberFormatException ex) {
            throw err("Invalid weight. Enter a number (e.g., 60 or 60.5).");
        }

        int dIdx = s.indexOf("d/");
        LocalDate setOn = null;
        if (dIdx != -1) {
            Slice dSlice = extractSlice(s, dIdx);
            if (startsWithSpace(dSlice)) {
                throw err("Invalid date. Use d/DD/MM/YY (e.g., d/28/10/25).");
            }
            if (!dSlice.valueTrimmed.isEmpty()) {
                setOn = parseDate(dSlice.valueTrimmed); // uses DATE_FMT
            }
            ensureNoJunkAfter(s, dSlice.endIndex,
                    "Unexpected text after date. Use exactly: /set_goal w/TARGET_WEIGHT [d/DD/MM/YY]");
            ensureOnlyAllowedFlags(s,
                    new Range(wIdx, wSlice.endIndex),
                    new Range(dIdx, dSlice.endIndex),
                    'w','d');
        } else {
            // no date provided → just ensure no junk after weight and allow only w/
            ensureOnlyAllowedFlags(s, new Range(wIdx, wSlice.endIndex), null, 'w');
            ensureNoJunkAfter(s, wSlice.endIndex,
                    "Unexpected trailing text. Use exactly: /set_goal w/TARGET_WEIGHT [d/DD/MM/YY]");
        }

        return new SetGoalArgs(target, setOn);
    }

    public static final class ViewLogArgs {
        public final java.time.YearMonth ym;
        public final int page;
        public final boolean detailed;
        public ViewLogArgs(java.time.YearMonth ym, int page, boolean detailed) {
            this.ym = ym; this.page = page; this.detailed = detailed;
        }
    }

    public static final class OpenArgs {
        public final int displayId;                  // 1-based ID shown in the list
        public final java.time.YearMonth ymContext;  // month context to resolve IDs against
        public OpenArgs(int displayId, java.time.YearMonth ymContext) {
            this.displayId = displayId; this.ymContext = ymContext;
        }
    }

    public ViewLogArgs parseViewLog(String raw) throws InvalidArgumentInput {
        java.time.YearMonth now = java.time.YearMonth.now();
        java.time.YearMonth target = now;
        int page = 1;
        boolean detailed = false; // not used in the new scheme

        // "vl"
        if (raw == null || raw.isBlank()) {
            return new ViewLogArgs(target, page, detailed);
        }

        String s = raw.trim();

        // Accept single-token only
        if (s.contains(" ")) {
            throw err("Invalid format. Accepted:\n" +
                    "  vl\n" +
                    "  vl pg/<page>\n" +
                    "  vl m/<month>\n" +
                    "  vl ym/<month>/<year>");
        }

        // "vl pg/<page>"
        if (s.startsWith("pg/")) {
            String v = s.substring(3).trim();
            if (!isInt(v)) throw err("Invalid page. Use: vl pg/<page>");
            page = readPositiveInt(v, "Page must be a positive integer.");
            return new ViewLogArgs(target, page, detailed);
        }

        // "vl m/<month>"
        if (s.startsWith("m/")) {
            String v = s.substring(2).trim();
            if (!isInt(v)) throw err("Invalid month. Use: vl m/<month>");
            int m = Integer.parseInt(v);
            validateMonth(m);
            target = java.time.YearMonth.of(now.getYear(), m);
            return new ViewLogArgs(target, page, detailed);
        }

        // "vl ym/<month>/<year>"
        if (s.startsWith("ym/")) {
            String body = s.substring(3).trim(); // "<MONTH>/<YEAR>"
            String[] parts = body.split("/");
            if (parts.length != 2 || !isInt(parts[0]) || !isInt(parts[1])) {
                throw err("Invalid ym. Use: vl ym/<month>/<year>  (e.g., vl ym/11/2025 or vl ym/11/25)");
            }
            int m = Integer.parseInt(parts[0]);
            int y = parseYearFlexible(parts[1]); // accepts 2- or 4-digit year
            validateMonth(m);
            validateYear(y);
            target = java.time.YearMonth.of(y, m);
            return new ViewLogArgs(target, page, detailed);
        }

        throw err("Invalid format. Accepted:\n" +
                "  vl\n" +
                "  vl pg/<page>\n" +
                "  vl m/<month>\n" +
                "  vl ym/<month>/<year>");
    }

    private static int parseYearFlexible(String yText) throws InvalidArgumentInput {
        try {
            int y = Integer.parseInt(yText);
            if (yText.length() == 2) y += 2000; // "25" -> 2025
            return y;
        } catch (NumberFormatException ex) {
            throw err("Invalid year. Use a number like 2025 (or 25 for 2025).");
        }
    }


    // === 1E) Implement parseOpen(...) ===
    public OpenArgs parseOpen(String raw) throws InvalidArgumentInput {
        if (raw == null || raw.isBlank()) {
            throw err("Usage: /open id/<INDEX>");
        }
        String[] tok = raw.trim().split("\\s+");
        if (tok.length != 1 || !tok[0].startsWith("id/")) {
            throw err("Usage: /open id/<INDEX>");
        }

        String num = tok[0].substring(3).trim();
        if (!isInt(num)) {
            throw err("Workout ID must be a positive integer.");
        }

        int id = readPositiveInt(num, "Workout ID must be a positive integer.");
        return new OpenArgs(id, java.time.YearMonth.now());
    }

    public static int readIntArg(String[] tok, int idx, String err) throws InvalidArgumentInput {
        if (idx >= tok.length || !isInt(tok[idx])) {
            throw new InvalidArgumentInput(err);
        }
        return Integer.parseInt(tok[idx]);
    }

    public static int readPositiveInt(String s, String err) throws InvalidArgumentInput {
        try {
            int v = Integer.parseInt(s);
            if (v <= 0) {
                throw new NumberFormatException();
            }
            return v;
        } catch (NumberFormatException nfe) {
            throw new InvalidArgumentInput(err);
        }
    }

    public static void validateMonth(int month) throws InvalidArgumentInput {
        if (month < 1 || month > 12) {
            throw new InvalidArgumentInput("Month must be between 1 and 12.");
        }
    }

    public static void validateYear(int year) throws InvalidArgumentInput {
        if (year < 1970 || year > 2100) {
            throw new InvalidArgumentInput("Year must be between 1970 and 2100.");
        }
    }

    public static final class DelArgs {
        /** If true: delete within a month context by index. Otherwise: delete by exact name. */
        public final boolean byMonth;
        /** Only set when byMonth = true */
        public final java.time.YearMonth ym;
        /** Only set when byMonth = true */
        public final Integer index1Based;
        /** Only set when byMonth = false */
        public final String targetName;   // <- rename for clarity
        public DelArgs(boolean byMonth, java.time.YearMonth ym, Integer index1Based, String targetName) {
            this.byMonth = byMonth; this.ym = ym; this.index1Based = index1Based; this.targetName = targetName;
        }
    }

    public DelArgs parseDel(String raw) throws InvalidArgumentInput {
        if (blank(raw)) {
            throw err(
                    "Usage:\n" +
                            "  /del_workout id/<ID>\n" +
                            "  /del_workout m/<MONTH> id/<ID>\n" +
                            "  /del_workout ym/<MONTH>/<YEAR> id/<ID>"
            );
        }

        String[] tok = raw.trim().split("\\s+");
        java.time.YearMonth now = java.time.YearMonth.now();

        // ---- Case 1: /del_workout id/<ID>
        if (tok.length == 1 && tok[0].startsWith("id/")) {
            String v = tok[0].substring(3);
            if (!isInt(v)) throw err("Invalid ID. Usage: /del_workout id/<ID>");
            int id = readPositiveInt(v, "ID must be a positive integer.");
            return new DelArgs(true, now, id, null);
        }

        // ---- Case 2: /del_workout m/<MONTH> id/<ID>
        if (tok.length == 2 && tok[0].startsWith("m/") && tok[1].startsWith("id/")) {
            String mStr = tok[0].substring(2).trim();
            String idStr = tok[1].substring(3).trim();
            if (!isInt(mStr)) throw err("Invalid month. Usage: /del_workout m/<MONTH> id/<ID>");
            int m = Integer.parseInt(mStr);
            validateMonth(m);
            if (!isInt(idStr)) throw err("Invalid ID. Usage: /del_workout m/<MONTH> id/<ID>");
            int id = readPositiveInt(idStr, "ID must be a positive integer.");
            return new DelArgs(true, java.time.YearMonth.of(now.getYear(), m), id, null);
        }

        // ---- Case 3: /del_workout ym/<MONTH>/<YEAR> id/<ID>
        if (tok.length == 2 && tok[0].startsWith("ym/") && tok[1].startsWith("id/")) {
            String ymBody = tok[0].substring(3).trim(); // "<MONTH>/<YEAR>"
            String[] parts = ymBody.split("/");
            if (parts.length != 2 || !isInt(parts[0]) || !isInt(parts[1])) {
                throw err("Invalid ym. Usage: /del_workout ym/<MONTH>/<YEAR> id/<ID>");
            }
            int m = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            if (y <= 100) {
                y += 2000; // interpret '25' as 2025
            }

            String idStr = tok[1].substring(3).trim();
            if (!isInt(idStr)) throw err("Invalid ID. Usage: /del_workout ym/<MONTH>/<YEAR> id/<ID>");
            int id = readPositiveInt(idStr, "ID must be a positive integer.");

            return new DelArgs(true, java.time.YearMonth.of(y, m), id, null);
        }

        // Anything else is NOT accepted
        throw err(
                "Invalid format.\nAccepted:\n" +
                        "  /del_workout id/<ID>\n" +
                        "  /del_workout m/<MONTH> id/<ID>\n" +
                        "  /del_workout ym/<MONTH>/<YEAR> id/<ID>"
        );
    }



    // ==================== OWT (override workout tag) ====================
    public static final class OverrideTagArgs {
        public final int displayId;      // 1-based ID in month context
        public final java.time.YearMonth ym;
        public final String newTag;
        public final boolean confirmChange;       // result of Y/N prompt
        public final boolean confirmOverrideAuto; // result of Y/N prompt for overriding auto-tags
        public OverrideTagArgs(int displayId, java.time.YearMonth ym, String newTag,
                               boolean confirmChange, boolean confirmOverrideAuto) {
            this.displayId = displayId; this.ym = ym; this.newTag = newTag;
            this.confirmChange = confirmChange; this.confirmOverrideAuto = confirmOverrideAuto;
        }
    }

    public OverrideTagArgs parseOverrideWorkoutTag(String raw) throws InvalidArgumentInput {
        if (blank(raw)) throw err("Usage: /override_workout_tag id/ID newTag/TAG [-m M] | [-ym YYYY M]");
        String[] tok = raw.trim().split("\\s+");
        Integer id = null;
        String newTag = null;
        java.time.YearMonth now = java.time.YearMonth.now();
        java.time.YearMonth target = now;

        for (int i = 0; i < tok.length; i++) {
            String t = tok[i];
            if (t.startsWith("id/")) {
                String v = t.substring(3);
                if (v.isEmpty() || !isInt(v)) throw err("Invalid ID. Example: id/3");
                id = readPositiveInt(v, "ID must be a positive integer.");
            } else if (t.startsWith("newTag/")) {
                newTag = t.substring(7).trim();
            } else if (t.equals("-m")) {
                int m = readIntArg(tok, ++i, "Missing month after -m");
                validateMonth(m);
                target = java.time.YearMonth.of(now.getYear(), m);
            } else if (t.equals("-ym")) {
                int y = readIntArg(tok, ++i, "Missing year after -ym");
                validateYear(y);
                int m = readIntArg(tok, ++i, "Missing month after year");
                validateMonth(m);
                target = java.time.YearMonth.of(y, m);
            } else if (t.startsWith("-")) {
                throw err("Unknown flag: " + t);
            } else {
                // ignore stray free text; we enforce id/ and newTag/ explicitly
            }
        }
        if (id == null) throw err("Missing id/. Example: id/3");
        if (newTag == null || newTag.trim().isEmpty()) throw err("Missing newTag/. Example: newTag/LEGDAY");

        // confirmations here
        ui.showMessage("Change workout " + id + " tag to: " + newTag + "? (Y/N)");
        boolean ok = confirmationMessage();

        boolean okAuto = true; // default true; logic may ask again if needed, but we can pre-ask here
        // We don’t know conflicts yet; we can collect an up-front consent to override auto-tags:
        ui.showMessage("If there are auto-generated tags, do you allow overriding them? (Y/N)");
        okAuto = confirmationMessage();

        return new OverrideTagArgs(id, target, newTag, ok, okAuto);
    }

    public static final class GymPageArgs {
        /** Either pageNumber (1-based) or gymName will be non-null. */
        public final Integer pageNumber;
        public final String gymName;
        public GymPageArgs(Integer pageNumber, String gymName) {
            this.pageNumber = pageNumber; this.gymName = gymName;
        }
    }

    public GymPageArgs parseGymPage(String raw) throws InvalidArgumentInput {
        if (blank(raw)) throw err("Usage: /gym_page p/<page_or_gym_name>");
        String s = raw.trim();
        if (!s.startsWith("p/")) throw err("Usage: /gym_page p/<page_or_gym_name>");

        String val = s.substring(2).trim();
        if (val.isEmpty()) throw err("Please provide a gym number or name. Example: /gym_page p/1");
        if (isInt(val)) {
            int page = readPositiveInt(val, "Page must be a positive integer.");
            return new GymPageArgs(page, null);
        } else {
            return new GymPageArgs(null, val);
        }
    }

    public static final class GymWhereArgs {
        public final String exerciseName;
        public GymWhereArgs(String exerciseName) { this.exerciseName = exerciseName; }
    }

    public GymWhereArgs parseGymWhere(String raw) throws InvalidArgumentInput {
        if (blank(raw)) throw err("Usage: /gym_where n/exercise_name");
        String s = raw.trim();
        if (!s.startsWith("n/")) throw err("Usage: /gym_where n/exercise_name");
        String name = s.substring(2).trim();
        if (name.isEmpty()) throw err("Exercise name cannot be empty.");
        return new GymWhereArgs(name);
    }

    public static final class AddMuscleTagArgs {
        public final String muscleEnum;  // caller can convert to MuscleGroup
        public final String keyword;
        public AddMuscleTagArgs(String muscleEnum, String keyword) {
            this.muscleEnum = muscleEnum; this.keyword = keyword;
        }
    }

    public AddMuscleTagArgs parseAddMuscleTag(String raw) throws InvalidArgumentInput {
        if (blank(raw)) throw err("Usage: /add_muscle_tag m/<LEGS|...> k/<keyword>");
        String[] tok = raw.trim().split("\\s+");
        String m = null, k = null;
        for (String t : tok) {
            if (t.startsWith("m/")) m = t.substring(2).trim();
            else if (t.startsWith("k/")) k = t.substring(2).trim();
            else if (t.startsWith("-")) throw err("Unknown flag: " + t);
        }
        if (m == null || m.isEmpty()) throw err("Missing m/<MUSCLE>. Example: m/LEGS");
        if (k == null || k.isEmpty()) throw err("Missing k/<keyword>. Example: k/running");
        return new AddMuscleTagArgs(m, k);
    }

    public static final class AddModalityTagArgs {
        public final String modalityEnum; // caller can convert to Modality
        public final String keyword;
        public AddModalityTagArgs(String modalityEnum, String keyword) {
            this.modalityEnum = modalityEnum; this.keyword = keyword;
        }
    }

    public AddModalityTagArgs parseAddModalityTag(String raw) throws InvalidArgumentInput {
        if (blank(raw)) throw err("Usage: /add_modality_tag m/(CARDIO|STRENGTH) k/<keyword>");
        String[] tok = raw.trim().split("\\s+");
        String m = null, k = null;
        for (String t : tok) {
            if (t.startsWith("m/")) m = t.substring(2).trim();
            else if (t.startsWith("k/")) k = t.substring(2).trim();
            else if (t.startsWith("-")) throw err("Unknown flag: " + t);
        }
        if (m == null || m.isEmpty()) throw err("Missing m/<modality>. Example: m/CARDIO");
        if (k == null || k.isEmpty()) throw err("Missing k/<keyword>. Example: k/running");
        return new AddModalityTagArgs(m, k);
    }
}
