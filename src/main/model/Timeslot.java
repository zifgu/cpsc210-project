package model;

import java.time.LocalTime;

public class Timeslot {
    public static final int INTERVAL_LENGTH = 30;

    private Section section;
    private int term;
    private int dayOfWeek;
    private LocalTime startTime;
    private int duration;

    // REQUIRES: startTime has the format XX:00 or XX:30 on a 24 hour clock, 1 <= dayOfWeek <= 7, and duration > 0
    // EFFECTS: constructs a new timeslot in the section with the given term, day of week, start time, and duration
    //          in 30-minute intervals
    public Timeslot(int term, int dayOfWeek, LocalTime startTime, int duration, Section section) {
        this.term = term;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.duration = duration;
        this.section = section;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
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

    // TODO: Test this method
    // EFFECTS: returns the end time of this timeslot
    public LocalTime getEndTime() {
        return startTime.plusMinutes(duration * INTERVAL_LENGTH);
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
        boolean result = term == other.getTerm() && dayOfWeek == other.getDayOfWeek() && duration == other.getDuration()
                && startTime == other.getStartTime();
        return result;
    }

    // EFFECTS: returns a string displaying timeslot info in printable form
    public String toString() {
        LocalTime end = getEndTime();

        String day;
        if (dayOfWeek == 1) {
            day = "Monday";
        } else if (dayOfWeek == 2) {
            day = "Tuesday";
        } else if (dayOfWeek == 3) {
            day = "Wednesday";
        } else if (dayOfWeek == 4) {
            day = "Thursday";
        } else if (dayOfWeek == 5) {
            day = "Friday";
        } else if (dayOfWeek == 6) {
            day = "Saturday";
        } else {
            day = "Sunday";
        }
        return day + " " + startTime.toString() + "-" + end.toString();
    }
}
