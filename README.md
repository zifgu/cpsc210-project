# Course schedule generator

## What is this application?

This application automates the process of organizing your required and elective courses into a schedule. 
As a student myself, I find that the most difficult part of creating a course schedule is putting the courses in order 
so that there are no time conflicts. The user of this application must input the courses they are interested 
in taking, and for each course, its status (required vs elective) and all time slots they are considering. The 
application will then calculate **all valid schedules** that can be made with the given list of courses and randomly
display a selection of them to the user. Rather than spending time putting together course lists, students can simply
browse the returned options and select the ones they prefer.

**Note:** Currently, this application does not yet support having multiple sections of one course in a schedule
(e.g. lab, tutorial).

## User Stories

+ As a user, I want to be able to add a course to my list of courses I’m considering.
+ As a user, I want to be able to declare a course as required or as an elective.
+ As a user, I want to be able to delete a course from my list of courses I’m considering.
+ As a user, I want to be able to add sections and times that I would be OK with to a course in my list of courses.
+ As a user, I want to be able to delete sections and times from a course in my list of courses.
+ As a user, I want to be able to receive a selection of valid schedules given the information I put in.

+ As a user, I want to be able to save courses, sections, and times to a file (I will not be able to save schedules)
+ As a user, I want to be able to load my previous course list from a file when I open the application