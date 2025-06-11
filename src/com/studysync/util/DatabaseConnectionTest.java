package com.studysync.util;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 資料庫連線測試工具
 * 用於測試資料庫配置是否正確
 */
public class DatabaseConnectionTest {
    
    public static void main(String[] args) {
        System.out.println("=== StudySync 資料庫連線測試 ===\n");
        
        // 測試 SQLite
        System.out.println("1. 測試 SQLite 連線...");
        testSQLiteConnection();
        
        System.out.println("\n" + "=".repeat(40) + "\n");
        
        // 測試 MySQL（如果已配置）
        System.out.println("2. 測試 MySQL 連線...");
        testMySQLConnection();
        
        System.out.println("\n=== 測試完成 ===");
    }
    
    private static void testSQLiteConnection() {
        try {
            DBUtil.useSQLite();
            Connection conn = DBUtil.getConnection();
            
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ SQLite 連線成功！");
                System.out.println("   資料庫類型: " + DBUtil.getDatabaseType());
                
                // 測試基本查詢
                var stmt = conn.createStatement();
                var rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table'");
                
                System.out.println("   現有資料表:");
                while (rs.next()) {
                    System.out.println("   - " + rs.getString("name"));
                }
                
                conn.close();
            } else {
                System.out.println("❌ SQLite 連線失敗");
            }
        } catch (SQLException e) {
            System.out.println("❌ SQLite 連線錯誤: " + e.getMessage());
        }
    }
    
    private static void testMySQLConnection() {
        // 這裡需要手動配置 MySQL 資訊進行測試
        // 實際使用時會透過對話框配置
        System.out.println("ℹ️  MySQL 測試需要透過應用程式的配置對話框進行");
        System.out.println("   您可以啟動 MainWindow 來測試 MySQL 連線");
    }
    
    public static boolean testCurrentConnection() {
        try {
            Connection conn = DBUtil.getConnection();
            boolean isValid = conn != null && !conn.isClosed();
            if (isValid) {
                conn.close();
            }
            return isValid;
        } catch (SQLException e) {
            return false;
        }
    }
}
