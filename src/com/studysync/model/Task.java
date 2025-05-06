package com.studysync.model;

import java.time.LocalDateTime;

public class Task {
    private int uid;
    private String title;
    private LocalDateTime dueTime;
    private String category;
    private boolean completed;

    public Task(int uid, String title, LocalDateTime dueTime, String category, boolean completed) {
        this.uid = uid;
        this.title = title;
        this.dueTime = dueTime;
        this.category = category;
        this.completed = completed;
    }

    public Task(String title, LocalDateTime dueTime, String category, boolean completed) {
        this(-1, title, dueTime, category, completed);
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getDueTime() {
        return dueTime;
    }

    public LocalDateTime getDueDate() {
        return dueTime;
    }

    public void setDueTime(LocalDateTime dueTime) {
        this.dueTime = dueTime;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isToday() {
        LocalDateTime now = LocalDateTime.now();
        return dueTime.toLocalDate().equals(now.toLocalDate());
    }

    public boolean isUpcoming() {
        return dueTime.isAfter(LocalDateTime.now());
    }
}
