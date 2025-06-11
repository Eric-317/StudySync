package com.studysync.model;

import java.time.LocalTime;

/**
 * 行事曆事件資料模型類
 * 代表系統中的一個行事曆事件，可以是一般事件或任務
 * 
 * 此類用於：
 * - 儲存事件的基本資訊（時間、描述、類型等）
 * - 在不同層之間傳遞事件資料
 * - 提供事件資料的存取方法
 * - 支援全天事件和指定時間事件
 * 
 * @author StudySync Team
 * @version 1.0
 */
public class CalendarEvent {
    /** 事件時間（可為 null 表示全天事件） */
    private LocalTime time;
    
    /** 事件描述 */
    private String description;
    
    /** 是否為任務（用於區分一般事件和任務） */
    private boolean isTask;
    
    /** 事件唯一識別碼 */
    private int id;
    
    /** 事件所屬的使用者 ID */
    private int userId;

    /**
     * 建構基本事件物件（不指定 ID 和使用者 ID）
     * 
     * @param time 事件時間
     * @param description 事件描述
     * @param isTask 是否為任務
     */
    public CalendarEvent(LocalTime time, String description, boolean isTask) {
        this(-1, time, description, isTask, -1);
    }

    /**
     * 建構事件物件（指定 ID，不指定使用者 ID）
     * 
     * @param id 事件唯一識別碼
     * @param time 事件時間
     * @param description 事件描述
     * @param isTask 是否為任務
     */
    public CalendarEvent(int id, LocalTime time, String description, boolean isTask) {
        this(id, time, description, isTask, -1);
    }

    /**
     * 建構完整的事件物件
     * 
     * @param id 事件唯一識別碼
     * @param time 事件時間（可為 null 表示全天事件）
     * @param description 事件描述
     * @param isTask 是否為任務
     * @param userId 事件所屬的使用者 ID
     */
    public CalendarEvent(int id, LocalTime time, String description, boolean isTask, int userId) {
        this.id = id;
        this.time = time;
        this.description = description;
        this.isTask = isTask;
        this.userId = userId;
    }

    /**
     * 建構事件物件（不指定 ID）
     * 
     * @param time 事件時間
     * @param description 事件描述
     * @param isTask 是否為任務
     * @param userId 事件所屬的使用者 ID
     */
    public CalendarEvent(LocalTime time, String description, boolean isTask, int userId) {
        this(-1, time, description, isTask, userId);
    }

    /**
     * 取得事件時間
     * 
     * @return 事件時間，null 表示全天事件
     */
    public LocalTime getTime() {
        return time;
    }

    /**
     * 取得事件描述
     * 
     * @return 事件描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 判斷是否為任務
     * 
     * @return true 表示是任務，false 表示是一般事件
     */
    public boolean isTask() {
        return isTask;
    }

    /**
     * 取得事件唯一識別碼
     * 
     * @return 事件 ID
     */
    public int getId() {
        return id;
    }

    /**
     * 設定事件唯一識別碼
     * 
     * @param id 事件 ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * 設定事件時間
     * 
     * @param time 事件時間，可為 null 表示全天事件
     */
    public void setTime(LocalTime time) {
        this.time = time;
    }

    /**
     * 設定事件描述
     * 
     * @param description 事件描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 取得事件所屬的使用者 ID
     * 
     * @return 使用者 ID
     */
    public int getUserId() {
        return userId;
    }

    /**
     * 設定事件所屬的使用者 ID
     * 
     * @param userId 使用者 ID
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    /**
     * 設定是否為任務
     * 
     * @param isTask true 表示是任務，false 表示是一般事件
     */
    public void setTask(boolean isTask) {
        this.isTask = isTask;
    }

    /**
     * 返回事件物件的字串表示
     * 包含時間、描述、類型等資訊
     * 
     * @return 包含事件詳細資訊的字串
     */
    @Override
    public String toString() {
        return (time != null ? time.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")) : "All-day")
            + " - " + description + (isTask ? " (Task)" : "") + " [id=" + id + "]";
    }
}
