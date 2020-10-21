package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

import java.util.ArrayList;
import java.util.List;

/*
    Represents a list of courses inputted by the user
*/
public class CourseList implements Writable {

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
    // EFFECTS: if this course list contains a course with given name, removes it and returns true
    //          otherwise returns false
    public boolean deleteCourse(String name) {
        int index = getCourseIndexByName(name);
        if (index >= 0) {
            courses.remove(index);
            return true;
        } else {
            return false;
        }
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
