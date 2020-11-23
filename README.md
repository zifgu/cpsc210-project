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
+ As a user, I want to be prompted to save my data before quitting

## Phase 4: Task 2
The CourseList class has a robust design, since each method specifies a behaviour for all possible inputs.
In particular, the allValidSchedules method used to require that the size of the schedule is positive. I have redesigned
this method so that it throws a ScheduleSizeException when the given schedule size is unfit for calculating schedules,
which includes 3 cases:
+ It is negative or zero
+ It exceeds the number of courses available to fill the schedule
+ It is smaller than the number of required courses, meaning not all required courses will be included

## Phase 4: Task 3
Future design improvements:
+ Remove the schedule calculation method from CourseList entirely, which would improve cohesion by a small
amount. I would instead concentrate this functionality inside the ScheduleCalculator class, and make it able to
operate on any CourseList object.
+ Remove the bidirectional associations between Course and Section, and between Section and Timeslot, since the core
mechanism of the application does not seem to require a Section to access its associated Course or a Timeslot to access
its associated Section. Currently, this creates unnecessary coupling that makes both classes more difficult to maintain.
+ Make some inner classes in CourseEditor their own separate classes: the panel for saving and loading,
the panel for calculating, and the class for playing sound. The present length of the CourseEditor class and the
clustering of all functionality in one large class makes it difficult to read and change. Furthermore, the fields for
the selected course, section, and timeslot are only used in some parts of the course editor.
    + To accomplish this, I would need to solve the difficulty where all the inner classes require access to the
courseList field in CourseEditor.