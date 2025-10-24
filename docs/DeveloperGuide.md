# Developer Guide

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

{Give non-functional requirements}

## Glossary

* *glossary item* - Definition

## Instructions for manual testing

{Give instructions on how to do a manual product testing e.g., how to load sample data to be used for testing}
