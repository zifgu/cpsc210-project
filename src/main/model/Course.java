package model;

import java.util.ArrayList;
import java.util.List;

/*
    Represents a course with name, status (required vs elective), and sections
*/
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

    // getters and setters
    public String getName() {
        return name;
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
    // EFFECTS: returns false if a section with the same name already exists, otherwise adds section to this course
    //          and returns true
    public boolean addSection(Section section) {
        if (containsSectionWithName(section.getName())) {
            return false;
        } else {
            sections.add(section);
            return true;
        }
    }

    // MODIFIES: this
    // EFFECTS: returns false if no section with given name exists, otherwise removes the section and returns true
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

    // EFFECTS: returns the section with the given name in this course, if it exists; otherwise returns null
    public Section getSectionByName(String name) {
        for (Section s : sections) {
            if (s.getName().equals(name)) {
                return s;
            }
        }
        return null;
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
