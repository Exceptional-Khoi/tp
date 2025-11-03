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
  * Automatically generates tags for workouts using an enum-based keyword matching engine (`DefaultTagger`) 
    that categorizes by modality (cardio, strength) and muscle groups 
    (legs, chest, back, shoulders, arms, core, posterior-chain).
  * Allows users to manually override tags using `/override_workout_tag id/[ID] newTag/[TAG]`
  * Maintains separation between auto-tags and manual tags to prevent user-defined tags 
    from being overwritten by automatic suggestions.
  * Implements dynamic keyword addition through `/add_modality_tag` and `/add_muscle_tag` commands

Justification:
This feature significantly improves user experience by organizing workouts intelligently without requiring manual 
categorization for every session. Users can quickly filter, search, and analyze workout history by type and 
muscle group, making progress tracking and workout planning more intuitive.

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

Primarily responsible for drafting and maintaining the core sections of the FitChasers User Guide, including feature 
explanations, command formats, FAQs and example scenarios. I also coordinated with my teammates to review, update
and finalize the documentation, ensuring clarity and completeness. Their suggestions and edits helped strengthen 
the guide, and the published version is the result of our joint efforts.

## Developer Guide Contribution
### WorkoutManager Component Documentation:
Added and documented the architecture, responsibilities, and API of the WorkoutManager component, 
including UML class diagram and explanation of its role as the controller for all workout-related features 
(workout creation, exercise management, tagging, data persistence, deletion, and display).

### Tagging System Implementation Details:
Documented in detail the underlying mechanics of the auto-tagging and manual tag override process. Included clear 
“Manual Tag Method” and “Overriding Workout Tags” subsections, with step-by-step breakdowns and code references. 
Added sequence diagrams for:
* Tag generation and updating when workouts are created or edited.

* Tag override workflow via /override_workout_tag, from command parsing to tag set manipulation.

### Contributions to Core Modules
#### Workout.java
* Contributor of the Workout class, which models individual workout sessions, encapsulates all properties, implements 
  tag logic and supports serialisation for persistence.
* Implemented methods for workout lifecycle (creation, ending, editing, deletion).
* Integrated tag management with auto and manual tags, as well as duration and date validation logic.

#### WorkoutManager.java 
* Contributor to the WorkoutManager class, the controller responsible for managing lists of workouts, handling file 
  storage/retrieval and coordinating logic for create/update/delete/search commands.
* Designed workout loading/saving using FileHandler.
* Integrated tagging engine and ensured support for both batch and single workout operations.
* Authored/maintained key methods for filtering, sorting, and indexing workout logs.

#### Contributed to the DeleteWorkout feature
* Helped implement the class and workflow that removes workouts from the user's log, handling user confirmation, 
  storage updates, and UI coordination.

### Design Considerations and Alternatives:
Described the decision process for storing tags in separate collections (autoTags, manualTags) vs. alternative 
single-set approaches with meta-flags. Outlined the trade-offs for each method, and justified the selected approach 
(clarity, extensibility, and debugging advantages for our CLI-based context).
Provided rationale for using substring-matching instead of full NLP, and compared their pros/cons 
(see “Keyword Matching Strategy”).

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
