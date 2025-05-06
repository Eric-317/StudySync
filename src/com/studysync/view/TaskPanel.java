package com.studysync.view;

import com.studysync.controller.TaskController;
import com.studysync.model.Task;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskPanel extends JPanel {
    private final TaskController controller = new TaskController();
    private final JPanel taskListPanel = new JPanel();
    private final JButton allBtn = new JButton("All");
    private final JButton todayBtn = new JButton("Today");
    private final JButton upcomingBtn = new JButton("Upcoming");
    private final JButton completedBtn = new JButton("Completed");

    public TaskPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 248, 255));

        JLabel title = new JLabel("Tasks");
        title.setFont(new Font("Dialog", Font.BOLD, 24));
        title.setBorder(new EmptyBorder(10, 20, 10, 0));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JButton[] buttons = {allBtn, todayBtn, upcomingBtn, completedBtn};
        for (JButton b : buttons) {
            b.setFocusPainted(false);
            b.setBackground(new Color(240, 240, 240));
            b.setFont(new Font("Dialog", Font.PLAIN, 13));
            b.setPreferredSize(new Dimension(100, 30));
            filterPanel.add(b);
        }

        allBtn.addActionListener(e -> refreshTasks("all"));
        todayBtn.addActionListener(e -> refreshTasks("today"));
        upcomingBtn.addActionListener(e -> refreshTasks("upcoming"));
        completedBtn.addActionListener(e -> refreshTasks("completed"));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.add(title, BorderLayout.NORTH);
        header.add(filterPanel, BorderLayout.SOUTH);

        taskListPanel.setLayout(new BoxLayout(taskListPanel, BoxLayout.Y_AXIS));
        taskListPanel.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(taskListPanel);
        scrollPane.setBorder(null);

        JButton addBtn = new JButton("+ Add Task");
        addBtn.setBackground(new Color(66, 135, 245));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.setPreferredSize(new Dimension(120, 36));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        bottomPanel.add(addBtn);

        add(header, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> {
            TaskFormDialog dialog = new TaskFormDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    controller,
                    () -> refreshTasks("all")
            );
            dialog.setVisible(true);
        });

        refreshTasks("all");
    }

    private void refreshTasks(String filter) {
        taskListPanel.removeAll();
        List<Task> allTasks = controller.getAllTasks();
        List<Task> tasksToShow = switch (filter) {
            case "today" -> allTasks.stream().filter(Task::isToday).collect(Collectors.toList());
            case "upcoming" -> allTasks.stream().filter(t -> !t.isCompleted() && t.isUpcoming()).collect(Collectors.toList());
            case "completed" -> allTasks.stream().filter(Task::isCompleted).collect(Collectors.toList());
            default -> allTasks;
        };
        for (Task t : tasksToShow) {
            taskListPanel.add(Box.createVerticalStrut(8));
            taskListPanel.add(new TaskItemPanel(t));
        }
        taskListPanel.revalidate();
        taskListPanel.repaint();
    }
}
