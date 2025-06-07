package com.studysync.model;

import java.time.LocalTime;

public class CalendarEvent {
    private LocalTime time;
    private String description;
    private boolean isTask; // To differentiate between general events and tasks
    private int id; // 新增 id
    private int userId; // 添加用戶ID字段

    public CalendarEvent(LocalTime time, String description, boolean isTask) {
        this(-1, time, description, isTask, -1);
    }

    // 新增帶 id 的建構子
    public CalendarEvent(int id, LocalTime time, String description, boolean isTask) {
        this(id, time, description, isTask, -1);
    }

    // 新增帶 userId 的建構子
    public CalendarEvent(int id, LocalTime time, String description, boolean isTask, int userId) {
        this.id = id;
        this.time = time;
        this.description = description;
        this.isTask = isTask;
        this.userId = userId;
    }

    public CalendarEvent(LocalTime time, String description, boolean isTask, int userId) {
        this(-1, time, description, isTask, userId);
    }

    public LocalTime getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public boolean isTask() {
        return isTask;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public void setTask(boolean isTask) {
        this.isTask = isTask;
    }

    @Override
    public String toString() {
        return (time != null ? time.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")) : "All-day")
            + " - " + description + (isTask ? " (Task)" : "") + " [id=" + id + "]";
    }
}
