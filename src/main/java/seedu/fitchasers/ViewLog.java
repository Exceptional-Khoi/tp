package seedu.fitchasers;
import seedu.fitchasers.exceptions.InvalidArgumentInput;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ViewLog {
    public  static final int MINIMUM_PAGE_SIZE = 1;
    public static final int ARRAY_INDEX_OFFSET = 1;
    private static UI ui = new UI();                         // your existing UI class
    private final WorkoutManager workoutManager;
    private int pageSize = 10;
    private final int DETAILED_ARG_CONST = 3;

    // Keeps the last full, filtered & sorted list so `/open <n>` can work after rendering.
    private List<Workout> lastFilteredSorted = List.of();

    public ViewLog(UI ui, WorkoutManager workoutManager) {
        ViewLog.ui = ui;
        this.workoutManager = workoutManager;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = Math.max(1, pageSize); //ensures page size is at least 1
    }

    /* ----------------------------- Core renderers ----------------------------- */

    /** Renders a month’s workouts with pagination. Sorting: newest first. */
    public void render(String requestedPage) throws InvalidArgumentInput {
        boolean detailed = false;
        int requestedPageNumber;

        if(requestedPage.contains("-d")){
            detailed = true;
            requestedPage = requestedPage.substring (requestedPage.indexOf("/d") + DETAILED_ARG_CONST);
        }
        List<Workout> sortedArray = new ArrayList<>(workoutManager.getWorkouts());
        sortedArray.sort(Comparator.comparing(Workout::getWorkoutEndDateTime).reversed());
        this.lastFilteredSorted = sortedArray; // store for /open <n>
        try{
            if(requestedPage.isEmpty()){
                requestedPage = "1";
            }
            requestedPageNumber = Integer.parseInt(requestedPage);
        }catch(NumberFormatException e){
            throw new InvalidArgumentInput("Page number must be an integer");
        }
        int currentPage = ensureValidPage(requestedPageNumber);
        int startIndex = (currentPage - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, sortedArray.size());
        int totalPages = computeTotalPages(sortedArray.size(), pageSize);


        ui.showMessage(String.format("Workouts (%d total) — Page %d/%d", sortedArray.size(), currentPage, totalPages));
        if (sortedArray.isEmpty()) {
            ui.showMessage("No workouts this month.");
            return;
        }

        if (!detailed) {
            ui.showMessage(String.format("%-4s %-20s %-22s %-10s", "ID", "Date", "Name", "Duration"));
        }

        for (int i = startIndex; i < endIndex; i++) {
            Workout w = sortedArray.get(i);
            int rowNum = i + 1; // 1-based global index across the month

            if (detailed) {
                renderDetailedRow(rowNum, w);
            } else {
                renderCompactRow(rowNum, w);
            }
        }

        ui.showMessage("Tip: /view_log 2 (next page), /view_log --search run, /open <ID> for details.");
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
        String tags = workout.getTags().toString();                  // "-" if none
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
        String dow = dt.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        int day = dt.getDayOfMonth();
        String mon = dt.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        return String.format("%s %d %s", dow, day, mon);
    }

    private static String formatLong(LocalDateTime dt) {
        String dow = dt.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        int d = dt.getDayOfMonth();
        String suffix = UI.getDaySuffix(d);
        String mon = dt.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        int hr = dt.getHour();
        int min = dt.getMinute();
        int hr12 = (hr % 12 == 0) ? 12 : (hr % 12);
        String ampm = hr < 12 ? "AM" : "PM";
        return String.format("%s %d%s of %s, %d:%02d %s", dow, d, suffix, mon, hr12, min, ampm);
    }

}

