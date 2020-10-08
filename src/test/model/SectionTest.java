package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class SectionTest {
    private Section testSection;
    private Course testCourse;
    private Timeslot testTime;
    private LocalTime twelve;
    private LocalTime one;

    @BeforeEach
    public void setup() {
        twelve = LocalTime.of(12, 0);
        one = LocalTime.of(13, 0);

        testCourse = new Course("A", false);
        testSection = new Section("001", testCourse);
        testTime = new Timeslot(1, DayOfWeek.MONDAY, twelve, one, testSection);
    }

    @Test
    public void testNumTimeslotsEmpty() {
        assertEquals(0, testSection.numTimeslots());
    }

    @Test
    public void testNumTimeslotsOne() {
        testSection.addTimeslot(testTime);
        assertEquals(1, testSection.numTimeslots());
    }

    @Test
    public void testNumTimeslotsMany() {
        for (int i = 1; i < 5; i++) {
            Timeslot timeslot = new Timeslot(2, DayOfWeek.of(i), twelve, one, testSection);
            testSection.addTimeslot(timeslot);
        }
        assertEquals(4, testSection.numTimeslots());
    }

    @Test
    public void testContainsTimeslotEmpty() {
        assertFalse(testSection.containsTimeslot(testTime));
    }

    @Test
    public void testContainsTimeslotDifferent() {
        Timeslot otherTime = new Timeslot(2, DayOfWeek.MONDAY, twelve, one.plusMinutes(30), testSection);
        testSection.addTimeslot(testTime);

        assertFalse(testSection.containsTimeslot(otherTime));
    }

    @Test
    public void testAddTimeslotNew() {
        assertTrue(testSection.addTimeslot(testTime));

        assertTrue(testSection.containsTimeslot(testTime));
        assertEquals(1, testSection.numTimeslots());
    }

    @Test
    public void testAddTimeslotRepeat() {
        assertTrue(testSection.addTimeslot(testTime));
        assertFalse(testSection.addTimeslot(testTime));

        assertTrue(testSection.containsTimeslot(testTime));
        assertEquals(1, testSection.numTimeslots());
    }

    @Test
    public void testAddTimeslotIdentical() {
        assertTrue(testSection.addTimeslot(testTime));
        Timeslot sameTime = new Timeslot(1, DayOfWeek.MONDAY, twelve, one, testSection);
        assertFalse(testSection.addTimeslot(sameTime));

        assertTrue(testSection.containsTimeslot(testTime));
        assertEquals(1, testSection.numTimeslots());
    }

    @Test
    public void testAddTimeslotOverlap() {
        assertTrue(testSection.addTimeslot(testTime));
        Timeslot overlappingTime = new Timeslot(1, DayOfWeek.MONDAY, twelve, one.plusMinutes(30), testSection);
        assertFalse(testSection.addTimeslot(overlappingTime));

        assertTrue(testSection.containsTimeslot(testTime));
        assertEquals(1, testSection.numTimeslots());
    }

    @Test
    public void testAddTimeslotNoOverlap() {
        assertTrue(testSection.addTimeslot(testTime));
        Timeslot otherTime = new Timeslot(1, DayOfWeek.MONDAY, twelve.minusHours(1), twelve, testSection);
        assertTrue(testSection.addTimeslot(otherTime));

        assertTrue(testSection.containsTimeslot(testTime));
        assertTrue(testSection.containsTimeslot(otherTime));
        assertEquals(2, testSection.numTimeslots());
    }

    @Test
    public void testDeleteTimeslotEmpty() {
        assertFalse(testSection.deleteTimeslot(testTime));
        assertEquals(0, testSection.numTimeslots());
    }

    @Test
    public void testDeleteTimeslotExisting() {
        testSection.addTimeslot(testTime);
        assertTrue(testSection.deleteTimeslot(testTime));
        assertEquals(0, testSection.numTimeslots());
    }

    @Test
    public void testDeleteTimeslotIdentical() {
        testSection.addTimeslot(testTime);
        Timeslot sameTime = new Timeslot(1, DayOfWeek.MONDAY, twelve, one, testSection);
        assertTrue(testSection.deleteTimeslot(sameTime));
        assertEquals(0, testSection.numTimeslots());
    }

    @Test
    public void testDeleteTimeslotDifferent() {
        testSection.addTimeslot(testTime);
        Timeslot otherTime = new Timeslot(1, DayOfWeek.MONDAY, one, LocalTime.of(14, 0), testSection);
        assertFalse(testSection.deleteTimeslot(otherTime));
        assertEquals(1, testSection.numTimeslots());
    }
}