package com.studysync.view;

import com.studysync.model.Task;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;

/**
 * 任務項目面板類
 * 顯示單個任務的詳細資訊和操作介面
 * 
 * 主要功能：
 * - 顯示任務基本資訊（標題、截止時間、分類）
 * - 任務完成狀態顯示
 * - 分類標籤顏色區分
 * - 點擊編輯任務功能
 * - 完成/未完成狀態視覺化
 * 
 * UI 組件：
 * - 任務完成狀態核取方塊
 * - 任務標題和截止時間標籤
 * - 分類標籤（帶顏色區分）
 * - 滑鼠點擊事件處理
 * 
 * 視覺特效：
 * - 不同分類使用不同顏色標籤
 * - 已完成任務使用刪除線效果
 * - 懸停效果和點擊反饋
 * 
 * @author StudySync Team
 * @version 1.0
 */
public class TaskItemPanel extends JPanel {
    /** 學習分類的顏色 */
    private final Color studyingColor = new Color(102, 153, 255);
    
    /** 作業分類的顏色 */
    private final Color homeworkColor = new Color(102, 204, 153);
    
    /** 寫作分類的顏色 */
    private final Color writingColor = new Color(255, 204, 102);
    
    /** 會議分類的顏色 */
    private final Color meetingColor = new Color(178, 153, 255);
    
    /** 父級任務面板引用，用於任務編輯後的刷新 */
    private final TaskPanel taskPanel;

    /**
     * 建構任務項目面板
     * 
     * @param task 要顯示的任務物件
     * @param taskPanel 父級任務面板引用
     */
    public TaskItemPanel(Task task, TaskPanel taskPanel) {
        this.taskPanel = taskPanel; // Store TaskPanel instance
        try { // Added try for the entire constructor
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 215, 230)),
                    new EmptyBorder(12, 0, 12, 0)
            ));
            setPreferredSize(new Dimension(720, 80));
            setMaximumSize(new Dimension(720, 80));

            JCheckBox checkBox = new JCheckBox();
            checkBox.setSelected(task.isCompleted());
            checkBox.setEnabled(false);
            checkBox.setBackground(Color.WHITE);
            checkBox.setPreferredSize(new Dimension(28, 28));

            Font titleFont = new Font("微軟正黑體", Font.BOLD, 18);
            Font normalFont = new Font("微軟正黑體", Font.PLAIN, 14);

            JLabel title = new JLabel(task.getTitle());
            title.setFont(titleFont);
            title.setForeground(new Color(30, 30, 30));
            if (task.isCompleted()) {
                title.setText("<html><strike>" + task.getTitle() + "</strike></html>");
                title.setForeground(new Color(180, 180, 180));
            }

            // Handle potential NullPointerException for getDueTime()
            String dueTimeText = "無截止時間";
            if (task.getDueTime() != null) {
                try {
                    dueTimeText = task.getDueTime().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                } catch (Exception e) {
                    System.err.println("格式化任務截止時間時發生錯誤: " + e.getMessage());
                    e.printStackTrace();
                    // dueTimeText remains "無截止時間"
                }
            }
            JLabel timeLabel = new JLabel(dueTimeText);
            timeLabel.setFont(normalFont);
            timeLabel.setForeground(new Color(120, 130, 150));
            timeLabel.setBorder(new EmptyBorder(0, 0, 0, 0));

            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setBackground(Color.WHITE);
            infoPanel.add(title);
            infoPanel.add(Box.createVerticalStrut(4));
            infoPanel.add(timeLabel);
            infoPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

            JLabel tag = new JLabel(task.getCategory());
            tag.setOpaque(true);
            tag.setFont(new Font("微軟正黑體", Font.BOLD, 16));
            tag.setForeground(Color.WHITE);
            tag.setBorder(new EmptyBorder(6, 32, 6, 32));
            tag.setBackground(getTagColor(task.getCategory()));
            tag.setHorizontalAlignment(SwingConstants.CENTER);
            tag.setPreferredSize(new Dimension(180, 36));

            JPanel leftPanel = new JPanel();
            leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));
            leftPanel.setBackground(Color.WHITE);
            leftPanel.add(checkBox);
            leftPanel.add(Box.createHorizontalStrut(20));
            leftPanel.add(infoPanel);
            leftPanel.add(Box.createHorizontalGlue());

            add(leftPanel, BorderLayout.CENTER);
            add(tag, BorderLayout.EAST);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try { // Added try for mouseClicked event
                        // TaskPanel taskPanel = (TaskPanel) SwingUtilities.getAncestorOfClass(TaskPanel.class, TaskItemPanel.this);
                        // The above line is no longer needed as we have a direct reference
                        if (TaskItemPanel.this.taskPanel != null) {
                            TaskFormDialog dialog = new TaskFormDialog(
                                (JFrame) SwingUtilities.getWindowAncestor(TaskItemPanel.this.taskPanel),
                                TaskItemPanel.this.taskPanel.controller,
                                () -> {
                                    try { // Added try for the callback
                                        // Use TaskPanel's current filter and category
                                        TaskItemPanel.this.taskPanel.refreshTasks(
                                            TaskItemPanel.this.taskPanel.getCurrentFilter(),
                                            TaskItemPanel.this.taskPanel.getCurrentCategory()
                                        );
                                    } catch (Exception ex) {
                                        JOptionPane.showMessageDialog(TaskItemPanel.this, "刷新任務時發生錯誤 (回呼): " + ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
                                        ex.printStackTrace();
                                    }
                                },
                                task
                            );
                            dialog.setVisible(true);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(TaskItemPanel.this, "處理任務項目點擊時發生錯誤: " + ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }
            });
        } catch (Exception ex) { // Added catch for the entire constructor
            ex.printStackTrace();
            // Fallback UI in case of critical initialization error
            setLayout(new BorderLayout()); // Ensure layout manager for fallback
            removeAll();
            add(new JLabel("任務項目載入失敗: " + ex.getMessage()), BorderLayout.CENTER);
            setBackground(Color.PINK); // Indicate error visually
            System.err.println("初始化 TaskItemPanel 時發生嚴重錯誤: " + ex.getMessage());
        }
    }

    private Color getTagColor(String cat) {
        try { // Added try for getTagColor
            if (cat == null) { // Handle null category gracefully
                return new Color(120, 120, 120); // Default color for null category
            }
            switch (cat.toLowerCase()) {
                case "studying": return new Color(66, 135, 245); // 深藍
                case "reading": return new Color(46, 204, 113); // 深綠
                case "homework": return new Color(155, 89, 182); // 紫
                case "writing": return new Color(241, 196, 15); // 黃
                case "meeting": return new Color(230, 126, 34); // 橘
                default: return new Color(120, 120, 120);
            }
        } catch (Exception ex) {
            System.err.println("獲取標籤顏色時發生錯誤: " + ex.getMessage());
            ex.printStackTrace();
            return new Color(120, 120, 120); // Fallback color
        }
    }
}
