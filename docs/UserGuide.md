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

### Interactive Deletion of Workouts: `del_workout`
The `del_workout` command supports both exact-name deletion and interactive selection,
allowing users to efficiently delete workouts with either direct specification or 
by picking from a displayed list.

Usage
* Format
  * `del_workout WOKROUT_NAME`
  * `del_workout d/DATE`
  * `del_workout`
* Description
    * Deletes the workout identified by `WORKOUT_NAME`.
    * if `d/date` is supplied s supplied, filters workouts to only those on the specified date, 
then lets users interactively select by index.
    * With no arguments, all workouts are shown as a numbered list for selection/deletion by index.
    * Multiple indices separated by spaces delete multiple workouts.
    * Invalid or empty input will trigger appropriate error messages.
  
Example Usage Scenario
1. User enter `/del_workout` without arguments
    * System displays all workouts by index.
2. User enters index number(s) to select workout(s) for deletion.
   * System deletes selected workout(s) and displays confirmation.
3. User enters `/del_workout d/20/10/25`
   * System displays only workouts from `20/10/25` for deletion.
   * If no workouts match, system outputs: `"No workouts found for the given date."`
4. User provides invalid index or leaves input empty.
   * System outputs: `No valid indices entered. Nothing deleted.`
5. If the initial input is fully empty, an `InvalidCommandException` is thrown and the user is asked to provide valid 
input.

Design Considerations
* Interactive Selection
  * Chosen because it allows multi-selection and robust error handling.
  * Pros: User friendly, supports batch deletion, fails safely.
  * Cons: Slower for very large lists.
* Database filtering:
  * Chosen for intuitive, context-driven user experience.
  * Pros: Consistent UX, simple to use.
  * Cons: Requires correct date format.

UML Sequence Design
![Alt text](UML_del_workout.jpeg)
## FAQ

**Q**: How do I transfer my data to another computer? 

**A**: {your answer here}

## Command Summary

{Give a 'cheat sheet' of commands here}

* Add todo `todo n/TODO_NAME d/DEADLINE`
