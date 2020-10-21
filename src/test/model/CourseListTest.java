package model;

import exceptions.InvalidSyntaxException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.ScheduleApp;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CourseListTest {
    private CourseList courses;
    private Course courseA;
    private Course courseB;

    @BeforeEach
    public void setup() {
        courses = new CourseList();
        courseA = new Course("A", false);
        courseB = new Course("B", true);
    }

    @Test
    public void testConstructor() {
        assertTrue(courses.getCourses().isEmpty());
    }

    @Test
    public void testAddCourseEmpty() {
        assertTrue(courses.addCourse(courseA));
        assertEquals(1, courses.numCourses());
        assertTrue(courses.containsCourse("A"));
    }

    @Test
    public void testAddCourseRepeat() {
        courses.addCourse(courseA);
        assertFalse(courses.addCourse(courseA));
        assertEquals(1, courses.numCourses());
        assertTrue(courses.containsCourse("A"));
    }

    @Test
    public void testAddCourseSameName() {
        Course courseARepeat = new Course("A", true);

        courses.addCourse(courseA);
        assertFalse(courses.addCourse(courseARepeat));
        assertEquals(1, courses.numCourses());
        assertTrue(courses.containsCourse("A"));
    }

    @Test
    public void testDeleteCourseEmpty() {
        assertFalse(courses.deleteCourse("A"));
        assertEquals(0, courses.numCourses());
    }

    @Test
    public void testDeleteCourseDifferent() {
        courses.addCourse(courseA);
        assertFalse(courses.deleteCourse("B"));
        assertEquals(1, courses.numCourses());
        assertTrue(courses.containsCourse("A"));
    }

    @Test
    public void testDeleteCourse() {
        courses.addCourse(courseA);
        assertTrue(courses.deleteCourse("A"));
        assertEquals(0, courses.numCourses());
    }

    @Test
    public void testDeleteCourseOne() {
        courses.addCourse(courseA);
        courses.addCourse(courseB);
        assertTrue(courses.deleteCourse("B"));
        assertEquals(1, courses.numCourses());
        assertTrue(courses.containsCourse("A"));
    }

    @Test
    public void testNumElectivesNone() {
        assertEquals(0, courses.numElectives());
    }

    @Test
    public void testNumElectivesOne() {
        courses.addCourse(courseA);
        courses.addCourse(courseB);
        assertEquals(1, courses.numElectives());
    }

    @Test
    public void testNumElectivesMany() {
        for (int i = 0; i < 5; i++) {
            Course testCourse = new Course(Integer.toString(i), i % 2 == 0);
            courses.addCourse(testCourse);
        }
        assertEquals(2, courses.numElectives());
    }

    @Test
    public void testGetCourseByNameListEmpty() {
        assertNull(courses.getCourseByName("A"));
    }

    @Test
    public void testGetCourseByNameCourseExists() {
        courses.addCourse(courseA);
        assertEquals(courseA, courses.getCourseByName("A"));
    }

    @Test
    public void testGetCourseByNameCourseDifferent() {
        courses.addCourse(courseB);
        assertNull(courses.getCourseByName("A"));
    }

    @Test
    public void testAllValidSchedulesEmpty() {
        assertFalse(courses.allValidSchedules(3));
        assertEquals(0, courses.getAllValidSchedules().size());
    }

    @Test
    public void testAllValidSchedulesOneSection() {
        Section sectionB1 = new Section("B1", courseB);
        Timeslot timeB1 = new Timeslot(1, DayOfWeek.MONDAY, LocalTime.of(13,0), LocalTime.of(14, 0), sectionB1);

        sectionB1.addTimeslot(timeB1);
        courseB.addSection(sectionB1);
        courses.addCourse(courseB);

        assertTrue(courses.allValidSchedules(1));
        List<Schedule> schedules = courses.getAllValidSchedules();
        assertEquals(1, schedules.size());
        assertTrue(schedules.get(0).containsSection(sectionB1));
    }

    @Test
    public void testAllValidSchedulesTwoElectives() {
        Course courseR = makeCourseWithSection("R", true, 1, DayOfWeek.MONDAY, LocalTime.of(13, 0), 2);
        courseA = makeCourseWithSection("A", false, 1, DayOfWeek.MONDAY, LocalTime.of(14, 0), 2);
        courseB = makeCourseWithSection("B", false, 1, DayOfWeek.MONDAY, LocalTime.of(9, 0), 2);

        courses.addCourse(courseR);
        courses.addCourse(courseA);
        courses.addCourse(courseB);

        assertTrue(courses.allValidSchedules(2));
        List<Schedule> schedules = courses.getAllValidSchedules();
        assertEquals(2, schedules.size());
        assertEquals(2, schedules.get(0).numCourses());
        assertTrue(schedules.get(0).containsCourse(courseR));
        assertTrue(schedules.get(0).containsCourse(courseA));

        assertEquals(2, schedules.get(1).numCourses());
        assertTrue(schedules.get(1).containsCourse(courseR));
        assertTrue(schedules.get(1).containsCourse(courseB));
    }

    @Test
    public void testAllValidSchedulesTwoRequired() {
        courseA = makeCourseWithSection("A", true, 1, DayOfWeek.MONDAY, LocalTime.of(14,0), 2);
        Section sectionA2 = addSection(courseA, "A2", 1, DayOfWeek.MONDAY, LocalTime.of(9, 0), 2);

        courseB = makeCourseWithSection("B", true, 1, DayOfWeek.MONDAY, LocalTime.of(14, 0), 2);
        Section sectionB2 = addSection(courseB, "B2", 1, DayOfWeek.MONDAY, LocalTime.of(9, 0), 2);

        courses.addCourse(courseA);
        courses.addCourse(courseB);

        assertTrue(courses.allValidSchedules(2));
        List<Schedule> schedules = courses.getAllValidSchedules();
        assertEquals(2, schedules.size());
        assertTrue(schedules.get(0).containsSection(sectionB2));
        assertTrue(schedules.get(1).containsSection(sectionA2));
    }

    @Test
    public void testAllValidSchedulesRequiredOverlap() {
        courseA = makeCourseWithSection("A", true, 1, DayOfWeek.MONDAY, LocalTime.of(14,0), 2);
        courseB = makeCourseWithSection("B", true, 1, DayOfWeek.MONDAY, LocalTime.of(14, 30), 3);

        courses.addCourse(courseA);
        courses.addCourse(courseB);

        assertFalse(courses.allValidSchedules(2));
        List<Schedule> schedules = courses.getAllValidSchedules();
        assertEquals(0, schedules.size());
    }

    @Test
    public void testAllValidSchedulesMultipleSections() {
        courseA = makeCourseWithSection("A", true, 1, DayOfWeek.MONDAY, LocalTime.of(14,0), 2);
        courseB = makeCourseWithSection("B", true, 1, DayOfWeek.MONDAY, LocalTime.of(14, 30), 3);
        Section sectionA2 = addSection(courseA, "A2", 1, DayOfWeek.WEDNESDAY, LocalTime.of(14, 0), 2);

        courses.addCourse(courseA);
        courses.addCourse(courseB);

        assertTrue(courses.allValidSchedules(2));
        List<Schedule> schedules = courses.getAllValidSchedules();
        assertEquals(1, schedules.size());
        assertTrue(schedules.get(0).containsSection(sectionA2));
    }

    @Test
    public void testAllValidSchedulesMultipleElectiveSections() {
        courseA = makeCourseWithSection("A", false, 1, DayOfWeek.MONDAY, LocalTime.of(14,0), 2);
        courseB = makeCourseWithSection("B", true, 1, DayOfWeek.MONDAY, LocalTime.of(9, 0), 2);
        Section sectionA2 = addSection(courseA, "A2", 1, DayOfWeek.WEDNESDAY, LocalTime.of(14, 0), 2);

        courses.addCourse(courseA);
        courses.addCourse(courseB);

        assertTrue(courses.allValidSchedules(2));
        List<Schedule> schedules = courses.getAllValidSchedules();
        assertEquals(2, schedules.size());
        assertTrue(schedules.get(1).containsSection(sectionA2));
    }

    @Test
    public void testAllValidSchedulesTwoTerms() {
        courseA = makeCourseWithSection("A", true, 1, DayOfWeek.MONDAY, LocalTime.of(14,0), 2);
        Section sectionA2 = addSectionMWF(courseA, "A2", 2, LocalTime.of(14, 0), 2);

        courseB = makeCourseWithSection("B", true, 1, DayOfWeek.MONDAY, LocalTime.of(14, 0), 2);
        Section sectionB2 = addSectionMWF(courseB, "B2", 2, LocalTime.of(14, 0), 2);

        courses.addCourse(courseA);
        courses.addCourse(courseB);

        assertTrue(courses.allValidSchedules(2));
        List<Schedule> schedules = courses.getAllValidSchedules();
        assertEquals(2, schedules.size());
        assertTrue(schedules.get(0).containsSection(sectionB2));
        assertTrue(schedules.get(1).containsSection(sectionA2));
    }

    @Test
    public void testAllValidSchedules() {
        courseA = new Course("A", true);
        courseB = new Course("B", true);
        Course courseC = new Course("C", false);
        Course courseD = new Course("D", false);
        Course courseE = new Course("E", false);

        Section sectionA1 = addSectionMWF(courseA, "A1", 1, LocalTime.of(9, 0), 2);
        Section sectionA2 = addSectionMWF(courseA, "A2", 1, LocalTime.of(12, 0), 2);
        Section sectionB1 = addSectionMWF(courseB, "B1", 1, LocalTime.of(12, 0), 2);
        Section sectionB2 = addSectionMWF(courseB, "B2", 1, LocalTime.of(13, 0), 2);
        Section sectionC1 = addSectionMWF(courseC, "C1", 1, LocalTime.of(10, 0), 2);
        Section sectionC2 = addSectionMWF(courseC, "C2", 1, LocalTime.of(13, 0), 2);
        Section sectionD1 = addSectionMWF(courseD, "D1", 1, LocalTime.of(9, 0), 2);
        Section sectionE1 = addSectionMWF(courseE, "E1", 1, LocalTime.of(12, 0), 2);
        Section sectionE2 = addSectionMWF(courseE, "E2", 1, LocalTime.of(13, 0), 2);

        courses.addCourse(courseA);
        courses.addCourse(courseB);
        courses.addCourse(courseC);
        courses.addCourse(courseD);
        courses.addCourse(courseE);

        assertTrue(courses.allValidSchedules(4));
        List<Schedule> schedules = courses.getAllValidSchedules();
        assertEquals(3, schedules.size());

        assertEquals(4, schedules.get(0).numCourses());
        assertTrue(schedules.get(0).containsSection(sectionA1));
        assertTrue(schedules.get(0).containsSection(sectionB1));
        assertTrue(schedules.get(0).containsSection(sectionC1));
        assertTrue(schedules.get(0).containsSection(sectionE2));

        assertEquals(4, schedules.get(1).numCourses());
        assertTrue(schedules.get(1).containsSection(sectionA1));
        assertTrue(schedules.get(1).containsSection(sectionB2));
        assertTrue(schedules.get(1).containsSection(sectionC1));
        assertTrue(schedules.get(1).containsSection(sectionE1));

        assertEquals(4, schedules.get(2).numCourses());
        assertTrue(schedules.get(2).containsSection(sectionA2));
        assertTrue(schedules.get(2).containsSection(sectionB2));
        assertTrue(schedules.get(2).containsSection(sectionC1));
        assertTrue(schedules.get(2).containsSection(sectionD1));
    }

    @Test
    public void testAllValidSchedulesFreeElectives() {
        courseA = new Course("A", true);
        courseB = new Course("B", false);
        Course courseC = new Course("C", true);
        Course courseD = new Course("D", true);
        Course courseE = new Course("E", false);

        Section sectionA1 = addSectionMWF(courseA, "A1", 1, LocalTime.of(9, 0), 2);
        Section sectionA2 = addSectionMWF(courseA, "A2", 1, LocalTime.of(12, 0), 2);
        Section sectionB1 = addSectionMWF(courseB, "B1", 1, LocalTime.of(12, 0), 2);
        Section sectionB2 = addSectionMWF(courseB, "B2", 1, LocalTime.of(13, 0), 2);
        Section sectionC1 = addSectionMWF(courseC, "C1", 1, LocalTime.of(10, 0), 2);
        Section sectionC2 = addSectionMWF(courseC, "C2", 1, LocalTime.of(13, 0), 2);
        Section sectionD1 = addSectionMWF(courseD, "D1", 1, LocalTime.of(9, 0), 2);
        Section sectionE1 = addSectionMWF(courseE, "E1", 1, LocalTime.of(12, 0), 2);
        Section sectionE2 = addSectionMWF(courseE, "E2", 1, LocalTime.of(13, 0), 2);

        courses.addCourse(courseA);
        courses.addCourse(courseB);
        courses.addCourse(courseC);
        courses.addCourse(courseD);
        courses.addCourse(courseE);

        assertTrue(courses.allValidSchedules(4));
        List<Schedule> schedules = courses.getAllValidSchedules();
        assertEquals(2, schedules.size());

        assertEquals(4, schedules.get(0).numCourses());
        assertTrue(schedules.get(0).containsSection(sectionA2));
        assertTrue(schedules.get(0).containsSection(sectionB2));
        assertTrue(schedules.get(0).containsSection(sectionC1));
        assertTrue(schedules.get(0).containsSection(sectionD1));

        assertEquals(4, schedules.get(1).numCourses());
        assertTrue(schedules.get(1).containsSection(sectionA2));
        assertTrue(schedules.get(1).containsSection(sectionC1));
        assertTrue(schedules.get(1).containsSection(sectionD1));
        assertTrue(schedules.get(1).containsSection(sectionE2));
    }

    @Test
    // A temporary test to make sure the InvalidSyntaxException is thrown for code coverage
    // Note that not all test cases for the exception are covered because UI tests are not required
    public void testInvalidSyntaxException() {
        String input = "set course cpsc210 section 102"; // "section" is not one of "true"/"false"
        try {
            ScheduleApp app = new ScheduleApp(input);
            fail("Exception was not thrown when it should have been");
        } catch (InvalidSyntaxException e) {
            System.out.println("Exception was thrown");
        }
    }

    @Test
    public void testToJsonEmptyCourses() {
        JSONObject json = courses.toJson();
        JSONArray courseList = (JSONArray) json.get("courses");
        assertEquals(0, courseList.length());
    }

    @Test
    public void testToJson() {
        courses.addCourse(courseA);

        JSONObject json = courses.toJson();
        JSONArray courseList = json.getJSONArray("courses");
        assertEquals(1, courseList.length());

        JSONObject course = courseList.getJSONObject(0);
        assertEquals("A", course.get("name"));
        assertEquals(false, course.get("required"));

        JSONArray sections = course.getJSONArray("sections");
        assertEquals(0, sections.length());
    }

    // TODO: tests with actual sections?

    private Course makeCourseWithSection(String name, boolean required, int term, DayOfWeek day, LocalTime start, int
            duration) {
        Course course = new Course(name, required);
        Section section = new Section(name + "1", course);
        Timeslot timeslot = new Timeslot(term, day, start, start.plusMinutes(duration * 30), section);
        section.addTimeslot(timeslot);
        course.addSection(section);

        return course;
    }

    private Section addSection(Course course, String name, int term, DayOfWeek day, LocalTime start, int duration) {
        Section section = new Section(name, course);
        Timeslot timeslot = new Timeslot(term, day, start, start.plusMinutes(duration * 30), section);
        section.addTimeslot(timeslot);
        course.addSection(section);
        return section;
    }

    private Section addSectionMWF(Course course, String name, int term, LocalTime start, int duration) {
        Section section = new Section(name, course);
        for (int i = 1; i <= 5; i+=2) {
            Timeslot timeslot = new Timeslot(term, DayOfWeek.of(i), start, start.plusMinutes(duration * 30), section);
            section.addTimeslot(timeslot);
        }
        course.addSection(section);
        return section;
    }
}
