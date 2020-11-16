package ui;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
//        For console application:
//        new ScheduleApp();
        CourseEditor courses = new CourseEditor();
        JTabbedPane mainPanel = courses.getMainPanel();

        JFrame frame = new JFrame("Schedule Calculator App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(900, 750));
        frame.setResizable(false);
        frame.setContentPane(mainPanel);
        frame.pack();
        frame.setVisible(true);
    }
}
