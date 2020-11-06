package ui;

import model.Course;
import model.CourseList;
import model.Section;
import model.Timeslot;

import javax.swing.*;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalTime;

public class CourseEditor {
    @SuppressWarnings("checkstyle:MethodLength")
    public static void main(String[] args) {
        Course course1 = new Course("cpsc210", true);
        Course course2 = new Course("cpsc121", true);
        Section course1s1 = new Section("101", course1);
        Section course1s2 = new Section("201", course1);
        course1s1.addTimeslot(new Timeslot(1, DayOfWeek.MONDAY, LocalTime.of(12,0), LocalTime.of(13,0), course1s1));
        course1s2.addTimeslot(new Timeslot(2, DayOfWeek.WEDNESDAY, LocalTime.of(13,0), LocalTime.of(14,0), course1s2));
        course1s2.addTimeslot(new Timeslot(2, DayOfWeek.FRIDAY, LocalTime.of(13,0), LocalTime.of(14,0), course1s2));
        course1.addSection(course1s1);
        course1.addSection(course1s2);

        Section course2s1 = new Section("001", course2);
        course2s1.addTimeslot(new Timeslot(1, DayOfWeek.THURSDAY, LocalTime.of(12, 30), LocalTime.of(14,0), course2s1));
        course2.addSection(course2s1);

        CourseList courseList = new CourseList();
        courseList.addCourse(course1);
        courseList.addCourse(course2);
        CourseListPanel courses = new CourseListPanel(courseList);
        JPanel mainPanel = courses.getMainPanel();

        JFrame frame = new JFrame("My frame");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(900, 750));
        frame.setResizable(false);
        frame.setContentPane(mainPanel);
        frame.pack();
        frame.setVisible(true);
    }
}
