package seedu.fitchasers.workouts;

import seedu.fitchasers.parser.openworkout.deleteworkout.OpenWorkoutArguments;
import seedu.fitchasers.parser.openworkout.deleteworkout.OpenWorkoutParser;
import seedu.fitchasers.storage.FileHandler;
import seedu.fitchasers.exceptions.FileNonexistent;
import seedu.fitchasers.exceptions.InvalidArgumentInput;
import seedu.fitchasers.ui.UI;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

//@@author ZhongBaode
/**
 * Handles the display and navigation of the user's workout logs.
 * <p>
 * This class is responsible for loading, filtering, sorting, and paginating
 * workout data for display. It also maintains the most recently rendered list
 * to support features like reopening or viewing specific workouts by index.
 */
public class ViewLog {
    public static final int MINIMUM_PAGE_SIZE = 1;
    public static final int ARRAY_INDEX_OFFSET = 1;
    private static final Pattern INT = Pattern.compile("^-?\\d+$");
    private static UI ui = new UI();                         // your existing UI class
    private static final int NAME_MAX = 22;
    private final WorkoutManager workoutManager;
    private final int pageSize = 10;
    private final FileHandler fileHandler;

    // Keeps the last full, filtered & sorted list so `/open <n>` can work after rendering.
    private List<Workout> lastFilteredListofWorkout = List.of();

    private static class DisplayWorkout {
        Workout workout;
        int originalIndex;  // Original index in the main workouts list (1-based for display)

        DisplayWorkout(Workout w, int idx) {
            this.workout = w;
            this.originalIndex = idx;  // idx should be 1-based already
        }
    }

    /**
     * Constructs a ViewLog instance with required dependencies.
     *
     * @param ui             the UI instance for displaying output
     * @param workoutManager the WorkoutManager for accessing workout data
     * @param fileHandler    the FileHandler for loading workouts from disk
     */
    public ViewLog(UI ui, WorkoutManager workoutManager, FileHandler fileHandler) {
        ViewLog.ui = ui;
        this.workoutManager = workoutManager;
        this.fileHandler = fileHandler;
    }

    /* ----------------------------- Core renderers ----------------------------- */

    /**
     * Renders the monthly workout log with pagination and flags.
     * <p>
     * Supported forms:
     * <ul>
     *   <li>/view_log m/ &lt;month 1..12&gt; [extractedArg]</li>
     *   <li>/view_log -ym &lt;year&gt; &lt;month 1..12&gt; [extractedArg]</li>
     *   <li>Optional: -d (detailed view)</li>
     * </ul>
     * <p>
     * Examples:
     * <pre>
     *   /view_log -m 10          // Oct (current year), extractedArg 1
     *   /view_log -m 10 2        // Oct (current year), extractedArg 2
     *   /view_log -ym 2024 10    // Oct 2024, extractedArg 1
     *   /view_log -ym 2024 10 2  // Oct 2024, extractedArg 2
     *   /view_log -m 10 -d       // Oct, detailed extractedArg 1
     * </pre>
     *
     * @param args raw argument string after the command name
     * @throws InvalidArgumentInput if flags or numbers are invalid
     */
    public void render(String args) throws InvalidArgumentInput, FileNonexistent, IOException {
        Parsed p = parseArgs(args);

        ArrayList<Workout> sorted = loadAndSortList(p.ym);

        ArrayList<DisplayWorkout> displayList = new ArrayList<>();
        for (int i = 0; i < sorted.size(); i++) {
            displayList.add(new DisplayWorkout(sorted.get(i), i + 1));  // IDs 1, 2, 3, 4, 5 in order
        }

        int totalPages = computeTotalPages(displayList.size(), pageSize);
        int current = ensureValidPage(p.extractedArg);

        int start = (current - 1) * pageSize;
        int end = Math.min(start + pageSize, displayList.size());

        StringBuilder buf = new StringBuilder();
        buf.append(String.format("Workouts for %s (%d total) — Page %d/%d%n",
                p.ym, displayList.size(), current, Math.max(1, totalPages)));

        if (displayList.isEmpty()) {
            buf.append("No workouts this month.");
            ui.showMessage(buf.toString());
            return;
        }

        if (!p.detailed) {
            buf.append(String.format("%-4s %-20s %-20s %-22s %-10s%n",
                    "ID","Start Date", "End Date", "Name", "Duration"));
        }

        for (int i = start; i < end; i++) {
            DisplayWorkout dw = displayList.get(i);
            if (p.detailed) {
                buf.append(renderDetailedRow(dw.originalIndex, dw.workout));
            } else {
                buf.append(renderCompactRow(dw.originalIndex, dw.workout));
            }
        }

        buf.append("Tip: /view_log pg/2 (next pg of Current Month), /view_log m/10 (view October), /open <ID>.");
        ui.showMessage(buf.toString());
    }

    /**
     * Loads workouts for a specific month and sorts them by date (newest first).
     * <p>
     * Fetches workouts from the file handler and sorts them by end time, then start time,
     * with null values placed last. The sorted list is cached internally for quick access
     * via subsequent operations.
     *
     * @param p the YearMonth to load workouts for
     * @return a new ArrayList of workouts for the month, sorted newest first
     * @throws IOException     if an error occurs reading from disk
     * @throws FileNonexistent if no workout file exists for the specified month
     * @see FileHandler#loadMonthList(YearMonth)
     */
    public ArrayList<Workout> loadAndSortList(YearMonth p) throws IOException, FileNonexistent {
        // Fetch month list (lazy-load), then sort newest first by end time (nulls last)
        ArrayList<Workout> monthList = fileHandler.loadMonthList(p);
        ArrayList<Workout> sorted = new ArrayList<>(monthList);
        sorted.sort(
                Comparator.comparing(
                        Workout::getWorkoutStartDateTime,
                        Comparator.nullsLast(Comparator.reverseOrder())   // start: desc, nulls last
                ).thenComparing(
                        Workout::getWorkoutEndDateTime,
                        Comparator.nullsLast(Comparator.reverseOrder())   // end: desc, nulls last
                )
        );

        this.lastFilteredListofWorkout = sorted;  // Store the sorted list
        return sorted;
    }

    public Workout getWorkoutByDisplayId(int displayId, YearMonth month) throws FileNonexistent, IOException {
        // Fetch and sort on demand if needed
        if (lastFilteredListofWorkout.isEmpty()) {
            loadAndSortList(month);
        }

        if (displayId <= 0 || displayId > lastFilteredListofWorkout.size()) {
            return null;
        }
        return lastFilteredListofWorkout.get(displayId - 1);
    }

    private String renderCompactRow(int id, Workout w) {
        String startDate = formatDayMon(w.getWorkoutStartDateTime());
        String endDate = formatDayMon(w.getWorkoutEndDateTime());
        String name = truncate(safe(w.getWorkoutName()));
        String dur  = formatDuration(w.getDuration());
        return String.format("%-4d %-20s %-20s %-22s %-10s%n",
                id, safe(startDate), safe(endDate), safe(name), safe(dur));
    }


    private String renderDetailedRow(int id, Workout workout) {
        String startDateLong = formatLong(workout.getWorkoutStartDateTime());
        String endDateLong = formatLong(workout.getWorkoutEndDateTime());
        String dur = formatDuration(workout.getDuration());
        StringBuilder sb = new StringBuilder();
        sb.append("—".repeat(60)).append('\n');
        sb.append(String.format("#%d  %s%n", id, safe(workout.getWorkoutName())));
        sb.append("Start Date     : ").append(startDateLong).append('\n');
        sb.append("End Date     : ").append(endDateLong).append('\n');
        sb.append("Duration : ").append(dur).append('\n');
        String tags = workout.getAllTags().toString();
        sb.append("Tags     : ").append((tags.isBlank() ? "-" : tags)).append('\n');
        return sb.toString();
    }

    /**
     * Opens and displays detailed information for a workout by its display index.
     * <p>
     * Retrieves the workout from the cached rendered list and delegates to the UI
     * for display. The index must correspond to a previously rendered workout list.
     *
     * @param argument the 1-based display index of the workout to open
     * @throws InvalidArgumentInput if the index is out of bounds or invalid
     * @see #lastFilteredListofWorkout
     */
    public void openByIndex(String argument) throws InvalidArgumentInput, FileNonexistent, IOException {
        OpenWorkoutArguments parsed =
                new OpenWorkoutParser().parse(argument, workoutManager.getCurrentLoadedMonth());
        YearMonth ym = parsed.yearMonth();
        loadAndSortList(ym);
        int i = parsed.indexToOpen() - ARRAY_INDEX_OFFSET;  // Convert to 0-based
        if (i < 0 || i >= workoutManager.getWorkoutSize()) {
            throw new InvalidArgumentInput("The number you requested is out of bounds! " +
                    "\nPlease check view_log to see total number of open workouts.");
        }
        ui.displayDetailsOfWorkout(lastFilteredListofWorkout.get(i));
    }

    /* ------------------------------ Helpers/Util ----------------------------- */

    /**
     * Computes the total number of pages required to display a given number of items.
     * <p>
     * Ensures that both size and page size are non-negative before performing the calculation.
     *
     * @param size The total number of items.
     * @param pageSize The number of items per page.
     * @return The total number of pages needed to display all items.
     */
    public static int computeTotalPages(int size, int pageSize) {
        return (int) Math.ceil(Math.max(0, size) / (double) Math.max(1, pageSize));
    }

    private int ensureValidPage(int page) {
        int totalPages = computeTotalPages(this.workoutManager.getWorkoutSize(), pageSize);
        if (page < MINIMUM_PAGE_SIZE) {
            ui.showMessage("Hey that page is too small! I will default to the first page okay!");
            return MINIMUM_PAGE_SIZE;
        }

        if (page > totalPages) {
            ui.showMessage("Hey that page exceeds largest page! I will default to the last page okay!");
            return totalPages;
        }
        return page;
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private static String truncate(String s) {
        return s.length() <= NAME_MAX ? s : s.substring(0, Math.max(0, NAME_MAX - 1)) + "…";
    }

    private static String formatDuration(int minutes) {
        int h = minutes / 60;
        int m = minutes % 60;
        if (h == 0) {
            return m + "m";
        } else if (m == 0) {
            return h + "h";
        } else {
            return h + "h " + m + "m";
        }
    }

    private static String formatDayMon(LocalDateTime dt) {
        if (dt == null) {
            return "Unended";
        }
        String dow = dt.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        int day = dt.getDayOfMonth();
        String mon = dt.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        return String.format("%s %d %s", dow, day, mon);
    }

    private static String formatLong(LocalDateTime dt) {
        if (dt == null) {
            return "Unended";
        }
        String dow = dt.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        int d = dt.getDayOfMonth();
        String suffix = ui.getDaySuffix(d);
        String mon = dt.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        int hr = dt.getHour();
        int min = dt.getMinute();
        int hr12 = (hr % 12 == 0) ? 12 : (hr % 12);
        String ampm = hr < 12 ? "AM" : "PM";
        return String.format("%s %d%s of %s, %d:%02d %s", dow, d, suffix, mon, hr12, min, ampm);
    }

    /**
     * Represents the parsed result of a command or user input.
     * <p>
     * This record encapsulates a {@code YearMonth}, an extracted integer argument,
     * and a boolean flag indicating whether detailed output was requested.
     *
     * @param ym The {@code YearMonth} extracted from the input.
     * @param extractedArg The numeric argument parsed from the input.
     * @param detailed {@code true} if detailed output is requested; {@code false} otherwise.
     */
    public record Parsed(YearMonth ym, int extractedArg, boolean detailed) {
    }

    //@@author nitin19011
    /**
     * Parses raw user input to extract date, page, and detail parameters.
     * <p>
     * Supports arguments in the following formats:
     * <ul>
     *   <li>{@code m/<MM>} — Specifies the month of the current year.</li>
     *   <li>{@code ym/<MM>/<YY>} — Specifies a particular year and month.</li>
     *   <li>{@code pg/<N>} — Specifies the page number for paginated display.</li>
     *   <li>{@code detailed/} — Requests detailed output mode.</li>
     * </ul>
     * The method validates argument consistency (e.g., {@code m/} cannot be combined with {@code ym/})
     * and ensures numeric values are positive.
     *
     * @param raw The raw user input string containing command arguments.
     * @return A {@code Parsed} record containing the parsed {@code YearMonth}, page number, and detail flag.
     * @throws InvalidArgumentInput If the arguments are invalid, conflicting, or improperly formatted.
     */
    public Parsed parseArgs(String raw) throws InvalidArgumentInput {
        YearMonth now = YearMonth.now();
        YearMonth target = now;
        int page = 1;
        boolean detailed = false;

        if (raw == null || raw.isBlank()) {
            return new Parsed(target, page, false);
        }

        String[] arguments = raw.trim().split("\\s+");
        boolean seenM = false;
        boolean seenYM = false;
        boolean seenPg = false;

        for (int i = 0; i < arguments.length; i++) {
            String t = arguments[i];

            if ("detailed/".equals(t)) {
                detailed = true;
                continue;
            }

            if (t.startsWith("m/")) {
                if (seenYM) {
                    throw new InvalidArgumentInput("Cannot combine m/<MM> with ym/<A>/<B>.");
                }
                seenM = true;
                int month = readPositiveInt(t.substring(2), "Month after m/ must be an integer.");
                validateMonth(month);
                target = YearMonth.of(now.getYear(), month);

                // optional trailing page
                if (i + 1 < arguments.length && isInt(arguments[i + 1])) {
                    page = readPositiveInt(arguments[++i], "Page must be a positive integer.");
                }
                continue;
            }

            if (t.startsWith("ym/")) {
                if (seenM) {
                    throw new InvalidArgumentInput("Cannot combine ym/<A>/<B> with m/<MM>.");
                }
                seenYM = true;
                target = parseYearMonthToken(t.substring(3));
                // optional trailing page
                if (i + 1 < arguments.length && isInt(arguments[i + 1])) {
                    page = readPositiveInt(arguments[++i], "Page must be a positive integer.");
                }
                continue;
            }

            if (t.startsWith("pg/")) {
                if (seenPg) {
                    throw new InvalidArgumentInput("Page specified more than once. Use a single pg/<N>.");
                }
                seenPg = true;
                page = readPositiveInt(t.substring(3), "Page after pg/ must be above 1!" +
                        " Also remember no space after pg/ :) e.g pg/2 ");
                continue;
            }

            if (t.contains("/")) {
                throw new InvalidArgumentInput("Unknown flag: " + t + ". Use /help to see how to use view log :)");
            } else {
                throw new InvalidArgumentInput("Unexpected Argument: " + t +
                        " . Use /help to see how to use view log :)");
            }
        }

        return new Parsed(target, page, detailed);
    }

    private static boolean isInt(String s) {
        return s != null && INT.matcher(s).matches();
    }

    private static int readPositiveInt(String s, String err) throws InvalidArgumentInput {
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

    private static void validateMonth(int month) throws InvalidArgumentInput {
        if (month < 1 || month > 12) {
            throw new InvalidArgumentInput("Month must be between 1 and 12.");
        }
    }

    private static void validateYear(int year) throws InvalidArgumentInput {
        if (year < 2025 || year > 2099) {
            throw new InvalidArgumentInput("Month must be between 1 and 12.");
        }
    }

    private YearMonth parseYearMonthToken(String token) throws InvalidArgumentInput {
        String[] parts = token.split("/");
        if (parts.length != 2) {
            throw new InvalidArgumentInput("Use ym/<MM>/<YY>, e.g., ym/10/25.");
        }
        String mmStr = parts[0].trim();
        String yyStr = parts[1].trim();

        if (!isInt(mmStr) || !isInt(yyStr)) {
            throw new InvalidArgumentInput("Use digits only: ym/<MM>/<YY>, e.g., ym/10/25.");
        }

        int mm = Integer.parseInt(mmStr);
        int yy = Integer.parseInt(yyStr);


        if (yy < 0 || yy > 99) {
            throw new InvalidArgumentInput("Year must be 00..99 (two digits).");
        }

        int yyyy = 2000 + yy; // Simple rule: map 00..99 → 2000..2099
        validateYear(yyyy);    // your existing guard (e.g., 1970..2100)
        validateMonth(mm);
        return YearMonth.of(yyyy, mm);
    }

}

