package com.studysync.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.File;

/**
 * 資料庫連線工具類
 * 提供統一的資料庫連線管理，支援 SQLite 和 MySQL 兩種資料庫
 * 
 * 主要功能：
 * - 管理 SQLite 本地資料庫連線
 * - 管理 MySQL 遠程資料庫連線
 * - 動態切換資料庫類型
 * - 自動載入資料庫驅動程式
 * - 提供連線池管理（基礎版本）
 * 
 * 使用方式：
 * 1. 使用 SQLite：直接呼叫 getConnection()
 * 2. 使用 MySQL：先呼叫 configureMysql() 設定連線資訊，再呼叫 getConnection()
 * 
 * @author StudySync Team
 * @version 1.0
 */
public class DBUtil {
    /** SQLite 資料庫檔案名稱 */
    private static final String SQLITE_DB_NAME = "studysync.db";
    
    /** SQLite 連線 URL */
    private static final String SQLITE_URL = "jdbc:sqlite:" + SQLITE_DB_NAME;
    
    /** MySQL 連線 URL（動態設定） */
    private static String mysqlUrl = null;
    
    /** MySQL 使用者名稱（動態設定） */
    private static String mysqlUsername = null;
    
    /** MySQL 密碼（動態設定） */
    private static String mysqlPassword = null;
    
    /** 是否使用 MySQL 資料庫的旗標 */
    private static boolean useMysql = false;    
    /**
     * 設定 MySQL 資料庫連線資訊
     * 呼叫此方法後，getConnection() 將使用 MySQL 連線
     * 
     * @param url MySQL 資料庫連線 URL
     * @param username MySQL 使用者名稱
     * @param password MySQL 密碼
     */
    public static void configureMysql(String url, String username, String password) {
        mysqlUrl = url;
        mysqlUsername = username;
        mysqlPassword = password;
        useMysql = true;
    }
    
    /**
     * 切換為使用 SQLite 資料庫
     * 呼叫此方法後，getConnection() 將使用 SQLite 連線
     */
    public static void useSQLite() {
        useMysql = false;
    }
    
    /**
     * 取得資料庫連線
     * 根據目前設定的資料庫類型返回對應的連線
     * 
     * @return 資料庫連線物件
     * @throws SQLException 當連線失敗時拋出例外
     */
    public static Connection getConnection() throws SQLException {
        if (useMysql && mysqlUrl != null) {
            return getMysqlConnection();
        } else {
            return getSQLiteConnection();
        }
    }
    
    /**
     * 建立 SQLite 資料庫連線
     * 自動載入 SQLite JDBC 驅動程式，並建立或連接到本地資料庫檔案
     * 
     * @return SQLite 資料庫連線
     * @throws SQLException 當連線失敗或驅動程式未找到時拋出例外
     */
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
    
    /**
     * 建立 MySQL 資料庫連線
     * 自動載入 MySQL JDBC 驅動程式，並連接到遠程 MySQL 伺服器
     * 
     * @return MySQL 資料庫連線
     * @throws SQLException 當連線失敗或驅動程式未找到時拋出例外
     */
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
    
    /**
     * 取得目前使用的資料庫類型
     * 
     * @return 資料庫類型字串："MySQL" 或 "SQLite"
     */
    public static String getDatabaseType() {
        return useMysql ? "MySQL" : "SQLite";
    }
    
    /**
     * 安全地關閉資料庫連線
     * 檢查連線是否為 null 且未關閉，然後安全地關閉連線
     * 
     * @param conn 要關閉的資料庫連線
     */
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