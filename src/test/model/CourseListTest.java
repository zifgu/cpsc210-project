package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
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
    public void testGetAllCourseNamesEmpty() {
        List<String> names = courses.getAllCourseNames();
        assertEquals(0, names.size());
    }

    @Test
    public void testGetAllCourseNames() {
        courses.addCourse(courseA);
        courses.addCourse(courseB);

        List<String> names = courses.getAllCourseNames();
        assertEquals(2, names.size());
        assertEquals("A", names.get(0));
        assertEquals("B", names.get(1));
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
        assertEquals(0, courses.numElectives());
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
    public void testAllValidSchedulesEmpty() {
        assertFalse(courses.allValidSchedules(3));
        assertEquals(0, courses.getAllValidSchedules().size());
    }

    @Test
    public void testAllValidSchedulesOneSection() {
        Section sectionB1 = new Section("B1", courseB);
        Timeslot timeB1 = new Timeslot(1, 1, LocalTime.of(13,0), 2, sectionB1);

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
        Course courseR = makeCourseWithSection("R", true, 1, 1, LocalTime.of(13, 0), 2);
        courseA = makeCourseWithSection("A", false, 1, 1, LocalTime.of(14, 0), 2);
        courseB = makeCourseWithSection("B", false, 1, 1, LocalTime.of(9, 0), 2);

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
        courseA = makeCourseWithSection("A", true, 1, 1, LocalTime.of(14,0), 2);
        Section sectionA2 = addSection(courseA, "A2", 1, 1, LocalTime.of(9, 0), 2);

        courseB = makeCourseWithSection("B", true, 1, 1, LocalTime.of(9, 0), 2);
        Section sectionB2 = addSection(courseB, "B2", 1, 1, LocalTime.of(14, 0), 2);

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
        courseA = makeCourseWithSection("A", true, 1, 1, LocalTime.of(14,0), 2);
        courseB = makeCourseWithSection("B", true, 1, 1, LocalTime.of(14, 30), 3);

        courses.addCourse(courseA);
        courses.addCourse(courseB);

        assertFalse(courses.allValidSchedules(2));
        List<Schedule> schedules = courses.getAllValidSchedules();
        assertEquals(0, schedules.size());
    }

    @Test
    public void testAllValidSchedulesTwoTerms() {
        courseA = makeCourseWithSection("A", true, 1, 1, LocalTime.of(14,0), 2);
        Section sectionA2 = addSectionMWF(courseA, "A2", 2, LocalTime.of(14, 0), 2);

        courseB = makeCourseWithSection("B", true, 1, 1, LocalTime.of(14, 0), 2);
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
        Course courseC = new Course("C", true);
        Course courseD = new Course("D", false);
        Course courseE = new Course("E", false);

        Section sectionA1 = addSectionMWF(courseA, "A1", 1, LocalTime.of(9, 0), 2);
        Section sectionA2 = addSectionMWF(courseA, "A2", 1, LocalTime.of(12, 0), 2);
        Section sectionB1 = addSectionMWF(courseA, "B1", 1, LocalTime.of(12, 1), 2);
        Section sectionB2 = addSectionMWF(courseA, "B2", 1, LocalTime.of(1, 2), 2);
        Section sectionC1 = addSectionMWF(courseA, "C1", 1, LocalTime.of(10, 0), 2);
        Section sectionC2 = addSectionMWF(courseA, "C2", 1, LocalTime.of(1, 0), 2);
        Section sectionD1 = addSectionMWF(courseA, "D1", 1, LocalTime.of(9, 0), 2);
        Section sectionE1 = addSectionMWF(courseA, "E1", 1, LocalTime.of(12, 0), 2);
        Section sectionE2 = addSectionMWF(courseA, "E2", 1, LocalTime.of(1, 0), 2);

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

        assertEquals(4, schedules.get(0).numCourses());
        assertTrue(schedules.get(1).containsSection(sectionA1));
        assertTrue(schedules.get(1).containsSection(sectionB1));
        assertTrue(schedules.get(1).containsSection(sectionC2));
        assertTrue(schedules.get(1).containsSection(sectionD1));

        assertEquals(4, schedules.get(0).numCourses());
        assertTrue(schedules.get(2).containsSection(sectionA2));
        assertTrue(schedules.get(2).containsSection(sectionB2));
        assertTrue(schedules.get(2).containsSection(sectionC1));
        assertTrue(schedules.get(2).containsSection(sectionD1));
    }


    private Course makeCourseWithSection(String name, boolean required, int term, int dayOfWeek, LocalTime start, int
            duration) {
        Course course = new Course(name, required);
        Section section = new Section(name + "1", course);
        Timeslot timeslot = new Timeslot(term, dayOfWeek, start, duration, section);
        section.addTimeslot(timeslot);
        course.addSection(section);

        return course;
    }

    private Section addSection(Course course, String name, int term, int dayOfWeek, LocalTime start, int duration) {
        Section section = new Section(name, course);
        Timeslot timeslot = new Timeslot(term, dayOfWeek, start, duration, section);
        section.addTimeslot(timeslot);
        course.addSection(section);
        return section;
    }

    private Section addSectionMWF(Course course, String name, int term, LocalTime start, int duration) {
        Section section = new Section(name, course);
        for (int i = 1; i <= 5; i+=2) {
            Timeslot timeslot = new Timeslot(term, i, start, duration, section);
            section.addTimeslot(timeslot);
        }
        course.addSection(section);
        return section;
    }
}
