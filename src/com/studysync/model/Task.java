package com.studysync.model;

import java.time.LocalDateTime;

/**
 * 任務資料模型類
 * 代表系統中的一個學習任務，包含任務的基本資訊和狀態
 * 
 * 此類用於：
 * - 儲存任務的詳細資訊（標題、到期時間、分類、完成狀態等）
 * - 提供任務狀態的判斷方法（是否為今日任務、是否即將到期等）
 * - 在不同層之間傳遞任務資料
 * 
 * @author StudySync Team
 * @version 1.0
 */
public class Task {
    /** 任務唯一識別碼 */
    private int uid;
    
    /** 任務標題 */
    private String title;
    
    /** 任務到期時間 */
    private LocalDateTime dueTime;
    
    /** 任務分類 */
    private String category;
    
    /** 任務完成狀態 */
    private boolean completed;
    
    /** 任務所屬的使用者 ID */
    private int userId;

    /**
     * 建構一個完整的任務物件
     * 
     * @param uid 任務唯一識別碼
     * @param title 任務標題
     * @param dueTime 任務到期時間
     * @param category 任務分類
     * @param completed 任務完成狀態
     * @param userId 任務所屬的使用者 ID
     */
    public Task(int uid, String title, LocalDateTime dueTime, String category, boolean completed, int userId) {
        this.uid = uid;
        this.title = title;
        this.dueTime = dueTime;
        this.category = category;
        this.completed = completed;
        this.userId = userId;
    }

    /**
     * 建構一個新任務物件（不指定 UID，通常用於新增任務）
     * 
     * @param title 任務標題
     * @param dueTime 任務到期時間
     * @param category 任務分類
     * @param completed 任務完成狀態
     * @param userId 任務所屬的使用者 ID
     */
    public Task(String title, LocalDateTime dueTime, String category, boolean completed, int userId) {
        this(-1, title, dueTime, category, completed, userId);
    }

    /**
     * 建構任務物件（保持向後相容性，不指定使用者 ID）
     * 
     * @param uid 任務唯一識別碼
     * @param title 任務標題
     * @param dueTime 任務到期時間
     * @param category 任務分類
     * @param completed 任務完成狀態
     */
    public Task(int uid, String title, LocalDateTime dueTime, String category, boolean completed) {
        this(uid, title, dueTime, category, completed, -1); // 預設userId為-1
    }

    /**
     * 建構新任務物件（保持向後相容性，不指定 UID 和使用者 ID）
     * 
     * @param title 任務標題
     * @param dueTime 任務到期時間
     * @param category 任務分類
     * @param completed 任務完成狀態
     */
    public Task(String title, LocalDateTime dueTime, String category, boolean completed) {
        this(-1, title, dueTime, category, completed, -1); // 預設userId為-1
    }

    /**
     * 取得任務唯一識別碼
     * 
     * @return 任務 UID
     */
    public int getUid() {
        return uid;
    }

    /**
     * 設定任務唯一識別碼
     * 
     * @param uid 任務 UID
     */
    public void setUid(int uid) {
        this.uid = uid;
    }

    /**
     * 取得任務標題
     * 
     * @return 任務標題
     */
    public String getTitle() {
        return title;
    }

    /**
     * 設定任務標題
     * 
     * @param title 任務標題
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 取得任務到期時間
     * 
     * @return 任務到期時間
     */
    public LocalDateTime getDueTime() {
        return dueTime;
    }

    /**
     * 取得任務到期日期（別名方法，與 getDueTime() 相同）
     * 
     * @return 任務到期時間
     */
    public LocalDateTime getDueDate() {
        return dueTime;
    }

    /**
     * 設定任務到期時間
     * 
     * @param dueTime 任務到期時間
     */
    public void setDueTime(LocalDateTime dueTime) {
        this.dueTime = dueTime;
    }

    /**
     * 取得任務所屬的使用者 ID
     * 
     * @return 使用者 ID
     */
    public int getUserId() {
        return userId;
    }

    /**
     * 設定任務所屬的使用者 ID
     * 
     * @param userId 使用者 ID
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * 取得任務分類
     * 
     * @return 任務分類
     */
    public String getCategory() {
        return category;
    }

    /**
     * 設定任務分類
     * 
     * @param category 任務分類
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * 取得任務完成狀態
     * 
     * @return true 表示已完成，false 表示未完成
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * 設定任務完成狀態
     * 
     * @param completed true 表示已完成，false 表示未完成
     */
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    /**
     * 判斷任務是否為今日任務
     * 比較任務到期日期與今日日期
     * 
     * @return true 表示是今日任務，false 表示不是
     */
    public boolean isToday() {
        LocalDateTime now = LocalDateTime.now();
        return dueTime.toLocalDate().equals(now.toLocalDate());
    }

    /**
     * 判斷任務是否即將到期（在未來）
     * 
     * @return true 表示任務尚未到期，false 表示已過期
     */
    public boolean isUpcoming() {
        return dueTime.isAfter(LocalDateTime.now());
    }
    
    /**
     * 返回任務物件的字串表示
     * 
     * @return 包含任務詳細資訊的字串
     */
    @Override
    public String toString() {
        return "Task{" +
                "uid=" + uid +
                ", title='" + title + '\'' +
                ", dueTime=" + dueTime +
                ", category='" + category + '\'' +
                ", completed=" + completed +
                ", userId=" + userId +
                '}';
    }
}
