package com.studysync.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateMySQLDatabase {
    public static void main(String[] args) {
        String jdbcUrl = "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String username = "root";
        String password = "Pooperic0317"; // ← 你提供的密碼

        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             Statement stmt = conn.createStatement()) {

            // 建立資料庫
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS studysync CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;");
            stmt.execute("USE studysync;");

            // 建立 users 資料表
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS users (
                  uid INT AUTO_INCREMENT PRIMARY KEY,
                  email VARCHAR(255) UNIQUE NOT NULL,
                  password VARCHAR(255) NOT NULL,
                  birth_date DATE NOT NULL
                );
            """);

            // 強制刪除舊 tasks 表，避免 due_time 遺失問題
            stmt.executeUpdate("DROP TABLE IF EXISTS tasks;");
            stmt.executeUpdate("""
                CREATE TABLE tasks (
                  uid INT AUTO_INCREMENT PRIMARY KEY,
                  title VARCHAR(255) NOT NULL,
                  due_time DATETIME NOT NULL,
                  category VARCHAR(50),
                  completed BOOLEAN DEFAULT 0
                );
            """);

            System.out.println("✅ 資料庫與資料表建立成功！");
        } catch (SQLException e) {
            System.err.println("❌ 建立資料庫時發生錯誤");
            e.printStackTrace();
        }
    }
}

