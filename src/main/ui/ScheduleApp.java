package ui;

import exceptions.InvalidSyntaxException;
import model.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;

public class ScheduleApp {
    private CourseList courseList;
    private Scanner input;
    // TODO: Get rid of magic numbers
    // TODO: Edit the specifications; they're outdated

    // EFFECTS: runs the schedule calculation application
    public ScheduleApp() {
        courseList = new CourseList();
        input = new Scanner(System.in);
        runApp();
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
            command = input.nextLine();
            command = command.toLowerCase();
            String[] splitCommand = command.split(" ");

            ArrayList<String> splitList = new ArrayList<>(Arrays.asList(splitCommand));

            if (splitList.get(0).equals("quit")) {
                keepGoing = false;
            } else {
                processCommand(splitList);
            }
        }
        input.close();
    }

    // MODIFIES: this, command
    // EFFECTS: processes user's commands from console
    private void processCommand(ArrayList<String> command) {
        try {
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
                case "display": display(command);
                    break;
                case "help": showHowToUse();
                    break;
                default: invalidCommand();
            }
        } catch (IndexOutOfBoundsException | InvalidSyntaxException e) {
            invalidCommand();
        } catch (DateTimeParseException e) {
            System.out.println("Invalid time format (use 09:00, not 9:00): try again.");
        }
    }

    // EFFECTS: prints instructions for the app to console
    private void showHowToUse() {
        System.out.println("Examples of commands");
        System.out.println("Add a course:                     add course cpsc110 true");
        System.out.println("Add a section to a course:        course cpsc110 add section 101");
        System.out.println("Add a time to a section:          course cpsc110 section 101 add time 1 24 14:00 15:30");
        System.out.println("Change a course's status:         set course cpsc110 false");
        System.out.println("Delete a course:                  delete course cpsc110");
        System.out.println("Delete a section:                 course cpsc110 delete section 101");
        System.out.println("Delete a time:                    course cpsc110 section 101 delete time 1 24 14:00 15:30");
        System.out.println("Calculate all 5-course schedules: calculate 5");
        System.out.println("Show course list:                 display courses");
        System.out.println("Show calculated schedules:        display schedules");

        System.out.println("\nTo get help:                   help");
        System.out.println("To exit app:                      quit");
    }

    // EFFECTS: prints message that the command is invalid
    private void invalidCommand() {
        System.out.println("Invalid command: try again.");
    }

    // MODIFIES: this, command
    // EFFECTS: adds a course to courseList, and optionally section/timeslot info
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
    // EFFECTS: performs operations on selected course
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
        if (courseList.deleteCourse(name)) {
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
        boolean required = Boolean.parseBoolean(command.get(3));
        Course c = courseList.getCourseByName(name);
        c.setRequired(required);
        System.out.println("Successfully changed status to " + c + ".");
    }

    // MODIFIES: this, command
    // EFFECTS: adds a section, and optionally its timeslots, to a course
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
    // EFFECTS: performs operations on selected section
    private void selectSection(ArrayList<String> command, Course c) throws InvalidSyntaxException {
        if (!command.get(0).equals("section")) {
            throw new InvalidSyntaxException();
        }
        Section s = c.getSectionByName(command.get(1));
        if (s != null) {
            String commandKey = command.get(2);
            command.subList(0, 2).clear();
            if (commandKey.equals("add")) {
                addTimeslots(command, s);
            } else if (commandKey.equals("delete")) {
                deleteTimeslot(command, s);
            } else {
                throw new InvalidSyntaxException();
            }
        } else {
            System.out.println("Unsuccessful select: section not found.");
        }
    }

    // MODIFIES: this
    // EFFECTS: deletes a section from a course in courseList
    private void deleteSection(ArrayList<String> command, Course c) throws InvalidSyntaxException {
        if (!command.get(0).equals("delete") || !command.get(1).equals("section")) {
            throw new InvalidSyntaxException();
        }
        String name = command.get(2);
        if (c.deleteSection(name)) {
            System.out.println("Successfully deleted section " + name + " from course " + c + ".");
        } else {
            System.out.println("Unsuccessful delete: section not found.");
        }
    }

    // MODIFIES: this
    // EFFECTS: adds one or multiple timeslots to a section of a course
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
    // EFFECTS: deletes one timeslot from a section of a course
    private void deleteTimeslot(ArrayList<String> command, Section s) throws InvalidSyntaxException {
        if (!command.get(0).equals("delete") || !command.get(1).equals("time")) {
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
    private void calculateSchedules(ArrayList<String> command) {
        int numCourses = Integer.parseInt(command.get(1));
        if (courseList.allValidSchedules(numCourses)) {
            System.out.println("Successfully calculated schedules.");
        } else {
            System.out.println("Unsuccessful calculation: no schedules possible.");
        }
    }

    // REQUIRES: command begins with "show"
    // EFFECTS: prints out either the current course list or a batch of schedules in the course list
    private void display(ArrayList<String> command) throws InvalidSyntaxException {
        String commandKey = command.get(1);
        if (commandKey.equals("courses")) {
            displayCourses();
        } else if (commandKey.equals("schedules")) {
            displaySchedules();
        } else {
            throw new InvalidSyntaxException();
        }
    }

    // EFFECTS: prints all valid schedules that can be made from the current course list to console
    private void displaySchedules() throws InvalidSyntaxException {
        List<Schedule> allSchedules = courseList.getAllValidSchedules();
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
        for (Course c : courseList.getCourses()) {
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
}