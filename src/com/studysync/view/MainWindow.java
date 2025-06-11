package com.studysync.view;

import javax.swing.*;
import java.awt.*;
import com.studysync.model.User;

/**
 * 主視窗類 - StudySync 應用程式的主要使用者介面
 * 
 * 此類負責：
 * - 建立和管理主視窗的佈局
 * - 在不同的功能面板之間切換（登入、任務、番茄鐘等）
 * - 管理底部導航圖示
 * - 處理使用者登入/登出狀態
 * - 協調各個功能模組的顯示
 * 
 * 主要功能模組：
 * - 使用者登入/註冊
 * - 任務管理
 * - 番茄鐘計時器
 * - 音樂播放
 * - 行事曆管理
 * - 個人設定
 * 
 * @author StudySync Team
 * @version 1.0
 */
public class MainWindow extends JFrame {
    /** 主要內容區域，用於顯示不同的功能面板 */
    private JPanel mainContentArea;
    
    /** 底部導航圖示面板，登入後才顯示 */
    private JPanel iconPanel;
    
    /** 音樂播放面板的全域參考，保持單一實例 */
    private MusicPanel musicPanel;
    
    /** 目前登入的使用者 */
    private User currentUser;
    
    /**
     * 建構主視窗
     * 初始化所有 UI 元件和佈局
     */
    public MainWindow() {
        // 設定視窗基本屬性
        setTitle("StudySync 學習平台");
        setSize(1000, 1000);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 視窗置中
        setResizable(false); // 禁止調整大小
        setLayout(new BorderLayout());

        // 初始化音樂面板並保持單一實例
        musicPanel = new MusicPanel();
        
        // 建立主要內容區域
        mainContentArea = new JPanel(new BorderLayout());
        add(mainContentArea, BorderLayout.CENTER);

        // 建立底部導航圖示面板
        createNavigationPanel();
        
        // 預設顯示登入面板
        showLoginPanel();
    }
    
    /**
     * 建立底部導航面板
     * 包含各功能模組的快速存取按鈕
     */
    private void createNavigationPanel() {
        iconPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        iconPanel.setBackground(Color.WHITE);
        
        String[] iconNames = {"行事曆", "任務", "番茄鐘", "音樂", "設定"};
        
        for (String iconName : iconNames) {
            JButton iconButton = createNavigationButton(iconName);
            iconButton.addActionListener(e -> handleNavigationClick(iconName));
            iconPanel.add(iconButton);
        }
        
        // 初始狀態隱藏導航面板，登入後才顯示
        iconPanel.setVisible(false);
        add(iconPanel, BorderLayout.SOUTH);
    }
    
    /**
     * 建立導航按鈕
     * 
     * @param iconName 按鈕名稱
     * @return 格式化的導航按鈕
     */
    private JButton createNavigationButton(String iconName) {
        JButton iconButton = new JButton(iconName);
        iconButton.setFocusPainted(false);
        iconButton.setBackground(new Color(245, 248, 255));
        iconButton.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        iconButton.setPreferredSize(new Dimension(120, 40));
        iconButton.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 230), 1));
        return iconButton;
    }
    
    /**
     * 處理導航按鈕點擊事件
     * 根據點擊的按鈕切換到對應的功能面板
     * 
     * @param iconName 被點擊的按鈕名稱
     */
    private void handleNavigationClick(String iconName) {
        switch (iconName) {
            case "行事曆":
                // 顯示行事曆面板
                CalendarPanel calendarPanel = new CalendarPanel();
                calendarPanel.setCurrentUser(currentUser);
                updateMainContent(calendarPanel);
                break;            case "任務":
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
                break;
            case "設定":
                SettingsPanel settingsPanel = new SettingsPanel();
                settingsPanel.setCurrentUser(currentUser);
                settingsPanel.setMainWindow(this); // 設置 MainWindow 引用，用於登出功能
                updateMainContent(settingsPanel);
                break;
        }
    }    /**
     * 更新主要內容區域的顯示
     * 清除目前內容並顯示新的面板
     * 
     * @param panel 要顯示的新面板
     */
    private void updateMainContent(JPanel panel) {
        mainContentArea.removeAll();
        mainContentArea.add(panel, BorderLayout.CENTER);
        mainContentArea.revalidate();
        mainContentArea.repaint();
    }

    /**
     * 顯示登入面板
     * 通常在應用程式啟動時或使用者登出後呼叫
     */
    public void showLoginPanel() {
        updateMainContent(new LoginPanel(this));
    }

    /**
     * 顯示註冊面板
     * 當使用者從登入面板點擊註冊按鈕時呼叫
     */
    public void showRegisterPanel() {
        updateMainContent(new RegisterPanel(this));
    }

    /**
     * 顯示主控台面板
     * 當使用者成功登入後呼叫，同時顯示導航按鈕
     * 
     * @param user 已登入的使用者物件
     */
    public void showDashboard(User user) {
        this.currentUser = user; // 保存當前用戶
        iconPanel.setVisible(true); // reveal navigation icons after login
        updateMainContent(new DashboardPanel(user));
    }
    
    /**
     * 處理使用者登出
     * 清除目前使用者資訊，隱藏導航按鈕，返回登入頁面
     */
    public void logout() {
        this.currentUser = null; // 清除當前用戶
        iconPanel.setVisible(false); // 隱藏導航圖標
        showLoginPanel(); // 返回登入頁面
    }
    
    /**
     * 應用程式主要進入點
     * 初始化主視窗並顯示資料庫選擇對話框
     * 
     * @param args 命令列參數
     */
    public static void main(String[] args) {
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
    
    /**
     * 覆寫 dispose 方法以正確釋放資源
     * 在視窗關閉時釋放音樂播放面板的資源
     */
    @Override
    public void dispose() {
        if (musicPanel != null) {
            musicPanel.dispose();
        }
        super.dispose();
    }
}
