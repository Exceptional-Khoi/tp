package seedu.fitchasers;
import seedu.fitchasers.Exceptions.InvalidArgumentInput;

import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class ViewLog {
    private final UI ui;                         // your existing UI class
    private final WorkoutManager workoutManager;
    public  static final int MINIMUN_PAGE_SIZE = 1;
    private int pageSize = 10;

    // Keeps the last full, filtered & sorted list so `/open <n>` can work after rendering.
    private List<Workout> lastFilteredSorted = List.of();

    public ViewLog(UI ui, WorkoutManager workoutManager) {
        this.ui = ui;
        this.workoutManager = workoutManager;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = Math.max(1, pageSize); //ensures page size is at least 1
    }

    /* ----------------------------- Core renderers ----------------------------- */

    /** Renders a month’s workouts with pagination. Sorting: newest first. */
    public void render(String requestedPage, boolean detailed) throws InvalidArgumentInput {
        List<Workout> sortedArray = new ArrayList<>(workoutManager.getWorkouts());
        int requestedPageNumber = 1;
        sortedArray.sort(Comparator.comparing(Workout::getWorkoutEndDateTime).reversed());
        this.lastFilteredSorted = sortedArray; // store for /open <n>
        try{
            if(requestedPage.equals("")){
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

    private void renderDetailedRow(int id, Workout w) {
        String dateLong = formatLong(w.getWorkoutEndDateTime());   // e.g., Monday 30th of June, 6:00 PM
        String dur = formatDuration(w.getDuration());
//        String type = safe(w.getType());
//        String tags = getTagsJoined(w);                  // "-" if none

        ui.showMessage("—".repeat(60));
        ui.showMessage(String.format("#%d  %s", id, safe(w.getWorkoutName())));
        ui.showMessage("Date     : " + dateLong);
        ui.showMessage("Duration : " + dur);
//        ui.showMessage("Type     : " + (type.isBlank() ? "-" : type));
//        ui.showMessage("Tags     : " + (tags.isBlank() ? "-" : tags));
        // Add more fields from Workout here (sets/reps, notes, RPE, etc.)
    }

    /* ------------------------------ Commands API ----------------------------- */

    /** For `/open <index>`: returns the workout from the last rendered set, 1-based index. */
    public Optional<Workout> openByIndex(int oneBasedIndex) {
        int i = oneBasedIndex - 1;
        if (i < 0 || i >= lastFilteredSorted.size()) {
            return Optional.empty();
        }
        return Optional.of(lastFilteredSorted.get(i));
    }

    /** `/view_log --search <text>` returns all matching 1-based indexes (name contains). */
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

    /** `/view_log --type strength` (or any tag). Falls back to type, then tags set. */
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

    /** `/view_log --summary` (optionally by tag). */
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
        if (page < MINIMUN_PAGE_SIZE) {
            return MINIMUN_PAGE_SIZE;
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
        String dow = dt.getDayOfWeek().getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.ENGLISH);
        int day = dt.getDayOfMonth();
        String mon = dt.getMonth().getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.ENGLISH);
        return String.format("%s %d %s", dow, day, mon);
    }

    /** e.g., "Monday 30th of June, 6:00 PM" */
    private static String formatLong(LocalDateTime dt) {
        String dow = dt.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        int d = dt.getDayOfMonth();
        String suffix = daySuffix(d);
        String mon = dt.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        int hr = dt.getHour();
        int min = dt.getMinute();
        int hr12 = (hr % 12 == 0) ? 12 : (hr % 12);
        String ampm = hr < 12 ? "AM" : "PM";
        return String.format("%s %d%s of %s, %d:%02d %s", dow, d, suffix, mon, hr12, min, ampm);
    }

    private static String daySuffix(int day) {
        if (day >= 11 && day <= 13) return "th";
        return switch (day % 10) {
        case 1 -> "st";
        case 2 -> "nd";
        case 3 -> "rd";
        default -> "th";
        };
    }

/*
    private static String getTagsJoined(Workout w) {
        Set<String> tags = getTags(w);
        if (tags.isEmpty()) return "-";
        return String.join(", ", tags);
    }

    // If you don't have tags yet, this stays empty. Once implemented, wire Workout#getTags().
    private static Set<String> getTags(Workout w) {
        Set<String> tags = w.getTags(); // if not implemented yet, return Set.of()
        return tags == null ? Set.of() : tags;
    }*/


    /* ----------------------- Tag template (optional) ------------------------- */

    /** Optional helper to auto-suggest tags like "strength", "leg day", "cardio". */
/*    public static Set<String> suggestTags(Workout w) {
        String name = safe(w.getWorkoutName()).toLowerCase(Locale.ENGLISH);
        Set<String> tags = new LinkedHashSet<>();
        if (name.matches(".*(squat|deadlift|bench|press|row).*")) tags.add("strength");
        if (name.matches(".*(run|jog|treadmill|cycle|bike|swim|rower).*")) tags.add("cardio");
        if (name.matches(".*(leg|squat|deadlift|calf|quad|hamstring).*")) tags.add("leg day");
        if (name.matches(".*(push|bench|overhead|press|tricep).*")) tags.add("push");
        if (name.matches(".*(pull|row|lat|bicep).*")) tags.add("pull");
        // merge with existing user-provided tags
        if (w.getTags() != null) tags.addAll(w.getTags());
        return tags;
    }
    */
}

