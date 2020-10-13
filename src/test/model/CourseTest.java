package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CourseTest {
    Course testCourse;

    @BeforeEach
    public void setup() {
        testCourse = new Course("A", false);
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
        assertFalse(testCourse.deleteSection("101"));
    }

    @Test
    public void testDeleteSectionDifferent() {
        Section testSection = new Section("101", testCourse);

        testCourse.addSection(testSection);
        assertFalse(testCourse.deleteSection("001"));
    }

    @Test
    public void testDeleteSectionSuccess() {
        Section testSection = new Section("101", testCourse);

        testCourse.addSection(testSection);
        assertTrue(testCourse.deleteSection("101"));
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
}