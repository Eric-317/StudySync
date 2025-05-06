package com.studysync.view;

import com.studysync.controller.TaskController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class TaskFormDialog extends JDialog {
    private final JTextField titleField = new JTextField();
    private final JComboBox<String> categoryBox = new JComboBox<>(new String[]{
            "Studying", "Homework", "Writing", "Meeting"
    });
    private final JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
    private final JButton submitButton = new JButton("Add Task");

    public TaskFormDialog(JFrame parent, TaskController controller, Runnable onTaskAdded) {
        super(parent, "Add New Task", true);
        setLayout(new BorderLayout());
        setSize(350, 300);
        setLocationRelativeTo(parent);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(20, 20, 20, 20));

        titleField.setBorder(BorderFactory.createTitledBorder("Title"));
        form.add(titleField);
        form.add(Box.createVerticalStrut(10));

        categoryBox.setBorder(BorderFactory.createTitledBorder("Category"));
        form.add(categoryBox);
        form.add(Box.createVerticalStrut(10));

        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd HH:mm"));
        ((JSpinner.DateEditor) dateSpinner.getEditor()).getTextField()
                .setBorder(BorderFactory.createTitledBorder("Due Time"));
        form.add(dateSpinner);

        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(Box.createVerticalStrut(20));
        form.add(submitButton);

        add(form, BorderLayout.CENTER);

        submitButton.addActionListener(e -> {
            String title = titleField.getText();
            String category = categoryBox.getSelectedItem().toString();
            Date date = (Date) dateSpinner.getValue();
            LocalDateTime dueTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());

            controller.addTask(title, category, dueTime);
            onTaskAdded.run();
            dispose();
        });
    }
}
