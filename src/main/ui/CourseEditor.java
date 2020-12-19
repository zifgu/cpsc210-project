package ui;

import exceptions.ScheduleSizeException;
import model.*;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

/*
    GUI for the schedule application
*/
public class CourseEditor {
    private CourseList courseList;
    private JTabbedPane mainPanel;
    private DefaultListModel<Course> courses = new DefaultListModel<>();
    private DefaultListModel<Section> sections = new DefaultListModel<>();
    private DefaultListModel<Timeslot> timeslots = new DefaultListModel<>();
    private Course selectedCourse;
    private Section selectedSection;
    private Timeslot selectedTimeslot;

    // EFFECTS: creates a course list panel that will display contents of the given course list
    public CourseEditor() {
        courseList = new CourseList();
        createMainPanel();
    }

    // EFFECTS: returns a JTabbedPane with one tab for displaying and editing courses
    //          and one tab for calculating schedules
    public JTabbedPane getMainPanel() {
        return mainPanel;
    }

    // MODIFIES: this
    // EFFECTS: initializes the main JTabbedPane
    private void createMainPanel() {
        JPanel editorPanel = createEditorPanel();
        JPanel calculationPanel = createCalculationPanel();

        mainPanel = new JTabbedPane();
        mainPanel.addTab("Course list", editorPanel);
        mainPanel.addTab("Schedule", calculationPanel);
    }

    // EFFECTS: returns a JPanel used to view and edit the course list
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

    // MODIFIES: panel
    // EFFECTS: returns a JPanel containing 3 lists, which is used to view the course list
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

    // EFFECTS: returns a single scroll pane containing a list attached to lm
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
        buttonPanel.setMaximumSize(new Dimension(300, 100));

        createButtonAndCommand(buttonPanel, "Save", listener);
        createButtonAndCommand(buttonPanel, "Add", listener);
        return buttonPanel;
    }

    // EFFECTS: creates a popup menu with one menu option for deleting the selected list item
    private void createPopupMenu(MouseEvent e, JList invoker, ActionListener actionListener) {
        if (e.isPopupTrigger()) {
            JPopupMenu popupMenu = new JPopupMenu();
            JMenuItem deleteOption = new JMenuItem("Delete");
            deleteOption.addActionListener(actionListener);
            popupMenu.add(deleteOption);
            popupMenu.show(invoker, e.getX(), e.getY());
        }
    }

    // EFFECTS: returns a JPanel with a text field to edit the course name, check box to toggle elective status,
    //          and buttons to save course info or add a new course
    private JPanel createCourseEditPanel() {
        JPanel coursePanel = createTitledPanel("Add/edit courses");
        coursePanel.setPreferredSize(new Dimension(300, 300));

        JTextField name = new JTextField(10);
        name.setMaximumSize(new Dimension(200, 30));
        JLabel nameLabel = createCenterAlignedLabel("Course name:");
        JCheckBox required = new JCheckBox("Is required?");
        required.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel feedback = createCenterAlignedLabel("");
        coursePanel.add(nameLabel);
        coursePanel.add(name);
        coursePanel.add(required);
        coursePanel.add(Box.createVerticalGlue());
        coursePanel.add(feedback);

        CourseEditListener listener = new CourseEditListener(name, required, feedback);
        createButtonAndCommand(coursePanel, "Add from SSC", listener);
        coursePanel.add(createButtonPanel(listener));
        return coursePanel;
    }

    // EFFECTS: returns a JPanel with a text field to edit the section name and buttons to save section info
    //          or add a new section to selected course
    private JPanel createSectionEditPanel() {
        JPanel sectionPanel = createTitledPanel("Add/edit sections");
        sectionPanel.setPreferredSize(new Dimension(300, 300));
        JTextField name = new JTextField(10);
        name.setMaximumSize(new Dimension(200, 30));
        JLabel nameLabel = createCenterAlignedLabel("Section name:");
        JLabel feedback = createCenterAlignedLabel("");
        sectionPanel.add(nameLabel);
        sectionPanel.add(name);
        sectionPanel.add(Box.createVerticalGlue());
        sectionPanel.add(feedback);

        SectionEditListener listener = new SectionEditListener(name, feedback);
        sectionPanel.add(createButtonPanel(listener));
        return sectionPanel;
    }

    // EFFECTS: returns a JPanel with text fields for term, start time, and end time, checkboxes for days of the week,
    //          and buttons to add timeslots to selected section
    private JPanel createTimeslotEditPanel() {
        JPanel timeslotPanel = createTitledPanel("Add timeslots");
        timeslotPanel.setPreferredSize(new Dimension(300, 300));
        JLabel feedback = createCenterAlignedLabel("");
        TimeslotEditListener listener = new TimeslotEditListener(feedback);

        timeslotPanel.add(createTimeslotTextFields(listener));
        timeslotPanel.add(createTimeslotDaysOfWeek(listener));
        timeslotPanel.add(Box.createVerticalGlue());
        timeslotPanel.add(feedback);
        createButtonAndCommand(timeslotPanel, "Add", listener);
        return timeslotPanel;
    }

    // EFFECTS: returns the part of the timeslot edit panel that contains checkboxes for the days of week
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

    // EFFECTS: returns the part of the timeslot edit panel that contains text fields for term, start time, and end time
    private JPanel createTimeslotTextFields(TimeslotEditListener listener) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        JTextField term = new JTextField(5);
        JLabel termLabel = new JLabel("Term:");
        panel.add(termLabel);
        panel.add(term);
        listener.setTermField(term);

        JTextField startTime = new JTextField();
        JLabel startTimeLabel = new JLabel("Start time:");
        panel.add(startTimeLabel);
        panel.add(startTime);
        listener.setStartTimeField(startTime);

        JTextField endTime = new JTextField();
        JLabel endTimeLabel = new JLabel("End time:");
        panel.add(endTimeLabel);
        panel.add(endTime);
        listener.setEndTimeField(endTime);

        return panel;
    }

    // EFFECTS: returns a JPanel with buttons for saving and loading course lists
    private JPanel createSaveLoadPanel() {
        JPanel saveLoadPanel = new JPanel();
        saveLoadPanel.setBorder(BorderFactory.createTitledBorder("Save/load courses"));
        JLabel feedback = createCenterAlignedLabel("");
        saveLoadPanel.add(feedback);
        SaveLoadListener listener = new SaveLoadListener(feedback);

        createButtonAndCommand(saveLoadPanel, "Save", listener);
        createButtonAndCommand(saveLoadPanel, "Load", listener);
        return saveLoadPanel;
    }

    // EFFECTS: returns a JPanel used for calculating and displaying possible schedules
    private JPanel createCalculationPanel() {
        JPanel calcPanel = createTitledPanel("Calculate schedules");

        JTextField scheduleSize = new JTextField();
        scheduleSize.setMaximumSize(new Dimension(100, 30));
        JLabel scheduleSizeLabel = createCenterAlignedLabel("Number of courses in the schedule: ");
        JLabel feedback = createCenterAlignedLabel("");
        JTextArea displayedSchedule = new JTextArea(10, 10);
        CalculationListener listener = new CalculationListener(scheduleSize, displayedSchedule, feedback);

        calcPanel.add(scheduleSizeLabel);
        calcPanel.add(scheduleSize);
        createButtonAndCommand(calcPanel, "Calculate", listener);
        calcPanel.add(feedback);
        calcPanel.add(displayedSchedule);
        createButtonAndCommand(calcPanel, "Previous", listener);
        createButtonAndCommand(calcPanel, "Next", listener);
        displayedSchedule.setEditable(false);
        displayedSchedule.setLineWrap(true);

        return calcPanel;
    }

    // MODIFIES: panel
    // EFFECTS: adds a button to panel with text and actionCommand equal to command and ActionListener listener
    private void createButtonAndCommand(JPanel panel, String command, ActionListener listener) {
        JButton button = new JButton(command);
        button.setActionCommand(command);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(listener);
        panel.add(button);
    }

    // EFFECTS: returns a JPanel with a BoxLayout using PAGE_AXIS and a border with title s
    private JPanel createTitledPanel(String s) {
        JPanel calcPanel = new JPanel();
        calcPanel.setLayout(new BoxLayout(calcPanel, BoxLayout.PAGE_AXIS));
        calcPanel.setBorder(BorderFactory.createTitledBorder(s));
        return calcPanel;
    }

    private JLabel createCenterAlignedLabel(String text) {
        JLabel feedback = new JLabel(text);
        feedback.setAlignmentX(Component.CENTER_ALIGNMENT);
        return feedback;
    }

    // MODIFIES: this
    // EFFECTS: clears sections and timeslots, refreshes the list of courses, and unselects all selections
    private void clearCoursesAndLoad() {
        courses.clear();
        sections.clear();
        timeslots.clear();
        for (Course c : courseList) {
            courses.addElement(c);
        }
        selectedCourse = null;
        selectedSection = null;
        selectedTimeslot = null;
    }

    // MODIFIES: this
    // EFFECTS: clears timeslots, refreshes the list of sections, and unselects selected sections and timeslots
    private void clearSectionsAndLoad(Course c) {
        sections.clear();
        timeslots.clear();
        for (Section s : c) {
            sections.addElement(s);
        }
        selectedSection = null;
        selectedTimeslot = null;
    }

    // MODIFIES: this
    // EFFECTS: refreshes the list of timeslots and unselects the selected timeslot
    private void clearTimeslotsAndLoad(Section s) {
        timeslots.clear();
        for (Timeslot t : s) {
            timeslots.addElement(t);
        }
        selectedTimeslot = null;
    }

    // MODIFIES: feedback
    // EFFECTS: makes feedback display a message that the save succeeded
    private void showSuccessMessage(JLabel feedback, String action) {
        feedback.setText("Successfully " + action + ".");
        playSuccessSound();
    }

    // MODIFIES: feedback
    // EFFECTS: makes feedback display a message that the save failed
    private void showFailMessage(JLabel feedback, String action) {
        feedback.setText("Sorry, could not " + action + ".");
        playFailSound();
    }

    // MODIFIES: feedback
    // EFFECTS: makes feedback display a message that no item of type type is selected
    private void showSelectionErrorMessage(JLabel feedback, String type) {
        feedback.setText("No " + type + " selected.");
        playFailSound();
    }

    // EFFECTS: plays a "success" sound
    private void playSuccessSound() {
        SoundPlayer player = new SoundPlayer(true);
        player.play();
    }

    // EFFECTS: plays a "fail" sound
    private void playFailSound() {
        SoundPlayer player = new SoundPlayer(false);
        player.play();
    }

    /*
        Handles selection in the list of courses
     */
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

    /*
        Handles selection in the list of sections
     */
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

    /*
        Handles selection in the list of timeslots
     */
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

    /*
        Handles adding and editing of courses
     */
    private class CourseEditListener implements ActionListener {
        private JTextField courseNameField;
        private JCheckBox courseRequiredField;
        private JLabel feedback;
        private String path;

        // EFFECTS: constructs an ActionListener with access to the given text field, checkbox, and label
        private CourseEditListener(JTextField name, JCheckBox required, JLabel feedback) {
            courseNameField = name;
            courseRequiredField = required;
            this.feedback = feedback;
            this.path = "";
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (e.getActionCommand()) {
                case "Save":
                    updateSelectedCourse();
                    break;
                case "Add":
                    addNewCourse();
                    break;
                case "Add from SSC":
                    addCourseFromSSC();
                    break;
            }
        }

        // MODIFIES: this
        // EFFECTS: updates selectedCourse with courseNameField and courseRequiredField
        //          if doing so would not create duplicate courses, and shows feedback
        private void updateSelectedCourse() {
            String name = courseNameField.getText();
            if (selectedCourse != null) {
                boolean nameChanged = !courseList.containsCourseWithName(name);
                boolean requiredChanged = selectedCourse.getRequired() != courseRequiredField.isSelected();
                if (nameChanged || requiredChanged) {
                    courseList.changeCourseName(selectedCourse, name);
                    selectedCourse.setRequired(courseRequiredField.isSelected());
                    showSuccessMessage(feedback, "saved");
                } else {
                    showFailMessage(feedback, "save");
                }
            }
        }

        // MODIFIES: this
        // EFFECTS: adds a new course with courseNameField and courseRequiredField
        //          if doing so would not create duplicate courses, and shows feedback
        private void addNewCourse() {
            Course newCourse = new Course(courseNameField.getText(), courseRequiredField.isSelected());
            addToList(newCourse);
        }

        // MODIFIES: this
        // EFFECTS: gets section info about courseNameField from the UBC website, tries to add it to the course list,
        //          and shows feedback
        private void addCourseFromSSC() {
            String name = courseNameField.getText().trim();
            String dept = "";
            String number = "";
            if (isUbcFormattedCourse(name)) {
                for (int i = 3; i < name.length(); i++) {
                    if (Character.isDigit(name.charAt(i))) {
                        dept = name.substring(0, i);
                        number = name.substring(i);
                        break;
                    }
                }

                path = JOptionPane.showInputDialog(mainPanel, "Enter your Python path, or use this value:", path);
                Course newCourse = new Course(dept, number, courseRequiredField.isSelected(), path);
                if (newCourse.numSections() > 0) {
                    addToList(newCourse);
                    return;
                }
            }
            showFailMessage(feedback, "add");
        }

        // MODIFIES: this
        // EFFECTS: adds c to courseList and displays feedback
        private void addToList(Course c) {
            boolean success = courseList.addCourse(c);
            if (success) {
                courses.addElement(c);
                showSuccessMessage(feedback, "added");
            } else {
                showFailMessage(feedback, "add");
            }
        }

        // EFFECTS: returns true if the course is named like a UBC course
        //          examples: CPSC110, phys 131, wrds 150a, ASL100
        private boolean isUbcFormattedCourse(String courseName) {
            return courseName.matches("[a-zA-Z]{3,4} ?\\d{3}[a-zA-Z]?");
        }
    }

    /*
        Handles adding and editing sections
     */
    private class SectionEditListener implements ActionListener {
        private JTextField sectionNameField;
        private JLabel feedback;

        // EFFECTS: constructs an ActionListener with access to the given text field and label
        private SectionEditListener(JTextField name, JLabel feedback) {
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

        // MODIFIES: this
        // EFFECTS: updates selectedSection with sectionNameField if doing so would not create duplicate sections,
        //          and shows feedback
        private void updateSelectedSection() {
            String name = sectionNameField.getText();
            if (selectedSection != null) {
                if (selectedCourse.changeSectionName(selectedSection, name)) {
                    showSuccessMessage(feedback, "saved");
                } else {
                    showFailMessage(feedback, "save");
                }
            }
        }

        // MODIFIES: this
        // EFFECTS: adds a section to selectedCourse with sectionNameField
        //          if doing so would not create duplicate sections, and shows feedback
        private void addNewSection() {
            Section newSection = new Section(sectionNameField.getText(), selectedCourse);
            boolean success = selectedCourse.addSection(newSection);
            if (success) {
                sections.addElement(newSection);
                showSuccessMessage(feedback, "added");
            } else {
                showFailMessage(feedback, "add");
            }
        }
    }

    /*
        Handles adding timeslots
     */
    private class TimeslotEditListener implements ActionListener {
        private JLabel feedback;
        private JTextField termField;
        private JTextField startTimeField;
        private JTextField endTimeField;
        private JCheckBox[] dayCheckBoxes = new JCheckBox[7];

        // EFFECTS: creates an ActionListener with access to the given label
        // NOTE: for the listener to function, other setters must also be called
        private TimeslotEditListener(JLabel feedback) {
            this.feedback = feedback;
        }

        public void setTermField(JTextField termField) {
            this.termField = termField;
        }

        public void setStartTimeField(JTextField startTimeField) {
            this.startTimeField = startTimeField;
        }

        public void setEndTimeField(JTextField endTimeField) {
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
                    if (dayCheckBoxes[i].isSelected()) {
                        if (addNewTimeslot(i + 1)) {
                            successCount++;
                        }
                    }
                }
                showSuccessMessage(feedback, "added " + successCount + " timeslots.");
            } else {
                showSelectionErrorMessage(feedback, "section");
            }
        }

        // MODIFIES: timeslots, selectedSection
        // EFFECTS: tries to add a timeslot with the given day of week to selectedSection, returns true if successful
        //          and false if unsuccessful
        private boolean addNewTimeslot(int dayOfWeek) {
            int term;
            try {
                term = Integer.parseInt(termField.getText());
            } catch (NumberFormatException nfe) {
                return false;
            }
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

    /*
        Handles saving and loading course lists
     */
    private class SaveLoadListener implements ActionListener {
        private JsonReader reader;
        private JsonWriter writer;
        private JLabel feedback;
        private static final String FILE_NAME = "./data/courselist.json";

        // EFFECTS: constructs an ActionListener with access to the given label
        private SaveLoadListener(JLabel feedback) {
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

        // MODIFIES: this
        // EFFECTS: writes the current course list to file and shows feedback
        private void writeCourseList() {
            try {
                writer.open();
                writer.writeCourseList(courseList);
                writer.close();
                showSuccessMessage(feedback, "saved course list");
            } catch (FileNotFoundException fileNotFoundException) {
                showFailMessage(feedback, "save course list");
            }
        }

        // MODIFIES: this
        // EFFECTS: loads the current course list from file and shows feedback
        private void readCourseList() {
            try {
                courseList = reader.read();
                clearCoursesAndLoad();
                showSuccessMessage(feedback, "loaded course list");
            } catch (IOException ioException) {
                showFailMessage(feedback, "load course list");
            }
        }
    }

    /*
        Handles calculating and displaying schedules
     */
    private class CalculationListener implements ActionListener {
        private JTextField scheduleSizeField;
        private JTextArea displayedSchedule;
        private JLabel feedback;
        private List<Schedule> selection = new ArrayList<>();
        private int currentIndex;

        // EFFECTS: constructs an ActionListener with access to the given text field, text area, and label
        private CalculationListener(JTextField size, JTextArea display, JLabel feedback) {
            scheduleSizeField = size;
            displayedSchedule = display;
            this.feedback = feedback;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (e.getActionCommand()) {
                case "Calculate":
                    calculateAndDisplaySchedules();
                    break;
                case "Previous":
                    goToPrevious();
                    break;
                case "Next":
                    goToNext();
                    break;
            }
        }

        // MODIFIES: this
        // EFFECTS: moves the display to the previous schedule if it exists, otherwise does nothing
        private void goToPrevious() {
            if (currentIndex > 0) {
                currentIndex--;
            }
            setScheduleAreaText();
        }

        // MODIFIES: this
        // EFFECTS: moves the display to the next schedule if it exists, otherwise does nothing
        private void goToNext() {
            if (currentIndex < selection.size() - 1) {
                currentIndex++;
            }
            setScheduleAreaText();
        }

        // MODIFIES: this
        // EFFECTS: sets text in displayedSchedule to the schedule at currentIndex
        private void setScheduleAreaText() {
            String currentSchedule = selection.get(currentIndex).toString();
            String display = "Schedule #" + (currentIndex + 1) + "\n" + currentSchedule;
            displayedSchedule.setText(display);
        }

        // MODIFIES: this
        // EFFECTS: calculates all possible schedules with size specified in scheduleSizeField and shows feedback
        private void calculateAndDisplaySchedules() {
            int size = 0;
            try {
                size = Integer.parseInt(scheduleSizeField.getText());
            } catch (NumberFormatException nfe) {
                // do nothing
            }
            List<Schedule> schedules = null;
            try {
                schedules = courseList.allValidSchedules(size);
                fillSelection(10, schedules);
            } catch (ScheduleSizeException e) {
                showFailMessage(feedback, "calculate schedules");
            }
        }

        // MODIFIES: this
        // EFFECTS: clears previous schedules, and
        //          if the number of schedules < selectionSize, loads all schedules into selection
        //          otherwise, randomly selects selectionSize schedules and loads them into selection
        private void fillSelection(int selectionSize, List<Schedule> schedules) {
            selection.clear();

            showSuccessMessage(feedback, "calculated " + schedules.size() + " schedules");

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

    /*
        Class that plays 2 different sounds
     */
    private class SoundPlayer {
        Clip clip;
        AudioInputStream audioInputStream;
        private final File failSound = new File("./data/fail.wav");
        private final File successSound = new File("./data/success.wav");

        // EFFECTS: if success is true, constructs a sound player that plays a success sound
        //          otherwise constructs a sound player that plays a failure sound
        private SoundPlayer(boolean success) {
            try {
                if (success) {
                    audioInputStream = AudioSystem.getAudioInputStream(successSound);
                } else {
                    audioInputStream = AudioSystem.getAudioInputStream(failSound);
                }
                clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                        try {
                            audioInputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                e.printStackTrace();
            }
        }

        // EFFECTS
        private void play() {
            clip.start();
        }
    }
}
