package com.studysync.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/studysync?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root"; // 改為你的 MySQL 使用者
    private static final String PASSWORD = "Pooperic0317"; // 改為你的 MySQL 密碼
    
    // 獲取新的資料庫連接
    public static Connection getConnection() throws SQLException {
        try {
            // 確保驅動已加載
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("無法加載 MySQL JDBC 驅動程序: " + e.getMessage());
        }
        
        // 建立新連接
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        conn.setAutoCommit(true); // 確保自動提交模式開啟
        return conn;
    }
    
    // 安全地關閉連接
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("關閉資料庫連接時發生錯誤: " + e.getMessage());
            }
        }
    }
}