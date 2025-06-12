package com.studysync.view;

import com.studysync.controller.TaskController;
import com.studysync.model.Task;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * 任務詳細資訊對話框類
 * 提供查看和編輯任務詳細資訊的介面
 * 
 * 主要功能：
 * - 顯示任務詳細資訊
 * - 編輯任務標題
 * - 修改任務完成狀態
 * - 刪除任務功能
 * - 任務狀態即時更新
 * 
 * UI 組件：
 * - 任務標題編輯欄位
 * - 完成狀態核取方塊
 * - 儲存和刪除按鈕
 * 
 * 注意：此類功能與 TaskFormDialog 有重疊，
 * 在實際應用中建議統一使用 TaskFormDialog
 * 
 * @author StudySync Team
 * @version 1.0
 */
public class TaskDetailDialog extends JDialog {
    /** 任務控制器，處理任務相關業務邏輯 */
    private final TaskController controller = new TaskController();
    
    /** 任務標題輸入欄位 */
    private final JTextField titleField = new JTextField();
    
    /** 完成狀態核取方塊 */
    private final JCheckBox completedCheckBox = new JCheckBox("Completed");
    
    /** 儲存按鈕 */
    private final JButton saveButton = new JButton("Save");
    
    /** 刪除按鈕 */
    private final JButton deleteButton = new JButton("Delete");

    /**
     * 建構任務詳細資訊對話框
     * 
     * @param taskPanel 父級任務面板，用於任務操作後的刷新
     * @param task 要顯示的任務物件
     */
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