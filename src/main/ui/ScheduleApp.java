package ui;

import exceptions.InvalidSyntaxException;
import exceptions.ScheduleSizeException;
import model.*;
import persistence.JsonReader;
import persistence.JsonWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;

public class ScheduleApp {
    private CourseList courseList;
    private Scanner input;
    private JsonReader reader;
    private JsonWriter writer;
    private static final String FILE_NAME = "./data/courselist.json";

    // EFFECTS: runs the schedule calculation application
    public ScheduleApp() {
        courseList = new CourseList();
        input = new Scanner(System.in);
        input.useDelimiter("\n");
        reader = new JsonReader(FILE_NAME);
        writer = new JsonWriter(FILE_NAME);
        runApp();
    }

    // EFFECTS: makes a schedule app for testing and attempts to process rawInput
    public ScheduleApp(String rawInput) throws InvalidSyntaxException {
        ArrayList<String> command = new ArrayList<>(Arrays.asList(rawInput.split(" ")));
        setCourseRequired(command);
    }

    // EFFECTS: processes user input
    // Based off the Teller project's runTeller method
    public void runApp() {
        System.out.println("Welcome!");
        showHowToUse();
        boolean keepGoing = true;
        String command;

        while (keepGoing) {
            System.out.print("Enter a command: ");
            command = input.next().toLowerCase();
            String[] splitCommand = command.split(" ");
            ArrayList<String> splitList = new ArrayList<>(Arrays.asList(splitCommand));

            if (splitList.get(0).equals("quit")) {
                keepGoing = false;
            } else {
                try {
                    processCommand(splitList);
                } catch (IndexOutOfBoundsException | InvalidSyntaxException e) {
                    invalidCommand();
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid time format (use 09:00 not 9:00): try again.");
                }
            }
        }
        saveBeforeQuitting();
        input.close();
    }

    // MODIFIES: this, command
    // EFFECTS: processes user's commands from console
    private void processCommand(ArrayList<String> command) throws InvalidSyntaxException {
        switch (command.get(0)) {
            case "add": addCourse(command);
                break;
            case "delete": deleteCourse(command);
                break;
            case "set": setCourseRequired(command);
                break;
            case "course": selectCourse(command);
                break;
            case "calculate": calculateSchedules(command);
                break;
            case "display": displayCourses();
                break;
            case "help": showHowToUse();
                break;
            case "save": saveCourses();
                break;
            case "load": loadCourses();
                break;
            default: invalidCommand();
        }
    }

    // EFFECTS: prints instructions for the app to console
    private void showHowToUse() {
        System.out.println("Add a course:                     add course cpsc110 true");
        System.out.println("Add a section to a course:        course cpsc110 add section 101");
        System.out.println("Add a time to a section:          course cpsc110 section 101 add time 1 24 14:00 15:30");
        System.out.println("Change a course's status:         set course cpsc110 false");
        System.out.println("Delete a course:                  delete course cpsc110");
        System.out.println("Delete a section:                 course cpsc110 delete section 101");
        System.out.println("Delete a time:                    course cpsc110 section 101 delete time 1 24 14:00 15:30");
        System.out.println("Calculate all n-course schedules: calculate n");
        System.out.println("Show course list:                 display courses");
        System.out.println("Show calculated schedules:        display schedules");

        System.out.println("\nTo get help:                      help");
        System.out.println("To exit app:                      quit");
        System.out.println("Save course list:                 save");
        System.out.println("Load course list:                 load");
    }

    // EFFECTS: prints message that the command is invalid
    private void invalidCommand() {
        System.out.println("Invalid command: try again.");
    }

    // EFFECTS: prompts user to save data before quitting
    private void saveBeforeQuitting() {
        System.out.println("Would you like to save your data before quitting?");
        System.out.println("\ty -> yes");
        System.out.println("\tn -> no");

        String proceed = input.next().toLowerCase();
        if (proceed.equals("y")) {
            saveCourses();
        }
    }

    // MODIFIES: this, command
    // EFFECTS: adds a new course, and optionally sections/timeslots, to courseList
    private void addCourse(ArrayList<String> command) throws InvalidSyntaxException {
        if (!command.get(0).equals("add") || !command.get(1).equals("course")) {
            throw new InvalidSyntaxException();
        }
        String name = command.get(2);
        if (!command.get(3).equals("true") && !command.get(3).equals("false")) {
            throw new InvalidSyntaxException();
        }
        boolean required = Boolean.parseBoolean(command.get(3));
        Course newCourse = new Course(name, required);
        if (courseList.addCourse(newCourse)) {
            System.out.println("Successfully added course " + newCourse + ".");
            command.subList(0, 4).clear();
            if (command.size() >= 2) {
                addSection(command, newCourse);
            }
        } else {
            System.out.println("Unsuccessful add: course with given name already exists.");
        }
    }

    // MODIFIES: this, command
    // EFFECTS: performs add or delete operations on sections within the selected course
    private void selectCourse(ArrayList<String> command) throws InvalidSyntaxException {
        if (!command.get(0).equals("course")) {
            throw new InvalidSyntaxException();
        }
        Course c = courseList.getCourseByName(command.get(1));
        if (c != null) {
            String commandKey = command.get(2);
            switch (commandKey) {
                case "add": command.subList(0, 3).clear();
                    addSection(command, c);
                    break;
                case "delete": command.subList(0, 3).clear();
                    deleteSection(command, c);
                    break;
                case "section": command.subList(0, 2).clear();
                    selectSection(command, c);
                    break;
                default:
                    throw new InvalidSyntaxException();
            }
        } else {
            System.out.println("Unsuccessful select: course not found.");
        }
    }

    // MODIFIES: this
    // EFFECTS: deletes a course from courseList
    private void deleteCourse(ArrayList<String> command) throws InvalidSyntaxException {
        if (!command.get(0).equals("delete") || !command.get(1).equals("course")) {
            throw new InvalidSyntaxException();
        }
        String name = command.get(2);
        if (courseList.deleteCourse(new Course(name, false))) {
            System.out.println("Successfully deleted course " + name + ".");
        } else {
            System.out.println("Unsuccessful delete: course not found.");
        }
    }

    // MODIFIES: this
    // EFFECTS: sets a course as required/not required
    private void setCourseRequired(ArrayList<String> command) throws InvalidSyntaxException {
        if (!command.get(0).equals("set") || !command.get(1).equals("course")) {
            throw new InvalidSyntaxException();
        }
        String name = command.get(2);
        String booleanExpected = command.get(3);
        if (!booleanExpected.equals("true") && !booleanExpected.equals("false")) {
            throw new InvalidSyntaxException();
        }
        boolean required = Boolean.parseBoolean(booleanExpected);
        Course c = courseList.getCourseByName(name);
        c.setRequired(required);
        System.out.println("Successfully changed status to " + c + ".");
    }

    // MODIFIES: this, command
    // EFFECTS: adds a new section, and optionally its timeslots, to c
    private void addSection(ArrayList<String> command, Course c) throws InvalidSyntaxException {
        if (!command.get(0).equals("section")) {
            throw new InvalidSyntaxException();
        }
        String name = command.get(1);
        Section s = new Section(name, c);
        if (c.addSection(s)) {
            System.out.println("Successfully added section " + name + " to course " + c + ".");
            command.subList(0, 2).clear();
            if (command.size() > 0) {
                addTimeslots(command, s);
            }
        } else {
            System.out.println("Unsuccessful add: section with given name already exists.");
        }
    }

    // MODIFIES: this, command
    // EFFECTS: performs add/delete operations on timeslots within the selected section
    // TODO: FIX BUG
    private void selectSection(ArrayList<String> command, Course c) throws InvalidSyntaxException {
        if (!command.get(0).equals("section")) {
            throw new InvalidSyntaxException();
        }
        Section s = c.getSectionByName(command.get(1));
        if (s != null) {
            String commandKey = command.get(2);
            command.subList(0, 3).clear();
            switch (commandKey) {
                case "add":
                    addTimeslots(command, s);
                    break;
                case "delete":
                    deleteTimeslot(command, s);
                    break;
                default:
                    throw new InvalidSyntaxException();
            }
        } else {
            System.out.println("Unsuccessful select: section not found.");
        }
    }

    // MODIFIES: this
    // EFFECTS: deletes a section from c
    private void deleteSection(ArrayList<String> command, Course c) throws InvalidSyntaxException {
        if (!command.get(0).equals("section")) {
            throw new InvalidSyntaxException();
        }
        String name = command.get(1);
        if (c.deleteSection(new Section(name, c))) {
            System.out.println("Successfully deleted section " + name + " from course " + c + ".");
        } else {
            System.out.println("Unsuccessful delete: section not found.");
        }
    }

    // MODIFIES: this
    // EFFECTS: adds one or multiple timeslots to s
    private void addTimeslots(ArrayList<String> command, Section s) throws InvalidSyntaxException {
        if (!command.get(0).equals("time")) {
            throw new InvalidSyntaxException();
        }
        int term = Integer.parseInt(command.get(1));
        LocalTime start = LocalTime.parse(command.get(3));
        LocalTime end = LocalTime.parse(command.get(4));
        String days = command.get(2);
        for (char ch : days.toCharArray()) {
            int day = Character.getNumericValue(ch);
            DayOfWeek dayOfWeek = DayOfWeek.of(day);
            Timeslot time = new Timeslot(term, dayOfWeek, start, end, s);
            if (s.addTimeslot(time)) {
                System.out.println("Successfully added time " + time + " to section " + s.getName() + ".");
            } else {
                System.out.println("Unsuccessful add of time " + time + ": given time already exists.");
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: deletes one timeslot from s
    private void deleteTimeslot(ArrayList<String> command, Section s) throws InvalidSyntaxException {
        if (!command.get(0).equals("time")) {
            throw new InvalidSyntaxException();
        }
        int term = Integer.parseInt(command.get(1));
        LocalTime start = LocalTime.parse(command.get(3));
        LocalTime end = LocalTime.parse(command.get(4));
        DayOfWeek day = DayOfWeek.of(Integer.parseInt(command.get(2)));
        Timeslot time = new Timeslot(term, day, start, end, s);
        if (s.deleteTimeslot(time)) {
            System.out.println("Successfully deleted time " + time + " from section " + s.getName() + ".");
        } else {
            System.out.println("Unsuccessful delete of time " + time + ": time not found.");
        }
    }

    // REQUIRES: first string in command is "calculate"
    // MODIFIES: this
    // EFFECTS: courseList now contains all valid schedules that can be made from the courses it currently contains
    private void calculateSchedules(ArrayList<String> command) throws InvalidSyntaxException {
        int numCourses = Integer.parseInt(command.get(1));
        try {
            List<Schedule> schedules = courseList.allValidSchedules(numCourses);
            displaySchedules(schedules);
            System.out.println("Successfully calculated schedules.");
        } catch (ScheduleSizeException e) {
            System.out.println("Unsuccessful calculation: no schedules possible.");
        }
    }

    // EFFECTS: prints all valid schedules that can be made from the current course list to console
    private void displaySchedules(List<Schedule> allSchedules) throws InvalidSyntaxException {
        System.out.println("Retrieved " + allSchedules.size() + " possible schedules.");
        System.out.println("How many schedules would you like to see? Enter a number or \"all\" to show all.");

        String numSchedules = input.next().toLowerCase();

        if (numSchedules.equals("all")) {
            printAllSchedules(allSchedules);
        } else {
            try {
                int batchSize = Integer.parseInt(numSchedules);
                printSomeSchedules(allSchedules, batchSize);
            } catch (NumberFormatException e) {
                System.out.println("Not a number: try again.");
            }
        }
    }

    // EFFECTS: prints to console all courses currently in courseList
    private void displayCourses() {
        for (Course c : courseList) {
            System.out.println(c);
        }
    }

    // EFFECTS: prints all schedules in list of schedules
    private void printAllSchedules(List<Schedule> schedules) {
        for (Schedule s : schedules) {
            System.out.println(s);
        }
    }

    // EFFECTS: randomly selects batchSize schedules from list of schedules and prints them out
    private void printSomeSchedules(List<Schedule> schedules, int batchSize) throws InvalidSyntaxException {
        if (batchSize < 0) {
            throw new InvalidSyntaxException();
        }
        if (batchSize > schedules.size()) {
            printAllSchedules(schedules);
        } else {
            for (int i = 0; i < batchSize; i++) {
                Random r = new Random();
                int index = r.nextInt(schedules.size());
                System.out.println(schedules.get(index));
            }
        }
    }

    // EFFECTS: prompts user to save current course list to file, replacing any previous contents
    // based on the saving method from JsonSerializationDemo
    private void saveCourses() {
        System.out.println("Saving will replace any previously saved course list. Do you wish to continue?");
        System.out.println("\ty -> yes");
        System.out.println("\tn -> no");

        String proceed = input.next().toLowerCase();
        if (proceed.equals("y")) {
            try {
                writer.open();
                writer.writeCourseList(courseList);
                writer.close();
                System.out.println("Successfully saved course list.");
            } catch (FileNotFoundException e) {
                System.out.println("There was a problem saving: try again.");
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: prompts user to load course list from file, replacing the current course list
    // based on the loading method from JsonSerializationDemo
    private void loadCourses() {
        System.out.println("Loading will replace any courses added during this session. Do you wish to continue?");
        System.out.println("\ty -> yes");
        System.out.println("\tn -> no");

        String proceed = input.next().toLowerCase();
        if (proceed.equals("y")) {
            try {
                courseList = reader.read();
                System.out.println("Successfully loaded course list.");
            } catch (IOException e) {
                System.out.println("There was a problem loading: try again.");
            }
        }
    }

}