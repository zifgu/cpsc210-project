package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/*
    Represents a course schedule, i.e. a collection of sections from courses
*/
public class Schedule {
    private List<Section> sections;

    // EFFECTS: constructs a schedule with no sections
    public Schedule() {
        sections = new ArrayList<>();
    }

    // getter
    public List<Section> getSections() {
        return sections;
    }

    // REQUIRES: the schedule does not already contain a section from the same course
    // MODIFIES: this
    // EFFECTS: returns false if >1 of the timeslots in the section conflicts with existing schedule, else fills this
    //          schedule with all timeslots in the section and returns true
    public boolean fillSection(Section section) {
        for (Section s : sections) {
            if (s.overlaps(section)) {
                return false;
            }
        }
        sections.add(section);
        return true;
    }

    // REQUIRES: section is in this schedule already
    // MODIFIES: this
    // EFFECTS: removes section from this schedule
    public void removeSection(Section section) {
        sections.remove(section);
    }

    // REQUIRES: schedule is valid, i.e. no time conflicts
    // EFFECTS: returns a string displaying this schedule in a printable form
    public String toString() {
        String result = "";
        for (Section s : sections) {
            result = result.concat(s + "\n");
        }
        return result;
    }

    // EFFECTS: returns the total duration of all sections in this schedule, as a number of 30-minute intervals
    public int totalTime() {
        int time = 0;
        for (Section sec : sections) {
            for (Timeslot t : sec.getTimeslots()) {
                time += t.getDuration();
            }
        }
        return time;
    }

    // EFFECTS: returns the number of courses in this schedule
    public int numCourses() {
        HashSet<Course> courses = new HashSet<>();
        for (Section s : sections) {
            courses.add(s.getCourse());
        }
        return courses.size();
    }

    // EFFECTS: returns the number of sections in this schedule
    public int numSections() {
        return sections.size();
    }

    // EFFECTS: returns the number of non-required courses in this schedule
    public int numElectives() {
        HashSet<Course> electives = new HashSet<>();
        for (Section s : sections) {
            Course c = s.getCourse();
            if (!c.getRequired()) {
                electives.add(c);
            }
        }
        return electives.size();
    }

    // EFFECTS: returns the number of required courses in this schedule
    public int numRequired() {
        HashSet<Course> required = new HashSet<>();
        for (Section s : sections) {
            Course c = s.getCourse();
            if (c.getRequired()) {
                required.add(c);
            }
        }
        return required.size();
    }

    // EFFECTS: returns true if this schedule contains a section from the given course
    public boolean containsCourse(Course course) {
        for (Section s : sections) {
            if (s.getCourse().equals(course)) {
                return true;
            }
        }
        return false;
    }

    // EFFECTS: returns true if this schedule contains the given section
    public boolean containsSection(Section section) {
        for (Section s : sections) {
            if (s.equals(section)) {
                return true;
            }
        }
        return false;
    }
}
