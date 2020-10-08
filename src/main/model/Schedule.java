package model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Schedule {
    private List<Section> sections;

    // EFFECTS: constructs an empty schedule
    public Schedule() {
        sections = new ArrayList<>();
    }

    // MODIFIES: this
    // EFFECTS: returns false if one of the timeslots in the section is already occupied, else fills this
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

    // EFFECTS: returns a string displaying this schedule in a printable form
    public String toString() {
        return "";
    }

    // EFFECTS: returns the total time of sections in this schedule, as a number of 30-minute intervals
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

    // EFFECTS: returns true if this schedule contains a section with the given timeslot
    private boolean timeslotFilled(Timeslot t) {
        return false; // stub
    }
}
