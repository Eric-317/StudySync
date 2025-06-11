package com.studysync.util;

import javax.swing.*;

/**
 * 應用程式啟動前的系統檢查
 */
public class StartupChecker {
    
    public static boolean checkDependencies() {
        boolean allOk = true;
        StringBuilder errorMessage = new StringBuilder();
        
        // 檢查 SQLite 驅動
        boolean sqliteAvailable = checkSQLiteDriver();
        if (!sqliteAvailable) {
            errorMessage.append("❌ SQLite JDBC 驅動未找到\n");
            errorMessage.append("請下載並添加到專案中:\n");
            errorMessage.append("https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.45.3.0/sqlite-jdbc-3.45.3.0.jar\n\n");
            allOk = false;
        }
        
        // 檢查 MySQL 驅動（可選）
        boolean mysqlAvailable = checkMySQLDriver();
        if (!mysqlAvailable) {
            errorMessage.append("⚠️ MySQL JDBC 驅動未找到（如果要使用 MySQL 才需要）\n\n");
        }
        
        if (!allOk) {
            errorMessage.append("建議操作:\n");
            errorMessage.append("1. 在 IntelliJ IDEA 中按 F4 開啟 Module Settings\n");
            errorMessage.append("2. 點選 Dependencies\n");
            errorMessage.append("3. 點擊 '+' 新增 JAR\n");
            errorMessage.append("4. 選擇下載的 sqlite-jdbc-3.45.3.0.jar\n");
            
            JOptionPane.showMessageDialog(
                null, 
                errorMessage.toString(), 
                "缺少必要的驅動程式", 
                JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
        
        System.out.println("✅ 所有依賴檢查通過");
        return true;
    }
    
    private static boolean checkSQLiteDriver() {
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("✅ SQLite 驅動可用");
            return true;
        } catch (ClassNotFoundException e) {
            System.out.println("❌ SQLite 驅動未找到");
            return false;
        }
    }
    
    private static boolean checkMySQLDriver() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ MySQL 驅動可用");
            return true;
        } catch (ClassNotFoundException e) {
            System.out.println("⚠️ MySQL 驅動未找到");
            return false;
        }
    }
    
    /**
     * 顯示驅動安裝說明
     */
    public static void showDriverInstallationGuide() {
        String message = """
            StudySync 驅動安裝指南
            
            需要安裝的驅動：
            
            1. SQLite JDBC 驅動（必需）
               下載地址：https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.45.3.0/sqlite-jdbc-3.45.3.0.jar
            
            2. MySQL JDBC 驅動（可選，如果選擇使用 MySQL）
               您現有的 mysql-connector-j-9.3.0.jar 可繼續使用
            
            安裝步驟：
            1. 在 IntelliJ IDEA 中按 F4 或右鍵選擇 'Open Module Settings'
            2. 選擇 'Dependencies'
            3. 點擊 '+' 號，選擇 'JARs or directories'
            4. 選擇下載的 JAR 檔案
            5. 點擊 'OK' 儲存
            
            完成後重新啟動程式即可。
            """;
        
        JOptionPane.showMessageDialog(
            null, 
            message, 
            "驅動安裝指南", 
            JOptionPane.INFORMATION_MESSAGE
        );
    }
}
