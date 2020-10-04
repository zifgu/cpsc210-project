package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class TimeslotTest {
    private Timeslot testTime;
    private Section testSection;
    private Course testCourse;
    private LocalTime start;
    private LocalTime end;

    @BeforeEach
    public void setup() {
        testCourse = new Course("A", false);
        testSection = new Section("001", testCourse);
        start = LocalTime.of(13,0);
        testTime = new Timeslot(1, 1, start, 2, testSection);
        testCourse.addSection(testSection);
        testSection.addTimeslot(testTime);
    }

    @Test
    public void testGetCourse() {
        Course result = testTime.getCourse();
        assertEquals(testCourse, result);
    }

    @Test
    public void testOverlapsExact() {
        Timeslot otherTime = new Timeslot(1, 1, start, 2, testSection);
        boolean overlaps = testTime.overlaps(otherTime);

        assertTrue(overlaps);
    }

    @Test
    public void testOverlapsSameStart() {
        Timeslot otherTime = new Timeslot(1, 1, start, 3, testSection);
        boolean overlaps = testTime.overlaps(otherTime);

        assertTrue(overlaps);
    }

    @Test
    public void testOverlapsSameEnd() {
        Timeslot otherTime = new Timeslot(1,1, LocalTime.of(12, 0), 4, testSection);
        boolean overlaps = otherTime.overlaps(testTime);

        assertTrue(overlaps);
    }

    @Test
    public void testOverlapsContains() {
        Timeslot otherTime = new Timeslot(1,1, LocalTime.of(12, 0), 6, testSection);
        boolean overlaps = testTime.overlaps(otherTime);

        assertTrue(overlaps);
    }

    @Test
    public void testOverlapsOverlap() {
        Timeslot otherTime = new Timeslot(1,1, LocalTime.of(12, 0), 3, testSection);
        boolean overlaps = testTime.overlaps(otherTime);

        assertTrue(overlaps);
    }

    @Test
    public void testOverlapsDifferentTerm() {
        Timeslot otherTime = new Timeslot(2,1, start, 2, testSection);
        boolean overlaps = testTime.overlaps(otherTime);

        assertFalse(overlaps);
    }

    @Test
    public void testOverlapsDifferentDay() {
        Timeslot otherTime = new Timeslot(1,4, start, 2, testSection);
        boolean overlaps = testTime.overlaps(otherTime);

        assertFalse(overlaps);
    }

    @Test
    public void testOverlapsBoundaryOverlap() {
        Timeslot otherTime = new Timeslot(1,1, LocalTime.of(12, 0), 2, testSection);
        boolean overlaps = testTime.overlaps(otherTime);

        assertFalse(overlaps);
    }

    @Test
    public void testOverlapsNoOverlap() {
        Timeslot otherTime = new Timeslot(1,1, LocalTime.of(9, 0), 2, testSection);
        boolean overlaps = testTime.overlaps(otherTime);

        assertFalse(overlaps);
    }

    @Test
    public void testEqualsSelf() {
        assertTrue(testTime.equals(testTime));
    }

    @Test
    public void testEqualsEqual() {
        Timeslot otherTime = new Timeslot(1,1, start, 2, testSection);
        assertTrue(testTime.equals(otherTime));
    }

    @Test
    public void testEqualsDifferentTerm() {
        Timeslot otherTime = new Timeslot(2,1, start, 2, testSection);
        assertFalse(testTime.equals(otherTime));
    }

    @Test
    public void testEqualsDifferentDay() {
        Timeslot otherTime = new Timeslot(1,3, start, 2, testSection);
        assertFalse(testTime.equals(otherTime));
    }

    @Test
    public void testEqualsDifferentStart() {
        Timeslot otherTime = new Timeslot(1,3, LocalTime.of(12,30), 2, testSection);
        assertFalse(testTime.equals(otherTime));
    }

    @Test
    public void testEqualsDifferentEnd() {
        Timeslot otherTime = new Timeslot(1,3, start, 3, testSection);
        assertFalse(testTime.equals(otherTime));
    }

    @Test
    public void testEqualsDifferentSection() {
        Section otherSection = new Section("002", testCourse);
        Timeslot otherTime = new Timeslot(1,3, start, 1, otherSection);
        assertTrue(testTime.equals(otherTime));
    }
}