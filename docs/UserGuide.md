# User Guide

## Introduction

{Give a product intro}

## Quick Start

{Give steps to get started quickly}

1. Ensure that you have Java 17 or above installed.
1. Down the latest version of `Duke` from [here](http://link.to/duke).

## Features 

{Give detailed description of each feature}

### Adding a todo: `todo`
Adds a new item to the list of todo items.

Format: `todo n/TODO_NAME d/DEADLINE`

* The `DEADLINE` can be in a natural language format.
* The `TODO_NAME` cannot contain punctuation.  

Example of usage: 

`todo n/Write the rest of the User Guide d/next week`

`todo n/Refactor the User Guide to remove passive voice d/13/04/2020`

### Tagging command guide

#### Adding Modality group keyword: `add_modality_tag`
Adds a new keyword to extend the automatic tagging system for workout modalities (cardio, strength).

Format: `add_modality_tag m/MODALITY k/KEYWORD`
* The `MODALITY` must be either `CARDIO` or `STRENGTH`.
* The `KEYWORD` should be lowercase and represent an exercise type (e.g., "jump_rope", "pilates").
* Once added, all future workouts containing this keyword will be auto-tagged with the corresponding modality.
* Existing workouts will be retagged immediately.

Example of usage:
`add_modality_tag m/CARDIO k/jump_rope`

`add_modality_tag m/STRENGTH k/pilates`

Expected output:
```
🤖 FitChaser
╭──────────────────────────────────────────────────────╮
│ Added keyword jump_rope to modality CARDIO           │
╰──────────────────────────────────────────────────────╯
```

#### Adding Muscle Group Keywords: `add_muscle_tag`
Adds a new keyword to extend the automatic tagging system for muscle groups (legs, chest, back, etc.).

Format: `add_muscle_tag m/MUSCLE_GROUP k/KEYWORD`
* The `MUSCLE_GROUP` must be one of: `LEGS, POSTERIOR_CHAIN, CHEST, BACK, SHOULDERS, ARMS, CORE`.
* The `KEYWORD` should be lowercase and represent an exercise targeting that muscle group (e.g., "lunges", "squats").
* Once added, all future workouts containing this keyword will be auto-tagged with the corresponding muscle group.
* Existing workouts will be retagged immediately.

Example of usage:
`add_muscle_tag m/LEGS k/lunges`
`add_muscle_tag m/CHEST k/push_ups`

Expected Output:
```
🤖 FitChaser
╭──────────────────────────────────────────────────────╮
│ Added keyword lunges to muscle group LEGS            │
╰──────────────────────────────────────────────────────╯
```
#### Overriding Workout Tags: `override_workout_tag`
Manually replaces the tags of a specific workout with a single new tag, clearing all auto-generated tags.

Format: `override_workout_tag id/WORKOUT_ID newTag/TAG_NAME`
* The `WORKOUT_ID` is the index number of the workout (use `/view_log` to see IDs).
* The `TAG_NAME` can be any custom tag (e.g., "cardio", "strength", "recovery", "custom_tag").
* The command clear both auto-generated and manual tags, replacing them with only the specified tag.
* Changes are saved immediately to persistent storage.

Example of usage:
`override_workout_tag id/1 newTag/strength`
`override_workout_tag id/3 newTag/recovery`

Expected output:
```
🤖 FitChaser
╭──────────────────────────────────────────────────────╮
│ Saved 1 workouts for 2025-10.                        │
│ Workout tags saved successfully.                     │
╰──────────────────────────────────────────────────────╯
```
#### Understanding Workout tags
Auto-Generated Tags vs. Manual Tags
FitChasers uses two types of tags:
* Auto-Generated Tags: Automatically assigned based on workout name keywords. These are suggested by the system.
* Manual Tags: Tags you explicitly set using `/override_workout_tag` command. These take priority over auto-generated 
  tags.

Viewing Workout Tags:
To see the tags assigned to a specific workout:
1.  Enter `/view_log` to list all workouts
2. Enter `/open [WORKOUT_ID]` to view detailed information including tags
Example:
```
Enter command > /view_log
Workouts (3 total) – Page 1/1
ID  Date         Name              Duration
1   Fri 24 Oct   run and swim     45m
2   Fri 24 Oct   leg day          30m
3   Fri 24 Oct   lunges session   25m

Enter command > /open 1
+------------------------------------------------------+
│  Here you go bestie! These are the workout details!  │
│                                                      │
│  Name       : run and swim                           │
│  Date       : Saturday 25th of October               │
│  Duration   : 0m                                     │
│  Tags       : cardio, back                           │
│                                                      │
│  Exercises  : (none added)                           │
+------------------------------------------------------+
```

### Deleting Workouts: del_workout
Deletes the specified workout(s) from your workout history.
#### Format: del_workout INDEX
* Deletes the workout at the specified INDEX.
* The index refers to the index number shown in the displayed workout list.
* The index must be a positive integer 1, 2, 3, …
* Multiple indices can be deleted by entering them separated by spaces.

#### Examples:
* `view_log` followed by `del_workout 2` deletes the 2nd workout in the address book.
* `view_log d/24/10/25` followed by `del_workout 1` deletes the 1st workout in the results of the filtered list.
* `del_workout 1 3 5` deletes the 1st, 3rd, and 5th workouts from the currently displayed list.



## FAQ

**Q**: How do I transfer my data to another computer? 

**A**: {your answer here}

## Command Summary

{Give a 'cheat sheet' of commands here}

* Add todo `todo n/TODO_NAME d/DEADLINE`
