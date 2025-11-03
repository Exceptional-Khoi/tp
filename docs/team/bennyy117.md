# Nguyen Bich Loan - Project Portfolio Page

## Overview
FitChasers is a desktop fitness tracking application that empowers users to log workouts, monitor weight changes, 
and set personal goals via an intuitive command-line interface.  
It is written in Java with approximately 4 kLoC, emphasizing data privacy through local storage, 
real-time validation, and visual progress feedback.

## Summary of Contributions
**Code Contributed:**  
[RepoSense link](https://nus-cs2113-ay2526s1.github.io/tp-dashboard/?search=bennyy117&breakdown=true&sort=groupTitle%20dsc&sortWithin=title&since=2025-09-19&timeframe=commit&mergegroup=&groupSelect=groupByRepos&checkedFileTypes=docs~functional-code~test-code~other)

### New Feature: Comprehensive Weight Logging & Progress Visualization
* Designed and implemented `/add_weight`, `/view_weight`, `/set_goal`, and `/view_goal` commands with interactive prompts for missing inputs.
* Built robust validation for weights and seamless persistence.
* Created a sorted history system that handles updates without duplicates.
* Added dynamic ASCII line-graph rendering with auto-scaling, sampling for large datasets, and color-coded markers.
* Integrated goal tracking with progress deltas and motivational status messages.

**Justification:** Weight tracking motivates users by turning raw data into actionable insights. 
This feature delivers instant visual trends, catches input errors early, and adapts to any history size for a frustration-free experience.

**Highlights:**
- Graph intelligently samples data to fit console width while highlighting key trends.
- Smart defaults and confirmations minimize typing errors.
- Every change auto-saves reliably, protecting user progress.

### New Feature: Dynamic User Profile Management
* Implemented `/rename` with strict format checks (1–30 chars, safe characters only).
* Ensured name persistence across sessions and personalized all feedback.
* Integrated name display in greetings, graphs, and history views.

**Justification:** Personalization builds emotional investment, seeing your name in interaction 
makes the app feel tailored and encouraging.

### Testing & Reliability
* Crafted extensive tests for validation, edge cases, and output consistency.
* Achieved high coverage in user-facing logic.
* Added safeguards against invalid inputs and save failures.

## Documentation
### User Guide Contributions
* Authored Weight and Goal sections: detailed formats, real examples, validation rules, and step-by-step flows.
* Added visual aids like command screenshots and graph previews using diagrams.
* Unified style, error quotes, and cross-references for seamless reading.

### Developer Guide Contributions
* Redesigned All Class Diagrams Using PlantUML:  
  Unified every diagram for crisp rendering, consistent styling (colors, borders, icons), and easy updates. Replaced outdated images with embeddable code for PDF compatibility.
    - WeightManager & GoalWeightTracker: Showed key associations, multiplicities, and persistence links.
    - WorkoutManager: Detailed workout-exercise-set relationships.
    - Tagging & UI: Streamlined interfaces and enums.

* Expanded Sections:
    - Design trade-offs for persistence strategies.
    - Future ideas: BMI trends and graph enhancements.
    - Manual testing steps for weight features.

These improvements make the DG a clear, visual reference. Diagrams now render perfectly in PDFs, fixing prior link issues.

## Team-Based Contributions
* Led persistence refactoring for reliable, modular saves.
* Helped in v1.0–v2.1 releases: milestones, changelogs, and merges.
* Resolved CI/storage issues in key PRs.

## Community
* PRs Reviewed: #51, #62, #72, #117, #121, #124, #128, #145, #146, #159, #173, #175, #182, 
#186, #191, #197, #198, #201, #204, #205, #257, #270
* PRs Created: #52, #55, #66, #67, #68, #69, #89, #90, #120, #126, #138, #140, #141, #144, 
#150, #157, #168, #169, #172, #174, #177, #181, #188, #190, #192, #194, #203, #207, #273, #280.

## Tools
* Adopted PlantUML for all diagrams.
* Enforced standards with static analysis in reviews.

---
**Impact Summary:**  
My contributions elevated FitChasers into a motivational powerhouse. Weight visualization turns 
numbers into stories—users see progress, stay consistent, and crush goals with confidence. 
Diagram overhauls ensure the project shines for future maintainers.