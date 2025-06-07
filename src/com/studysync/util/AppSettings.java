package com.studysync.util;

import java.io.*;
import java.util.Properties;

public class AppSettings {
    private static final String SETTINGS_FILE = "app_settings.properties";
    public static final String POMODORO_VIDEO_PATH_KEY = "pomodoro.video.path"; // 新增 Key
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
        saveSettings();
    }

    public String getPomodoroVideoPath() {
        return properties.getProperty(POMODORO_VIDEO_PATH_KEY);
    }

    public void setPomodoroVideoPath(String path) {
        properties.setProperty(POMODORO_VIDEO_PATH_KEY, path);
        saveSettings();
    }

    public void saveSettings() {
        try (OutputStream output = new FileOutputStream(SETTINGS_FILE)) {
            properties.store(output, "StudySync Application Settings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}