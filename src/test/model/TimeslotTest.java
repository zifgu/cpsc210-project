package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
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
        end = LocalTime.of(14, 0);
        testTime = new Timeslot(1, DayOfWeek.MONDAY, start, end, testSection);
        testCourse.addSection(testSection);
        testSection.addTimeslot(testTime);
    }

    @Test
    public void testConstructor() {
        Timeslot t = new Timeslot(1, DayOfWeek.TUESDAY, LocalTime.of(12, 30), LocalTime.of(14, 0), testSection);
        assertEquals(1, t.getTerm());
        assertEquals(DayOfWeek.TUESDAY, t.getDayOfWeek());
        assertEquals(LocalTime.of(12, 30), t.getStartTime());
        assertEquals(LocalTime.of(14, 0), t.getEndTime());
        assertEquals(testSection, t.getSection());
    }

    @Test
    public void testGetCourse() {
        Course result = testTime.getCourse();
        assertEquals(testCourse, result);
    }

    @Test
    public void testOverlapsExact() {
        Timeslot otherTime = new Timeslot(1, DayOfWeek.MONDAY, start, end, testSection);
        boolean overlaps = testTime.overlaps(otherTime);

        assertTrue(overlaps);
    }

    @Test
    public void testOverlapsSameStart() {
        Timeslot otherTime = new Timeslot(1, DayOfWeek.MONDAY, start, end.plusMinutes(30), testSection);
        boolean overlaps = testTime.overlaps(otherTime);

        assertTrue(overlaps);
    }

    @Test
    public void testOverlapsSameEnd() {
        Timeslot otherTime = new Timeslot(1,DayOfWeek.MONDAY, LocalTime.of(12, 0), end, testSection);
        boolean overlaps = otherTime.overlaps(testTime);

        assertTrue(overlaps);
    }

    @Test
    public void testOverlapsContains() {
        Timeslot otherTime = new Timeslot(1,DayOfWeek.MONDAY, LocalTime.of(12, 0), end.plusHours(1), testSection);
        boolean overlaps = testTime.overlaps(otherTime);

        assertTrue(overlaps);
    }

    @Test
    public void testOverlapsOverlap() {
        Timeslot otherTime = new Timeslot(1,DayOfWeek.MONDAY, LocalTime.of(12, 0), end.minusMinutes(30), testSection);
        boolean overlaps = testTime.overlaps(otherTime);

        assertTrue(overlaps);
    }

    @Test
    public void testOverlapsDifferentTerm() {
        Timeslot otherTime = new Timeslot(2,DayOfWeek.MONDAY, start, end, testSection);
        boolean overlaps = testTime.overlaps(otherTime);

        assertFalse(overlaps);
    }

    @Test
    public void testOverlapsDifferentDay() {
        Timeslot otherTime = new Timeslot(1,DayOfWeek.THURSDAY, start, end, testSection);
        boolean overlaps = testTime.overlaps(otherTime);

        assertFalse(overlaps);
    }

    @Test
    public void testOverlapsBoundaryOverlap() {
        Timeslot otherTime = new Timeslot(1,DayOfWeek.MONDAY, LocalTime.of(12, 0), start, testSection);
        boolean overlaps = testTime.overlaps(otherTime);

        assertFalse(overlaps);
    }

    @Test
    public void testOverlapsNoOverlap() {
        Timeslot otherTime = new Timeslot(1,DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10,0), testSection);
        boolean overlaps = testTime.overlaps(otherTime);

        assertFalse(overlaps);
    }

    @Test
    public void testTimeEqualsSelf() {
        assertTrue(testTime.timeEquals(testTime));
    }

    @Test
    public void testTimeEqualsEqual() {
        Timeslot otherTime = new Timeslot(1,DayOfWeek.MONDAY, start, end, testSection);
        assertTrue(testTime.timeEquals(otherTime));
    }

    @Test
    public void testTimeEqualsDifferentTerm() {
        Timeslot otherTime = new Timeslot(2,DayOfWeek.MONDAY, start, end, testSection);
        assertFalse(testTime.timeEquals(otherTime));
    }

    @Test
    public void testTimeEqualsDifferentDay() {
        Timeslot otherTime = new Timeslot(1,DayOfWeek.WEDNESDAY, start, end, testSection);
        assertFalse(testTime.timeEquals(otherTime));
    }

    @Test
    public void testTimeEqualsDifferentStart() {
        Timeslot otherTime = new Timeslot(1,DayOfWeek.MONDAY, LocalTime.of(12,30), end.minusMinutes(30), testSection);
        assertFalse(testTime.timeEquals(otherTime));
    }

    @Test
    public void testTimeEqualsDifferentEnd() {
        Timeslot otherTime = new Timeslot(1,DayOfWeek.MONDAY, start, end.plusMinutes(30), testSection);
        assertFalse(testTime.timeEquals(otherTime));
    }

    @Test
    public void testTimeEqualsDifferentSection() {
        Section otherSection = new Section("002", testCourse);
        Timeslot otherTime = new Timeslot(1,DayOfWeek.MONDAY, start, end, otherSection);
        assertTrue(testTime.timeEquals(otherTime));
    }

    @Test
    public void testGetDurationOneHour() {
        assertEquals(2, testTime.getDuration());
    }

    @Test
    public void testGetDurationManyHours() {
        Timeslot otherTime = new Timeslot(1, DayOfWeek.MONDAY, start.minusMinutes(90), end, testSection);
        assertEquals(5, otherTime.getDuration());
    }

    @Test
    public void testToString() {
        assertEquals("Term 1 MONDAY 13:00-14:00", testTime.toString());
    }
}
