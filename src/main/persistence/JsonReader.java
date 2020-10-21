package persistence;

import model.Course;
import model.CourseList;
import model.Section;
import model.Timeslot;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Scanner;

public class JsonReader {
    private String source;

    // EFFECTS: constructs reader to read from source file
    // TODO: give credit
    public JsonReader(String source) {
        this.source = source;
    }

    // EFFECTS: reads course list from file and returns it
    // TODO: give credit, add exceptions
    // source: https://www.journaldev.com/875/java-read-file-to-string
    public CourseList read() throws IOException {
        Scanner scanner = new Scanner(Paths.get(source), StandardCharsets.UTF_8.name());
        String content = scanner.useDelimiter("\\A").next();
        scanner.close();
        JSONObject jsonObject = new JSONObject(content);
        return readCourseList(jsonObject);
    }

    // EFFECTS: converts contents of JSON object to a CourseList
    private CourseList readCourseList(JSONObject jsonObject) {
        CourseList courseList = new CourseList();

        JSONArray courses = jsonObject.getJSONArray("courses");
        for (Object o : courses) {
            JSONObject courseObj = (JSONObject) o;
            courseList.addCourse(readCourse(courseObj));
        }
        return courseList;
    }

    // EFFECTS: converts contents of JSON object for a course to a Course
    // TODO: specification
    private Course readCourse(JSONObject courseObj) {
        String name = courseObj.getString("name");
        boolean required = courseObj.getBoolean("required");

        Course course = new Course(name, required);

        JSONArray sectionList = courseObj.getJSONArray("sections");
        for (Object o : sectionList) {
            JSONObject sectionObj = (JSONObject) o;
            course.addSection(readSection(sectionObj, course));
        }

        return course;
    }

    // EFFECTS: converts contents of JSON object for a section to a Section of the given Course
    // TODO: specification
    private Section readSection(JSONObject sectionObj, Course c) {
        String name = sectionObj.getString("name");

        Section section = new Section(name, c);

        JSONArray timeslotList = sectionObj.getJSONArray("times");
        for (Object o : timeslotList) {
            JSONObject timeslotObj = (JSONObject) o;
            section.addTimeslot(readTimeslot(timeslotObj, section));
        }

        return section;
    }

    // EFFECTS: converts contents of JSON object for a time to a Timeslot of the given Section
    // TODO: specification
    private Timeslot readTimeslot(JSONObject timeslotObj, Section s) {
        int term = timeslotObj.getInt("term");
        DayOfWeek day = DayOfWeek.valueOf(timeslotObj.getString("day"));
        LocalTime start = LocalTime.parse(timeslotObj.getString("start"));
        LocalTime end = LocalTime.parse(timeslotObj.getString("end"));

        return new Timeslot(term, day, start, end, s);
    }
}
