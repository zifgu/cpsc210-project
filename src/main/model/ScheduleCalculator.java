package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/*
    A utility class for calculating all possible schedules from a course list
*/
class ScheduleCalculator {
    private List<Course> required;
    private List<Course> electives;
    private List<Schedule> schedules;
    private int numCourses;
    private int numRequired;
    private int numElectives;

    // EFFECTS: constructs ScheduleCalculator with courses sorted into required/electives, no schedules
    //          and given value of numCourses
    ScheduleCalculator(int n, Set<Course> courses) {
        numCourses = n;
        required = new ArrayList<>();
        electives = new ArrayList<>();
        schedules = new ArrayList<>();
        sortRequiredAndElectives(courses);
    }

    // MODIFIES: this
    // EFFECTS: sorts courses into a list of required courses and a list of elective courses and updates size of each
    private void sortRequiredAndElectives(Set<Course> courses) {
        for (Course c : courses) {
            if (c.getRequired()) {
                required.add(c);
            } else {
                electives.add(c);
            }
        }
        numRequired = required.size();
        numElectives = electives.size();
    }

    // MODIFIES: this
    // EFFECT: returns list of all valid schedules with numCourses courses
    List<Schedule> allValidSchedules() {
        Schedule s = new Schedule();
        fillRequired(s, 0);
        return schedules;
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
            for (Section s : c) {
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
            for (Section s : c) {
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

    // REQUIRES: index > 0
    // EFFECTS: returns true if number of remaining electives is enough to produce a schedule with numCourses courses
    private boolean enoughElectives(Schedule currentSchedule, int index) {
        return currentSchedule.numCourses() + (numElectives + numRequired - 1 - index) >= numCourses;
    }

    // REQUIRES: current schedule is a valid schedule
    // MODIFIES: this
    // EFFECTS: copies the current schedule into a new schedule and adds it to the list of all possible schedules
    private void addToListOfSchedules(Schedule filledSchedule) {
        Schedule newSchedule = new Schedule();
        for (Section sec : filledSchedule) {
            newSchedule.fillSection(sec);
        }
        schedules.add(newSchedule);
    }
}
