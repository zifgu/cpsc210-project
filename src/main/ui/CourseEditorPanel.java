package ui;

import model.*;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

public class CourseEditorPanel {
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
    public CourseEditorPanel(CourseList cl) {
        courseList = cl;
        for (Course c : courseList.getCourses()) {
            courses.addElement(c);
        }
        createMainPanel();
    }

    // EFFECTS: returns the JPanel that displays the entire course list
    public JTabbedPane getMainPanel() {
        return mainPanel;
    }

    private void createMainPanel() {
        JPanel editorPanel = createEditorPanel();
        JPanel calculationPanel = createCalculationPanel();

        mainPanel = new JTabbedPane();
        mainPanel.addTab("Course list", editorPanel);
        mainPanel.addTab("Schedule", calculationPanel);
    }

    private JPanel createEditorPanel() {
        JPanel editorPanel = new JPanel();
        editorPanel.setLayout(new BoxLayout(editorPanel, BoxLayout.PAGE_AXIS));

        JPanel courseListPanel = new JPanel(new GridLayout(2, 3));
        createListPanel(courseListPanel);
        JPanel courseEditPanel = createCourseEditPanel();
        courseListPanel.add(courseEditPanel);
        JPanel sectionEditPanel = createSectionEditPanel();
        courseListPanel.add(sectionEditPanel);
        JPanel timeslotEditPanel = createTimeslotEditPanel();
        courseListPanel.add(timeslotEditPanel);

        JPanel saveLoadPanel = createSaveLoadPanel();
        editorPanel.add(courseListPanel);
        editorPanel.add(saveLoadPanel);
        return editorPanel;
    }

    // EFFECTS: creates a JPanel containing 3 lists
    private void createListPanel(JPanel panel) {
        CourseSelectHandler courseHandler = new CourseSelectHandler();
        SectionSelectHandler sectionHandler = new SectionSelectHandler();
        TimeslotSelectHandler timeslotHandler = new TimeslotSelectHandler();
        JScrollPane coursesList = createScrollingList(courses, "Courses", courseHandler, courseHandler);
        JScrollPane sectionsList = createScrollingList(sections, "Sections", sectionHandler, sectionHandler);
        JScrollPane timeslotsList = createScrollingList(timeslots, "Times", timeslotHandler, timeslotHandler);

        panel.add(coursesList);
        panel.add(sectionsList);
        panel.add(timeslotsList);
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

        createButtonAndCommand(buttonPanel, "Save", listener);
        createButtonAndCommand(buttonPanel, "Add", listener);
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

    // EFFECTS: creates a JPanel for this course panel
    private JPanel createCourseEditPanel() {
        JPanel coursePanel = createTitledPanel("Add/edit courses", BoxLayout.PAGE_AXIS);
        coursePanel.setPreferredSize(new Dimension(300, 300));

        JTextField courseNameField = new JTextField(10);
        courseNameField.setMaximumSize(new Dimension(200, 30));
        JLabel courseNameFieldLabel = new JLabel("Course name: ");
        courseNameFieldLabel.setLabelFor(courseNameField);
        JCheckBox courseRequiredField = new JCheckBox("Is required?");
        JLabel feedback = new JLabel();
        coursePanel.add(courseNameFieldLabel);
        coursePanel.add(courseNameField);
        coursePanel.add(courseRequiredField);
        coursePanel.add(Box.createVerticalGlue());
        coursePanel.add(feedback);

        CourseEditListener listener = new CourseEditListener(courseNameField, courseRequiredField, feedback);
        coursePanel.add(createButtonPanel(listener));
        return coursePanel;
    }

    private JPanel createSectionEditPanel() {
        JPanel sectionPanel = createTitledPanel("Add/edit sections", BoxLayout.PAGE_AXIS);
        sectionPanel.setPreferredSize(new Dimension(300, 300));
        JTextField sectionNameField = new JTextField(10);
        sectionNameField.setMaximumSize(new Dimension(200, 30));
        JLabel sectionNameFieldLabel = new JLabel("Section name: ");
        sectionNameFieldLabel.setLabelFor(sectionNameField);
        JLabel feedback = new JLabel();
        sectionPanel.add(sectionNameFieldLabel);
        sectionPanel.add(sectionNameField);
        sectionPanel.add(Box.createVerticalGlue());
        sectionPanel.add(feedback);

        SectionEditListener listener = new SectionEditListener(sectionNameField, feedback);
        sectionPanel.add(createButtonPanel(listener));
        return sectionPanel;
    }

    private JPanel createTimeslotEditPanel() {
        JPanel timeslotPanel = createTitledPanel("Timeslot panel", BoxLayout.PAGE_AXIS);
        timeslotPanel.setPreferredSize(new Dimension(300, 300));
        JLabel feedback = new JLabel("");
        TimeslotEditListener listener = new TimeslotEditListener(feedback);

        timeslotPanel.add(createTimeslotTextFields(listener));
        timeslotPanel.add(createTimeslotDaysOfWeek(listener));
        timeslotPanel.add(Box.createVerticalGlue());
        timeslotPanel.add(feedback);
        createButtonAndCommand(timeslotPanel, "Add", listener);
        return timeslotPanel;
    }

    private JPanel createTimeslotDaysOfWeek(TimeslotEditListener listener) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));
        JCheckBox[] dayCheckBoxes = new JCheckBox[7];
        for (int i = 1; i <= 7; i++) {
            JCheckBox dayCheckBox = new JCheckBox(DayOfWeek.of(i).toString());
            panel.add(dayCheckBox);
            dayCheckBoxes[i - 1] = dayCheckBox;
        }
        listener.setDayCheckBoxes(dayCheckBoxes);
        return panel;
    }

    private JPanel createTimeslotTextFields(TimeslotEditListener listener) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        JTextField termField = new JTextField(5);
        JLabel termFieldLabel = new JLabel("Term:");
        panel.add(termFieldLabel);
        panel.add(termField);
        listener.setTermField(termField);

        JFormattedTextField startTimeField = new JFormattedTextField();
        JLabel startTimeFieldLabel = new JLabel("Start time:");
        panel.add(startTimeFieldLabel);
        panel.add(startTimeField);
        listener.setStartTimeField(startTimeField);

        JFormattedTextField endTimeField = new JFormattedTextField();
        JLabel endTimeFieldLabel = new JLabel("End time:");
        panel.add(endTimeFieldLabel);
        panel.add(endTimeField);
        listener.setEndTimeField(endTimeField);

        return panel;
    }

    private JPanel createSaveLoadPanel() {
        JPanel saveLoadPanel = new JPanel();
        saveLoadPanel.setBorder(BorderFactory.createTitledBorder("Save/load courses"));
        JLabel feedback = new JLabel("");
        saveLoadPanel.add(feedback);
        SaveLoadListener listener = new SaveLoadListener(feedback);

        createButtonAndCommand(saveLoadPanel, "Save", listener);
        createButtonAndCommand(saveLoadPanel, "Load", listener);
        return saveLoadPanel;
    }

    @SuppressWarnings("checkstyle:MethodLength")
    private JPanel createCalculationPanel() {
        JPanel calcPanel = createTitledPanel("Calculate schedules", BoxLayout.PAGE_AXIS);

        JTextField scheduleSizeField = new JTextField();
        scheduleSizeField.setMaximumSize(new Dimension(100, 30));
        JLabel scheduleSizeFieldLabel = new JLabel("Number of courses to calculate:");
        JLabel feedback = new JLabel("");
        JTextArea displayedSchedule = new JTextArea(10, 10);
        CalculationListener listener = new CalculationListener(scheduleSizeField, displayedSchedule, feedback);

        calcPanel.add(scheduleSizeFieldLabel);
        calcPanel.add(scheduleSizeField);
        createButtonAndCommand(calcPanel, "Calculate", listener);
        calcPanel.add(feedback);
        calcPanel.add(displayedSchedule);
        createButtonAndCommand(calcPanel, "Previous", listener);
        createButtonAndCommand(calcPanel, "Next", listener);
        displayedSchedule.setEditable(false);
        displayedSchedule.setLineWrap(true);

        return calcPanel;
    }

    private void createButtonAndCommand(JPanel panel, String command, ActionListener listener) {
        JButton button = new JButton(command);
        button.setActionCommand(command);
        button.addActionListener(listener);
        panel.add(button);
    }

    private JPanel createTitledPanel(String s, int axis) {
        JPanel calcPanel = new JPanel();
        calcPanel.setLayout(new BoxLayout(calcPanel, axis));
        calcPanel.setBorder(BorderFactory.createTitledBorder(s));
        return calcPanel;
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

    private void showSavingSuccessMessage(JLabel feedback) {
        feedback.setText("Successfully saved.");
    }

    private void showSavingErrorMessage(JLabel feedback) {
        feedback.setText("Sorry, could not save.");
    }

    private void showAddingSuccessMessage(JLabel feedback) {
        feedback.setText("Successfully added.");
    }

    private void showAddingErrorMessage(JLabel feedback) {
        feedback.setText("Sorry, could not add.");
    }

    private void showSelectionErrorMessage(JLabel feedback, String type) {
        feedback.setText("No " + type + " selected.");
    }

    private class CourseSelectHandler implements ListSelectionListener, ActionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                JList list = (JList) e.getSource();
                int index = list.getSelectedIndex();

                if (index >= 0) {
                    selectedCourse = courses.getElementAt(index);
                    clearSectionsAndLoad(selectedCourse);
                }
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            courseList.deleteCourse(selectedCourse);
            clearCoursesAndLoad();
        }
    }

    private class SectionSelectHandler implements ListSelectionListener, ActionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                JList list = (JList) e.getSource();
                int index = list.getSelectedIndex();

                if (index >= 0) {
                    selectedSection = sections.getElementAt(index);
                    clearTimeslotsAndLoad(selectedSection);
                }
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            selectedCourse.deleteSection(selectedSection);
            clearSectionsAndLoad(selectedCourse);
        }
    }

    private class TimeslotSelectHandler implements ListSelectionListener, ActionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                JList list = (JList) e.getSource();
                int index = list.getSelectedIndex();
                if (index >= 0) {
                    selectedTimeslot = timeslots.getElementAt(index);
                }
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            selectedSection.deleteTimeslot(selectedTimeslot);
            clearTimeslotsAndLoad(selectedSection);
        }
    }

    private class CourseEditListener implements ActionListener {
        private JTextField courseNameField;
        private JCheckBox courseRequiredField;
        private JLabel feedback;

        // EFFECTS: creates a panel component with buttons for adding or modifying courses
        CourseEditListener(JTextField name, JCheckBox required, JLabel feedback) {
            courseNameField = name;
            courseRequiredField = required;
            this.feedback = feedback;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO: try to make the save update immediately
            if (e.getActionCommand().equals("Save")) {
                updateSelectedCourse();
            } else if (e.getActionCommand().equals("Add")) {
                addNewCourse();
            }
        }

        // TODO: debug the issue where you can add a course with the same name as an edited course
        private void updateSelectedCourse() {
            String name = courseNameField.getText();
            if (selectedCourse != null) {
                boolean nameChanged = !courseList.containsCourseWithName(name);
                System.out.println(nameChanged);
                boolean requiredChanged = selectedCourse.getRequired() != courseRequiredField.isSelected();
                System.out.println(requiredChanged);
                if (nameChanged || requiredChanged) {
                    selectedCourse.setName(name);
                    selectedCourse.setRequired(courseRequiredField.isSelected());
                    System.out.println("Saved course " + name);
                    System.out.println("Contains course? " + courseList.containsCourseWithName(name));
                    showSavingSuccessMessage(feedback);
                } else {
                    showSavingErrorMessage(feedback);
                }
            }
        }

        private void addNewCourse() {
            Course newCourse = new Course(courseNameField.getText(), courseRequiredField.isSelected());
            boolean success = courseList.addCourse(newCourse);
            System.out.println("Added course " + courseNameField.getText() + "? " + success);
            if (success) {
                courses.addElement(newCourse);
                showAddingSuccessMessage(feedback);
            } else {
                showAddingErrorMessage(feedback);
            }
        }
    }

    private class SectionEditListener implements ActionListener {
        private JTextField sectionNameField;
        private JLabel feedback;

        // EFFECTS: creates a panel component with buttons for adding or modifying sections
        SectionEditListener(JTextField name, JLabel feedback) {
            sectionNameField = name;
            this.feedback = feedback;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (selectedCourse != null) {
                if (e.getActionCommand().equals("Save")) {
                    updateSelectedSection();
                } else if (e.getActionCommand().equals("Add")) {
                    addNewSection();
                }
            } else {
                showSelectionErrorMessage(feedback, "course");
            }
        }

        private void updateSelectedSection() {
            String name = sectionNameField.getText();
            if (selectedSection != null) {
                System.out.println("Saving to course " + selectedCourse);
                System.out.println("Section name is " + name);
                if (!selectedCourse.containsSectionWithName(name)) {
                    selectedSection.setName(name);
                    showSavingSuccessMessage(feedback);
                } else {
                    showSavingErrorMessage(feedback);
                }
            }
        }

        private void addNewSection() {
            Section newSection = new Section(sectionNameField.getText(), selectedCourse);
            boolean success = selectedCourse.addSection(newSection);
            if (success) {
                sections.addElement(newSection);
                showAddingSuccessMessage(feedback);
            } else {
                showAddingErrorMessage(feedback);
            }
        }
    }

    private class TimeslotEditListener implements ActionListener {
        private JLabel feedback;
        private JTextField termField;
        private JFormattedTextField startTimeField;
        private JFormattedTextField endTimeField;
        private JCheckBox[] dayCheckBoxes = new JCheckBox[7];

        TimeslotEditListener(JLabel feedback) {
            this.feedback = feedback;
        }

        public void setTermField(JTextField termField) {
            this.termField = termField;
        }

        public void setStartTimeField(JFormattedTextField startTimeField) {
            this.startTimeField = startTimeField;
        }

        public void setEndTimeField(JFormattedTextField endTimeField) {
            this.endTimeField = endTimeField;
        }

        public void setDayCheckBoxes(JCheckBox[] dayCheckBoxes) {
            this.dayCheckBoxes = dayCheckBoxes;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (selectedSection != null) {
                int successCount = 0;
                for (int i = 0; i < 7; i++) {
                    int term;
                    try {
                        term = Integer.parseInt(termField.getText());
                    } catch (NumberFormatException nfe) {
                        continue;
                    }
                    if (dayCheckBoxes[i].isSelected()) {
                        if (addNewTimeslot(term, i + 1)) {
                            successCount++;
                        } else {
                            continue;
                        }
                    }
                }
                feedback.setText("Added " + successCount + " timeslots.");
            } else {
                showSelectionErrorMessage(feedback, "section");
            }
        }

        private boolean addNewTimeslot(int term, int dayOfWeek) {
            LocalTime start;
            LocalTime end;
            try {
                start = LocalTime.parse(startTimeField.getText());
                end = LocalTime.parse(endTimeField.getText());
            } catch (DateTimeParseException dtpe) {
                return false;
            }
            Timeslot t = new Timeslot(term, DayOfWeek.of(dayOfWeek), start, end, selectedSection);
            boolean success = selectedSection.addTimeslot(t);
            if (success) {
                timeslots.addElement(t);
                return true;
            }
            return false;
        }
    }

    private class SaveLoadListener implements ActionListener {
        // TODO: class level comment and invariants
        private JsonReader reader;
        private JsonWriter writer;
        private JLabel feedback;
        private static final String FILE_NAME = "./data/courselist.json";

        // TODO: specification
        public SaveLoadListener(JLabel feedback) {
            reader = new JsonReader(FILE_NAME);
            writer = new JsonWriter(FILE_NAME);
            this.feedback = feedback;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("Save")) {
                writeCourseList();
            } else if (e.getActionCommand().equals("Load")) {
                readCourseList();
            }
        }

        private void writeCourseList() {
            try {
                writer.open();
                writer.writeCourseList(courseList);
                writer.close();
                feedback.setText("Successfully saved course list.");
            } catch (FileNotFoundException fileNotFoundException) {
                feedback.setText("Sorry, there was a problem saving.");
            }
        }

        private void readCourseList() {
            try {
                courseList = reader.read();
                clearCoursesAndLoad();
                feedback.setText("Successfully loaded course list.");
            } catch (IOException ioException) {
                feedback.setText("Sorry, there was a problem loading.");
            }
        }
    }

    private class CalculationListener implements ActionListener {
        private JTextField scheduleSizeField;
        private JTextArea displayedSchedule;
        private JLabel feedback;
        private List<Schedule> selection = new ArrayList<>();
        private int currentIndex;

        public CalculationListener(JTextField size, JTextArea display, JLabel feedback) {
            scheduleSizeField = size;
            displayedSchedule = display;
            this.feedback = feedback;
        }

        @Override
        // TODO: add refresh function
        // TODO: check size of calculated schedules
        public void actionPerformed(ActionEvent e) {
            JButton b = (JButton) e.getSource();
            switch (e.getActionCommand()) {
                case "Calculate":
                    calculateAndDisplaySchedules();
                    break;
                case "Previous":
                    goToPrevious(b);
                    break;
                case "Next":
                    goToNext(b);
                    break;
            }
        }

        private void goToPrevious(JButton button) {
            // TODO: check if there are no schedules
            if (currentIndex > 0) {
                currentIndex--;
            }
            setScheduleAreaText();
        }

        private void goToNext(JButton button) {
            // TODO: check if there are no schedules
            if (currentIndex < selection.size() - 1) {
                currentIndex++;
            }
            setScheduleAreaText();
        }

        private void setScheduleAreaText() {
            String currentSchedule = selection.get(currentIndex).toString();
            String display = "Schedule #" + (currentIndex + 1) + "\n" + currentSchedule;
            displayedSchedule.setText(display);
        }

        private void calculateAndDisplaySchedules() {
            int size = 0;
            try {
                size = Integer.parseInt(scheduleSizeField.getText());
            } catch (NumberFormatException nfe) {
                // do nothing
            }
            boolean success = courseList.allValidSchedules(size);
            if (success) {
                fillSelection(10);
            } else {
                feedback.setText("No schedules possible.");
            }
        }

        private void fillSelection(int selectionSize) {
            selection.clear();
            List<Schedule> schedules = courseList.getAllValidSchedules();
            feedback.setText("Calculated " + schedules.size() + " schedules.");

            int listLength = Math.min(selectionSize, schedules.size());
            if (schedules.size() > selectionSize) {
                Collections.shuffle(schedules);
            }
            for (int i = 0; i < listLength; i++) {
                selection.add(schedules.get(i));
            }
            currentIndex = 0;
            setScheduleAreaText();
        }
    }
}
