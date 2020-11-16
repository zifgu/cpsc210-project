package ui;

import model.CourseList;

import javax.swing.*;
import java.awt.*;

public class CourseEditor {
    @SuppressWarnings("checkstyle:MethodLength")
    public static void main(String[] args) {
        CourseList courseList = new CourseList();
        CourseEditorPanel courses = new CourseEditorPanel(courseList);
        JTabbedPane mainPanel = courses.getMainPanel();

        JFrame frame = new JFrame("Schedule calculator app");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(900, 750));
        frame.setResizable(false);
        frame.setContentPane(mainPanel);
        frame.pack();
        frame.setVisible(true);
    }
}
