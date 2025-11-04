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
- 100% method coverage in `UITest` for UI logic.

### New Feature: Dynamic User Profile Management
* Implemented `/rename` with strict format checks (1-30 chars, safe characters only).
* Ensured name persistence across sessions and personalized all feedback.
* Integrated name display in greetings, graphs, and history views.

**Justification:** Personalization builds emotional investment, seeing your name in interaction 
makes the app feel tailored and encouraging.

### Testing & Reliability
* Crafted extensive unit tests for validation, edge cases, and output consistency.
* Achieved 100% method coverage and 95% line coverage in `UITest.java` using JUnit 5 and reflection testing.
* Added safeguards against invalid inputs, EOF, and save failures.

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
* Added Object Diagram for `WeightManager`
* Expanded Sections:
    - Design trade-offs for persistence strategies.
    - Future ideas: BMI trends and graph enhancements.
    - Manual testing steps for weight features.

These improvements make the DG a clear, visual reference. Diagrams now render perfectly in PDFs, fixing prior link issues.

## Team-Based Contributions
* Led persistence refactoring for reliable, modular saves.
* Helped in v1.0-v2.1 releases: milestones, changelogs, and merges.
* Resolved CI/storage issues in key PRs.

## Community
**PRs Reviewed:** 
[#51](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/51), [#62](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/62), [#72](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/72), [#117](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/117),
[#121](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/121), [#124](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/124), [#128](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/128), [#145](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/145),
[#146](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/146), [#159](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/159), [#173](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/173), [#175](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/175),
[#182](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/182), [#186](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/186), [#191](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/191), [#197](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/197),
[#198](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/198), [#201](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/201), [#204](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/204), [#205](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/205),
[#257](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/257), [#270](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/270)

**PRs Created:**
[#52](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/52), [#55](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/55), [#66](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/66), [#67](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/67),
[#68](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/68), [#69](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/69), [#89](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/89), [#90](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/90),
[#120](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/120), [#126](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/126), [#138](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/138), [#140](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/140),
[#141](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/141), [#144](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/144), [#150](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/150), [#157](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/157),
[#168](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/168), [#169](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/169), [#172](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/172), [#174](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/174),
[#177](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/177), [#181](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/181), [#188](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/188), [#190](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/190),
[#192](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/192), [#194](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/194), [#203](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/203), [#207](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/207),
[#273](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/273), [#280](https://github.com/AY2526S1-CS2113-W14-3/tp/pull/280)

## Tools
* Adopted PlantUML for all diagrams.
* Enforced standards with static analysis in reviews.

---
**Impact Summary:**  
My contributions elevated FitChasers into a motivational powerhouse. Weight visualization turns 
numbers into stories, users see progress, stay consistent, and crush goals with confidence. 
Diagram overhauls ensure the project shines for future maintainers.