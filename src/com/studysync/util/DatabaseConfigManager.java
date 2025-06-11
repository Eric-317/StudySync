package com.studysync.util;

import java.io.*;
import java.util.Properties;

/**
 * 資料庫配置管理工具
 * 用於保存和載入用戶的資料庫配置偏好
 */
public class DatabaseConfigManager {
    private static final String CONFIG_FILE = "database_config.properties";
    private static final String DB_TYPE_KEY = "database.type";
    private static final String MYSQL_URL_KEY = "mysql.url";
    private static final String MYSQL_USERNAME_KEY = "mysql.username";
    // 注意：密碼不會保存到檔案中，每次都需要重新輸入
    
    /**
     * 保存資料庫配置
     */
    public static void saveConfig(String databaseType, String mysqlUrl, String mysqlUsername) {
        Properties props = new Properties();
        props.setProperty(DB_TYPE_KEY, databaseType);
        
        if ("MySQL".equals(databaseType) && mysqlUrl != null && mysqlUsername != null) {
            props.setProperty(MYSQL_URL_KEY, mysqlUrl);
            props.setProperty(MYSQL_USERNAME_KEY, mysqlUsername);
        }
        
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            props.store(fos, "StudySync Database Configuration");
            System.out.println("✅ 資料庫配置已保存");
        } catch (IOException e) {
            System.err.println("❌ 保存資料庫配置失敗: " + e.getMessage());
        }
    }
    
    /**
     * 載入資料庫配置
     */
    public static DatabaseConfig loadConfig() {
        File configFile = new File(CONFIG_FILE);
        if (!configFile.exists()) {
            return new DatabaseConfig(); // 返回預設配置
        }
        
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            props.load(fis);
            
            String dbType = props.getProperty(DB_TYPE_KEY, "SQLite");
            String mysqlUrl = props.getProperty(MYSQL_URL_KEY);
            String mysqlUsername = props.getProperty(MYSQL_USERNAME_KEY);
            
            return new DatabaseConfig(dbType, mysqlUrl, mysqlUsername);
        } catch (IOException e) {
            System.err.println("❌ 載入資料庫配置失敗: " + e.getMessage());
            return new DatabaseConfig(); // 返回預設配置
        }
    }
    
    /**
     * 刪除配置檔案
     */
    public static void clearConfig() {
        File configFile = new File(CONFIG_FILE);
        if (configFile.exists()) {
            configFile.delete();
            System.out.println("✅ 資料庫配置已清除");
        }
    }
    
    /**
     * 資料庫配置資料類別
     */
    public static class DatabaseConfig {
        private String databaseType;
        private String mysqlUrl;
        private String mysqlUsername;
        
        public DatabaseConfig() {
            this.databaseType = "SQLite"; // 預設使用 SQLite
        }
        
        public DatabaseConfig(String databaseType, String mysqlUrl, String mysqlUsername) {
            this.databaseType = databaseType;
            this.mysqlUrl = mysqlUrl;
            this.mysqlUsername = mysqlUsername;
        }
        
        // Getters
        public String getDatabaseType() { return databaseType; }
        public String getMysqlUrl() { return mysqlUrl; }
        public String getMysqlUsername() { return mysqlUsername; }
        
        public boolean isMySQL() {
            return "MySQL".equals(databaseType);
        }
        
        public boolean isSQLite() {
            return "SQLite".equals(databaseType);
        }
    }
}
