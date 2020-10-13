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
    private void sortRequiredAndElectives(List<Course> courses) {
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
            fillRequired(s, 0);
            return schedules;
        }
    }

    // REQUIRES: number of required courses < desired size of schedule
    // MODIFIES: this
    // EFFECTS: fills schedule with a section from each required course
    // I based this approach on a backtracking solution to the n queens problem created by 29AjayKumar
    // Link https://www.geeksforgeeks.org/printing-solutions-n-queen-problem/
    private boolean fillRequired(Schedule currentSchedule, int courseIndex) {
        boolean possible = false;
        if (currentSchedule.numCourses() == numRequired) {
            possible = fillElectives(currentSchedule, courseIndex);
        } else {
            Course c = required.get(courseIndex);
            for (Section s : c.getSections()) {
                if (!currentSchedule.fillSection(s)) {
                    continue;
                }
                possible = fillRequired(currentSchedule, courseIndex + 1) || possible;
                currentSchedule.removeSection(s); //backtrack
            }
        }
        return possible;
    }

    // REQUIRES: total number of courses > desired size of schedule
    // MODIFIES: this
    // EFFECTS: fills schedule with enough electives to have numCourse courses, and adds successes to list of schedules
    // I based this approach on a backtracking solution to the n queens problem created by 29AjayKumar
    // Link https://www.geeksforgeeks.org/printing-solutions-n-queen-problem/
    private boolean fillElectives(Schedule currentSchedule, int courseIndex) {
        boolean possible = false;
        if (currentSchedule.numCourses() == numCourses) {
            addToListOfSchedules(currentSchedule);
            possible = true;
        } else {
            Course c = electives.get(courseIndex - required.size());
            // try adding a section from this course to the schedule
            for (Section s : c.getSections()) {
                if (!currentSchedule.fillSection(s)) {
                    continue;
                }
                possible = fillElectives(currentSchedule, courseIndex + 1) || possible;
                currentSchedule.removeSection(s);
            }
            // if there are enough electives remaining, try to make a schedule not containing the current course
            if (enoughElectives(currentSchedule, courseIndex)) {
                possible = fillElectives(currentSchedule, courseIndex + 1) || possible;
            }
        }
        return possible;
    }

    /*
    // ORIGINAL METHOD - method length was more than 31 lines
    // REQUIRES: there are enough courses to fill the schedule
    // MODIFIES: this
    // EFFECTS: recursively fills schedule with a section from each required course and sections from enough elective
    //          courses to have numCourses in total; adds successful schedules to list of schedules
    private boolean fillCourses(Schedule currentSchedule, int courseIndex) {
        boolean possible = false;
        if (currentSchedule.numCourses() == numCourses) {
            addToListOfSchedules(currentSchedule);
            possible = true;
        } else if (courseIndex < required.size()) {
            Course c = required.get(courseIndex);
            for (Section s : c.getSections()) {
                if (!currentSchedule.fillSection(s)) {
                    continue;
                }
                possible = fillCourses(currentSchedule, courseIndex + 1) || possible;
                currentSchedule.removeSection(s);
            }
        } else if (courseIndex < required.size() + electives.size()) {
            Course c = electives.get(courseIndex - required.size());
            for (Section s : c.getSections()) {
                if (!currentSchedule.fillSection(s)) {
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
}
