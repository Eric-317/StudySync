package com.studysync.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.File;

public class DBUtil {
    // SQLite 設定
    private static final String SQLITE_DB_NAME = "studysync.db";
    private static final String SQLITE_URL = "jdbc:sqlite:" + SQLITE_DB_NAME;
    
    // MySQL 設定 (動態配置)
    private static String mysqlUrl = null;
    private static String mysqlUsername = null;
    private static String mysqlPassword = null;
    private static boolean useMysql = false;
    
    // 設定 MySQL 連線資訊
    public static void configureMysql(String url, String username, String password) {
        mysqlUrl = url;
        mysqlUsername = username;
        mysqlPassword = password;
        useMysql = true;
    }
    
    // 使用 SQLite
    public static void useSQLite() {
        useMysql = false;
    }
    
    // 獲取新的資料庫連接
    public static Connection getConnection() throws SQLException {
        if (useMysql && mysqlUrl != null) {
            return getMysqlConnection();
        } else {
            return getSQLiteConnection();
        }
    }
      private static Connection getSQLiteConnection() throws SQLException {
        try {
            // 嘗試加載 SQLite 驅動
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ 無法加載 SQLite JDBC 驅動程序!");
            System.err.println("請下載 SQLite JDBC 驅動並添加到專案中:");
            System.err.println("下載地址: https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.45.3.0/sqlite-jdbc-3.45.3.0.jar");
            throw new SQLException("SQLite 驅動未找到", e);
        }
        
        // 檢查資料庫檔案是否存在，如果不存在會自動建立
        File dbFile = new File(SQLITE_DB_NAME);
        if (!dbFile.exists()) {
            System.out.println("✅ 建立新的 SQLite 資料庫: " + SQLITE_DB_NAME);
        }
        
        // 建立新連接
        Connection conn = DriverManager.getConnection(SQLITE_URL);
        conn.setAutoCommit(true); // 確保自動提交模式開啟
        
        // 啟用外鍵約束
        try {
            conn.createStatement().execute("PRAGMA foreign_keys = ON");
        } catch (SQLException e) {
            System.err.println("警告: 無法啟用外鍵約束: " + e.getMessage());
        }
        
        return conn;
    }
      private static Connection getMysqlConnection() throws SQLException {
        try {
            // 確保 MySQL 驅動已加載
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ 無法加載 MySQL JDBC 驅動程序!");
            System.err.println("請確認已將 MySQL JDBC 驅動添加到專案中");
            throw new SQLException("MySQL 驅動未找到", e);
        }
        
        if (mysqlUrl == null || mysqlUsername == null || mysqlPassword == null) {
            throw new SQLException("MySQL 連線資訊未設定");
        }
        
        // 建立新連接
        Connection conn = DriverManager.getConnection(mysqlUrl, mysqlUsername, mysqlPassword);
        conn.setAutoCommit(true); // 確保自動提交模式開啟
        return conn;
    }
    
    // 檢查目前使用的資料庫類型
    public static String getDatabaseType() {
        return useMysql ? "MySQL" : "SQLite";
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