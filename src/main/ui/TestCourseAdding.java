package ui;

import model.*;

public class TestCourseAdding {
    public static void main(String[] args) {
        CourseList courseList = new CourseList();
        boolean result = courseList.addCourseFromSSC("CPSC", "210", true);
        System.out.println(result);

        if (result) {
            for (Course c : courseList.getCourses()) {
                System.out.println(c);
                for (Section s : c.getSections()) {
                    System.out.println(s);
                    for (Timeslot t : s.getTimeslots()) {
                        System.out.println(t);
                    }
                }
            }
        }
    }
}
