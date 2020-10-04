package model;

import java.time.LocalTime;

public class Timeslot {
    // REQUIRES: startTime has the format XX:00 or XX:30 on a 24 hour clock, 1 <= dayOfWeek <= 7, and duration > 0
    // EFFECTS: constructs a new timeslot in the section with the given term, day of week, start time, and duration
    //          in 30-minute intervals
    public Timeslot(int term, int dayOfWeek, LocalTime startTime, int duration, Section section) {

    }

    public LocalTime getStartTime() {
        return null;
    }

    public LocalTime getEndTime() {
        return null;
    }

    public void setStartTime() {

    }

    public void setEndTime() {

    }

    public int getDayOfWeek() {
        return 1;
    }

    public void setDayOfWeek() {

    }

    public int getTerm() {
        return 1;
    }

    public void setTerm() {

    }

    public Section getSection() {
        return null;
    }

    // EFFECTS: returns the course this timeslot is associated with
    public Course getCourse() {
        return null;
    }

    // EFFECTS: returns true if this timeslot overlaps with timeslot other
    public boolean overlaps(Timeslot other) {
        return false;
    }

    // EFFECTS: returns true if this timeslot has the same term, day of week, start time, and end time as timeslot other
    public boolean equals(Timeslot other) {
        return false;
    }

    // EFFECTS: returns a string displaying timeslot info in printable form
    public String toString() {
        return "";
    }
}
