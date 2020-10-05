package model;

import java.util.ArrayList;
import java.util.List;

public class CourseList {
    public static final int EARLIEST_HOURS = 7;
    public static final int NUM_DAILY_HOURS = 15;
    public static final int TERMS = 2;
    public static final int INTERVALS_PER_HOUR = 2;

    private ArrayList<Course> courses;
    private ArrayList<Schedule> schedules;

    // EFFECTS: constructs a new course list with no courses and no possible schedules
    public CourseList() {
        courses = new ArrayList<>();
        schedules = new ArrayList<>();
    }

    // EFFECTS: returns list of names of all courses in this course list
    public List<String> getAllCourseNames() {
        ArrayList<String> names = new ArrayList<>();
        for (Course c : courses) {
            names.add(c.getName());
        }
        return names;
    }

    // EFFECTS: returns the number of courses in this CourseList
    public int numCourses() {
        return courses.size();
    }

    // EFFECTS: returns the number of non-required courses in this CourseList
    public int numElectives() {
        int count = 0;
        for (Course c : courses) {
            if (!c.getRequired()) {
                count++;
            }
        }
        return count;
    }

    // EFFECTS: returns true if this CourseList contains a course with the given name
    public boolean containsCourse(String name) {
        for (Course c : courses) {
            if (c.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    // MODIFIES: this
    // EFFECTS: if this CourseList already contains a course with the same name, returns false
    //          otherwise adds the course to this CourseList and returns true
    public boolean addCourse(Course course) {
        if (containsCourse(course.getName())) {
            return false;
        } else {
            courses.add(course);
            return true;
        }
    }

    // MODIFIES: this
    // EFFECTS: removes the course with the given name from this CourseList
    //          returns true if successfully removed, false if this CourseList does not contain a course with given name
    public boolean deleteCourse(String name) {
        int index = getCourseIndexByName(name);
        if (index >= 0) {
            courses.remove(index);
            return true;
        } else {
            return false;
        }
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
        return Integer.toString(numCourses()) + " courses";
    }

    // EFFECTS: returns the list of all valid Schedules that can be generated from the current courses
    public List<Schedule> getAllValidSchedules() {
        return schedules;
    }

    // EFFECTS: returns index of the course with the given name if it exists, otherwise returns -1
    private int getCourseIndexByName(String name) {
        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

}
