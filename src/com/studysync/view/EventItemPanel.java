package com.studysync.view;

import com.studysync.model.CalendarEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;
import java.time.LocalDate;

public class EventItemPanel extends JPanel {
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    // 更新字體以匹配 TaskItemPanel 風格，或使其相似
    private static final Font TITLE_FONT = new Font("微軟正黑體", Font.BOLD, 16); // 類似任務標題
    private static final Font TIME_INFO_FONT = new Font("微軟正黑體", Font.PLAIN, 12); // 類似任務時間
    private static final Color TITLE_COLOR = new Color(30, 30, 30);
    private static final Color TIME_INFO_COLOR = new Color(120, 130, 150);

    public EventItemPanel(LocalDate date,
                          CalendarEvent event,
                          Consumer<CalendarEvent> onSave,
                          Runnable onDelete) {
        setLayout(new BorderLayout()); // 主面板使用 BorderLayout
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230,230,230)), // 底部邊框作為分隔線
                new EmptyBorder(8, 10, 8, 10) // 上下左右邊距
        ));

        // 內容面板，使用 BoxLayout 垂直排列標題和時間
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false); // 使其透明以顯示 EventItemPanel 的背景色        // 標題標籤 (使用行程描述)
        String displayTitle = event.getDescription();
        
        JLabel titleLabel = new JLabel(displayTitle);
        titleLabel.setFont(TITLE_FONT);
        // 根據事件類型使用不同的標題顏色
        if (event.isTask()) {
            titleLabel.setForeground(new Color(200, 100, 50)); // 橘色代表任務
        } else {
            titleLabel.setForeground(TITLE_COLOR); // 原來的顏色代表事件
        }
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // 在 BoxLayout 中左對齊// 時間和類型標籤
        String timeStr;
        if (event.getTime() != null) {
            timeStr = event.getTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        } else {
            timeStr = "All-day"; // 全天行程
        }
        
        // 添加事件類型標示
        String eventType = event.isTask() ? "Task" : "Event";
        String displayText = timeStr + " • " + eventType;
        
        JLabel timeInfoLabel = new JLabel(displayText);
        timeInfoLabel.setFont(TIME_INFO_FONT);
        // 根據事件類型使用不同顏色
        if (event.isTask()) {
            timeInfoLabel.setForeground(new Color(200, 100, 50)); // 橘色代表任務
        } else {
            timeInfoLabel.setForeground(TIME_INFO_COLOR); // 原來的顏色代表事件
        }
        timeInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // 在 BoxLayout 中左對齊

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(4)); // 標題和時間之間的小間距
        contentPanel.add(timeInfoLabel);

        add(contentPanel, BorderLayout.CENTER); // 將內容面板添加到主面板中央

        // 保留用於編輯/刪除的滑鼠監聽器
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                EventFormDialog dialog = new EventFormDialog(
                    (Frame) SwingUtilities.getWindowAncestor(EventItemPanel.this),
                    date,
                    event,
                    updated -> {
                        // 更新完畢後的回調
                        onSave.accept(updated);
                    },
                    onDelete // 刪除行程的回調
                );
                dialog.setVisible(true);
            }
        });
    }
}
