package model;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class Timeslot {
    public static final int INTERVAL_LENGTH = 30;

    private Section section;
    private int term;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    // REQUIRES: times have the format XX:00 or XX:30 (24 hour clock), and startTime is before endTime
    // EFFECTS: constructs a new timeslot in the section with the given term, day of week, start time, and duration
    //          in 30-minute intervals
    public Timeslot(int term, DayOfWeek day, LocalTime startTime, LocalTime endTime, Section section) {
        this.term = term;
        this.dayOfWeek = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.section = section;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek day) {
        this.dayOfWeek = day;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
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
                if (startTime.isBefore(otherEnd)) {
                    return true;
                }
            } else if (startTime.equals(otherStart)) {
                return true;
            } else if (endTime.isAfter(otherStart)) {
                return true;
            }
        }
        return false;
    }

    // EFFECTS: returns true if this timeslot has the same term, day of week, start time, and duration as timeslot other
    public boolean timeEquals(Timeslot other) {
        boolean result = term == other.getTerm() && dayOfWeek.equals(other.getDayOfWeek())
                && endTime.equals(other.getEndTime()) && startTime.equals(other.getStartTime());
        return result;
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
        LocalTime end = getEndTime();
        return dayOfWeek.toString() + " " + startTime.toString() + "-" + end.toString();
    }
}
