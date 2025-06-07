package com.studysync.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateMySQLDatabase {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "Pooperic0317"; // ← 你提供的密碼
    
    // 檢查資料庫是否存在，如果不存在則創建它
    private static void ensureDatabaseExists() {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
             
            // 建立資料庫
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS studysync CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;");
            System.out.println("✅ 確認資料庫存在");
        } catch (SQLException e) {
            System.err.println("❌ 檢查資料庫存在性時發生錯誤");
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        // 首先確保資料庫存在
        ensureDatabaseExists();
        
        // 連接到已經存在的資料庫
        String jdbcUrl = "jdbc:mysql://localhost:3306/studysync?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        
        try (Connection conn = DriverManager.getConnection(jdbcUrl, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            // 建立 users 資料表
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS users (
                  uid INT AUTO_INCREMENT PRIMARY KEY,
                  email VARCHAR(255) UNIQUE NOT NULL,
                  password VARCHAR(255) NOT NULL,
                  birth_date DATE NOT NULL
                );
            """);            
            // 修改 tasks 資料表，添加 user_id 外鍵
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
            
            // 修改 events 資料表，添加 user_id 外鍵
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
            // 使用 IF NOT EXISTS 而不是強制刪除，這樣可以保留現有資料
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS categories (
                  id INT AUTO_INCREMENT PRIMARY KEY,
                  name VARCHAR(50) UNIQUE NOT NULL
                );
            """);
            
            // 檢查現有資料表是否已有 user_id 欄位，如果沒有則添加
            try {
                ResultSet rs = stmt.executeQuery("SHOW COLUMNS FROM tasks LIKE 'user_id'");
                if (!rs.next()) {
                    // 添加 user_id 欄位
                    stmt.executeUpdate("ALTER TABLE tasks ADD COLUMN user_id INT");
                    stmt.executeUpdate("ALTER TABLE tasks ADD CONSTRAINT fk_user_task FOREIGN KEY (user_id) REFERENCES users(uid) ON DELETE CASCADE");
                    System.out.println("✅ 已向 tasks 資料表添加 user_id 欄位");
                }
            } catch (SQLException e) {
                System.err.println("❌ 檢查或添加 tasks.user_id 欄位時發生錯誤: " + e.getMessage());
            }
            
            try {
                ResultSet rs = stmt.executeQuery("SHOW COLUMNS FROM events LIKE 'user_id'");
                if (!rs.next()) {
                    // 添加 user_id 欄位
                    stmt.executeUpdate("ALTER TABLE events ADD COLUMN user_id INT");
                    stmt.executeUpdate("ALTER TABLE events ADD CONSTRAINT fk_user_event FOREIGN KEY (user_id) REFERENCES users(uid) ON DELETE CASCADE");
                    System.out.println("✅ 已向 events 資料表添加 user_id 欄位");
                }
            } catch (SQLException e) {
                System.err.println("❌ 檢查或添加 events.user_id 欄位時發生錯誤: " + e.getMessage());
            }
              
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

            System.out.println("✅ 資料庫與資料表建立/更新成功！");
        } catch (SQLException e) {
            System.err.println("❌ 建立資料庫時發生錯誤");
            e.printStackTrace();
        }
    }
}

