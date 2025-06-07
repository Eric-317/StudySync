package com.studysync.util;

import java.io.*;
import java.util.Properties;

public class AppSettings {
    private static final String SETTINGS_FILE = "app_settings.properties";
    public static final String POMODORO_VIDEO_PATH_KEY = "pomodoro.video.path"; // 新增 Key
    public static final String CUSTOM_MUSIC_PATHS_KEY = "custom.music.paths"; // Key for custom music paths
    private static AppSettings instance;
    private Properties properties;
    
    private AppSettings() {
        properties = new Properties();
        loadSettings();
    }
    
    public static synchronized AppSettings getInstance() {
        if (instance == null) {
            instance = new AppSettings();
        }
        return instance;
    }
    
    private void loadSettings() {
        try (InputStream input = new FileInputStream(SETTINGS_FILE)) {
            properties.load(input);
        } catch (IOException e) {
            // 如果設定檔不存在，設定預設值
            setDefaultSettings();
        }
    }

    private void setDefaultSettings() {
        // 設定預設影片路徑，可以是您目前使用的影片，或者是一個空字串表示不播放
        properties.setProperty(POMODORO_VIDEO_PATH_KEY, "src/com/studysync/assets/video/大江大海江大海.mp4");
        properties.setProperty(CUSTOM_MUSIC_PATHS_KEY, ""); // Default to empty string

        // 設定預設的資料庫連線參數 (主要用於開發或首次執行)
        properties.setProperty("db.url", "jdbc:mysql://localhost:3306/studysync?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
        properties.setProperty("db.username", "root");
        properties.setProperty("db.password", ""); // 預設為空密碼，建議使用者在 app_settings.properties 中修改

        saveSettings();
    }

    public String getPomodoroVideoPath() {
        return properties.getProperty(POMODORO_VIDEO_PATH_KEY);
    }

    public void setPomodoroVideoPath(String path) {
        properties.setProperty(POMODORO_VIDEO_PATH_KEY, path);
        saveSettings();
    }

    public String getCustomMusicPaths() {
        return properties.getProperty(CUSTOM_MUSIC_PATHS_KEY, ""); // Return empty string if not found
    }

    public void setCustomMusicPaths(String paths) {
        properties.setProperty(CUSTOM_MUSIC_PATHS_KEY, paths);
        saveSettings();
    }

    // 新增公開的 getProperty 方法
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public void saveSettings() {
        try (OutputStream output = new FileOutputStream(SETTINGS_FILE)) {
            properties.store(output, "StudySync Application Settings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}