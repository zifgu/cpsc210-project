package model;

import org.json.JSONObject;
import persistence.Writable;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;

/*
    Represents one block of a section with a specific term, day of week, start time, and end time
*/
public class Timeslot implements Writable {
    private Section section;
    private int term;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    // REQUIRES: startTime is before endTime
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

    @Override
    public String toString() {
        return "Term " + term + " " + dayOfWeek + " " + startTime + "-" + endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Timeslot timeslot = (Timeslot) o;
        return term == timeslot.term
                && dayOfWeek == timeslot.dayOfWeek
                && Objects.equals(startTime, timeslot.startTime)
                && Objects.equals(endTime, timeslot.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(term, dayOfWeek, startTime, endTime);
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
