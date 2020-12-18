package model;

import exceptions.ScheduleSizeException;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.JsonReader;
import persistence.Writable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
    Represents a list of courses inputted by the user
*/
public class CourseList implements Writable {
    private Set<Course> courses;

    // EFFECTS: constructs a new course list with no courses and no possible schedules
    public CourseList() {
        courses = new HashSet<>();
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
        return courses.contains(new Course(name, false));
    }

    // MODIFIES: this
    // EFFECTS: sets name of c to name if c is in this course list and no other courses have the same name, returns true
    //          otherwise returns false
    public boolean changeCourseName(Course c, String name) {
        if (!containsCourseWithName(name)) {
            courses.remove(c);
            c.setName(name);
            return courses.add(c);
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

    // MODIFIES: this
    // EFFECTS: if numCourses <= 0, course list contains fewer than numCourses courses, or number of required courses
    //          < desired size of schedule, throws ScheduleSizeException
    //          otherwise, returns all valid Schedules that can be generated from the current courses
    //
    // NOTE:    A schedule is valid if there are no time conflicts and it has numCourses courses total, including
    //          all required courses
    public List<Schedule> allValidSchedules(int numCourses) throws ScheduleSizeException {
        if (numCourses <= 0 || numCourses > courses.size() || numCourses < courses.size() - numElectives()) {
            throw new ScheduleSizeException();
        }
        ScheduleCalculator sc = new ScheduleCalculator(numCourses, courses);
        return sc.allValidSchedules();
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

    // TODO: finish
    public boolean addCourseFromSSC(String department, String number, boolean required) {
        try {
            String s;
            Process p = Runtime.getRuntime().exec("/Users/markgu/Documents/GitHub/ubc-scraper/.venv/bin/python"
                    + " /Users/markgu/Documents/GitHub/ubc-scraper/course_scraper.py "
                    + department + " " + number);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            // read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            // read any errors from the attempted command
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
            JsonReader reader = new JsonReader("./data/temp_course.json");
            Course c = reader.readCourseResult();
            c.setRequired(required);

            return addCourse(c);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
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
