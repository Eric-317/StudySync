package com.studysync.view;

import com.studysync.controller.UserController;
import com.studysync.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * 登入面板類
 * 提供使用者登入功能的 UI 介面
 * 
 * 主要功能：
 * - 使用者名稱和密碼輸入
 * - 登入驗證處理
 * - 登入成功後導航到主界面
 * - 註冊頁面導航
 * - 錯誤訊息顯示
 * 
 * UI 組件：
 * - Logo 和應用程式標題
 * - 使用者名稱輸入欄位
 * - 密碼輸入欄位
 * - 登入按鈕
 * - 註冊連結
 * 
 * 設計特點：
 * - 卡片式設計風格
 * - 居中布局
 * - 統一的色彩主題
 * 
 * @author StudySync Team
 * @version 1.0
 */
public class LoginPanel extends JPanel {
    /** 使用者控制器實例 */
    private final UserController userController = new UserController();
    
    /** 主視窗引用，用於頁面導航 */
    private final MainWindow mainWindow;    /**
     * 建構登入面板
     * 初始化 UI 組件和事件監聽器
     * 
     * @param mainWindow 主視窗引用，用於登入成功後的頁面導航
     */
    public LoginPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setLayout(new GridBagLayout());
        setBackground(new Color(245, 248, 255));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(30, 40, 30, 40));
        card.setMaximumSize(new Dimension(400, 450));

        // logo + text panel
        JPanel logoTextPanel = new JPanel();
        logoTextPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        logoTextPanel.setBackground(Color.WHITE);

        ImageIcon rawIcon = new ImageIcon(getClass().getResource("/com/studysync/assets/photo/logo.png"));
        Image scaled = rawIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaled));

        JLabel titleLabel = new JLabel("StudySync");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        titleLabel.setForeground(new Color(30, 30, 30));

        logoTextPanel.add(logoLabel);
        logoTextPanel.add(titleLabel);

        JTextField emailField = new JTextField();
        setFixedSize(emailField, 300, 60);
        emailField.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailField.setBorder(BorderFactory.createTitledBorder("Email Address"));

        JPasswordField passwordField = new JPasswordField();
        setFixedSize(passwordField, 300, 60);
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));

        JLabel forgot = new JLabel("Forgot password?");
        forgot.setForeground(new Color(50, 90, 255));
        forgot.setFont(new Font("Dialog", Font.PLAIN, 12));
        forgot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgot.setAlignmentX(Component.CENTER_ALIGNMENT);

        forgot.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JOptionPane.showMessageDialog(LoginPanel.this,
                        "請聯絡管理員或重新註冊帳號。",
                        "忘記密碼",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JButton loginBtn = new JButton("Log in");
        loginBtn.setBackground(new Color(66, 135, 245));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        setFixedSize(loginBtn, 300, 40);

        JButton signupBtn = new JButton("Sign up");
        signupBtn.setFocusPainted(false);
        signupBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        setFixedSize(signupBtn, 300, 40);

        card.add(logoTextPanel);
        card.add(Box.createVerticalStrut(20));
        card.add(emailField);
        card.add(Box.createVerticalStrut(10));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(5));
        card.add(forgot);
        card.add(Box.createVerticalStrut(20));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(10));
        card.add(signupBtn);

        add(card);

        loginBtn.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            User user = userController.login(email, password);
            if (user != null) {
                mainWindow.showDashboard(user);
            } else {
                JOptionPane.showMessageDialog(this, "登入失敗，請檢查帳密。");
            }
        });

        signupBtn.addActionListener(e -> mainWindow.showRegisterPanel());
    }

    private void setFixedSize(JComponent comp, int width, int height) {
        Dimension d = new Dimension(width, height);
        comp.setPreferredSize(d);
        comp.setMinimumSize(d);
        comp.setMaximumSize(d);
    }
}
