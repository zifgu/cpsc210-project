package ui;

import model.CourseList;

import javax.swing.*;
import java.awt.*;

public class CourseEditor {
    @SuppressWarnings("checkstyle:MethodLength")
    public static void main(String[] args) {
        CourseList courseList = new CourseList();
        CourseListPanel courses = new CourseListPanel(courseList);
        JTabbedPane mainPanel = courses.getMainPanel();

        JFrame frame = new JFrame("My frame");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(900, 750));
        frame.setResizable(false);
        frame.setContentPane(mainPanel);
        frame.pack();
        frame.setVisible(true);
    }
}
