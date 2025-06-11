package com.studysync.view;

import com.studysync.model.User;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

/**
 * 儀表板面板類
 * 作為使用者登入後的主要工作區域
 * 
 * 主要功能：
 * - 整合任務管理面板
 * - 傳遞使用者資訊給子組件
 * - 提供統一的布局和樣式
 * 
 * 設計模式：
 * - 使用組合模式整合 TaskPanel
 * - 使用依賴注入傳遞使用者物件
 * 
 * @author StudySync Team
 * @version 1.0
 */
public class DashboardPanel extends JPanel {
    /** 當前使用者物件 */
    private final User user;

    /**
     * 建構儀表板面板
     * 
     * @param user 當前登入的使用者
     */
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
