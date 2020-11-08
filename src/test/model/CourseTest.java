package model;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class CourseTest {
    Course testCourse;

    @BeforeEach
    public void setup() {
        testCourse = new Course("A", false);
    }

    @Test
    public void testConstructor() {
        assertEquals("A", testCourse.getName());
        assertFalse(testCourse.getRequired());
    }

    @Test
    public void testContainsSectionWithNameTrue() {
        Section testSection = new Section("101", testCourse);
        testCourse.addSection(testSection);
        assertTrue(testCourse.containsSectionWithName("101"));
    }

    @Test
    public void testContainsSectionWithNameFalse() {
        Section testSection = new Section("101", testCourse);
        testCourse.addSection(testSection);
        assertFalse(testCourse.containsSectionWithName("001"));
    }

    @Test
    public void testNumSectionsNone() {
        assertEquals(0, testCourse.numSections());
    }

    @Test
    public void testNumSectionsMany() {
        for (int i = 0; i < 4; i++) {
            Section testSection = new Section("10" + i, testCourse);
            testCourse.addSection(testSection);
        }
        assertEquals(4, testCourse.numSections());
    }

    @Test
    public void testAddSectionNew() {
        Section testSection = new Section("101", testCourse);
        assertTrue(testCourse.addSection(testSection));
        assertEquals(1, testCourse.numSections());
    }

    @Test
    public void testAddSectionRepeat() {
        Section testSection = new Section("101", testCourse);
        testCourse.addSection(testSection);
        assertFalse(testCourse.addSection(testSection));
        assertEquals(1, testCourse.numSections());
        assertTrue(testCourse.containsSectionWithName("101"));
    }

    @Test
    public void testAddSectionSameName() {
        Section testSection = new Section("101", testCourse);
        Section otherSection = new Section("101", testCourse);

        testCourse.addSection(testSection);

        assertFalse(testCourse.addSection(otherSection));
        assertEquals(1, testCourse.numSections());
        assertTrue(testCourse.containsSectionWithName("101"));
    }

    @Test
    public void testDeleteSectionEmpty() {
        assertFalse(testCourse.deleteSection(new Section("101", testCourse)));
    }

    @Test
    public void testDeleteSectionDifferent() {
        Section testSection = new Section("101", testCourse);

        testCourse.addSection(testSection);
        assertFalse(testCourse.deleteSection(new Section("001", testCourse)));
    }

    @Test
    public void testDeleteSectionSuccess() {
        Section testSection = new Section("101", testCourse);

        testCourse.addSection(testSection);
        assertTrue(testCourse.deleteSection(testSection));
    }

    @Test
    public void testGetSectionByNameNoSections() {
        assertNull(testCourse.getSectionByName("101"));
    }

    @Test
    public void testGetSectionByName() {
        Section testSection = new Section("101", testCourse);
        Section testSection2 = new Section("102", testCourse);
        testCourse.addSection(testSection);
        testCourse.addSection(testSection2);

        assertEquals(testSection, testCourse.getSectionByName("101"));
        assertEquals(testSection2, testCourse.getSectionByName("102"));
    }

    @Test
    public void testGetSectionByNameDifferentName() {
        Section testSection = new Section("101", testCourse);
        testCourse.addSection(testSection);

        assertNull(testCourse.getSectionByName("102"));
    }

    @Test
    public void testToString() {
        Course courseB = new Course("B", true);

        assertEquals("A (elective)", testCourse.toString());
        assertEquals("B (required)", courseB.toString());
    }

    @Test
    public void testToJsonEmptySections() {
        JSONObject json = testCourse.toJson();
        assertEquals("A", json.get("name"));
        assertEquals(false, json.get("required"));

        JSONArray sections = json.getJSONArray("sections");
        assertEquals(0, sections.length());
    }

    @Test
    public void testToJsonEmptyTimes() {
        Section testSection = new Section("101", testCourse);
        testCourse.addSection(testSection);

        JSONObject json = testCourse.toJson();
        assertEquals("A", json.get("name"));
        assertEquals(false, json.get("required"));

        JSONArray sections = json.getJSONArray("sections");
        assertEquals(1, sections.length());

        JSONObject section = sections.getJSONObject(0);
        assertEquals("101", section.get("name"));
        JSONArray times = section.getJSONArray("times");
        assertEquals(0, times.length());
    }

    @Test
    public void testToJson() {
        Section testSection = new Section("101", testCourse);
        Timeslot testTime = new Timeslot(1, DayOfWeek.FRIDAY, LocalTime.parse("12:00"), LocalTime.parse("13:00"), testSection);
        testSection.addTimeslot(testTime);
        testCourse.addSection(testSection);

        JSONObject json = testCourse.toJson();
        assertEquals("A", json.get("name"));
        assertEquals(false, json.get("required"));

        JSONArray sections = json.getJSONArray("sections");
        assertEquals(1, sections.length());

        JSONObject section = sections.getJSONObject(0);
        assertEquals("101", section.get("name"));
        JSONArray times = section.getJSONArray("times");

        assertEquals(1, times.length());
        JSONObject time = times.getJSONObject(0);
        assertEquals(1, time.get("term"));
        assertEquals(DayOfWeek.FRIDAY, time.get("day"));
        assertEquals(LocalTime.parse("12:00"), time.get("start"));
        assertEquals(LocalTime.parse("13:00"), time.get("end"));
    }
}