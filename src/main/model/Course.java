package model;

import java.time.LocalTime;
import java.util.List;

public class Course {

    // EFFECTS: constructs new Course with given name and required/not required status, and no sections
    public Course(String name, boolean required) {

    }

    public String getName() {
        return "";
    }

    public void setName(String name) {

    }

    public boolean getRequired() {
        return false;
    }

    public void setRequired(boolean required) {

    }

    public List<Section> getSections() {
        return null;
    }

    // EFFECTS: returns true if the course contains a section with the given name
    public boolean containsSectionWithName(String name) {
        return false;
    }

    // EFFECTS: returns the number of sections for this course
    public int numSections() {
        return 0;
    }

    // MODIFIES: this
    // EFFECTS: adds existing section to this course
    //          returns true if successfully added, false if a section with same name already exists
    public boolean addSection(Section section) {
        return false;
    }

    // MODIFIES: this
    // EFFECTS: removes the section with the given name from this course
    //          returns true if successfully removed, false if no section with name sectionName exists
    public boolean deleteSection(String sectionName) {
        return false;
    }

    // EFFECTS: returns a string displaying course info in printable form
    public String toString() {
        return "";
    }

}
