package seedu.fitchasers.ui;
import seedu.fitchasers.FileHandler;
import seedu.fitchasers.workouts.Workout;
import seedu.fitchasers.workouts.WorkoutManager;
import seedu.fitchasers.exceptions.InvalidArgumentInput;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class ViewLog {
    public static final int MINIMUM_PAGE_SIZE = 1;
    public static final int ARRAY_INDEX_OFFSET = 1;
    public static final int AFTER_ARG_CONST = 4;
    private static final Pattern INT = Pattern.compile("^-?\\d+$");
    private static UI ui = new UI();                         // your existing UI class
    private final WorkoutManager workoutManager;
    private int pageSize = 10;
    private FileHandler fileHandler;

    // Keeps the last full, filtered & sorted list so `/open <n>` can work after rendering.
    private List<Workout> lastFilteredSorted = List.of();

    public ViewLog(UI ui, WorkoutManager workoutManager) {
        ViewLog.ui = ui;
        this.workoutManager = workoutManager;
    }

    public ViewLog(UI ui, WorkoutManager workoutManager, FileHandler fileHandler) {
        ViewLog.ui = ui;
        this.workoutManager = workoutManager;
        this.fileHandler = fileHandler;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = Math.max(1, pageSize); //ensures page size is at least 1
    }

    /* ----------------------------- Core renderers ----------------------------- */

    /**
     * Renders the monthly workout log with pagination and flags.
     * <p>
     * Supported forms:
     * <ul>
     *   <li>/view_log -m &lt;month 1..12&gt; [page]</li>
     *   <li>/view_log -ym &lt;year&gt; &lt;month 1..12&gt; [page]</li>
     *   <li>Optional: -d (detailed view)</li>
     * </ul>
     *
     * Examples:
     * <pre>
     *   /view_log -m 10          // Oct (current year), page 1
     *   /view_log -m 10 2        // Oct (current year), page 2
     *   /view_log -ym 2024 10    // Oct 2024, page 1
     *   /view_log -ym 2024 10 2  // Oct 2024, page 2
     *   /view_log -m 10 -d       // Oct, detailed page 1
     * </pre>
     *
     * @param args raw argument string after the command name
     * @throws InvalidArgumentInput if flags or numbers are invalid
     */
    public void render(String args) throws InvalidArgumentInput {
        Parsed p = parseArgs(args);

        // Fetch month list (lazy-load), then sort newest first by end time (nulls last)
        ArrayList<Workout> monthList = fileHandler.getWorkoutsForMonth(p.ym);
        ArrayList<Workout> sorted = new ArrayList<>(monthList);
        sorted.sort(Comparator.comparing(
                Workout::getWorkoutEndDateTime,
                Comparator.nullsLast(Comparator.naturalOrder())
        ).reversed());

        this.lastFilteredSorted = sorted; // for /open <ID>

        int totalPages = computeTotalPages(sorted.size(), pageSize);
        int current = ensureValidPage(p.page);

        int start = (current - 1) * pageSize;
        int end = Math.min(start + pageSize, sorted.size());

        ui.showMessage(String.format("Workouts for %s (%d total) — Page %d/%d",
                p.ym, sorted.size(), current, Math.max(1, totalPages)));

        if (sorted.isEmpty()) {
            ui.showMessage("No workouts this month.");
            return;
        }

        if (!p.detailed) {
            ui.showMessage(String.format("%-4s %-20s %-22s %-10s", "ID", "Date", "Name", "Duration"));
        }

        for (int i = start; i < end; i++) {
            Workout w = sorted.get(i);
            int rowNum = i + 1; // 1-based across the month

            if (p.detailed) {
                renderDetailedRow(rowNum, w);
            } else {
                renderCompactRow(rowNum, w);
            }
        }

        ui.showMessage("Tip: /view_log -m 10 2 (next page Oct), /view_log --search run, /open <ID>.");
    }

    private void renderCompactRow(int id, Workout w) {
        String date = formatDayMon(w.getWorkoutEndDateTime());     // e.g., Mon 30 Jun
        String name = truncate(safe(w.getWorkoutName()), 22);
        String dur = formatDuration(w.getDuration()); // e.g., 1h 15m
        ui.showMessage(String.format("%-4d %-20s %-22s %-10s", id, date, name, dur));
    }

    private void renderDetailedRow(int id, Workout workout) {
        String dateLong = formatLong(workout.getWorkoutEndDateTime());   // e.g., Monday 30th of June, 6:00 PM
        String dur = formatDuration(workout.getDuration());

        ui.showMessage("—".repeat(60));
        ui.showMessage(String.format("#%d  %s", id, safe(workout.getWorkoutName())));
        ui.showMessage("Date     : " + dateLong);
        ui.showMessage("Duration : " + dur);
        //String type = safe(workout.getType());
        String tags = workout.getAllTags().toString();                  // "-" if none
        //ui.showMessage("Type     : " + (type.isBlank() ? "-" : type));
        ui.showMessage("Tags     : " + (tags.isBlank() ? "-" : tags));
        // Add more fields from Workout here (sets/reps, notes, RPE, etc.)

    }

    /* ------------------------------ Commands API ----------------------------- */

    public void openByIndex(int oneBasedIndex) throws InvalidArgumentInput {
        int i = oneBasedIndex - ARRAY_INDEX_OFFSET;
        if (i < 0 || i >= lastFilteredSorted.size()) {
            throw new InvalidArgumentInput("The number you requested is out of bounds! Please try again. ");
        }
        ui.displayDetailsOfWorkout(lastFilteredSorted.get(i));
    }

    /*public List<Integer> searchByName(List<Workout> monthWorkouts, String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        String needle = query.toLowerCase();
        List<Workout> sorted = monthWorkouts.stream()
                .sorted(Comparator.comparing(Workout::getDateTime).reversed())
                .toList();

        this.lastFilteredSorted = sorted; // set context for `/open <n>`
        List<Integer> matches = new ArrayList<>();
        for (int i = 0; i < sorted.size(); i++) {
            Workout w = sorted.get(i);
            if (safe(w.getWorkoutName()).toLowerCase().contains(needle)) {
                matches.add(i + 1); // 1-based
            }
        }

        // Display results
        if (matches.isEmpty()) {
            ui.showMessage("No workouts matched \"" + query + "\".");
        } else {
            ui.showMessage("Matches for \"" + query + "\":");
            ui.showMessage(String.format("%-4s %-20s %-22s %-10s", "ID", "Date", "Name", "Duration"));
            for (Integer idx : matches) {
                renderCompactRow(idx, sorted.get(idx - 1));
            }
        }
        return matches;
    } */

    /*public void renderByTag(List<Workout> monthWorkouts, String tag, int page, boolean detailed) {
        if (tag == null || tag.isBlank()) {
            ui.showMessage("Please provide a tag, e.g., /view_log --type strength");
            return;
        }
        String needle = tag.toLowerCase();

        List<Workout> filtered = monthWorkouts.stream()
                .filter(w -> safe(w.getType()).equalsIgnoreCase(tag) ||
                        getTags(w).stream().anyMatch(t -> t.equalsIgnoreCase(needle)))
                .sorted(Comparator.comparing(Workout::getDateTime).reversed())
                .toList();

        if (filtered.isEmpty()) {
            ui.showMessage("No workouts with tag/type \"" + tag + "\" this month.");
            return;
        }

        this.lastFilteredSorted = filtered;
        render(filtered, page, detailed);
    } */

    /*public void renderSummary(List<Workout> monthWorkouts, Optional<String> tagOpt) {
        List<Workout> scope = monthWorkouts;
        if (tagOpt.isPresent()) {
            String tag = tagOpt.get();
            scope = monthWorkouts.stream()
                    .filter(w -> safe(w.getType()).equalsIgnoreCase(tag) ||
                            getTags(w).stream().anyMatch(t -> t.equalsIgnoreCase(tag)))
                    .toList();
            ui.showMessage("Summary for tag/type \"" + tag + "\":");
        } else {
            ui.showMessage("Monthly summary:");
        }

        int sessions = scope.size();
        int totalMinutes = scope.stream().mapToInt(Workout::getDurationMinutes).sum();

        ui.showMessage("Total sessions : " + sessions);
        ui.showMessage("Total duration : " + formatDuration(totalMinutes));

        // PR hooks (template) — implement when you track PRs.
        // Example: group by workout name and show best duration/weight/etc.
        Map<String, Integer> bestByName = new HashMap<>();
        for (Workout w : scope) {
            String name = safe(w.getWorkoutName());
            // Replace with your actual PR metric (e.g., weight lifted, fastest time, etc.)
            int metric = w.getDurationMinutes();
            bestByName.merge(name, metric, Math::max);
        }
        if (!bestByName.isEmpty()) {
            ui.showMessage("Top PRs (by duration as placeholder):");
            bestByName.entrySet().stream()
                    .sorted(Map.Entry.<String,Integer>comparingByValue().reversed())
                    .limit(5)
                    .forEach(e -> ui.showMessage(" • " + e.getKey() + " — " + e.getValue() + " min"));
        }
    } */

    /* ------------------------------ Helpers/Util ----------------------------- */

    public static int computeTotalPages(int size, int pageSize) {
        return (int) Math.ceil(Math.max(0, size) / (double) Math.max(1, pageSize));
    }

    private int ensureValidPage(int page) {
        int totalPages = computeTotalPages(this.workoutManager.getWorkoutSize(), pageSize);
        if (page < MINIMUM_PAGE_SIZE) {
            return MINIMUM_PAGE_SIZE;
        }
        return Math.min(page, totalPages);
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private static String truncate(String s, int max) {
        return s.length() <= max ? s : s.substring(0, Math.max(0, max - 1)) + "…"; //Used to shorten name description
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
            return "Unknown date";
        }
        String dow = dt.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        int day = dt.getDayOfMonth();
        String mon = dt.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        return String.format("%s %d %s", dow, day, mon);
    }

    private static String formatLong(LocalDateTime dt) {
        if (dt == null) {
            return "Unknown date";
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
    private static final class Parsed {
        final YearMonth ym;
        final int page;
        final boolean detailed;

        Parsed(YearMonth ym, int page, boolean detailed) {
            this.ym = ym;
            this.page = page;
            this.detailed = detailed;
        }
    }

    /**
     * Parses flags/args into a structured form with validation/guards.
     */
    private Parsed parseArgs(String raw) throws InvalidArgumentInput {
        YearMonth now = YearMonth.now();
        YearMonth target = now;
        int page = 1;
        boolean detailed = false;

        if (raw == null || raw.isBlank()) {
            return new Parsed(target, page, false);
        }

        String[] tok = raw.trim().split("\\s+");
        boolean seenMonthFlag = false;  // -m
        boolean seenYearMonthFlag = false; // -ym

        for (int i = 0; i < tok.length; i++) {
            String t = tok[i];

            switch (t) {
            case "-d":
            case "--detailed":
                detailed = true;
                break;

            case "-m":
                if (seenYearMonthFlag) {
                    throw new InvalidArgumentInput("Cannot combine -m with -ym.");
                }
                seenMonthFlag = true;
                int month = readIntArg(tok, ++i, "Missing month after -m");
                validateMonth(month);
                // default to current year when -m is used
                target = YearMonth.of(now.getYear(), month);
                // optional page next
                if (i + 1 < tok.length && isInt(tok[i + 1])) {
                    page = readPositiveInt(tok[++i], "Page must be a positive integer.");
                }
                break;

            case "-ym":
                if (seenMonthFlag) {
                    throw new InvalidArgumentInput("Cannot combine -ym with -m.");
                }
                seenYearMonthFlag = true;
                int year = readIntArg(tok, ++i, "Missing year after -ym");
                validateYear(year);
                int m = readIntArg(tok, ++i, "Missing month after year");
                validateMonth(m);
                target = YearMonth.of(year, m);
                // optional page next
                if (i + 1 < tok.length && isInt(tok[i + 1])) {
                    page = readPositiveInt(tok[++i], "Page must be a positive integer.");
                }
                break;

            default:
                // allow trailing page as bare number (e.g., "/view_log 2")
                if (isInt(t)) {
                    page = readPositiveInt(t, "Page must be a positive integer.");
                } else if (t.startsWith("-")) {
                    throw new InvalidArgumentInput("Unknown flag: " + t);
                } else {
                    throw new InvalidArgumentInput("Unexpected token: " + t);
                }
            }
        }

        return new Parsed(target, page, detailed);
    }

    private static boolean isInt(String s) {
        return s != null && INT.matcher(s).matches();
    }

    private static int readIntArg(String[] tok, int idx, String err) throws InvalidArgumentInput {
        if (idx >= tok.length || !isInt(tok[idx])) {
            throw new InvalidArgumentInput(err);
        }
        return Integer.parseInt(tok[idx]);
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
        if (year < 1970 || year > 2100) {
            throw new InvalidArgumentInput("Year must be between 1970 and 2100.");
        }
    }

}

