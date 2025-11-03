# User Guide

## Introduction
### FitChasers User Guide
FitChasers is a desktop app for managing your fitness journey, optimized for use via a Command Line Interface (CLI) 
while still having the benefits of a Graphical User Interface (GUI). If you can type fast, ss can help you 
track workouts, monitor weight progress, and achieve your fitness goals faster than traditional GUI apps.

### Interface Display (Console Width)

FitChasers' chat-style interface is optimized for clear and readable text bubbles within the console window.  
The system assumes a console width of approximately 150 characters, meaning each line of text can display up to 150 visible characters before wrapping automatically.

- This width defines how much text can appear in one line within chat bubbles.
- Messages longer than this limit are automatically wrapped to the next line.
- If your terminal window is narrower than 150 characters, the chat bubbles may wrap early or appear slightly misaligned.
- You can resize your terminal window to ensure proper alignment.

> **Notes:**  
> The `150` refers to **the number of text characters**, not pixels.  
> Each printed character (including spaces and punctuation) counts toward this width limit.

## Quick Start

1. Ensure that you have Java 17 or above installed.
2. Down the latest version of `FitChasers`.
3. Copy the file to the folder you want to use as the home folder for your FitChasers data.
4. Open a command terminal, cd into the folder you put the jar file in, and use the java -jar 
   FitChasers.jar command to run the application.
5. On first launch, you will be prompted to enter your name and initial weight.
6. Type the command in the command box and press Enter to execute it. e.g. typing `/help` and pressing Enter 
   will open the help window.

## Features
* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  e.g. in `/create_workout n/NAME`, `NAME` is a parameter which can be used as `/create_workout n/Chest Day`.

[//]: # (* Extraneous parameters for commands that do not take in parameters &#40;such as `/help`, `/exit`&#41; will be ignored.<br>)
[//]: # (  e.g. if the command specifies help 123, it will be interpreted as help.)

* Date format is `DD/MM/YY` (e.g., `30/10/25` for `October 30, 2025`). 
* Time format is `HHMM` in 24-hour format (e.g., `1430` for `2:30 PM`).

### Viewing help: `/help`
Shows a message explaining how to use FitChasers and all available commands.

Format: `/help`

Alternative: `h`

### Changing Your Name: `/rename`
This command allows you to update your display name in FitChasers. The name is saved automatically and will be shown whenever you start the program.

Format: `/rename n/NAME`

Alternative: `rn`

* `NAME`:
  - Must contain only letters (A–Z, a–z), numbers (0–9), spaces, underscores (_), or dashes (-). 
  - Must be 1–30 characters long. 
  - Cannot be empty or just whitespace.

Examples:
- `/rename n/Nitin`
- `/rename n/Nitin Ni`
- `/rename n/FitChaser_User-1`

### Adding weight: `/add_weight`
Logs your weight for a specific date. You can record one or multiple weights per day to monitor changes over time.

Format: `/add_weight w/WEIGHT [d/DATE]`

Rules:

* `WEIGHT`: A numeric value (integer or decimal) in kilograms
  - Must be between 20.0 and 500.0 (for realistic human body weight range).
  - All weights are rounded up to 1 decimal place. 
  - Due to floating-point precision in Java, extremely close values such as 19.999999999999999 or 500.000000000000001 
  will be rounded into the valid range and accepted. This behaviour is expected and does not affect normal usage.
  - Must not contain symbols or units (e.g., kg is not accepted).
  - Must input weight.


* `DATE`: Must follow DD/MM/YY format. 
  - Defaults to today's date if not provided.
  - Day must be `01` to `31`.
  - Month must be `01` to `12`.
  - Year must be two digits (e.g., `25` for 2025).
  - Date must not be in the future relative to the system date.

**Note:** You can log multiple weight entries for the same date but the application only saves the latest entry.

Example:

* `/add_weight w/75 d/30/10/25`
* `/add_weight w/74.5 d/28/10/25`

Alternative: `aw`

### Viewing weight: `/view_weight`
Displays your recorded weight entries in chronological order.

Format: `/view_weight`

**Notes:** 
- You can log multiple weight entries for the same date but the application only saves the latest entry.
- Extraneous parameters for commands that do not take in parameters will be ignored.<br>
    e.g. if the command specifies /view_weight 123, it will be interpreted as /view_weight.

Alternative: `vw`

### Setting a goal weight: `/set_goal`
Lets you specify a target weight to monitor your progress.

Format: `/set_goal w/TARGET_WEIGHT`

- `TARGET_WEIGHT` is your goal weight. Rules: Same as `WEIGHT` parameter rules above.

**Note:** When you set a goal, FitChaser automatically uses your latest recorded weight (the most recent `/add_weight` entry) as your starting weight.
This ensures that your progress is calculated accurately based on your most up-to-date data.

Example:

* `/set_goal w/70.0`

Alternative: `sg`

### Viewing a goal weight: `/view_goal`
Displays your current goal weight, your latest recorded weight, and how far you are from reaching your target.

Format: `/view_goal`

Alternative: `vg`

### Creating a workout: `/create_workout`
Starts a new workout session.

Format: `/create_workout n/WORKOUT_NAME d/DATE t/TIME`

- `WORKOUT_NAME` is the name of your workout (e.g., "Chest Day", "Morning Run"). 
  - Rules: Same as `NAME` parameter rules above.
  - No duplicates on the same date.
- `DATE` is in `DD/MM/YY` format. Rules: Same as `DATE` parameter rules above.
- `TIME` is in `HHMM` format (24-hour, e.g., `1430` for 2:30 PM).

**Notes:** 
- You can create multiple workouts on the same day with different times. 
However, you cannot create 2 workouts that have overlapping time. e.g. if you have a workout from 1400 to 1500, 
you cannot create another workout starting at 1430 but you can create one starting at 1500.
- You can only create workouts with a start time from the current time (according to your computer system).
- FitChasers only operates for workout logs dated between the month of activation in 2025 and December 2099.

Example:

* `/create_workout n/Chest Day d/30/10/25 t/1430`

Alternative: `cw`

### Adding an exercise: `/add_exercise`
Adds an exercise to your current workout session.

Format: `/add_exercise n/EXERCISE_NAME r/REPS`

- `EXERCISE_NAME` is the exercise name. Rules: Same as `NAME` parameter rules above.
- `REPS` is a number represents repetition for a set (e.g., `15`). It must be an integer less than 1000.

Example:

- `/add_exercise n/Deadlift r/5`

Alternative: `ae`

### Adding a set: `/add_set`
Adds another set to the last exercise in your current workout.

Format: `/add_set r/REPS`

- `REPS` is the number of repetitions for this set.

Examples:
- `/add_set r/10`
- `/add_set r/12`

Alternative: `as`

### Ending a workout: `/end_workout`
Completes your current workout session and saves it.

Format: `/end_workout d/DATE t/TIME`

- `DATE` is in `DD/MM/YY` format. Rules: Same as `DATE` parameter rules above.
- `TIME` is in `HHMM` format.

Example:

- `/end_workout d/30/10/25 t/1500`

Alternative: `ew`

### Adding Modality group keyword: `/add_modality_tag`
Adds a new keyword to extend the automatic tagging system for workout modalities (cardio, strength).

Format: `/add_modality_tag m/MODALITY k/KEYWORD`
* The `MODALITY` must be either `CARDIO` or `STRENGTH`.
* The `KEYWORD` should be lowercase and represent an exercise type (e.g., "jump_rope").
  
Valid Modalities:

`CARDIO` - For cardio exercises like running, swimming, cycling

`STRENGTH` - For strength training exercises like lifting, pressing, squatting

Examples:

- `/add_modality_tag m/CARDIO k/jump_rope`
- `/add_modality_tag m/STRENGTH k/pilates`

Alternative: `amot`

#### Adding Muscle Group Keywords: `/add_muscle_tag`
Adds a new keyword to extend the automatic tagging system for muscle groups (legs, chest, back, etc.).

Format: `/add_muscle_tag m/MUSCLE_GROUP k/KEYWORD`
* The `MUSCLE_GROUP` must be one of: `LEGS, POSTERIOR_CHAIN, CHEST, BACK, SHOULDERS, ARMS, CORE`.
* The `KEYWORD` should be lowercase and represent an exercise targeting that muscle group (e.g., "lunges", "squats").
  
Valid Muscle Groups:

`LEGS` - Leg exercises like squats, lunges, leg presses

`POSTERIOR_CHAIN` - Posterior chain exercises like deadlifts

`CHEST` - Chest exercises like bench press, push-ups

`BACK` - Back exercises like rows, pull-ups

`SHOULDERS` - Shoulder exercises like overhead press

`ARMS` - Arm exercises like curls extension

`CORE` - Core exercises like planks, abs work

Examples:
- `/add_muscle_tag m/LEGS k/lunges`
- `/add_muscle_tag m/CHEST k/push_ups`

Alternative: `amt`

### Overriding Workout Tags: `/override_workout_tag`
Manually replaces the tags of a specific workout with a single new tag, clearing all auto-generated tags and manual tags.

Format: `/override_workout_tag id/WORKOUT_ID newTag/TAG_NAME`
* The `WORKOUT_ID` is the index number of the workout (use `/view_log` to see IDs).
* The `TAG_NAME` can be any custom tag (e.g., "cardio", "strength", "recovery", "custom_tag"). Rules: Same as `NAME` parameter rules above.
* The command clears both auto-generated and manual tags, replacing them with only the specified tag.
* Changes are saved immediately to persistent storage.

Examples:
- `/override_workout_tag id/1 newTag/strength`
- `/override_workout_tag id/3 newTag/legs`

Alternative: `owt`

### Finding gyms by exercise: `/gym_where`
Searches for nearby NUS gyms that have equipment for a specific exercise.

Format: `/gym_where n/EXERCISE_NAME`
* Ensure workout has ended before you modify the workout tag.

* `EXERCISE_NAME` is the exercise you want to do. Rules: Same as `NAME` parameter rules above.

Examples:
- `/gym_where n/deadlift`
- `/gym_where n/treadmill`

Alternative: `gw`

### Viewing gym equipment: `/gym_page`
Shows the available equipment at a specific NUS gym.

Format: `/gym_page p/PAGE_NUMBER_OR_GYM_NAME`

* `PAGE_NUMBER_OR_GYM_NAME` can be a number (1-N) or a gym name. Rules: Same as `NAME` parameter rules above.

Examples:
- `/gym_page p/1` - Shows equipment at the first gym
- `/gym_page p/SRC Gym` - Shows equipment at SRC Gym

Alternative: `gp`

### Viewing workout log: `/view_log`
Displays a list of your workouts, typically for the current month.

Format: `/view_log`

Parameters:
* -m Month [PAGE] - View workouts for a specific month in the current year
  * `MONTH` must be `1-12` (e.g., 10 for October)
  * Optional `PAGE` for pagination (default is page 1)
* `-ym YEAR MONTH [PAGE]` - View workouts for a specific year and month
  * `YEAR` must be a two-digit year (e.g.`25` for 2025)
  * `MONTH` must be `1-12`
* `[PAGE]` - Navigate to a specific page of the current month's workouts
  * `PAGE` must be a positive integer
* `-d` - Display workouts in detailed view with full exercise information
Default Behavior: When called without parameters, shows the current month's workouts (page 1).

Alternative: `vl`

### Opening a workout: `/open`
Opens and displays detailed information about a specific workout by its index in the current list.

Format: `/open INDEX`

- `INDEX` is the number of the workout in the displayed list.
- The index must be a positive integer `1, 2, 3 ...`

Examples:
- `/open 1 - Opens the first workout`
- `/open 3 - Opens the third workout`

Alternative: `o`

### Deleting Workouts: `/delete_workout`
Deletes the specified workout from your workout history by its display ID.

Format: `/delete_workout id/INDEX`

* `INDEX` is the positive ID number of the workout you want to delete, as shown in `/view_log`.

Examples:
* `/delete_workout id/8`
* `/delete_workout id/2`


* Alternative: `dw`

### Exiting the program: `/exit`
Exits FitChasers and saves all your data.

Format: `/exit`

Alternative: `e`

**Note:** Extraneous parameters for commands that do not take in parameters will be ignored.<br>
e.g. if the command specifies /exit 123, it will be interpreted as /exit.

### Saving the data
FitChasers data is saved automatically after any command that changes the data. There is no need to save manually.

Data is organized by month and stored in the data/ folder in your FitChasers home directory.


## FAQ

**Q**: How do I transfer my data to another computer? 

**A**: Install FitChasers on the other computer and copy the entire data/ folder from your current FitChasers home 
directory to the new computer's FitChasers installation. The next time you run FitChasers, it will load all your 
saved data.

**Q**: What happens if I enter an invalid date format?

**A**: FitChasers will display an error message and ask you to re-enter the command with the correct `DD/MM/YY` format.

**Q**: Can I have multiple workouts on the same day?

**A**: Yes! FitChasers allows you to create and save multiple workouts on the same date. You can even specify 
different times for each workout.

**Q**: How are tags automatically assigned?

**A**: FitChasers uses keyword matching to automatically assign modality and muscle group tags based on your workout 
and exercise names. You can customize keywords using /add_modality_tag and /add_muscle_tag commands.

**Q**: What are the valid values for muscle groups?

**A**: The valid muscle groups are: `LEGS, POSTERIOR_CHAIN, CHEST, BACK, SHOULDERS, ARMS, and CORE`.

## Command Summary

| Action               | Format, Examples                                                                                |
|----------------------|-------------------------------------------------------------------------------------------------|
| **Add Exercise**     | `/add_exercise n/NAME r/REPS`<br>e.g., `/add_exercise n/Push Ups r/15`                          |
| **Add Modality Tag** | `/add_modality_tag m/MODALITY k/KEYWORD`<br>e.g., `/add_modality_tag m/CARDIO k/running`        |
| **Add Muscle Tag**   | `/add_muscle_tag m/MUSCLE_GROUP k/KEYWORD`<br>e.g., `/add_muscle_tag m/LEGS k/squat`            |
| **Add Set**          | `/add_set r/REPS`<br>e.g., `/add_set r/10`                                                      |
| **Add Weight**       | `/add_weight w/WEIGHT d/DATE`<br>e.g., `/add_weight w/75 d/30/10/25`                            |
| **Create Workout**   | `/create_workout n/NAME d/DATE t/TIME`<br>e.g., `/create_workout n/Chest Day d/30/10/25 t/1430` |
| **Delete Workout**   | `/del_workout WORKOUT_NAME` or `/del_workout d/DATE`<br>e.g., `/del_workout Chest Day`          |
| **End Workout**      | `/end_workout d/DATE t/TIME`<br>e.g., `/end_workout d/30/10/25 t/1500`                          |
| **Exit**             | `/exit` or `e`                                                                                  |
| **Gym Page**         | `/gym_page p/PAGE_OR_NAME`<br>e.g., `/gym_page p/1` or `/gym_page p/SRC Gym`                    |
| **Gym Where**        | `/gym_where n/EXERCISE`<br>e.g., `/gym_where n/deadlift`                                        |
| **Help**             | `/help` or `h`                                                                                  |
| **Open Workout**     | `/open INDEX`<br>e.g., `/open 1`                                                                |
| **Override Tag**     | `/override_workout_tag id/ID newTag/TAG`<br>e.g., `/override_workout_tag id/1 newTag/strength`  |
| **Rename**           | `/rename n/NAME`<br>e.g., `/rename n/John Doe`                                                  |
| **Set Goal**         | `/set_goal w/TARGET_WEIGHT`<br>e.g., `/set_goal w/70`                                           |
| **View Goal**        | `/view_goal` or `vg`                                                                            |
| **View Log**         | `/view_log`<br>e.g., `/view_log`                                                                |
| **View Weight**      | `/view_weight` or `vw`                                                                          |
