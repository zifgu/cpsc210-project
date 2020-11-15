package ui;

import model.*;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;

import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

public class CourseListPanel {
    // TODO: figure out specifications for all of this
    // TODO: invariants - selected section is always a section of selected course?
    // TODO: class level comments
    private CourseList courseList;
    private JTabbedPane mainPanel;
    private DefaultListModel<Course> courses = new DefaultListModel<>();
    private DefaultListModel<Section> sections = new DefaultListModel<>();
    private DefaultListModel<Timeslot> timeslots = new DefaultListModel<>();
    private Course selectedCourse;
    private Section selectedSection;
    private Timeslot selectedTimeslot;

    // EFFECTS: creates a course list panel that will display contents of the given course list
    public CourseListPanel(CourseList cl) {
        courseList = cl;
        for (Course c : courseList.getCourses()) {
            courses.addElement(c);
        }
        JPanel listPanel = createCourseListPanel();
        CoursePanel cp = new CoursePanel();
        SectionPanel sp = new SectionPanel();
        TimeslotPanel tp = new TimeslotPanel();
        JPanel editPanel = new JPanel();
        editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.LINE_AXIS));
        editPanel.add(cp.getCoursePanel());
        editPanel.add(sp.getSectionPanel());
        editPanel.add(tp.getTimeslotPanel());
        JPanel coursePanel = new JPanel();
        coursePanel.setLayout(new BoxLayout(coursePanel, BoxLayout.PAGE_AXIS));
        SaveLoadPanel slp = new SaveLoadPanel();

        coursePanel.add(listPanel);
        coursePanel.add(editPanel);
        coursePanel.add(slp.getSaveLoadPanel());

        CalculatorPanel calcPanel = new CalculatorPanel();

        mainPanel = new JTabbedPane();
        mainPanel.addTab("Course list", coursePanel);
        mainPanel.addTab("Schedule", calcPanel.getCalcPanel());
    }

    // EFFECTS: returns the JPanel that displays the entire course list
    public JTabbedPane getMainPanel() {
        return mainPanel;
    }

    // EFFECTS: creates a JPanel containing 3 lists
    private JPanel createCourseListPanel() {
        JPanel panel = new JPanel();
        CourseSelectHandler courseHandler = new CourseSelectHandler();
        SectionSelectHandler sectionHandler = new SectionSelectHandler();
        TimeslotSelectHandler timeslotHandler = new TimeslotSelectHandler();
        JScrollPane coursesList = createScrollingList(courses, "Courses", courseHandler, courseHandler);
        JScrollPane sectionsList = createScrollingList(sections, "Sections", sectionHandler, sectionHandler);
        JScrollPane timeslotsList = createScrollingList(timeslots, "Times", timeslotHandler, timeslotHandler);

        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
        panel.add(coursesList);
        panel.add(sectionsList);
        panel.add(timeslotsList);
        return panel;
    }

    // EFFECTS: creates a single scroll pane with a list attached to listModel
    // TODO: break this up into multiple methods
    private JScrollPane createScrollingList(DefaultListModel lm, String t, ListSelectionListener l, ActionListener al) {
        JList list = new JList(lm);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(l);
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                createPopupMenu(e, list, al);
            }
        });

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(300, 300));
        TitledBorder border = BorderFactory.createTitledBorder(t);
        scrollPane.setBorder(border);

        return scrollPane;
    }

    // EFFECTS: creates a panel containing a "Save" button and an "Add" button
    private JPanel createButtonPanel(ActionListener listener) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEtchedBorder());
        buttonPanel.setMaximumSize(new Dimension(300, 100));

        JButton save = new JButton("Save");
        save.setActionCommand("Save");
        save.addActionListener(listener);

        JButton add = new JButton("Add");
        add.setActionCommand("Add");
        add.addActionListener(listener);

        buttonPanel.add(save);
        buttonPanel.add(add);
        return buttonPanel;
    }

    private void createPopupMenu(MouseEvent e, Component invoker, ActionListener actionListener) {
        if (e.isPopupTrigger()) {
            JPopupMenu popupMenu = new JPopupMenu();
            JMenuItem deleteOption = new JMenuItem("Delete");
            deleteOption.addActionListener(actionListener);
            popupMenu.add(deleteOption);
            popupMenu.show(invoker, e.getX(), e.getY());
        }
    }

    private void clearCoursesAndLoad() {
        courses.clear();
        sections.clear();
        timeslots.clear();
        for (Course c : courseList.getCourses()) {
            courses.addElement(c);
        }
        selectedCourse = null;
        selectedSection = null;
        selectedTimeslot = null;
    }

    private void clearSectionsAndLoad(Course c) {
        sections.clear();
        timeslots.clear();
        for (Section s : c.getSections()) {
            sections.addElement(s);
        }
        selectedSection = null;
        selectedTimeslot = null;
    }

    private void clearTimeslotsAndLoad(Section s) {
        timeslots.clear();
        for (Timeslot t : s.getTimeslots()) {
            timeslots.addElement(t);
        }
        selectedTimeslot = null;
    }

    private class CourseSelectHandler implements ListSelectionListener, ActionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                // TODO: for debugging
                System.out.println("Course selected");
                JList list = (JList) e.getSource();
                int index = list.getSelectedIndex();

                if (index >= 0) {
                    selectedCourse = courses.getElementAt(index);
                    clearSectionsAndLoad(selectedCourse);
                }
            }
        }

        @Override
        // TODO: repetition of code, also decide whether to use update method or manually delete?
        public void actionPerformed(ActionEvent e) {
            courseList.deleteCourse(selectedCourse);
            clearCoursesAndLoad();
        }
    }

    private class SectionSelectHandler implements ListSelectionListener, ActionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                // TODO: for debugging
                System.out.println("Section selected");
                JList list = (JList) e.getSource();
                int index = list.getSelectedIndex();

                if (index >= 0) {
                    selectedSection = sections.getElementAt(index);
                    // TODO: for debugging
                    System.out.println(selectedSection);
                    clearTimeslotsAndLoad(selectedSection);
                }
            }
        }

        @Override
        // TODO: repetition of code, also decide whether to use update method or manually delete?
        public void actionPerformed(ActionEvent e) {
            selectedCourse.deleteSection(selectedSection);
            clearSectionsAndLoad(selectedCourse);
        }
    }

    private class TimeslotSelectHandler implements ListSelectionListener, ActionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                // TODO: for debugging
                System.out.println("Timeslot selected");
                JList list = (JList) e.getSource();
                int index = list.getSelectedIndex();
                if (index >= 0) {
                    selectedTimeslot = timeslots.getElementAt(index);
                    // TODO: for debugging
                    System.out.println(selectedTimeslot);
                }
            }
        }

        @Override
        // TODO: repetition of code, also decide whether to use update method or manually delete?
        public void actionPerformed(ActionEvent e) {
            selectedSection.deleteTimeslot(selectedTimeslot);
            clearTimeslotsAndLoad(selectedSection);
        }
    }

    private class CoursePanel implements ActionListener {
        private JPanel coursePanel;
        private JTextField courseNameField;
        private JCheckBox courseRequiredField;
        private JLabel feedback;

        // EFFECTS: creates a panel component with buttons for adding or modifying courses
        CoursePanel() {
            createCoursePanel();
        }

        // EFFECTS: creates a JPanel for this course panel
        private void createCoursePanel() {
            coursePanel = new JPanel();
            coursePanel.setLayout(new BoxLayout(coursePanel, BoxLayout.PAGE_AXIS));
            coursePanel.setBorder(BorderFactory.createTitledBorder("Add/edit courses"));
            coursePanel.setPreferredSize(new Dimension(300, 300));

            courseNameField = new JTextField(10);
            courseNameField.setMaximumSize(new Dimension(200, 30));
            JLabel courseNameFieldLabel = new JLabel("Course name: ");
            courseNameFieldLabel.setLabelFor(courseNameField);
            courseRequiredField = new JCheckBox("Is required?");
            feedback = new JLabel();
            coursePanel.add(courseNameFieldLabel);
            coursePanel.add(courseNameField);
            coursePanel.add(courseRequiredField);
            coursePanel.add(Box.createVerticalGlue());
            coursePanel.add(feedback);
            coursePanel.add(createButtonPanel(this));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO: single point of control
            // TODO: check for duplicate names
            // TODO: try to make the save update immediately
            feedback.setText("");
            if (e.getActionCommand().equals("Save")) {
                selectedCourse.setName(courseNameField.getText());
                selectedCourse.setRequired(courseRequiredField.isSelected());
            } else if (e.getActionCommand().equals("Add")) {
                Course newCourse = new Course(courseNameField.getText(), courseRequiredField.isSelected());
                boolean success = courseList.addCourse(newCourse);
                if (success) {
                    feedback.setText("Successfully added course.");
                    courses.addElement(newCourse);
                } else {
                    feedback.setText("Sorry, could not add course.");
                }
            }
        }

        // EFFECTS: returns the JPanel with the menu that allows you to add courses
        JPanel getCoursePanel() {
            return coursePanel;
        }
    }

    private class SectionPanel implements ActionListener {
        private JPanel sectionPanel;
        private JTextField sectionNameField;
        private JLabel feedback;

        // EFFECTS: creates a panel component with buttons for adding or modifying sections
        SectionPanel() {
            createSectionPanel();
        }

        private void createSectionPanel() {
            sectionPanel = new JPanel();
            sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.PAGE_AXIS));
            sectionPanel.setBorder(BorderFactory.createTitledBorder("Add/edit sections"));
            sectionPanel.setPreferredSize(new Dimension(300, 300));
            sectionNameField = new JTextField(10);
            sectionNameField.setMaximumSize(new Dimension(200, 30));
            JLabel sectionNameFieldLabel = new JLabel("Section name: ");
            sectionNameFieldLabel.setLabelFor(sectionNameField);
            feedback = new JLabel();
            sectionPanel.add(sectionNameFieldLabel);
            sectionPanel.add(sectionNameField);
            sectionPanel.add(Box.createVerticalGlue());
            sectionPanel.add(feedback);
            sectionPanel.add(createButtonPanel(this));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (selectedCourse != null) {
                feedback.setText("");
                if (e.getActionCommand().equals("Save")) {
                    selectedSection.setName(sectionNameField.getText());
                } else if (e.getActionCommand().equals("Add")) {
                    Section newSection = new Section(sectionNameField.getText(), selectedCourse);
                    boolean success = selectedCourse.addSection(newSection);
                    if (success) {
                        feedback.setText("Successfully added section.");
                        sections.addElement(newSection);
                    } else {
                        feedback.setText("Sorry, could not add section.");
                    }
                }
            } else {
                feedback.setText("No course selected.");
            }
        }

        // EFFECTS: returns the JPanel with the menu that allows you to add sections
        JPanel getSectionPanel() {
            return sectionPanel;
        }
    }

    private class TimeslotPanel implements ActionListener {
        private JPanel timeslotPanel;
        private JLabel feedback;
        private JTextField termField;
        private JFormattedTextField startTimeField;
        private JFormattedTextField endTimeField;
        private JCheckBox[] dayCheckBoxes = new JCheckBox[7];

        // EFFECTS: creates a panel component with buttons for adding or modifying time slots
        TimeslotPanel() {
            createTimeslotPanel();
        }

        private void createTimeslotPanel() {
            timeslotPanel = new JPanel();
            timeslotPanel.setLayout(new BoxLayout(timeslotPanel, BoxLayout.PAGE_AXIS));
            timeslotPanel.setBorder(BorderFactory.createTitledBorder("Timeslot panel"));
            timeslotPanel.setPreferredSize(new Dimension(300, 300));
            feedback = new JLabel("");
            JButton add = new JButton("Add");
            add.setActionCommand("Add");
            add.addActionListener(this);

            timeslotPanel.add(createTimeslotTextFields());
            timeslotPanel.add(createTimeslotDaysOfWeek());
            timeslotPanel.add(Box.createVerticalGlue());
            timeslotPanel.add(feedback);
            timeslotPanel.add(add);
        }

        private JPanel createTimeslotDaysOfWeek() {
            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(4, 2));
            for (int i = 1; i <= 7; i++) {
                JCheckBox dayCheckBox = new JCheckBox(DayOfWeek.of(i).toString());
                panel.add(dayCheckBox);
                dayCheckBoxes[i - 1] = dayCheckBox;
            }
            return panel;
        }

        private JPanel createTimeslotTextFields() {
            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(3, 2));

            termField = new JTextField(5);
            JLabel termFieldLabel = new JLabel("Term:");
            panel.add(termFieldLabel);
            panel.add(termField);

            startTimeField = new JFormattedTextField();
            JLabel startTimeFieldLabel = new JLabel("Start time:");
            panel.add(startTimeFieldLabel);
            panel.add(startTimeField);

            endTimeField = new JFormattedTextField();
            JLabel endTimeFieldLabel = new JLabel("End time:");
            panel.add(endTimeFieldLabel);
            panel.add(endTimeField);

            return panel;
        }

        @SuppressWarnings("checkstyle:MethodLength")
        @Override
        // TODO: method is too long
        public void actionPerformed(ActionEvent e) {
            feedback.setText("");
            if (selectedSection != null) {
                if (e.getActionCommand().equals("Add")) {
                    int successCount = 0;
                    for (int i = 0; i < 7; i++) {
                        int term;
                        try {
                            term = Integer.parseInt(termField.getText());
                        } catch (NumberFormatException nfe) {
                            continue;
                        }
                        if (dayCheckBoxes[i].isSelected()) {
                            LocalTime start;
                            LocalTime end;
                            try {
                                start = LocalTime.parse(startTimeField.getText());
                                end = LocalTime.parse(endTimeField.getText());
                            } catch (DateTimeParseException dtpe) {
                                continue;
                            }
                            Timeslot t = new Timeslot(term, DayOfWeek.of(i + 1), start, end, selectedSection);
                            boolean success = selectedSection.addTimeslot(t);
                            if (success) {
                                successCount++;
                                timeslots.addElement(t);
                            }
                        }
                    }
                    feedback.setText("Added " + successCount + " timeslots.");
                }
            } else {
                feedback.setText("No section selected.");
            }
        }

        // EFFECTS: returns the JPanel with the menu that allows you to add timeslots
        JPanel getTimeslotPanel() {
            return timeslotPanel;
        }
    }

    private class SaveLoadPanel implements ActionListener {
        // TODO: class level comment and invariants
        private JsonReader reader;
        private JsonWriter writer;
        private JPanel saveLoadPanel;
        private JLabel feedback;
        private static final String FILE_NAME = "./data/courselist.json";

        // TODO: specification
        public SaveLoadPanel() {
            reader = new JsonReader(FILE_NAME);
            writer = new JsonWriter(FILE_NAME);
            createSaveLoadPanel();
        }

        private void createSaveLoadPanel() {
            saveLoadPanel = new JPanel();
            saveLoadPanel.setBorder(BorderFactory.createTitledBorder("Save/load courses"));
            JButton save = new JButton("Save");
            JButton load = new JButton("Load");
            save.addActionListener(this);
            save.setActionCommand("Save");
            load.addActionListener(this);
            load.setActionCommand("Load");
            saveLoadPanel.add(save);
            saveLoadPanel.add(load);
            feedback = new JLabel("");
            saveLoadPanel.add(feedback);
        }

        public JPanel getSaveLoadPanel() {
            return saveLoadPanel;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("Save")) {
                try {
                    writer.open();
                    writer.writeCourseList(courseList);
                    writer.close();
                    feedback.setText("Successfully saved course list.");
                } catch (FileNotFoundException fileNotFoundException) {
                    feedback.setText("Sorry, there was a problem saving.");
                }
            } else if (e.getActionCommand().equals("Load")) {
                try {
                    courseList = reader.read();
                    clearCoursesAndLoad();
                    feedback.setText("Successfully loaded course list.");
                } catch (IOException ioException) {
                    feedback.setText("Sorry, there was a problem loading.");
                }
            }
        }
    }

    private class CalculatorPanel implements ActionListener {
        private JPanel calcPanel;
        private JTextField scheduleSizeField;
        private JTextArea displayedSchedule;
        private JButton nextButton;
        private JButton prevButton;
        private JLabel feedback;
        private java.util.List<Schedule> selection = new ArrayList<>();
        private int currentIndex;
        private int listLength;

        public CalculatorPanel() {
            createMainPanel();
        }

        @SuppressWarnings("checkstyle:MethodLength")
        private void createMainPanel() {
            calcPanel = new JPanel();
            calcPanel.setLayout(new BoxLayout(calcPanel, BoxLayout.PAGE_AXIS));
            calcPanel.setBorder(BorderFactory.createTitledBorder("Calculate schedules"));

            // TODO: this doesn't update
//        JLabel info = new JLabel("Your course list contains " + courseList.numCourses() + " courses.");
//        mainPanel.add(info);

            scheduleSizeField = new JTextField();
            scheduleSizeField.setMaximumSize(new Dimension(100, 30));
            JLabel scheduleSizeFieldLabel = new JLabel("Enter the number of courses to calculate:");
            calcPanel.add(scheduleSizeFieldLabel);
            calcPanel.add(scheduleSizeField);

            JButton calculateButton = new JButton("Calculate");
            calculateButton.setActionCommand("Calculate");
            calculateButton.addActionListener(this);
            calcPanel.add(calculateButton);

            feedback = new JLabel("");
            calcPanel.add(feedback);

            displayedSchedule = new JTextArea(100, 100);
            displayedSchedule.setEditable(false);
            displayedSchedule.setLineWrap(true);
            calcPanel.add(displayedSchedule);

            prevButton = new JButton("Previous");
            prevButton.setActionCommand("Previous");
            prevButton.addActionListener(this);
            nextButton = new JButton("Next");
            nextButton.setActionCommand("Next");
            nextButton.addActionListener(this);
            calcPanel.add(prevButton);
            calcPanel.add(nextButton);
        }

        private JPanel getCalcPanel() {
            return calcPanel;
        }

        @Override
        // TODO: add refresh function
        // TODO: fix code duplication
        // TODO: check size of calculated schedules
        public void actionPerformed(ActionEvent e) {
            switch (e.getActionCommand()) {
                case "Calculate":
                    calculateAndDisplaySchedules();
                    break;
                case "Previous":
                    // TODO: check if there are no schedules
                    // TODO: disable/enable buttons, maybe change a label
                    System.out.println("Previous button pressed: current index is " + currentIndex);
                    if (currentIndex > 0) {
                        currentIndex--;
                    }
                    displayedSchedule.setText(selection.get(currentIndex).toString());
                    break;
                case "Next":
                    // TODO: check if there are no schedules
                    // TODO: disable/enable buttons, maybe change a label
                    System.out.println("Next button pressed: current index is " + currentIndex);
                    if (currentIndex < listLength - 1) {
                        currentIndex++;
                    }
                    displayedSchedule.setText(selection.get(currentIndex).toString());
                    break;
            }
        }

        private void calculateAndDisplaySchedules() {
            try {
                selection.clear();
                int size = Integer.parseInt(scheduleSizeField.getText());
                System.out.println(size);
                boolean success = courseList.allValidSchedules(size);
                if (success) {
                    List<Schedule> schedules = courseList.getAllValidSchedules();
                    feedback.setText("Successfully calculated " + schedules.size() + " schedules.");
                    listLength = Math.min(10, schedules.size());
                    if (schedules.size() > 10) {
                        Collections.shuffle(schedules);
                    }
                    for (int i = 0; i < listLength; i++) {
                        selection.add(schedules.get(i));
                    }
                    currentIndex = 0;
                    displayedSchedule.setText(selection.get(currentIndex).toString());
                } else {
                    feedback.setText("No schedules possible.");
                }
            } catch (NumberFormatException nfe) {
                // do nothing (???)
            }
        }
    }
}
