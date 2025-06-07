package com.studysync.view;

import com.studysync.controller.TaskController;
import com.studysync.model.Task;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TaskDetailDialog extends JDialog {
    private final TaskController controller = new TaskController();
    private final JTextField titleField = new JTextField();
    private final JCheckBox completedCheckBox = new JCheckBox("Completed");
    private final JButton saveButton = new JButton("Save");
    private final JButton deleteButton = new JButton("Delete");

    public TaskDetailDialog(TaskPanel taskPanel, Task task) {
        super((JFrame) SwingUtilities.getWindowAncestor(taskPanel), "Task Details", true);
        setLayout(new BorderLayout());
        setSize(400, 300);
        setLocationRelativeTo(taskPanel);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        titleField.setText(task.getTitle());
        titleField.setBorder(BorderFactory.createTitledBorder("Title"));
        formPanel.add(titleField);
        formPanel.add(Box.createVerticalStrut(10));

        completedCheckBox.setSelected(task.isCompleted());
        formPanel.add(completedCheckBox);
        formPanel.add(Box.createVerticalStrut(20));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> {
            task.setTitle(titleField.getText());
            boolean wasCompleted = task.isCompleted();
            boolean nowCompleted = completedCheckBox.isSelected();
            task.setCompleted(nowCompleted);
            if (!wasCompleted && nowCompleted) {
                controller.markTaskCompleted(task.getUid());
            }
            // 這裡可加上更新標題等其他欄位的資料庫邏輯（如有需要）
            // 目前只支援完成狀態變更
            taskPanel.refreshTasks(nowCompleted ? "completed" : "all");
            dispose();
        });

        deleteButton.addActionListener(e -> {
            controller.deleteTask(task.getUid());
            dispose(); // 僅關閉對話框
            taskPanel.refreshTasks("all"); // 刷新任務列表
        });
    }
}