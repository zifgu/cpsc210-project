package model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ListSchedule {
    private ArrayList<Timeslot> slots;

    // EFFECTS: constructs an empty schedule
    public ListSchedule() {
        slots = new ArrayList<>();
        for (int i = 0; i < CourseList.TERMS * CourseList.NUM_DAILY_HOURS * CourseList.INTERVALS_PER_HOUR * 7; i++) {
            slots.add(null);
        }
    }

    // MODIFIES: this
    // EFFECTS: returns false if one of the timeslots in the section is already occupied, else fills this
    //          schedule with all timeslots in the section and returns true
    public boolean fillSection(Section section) {
        int index;
        List<Timeslot> timeslots = section.getTimeslots();
        for (Timeslot t : timeslots) {
            index = calculateStartIndex(t);
            for (int i = 0; i < t.getDuration(); i++) {
                if (isFilled(t.getTerm(), t.getDayOfWeek(), t.getStartTime().plusMinutes(i * 30))) {
                    return false;
                }
            }
        }
        for (Timeslot t: timeslots) {
            index = calculateStartIndex(t);
            for (int i = index; i < index + t.getDuration(); i++) {
                slots.set(i, t);
            }
        }
        return true;
    }

    // EFFECTS: returns a string displaying this schedule in a printable form
    public String toString() {
        return "";
    }

    // EFFECTS: returns the number of 30-minute intervals in this schedule that are filled
    public int numFilledIntervals() {
        int count = 0;
        for (Timeslot t : slots) {
            if (t != null) {
                count++;
            }
        }
        return count;
    }

    // EFFECTS: returns the number of courses in this schedule
    public int numCourses() {
        ArrayList<Course> courses = new ArrayList<>();
        for (Timeslot t : slots) {
            if (t != null) {
                Course c = t.getCourse();
                if (!courses.contains(c)) {
                    courses.add(c);
                }
            }
        }
        return courses.size();
    }

    // EFFECTS: returns the number of non-required courses in this schedule
    public int numElectives() {
        int count = 0;
        ArrayList<Course> courses = new ArrayList<>();
        for (Timeslot t : slots) {
            if (t != null) {
                Course c = t.getCourse();
                if (!courses.contains(c)) {
                    courses.add(c);
                    if (!c.getRequired()) {
                        count++;
                    }
                }
            }
        }
        return count;
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

    // EFFECTS: returns true if this schedule contains a section from the given course
    public boolean containsCourse(Course course) {
        for (Section s : course.getSections()) {
            if (containsSection(s)) {
                return true;
            }
        }
        return false;
    }

    // EFFECTS: returns true if the 30-minute interval beginning at start is filled with a section of a course
    public boolean isFilled(int term, int dayOfWeek, LocalTime start) {
        int startIndex = calculateStartIndex(term, dayOfWeek, start);
        if (slots.get(startIndex) != null) {
            return true;
        } else {
            return false;
        }
    }

    private int calculateStartIndex(Timeslot t) {
        return calculateStartIndex(t.getTerm(), t.getDayOfWeek(), t.getStartTime());
    }

    private int calculateStartIndex(int term, int dayOfWeek, LocalTime start) {
        int termOffset = (term - 1) * 7 * CourseList.NUM_DAILY_HOURS * CourseList.INTERVALS_PER_HOUR;
        int dayOffset = (dayOfWeek - 1) * CourseList.NUM_DAILY_HOURS * CourseList.INTERVALS_PER_HOUR;
        int timeOffset = start.minusHours(CourseList.EARLIEST_HOURS).toSecondOfDay() / (30 * 60);
        return termOffset + dayOffset + timeOffset;
    }

    private boolean containsTimeslot(Timeslot t) {
        int startIndex = calculateStartIndex(t);
        for (int i = startIndex; i < startIndex + t.getDuration(); i++) {
            Timeslot timeslotAtIndex = slots.get(i);
            if (timeslotAtIndex == null || !timeslotAtIndex.equals(t)) {
                return false;
            }
        }
        return true;
    }

}
