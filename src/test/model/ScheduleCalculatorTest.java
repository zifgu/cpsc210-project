package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScheduleCalculatorTest {
    ScheduleCalculator sc;
    Schedule testSchedule;
    Course courseA;
    Course courseB;
    ArrayList<Course> reqs;

    @BeforeEach
    public void setup() {
        testSchedule = new Schedule();
        reqs = new ArrayList<>();
    }

    @Test
    public void testFillRequiredEmpty() {
        sc = new ScheduleCalculator(0);
        sc.setRequired(reqs);
        assertTrue(sc.fillRequired(testSchedule, 0));
    }

    @Test
    public void testFillRequiredOne() {
        sc = new ScheduleCalculator(1);
        courseA = makeCourseWithSection("A", true, 1, DayOfWeek.MONDAY, LocalTime.of(13,0), 2);
        reqs.add(courseA);
        sc.setRequired(reqs);
        assertTrue(sc.fillRequired(testSchedule, 0));
    }

    @Test
    public void testFillRequiredOverlap() {
        sc = new ScheduleCalculator(2);
        courseA = makeCourseWithSection("A", true, 1, DayOfWeek.MONDAY, LocalTime.of(13, 0), 2);
        courseB = makeCourseWithSection("B", true, 1, DayOfWeek.MONDAY, LocalTime.of(13, 0), 2);
        reqs.add(courseA);
        reqs.add(courseB);
        sc.setRequired(reqs);

        assertFalse(sc.fillRequired(testSchedule, 0));
    }

    @Test
    public void testFillRequiredTwo() {
        sc = new ScheduleCalculator(2);
        courseA = makeCourseWithSection("A", true, 1, DayOfWeek.MONDAY, LocalTime.of(13, 0), 2);
        addSection(courseA, "A2", 1, DayOfWeek.MONDAY, LocalTime.of(14, 0), 2);
        courseB = makeCourseWithSection("B", true, 1, DayOfWeek.MONDAY, LocalTime.of(13, 0), 2);
        addSection(courseB, "B2", 1, DayOfWeek.MONDAY, LocalTime.of(14, 0), 2);
        reqs.add(courseA);
        reqs.add(courseB);
        sc.setRequired(reqs);

        assertTrue(sc.fillRequired(testSchedule, 0));
    }

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
