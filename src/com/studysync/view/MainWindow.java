package com.studysync.view;

import javax.swing.*;
import java.awt.*;
import com.studysync.model.User; // for showDashboard user parameter

public class MainWindow extends JFrame {
    private JPanel mainContentArea; // Panel to hold swappable content like LoginPanel, TaskPanel, etc.
    private JPanel iconPanel; // bottom icons, hidden until login
    private MusicPanel musicPanel; // 保持對 MusicPanel 的全局引用
    private User currentUser; // 添加當前用戶引用
    
    public MainWindow() {
        setTitle("StudySync 學習平台");
        setSize(1000, 1000);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false); // 禁止調整大小
        setLayout(new BorderLayout()); // Set BorderLayout for the JFrame's content pane

        // 初始化 MusicPanel 並保持單一實例
        musicPanel = new MusicPanel();
          // Create the main content area panel
        mainContentArea = new JPanel(new BorderLayout()); // This panel will display Login, Tasks, etc.
        add(mainContentArea, BorderLayout.CENTER);

        // Create and store the bottom icon panel
        iconPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        iconPanel.setBackground(Color.WHITE);
        
        String[] iconNames = {"行事曆", "任務", "番茄鐘", "音樂", "設定"}; // Added "音樂" option
        for (String iconName : iconNames) {
            JButton iconButton = new JButton(iconName);
            iconButton.setFocusPainted(false);
            iconButton.setBackground(new Color(245, 248, 255));
            iconButton.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
            iconButton.setPreferredSize(new Dimension(120, 40));
            iconButton.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 230), 1));
            iconButton.addActionListener(e -> {
                switch (iconName) {
                    case "行事曆":
                        // 顯示行事曆面板
                        CalendarPanel calendarPanel = new CalendarPanel();
                        calendarPanel.setCurrentUser(currentUser);
                        updateMainContent(calendarPanel);
                        break;
                    case "任務":
                        TaskPanel taskPanel = new TaskPanel();
                        taskPanel.setCurrentUser(currentUser);
                        updateMainContent(taskPanel);
                        break;
                    case "番茄鐘":
                        updateMainContent(new PomodoroPanel());
                        break;
                    case "音樂":
                        // 使用已存在的單一 MusicPanel 實例
                        updateMainContent(musicPanel);
                        break;                    case "設定":
                        SettingsPanel settingsPanel = new SettingsPanel();
                        settingsPanel.setCurrentUser(currentUser);
                        settingsPanel.setMainWindow(this); // 設置 MainWindow 引用，用於登出功能
                        updateMainContent(settingsPanel);
                        break;
                }
            });
            iconPanel.add(iconButton);
        }
        iconPanel.setVisible(false); // hide until after login
        add(iconPanel, BorderLayout.SOUTH); // Add iconPanel to the JFrame's SOUTH region

        showLoginPanel(); // Display the initial login panel in the mainContentArea
    }

    // Method to update the content of mainContentArea
    private void updateMainContent(JPanel panel) {
        mainContentArea.removeAll();
        mainContentArea.add(panel, BorderLayout.CENTER);
        mainContentArea.revalidate();
        mainContentArea.repaint();
    }

    public void showLoginPanel() {
        updateMainContent(new LoginPanel(this));
    }

    public void showRegisterPanel() {
        updateMainContent(new RegisterPanel(this));
    }

    public void showDashboard(User user) {
        this.currentUser = user; // 保存當前用戶
        iconPanel.setVisible(true); // reveal navigation icons after login
        updateMainContent(new DashboardPanel(user));
    }    
    
    public void logout() {
        this.currentUser = null; // 清除當前用戶
        iconPanel.setVisible(false); // 隱藏導航圖標
        showLoginPanel(); // 返回登入頁面
    }    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                MainWindow mainWindow = new MainWindow();
                
                // 顯示資料庫選擇對話框
                DatabaseSelectionDialog dbDialog = new DatabaseSelectionDialog(mainWindow);
                dbDialog.setVisible(true);
                
                if (dbDialog.isConfigurationComplete()) {
                    mainWindow.setVisible(true);
                } else {
                    System.out.println("用戶取消了資料庫配置，程式結束。");
                    System.exit(0);
                }
            } catch (Exception e) {
                System.err.println("啟動時發生錯誤: " + e.getMessage());
                e.printStackTrace();
                
                // 顯示錯誤對話框和安裝指南
                JOptionPane.showMessageDialog(
                    null,
                    "程式啟動失敗。可能是缺少必要的驅動程式。\n\n" +
                    "請確認已安裝 SQLite JDBC 驅動:\n" +
                    "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.45.3.0/sqlite-jdbc-3.45.3.0.jar\n\n" +
                    "安裝步驟:\n" +
                    "1. 在 IntelliJ IDEA 中按 F4\n" +
                    "2. 選擇 Dependencies\n" +
                    "3. 點擊 '+' 新增 JAR\n" +
                    "4. 選擇下載的 sqlite-jdbc-3.45.3.0.jar",
                    "啟動錯誤",
                    JOptionPane.ERROR_MESSAGE
                );
                System.exit(1);
            }
        });
    }
    
    // 關閉窗口時釋放音樂資源
    @Override
    public void dispose() {
        if (musicPanel != null) {
            musicPanel.dispose();
        }
        super.dispose();
    }
}
