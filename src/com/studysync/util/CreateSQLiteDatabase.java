package com.studysync.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class CreateSQLiteDatabase {
    
    public static void createTablesIfNotExists() {
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement()) {

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
            
            // 建立 tasks 資料表，添加 user_id 外鍵
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
            
            // 建立 events 資料表，添加 user_id 外鍵
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

            System.out.println("✅ SQLite 資料庫與資料表建立/更新成功！");
        } catch (SQLException e) {
            System.err.println("❌ 建立 SQLite 資料庫時發生錯誤");
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        createTablesIfNotExists();
    }
}
