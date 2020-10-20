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

public class JsonWriterTest {
    // TODO: give credit

    @Test
    public void testInvalidFile() {
        try {
            JsonWriter writer = new JsonWriter("./data/my\0illegal:fileName.json");
            writer.open();
            fail("IOException expected but not thrown");
        } catch (IOException e) {
            // expected
        }
    }

    @Test
    public void testWriteEmptyCourseList() {
        CourseList list = new CourseList();
        try {
            JsonWriter writer = new JsonWriter(".data/TestWriterEmptyCourseList.json");
            writer.writeCourseList(list);

            JsonReader reader = new JsonReader(".data/TestWriterEmptyCourseList.json");
            list = reader.readCourseList();
            assertEquals(0, list.numCourses());
        } catch (IOException e) {
            fail("Not expecting IOException");
        }
    }

    @Test
    public void testWriteGeneralCourseList() {
        CourseList list = new CourseList();
        Course course1 = new Course("cpsc210", true);
        Section course1s1 = new Section("101", course1);
        Timeslot course1s1t1 = new Timeslot(1, DayOfWeek.FRIDAY, LocalTime.parse("11:00"), LocalTime.parse("12:00"), course1s1);

        course1s1.addTimeslot(course1s1t1);
        course1.addSection(course1s1);
        list.addCourse(course1);

        try {
            JsonWriter writer = new JsonWriter(".data/TestWriterGeneralCourseList.json");
            writer.writeCourseList(list);

            JsonReader reader = new JsonReader(".data/TestWriterGeneralCourseList.json");
            list = reader.readCourseList();
            assertEquals(1, list.numCourses());

            Course readCourse = list.getCourses().get(0);
            assertEquals(course1.getName(), readCourse.getName());
            assertEquals(course1.getRequired(), readCourse.getRequired());
            assertEquals(course1.numSections(), readCourse.numSections());

            Section readSection = readCourse.getSections().get(0);
            assertEquals(course1s1.getName(), readSection.getName());
            assertEquals(course1s1.numTimeslots(), readSection.numTimeslots());

            Timeslot readTime = readSection.getTimeslots().get(0);
            assertTrue(readTime.timeEquals(course1s1t1));
        } catch (IOException e) {
            fail("Not expecting IOException");
        }
    }
}
