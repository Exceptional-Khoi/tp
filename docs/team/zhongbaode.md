# Baode - Project Portfolio Page

## Overview
FitChasers is a desktop-based fitness tracking application that enables users to log, organize, and analyze their workout progress through a clean command-line interface.  
It is written in Java and comprises around 4 kLoC of code with an emphasis on modular design, validation, and user experience.

---

## Summary of Contributions

**Code Contributed:**  
[RepoSense link](https://nus-cs2113-ay2526s1.github.io/tp-dashboard/?search=nitin&sort=totalCommits%20dsc&sortWithin=totalCommits%20dsc&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other&since=2025-09-19T00%3A00%3A00&filteredFileName=&tabOpen=true&tabType=zoom&zA=nitin19011&zR=AY2526S1-CS2113-W14-3%2Ftp%5Bmaster%5D&zACS=216.05790645879733&zS=2025-09-19T00%3A00%3A00&zFS=&zU=2025-11-03T23%3A59%3A59&zMG=false&zFTF=commit&zFGS=groupByRepos&zFR=false)


 ### Implemented /view_log commands
* Added the ViewLog UI workflow to load monthly workout data, sort it chronologically, render paginated compact or detailed views, and keep context for /open interactions. Viewlog also enforces guarded argument parsing.

Justification:
 This monthly log viewer lets users audit past sessions quickly, catch training gaps, and inspect into specific entries without manually opening files, which is essential for reviewing trends and planning upcoming workouts. Pagination is done to ensure that over the course of many years, users can easily view their worksouts without having to scroll through thousands of logs. 

### Implemented FileHandler that manages persistent files
  *    Introduced monthly-aware persistence that ensures storage directories exist, serialises each month’s workouts to workouts_YYYY-MM.txt, and reloads them via structured parsing while preserving exercises, tags, and durations.
  
Justification: 
Monthly segregation prevents bloated single files, makes historic retrieval and updates to the enter workout performant through the use of lazy loading (Loading data only when it is needed). 

### Implemented Delete Workout functionality
  * Added index-based deletion with confirmation prompts amd month/year-awareness into the FitChasers command parser.

Justification:
 Safe, flexible deletion keeps logs tidy while preventing accidental data loss, ensuring users can correct mistakes or purge outdated sessions without editing save files manually.

### Contributed to WorkoutManager class
  * Enhanced WorkoutManager to coordinate with FileHandler, perform strict command parsing for /create_workout, auto-tag new workouts, and refresh in-memory lists when switching months.

Justification:
 Centralising validation and persistence logic in the manager keeps workout lifecycle flows consistent, letting other features rely on a single source when creating sessions.

### Contributed to Parser classes (FitChasers and delete workflow)
  * Extended the main command router to recognise /view_log, /open, and /del_workout, delegating to specialised handlers while surfacing parsing errors to the UI.

Justification: 
Parser integration enforces consistent command syntax, ensuring the CLI remains discoverable and coherent as features expand. It also guards against malformed input, reducing crashes and guiding users toward successful commands.


## 📘 Documentation

### User Guide Contributions
Refined and formatted the User Guide sections related to create_workout, deletion and viewlog commands.
Reviewed teammates’ sections to ensure accuracy and consistent format

---

### Developer Guide Contributions

* Authored the architectural components and sequence diagrams for architecture, fileHandle,r and WorkoutManager
* Improved formatting and structure across Developer Guide sections for better clarity.
* Reviewed diagrams and ensured alignment between documentation and actual implemented features.

---

## 🤝 Team-Based Contributions
* Assigned issue to specific members and helped them when they faced issue trying to solve it 
* Refactored codebase to ensure clear and efficient workflow for the team
* Helped maintain the issue tracker and coordinated merges before milestone submissions.
* Participated in team code reviews and ensured CI compliance across key branches.
* Setting up the GitHub team org/repo
* Release Management


---

## 🧠 Review & Mentoring Contributions
* **Pull Requests Reviewed:** #58, #59, #106, #127, #151, #165, #180, #194, #269, #275
  → Provided detailed validation, formatting, and consistency feedback.
* **Pull Requests Created:** #3, #6, #33, #60, #75, #86, #92, #97, #98, #100, #102, #113, #170, #183, #198, #208, #275, #276, #279

---
