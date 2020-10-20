package persistence;

import model.Course;
import model.CourseList;
import model.Section;
import model.Timeslot;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class JsonReaderTest {
    // TODO: give credit

    @Test
    public void testOpenInvalidFile() {
        try {
            JsonReader reader = new JsonReader("./data/my\0illegal:fileName.json");
            CourseList list = reader.readCourseList();
            fail("IOException was expected but not thrown");
        } catch (IOException e) {
            // expected
        }
    }

    @Test
    public void testReadEmptyCourseList() {
        try {
            JsonReader reader = new JsonReader("./data/TestReaderEmptyCourseList.json");
            CourseList list = reader.readCourseList();
            assertEquals(0, list.numCourses());
        } catch (IOException e) {
            fail("Not expecting IOException");
        }
    }

    @Test
    public void testReadCourseListEmptySection() {
        try {
            JsonReader reader = new JsonReader("./data/TestReaderEmptySections.json");
            CourseList list = reader.readCourseList();
            assertEquals(1, list.numCourses());

            Course c = list.getCourses().get(0);
            assertEquals("cpsc210", c.getName());
            assertTrue(c.getRequired());
            assertEquals(0, c.numSections());
        } catch (IOException e) {
            fail("Not expecting IOException");
        }
    }

    @Test
    public void testReadCourseListEmptyTimes() {
        try {
            JsonReader reader = new JsonReader("./data/TestReaderEmptyTimes.json");
            CourseList list = reader.readCourseList();
            assertEquals(1, list.numCourses());

            Course c = list.getCourses().get(0);
            Section s = c.getSections().get(0);
            assertEquals("101", s.getName());
            assertEquals(0, s.numTimeslots());
            assertEquals(c, s.getCourse());
        } catch (IOException e) {
            fail("Not expecting IOException");
        }
    }

    @Test
    public void testReadCourseListGeneral() {
        try {
            JsonReader reader = new JsonReader("./data/TestReaderGeneralCourseList.json");
            CourseList list = reader.readCourseList();

            assertEquals(2, list.numCourses());

            Course course1 = list.getCourses().get(0);
            assertEquals("cpsc121", course1.getName());
            assertTrue(course1.getRequired());
            assertEquals(3, course1.numSections());

            Section course1s1 = course1.getSections().get(0);
            assertEquals("101", course1s1.getName());
            assertEquals(course1, course1s1.getCourse());
            assertEquals(1, course1s1.numTimeslots());
            checkFirstTimeslot(course1s1, 1, DayOfWeek.TUESDAY, LocalTime.parse("12:30"), LocalTime.parse("14:00"));

            Section course1s2 = course1.getSections().get(1);
            assertEquals("104", course1s2.getName());
            assertEquals(course1, course1s2.getCourse());
            assertEquals(0, course1s2.numTimeslots());

            Course course2 = list.getCourses().get(1);
            assertEquals("ling100", course2.getName());
            assertFalse(course2.getRequired());
            assertEquals(1, course2.numSections());

            Section course2s1 = course1.getSections().get(0);
            assertEquals("001", course2s1.getName());
            assertEquals(0, course2s1.numTimeslots());
            checkFirstTimeslot(course2s1, 2, DayOfWeek.FRIDAY, LocalTime.parse("10:00"), LocalTime.parse("11:00"));

        } catch (IOException e) {
            fail("Not expecting IOException");
        }
    }

    private void checkFirstTimeslot(Section section, int term, DayOfWeek day, LocalTime start, LocalTime end) {
        Timeslot time = section.getTimeslots().get(0);
        Timeslot comparisonTime = new Timeslot(term, day, start, end, section);
        assertTrue(time.timeEquals(comparisonTime));
    }
}
