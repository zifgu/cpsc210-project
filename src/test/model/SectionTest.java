package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class SectionTest {
    private Section testSection;
    private Course testCourse;
    private Timeslot testTime;

    @BeforeEach
    public void setup() {
        testCourse = new Course("A", false);
        testSection = new Section("001", testCourse);
        testTime = new Timeslot(1, 1, LocalTime.of(12, 0), 2, testSection);
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
            Timeslot timeslot = new Timeslot(2, i, LocalTime.of(12, 0), 2, testSection);
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
        Timeslot otherTime = new Timeslot(2, 1, LocalTime.of(12,0), 3, testSection);
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
        Timeslot sameTime = new Timeslot(1, 1, LocalTime.of(12, 0), 2, testSection);
        assertFalse(testSection.addTimeslot(sameTime));

        assertTrue(testSection.containsTimeslot(testTime));
        assertEquals(1, testSection.numTimeslots());
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
        Timeslot sameTime = new Timeslot(1, 1, LocalTime.of(12, 0), 2, testSection);
        assertTrue(testSection.deleteTimeslot(sameTime));
        assertEquals(0, testSection.numTimeslots());
    }

    @Test
    public void testDeleteTimeslotDifferent() {
        testSection.addTimeslot(testTime);
        Timeslot otherTime = new Timeslot(1, 1, LocalTime.of(13, 0), 2, testSection);
        assertFalse(testSection.deleteTimeslot(otherTime));
        assertEquals(0, testSection.numTimeslots());
    }

}
