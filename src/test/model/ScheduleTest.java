package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class ScheduleTest {
    private Schedule testSchedule;
    private Course testCourse;
    private Section testSection;
    private LocalTime start;
    private LocalTime end;

    @BeforeEach
    public void setup() {
        start = LocalTime.of(13, 0);
        end = LocalTime.of(14, 0);

        testCourse = new Course("A", true);
        testSection = new Section("001", testCourse);
        testCourse.addSection(testSection);

        Timeslot testTime1 = new Timeslot(1, DayOfWeek.MONDAY, start, end, testSection);
        Timeslot testTime2 = new Timeslot(1, DayOfWeek.WEDNESDAY, start, end, testSection);
        Timeslot testTime3 = new Timeslot(1, DayOfWeek.FRIDAY, start, end, testSection);

        testSection.addTimeslot(testTime1);
        testSection.addTimeslot(testTime2);
        testSection.addTimeslot(testTime3);

        testSchedule = new Schedule();
    }

    @Test
    public void testConstructor() {
        assertEquals(0, testSchedule.numSections());
    }

    @Test
    public void testFillSectionEmpty() {
        assertTrue(testSchedule.fillSection(testSection));
        assertEquals(6, testSchedule.totalTime());
    }

    @Test
    public void testFillSectionOneOverlap() {
        Course otherCourse = new Course("B", false);
        Section otherSection = new Section("102", otherCourse);
        Timeslot otherTime = new Timeslot(1,DayOfWeek.WEDNESDAY, start, end.plusMinutes(30), otherSection);
        otherSection.addTimeslot(otherTime);

        assertTrue(testSchedule.fillSection(otherSection));
        assertFalse(testSchedule.fillSection(testSection));
        assertEquals(3, testSchedule.totalTime());
    }

    @Test
    public void testFillSectionManyOverlaps() {
        Course otherCourse = new Course("B", false);
        Section otherSection = new Section("102", otherCourse);
        Timeslot otherTime = new Timeslot(1,DayOfWeek.WEDNESDAY, LocalTime.of(13, 0), end, otherSection);
        Timeslot anotherTime = new Timeslot(1,DayOfWeek.FRIDAY, LocalTime.of(12, 0), end.minusMinutes(30), otherSection);
        otherSection.addTimeslot(otherTime);
        otherSection.addTimeslot(anotherTime);

        assertTrue(testSchedule.fillSection(otherSection));
        assertFalse(testSchedule.fillSection(testSection));
        assertEquals(5, testSchedule.totalTime());
    }

    @Test
    public void testFillSectionLeftBound() {
        Course otherCourse = new Course("B", false);
        Section otherSection = new Section("102", otherCourse);
        Timeslot otherTime = new Timeslot(1,DayOfWeek.WEDNESDAY, LocalTime.of(11, 30), start, otherSection);
        otherSection.addTimeslot(otherTime);

        testSchedule.fillSection(testSection);
        assertTrue(testSchedule.fillSection(otherSection));
        assertEquals(9, testSchedule.totalTime());
        assertTrue(testSchedule.containsSection(otherSection));
    }

    @Test
    public void testFillSectionRightBound() {
        Course otherCourse = new Course("B", false);
        Section otherSection = new Section("102", otherCourse);
        Timeslot otherTime = new Timeslot(1,DayOfWeek.WEDNESDAY, end, end.plusHours(1), otherSection);
        otherSection.addTimeslot(otherTime);

        testSchedule.fillSection(testSection);
        assertTrue(testSchedule.fillSection(otherSection));
        assertEquals(8, testSchedule.totalTime());
        assertTrue(testSchedule.containsSection(otherSection));
    }

    @Test
    public void testNumFilledIntervalsEmpty() {
        assertEquals(0, testSchedule.totalTime());
    }

    @Test
    public void testNumFilledIntervals() {
        testSchedule.fillSection(testSection);
        assertEquals(6, testSchedule.totalTime());
    }

    @Test
    public void testNumCoursesSectionsEmpty() {
        assertEquals(0, testSchedule.numCourses());
        assertEquals(0, testSchedule.numSections());
    }

    @Test
    public void testNumCoursesSectionsOne() {
        testSchedule.fillSection(testSection);
        assertEquals(1, testSchedule.numCourses());
        assertEquals(1, testSchedule.numSections());
    }

    @Test
    public void testNumCoursesSectionsTwo() {
        Course otherCourse = new Course("B", false);
        Section otherSection = new Section("102", otherCourse);
        Timeslot otherTime = new Timeslot(1,DayOfWeek.MONDAY, LocalTime.of(12, 0), start, otherSection);
        otherSection.addTimeslot(otherTime);

        testSchedule.fillSection(otherSection);
        testSchedule.fillSection(testSection);

        assertEquals(2, testSchedule.numCourses());
        assertEquals(2, testSchedule.numSections());
    }

    @Test
    public void testNumCoursesSectionsDifferent() {
        Section otherSection = new Section("102", testCourse);
        Timeslot otherTime = new Timeslot(1,DayOfWeek.MONDAY, LocalTime.of(12, 0), start, otherSection);
        otherSection.addTimeslot(otherTime);

        testSchedule.fillSection(otherSection);
        testSchedule.fillSection(testSection);

        assertEquals(1, testSchedule.numCourses());
        assertEquals(2, testSchedule.numSections());
    }

    @Test
    public void testNumElectivesNone() {
        assertEquals(0, testSchedule.numElectives());
    }

    @Test
    public void testNumElectivesOne() {
        Course otherCourse = new Course("B", false);
        Section otherSection = new Section("102", otherCourse);
        Timeslot otherTime = new Timeslot(1,DayOfWeek.MONDAY, LocalTime.of(12, 0), start, otherSection);
        otherSection.addTimeslot(otherTime);

        testSchedule.fillSection(otherSection);
        testSchedule.fillSection(testSection);

        assertEquals(1, testSchedule.numElectives());
    }

    @Test
    public void testNumElectivesMany() {
        testCourse.setRequired(false);
        Course otherCourse = new Course("B", false);
        Section otherSection = new Section("102", otherCourse);
        Timeslot otherTime = new Timeslot(1,DayOfWeek.MONDAY, LocalTime.of(12, 0), start, otherSection);
        otherSection.addTimeslot(otherTime);

        testSchedule.fillSection(otherSection);
        testSchedule.fillSection(testSection);

        assertEquals(2, testSchedule.numElectives());
    }

    @Test
    public void testNumRequiredNone() {
        assertEquals(0, testSchedule.numRequired());
    }

    @Test
    public void testNumRequiredOne() {
        Course otherCourse = new Course("B", false);
        Section otherSection = new Section("102", otherCourse);
        Timeslot otherTime = new Timeslot(1,DayOfWeek.MONDAY, LocalTime.of(12, 0), start, otherSection);
        otherSection.addTimeslot(otherTime);

        testSchedule.fillSection(testSection);
        testSchedule.fillSection(otherSection);

        assertEquals(1, testSchedule.numRequired());
    }

    @Test
    public void testNumRequiredMany() {
        Course otherCourse = new Course("B", true);
        Section otherSection = new Section("102", otherCourse);
        Timeslot otherTime = new Timeslot(1,DayOfWeek.MONDAY, LocalTime.of(12, 0), start, otherSection);
        otherSection.addTimeslot(otherTime);

        testSchedule.fillSection(testSection);
        testSchedule.fillSection(otherSection);

        assertEquals(2, testSchedule.numRequired());
    }

    @Test
    public void testContainsSection() {
        testSchedule.fillSection(testSection);

        assertTrue(testSchedule.containsSection(testSection));
    }

    @Test
    public void testContainsSectionEmpty() {
        assertFalse(testSchedule.containsSection(testSection));
    }

    @Test
    public void testContainsSectionDifferent() {
        Course otherCourse = new Course("B", true);
        Section otherSection = new Section("102", otherCourse);
        Timeslot otherTime = new Timeslot(1,DayOfWeek.MONDAY, LocalTime.of(12, 0), start, otherSection);
        otherSection.addTimeslot(otherTime);

        testSchedule.fillSection(otherSection);

        assertFalse(testSchedule.containsSection(testSection));
    }

    @Test
    public void testContainsCourseEmpty() {
        assertFalse(testSchedule.containsCourse(testCourse));
    }

    @Test
    public void testContainsCourse() {
        testSchedule.fillSection(testSection);

        assertTrue(testSchedule.containsCourse(testCourse));
    }

    @Test
    public void testContainsCourseDifferent() {
        Course otherCourse = new Course("B", true);
        Section otherSection = new Section("102", otherCourse);
        Timeslot otherTime = new Timeslot(1,DayOfWeek.MONDAY, LocalTime.of(12, 0), start, otherSection);
        otherSection.addTimeslot(otherTime);

        testSchedule.fillSection(otherSection);

        assertFalse(testSchedule.containsCourse(testCourse));
    }

    @Test
    public void testRemoveSection() {
        testSchedule.fillSection(testSection);
        assertTrue(testSchedule.containsSection(testSection));
        testSchedule.removeSection(testSection);
        assertEquals(0, testSchedule.numSections());
    }

    @Test
    public void testRemoveSectionDifferentObject() {
        Section otherSection = new Section("001", testCourse);
        testSchedule.fillSection(otherSection);
        assertTrue(testSchedule.containsSection(testSection));
        testSchedule.removeSection(testSection);
        assertEquals(0, testSchedule.numSections());
    }

    @Test
    public void testRemoveSectionDifferentCourse() {
        Course otherCourse = new Course("Other", false);
        Section otherSection = new Section("001", otherCourse);
        testSchedule.fillSection(otherSection);
        assertFalse(testSchedule.containsSection(testSection));
        testSchedule.removeSection(testSection);
        assertEquals(1, testSchedule.numSections());
    }

    @Test
    public void testToString() {
        Course otherCourse = new Course("B", true);
        Section otherSection = new Section("102", otherCourse);
        Timeslot otherTime = new Timeslot(1,DayOfWeek.MONDAY, LocalTime.of(12, 0), start, otherSection);
        otherSection.addTimeslot(otherTime);
        testSchedule.fillSection(otherSection);
        testSchedule.fillSection(testSection);

        assertEquals(otherSection.toString() + "\n" + testSection.toString() + "\n", testSchedule.toString());
    }
}
