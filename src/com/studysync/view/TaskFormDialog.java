package com.studysync.view;

import com.studysync.controller.TaskController;
import com.studysync.model.Task;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * 任務表單對話框類
 * 提供新增和編輯任務的介面
 * 
 * 主要功能：
 * - 新增新的任務
 * - 編輯現有任務的詳細資訊
 * - 設置任務標題、分類、截止時間和完成狀態
 * - 刪除現有任務
 * - 動態載入分類列表
 * - 表單驗證和資料處理
 * 
 * UI 組件：
 * - 任務標題輸入欄位
 * - 分類下拉選單
 * - 截止時間選擇器
 * - 完成狀態核取方塊
 * - 儲存和刪除按鈕
 * 
 * @author StudySync Team
 * @version 1.0
 */
public class TaskFormDialog extends JDialog {
    /** 任務標題輸入欄位 */
    private final JTextField titleField = new JTextField();
    
    /** 分類下拉選單 */
    private final JComboBox<String> categoryBox = new JComboBox<>();
    
    /** 截止時間選擇器 */
    private final JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
    
    /** 完成狀態核取方塊 */
    private final JCheckBox completedCheckBox = new JCheckBox("Completed");
    
    /** 提交按鈕 */
    private final JButton submitButton = new JButton("Save");
    
    /** 刪除按鈕 */
    private final JButton deleteButton = new JButton("Delete");
    
    /** 正在編輯的任務（null表示新增任務） */
    private final Task editingTask;

    /**
     * 建構新任務對話框
     * 
     * @param parent 父視窗
     * @param controller 任務控制器
     * @param onTaskAdded 任務操作完成後的回調函數
     */
    public TaskFormDialog(JFrame parent, TaskController controller, Runnable onTaskAdded) {
        this(parent, controller, onTaskAdded, null);
    }
    
    /**
     * 建構任務表單對話框
     * 
     * @param parent 父視窗
     * @param controller 任務控制器
     * @param onTaskAdded 任務操作完成後的回調函數
     * @param task 要編輯的任務（null表示新增任務）
     */
    public TaskFormDialog(JFrame parent, TaskController controller, Runnable onTaskAdded, Task task) {super(parent, task == null ? "Add New Task" : "Edit Task", true);
        this.editingTask = task;
        setLayout(new BorderLayout());
        setSize(400, 350); // 增加寬度從350到400
        setLocationRelativeTo(parent);
        
        // 清空類別下拉選單
        categoryBox.removeAllItems();
        
        // 從 CategoryService 獲取類別列表
        try {
            List<String> categories = controller.getCategoryService().getCategoriesWithoutAll();
            for (String category : categories) {
                categoryBox.addItem(category);
            }
        } catch (Exception e) {
            System.err.println("加載類別列表失敗: " + e.getMessage());
            e.printStackTrace();
            // 加載預設類別
            String[] defaultCategories = {"Studying", "Homework", "Writing", "Meeting", "Reading"};
            for (String category : defaultCategories) {
                categoryBox.addItem(category);
            }
        }

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(20, 20, 20, 20));        titleField.setBorder(BorderFactory.createTitledBorder("Title"));
        titleField.setMaximumSize(new Dimension(Integer.MAX_VALUE, titleField.getPreferredSize().height));
        form.add(titleField);
        form.add(Box.createVerticalStrut(10));
        
        categoryBox.setBorder(BorderFactory.createTitledBorder("Category"));
        categoryBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, categoryBox.getPreferredSize().height));
        form.add(categoryBox);
        form.add(Box.createVerticalStrut(10));        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd HH:mm"));
        ((JSpinner.DateEditor) dateSpinner.getEditor()).getTextField()
                .setBorder(BorderFactory.createTitledBorder("Due Time"));
        dateSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, dateSpinner.getPreferredSize().height));
        form.add(dateSpinner);
        form.add(Box.createVerticalStrut(10));

        completedCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(completedCheckBox);
        form.add(Box.createVerticalStrut(20));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(submitButton);
        buttonPanel.add(deleteButton);

        add(form, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        if (task != null) {
            titleField.setText(task.getTitle());
            categoryBox.setSelectedItem(task.getCategory());
            dateSpinner.setValue(java.util.Date.from(task.getDueTime().atZone(java.time.ZoneId.systemDefault()).toInstant()));
            completedCheckBox.setSelected(task.isCompleted());
        }        submitButton.addActionListener(e -> {
            String title = titleField.getText();
            String category = categoryBox.getSelectedItem().toString();
            java.util.Date date = (java.util.Date) dateSpinner.getValue();
            java.time.LocalDateTime dueTime = java.time.LocalDateTime.ofInstant(date.toInstant(), java.time.ZoneId.systemDefault());
            boolean completed = completedCheckBox.isSelected();

            // 保存操作前輸出日誌
            System.out.println("正在保存任務: " + title);
            
            if (editingTask == null) {
                // 獲取新建立的任務
                Task newTask = controller.addTask(title, category, dueTime);
                if (newTask != null) {
                    System.out.println("任務建立成功: " + newTask.getTitle() + ", ID: " + newTask.getUid() + ", 用戶ID: " + newTask.getUserId());
                    onTaskAdded.run(); // 直接執行回調
                } else {
                    System.err.println("任務創建失敗，返回了null");
                    JOptionPane.showMessageDialog(this, 
                        "無法創建任務。請確保您已登入，且所有欄位都已正確填寫。", 
                        "創建失敗", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                editingTask.setTitle(title);
                editingTask.setCategory(category);
                editingTask.setDueTime(dueTime);
                editingTask.setCompleted(completed);
                controller.updateTask(editingTask);
                System.out.println("任務更新成功: " + editingTask.getTitle() + ", ID: " + editingTask.getUid());
                onTaskAdded.run(); // 直接執行回調
            }
            
            // 關閉對話框前先等待一個 UI 更新週期
            dispose();
        });

        deleteButton.addActionListener(e -> {
            if (editingTask != null) {
                controller.deleteTask(editingTask.getUid());
            }
            dispose();
            onTaskAdded.run();
        });

        if (editingTask == null) {
            deleteButton.setVisible(false);
        }
    }
}
