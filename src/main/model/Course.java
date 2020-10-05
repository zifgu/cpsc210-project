package model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Course {
    private String name;
    private boolean required;
    private ArrayList<Section> sections;

    // EFFECTS: constructs new Course with given name and required/not required status, and no sections
    public Course(String name, boolean required) {
        this.name = name;
        this.required = required;
        sections = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public List<Section> getSections() {
        return sections;
    }

    // EFFECTS: returns true if the course contains a section with the given name
    public boolean containsSectionWithName(String name) {
        for (Section s: sections) {
            if (s.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    // EFFECTS: returns the number of sections for this course
    public int numSections() {
        return sections.size();
    }

    // MODIFIES: this
    // EFFECTS: adds existing section to this course
    //          returns true if successfully added, false if a section with same name already exists
    public boolean addSection(Section section) {
        if (containsSectionWithName(section.getName())) {
            return false;
        } else {
            sections.add(section);
            return true;
        }
    }

    // MODIFIES: this
    // EFFECTS: removes the section with the given name from this course
    //          returns true if successfully removed, false if no section with name sectionName exists
    public boolean deleteSection(String sectionName) {
        int index = getSectionIndexByName(sectionName);
        if (index >= 0) {
            sections.remove(index);
            return true;
        } else {
            return false;
        }
    }

    // EFFECTS: returns a string displaying course info in printable form
    public String toString() {
        if (required) {
            return name + " (required)";
        } else {
            return name + " (elective)";
        }
    }

    // EFFECTS: returns index of the section with the given name if it exists, otherwise returns -1
    private int getSectionIndexByName(String name) {
        for (int i = 0; i < sections.size(); i++) {
            if (sections.get(i).getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }
}
