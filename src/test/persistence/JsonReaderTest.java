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
    // based on the JsonReader test class from JsonSerializationDemo
    // https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo

    @Test
    public void testOpenInvalidFile() {
        try {
            JsonReader reader = new JsonReader("./data/someFile.json");
            CourseList list = reader.read();
            fail("IOException was expected but not thrown");
        } catch (IOException e) {
            // expected
        }
    }

    @Test
    public void testReadEmptyCourseList() {
        try {
            JsonReader reader = new JsonReader("./data/TestReaderEmptyCourseList.json");
            CourseList list = reader.read();
            assertEquals(0, list.numCourses());
        } catch (IOException e) {
            fail("Not expecting IOException");
        }
    }

    @Test
    public void testReadCourseListEmptySection() {
        try {
            JsonReader reader = new JsonReader("./data/TestReaderEmptySections.json");
            CourseList list = reader.read();
            assertEquals(1, list.numCourses());

            Course c = list.getCourseByName("cpsc210");
            assertNotNull(c);
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
            CourseList list = reader.read();
            assertEquals(1, list.numCourses());

            Course c = list.getCourseByName("cpsc121");
            Section s = c.getSectionByName("101");
            assertNotNull(s);
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
            CourseList list = reader.read();

            assertEquals(2, list.numCourses());

            Course course1 = list.getCourseByName("cpsc121");
            assertNotNull(course1);
            assertTrue(course1.getRequired());
            assertEquals(2, course1.numSections());

            Section course1s1 = course1.getSectionByName("101");
            assertNotNull(course1s1);
            assertEquals(course1, course1s1.getCourse());
            assertEquals(1, course1s1.numTimeslots());
            checkFirstTimeslot(course1s1, 1, DayOfWeek.TUESDAY, LocalTime.parse("12:30"), LocalTime.parse("14:00"));

            Section course1s2 = course1.getSectionByName("104");
            assertNotNull(course1s2);
            assertEquals(course1, course1s2.getCourse());
            assertEquals(0, course1s2.numTimeslots());

            Course course2 = list.getCourseByName("ling100");
            assertNotNull(course2);
            assertFalse(course2.getRequired());
            assertEquals(1, course2.numSections());

            Section course2s1 = course2.getSectionByName("001");
            assertNotNull(course2s1);
            assertEquals(1, course2s1.numTimeslots());
            checkFirstTimeslot(course2s1, 2, DayOfWeek.FRIDAY, LocalTime.parse("10:00"), LocalTime.parse("11:00"));

        } catch (IOException e) {
            fail("Not expecting IOException");
        }
    }

    private void checkFirstTimeslot(Section section, int term, DayOfWeek day, LocalTime start, LocalTime end) {
        assertTrue(section.getTimeslots().contains(new Timeslot(term, day, start, end, section)));
    }
}
