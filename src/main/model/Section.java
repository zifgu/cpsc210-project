package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

import java.time.DayOfWeek;
import java.util.*;

/*
    Represents a section of a course with associated times
*/
public class Section implements Writable {
    private String name;
    private Course course;
    private Set<Timeslot> times;

    // REQUIRES: the new section has a different name from any other section in the same course
    // EFFECTS: constructs a new section of given course with given name and no timeslots
    public Section(String name, Course course) {
        this.name = name;
        this.course = course;
        this.times = new HashSet<>();
    }

    // getters
    public String getName() {
        return name;
    }

    public Course getCourse() {
        return course;
    }

    public Set<Timeslot> getTimeslots() {
        return times;
    }

    // MODIFIES: this
    // EFFECTS: if there is not already a timeslot with conflicting time, adds given timeslot to this section
    //          and returns true; otherwise returns false
    public boolean addTimeslot(Timeslot timeslot) {
        for (Timeslot t : times) {
            if (t.overlaps(timeslot)) {
                return false;
            }
        }
        times.add(timeslot);
        return true;
    }

    // MODIFIES: this
    // EFFECTS: if the given timeslot or a timeslot with identical time is in this section, removes it and returns true
    //          otherwise returns false
    public boolean deleteTimeslot(Timeslot timeslot) {
        return times.remove(timeslot);
    }

    // EFFECTS: returns the number of timeslots in this section
    public int numTimeslots() {
        return times.size();
    }

    // EFFECTS: returns true if this section contains the given timeslot or a timeslot with identical time
    public boolean containsTimeslot(Timeslot timeslot) {
        return times.contains(timeslot);
    }

    // EFFECTS: returns true if the timeslots of this section conflict with the timeslots of other
    public boolean overlaps(Section other) {
        for (Timeslot t1 : times) {
            for (Timeslot t2 : other.getTimeslots()) {
                if (t1.overlaps(t2)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    // EFFECTS: returns a string displaying section info in a printable form
    public String toString() {
        String result = course.getName() + " " + name + ": ";
        HashMap<String, List<DayOfWeek>> sectionGroups = new HashMap<>();
        for (Timeslot t : times) {
            String termAndTime = "Term " + t.getTerm() + " " + t.getStartTime() + "-" + t.getEndTime();
            if (sectionGroups.containsKey(termAndTime)) {
                sectionGroups.get(termAndTime).add(t.getDayOfWeek());
            } else {
                List<DayOfWeek> days = new ArrayList<>();
                days.add(t.getDayOfWeek());
                sectionGroups.put(termAndTime, days);
            }
        }
        for (String s : sectionGroups.keySet()) {
            result += s + " ";
            List<DayOfWeek> days = sectionGroups.get(s);
            Collections.sort(days);
            for (DayOfWeek day : days) {
                result += getAbbreviation(day);
            }
            result += "\t";
        }
        return result;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        JSONArray timesList = new JSONArray();

        for (Timeslot t : times) {
            timesList.put(t.toJson());
        }

        json.put("name", name);
        json.put("times", timesList);
        return json;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Section section = (Section) o;
        return Objects.equals(name, section.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    private String getAbbreviation(DayOfWeek day) {
        switch (day) {
            case MONDAY:
                return "M";
            case TUESDAY:
                return "T";
            case WEDNESDAY:
                return "W";
            case THURSDAY:
                return "Th";
            case FRIDAY:
                return "F";
            case SATURDAY:
                return "S";
            default:
                return "Su";
        }
    }
}
