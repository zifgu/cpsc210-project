package model;

import java.util.ArrayList;
import java.util.List;

/*
    A utility class for calculating all possible schedules from a course list
*/
class ScheduleCalculator {
    private final List<Course> required;
    private final List<Course> electives;
    private List<Schedule> schedules;
    private final int numCourses;
    private int numRequired;
    private int numElectives;

    ScheduleCalculator(int n, List<Course> courses) {
        numCourses = n;
        required = new ArrayList<>();
        electives = new ArrayList<>();
        schedules = new ArrayList<>();
        sortRequiredAndElectives(courses);
    }

    // EFFECT: returns list of all required courses
    void sortRequiredAndElectives(List<Course> courses) {
        numRequired = 0;
        numElectives = 0;
        for (Course c : courses) {
            if (c.getRequired()) {
                required.add(c);
                numRequired++;
            } else {
                electives.add(c);
                numElectives++;
            }
        }
    }

    // EFFECT: returns list of all valid schedules with numCourses courses
    List<Schedule> allValidSchedules() {
        if (required.size() + electives.size() < numCourses) {
            return new ArrayList<>();
        } else {
            Schedule s = new Schedule();
            fillCourses(s, 0);
            return schedules;
        }
    }

    @SuppressWarnings("checkstyle:MethodLength")
    // REQUIRES: there are enough courses to fill the schedule
    // MODIFIES: this
    // EFFECTS: recursively fills schedule with a section from each required course and sections from enough elective
    //          courses to have numCourses in total; adds successful schedules to list of schedules
    // Based off the solution from https://www.geeksforgeeks.org/printing-solutions-n-queen-problem/
    private boolean fillCourses(Schedule currentSchedule, int courseIndex) {
        boolean possible = false;
        if (currentSchedule.numCourses() == numCourses) {
            addToListOfSchedules(currentSchedule);
            possible = true;
        } else if (courseIndex < required.size()) {
            Course c = required.get(courseIndex);
            for (Section s : c.getSections()) {
                if (!currentSchedule.fillSection(s)) {
                    possible = false;
                    continue;
                }
                possible = fillCourses(currentSchedule, courseIndex + 1) || possible;
                currentSchedule.removeSection(s);
            }
        } else if (courseIndex < required.size() + electives.size()) {
            Course c = electives.get(courseIndex - required.size());
            for (Section s : c.getSections()) {
                if (!currentSchedule.fillSection(s)) {
                    possible = false;
                    continue;
                }
                possible = fillCourses(currentSchedule, courseIndex + 1) || possible;
                currentSchedule.removeSection(s);
            }
            if (enoughElectives(currentSchedule, courseIndex)) {
                possible = fillCourses(currentSchedule, courseIndex + 1) || possible;
            }
        }
        return possible;
    }

    // EFFECTS: returns true if number of remaining electives is enough to produce a schedule with numCourses courses
    private boolean enoughElectives(Schedule currentSchedule, int index) {
        return currentSchedule.numCourses() + (numElectives + numRequired - 1 - index) >= numCourses;
    }

    // REQUIRES: current schedule is a valid schedule
    // EFFECTS: copies the current schedule into a new schedule and adds it to the list of all possible schedules
    private void addToListOfSchedules(Schedule filledSchedule) {
        Schedule newSchedule = new Schedule();
        for (Section sec : filledSchedule.getSections()) {
            newSchedule.fillSection(sec);
        }
        schedules.add(newSchedule);
    }
}
