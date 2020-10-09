package model;

import java.util.ArrayList;
import java.util.List;

public class ScheduleCalculator {
    private List<Course> required;
    private List<Course> electives;
    private List<Schedule> schedules;
    private int numCourses;
    private int numRequired;
    private int numElectives;

    public ScheduleCalculator(int n, List<Course> courses) {
        numCourses = n;
        required = new ArrayList<>();
        electives = new ArrayList<>();
        schedules = new ArrayList<>();
        sortRequiredAndElectives(courses);
    }

    // EFFECT: returns list of all required courses
    public void sortRequiredAndElectives(List<Course> courses) {
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

    // EFFECT: returns list of all valid schedules
    public List<Schedule> allValidSchedules() {
        if (required.size() + electives.size() < numCourses) {
            return schedules;
        } else {
            Schedule s = new Schedule();
            fillCourses(s, 0);
            return schedules;
        }
    }

    @SuppressWarnings("checkstyle:MethodLength")
    // MODIFIES: this
    // EFFECTS:
    public boolean fillCourses(Schedule currentSchedule, int courseIndex) {
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

    /*
    private boolean fillElectives(Schedule currentSchedule, int electivesRemaining, int electiveIndex) {
        if (electivesRemaining <= 0) {
            return true;
        }
        boolean possible = false;
        Course c = electives.get(electiveIndex);
        for (Section s : c.getSections()) {
            if (!currentSchedule.fillSection(s)) {
                possible = false;
                continue;
            }
            possible = fillElectives(currentSchedule, electivesRemaining - 1, electiveIndex + 1);
            currentSchedule.removeSection(s);
        }
        return possible;
    }
     */

    /*
    private boolean fillElectives(Schedule currentSchedule, int electiveIndex) {
        boolean possible = false;
        if (currentSchedule.numElectives() == numCourses - numRequired) {
            addToListOfSchedules(currentSchedule);
            possible = true;
            // do something else
        } else if (!enoughElectives(currentSchedule, electiveIndex)) {
            possible = false;
        } else {
            Course c = electives.get(electiveIndex);
            for (Section s : c.getSections()) {
                if (!currentSchedule.fillSection(s)) {
                    possible = false;
                    continue;
                }
                possible = fillElectives(currentSchedule, electiveIndex + 1) || possible;
                currentSchedule.removeSection(s);
            }
        }
        return possible;
    }
     */

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
