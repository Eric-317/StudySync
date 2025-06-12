package com.studysync.view;

import com.studysync.controller.UserController;
import com.studysync.model.User;
import com.studysync.util.DBUtil;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.border.EmptyBorder;

/**
 * 設定面板類
 * 提供用戶帳戶設定和個人資料管理功能
 * 
 * 主要功能：
 * - 顯示用戶帳戶資訊（電子郵件、用戶ID、出生日期）
 * - 變更密碼功能
 * - 編輯出生日期功能
 * - 登出功能
 * - 用戶資訊即時更新
 * 
 * UI 組件：
 * - 帳戶資訊顯示區域
 * - 功能按鈕組（變更密碼、編輯出生日期、登出）
 * - 各種對話框（密碼變更、出生日期編輯）
 * 
 * @author StudySync Team
 * @version 1.0
 */
public class SettingsPanel extends JPanel {
    /** 當前登入的用戶 */
    private User currentUser;
    
    /** 主要內容面板 */
    private JPanel contentPanel;
    
    /** 電子郵件顯示標籤 */
    private JLabel emailLabel;
    
    /** 用戶ID顯示標籤 */
    private JLabel uidLabel;
    
    /** 出生日期顯示標籤 */
    private JLabel birthDateLabel;
    
    /** 主視窗引用，用於登出後的頁面切換 */
    private MainWindow mainWindow;
    
    /** 用戶控制器，處理用戶相關業務邏輯 */
    private final UserController userController = new UserController();

    /**
     * 建構設定面板
     * 初始化UI組件
     */
    public SettingsPanel() {
        initializeUI();
    }

    /**
     * 初始化使用者介面
     * 設置面板布局和各個UI組件
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 248, 255));
          // 創建頂部標題面板
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(new Color(235, 242, 250));
        titlePanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("設定");
        titleLabel.setFont(new Font("微軟正黑體", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        
        add(titlePanel, BorderLayout.NORTH);
        
        // 創建主要內容面板
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(245, 248, 255));
        contentPanel.setBorder(new EmptyBorder(30, 50, 30, 50));
        
        // 用戶帳戶資訊面板
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new GridLayout(3, 2, 10, 20));
        userInfoPanel.setBackground(new Color(245, 248, 255));
        userInfoPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 230), 1),
                "帳戶資訊",
                1,
                0,
                new Font("微軟正黑體", Font.BOLD, 16)
        ));
        
        // 創建標籤
        JLabel emailTitleLabel = new JLabel("電子郵件:");
        emailTitleLabel.setFont(new Font("微軟正黑體", Font.BOLD, 14));
        emailLabel = new JLabel("尚未登入");
        emailLabel.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        
        JLabel uidTitleLabel = new JLabel("帳戶 ID:");
        uidTitleLabel.setFont(new Font("微軟正黑體", Font.BOLD, 14));
        uidLabel = new JLabel("尚未登入");
        uidLabel.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        
        JLabel birthDateTitleLabel = new JLabel("出生日期:");
        birthDateTitleLabel.setFont(new Font("微軟正黑體", Font.BOLD, 14));
        birthDateLabel = new JLabel("尚未登入");
        birthDateLabel.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        
        // 添加到用戶資訊面板
        userInfoPanel.add(emailTitleLabel);
        userInfoPanel.add(emailLabel);
        userInfoPanel.add(uidTitleLabel);
        userInfoPanel.add(uidLabel);
        userInfoPanel.add(birthDateTitleLabel);
        userInfoPanel.add(birthDateLabel);
          contentPanel.add(userInfoPanel);
        contentPanel.add(Box.createVerticalStrut(20)); // 間隔
        
        // 系統資訊面板
        JPanel systemInfoPanel = new JPanel();
        systemInfoPanel.setLayout(new GridLayout(1, 2, 10, 20));
        systemInfoPanel.setBackground(new Color(245, 248, 255));
        systemInfoPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 230), 1),
                "系統資訊",
                1,
                0,
                new Font("微軟正黑體", Font.BOLD, 16)
        ));
        
        JLabel databaseTypeLabel = new JLabel("資料庫類型:");
        databaseTypeLabel.setFont(new Font("微軟正黑體", Font.BOLD, 14));
        JLabel databaseValueLabel = new JLabel(DBUtil.getDatabaseType());
        databaseValueLabel.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        
        systemInfoPanel.add(databaseTypeLabel);
        systemInfoPanel.add(databaseValueLabel);
        
        contentPanel.add(systemInfoPanel);
        contentPanel.add(Box.createVerticalStrut(20)); // 間隔
        
        // 添加按鈕面板
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        buttonPanel.setBackground(new Color(245, 248, 255));
        
        // 變更密碼按鈕
        JButton changePasswordButton = new JButton("變更密碼");
        styleButton(changePasswordButton);
        changePasswordButton.addActionListener(e -> showChangePasswordDialog());
        
        // 編輯出生日期按鈕
        JButton editBirthDateButton = new JButton("編輯出生日期");
        styleButton(editBirthDateButton);
        editBirthDateButton.addActionListener(e -> showEditBirthDateDialog());
        
        // 登出按鈕
        JButton logoutButton = new JButton("登出");
        styleButton(logoutButton);
        logoutButton.setBackground(new Color(255, 210, 210)); // 登出按鈕使用不同顏色
        logoutButton.addActionListener(e -> logout());
        
        buttonPanel.add(changePasswordButton);
        buttonPanel.add(editBirthDateButton);
        buttonPanel.add(logoutButton);
        
        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonWrapper.setBackground(new Color(245, 248, 255));
        buttonWrapper.add(buttonPanel);
        
        contentPanel.add(buttonWrapper);
        
        // 添加彈性空間
        contentPanel.add(Box.createVerticalGlue());
        
        // 將主內容面板添加到中央
        add(new JScrollPane(contentPanel), BorderLayout.CENTER);
    }
    
    private void styleButton(JButton button) {
        button.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        button.setBackground(new Color(210, 230, 255));
        button.setPreferredSize(new Dimension(150, 40));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(180, 200, 220)));
    }
    
    private void showChangePasswordDialog() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "請先登入後再變更密碼", "錯誤", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 10));
        
        JLabel oldPasswordLabel = new JLabel("目前密碼:");
        JPasswordField oldPasswordField = new JPasswordField(20);
        
        JLabel newPasswordLabel = new JLabel("新密碼:");
        JPasswordField newPasswordField = new JPasswordField(20);
        
        JLabel confirmPasswordLabel = new JLabel("確認新密碼:");
        JPasswordField confirmPasswordField = new JPasswordField(20);
        
        panel.add(oldPasswordLabel);
        panel.add(oldPasswordField);
        panel.add(newPasswordLabel);
        panel.add(newPasswordField);
        panel.add(confirmPasswordLabel);
        panel.add(confirmPasswordField);
        
        int result = JOptionPane.showConfirmDialog(
                this, panel, "變更密碼", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String oldPassword = new String(oldPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            
            // 確認新密碼與確認密碼相符
            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "新密碼與確認密碼不相符", "錯誤", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 密碼長度檢查
            if (newPassword.length() < 6) {
                JOptionPane.showMessageDialog(this, "新密碼長度須至少6個字元", "錯誤", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 變更密碼
            if (userController.changePassword(currentUser.getUid(), oldPassword, newPassword)) {
                // 更新當前用戶資訊
                currentUser.setPassword(newPassword);
                JOptionPane.showMessageDialog(this, "密碼變更成功", "成功", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "密碼變更失敗，請確認目前密碼是否正確", "錯誤", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showEditBirthDateDialog() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "請先登入後再編輯個人資料", "錯誤", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JPanel panel = new JPanel(new GridLayout(1, 2, 5, 10));
        
        JLabel birthDateLabel = new JLabel("出生日期 (YYYY-MM-DD):");
        JTextField birthDateField = new JTextField(currentUser.getBirthDate(), 15);
        
        panel.add(birthDateLabel);
        panel.add(birthDateField);
        
        int result = JOptionPane.showConfirmDialog(
                this, panel, "編輯出生日期", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String newBirthDate = birthDateField.getText().trim();
            
            // 驗證日期格式
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            try {
                sdf.parse(newBirthDate);
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this, "請輸入正確的日期格式 (YYYY-MM-DD)", "錯誤", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 更新出生日期
            if (userController.updateBirthDate(currentUser.getUid(), newBirthDate)) {
                // 更新當前用戶資訊
                currentUser.setBirthDate(newBirthDate);
                updateUserInfo();
                JOptionPane.showMessageDialog(this, "出生日期更新成功", "成功", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "出生日期更新失敗", "錯誤", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void logout() {
        int result = JOptionPane.showConfirmDialog(
                this, "確定要登出嗎?", "登出確認", 
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (result == JOptionPane.YES_OPTION && mainWindow != null) {
            mainWindow.logout();
        }
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        updateUserInfo();
    }
    
    public void setMainWindow(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    private void updateUserInfo() {
        if (currentUser != null) {
            emailLabel.setText(currentUser.getEmail());
            uidLabel.setText(String.valueOf(currentUser.getUid()));
            birthDateLabel.setText(currentUser.getBirthDate());
        } else {
            emailLabel.setText("尚未登入");
            uidLabel.setText("尚未登入");
            birthDateLabel.setText("尚未登入");
        }
    }
}
