package model;

import org.json.JSONObject;
import persistence.Writable;

import java.time.DayOfWeek;
import java.time.LocalTime;

/*
    Represents one block of a section with a specific term, day of week, start time, and end time
*/
public class Timeslot implements Writable {
    private Section section;
    private int term;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    // REQUIRES: times have the format XX:00 or XX:30 (24 hour clock), and startTime is before endTime
    // EFFECTS: constructs a new timeslot in the section with the given term, day of week, start time, and end time
    //          in 30-minute intervals
    public Timeslot(int term, DayOfWeek day, LocalTime startTime, LocalTime endTime, Section section) {
        this.term = term;
        this.dayOfWeek = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.section = section;
    }

    // getters
    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public int getTerm() {
        return term;
    }

    public Section getSection() {
        return section;
    }

    // EFFECTS: returns the course this timeslot is associated with
    public Course getCourse() {
        return section.getCourse();
    }

    // EFFECTS: returns true if this timeslot overlaps with timeslot other
    public boolean overlaps(Timeslot other) {
        if (term == other.getTerm() && dayOfWeek == other.getDayOfWeek()) {
            LocalTime endTime = getEndTime();
            LocalTime otherStart = other.getStartTime();
            LocalTime otherEnd = other.getEndTime();
            if (startTime.isAfter(otherStart)) {
                return startTime.isBefore(otherEnd);
            } else if (startTime.equals(otherStart)) {
                return true;
            } else {
                return endTime.isAfter(otherStart);
            }
        }
        return false;
    }

    // EFFECTS: returns true if this timeslot has the same term, day of week, start time, and end time as timeslot other
    public boolean timeEquals(Timeslot other) {
        return term == other.getTerm() && dayOfWeek.equals(other.getDayOfWeek())
                && startTime.equals(other.getStartTime()) && endTime.equals(other.getEndTime());
    }

    // EFFECTS: returns the difference between start and end times as a number of 30-minute intervals
    public int getDuration() {
        int duration = 0;
        LocalTime current = endTime;
        while (!current.equals(startTime)) {
            current = current.minusMinutes(30);
            duration++;
        }
        return duration;
    }

    // EFFECTS: returns a string displaying timeslot info in printable form
    public String toString() {
        return "Term " + term + " " + dayOfWeek + " " + startTime + "-" + endTime;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("term", term);
        json.put("day", dayOfWeek);
        json.put("start", startTime);
        json.put("end", endTime);
        return json;
    }
}
