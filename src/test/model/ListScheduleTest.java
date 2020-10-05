package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class ListScheduleTest {
    private ListSchedule testSchedule;
    private Course testCourse;
    private Section testSection;
    private Timeslot testTime1;
    private Timeslot testTime2;
    private Timeslot testTime3;
    private LocalTime start;

    @BeforeEach
    public void setup() {
        start = LocalTime.of(13, 0);

        testCourse = new Course("A", true);
        testSection = new Section("001", testCourse);
        testCourse.addSection(testSection);

        testTime1 = new Timeslot(1, 1, start, 2, testSection);
        testTime2 = new Timeslot(1, 3, start, 2, testSection);
        testTime3 = new Timeslot(1, 5, start, 2, testSection);

        testSection.addTimeslot(testTime1);
        testSection.addTimeslot(testTime2);
        testSection.addTimeslot(testTime3);

        testSchedule = new ListSchedule();
    }

    @Test
    public void testFillSectionEmpty() {
        assertTrue(testSchedule.fillSection(testSection));
        assertEquals(6, testSchedule.numFilledIntervals());
        assertTrue(testSchedule.isFilled(1,1, start));
        assertTrue(testSchedule.isFilled(1,1, start.plusMinutes(30)));
        assertTrue(testSchedule.isFilled(1,3, start));
        assertTrue(testSchedule.isFilled(1,3, start.plusMinutes(30)));
        assertTrue(testSchedule.isFilled(1,5, start));
        assertTrue(testSchedule.isFilled(1,5, start.plusMinutes(30)));
    }

    @Test
    public void testFillSectionOneOverlap() {
        Course otherCourse = new Course("B", false);
        Section otherSection = new Section("102", otherCourse);
        Timeslot otherTime = new Timeslot(1,3, LocalTime.of(13, 0), 3, otherSection);
        otherSection.addTimeslot(otherTime);

        assertTrue(testSchedule.fillSection(otherSection));
        assertFalse(testSchedule.fillSection(testSection));
        assertEquals(3, testSchedule.numFilledIntervals());
    }

    @Test
    public void testFillSectionManyOverlaps() {
        Course otherCourse = new Course("B", false);
        Section otherSection = new Section("102", otherCourse);
        Timeslot otherTime = new Timeslot(1,3, LocalTime.of(13, 0), 2, otherSection);
        Timeslot anotherTime = new Timeslot(1,5, LocalTime.of(12, 0), 3, otherSection);
        otherSection.addTimeslot(otherTime);
        otherSection.addTimeslot(anotherTime);

        assertTrue(testSchedule.fillSection(otherSection));
        assertFalse(testSchedule.fillSection(testSection));
        assertEquals(5, testSchedule.numFilledIntervals());
    }

    @Test
    public void testFillSectionLeftBound() {
        Course otherCourse = new Course("B", false);
        Section otherSection = new Section("102", otherCourse);
        Timeslot otherTime = new Timeslot(1,3, LocalTime.of(11, 30), 3, otherSection);
        otherSection.addTimeslot(otherTime);

        testSchedule.fillSection(testSection);
        assertTrue(testSchedule.fillSection(otherSection));
        assertEquals(9, testSchedule.numFilledIntervals());
        for (int i = 0; i < 3; i++) {
            assertTrue(testSchedule.isFilled(1, 3, LocalTime.of(11,30).plusMinutes(30*i)));
        }
    }

    @Test
    public void testFillSectionRightBound() {
        Course otherCourse = new Course("B", false);
        Section otherSection = new Section("102", otherCourse);
        Timeslot otherTime = new Timeslot(1,3, LocalTime.of(14, 0), 2, otherSection);
        otherSection.addTimeslot(otherTime);

        testSchedule.fillSection(testSection);
        assertTrue(testSchedule.fillSection(otherSection));
        assertEquals(8, testSchedule.numFilledIntervals());
        assertTrue(testSchedule.isFilled(1, 3, LocalTime.of(14, 0)));
        assertTrue(testSchedule.isFilled(1, 3, LocalTime.of(14, 30)));
    }

    @Test
    public void testNumFilledIntervalsEmpty() {
        assertEquals(0, testSchedule.numFilledIntervals());
    }

    @Test
    public void testNumFilledIntervals() {
        testSchedule.fillSection(testSection);
        assertEquals(6, testSchedule.numFilledIntervals());
    }

    @Test
    public void testNumCoursesEmpty() {
        assertEquals(0, testSchedule.numCourses());
    }

    @Test
    public void testNumCoursesOne() {
        testSchedule.fillSection(testSection);
        assertEquals(1, testSchedule.numCourses());
    }

    @Test
    public void testNumCoursesTwo() {
        Course otherCourse = new Course("B", false);
        Section otherSection = new Section("102", otherCourse);
        Timeslot otherTime = new Timeslot(1,1, LocalTime.of(12, 0), 2, otherSection);
        otherSection.addTimeslot(otherTime);

        testSchedule.fillSection(otherSection);
        testSchedule.fillSection(testSection);

        assertEquals(2, testSchedule.numCourses());
    }

    @Test
    public void testNumElectivesNone() {
        assertEquals(0, testSchedule.numElectives());
    }

    @Test
    public void testNumElectivesOne() {
        Course otherCourse = new Course("B", false);
        Section otherSection = new Section("102", otherCourse);
        Timeslot otherTime = new Timeslot(1,1, LocalTime.of(12, 0), 2, otherSection);
        otherSection.addTimeslot(otherTime);

        testSchedule.fillSection(otherSection);
        testSchedule.fillSection(testSection);

        assertEquals(1, testSchedule.numElectives());
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
        Timeslot otherTime = new Timeslot(1,1, LocalTime.of(12, 0), 2, otherSection);
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
        Timeslot otherTime = new Timeslot(1,1, LocalTime.of(12, 0), 2, otherSection);
        otherSection.addTimeslot(otherTime);

        testSchedule.fillSection(otherSection);

        assertFalse(testSchedule.containsCourse(testCourse));
    }

    @Test
    public void testIsFilledIntervalEmpty() {
        testSchedule.fillSection(testSection);
        assertFalse(testSchedule.isFilled(1, 3, LocalTime.of(12,0)));
    }

    @Test
    public void testIsFilledIntervalLeftBound() {
        testSchedule.fillSection(testSection);
        assertFalse(testSchedule.isFilled(1, 3, LocalTime.of(12,30)));
    }

    @Test
    public void testIsFilledIntervalRightBound() {
        testSchedule.fillSection(testSection);
        assertFalse(testSchedule.isFilled(1, 3, LocalTime.of(14,0)));
    }

    @Test
    public void testIsFilledStart() {
        testSchedule.fillSection(testSection);
        assertTrue(testSchedule.isFilled(1, 3, start));
    }

    @Test
    public void testIsFilledEnd() {
        testSchedule.fillSection(testSection);
        assertTrue(testSchedule.isFilled(1, 3, start.plusMinutes(30)));
    }

}
