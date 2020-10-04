package model;

import java.time.LocalTime;

public class Schedule {
    // EFFECTS: constructs an empty schedule
    public Schedule() {

    }

    // MODIFIES: this
    // EFFECTS: returns false if one of the timeslots in the section is already occupied, else fills this
    //          schedule with all timeslots in the section and returns true
    public boolean fillSection(Section section) {
        return false;
    }

    // EFFECTS: returns a string displaying this schedule in a printable form
    public String toString() {
        return "";
    }

    // EFFECTS: returns the number of 30-minute intervals in this schedule that are filled
    public int numFilledIntervals() {
        return 0;
    }

    // EFFECTS: returns the number of courses in this schedule
    public int numCourses() {
        return 0;
    }

    // EFFECTS: returns the number of non-required courses in this schedule
    public int numElectives() {
        return 0;
    }

    // EFFECTS: returns true if this schedule contains all timeslots from the given section
    public boolean containsSection(Section section) {
        return false;
    }

    // EFFECTS: returns true if this schedule contains a section from the given course
    public boolean containsCourse(Course course) {
        return false;
    }

    // EFFECTS: returns true if the 30-minute interval beginning at start is filled with a course
    public boolean isFilled(int term, int dayOfWeek, LocalTime start) {
        return false;
    }

}
