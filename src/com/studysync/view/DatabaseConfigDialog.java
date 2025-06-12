package com.studysync.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 資料庫配置對話框類
 * 提供 MySQL 資料庫連線設定和測試功能
 * 
 * 主要功能：
 * - MySQL 資料庫連線參數設定
 * - 資料庫連線測試功能
 * - 自動建立資料庫（如不存在）
 * - 連線參數驗證和儲存
 * - 錯誤處理和使用者回饋
 * 
 * UI 組件：
 * - 伺服器地址輸入欄位
 * - 資料庫名稱輸入欄位
 * - 使用者名稱和密碼輸入欄位
 * - 測試連線、儲存和取消按鈕
 * 
 * 設定流程：
 * 1. 輸入 MySQL 伺服器連線資訊
 * 2. 測試連線確認可用性
 * 3. 自動建立指定資料庫
 * 4. 儲存設定供應用程式使用
 * 
 * @author StudySync Team
 * @version 1.0
 */
public class DatabaseConfigDialog extends JDialog {
    /** 資料庫伺服器 URL 輸入欄位 */
    private JTextField urlField;
    
    /** 使用者名稱輸入欄位 */
    private JTextField usernameField;
    
    /** 密碼輸入欄位 */
    private JPasswordField passwordField;
    
    /** 資料庫名稱輸入欄位 */
    private JTextField databaseNameField;
    
    /** 測試連線按鈕 */
    private JButton testConnectionButton;
    
    /** 儲存設定按鈕 */
    private JButton saveButton;
    
    /** 取消按鈕 */
    private JButton cancelButton;
    
    /** 配置是否成功的標誌 */
    private boolean configurationSuccessful = false;
    
    /** 最終確認的資料庫 URL */
    private String finalUrl;
    
    /** 最終確認的使用者名稱 */
    private String finalUsername;
    
    /** 最終確認的密碼 */
    private String finalPassword;
    
    /**
     * 建構資料庫配置對話框
     * 
     * @param parent 父視窗
     */
    public DatabaseConfigDialog(JFrame parent) {
        super(parent, "資料庫配置", true);
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(450, 300);
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        urlField = new JTextField("localhost:3306", 20);
        usernameField = new JTextField("root", 15);
        passwordField = new JPasswordField(15);
        databaseNameField = new JTextField("studysync", 15);
        
        testConnectionButton = new JButton("測試連線");
        saveButton = new JButton("儲存配置");
        cancelButton = new JButton("取消");
        
        saveButton.setEnabled(false); // 初始狀態下禁用
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 創建主面板
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 標題
        JLabel titleLabel = new JLabel("請輸入您的 MySQL 資料庫配置");
        titleLabel.setFont(new Font("微軟正黑體", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);
        
        gbc.gridwidth = 1;
        
        // 資料庫伺服器地址
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("伺服器地址:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(urlField, gbc);
        
        // 資料庫名稱
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("資料庫名稱:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(databaseNameField, gbc);
        
        // 用戶名
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(new JLabel("用戶名:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);
        
        // 密碼
        gbc.gridx = 0; gbc.gridy = 4;
        mainPanel.add(new JLabel("密碼:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);
        
        // 測試連線按鈕
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(testConnectionButton, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // 按鈕面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        testConnectionButton.addActionListener(e -> testConnection());
        saveButton.addActionListener(e -> saveConfiguration());
        cancelButton.addActionListener(e -> {
            configurationSuccessful = false;
            dispose();
        });
    }
    
    private void testConnection() {
        String url = "jdbc:mysql://" + urlField.getText() + 
                    "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        testConnectionButton.setEnabled(false);
        testConnectionButton.setText("測試中...");
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection conn = DriverManager.getConnection(url, username, password);
                    conn.close();
                    return true;
                } catch (Exception ex) {
                    return false;
                }
            }
            
            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        JOptionPane.showMessageDialog(DatabaseConfigDialog.this, 
                            "✅ 連線測試成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                        saveButton.setEnabled(true);
                        
                        // 保存連線資訊
                        finalUrl = url;
                        finalUsername = username;
                        finalPassword = password;
                    } else {
                        JOptionPane.showMessageDialog(DatabaseConfigDialog.this, 
                            "[錯誤] 連線測試失敗，請檢查您的設定。", "錯誤", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(DatabaseConfigDialog.this, 
                        "[錯誤] 連線測試失敗：" + ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
                }
                
                testConnectionButton.setEnabled(true);
                testConnectionButton.setText("測試連線");
            }
        };
        
        worker.execute();
    }
    
    private void saveConfiguration() {
        // 創建資料庫（如果不存在）
        createDatabaseIfNotExists();
        configurationSuccessful = true;
        dispose();
    }
    
    private void createDatabaseIfNotExists() {
        String baseUrl = "jdbc:mysql://" + urlField.getText() + 
                        "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String databaseName = databaseNameField.getText();
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        try {
            // 先連接到 MySQL 伺服器（不指定資料庫）
            Connection conn = DriverManager.getConnection(baseUrl, username, password);
            Statement stmt = conn.createStatement();
            
            // 創建資料庫
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + databaseName + 
                             " CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci");
            
            conn.close();
            
            // 更新最終 URL 包含資料庫名稱
            finalUrl = "jdbc:mysql://" + urlField.getText() + "/" + databaseName + 
                      "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
            
            JOptionPane.showMessageDialog(this, 
                "✅ 資料庫配置已儲存！", "成功", JOptionPane.INFORMATION_MESSAGE);
                
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "[錯誤] 創建資料庫時發生錯誤：" + ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isConfigurationSuccessful() {
        return configurationSuccessful;
    }
    
    public String getDatabaseUrl() {
        return finalUrl;
    }
    
    public String getDatabaseUsername() {
        return finalUsername;
    }
    
    public String getDatabasePassword() {
        return finalPassword;
    }
}
