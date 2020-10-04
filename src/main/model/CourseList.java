package model;

import java.util.List;

public class CourseList {

    // EFFECTS: constructs a new course list with no courses and no possible schedules
    public CourseList() {

    }

    // EFFECTS: returns list of names of all courses in this course list
    public List<String> getAllCourseNames() {
        return null;
    }

    // MODIFIES: this
    // EFFECTS: if this CourseList already contains a course with the same name, returns false
    //          otherwise adds the course to this CourseList and returns true
    public boolean addCourse(Course course) {
        return false;
    }

    // MODIFIES: this
    // EFFECTS: removes the course with the given name from this CourseList
    //          returns true if successfully removed, false if this CourseList does not contain a course with given name
    public boolean deleteCourse(String name) {
        return false;
    }

    // EFFECTS: returns the number of courses in this CourseList
    public int numCourses() {
        return 0;
    }

    // EFFECTS: returns the number of non-required courses in this CourseList
    public int numElectives() {
        return 0;
    }

    // EFFECTS: returns true if this CourseList contains a course with the given name
    public boolean containsCourse(String name) {
        return false;
    }

    // MODIFIES: this
    // EFFECTS: adds to this CourseList all valid Schedules that can be generated from the current courses
    //          a schedule is valid if there are no time conflicts and it has numCourses courses total, including
    //          all required courses
    public boolean allValidSchedules(int numCourses) {
        return false;
    }

    // EFFECTS: returns a string displaying CourseList info in printable form
    public String toString() {
        return "";
    }

    // EFFECTS: returns the list of all valid Schedules that can be generated from the current courses
    public List<Schedule> getAllValidSchedules() {
        return null;
    }

}
