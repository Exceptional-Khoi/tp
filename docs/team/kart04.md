# Karthik - Project Portfolio Page

## Overview
FitChasers is a desktop fitness tracking application that empowers users to log, organize, and analyze their
workout sessions through an intuitive command-line interface. It is written in Java and incorporates approximately
4 kLoC of well-structured code.

## Summary of Contributions
Given below are my contributions to the project:

**Code Contributed:**  
[RepoSense link](https://nus-cs2113-ay2526s1.github.io/tp-dashboard/?search=kart04&breakdown=true&sort=groupTitle%20dsc&sortWithin=title&since=2025-09-19T00%3A00%3A00&timeframe=commit&mergegroup=&groupSelect=groupByRepos&checkedFileTypes=docs~functional-code~test-code~other&filteredFileName=&tabOpen=true&tabType=authorship&tabAuthor=Kart04&tabRepo=AY2526S1-CS2113-W14-3%2Ftp%5Bmaster%5D&authorshipIsMergeGroup=false&authorshipFileTypes=docs~functional-code~test-code&authorshipIsBinaryFileTypeChecked=false&authorshipIsIgnoredFilesChecked=false)


### New Feature: Intelligent Workout Tagging System with Manual & Auto Tags
* Automatically generates workout tags using an enum-based keyword matching engine for modalities and muscle groups.
* Allows users to manually override tags using `/override_workout_tag id/[ID] newTag/[TAG]`
* Maintains separation between auto-tags and manual tags
* Implements dynamic keyword addition through `/add_modality_tag` and `/add_muscle_tag` commands

Justification:
This feature greatly enhances user experience by intelligently organizing workouts, enabling quick filtering, searching
and analysis by type and muscle group for better progress tracking and planning.

### New Feature: Gym Localization & Equipment Recommendation System
* Suggests NUS gyms (UTown, SRC, USC) that have equipment matching user exercises using `/gym_where n/[EXERCISE]`
* Displays equipment tables for specific gyms using `/gym_page p/[PAGE_NUMBER]`
* Maps exercise names to body part tags, then matches against available gym equipment

Justification:
Helps NUS students find the most convenient gym location for their planned workouts, reducing friction
in their fitness routine and promoting consistent training.

### New_feature: Enhanced Workout Session Management
* Improved `/create_workout` and `/end_workout` command to handle partial date/time input with smart defaulting.
* Uses current date and time when user omits these parameters
* Validates that end time is not before start time with retry logic

Justification:
Reduces user errors by allowing flexible input while maintaining data integrity through validation.

## Documentation
### User Guide Contributions
Primarily drafted and maintained core FitChasers User Guide sections, coordinating with teammates to review and finalize
clear, comprehensive documentation.

## Developer Guide Contribution
### WorkoutManager Component Documentation:
Added and documented the WorkoutManager architecture, responsibilities, and API, including UML class diagram and its
role as the controller for workout features like creation, management, tagging, persistence, deletion, and display.

### Tagging System Implementation Details:
* Documented auto-tagging and manual override processes with step-by-step breakdowns including sequence diagrams to illustrate workflow.
* Tag generation and updating when workouts are created or edited.
* Tag override workflow via /override_workout_tag, from command parsing to tag set manipulation.

### Contributions to Core Modules
#### Workout.java
* Contributor to the Workout class that models workout sessions, manages tags, and supports data persistence.
* Implemented methods for workout lifecycle (creation, ending, editing, deletion).
* Integrated tag management with auto and manual tags, as well as duration and date validation logic.

#### WorkoutManager.java
* Contributor to the WorkoutManager class, which manages workouts, file I/O, and core command logic.
* Designed workout loading/saving using FileHandler.
* Integrated tagging engine and ensured support for both batch and single workout operations.

#### Contributed to the DeleteWorkout feature
* Helped implement the class and workflow that removes workouts from the user's log, handling user confirmation,
  storage updates, and UI coordination.

### Design Considerations and Alternatives:
Chose separate collections for auto/manual tags (over a meta-flag set) for greater clarity and extensibility. Used
substring matching (not full NLP) for its simplicity and efficiency in our CLI context.

### Future Enhancements Section:
Wrote “Future Enhancements” discussing planned extensions for tag-based log filtering and statistics, with example
commands/output and Java stream-based implementation plan.

### Diagrams:
* WorkoutManager class diagram (reflecting aggregation of workouts, integration with tagger and gym systems)
* Tagging sequence diagram (showing control/data flow during tag generation and override)
* Various supporting diagrams for design alternatives and anticipated enhancements.

## Contributions to team-based tasks
* Documenting the target user profile
* Maintaining the issue tracker
* Added Junit testing for ViewLogTest, WorkoutManagerTest, WorkoutTest, WeightManagerTest and EquipmentDisplayTest

## Review/mentoring contributions
* Pull requests reviewed (with non-trivial review comments):#256, #164, #133, #100, #98, #87, #60, #57. #272
* Pull request created: #7, #32, #42, $58, #82, #106, #108, #112, #127, #142, #151, #165, #180, #195, #204, #257, #270,
  #270, #275
