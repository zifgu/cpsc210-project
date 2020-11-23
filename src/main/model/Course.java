package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/*
    Represents a course with name, status (required vs elective), and sections
*/
public class Course implements Writable {
    private String name;
    private boolean required;
    private Set<Section> sections;

    // EFFECTS: constructs new Course with given name and required/not required status, and no sections
    //          if course name is empty, names the course "New Course"
    public Course(String name, boolean required) {
        if (name.equals("")) {
            this.name = "New Course";
        } else {
            this.name = name;
        }
        this.required = required;
        sections = new HashSet<>();
    }

    // getters and setters
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

    public Set<Section> getSections() {
        return sections;
    }

    // EFFECTS: returns true if the course contains a section with the given name
    public boolean containsSectionWithName(String name) {
        return sections.contains(new Section(name, this));
    }

    // MODIFIES: this
    // EFFECTS: changes the name of s to name if s is in this course and returns true, otherwise returns false
    public boolean changeSectionName(Section s, String name) {
        if (!containsSectionWithName(name)) {
            sections.remove(s);
            s.setName(name);
            sections.add(s);
            return true;
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
        return sections.add(section);
    }

    // MODIFIES: this
    // EFFECTS: returns false if this course does not contain s, otherwise removes s and returns true
    public boolean deleteSection(Section s) {
        return sections.remove(s);
    }

    @Override
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

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        JSONArray sectionsList = new JSONArray();

        for (Section s : sections) {
            sectionsList.put(s.toJson());
        }

        json.put("name", name);
        json.put("required", required);
        json.put("sections", sectionsList);
        return json;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Course course = (Course) o;
        return Objects.equals(name, course.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
