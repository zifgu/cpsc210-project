package model;

import java.util.ArrayList;
import java.util.List;

/*
    Represents a list of courses inputted by the user
*/
public class CourseList {

    private List<Course> courses;
    private List<Schedule> schedules;

    // EFFECTS: constructs a new course list with no courses and no possible schedules
    public CourseList() {
        courses = new ArrayList<>();
        schedules = new ArrayList<>();
    }

    // getters
    public List<Course> getCourses() {
        return courses;
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
        ScheduleCalculator sc = new ScheduleCalculator(numCourses, courses);
        schedules = sc.allValidSchedules();
        return !schedules.isEmpty();
    }

    // EFFECTS: returns the list of all valid Schedules that can be generated from the current courses
    public List<Schedule> getAllValidSchedules() {
        return schedules;
    }

    // EFFECTS: returns a course with the given name in this course list if it exists, otherwise returns null
    public Course getCourseByName(String name) {
        for (Course c : courses) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
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
