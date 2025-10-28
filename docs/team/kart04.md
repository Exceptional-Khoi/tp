# Nary - Project Portfolio Page

## Overview
FitChasers is a desktop fitness tracking application that empowers users to log, organize, and analyze their 
workout sessions through an intuitive command-line interface. It is written in Java and incorporates approximately 
4 kLoC of well-structured code.

## Summary of Contributions
Given below are my contributions to the project:

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

### New Feature: Robust Workout Deletion with User Confirmation
 * Allows users to delete workouts by name or date using `/delete [WORKOUT_NAME]` or `/delete d/[DATE]`
 * Shows indexed list of workouts matching criteria before deletion
 * Requires user confirmation before permanently removing data
 * Supports batch deletion of multiple workouts

Justification:
Provides users with deletion with clear feedback and confirmation steps, preventing accidental data loss.

### New_feature: Enhanced Workout Session Management
* Improved `/create_workout` and `/end_workout` command to handle partial date/time input with smart defaulting.
* Uses current date and time when user omits these parameters
* Validates that end time is not before start time with retry logic

Justification:
Reduces user errors by allowing flexible input while maintaining data integrity through validation.

