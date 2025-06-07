package com.studysync.view;

import com.studysync.model.User;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

public class DashboardPanel extends JPanel {
    private final User user;

    public DashboardPanel(User user) {
        this.user = user;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 248, 255));

        // 創建TaskPanel並傳入當前用戶
        TaskPanel taskPanel = new TaskPanel();
        taskPanel.setCurrentUser(user);
        add(taskPanel, BorderLayout.CENTER);
    }
}
