# Developer Guide

## Table of Contents

1. [Acknowledgements](#acknowledgements)
2. [Setting up, Getting Started](#setting-up-getting-started)
3. [Design](#Design-&-implementation)
    - [Architecture](#architecture)
    - [UI Component](#ui-component)
    - [CaseFile Component](#casefile-component)
    - [Command Component](#command-component)
    - [Storage Component](#storage-component)
    - [Common](#common)
4. [Implementation](#implementation)
5. [Appendix A: Product Scope](#appendix-a-product-scope)
    - [Target user profile](#target-user-profile)
    - [Value proposition](#value-proposition)
6. [Appendix B: User Stories](#appendix-b-user-stories)
7. [Appendix C: Non-Functional Requirements](#appendix-c-non-functional-requirements)
8. [Appendix D: Glossary](#appendix-d-glossary)
9. [Appendix E: Instructions for Manual Testing](#appendix-e-instructions-for-manual-testing)

---

## Acknowledgements

{list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well}

## Design & implementation

### Design
The Architecture Diagram given above explains the high-level design of FitChaser.
Given below is a quick overview of main components and how they interact with each other.

#### Main components of the architecture
![Alt text](docs/diagrams/FitChaser_Architecture.jpg "Basic Architecture")
FitChasers (consisting of classes FitChasers and Managers) is in charge of the app launch and shut down.
At app launch, it initializes and loads the components and data in the correct sequence, and connects them up with each other.
At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app’s work is done by the following six components:
1) UI: The UI of the App.
2) Logic: The command executor
3) FileHandler: Reads data from, and writes data to, the hard disk.
4) ViewLog: Displays advance viewing features like summaries, or grouped data in pages
5) Managers: Records, alter and save data on relevant classes 
6) Models: Instantiatable objects that is managed by managers  
7) Commons: represents a collection of classes used by multiple other components.


How the architecture components interact with each other

The Sequence Diagram below shows how the components interact with each other for the scenario where the user issues the command /create_workout pushup.


## Product scope
### Target user profile

FitChasers is designed for individuals who want to track and improve their fitness progress effectively.
Our target users include:

1. University students and working adults who want a lightweight fitness tracker without complex setup.
2. Users who prefer a command-line interface for fast and distraction-free input.
3. Fitness enthusiasts who want to log, view, and analyze workouts and body data (e.g., weight, reps, sets).
4. People who value privacy and prefer storing their data locally instead of using cloud-based apps.
5. Beginners who want simple, guided commands to build consistent fitness habits.

### Value proposition

{Describe the value proposition: what problem does it solve?}

FitChasers provides an intuitive, local, and command-based way to track your workouts and progress over time.
Unlike most fitness apps that require constant internet access or sign-ins, FitChasers focuses on simplicity and data ownership.
It helps users build consistency by making fitness tracking fast and rewarding, using summaries and visualization features.
The system ensures data integrity and transparency — users can view, export, and back up their own fitness records anytime.
Overall, FitChasers empowers users to understand their progress and stay motivated without unnecessary complexity.

## User Stories

|Version| As a ... | I want to ... | So that I can ...|
|--------|----------|---------------|------------------|
|v1.0|new user|see usage instructions|refer to them when I forget how to use the application|
|v2.0|user|find a to-do item by name|locate a to-do without having to go through the entire list|

## Non-Functional Requirements

- The system should respond to commands within 1 second under normal usage.
- The app should be able to handle at least 1,000 workout records without noticeable lag.
- The code should follow standard Java coding conventions and maintain >80% JUnit test coverage.
- The data should be saved automatically upon exit to prevent accidental loss.
- The system should be platform-independent (tested on Windows, macOS, Linux).
- Error messages must be clear, consistent, and user-friendly.
- The system should launch without internet connectivity.

## WorkoutManager component
**API**: [`WorkoutManager.java`](https://github.com/AY2526S1-CS2113-W14-3/tp/blob/master/src/main/java/seedu/fitchasers/WorkoutManager.java)

The `WorkoutManager` component is responsible for managing all workout-related operations in FitChasers, including 
workout creation, exercise tracking, and workout history management.
![Alt text](docs/diagrams/WorkoutManager_class_diagram.png "Basic Architecture")
### Overview
The `WorkoutManager` acts as the central controller for workout operations. It maintains a list of completed workouts 
and tracks the current active workout session. 
The component handles:
* Creating and ending workout
* Adding exercises and sets to activa workouts
* Managing workout history and persistence
* Tag generation and management integration
* Workout deletion and viewing



## Glossary
| Term                         | Definition                                                                                     |
| ---------------------------- | ---------------------------------------------------------------------------------------------- |
| **Active Workout**           | The workout session that is currently in progress.                                             |
| **Command**                  | A user instruction starting with a verb (e.g., `/add_weight`, `/create_workout`, `/view_log`). |
| **Completed Workout**        | A workout session that has been ended and saved to history.                                    |
| **Date format**              | `dd/MM/yy`, e.g., `26/10/25` (used with `d/`).                                                 |
| **Exercise**                 | A specific physical activity performed during a workout (e.g., bench press, push-ups, squats). |
| **FileHandler**              | Component responsible for persistence (reading/writing app data on disk).                      |
| **FitChasers (app)**         | The main application that wires UI, logic, and storage; runs the command loop.                 |
| **Manager**                  | Service object that encapsulates a feature area (e.g., `WeightManager`, `WorkoutManager`).     |
| **Modality**                 | The type of exercise (e.g., cardio, strength).                                                 |
| **Muscle Group**             | The primary muscles targeted by an exercise (e.g., legs, chest).                               |
| **Parameter token / Prefix** | Short marker introducing an argument (e.g., `n/` name, `d/` date, `t/` time, `w/` weight).     |
| **Parsing**                  | Converting raw command text into a structured request (command + arguments).                   |
| **Persistence**              | Saving/loading data between runs (handled by `FileHandler`).                                   |
| **Person**                   | Domain entity representing the user; owns profile and histories.                               |
| **Prompt**                   | The UI input line where the user types commands.                                               |
| **Repetition (Rep)**         | A single complete motion of an exercise.                                                       |
| **Set (workout)**            | A group of consecutive repetitions of an exercise (e.g., “Set 2 → Reps: 14”).                  |
| **Storage**                  | The persistence layer; the files managed by `FileHandler`.                                     |
| **Tag**                      | A label assigned to workouts for categorization (e.g., `cardio`, `strength`).                  |
| **Time format**              | `HHmm` 24-hour time for `t/` tokens (e.g., `1900` = 7:00 PM).                                  |
| **UI**                       | Presentation layer handling all user I/O (printing messages, reading input).                   |
| **Validation**               | Checks that inputs satisfy constraints (e.g., numeric weight, date not in the future).         |
| **ViewLog**                  | Component that formats and displays logs/history for the user.                                 |
| **WeightManager**            | Feature service for creating, validating, and listing weight entries.                          |
| **WeightRecord**             | Domain object holding a single weight entry (value + date).                                    |
| **Workout**                  | A session of physical exercise consisting of various exercises and sets.                       |

# Instructions for manual testing
## Help

### Success Cases

```
/help → shows all commands

h     → shows all commands
```

### Error Cases

```
(n/a)
```

**Usage:** `/help` (alias: `h`)

---

## Set Name

### Success Cases

```
/rename n/Nitin → success (name saved)

rn n/Ana   → success
```

### Error Cases

```
/my_name           → error (missing n/)
/my_name n/       → error (empty name)
/my_name n/<31+ chars> → error (name too long; max 30)
```

**Usage:** `/rename n/<name>` (alias: `rn`)

---

## Weights

### Success Cases

```
/add_weight w/70.5 d/23/10/25 → success

aw w/70.5 d/23/10/25 → success
```

### Error Cases

```
/add_weight w/invalid d/23/10/25     → error (invalid weight)
/add_weight w/70.5 d/invalid/date    → error (invalid date)
/add_weight w/ d/23/10/25            → error (missing weight value)
/add_weight w/70.5 d/<future date>   → error (date cannot be in the future)
```

**Usage:** `/add_weight w/<weight> d/<dd/MM/yy>` (alias: `aw`)

---

## View Weight

### Success Cases

```
/view_weight → shows weight history and graph

vw           → shows weight history and graph
```

### Error Cases

```
(none; if no data) → "<name> has no weight records yet."
```

**Usage:** `/view_weight` (alias: `vw`)

---

## Create Workout

### Success Cases

```
/create_workout n/Push d/23/10/25 t/0700 → success (active workout created)

cw n/Push d/23/10/25 t/0700 → success (active workout created)
```

### Error Cases

```
/create_workout                          → error (missing n/)
/create_workout n/                       → error (empty name)
/create_workout n/Push d/23/10/25        → error (missing t/)
/create_workout n/Push t/0700            → error (missing d/)
/create_workout n/Push d/32/10/25 t/0700 → error (invalid date)
/create_workout n/Push d/23/10/25 t/2460 → error (invalid time)

[when a workout is already active]
/create_workout n/Arms d/23/10/25 t/0900 → error (cannot create while another workout is active)
```

**Usage:** `/create_workout n/<name> d/<dd/MM/yy> t/<HHmm>` (alias: `cw`)

---

## Add Exercise

### Success Cases

```
/add_exercise n/PushUp r/12 → success (adds exercise to active workout)

ae n/PushUp r/12 → success (adds exercise to active workout)
```

### Error Cases

```
/add_exercise                 → error (missing n/ and r/)
/add_exercise n/PushUp        → error (missing r/)
/add_exercise n/PushUpr/12    → error (needs space before r/)
/add_exercise n/ r/12         → error (empty name)
/add_exercise n/PushUp r/x12  → error (invalid reps)

[no active workout]
/add_exercise n/PushUp r/12  → error (no active workout)
```

**Usage:** `/add_exercise n/<exercise_name> r/<reps>` (alias: `ae`)

---

## Add Set

### Success Cases

```
/add_set r/15 → success (appends set to latest exercise)

as r/15 → success (appends set to latest exercise)
```

### Error Cases

```
/add_set         → error (missing r/)
/add_set r/      → error (missing reps)
/add_set r/abc   → error (invalid reps)

[no active workout]
/add_set r/15    → error (no active workout)

[no exercise yet in active workout]
/add_set r/15    → error (no exercise to attach set)
```

**Usage:** `/add_set r/<reps>` (alias: `as`)

---

## End Workout

### Success Cases

```
/end_workout d/23/10/25 t/0830 → success (ends active workout)

ew d/23/10/25 t/0830 → success (ends active workout)
```

### Error Cases

```
/end_workout                           → error (missing d/ and t/)
/end_workout d/23/10/25                → error (missing t/)
/end_workout t/0830                    → error (missing d/)
/end_workout d/23/10/25 t/0700         → error (end time before start time)
/end_workout d/32/10/25 t/0830         → error (invalid date)
/end_workout d/23/10/25 t/2460         → error (invalid time)

[no active workout]
/end_workout d/23/10/25 t/0830         → error (no active workout)
```

**Usage:** `/end_workout d/<dd/MM/yy> t/<HHmm>` (alias: `ew`)

---

## View Log

### Success Cases

```
/view_log            → success (current month, page 1)

/view_log -m 10      → success (Oct of current year, page 1)

/view_log -m 10 2    → success (Oct, page 2)

/view_log -ym 2024 10 → success (Oct 2024, page 1)

/view_log -m 10 -d   → success (detailed view)
```

### Error Cases

```
/view_log -m x          → error (month must be 1..12)
/view_log -ym 2024 x    → error (month must be 1..12)
/view_log -ym yyyy mm p → error (page must be positive integer)
/view_log -q            → error (unknown flag)
/view_log -m 10 0       → error (page must be positive)
```

**Usage:**

* `/view_log`
* `/view_log -m <month 1..12> [page]`
* `/view_log -ym <year> <month 1..12> [page]`
* Optional `-d` for detailed view (alias: `vl`)

---

## Open (Workout Details)

### Success Cases

```
/view_log
/open 1    → success (opens the 1st listed workout)
```

### Error Cases

```
/open           → error (missing index)
/open abc       → error (index must be an integer)
/open 999       → error (index out of bounds)
```

**Usage:** `/open <index>` (alias: `o`)

---

## Delete Workout

### Success Cases

```
/del_workout Push          → success (delete by name)

/del_workout d/23/10/25    → success (delete by date; interactive path)
```

### Error Cases

```
/del_workout                   → error (missing target)
/del_workout d/99/99/99        → error (invalid date)
/del_workout NotAWorkout       → error (not found)
```

**Usage:**

* `/del_workout <WORKOUT_NAME>`
* `/del_workout d/<dd/MM/yy>` (interactive delete)
  (aliases: `d`)

---

## Add Modality Tag

### Success Cases

```
/add_modality_tag m/CARDIO k/hiking   → success (keyword added; workouts retagged)
amot m/CARDIO k/hiking   → success (keyword added; workouts retagged)

/add_modality_tag m/STRENGTH k/deadlift → success
amot m/STRENGTH k/deadlift → success
```

### Error Cases

```
/add_modality_tag                 → error (missing m/ and k/)
/add_modality_tag m/CARDIO        → error (missing k/)
/add_modality_tag k/hiking        → error (missing m/)
/add_modality_tag m/XYZ k/run     → error (unknown modality)
```

**Usage:** `/add_modality_tag m/(CARDIO|STRENGTH) k/<keyword>` (alias: `amot`)

---

## Add Muscle Tag

### Success Cases

```
/add_muscle_tag m/LEGS k/lunges   → success (keyword added; workouts retagged)
amt m/LEGS k/lunges   → success (keyword added; workouts retagged)

/add_muscle_tag m/CHEST k/bench   → success
amt m/CHEST k/bench   → success
```

### Error Cases

```
/add_muscle_tag                 → error (missing m/ and k/)
/add_muscle_tag m/LEGS          → error (missing k/)
/add_muscle_tag k/squat         → error (missing m/)
/add_muscle_tag m/XYZ k/foo     → error (unknown muscle group)
```

**Usage:** `/add_muscle_tag m/<MUSCLE_GROUP> k/<keyword>` (alias: `amt`)

---

## Override Workout Tag

### Success Cases

```
/override_workout_tag id/1 newTag/LEG_DAY → success (tag updated & saved)
owt id/1 newTag/LEG_DAY → success (tag updated & saved)
```

### Error Cases

```
/override_workout_tag                    → error (missing id/ and newTag/)
/override_workout_tag id/1               → error (missing newTag/)
/override_workout_tag newTag/LEG_DAY     → error (missing id/)
/override_workout_tag id/x newTag/Y      → error (invalid id)
```

**Usage:** `/override_workout_tag id/<WORKOUT_ID> newTag/<NEW_TAG>` (alias: `owt`)

---

## Gym Where

### Success Cases

```
/gym_where n/squat → success (lists gyms that support the exercise)
gw n/bench         → success
```

### Error Cases

```
/gym_where           → error (missing n/)
/gym_where n/        → error (empty exercise)
/gym_where n/unknown → "Sorry, no gyms found for that exercise."
```

**Usage:** `/gym_where n/<exercise>` (alias: `gw`)

---

## Gym Page

### Success Cases

```
/gym_page p/1 → success (shows equipment table for gym #1)
gp p/2        → success
```

### Error Cases

```
/gym_page        → error (missing p/)
/gym_page p/     → error (missing page number)
/gym_page p/abc  → error (page must be an integer)
/gym_page p/0    → error (page must be ≥ 1)
/gym_page p/999  → error (invalid page; out of range)
```

**Usage:** `/gym_page p/<page_number>` (alias: `gp`)

---

## Exit

### Success Cases

```
/exit → saves data and exits
e     → saves data and exits
```

### Error Cases

```
(n/a; any I/O error is reported before exit)
```

**Usage:** `/exit` (alias: `e`)

---

### Formats (for reference)

* **Date:** `dd/MM/yy` (e.g., `23/10/25`)
* **Time:** `HHmm` 24-hour (e.g., `0700`, `1830`)

---

## Tagging and Categorization
### Design
The tagging system in FitChasers automatically categorizes workouts based on
exercise modalities (e.g., cardio, strength) and muscle groups (e.g., legs, chest, back).
This enables users to quickly identify workout types and track training patterns over time.

### Class Diagram
![Alt text](docs/diagrams/Class_Diagram_for_tagging_2.png "Class Diagram for Tagging")

Key Relationships:
- Dependency: `WorkoutManager` depends on the `Tagger` interface for tag suggestion services
- Composition: `WorkoutManager` owns and manages multiple `Workout` instances.
- Aggregation: `Workout` contains `Exercise` objects (exercises can exist independently
- Implementation: `DefaultTagger` implements the `Tagger` interface
- Association: `DefaultTagger` uses Modality and `MuscleGroup` enums to organize keywords

### Implementation
Automatic Tag Generation
When a user creates a new workout using the /create_workout command, the system automatically
generates tags based on keywords found in the workout name.

Example Command: `/create_workout n/run and swim d/24/10/25 t/1200`

Process:
1. `FitChasers` parses the command and delegates to `WorkoutManager.addWorkout()`
2. `WorkoutManager` creates a new `Workout` object with name "run and swim"
3. `WorkoutManager` calls `tagger.suggest(workout)` to generate tags
4. `DefaultTagger` scans the workout name for matching keywords:
    * "run" matches `Modality.CARDIO`
    * "swim" matches `Modality.CARDIO` and `MuscleGroup.BACK`
5. The suggested tags `{cardio, back}` are stored in workout.autoTags via `workout.setAutoTags()`
6. The workout is added to the workout list

### Sequence Diagram
The following sequence diagram shows the interaction between components when a workout is created
and tags are auto-generated:
![Alt text](docs/diagrams/Sequence Digram for tagging.png "Sequence Diagram for Tagging")

### Manual Tag Method
#### Adding modality keywords
Users can extend the `DefaultTagger`'s keyword dictionary using the `/add_modality_tag` command.
Example: `/add_modality_tag m/cardio k/jump_rope`
#### Process
1. `FitChasers` parses the command and extracts modality (CARDIO) and keyword ("jump_rope")
2. `FitChasers` calls `tagger.addModalityKeyword(Modality.CARDIO, "jump_rope")` directly on the `DefaultTagger`
   instance
3. The keyword "jump_rope" is added to the `Modality.CARDIO` keyword set in `DefaultTagger`
4. Future workouts containing "jump_rope" in their name will automatically receive the `cardio` tag
#### Overriding workout tags
Users can manually override tags for a specific workout using the `/override_workout_tag` command:
`/override_workout_tag id/3 newTag/strength
1. WorkoutManager.overrideWorkoutTags(int workoutId, String newTag) is invoked with workoutId=3 and newTag="strength"
2. The target workout is retrieved by ID (1-based index)
3. A new Set<String> containing only "strength" is created
4. `workout.setManualTags(newTagsSet)` replaces any existing manual tags
5. `workout.setAutoTags(new LinkedHashSet<>())` clears all auto-generated tags
6. Subsequent calls to `getAllTags()` return only `{strength}`

#### Important Design Decision
Overriding clears auto-tags to prevent confusion. If a workout is auto-tagged as cardio but the user overrides
it to strength, keeping both tags would be misleading. This design prioritizes user intent over system suggestions.

### Design Consideration
#### Aspect: Separate Auto vs. Manual Tags
#### Alternative 1 (Current Choice): Maintain two separate tag sets (autoTags and manualTags)
#### Pros:
* Clear separation of system-generated vs. user-defined data
* Enables selective clearing (e.g., override can clear auto-tags while preserving manual tags if needed)
* Easier debugging and testing (verify auto-generation logic independently)
#### Cons:
* Requires merging sets when displaying all tags
  Alternative 2: Use single tag set with metadata flags
#### Pros
* Simpler data structure(1 set instead of 2)
* Easier to implement tag equality checks
#### Cons:
* Requires additional data structure (e.g., `Map<String, TagSource>`) to track tag origin
* More complex override logic

Rationale: Alternative 1 was chosen as the separation provides clearer semantics and aligns with the use
case where users may want to distinguish between automatic suggestions and their own categorization.

#### Aspect: Keyword Matching Strategy
#### Alternative 1 (Current Choice): Exact substring matching with predefined keywords
 Pros:
* O(n) scan for workout name
* Easy to extend via `/add_modality_tag` command

Cons:
* Limited to keywords explicitly registered
* Cannot handle synonyms or misspellings
* May miss relevant tags if workout names use non-standard terminology

#### Alternative 2: Natural Language Processing (NLP) with word embeddings
 Pros:
* Can recognize semantic similarity (e.g., "jogging" ≈ "running")
* More robust to variations in user input
* Could auto-discover new exercise types

Cons:
* Requires external libraries
* Higher computational cost
* Difficult to debug and test

Rationale: ALternative 1 was chosen for simplicity and predictability. For a CLI Fitchaser,
deterministic tagging with user-extensible keywords provides a better balance of functionality and
maintainability than complex NLP approaches.

### Future Enhancements
#### Tag-based filtering(Planned for v3.0)
#### Proposed feature: Allow use to filter workout logs by tags
Example command: `/view_log --tag cardio`    
Expected Output:
```
Workouts tagged with 'cardio' (2 total):
ID    Date          Name              Duration
2     Fri 24 Oct    gga              45m
3     Fri 24 Oct    run and swim     45m
```
Implementation considerations:
* Add a `filterByTag(String tag)` method to `WorkoutManager`
* Modify `Workout.getAllTags()` to support efficient tag lookups
#### Proposed feature: Display aggregate statistics grouped by tag
Example command: `/stats --by-tag`
Expected Output:
```
Training Summary by Tag:

Cardio:     12 workouts, 540 minutes total
Strength:    8 workouts, 320 minutes total
Legs:        5 workouts, 200 minutes total

```
Implementation consideration:
* Add a generateTagStats() method to WorkoutManager
* Use Java Streams to group and aggregate workout data
* Consider caching statistics to avoid recalculating on every query

## Notes

- All parameters are required unless otherwise noted
- Spacing is critical in parameter syntax (e.g., space before `r/`, no space after `r/`)
- Invalid or malformed parameters return specific error messages with usage guidance
- Y/N prompts are used when parameters are missing

