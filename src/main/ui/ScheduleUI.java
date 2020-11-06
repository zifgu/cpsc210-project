package ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.time.DayOfWeek;

import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

public class ScheduleUI {
    public static void main(String[] args) {
        ScheduleUI ui = new ScheduleUI();
        JPanel listPanel = ui.createListPanel();
        JPanel infoPanel = ui.createInfoPanel();
        JPanel saveLoadPanel = ui.createSaveLoadPanel();

        JPanel editPanelTab = new JPanel();
        editPanelTab.setLayout(new BoxLayout(editPanelTab, BoxLayout.PAGE_AXIS));
        editPanelTab.setBorder(BorderFactory.createTitledBorder("Main frame"));
        editPanelTab.add(listPanel);
        editPanelTab.add(infoPanel);
        editPanelTab.add(saveLoadPanel);

        JPanel schedulePanelTab = ui.createSchedulePanel();

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Course list", editPanelTab);
        tabbedPane.addTab("Schedule", schedulePanelTab);

        JFrame frame = new JFrame("My frame");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(900, 700));
        frame.setResizable(false);
        frame.setContentPane(tabbedPane);
        frame.pack();
        frame.setVisible(true);
    }

    private JPanel createListPanel() {
        JScrollPane scrollPane1 = createScrollingList(new String[]{"A", "B", "C"});
        JScrollPane scrollPane2 = createScrollingList(new String[]{"D", "E"});
        JScrollPane scrollPane3 = createScrollingList(new String[]{"F", "G", "H", "I"});

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.LINE_AXIS));
        listPanel.add(scrollPane1);
        listPanel.add(scrollPane2);
        listPanel.add(scrollPane3);
        return listPanel;
    }

    private JScrollPane createScrollingList(String[] elements) {
        DefaultListModel listModel = new DefaultListModel();
        for (String s : elements) {
            listModel.addElement(s);
        }
        JList list = new JList(listModel);

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(300, 300));
        TitledBorder border = BorderFactory.createTitledBorder("List");
        scrollPane.setBorder(border);

        return scrollPane;
    }

    private JPanel createInfoPanel() {
        JPanel coursePanel = createCourseButtonPanel();
        JPanel sectionPanel = createSectionButtonPanel();
        JPanel timeslotPanel = createTimeslotButtonPanel();

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.LINE_AXIS));
        infoPanel.add(coursePanel);
        infoPanel.add(sectionPanel);
        infoPanel.add(timeslotPanel);
        return infoPanel;
    }

    private JPanel createCourseButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Course panel"));
        panel.setPreferredSize(new Dimension(300, 300));

        JTextField textField = new JTextField(10);
        textField.setMaximumSize(new Dimension(200, 30));
        JLabel textFieldLabel = new JLabel("Course name: ");
        textFieldLabel.setLabelFor(textField);
        JCheckBox checkBox = new JCheckBox("Is required?");
        panel.add(textFieldLabel);
        panel.add(textField);
        panel.add(checkBox);
        panel.add(Box.createVerticalGlue());
        panel.add(createButtonPanel());
        return panel;
    }

    private JPanel createSectionButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Section panel"));
        panel.setPreferredSize(new Dimension(300, 300));
        JTextField textField = new JTextField(10);
        textField.setMaximumSize(new Dimension(200, 30));
        JLabel textFieldLabel = new JLabel("Section name: ");
        textFieldLabel.setLabelFor(textField);
        panel.add(textFieldLabel);
        panel.add(textField);
        panel.add(Box.createVerticalGlue());
        panel.add(createButtonPanel());
        return panel;
    }

    private JPanel createTimeslotButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Timeslot panel"));
        panel.setPreferredSize(new Dimension(300, 300));
        panel.add(createTimeslotTextFields());
        panel.add(createTimeslotDaysOfWeek());
        panel.add(Box.createVerticalGlue());
        panel.add(createButtonPanel());
        return panel;
    }

    private JPanel createTimeslotDaysOfWeek() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));
        for (int i = 1; i <= 7; i++) {
            JCheckBox dayCheckBox = new JCheckBox(DayOfWeek.of(i).toString());
            panel.add(dayCheckBox);
        }
        return panel;
    }

    private JPanel createTimeslotTextFields() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        JTextField term = new JTextField(5);
        JLabel termFieldLabel = new JLabel("Term: ");
        panel.add(termFieldLabel);
        panel.add(term);

        JFormattedTextField startTime = new JFormattedTextField();
        JLabel startTimeFieldLabel = new JLabel("Start time: ");
        panel.add(startTimeFieldLabel);
        panel.add(startTime);

        JFormattedTextField endTime = new JFormattedTextField();
        JLabel endTimeFieldLabel = new JLabel("End time: ");
        panel.add(endTimeFieldLabel);
        panel.add(endTime);

        return panel;
    }

    private JPanel createButtonPanel() {
        JButton save = new JButton("Save");
        JButton add = new JButton("Add");
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEtchedBorder());
        panel.setMaximumSize(new Dimension(300, 100));
        panel.add(save);
        panel.add(add);
        return panel;
    }

    private JPanel createSaveLoadPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Save/load panel"));
        JButton save = new JButton("Save");
        JButton load = new JButton("Load");
        panel.add(save);
        panel.add(load);
        return panel;
    }

    private JPanel createSchedulePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Schedule tab"));

        JLabel info = new JLabel("Your course list contains 3 courses.");
        panel.add(info);

        JFormattedTextField scheduleSizeField = new JFormattedTextField();
        scheduleSizeField.setMaximumSize(new Dimension(100, 30));
        JLabel scheduleSizeFieldLabel = new JLabel("Enter the number of courses to calculate:");
        panel.add(scheduleSizeFieldLabel);
        panel.add(scheduleSizeField);

        JButton calculateButton = new JButton("Calculate");
        panel.add(calculateButton);

        return panel;
    }
}
