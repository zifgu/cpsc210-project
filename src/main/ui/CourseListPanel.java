package ui;

import model.Course;
import model.CourseList;
import model.Section;
import model.Timeslot;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

public class CourseListPanel {
    // TODO: figure out specifications for all of this
    // TODO: invariants - selected section is always a section of selected course?
    // TODO: class level comments
    private CourseList courseList;
    private JPanel mainPanel;
    private DefaultListModel courses = new DefaultListModel();
    private DefaultListModel sections = new DefaultListModel();
    private DefaultListModel timeslots = new DefaultListModel();
    private Course selectedCourse;
    private Section selectedSection;
    private Timeslot selectedTimeslot;

    // EFFECTS: creates a course list panel that will display contents of the given course list
    public CourseListPanel(CourseList cl) {
        courseList = cl;
        for (Course c : courseList.getCourses()) {
            courses.addElement(c.getName());
        }
        mainPanel = createCourseListPanel();
    }

    // EFFECTS: returns the JPanel that displays the entire course list
    public JPanel getMainPanel() {
        return mainPanel;
    }

    // EFFECTS: creates a JPanel containing 3 lists
    private JPanel createCourseListPanel() {
        JPanel panel = new JPanel();
        JScrollPane coursesList = createScrollingList(courses, "Courses", new CourseSelectHandler());
        JScrollPane sectionsList = createScrollingList(sections, "Sections", new SectionSelectHandler());
        JScrollPane timeslotsList = createScrollingList(timeslots, "Times", new TimeslotSelectHandler());
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
        panel.add(coursesList);
        panel.add(sectionsList);
        panel.add(timeslotsList);
        return panel;
    }

    // EFFECTS: creates a single scroll pane with a list attached to listModel
    // TODO: break this up into multiple methods
    private JScrollPane createScrollingList(DefaultListModel listModel, String title, ListSelectionListener l) {
        JList list = new JList(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(l);

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(300, 300));
        TitledBorder border = BorderFactory.createTitledBorder(title);
        scrollPane.setBorder(border);

        return scrollPane;
    }

    // EFFECTS: creates a panel containing a "Save" button and an "Add" button
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEtchedBorder());
        buttonPanel.setMaximumSize(new Dimension(300, 100));
        JButton save = new JButton("Save");
        JButton add = new JButton("Add");
        buttonPanel.add(save);
        buttonPanel.add(add);
        return buttonPanel;
    }

    private class CourseSelectHandler implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                System.out.println("Course selected");
                sections.clear();
                timeslots.clear();

                JList list = (JList) e.getSource();
                String courseName = (String) courses.getElementAt(list.getSelectedIndex());
                System.out.println(courseName);
                selectedCourse = courseList.getCourseByName(courseName);

                // TODO: for debugging
                for (Section s : selectedCourse.getSections()) {
                    sections.addElement(s.getName());
                }
            }
        }
    }

    private class SectionSelectHandler implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                System.out.println("Section selected");
                JList list = (JList) e.getSource();
                int index = list.getSelectedIndex();
                timeslots.clear();

                if (index >= 0) {
                    String sectionName = (String) sections.getElementAt(index);
                    selectedSection = selectedCourse.getSectionByName(sectionName);
                    // TODO: for debugging
                    System.out.println(selectedSection);

                    for (Timeslot t : selectedSection.getTimeslots()) {
                        timeslots.addElement(t);
                    }
                }
            }
        }
    }

    private class TimeslotSelectHandler implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                System.out.println("Timeslot selected");
                JList list = (JList) e.getSource();
                int index = list.getSelectedIndex();
                if (index >= 0) {
                    selectedTimeslot = (Timeslot) timeslots.getElementAt(index);
                    // TODO: for debugging
                    System.out.println(selectedTimeslot);
                }
            }
        }
    }
//
//    private class CoursePanel implements ActionListener, ItemListener {
//        private JPanel coursePanel;
//
//        // EFFECTS: creates a panel component with buttons for adding or modifying courses
//        CoursePanel() {
//            coursePanel = createCoursePanel();
//        }
//
//        // EFFECTS: creates a JPanel for this course panel
//        private JPanel createCoursePanel() {
//            JPanel panel = new JPanel();
//            panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
//            panel.setBorder(BorderFactory.createTitledBorder("Course panel"));
//            panel.setPreferredSize(new Dimension(300, 300));
//
//            JTextField textField = new JTextField(10);
//            textField.setMaximumSize(new Dimension(200, 30));
//            JLabel textFieldLabel = new JLabel("Course name: ");
//            textFieldLabel.setLabelFor(textField);
//            JCheckBox checkBox = new JCheckBox("Is required?");
//            panel.add(textFieldLabel);
//            panel.add(textField);
//            panel.add(checkBox);
//            panel.add(Box.createVerticalGlue());
//            panel.add(createButtonPanel());
//            return panel;
//        }
//
//        @Override
//        public void actionPerformed(ActionEvent e) {
//
//        }
//
//        @Override
//        public void itemStateChanged(ItemEvent e) {
//
//        }
//
//        // EFFECTS: returns the JPanel with the menu that allows you to add courses
//        JPanel getCoursePanel() {
//            return coursePanel;
//        }
//
//    }
//
//    private class SectionPanel implements ActionListener {
//        private JPanel sectionPanel;
//
//        // EFFECTS: creates a panel component with buttons for adding or modifying sections
//        SectionPanel() {
//
//        }
//
//        @Override
//        public void actionPerformed(ActionEvent e) {
//
//        }
//
//        // EFFECTS: returns the JPanel with the menu that allows you to add sections
//        JPanel getSectionPanel() {
//            return sectionPanel;
//        }
//    }
//
//    private class TimeslotPanel implements ActionListener, ItemListener {
//        private JPanel timeslotPanel;
//
//        // EFFECTS: creates a panel component with buttons for adding or modifying time slots
//        TimeslotPanel() {
//
//        }
//
//        @Override
//        public void actionPerformed(ActionEvent e) {
//
//        }
//
//        @Override
//        public void itemStateChanged(ItemEvent e) {
//
//        }
//
//        // EFFECTS: returns the JPanel with the menu that allows you to add timeslots
//        JPanel getTimeslotPanel() {
//            return timeslotPanel;
//        }
//    }
}
