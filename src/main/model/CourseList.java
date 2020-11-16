package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
    Represents a list of courses inputted by the user
*/
public class CourseList implements Writable {
    private Set<Course> courses;
    private List<Schedule> schedules;

    // EFFECTS: constructs a new course list with no courses and no possible schedules
    public CourseList() {
        courses = new HashSet<>();
        schedules = new ArrayList<>();
    }

    // getters
    public Set<Course> getCourses() {
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
    public boolean containsCourseWithName(String name) {
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
        return courses.add(course);
    }

    // MODIFIES: this
    // EFFECTS: if this course list contains the given course, removes it and returns true
    //          otherwise returns false
    public boolean deleteCourse(Course c) {
        return courses.remove(c);
    }

    // REQUIRES: numCourses > 0
    // MODIFIES: this
    // EFFECTS: adds to this CourseList all valid Schedules that can be generated from the current courses
    //          returns true if at least 1 schedule is possible, otherwise returns false
    //          A schedule is valid if there are no time conflicts and it has numCourses courses total, including
    //          all required courses
    public boolean allValidSchedules(int numCourses) {
        ScheduleCalculator sc = new ScheduleCalculator(numCourses, courses);
        schedules = sc.allValidSchedules();
        return !schedules.isEmpty();
    }

    // REQUIRES: allValidSchedules is called before calling this method
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

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        JSONArray courseList = new JSONArray();

        for (Course c : courses) {
            courseList.put(c.toJson());
        }

        json.put("courses", courseList);

        return json;
    }

}
