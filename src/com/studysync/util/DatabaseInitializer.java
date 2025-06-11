package com.studysync.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class DatabaseInitializer {
      public static void createTables() {
        String dbType = DBUtil.getDatabaseType();
        System.out.println("正在初始化 " + dbType + " 資料庫...");
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement()) {

            if ("MySQL".equals(dbType)) {
                createMySQLTables(stmt);
            } else {
                createSQLiteTables(stmt);
            }
            
            // 插入預設類別（如果沒有的話）
            insertDefaultCategories(stmt);
            
            System.out.println("✅ " + dbType + " 資料庫初始化完成！");
        } catch (SQLException e) {
            System.err.println("❌ 資料庫初始化失敗: " + e.getMessage());
            
            // 顯示用戶友善的錯誤訊息
            String errorMsg = "資料庫初始化失敗\n\n";
            if (e.getMessage().contains("sqlite")) {
                errorMsg += "可能原因：SQLite 驅動未安裝\n";
                errorMsg += "請下載並安裝 SQLite JDBC 驅動";
            } else if (e.getMessage().contains("mysql")) {
                errorMsg += "可能原因：MySQL 連線失敗\n";
                errorMsg += "請檢查 MySQL 伺服器是否運行以及連線設定是否正確";
            } else {
                errorMsg += "錯誤詳情：" + e.getMessage();
            }
            
            javax.swing.JOptionPane.showMessageDialog(
                null, 
                errorMsg, 
                "資料庫初始化錯誤", 
                javax.swing.JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }
    
    private static void createMySQLTables(Statement stmt) throws SQLException {
        // 建立 users 資料表
        stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS users (
              uid INT AUTO_INCREMENT PRIMARY KEY,
              email VARCHAR(255) UNIQUE NOT NULL,
              password VARCHAR(255) NOT NULL,
              birth_date DATE NOT NULL
            );
        """);
        
        // 建立 categories 資料表
        stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS categories (
              id INT AUTO_INCREMENT PRIMARY KEY,
              name VARCHAR(50) UNIQUE NOT NULL
            );
        """);
        
        // 建立 tasks 資料表
        stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS tasks (
              uid INT AUTO_INCREMENT PRIMARY KEY,
              title VARCHAR(255) NOT NULL,
              due_time DATETIME NOT NULL,
              category VARCHAR(50),
              completed BOOLEAN DEFAULT 0,
              user_id INT,
              FOREIGN KEY (user_id) REFERENCES users(uid) ON DELETE CASCADE
            );
        """);
        
        // 建立 events 資料表
        stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS events (
              id INT AUTO_INCREMENT PRIMARY KEY,
              event_date DATE NOT NULL,
              event_time TIME,
              description VARCHAR(255),
              event_type VARCHAR(50) DEFAULT 'General',
              user_id INT,
              FOREIGN KEY (user_id) REFERENCES users(uid) ON DELETE CASCADE
            );
        """);
    }
    
    private static void createSQLiteTables(Statement stmt) throws SQLException {
        // 建立 users 資料表
        stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS users (
              uid INTEGER PRIMARY KEY AUTOINCREMENT,
              email TEXT UNIQUE NOT NULL,
              password TEXT NOT NULL,
              birth_date DATE NOT NULL
            );
        """);
        
        // 建立 categories 資料表
        stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS categories (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              name TEXT UNIQUE NOT NULL
            );
        """);
        
        // 建立 tasks 資料表
        stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS tasks (
              uid INTEGER PRIMARY KEY AUTOINCREMENT,
              title TEXT NOT NULL,
              due_time DATETIME NOT NULL,
              category TEXT,
              completed INTEGER DEFAULT 0,
              user_id INTEGER,
              FOREIGN KEY (user_id) REFERENCES users(uid) ON DELETE CASCADE
            );
        """);
        
        // 建立 events 資料表
        stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS events (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              event_date DATE NOT NULL,
              event_time TIME,
              description TEXT,
              event_type TEXT DEFAULT 'General',
              user_id INTEGER,
              FOREIGN KEY (user_id) REFERENCES users(uid) ON DELETE CASCADE
            );
        """);
    }
    
    private static void insertDefaultCategories(Statement stmt) throws SQLException {
        // 檢查是否有預設類別，如果沒有則插入
        try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM categories")) {
            rs.next();
            int count = rs.getInt(1);
            if (count == 0) {
                // 插入預設類別
                stmt.executeUpdate("""
                    INSERT INTO categories (name) VALUES 
                    ('Studying'), ('Homework'), ('Writing'), ('Meeting'), ('Reading');
                """);
                System.out.println("✅ 已插入預設類別");
            } else {
                System.out.println("✓ 已有類別資料，跳過初始化");
            }
        }
    }
}
