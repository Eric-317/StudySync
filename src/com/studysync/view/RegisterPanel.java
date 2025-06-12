package com.studysync.view;

import com.studysync.controller.UserController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 使用者註冊面板類
 * 提供新用戶註冊功能介面
 * 
 * 主要功能：
 * - 用戶資料輸入表單（電子郵件、密碼、出生日期）
 * - 表單驗證和資料格式檢查
 * - 註冊成功後自動跳轉到登入頁面
 * - 提供返回登入頁面的功能
 * 
 * UI 組件：
 * - 電子郵件輸入欄位
 * - 密碼輸入欄位
 * - 出生日期選擇器
 * - 註冊按鈕和返回按鈕
 * 
 * @author StudySync Team
 * @version 1.0
 */
public class RegisterPanel extends JPanel {
    /** 用戶控制器，處理註冊相關的業務邏輯 */
    private final UserController userController = new UserController();
    
    /** 主視窗引用，用於頁面切換 */
    private final MainWindow mainWindow;

    /**
     * 建構註冊面板
     * 初始化UI組件並設置事件監聽器
     * 
     * @param mainWindow 主視窗引用，用於註冊完成後的頁面導航
     */
    public RegisterPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setLayout(new GridBagLayout());
        setBackground(new Color(245, 248, 255));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(30, 40, 30, 40));
        card.setMaximumSize(new Dimension(400, 450));

        JTextField emailField = new JTextField();
        setFixedSize(emailField, 300, 60);
        emailField.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailField.setBorder(BorderFactory.createTitledBorder("Email Address"));

        JPasswordField passwordField = new JPasswordField();
        setFixedSize(passwordField, 300, 60);
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));

        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner birthSpinner = new JSpinner(dateModel);
        birthSpinner.setAlignmentX(Component.CENTER_ALIGNMENT);
        birthSpinner.setBorder(BorderFactory.createTitledBorder("Birth Date"));
        setFixedSize(birthSpinner, 300, 60);        // 設置出生日期選擇器的日期格式
        JSpinner.DateEditor editor = new JSpinner.DateEditor(birthSpinner, "yyyy-MM-dd");
        JFormattedTextField textField = editor.getTextField();
        textField.setHorizontalAlignment(JTextField.CENTER);
        setFixedSize(textField, 300, 30);
        birthSpinner.setEditor(editor);

        // 建立註冊按鈕
        JButton registerBtn = new JButton("Sign up");
        registerBtn.setBackground(new Color(66, 133, 244));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFocusPainted(false);
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        setFixedSize(registerBtn, 300, 40);

        // 建立返回登入按鈕
        JButton backBtn = new JButton("Back to login");
        backBtn.setFocusPainted(false);
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        setFixedSize(backBtn, 300, 40);

        card.add(emailField);
        card.add(Box.createVerticalStrut(10));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(10));
        card.add(birthSpinner);
        card.add(Box.createVerticalStrut(20));
        card.add(registerBtn);
        card.add(Box.createVerticalStrut(10));
        card.add(backBtn);        add(card);

        // 設置註冊按鈕事件監聽器
        registerBtn.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            Date birth = (Date) birthSpinner.getValue();
            String birthStr = new SimpleDateFormat("yyyy-MM-dd").format(birth);
            
            if (userController.register(email, password, birthStr)) {
                JOptionPane.showMessageDialog(this, "註冊成功！請登入");
                mainWindow.showLoginPanel();
            } else {
                JOptionPane.showMessageDialog(this, "註冊失敗或帳號已存在");
            }
        });

        // 設置返回按鈕事件監聽器
        backBtn.addActionListener(e -> mainWindow.showLoginPanel());
    }

    /**
     * 設置組件固定尺寸
     * 統一設置組件的首選、最小和最大尺寸
     * 
     * @param comp 要設置尺寸的UI組件
     * @param width 寬度
     * @param height 高度
     */
    private void setFixedSize(JComponent comp, int width, int height) {
        Dimension d = new Dimension(width, height);
        comp.setPreferredSize(d);
        comp.setMinimumSize(d);
        comp.setMaximumSize(d);
    }
}
