package com.studysync.util;

import java.io.*;
import java.util.Properties;

/**
 * 應用程式設定管理類
 * 使用單例模式管理應用程式的所有設定項目
 * 設定檔案使用 Properties 格式儲存在 app_settings.properties 檔案中
 * 
 * 主要功能：
 * - 管理番茄鐘背景影片路徑
 * - 管理自訂音樂檔案路徑
 * - 提供設定的讀取和儲存功能
 * - 自動建立預設設定檔
 * 
 * @author StudySync Team
 * @version 1.0
 */
public class AppSettings {
    /** 設定檔案名稱 */
    private static final String SETTINGS_FILE = "app_settings.properties";
    
    /** 番茄鐘背景影片路徑的設定鍵 */
    public static final String POMODORO_VIDEO_PATH_KEY = "pomodoro.video.path";
    
    /** 自訂音樂檔案路徑的設定鍵（多個路徑用分號分隔） */
    public static final String CUSTOM_MUSIC_PATHS_KEY = "custom.music.paths";
    
    /** 單例實例 */
    private static AppSettings instance;
    
    /** Properties 物件，用於儲存所有設定項目 */
    private Properties properties;
      /**
     * 私有建構子，實現單例模式
     * 初始化 Properties 物件並載入設定檔
     */
    private AppSettings() {
        properties = new Properties();
        loadSettings();
    }
    
    /**
     * 取得 AppSettings 的單例實例
     * 使用雙重檢查鎖定確保執行緒安全
     * 
     * @return AppSettings 的唯一實例
     */
    public static synchronized AppSettings getInstance() {
        if (instance == null) {
            instance = new AppSettings();
        }
        return instance;
    }
    
    /**
     * 從設定檔載入設定
     * 如果設定檔不存在，則建立預設設定
     */
    private void loadSettings() {
        try (InputStream input = new FileInputStream(SETTINGS_FILE)) {
            properties.load(input);
        } catch (IOException e) {
            // 如果設定檔不存在，設定預設值
            setDefaultSettings();
        }
    }

    /**
     * 設定預設的應用程式設定值
     * 當設定檔不存在時會自動呼叫此方法
     */
    private void setDefaultSettings() {
        // 設定預設影片路徑，可以是您目前使用的影片，或者是一個空字串表示不播放
        properties.setProperty(POMODORO_VIDEO_PATH_KEY, "src/com/studysync/assets/video/大江大海江大海.mp4");
        properties.setProperty(CUSTOM_MUSIC_PATHS_KEY, ""); // Default to empty string
        saveSettings();
    }

    /**
     * 取得番茄鐘背景影片的檔案路徑
     * 
     * @return 影片檔案路徑，如果未設定則返回 null
     */
    public String getPomodoroVideoPath() {
        return properties.getProperty(POMODORO_VIDEO_PATH_KEY);
    }

    /**
     * 設定番茄鐘背景影片的檔案路徑
     * 
     * @param path 影片檔案的絕對或相對路徑
     */
    public void setPomodoroVideoPath(String path) {
        properties.setProperty(POMODORO_VIDEO_PATH_KEY, path);
        saveSettings();
    }

    /**
     * 取得自訂音樂檔案路徑清單
     * 多個路徑以分號分隔
     * 
     * @return 音樂檔案路徑字串，如果未設定則返回空字串
     */
    public String getCustomMusicPaths() {
        return properties.getProperty(CUSTOM_MUSIC_PATHS_KEY, ""); // Return empty string if not found
    }

    /**
     * 設定自訂音樂檔案路徑清單
     * 多個路徑請使用分號分隔
     * 
     * @param paths 音樂檔案路徑字串，多個路徑用分號分隔
     */
    public void setCustomMusicPaths(String paths) {
        properties.setProperty(CUSTOM_MUSIC_PATHS_KEY, paths);
        saveSettings();
    }

    /**
     * 將目前的設定儲存到檔案
     * 使用 Properties.store() 方法將設定寫入 app_settings.properties 檔案
     */
    public void saveSettings() {
        try (OutputStream output = new FileOutputStream(SETTINGS_FILE)) {
            properties.store(output, "StudySync Application Settings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}