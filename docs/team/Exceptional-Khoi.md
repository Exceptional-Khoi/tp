# Project Portfolio Page
> **Module:** CS2113 — Team Project (tP)  
> **Product:** FitChasers — CLI fitness tracker (chat-bubble UI, tagging, weight & goal tracking)

## Overview
FitChasers is a console-based fitness tracking application designed to help users manage workouts, track weight progress, and set personal fitness goals — all through an intuitive chat-bubble style user interface.

It provides a complete command-driven experience that integrates workout logging, goal tracking, and tag-based categorization, enabling users to efficiently record, view, and analyze their fitness journey.

## Summary of Contributions
### Code contributed
[tP Code Dashboard — Exceptional-Khoi](https://nus-cs2113-ay2526s1.github.io/tp-dashboard/?search=&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other&since=2025-09-19T00%3A00%3A00&filteredFileName=&tabOpen=true&tabType=authorship&tabAuthor=Exceptional-Khoi&tabRepo=AY2526S1-CS2113-W14-3%2Ftp%5Bmaster%5D&authorshipIsMergeGroup=false&authorshipFileTypes=docs~functional-code~test-code~other&authorshipIsBinaryFileTypeChecked=false&authorshipIsIgnoredFilesChecked=false)
### Enhancements implemented

* **Chat‑bubble Console UI (UI.java)**
  Implemented a polished, readable console UI with left/right "chat" bubbles and ANSI styling.

    * **Left bubble renderer**: `leftBubble(String)` builds framed messages with dynamic width, padding, and safe ANSI‑stripping via `stripAnsi(...)`. Handles multi‑line wrapping (`wrapLine(...)`) and clamps to console width (`CONSOLE_WIDTH`, `FRAME_OVERHEAD`, `PADDING`).
    * **Right input bubble**: `readInsideRightBubble(String)` prints a right‑aligned prompt label `(You)`, draws a bordered bubble, reads input via a dedicated `Scanner`, echoes a tidy footer, and reprints the left header for continuity. Robust against EOF (returns `null`).
    * **Header & styling**: `printLeftHeader()` with brand header `{^o^} FitChasers`; curated ANSI palette (e.g., `BOLD_BRIGHT_PURPLE`, `LIGHT_YELLOW`) and safe resets to prevent color bleed.
    * **Impact**: Greatly improves legibility and UX compared to plain println; consistent visual hierarchy for prompts vs. app messages; easy to reuse across app components.  

* **Human‑friendly Interaction Flows**

    * **Greeting banner**: `showGreeting()` with multi‑shade ASCII art and an onboarding nudge to `/help`.
    * **Quick‑start tutorial**: `showQuickStartTutorial()` — a single‑page script guiding users through `/create_workout`, `/add_exercise`, `/end_workout`, `/view_log` with defaults (accept current date/time) to ensure first‑run success.
    * **Comprehensive help**: `showHelp()` groups commands by domain (Profile, Weight, Workout, Log, Tagging, Gym Finder, System) and shows live examples matching our parser grammar.
    * **Error & info surfaces**: `showMessage(...)` and `showError(...)` normalize outputs into the left bubble with a consistent `[Oops!]` prefix for errors.

* **Safe confirmations with cancel/help**

    * `confirmationMessageWithCancel()` supports **Y/N**, `/help`, **`/cancel` → returns `null`** to let callers differentiate **cancel** from **decline**. Clear error text with tips to exit quickly.
    * `confirmationMessage()` retains the simple Y/N + `/help` flow for call sites that don’t need cancel.
    * **Impact**: Eliminates NPE risk paths by standardizing tri‑state confirmation; improves recoverability from destructive actions.

* **Rich workout detail view**

    * `displayDetailsOfWorkout(Workout)` prints name, date, computed duration (hh/mm), optional start/end timestamps, **tags** (from `getAllTags()`), and an enumerated exercise list with per‑set reps.
    * Validates `workout != null`, asserts non‑negative durations, handles empty tags/sets gracefully.

* **Utility correctness & robustness**

    * `getDaySuffix(int)` with 11–13 special‑case and modulo mapping.
    * `wrapLine(...)`, `stripAnsi(...)`, and `clampNonNeg(...)` to ensure safe rendering and prevent index/width errors.

> **Why it’s deep/complete:** The UI layer abstracts console rendering and input concerns from business logic, uses width calculations and ANSI‑aware wrapping to prevent layout bugs, and exposes reusable helpers that other features (e.g., ViewLog, delete flows) can call. Edge cases covered include EOF on input, empty names/weights, invalid characters, and tri‑state confirmations.

## Contribution to the User Guide (UG)

* Authored the Interface Display (Console Width) section, describing how FitChasers’ chat-style interface is optimized for a 150-character console width and how text wrapping works automatically.

* Added and refined documentation for system commands:
    * Exiting the program (`/exit`) — described how this command safely saves data and exits the app.
    * Viewing help (`/help`) — documented how the command displays all available commands, usage patterns, and aliases.

* Collaborated with teammates for review cycles, ensuring clarity and completeness

## Contributions to the Developer Guide (DG)

* Authored and refined the *Setting up and Getting Started* section:

    * Wrote detailed step-by-step setup instructions, including forking, cloning, Gradle import, and running tests.
    * Clarified JDK 17 setup and IntelliJ Gradle import process with references to SE-EDU guides.
    * Added tutorials for tracing commands (`/help`) and implementing new commands (`/stats`) to help new contributors onboard quickly.
    * Explained CI workflow basics and Checkstyle configuration tips.

* Authored *UI Component* section in DG:

    * Documented the architecture and flow of the `seedu.fitchasers.ui` package, detailing the chat-bubble CLI design and how `UI` interacts with domain managers.
    * Added ASCII layout diagram and class diagram reference (`UI_class_diagram.png`) with standardized UML notation and formatting consistency across all diagrams.
    * Described key constants (`CONSOLE_WIDTH`, `PADDING`, `FRAME_OVERHEAD`) and their effect on layout.
    * Clarified UI responsibilities, emphasizing separation between I/O and domain logic.

* Reformatted multiple UML diagrams to use consistent **PlantUML notation**, line weights, color themes, and naming conventions to match SE-EDU standards.

    * Ensured diagram exports (PNG/SVG) were legible and properly sized for A4 PDF conversion.

* Authored and refined *Non-Functional Requirements* section:

    * Defined response time limits (≤1s), scalability (≥1,000 records), test coverage (>80%), auto-save behavior, platform independence, and clear error messaging standards.

## Team-based tasks
* Maintained code quality and consistency
    * Standardized ANSI color usage and chat-bubble formatting across all UI outputs.
    * Helped enforce consistent naming conventions, indentation, and JavaDoc style across multiple packages (ui, workouts, user).
* Release management and QA
    * Participated in preparing and reviewing team deliverables for v2.0 and v2.1 releases.
    * Helped check command examples and error messages for accuracy before the PE-D.
    * Verified UG/DG/PPP PDF conversions early to catch layout or hyperlink issues.

## Reivew/ mentoring contributions
Regularly reviewed teammates’ PRs: #52, #66, #67, #68,  #75, #89, #90, #97, #126, #138, #140, #141, #142, #144, #150, #157, #168, #169, #170, #172, #174, #177, #181, #188, #190, #192, #196, #203, #207, #208, #209, #273, #280



