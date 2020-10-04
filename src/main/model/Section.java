package model;

import java.time.LocalTime;
import java.util.List;

public class Section {

    // EFFECTS: constructs a new section with no timeslots
    public Section(String name, Course course) {

    }

    public String getName() {
        return "";
    }

    public void setName(String name) {

    }

    public Course getCourse() {
        return null;
    }

    public void setCourse() {

    }

    public List<Timeslot> getTimeslots() {
        return null;
    }

    // MODIFIES: this
    // EFFECTS: if there is not already a timeslot with identical time, adds given timeslot to this section
    //          and returns true; otherwise returns false
    public boolean addTimeslot(Timeslot timeslot) {
        return false;
    }

    // MODIFIES: this
    // EFFECTS: if the given timeslot or a timeslot with identical time is in this section, removes it and returns true
    //          otherwise returns false
    public boolean deleteTimeslot(Timeslot timeslot) {
        return false;
    }

    // EFFECTS: returns a string displaying section info in a printable form
    public String toString() {
        return "";
    }

    // EFFECTS: returns the number of timeslots in this section
    public int numTimeslots() {
        return 0;
    }

    // EFFECTS: returns true if this section contains the given timeslot or a timeslot with identical time
    public boolean containsTimeslot(Timeslot timeslot) {
        return false;
    }

}
