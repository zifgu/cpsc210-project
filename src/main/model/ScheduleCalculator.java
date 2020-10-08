package model;

import java.util.ArrayList;
import java.util.List;

public class ScheduleCalculator {
    private List<Course> required;
    private List<Course> electives;
    private int numCourses;

    public ScheduleCalculator(int n) {
        numCourses = n;
    }

    // EFFECT: returns list of all required courses
    public List<Course> getRequired() {
        return null;
    }

    public void setRequired(List<Course> courses) {
        required = courses;
    }

    // EFFECT: returns list of all elective courses
    public List<Course> getElectives() {
        return null;
    }

    public void setElectives(List<Course> courses) {
        electives = courses;
    }

    // EFFECT: returns list of all valid schedules
    public List<Schedule> allValidSchedules() {
        return null;
    }

    public boolean allValidSchedulesTool() {
        Schedule s = new Schedule();
        fillCourses(s);
        // TODO: figure this out
        return false;
    }

    public boolean fillCourses(Schedule currentSchedule) {
        if (required.size() + electives.size() < numCourses) {
            return false;
        }
        // TODO: finish
        return false;
    }

    public boolean fillRequired(Schedule currentSchedule, int indexLeft) {
        return false;
    }

    public boolean fillElectives() {
        return false;
    }

    /*

    FIELDS: list of required courses, list of elective courses, number of courses, all the schedules formed so far

    private boolean fillRequired(int startIndex, Schedule currentSchedule) {
        if (all required courses filled) {
            add this schedule to successful schedules
            currentSchedule = new Schedule();
            return true??
        } else {
            for (the remaining required courses) {
                for (a section in this course) {
                    attempt to fill currentSchedule with the section
                    if (successful) {
                        recur to fill the rest of the courses
                        break???
                    } else {
                        don't recur, move on
                    }
                }
                return false if none of the sections work
            }
        }
    }

    private boolean fillElectives(int startIndex, Schedule currentSchedule) {
        if (schedule contains required # of courses) {
            make a copy of this schedule and save it???
            currentSchedule = new Schedule();
            return true????

        } else {
            for (the remaining elective courses) {
                for (a section in this course) {
                    attempt to fill the section
                    if (successful) {
                        recur to fill the rest of the ELECTIVE courses
                        break???
                    } else {
                        fail this branch
                    }
                }
                return false if the number of electives remaining is less than the number of electives we still need to
                fill
            }
        }
    }

    private boolean fillCourses() {
        first check if there are enough courses to fill the course quota
        if there are:
        make new schedule
        if (fill required courses is false) {
            return false;
        } else {
            try to fill elective courses
            if (fill electives is false) {
                return false;
            } else {
                return true;
                return all results
            }
        }
    }
     */
}
