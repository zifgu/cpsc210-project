package model;

import java.util.ArrayList;
import java.util.List;

public class Section {
    private String name;
    private Course course;
    private ArrayList<Timeslot> times;

    // EFFECTS: constructs a new section with no timeslots
    public Section(String name, Course course) {
        this.name = name;
        this.course = course;
        this.times = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Course getCourse() {
        return course;
    }

    public List<Timeslot> getTimeslots() {
        return times;
    }

    // MODIFIES: this
    // EFFECTS: if there is not already a timeslot with identical time, adds given timeslot to this section
    //          and returns true; otherwise returns false
    public boolean addTimeslot(Timeslot timeslot) {
        if (findDuplicate(timeslot) == null) {
            times.add(timeslot);
            return true;
        } else {
            return false;
        }
    }

    // MODIFIES: this
    // EFFECTS: if the given timeslot or a timeslot with identical time is in this section, removes it and returns true
    //          otherwise returns false
    public boolean deleteTimeslot(Timeslot timeslot) {
        Timeslot duplicate = findDuplicate(timeslot);
        if (duplicate == null) {
            return false;
        } else {
            times.remove(duplicate);
            return true;
        }
    }

    // EFFECTS: returns a string displaying section info in a printable form
    public String toString() {
        String result = name + ": ";
        for (Timeslot t : times) {
            result = result.concat(t.toString() + ", ");
        }
        return result;
    }

    // EFFECTS: returns the number of timeslots in this section
    public int numTimeslots() {
        return times.size();
    }

    // EFFECTS: returns true if this section contains the given timeslot or a timeslot with identical time
    public boolean containsTimeslot(Timeslot timeslot) {
        return findDuplicate(timeslot) != null;
    }

    // EFFECTS: returns timeslot sharing the same term, day, start time, and duration as the given timeslot if it exists
    //          otherwise returns null
    private Timeslot findDuplicate(Timeslot timeslot) {
        for (Timeslot t : times) {
            if (t.timeEquals(timeslot)) {
                return t;
            }
        }
        return null;
    }

}
