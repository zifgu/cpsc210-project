package model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Schedule {
    private Timeslot[][] slots;

    // EFFECTS: constructs an empty schedule
    public Schedule() {
        slots = new Timeslot[7 * CourseList.TERMS][CourseList.NUM_DAILY_HOURS * CourseList.INTERVALS_PER_HOUR];
    }

    // MODIFIES: this
    // EFFECTS: returns false if one of the timeslots in the section is already occupied, else fills this
    //          schedule with all timeslots in the section and returns true
    public boolean fillSection(Section section) {
        for (Timeslot t : section.getTimeslots()) {
            if (isFilled(t.getTerm(), t.getDayOfWeek(), t.getStartTime())) {
                return false;
            }
        }
        for (Timeslot t : section.getTimeslots()) {
            int weekIndex = weekIndex(t);
            int timeIndex = timeIndex(t);
            for (int i = timeIndex; i < timeIndex + t.getDuration(); i++) {
                slots[weekIndex][i] = t;
            }
        }
        return true;
    }

    // EFFECTS: returns a string displaying this schedule in a printable form
    public String toString() {
        return "";
    }

    // EFFECTS: returns true if the 30-minute interval beginning at start is filled with a section of a course
    public boolean isFilled(int term, int dayOfWeek, LocalTime start) {
        int weekIndex = weekIndex(term, dayOfWeek);
        int timeIndex = timeIndex(start);
        if (slots[weekIndex][timeIndex] != null) {
            return true;
        }
        return false;
    }

    // EFFECTS: returns the number of 30-minute intervals in this schedule that are filled
    public int numFilledIntervals() {
        int count = 0;
        for (Timeslot[] day: slots) {
            for (Timeslot t : day) {
                if (t != null) {
                    count++;
                }
            }
        }
        return count;
    }

    // EFFECTS: returns the number of courses in this schedule
    public int numCourses() {
        HashSet<Course> courses = new HashSet<>();
        for (Timeslot[] day: slots) {
            for (Timeslot t : day) {
                if (t != null) {
                    courses.add(t.getCourse());
                }
            }
        }
        return courses.size();
    }

    // EFFECTS: returns the number of non-required courses in this schedule
    public int numElectives() {
        HashSet<Course> courses = new HashSet<>();
        int count = 0;
        for (Timeslot[] day: slots) {
            for (Timeslot t : day) {
                if (t != null) {
                    Course c = t.getCourse();
                    boolean contained = courses.add(c);
                    if (!contained && !c.getRequired()) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    // EFFECTS: returns true if this schedule contains a section from the given course
    public boolean containsCourse(Course course) {
        for (Section s : course.getSections()) {
            if (containsSection(s)) {
                return true;
            }
        }
        return false;
    }

    // EFFECTS: returns true if this schedule contains all timeslots from the given section
    public boolean containsSection(Section section) {
        for (Timeslot t : section.getTimeslots()) {
            if (!containsTimeslot(t)) {
                return false;
            }
        }
        return true;
    }

    private boolean containsTimeslot(Timeslot t) {
        int weekIndex = weekIndex(t);
        int timeIndex = timeIndex(t);
        for (int i = timeIndex; i < timeIndex + t.getDuration(); i++) {
            Timeslot other = slots[weekIndex][i];
            if (other == null || !other.equals(t)) {
                return false;
            }
        }
        return true;
    }

    private int weekIndex(Timeslot t) {
        return weekIndex(t.getTerm(), t.getDayOfWeek());
    }

    // EFFECTS: returns the week where the given time would fall in this schedule
    private int weekIndex(int term, int dayOfWeek) {
        return (term - 1) * 7 + dayOfWeek;
    }

    private int timeIndex(Timeslot t) {
        return timeIndex(t.getStartTime());
    }

    // EFFECTS: returns the position in a week of this schedule where the given time would fall
    private int timeIndex(LocalTime start) {
        return start.minusHours(CourseList.EARLIEST_HOURS).toSecondOfDay() / (30 * 60);
    }
}
