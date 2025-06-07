package com.studysync.controller;

import com.studysync.model.CalendarEvent;
import com.studysync.model.User;
import com.studysync.repository.CalendarEventRepository;
import com.studysync.service.CalendarEventService;
import com.studysync.util.DBUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class CalendarEventController {
    private final CalendarEventService eventService;
    private User currentUser; // 添加當前用戶引用

    public CalendarEventController() {
        try {
            Connection conn = DBUtil.getConnection();
            CalendarEventRepository repo = new CalendarEventRepository(conn);
            this.eventService = new CalendarEventService(repo);
        } catch (SQLException e) {
            throw new RuntimeException("資料庫連線失敗", e);
        }
    }
    
    // 設置當前用戶
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    // 獲取當前用戶
    public User getCurrentUser() {
        return currentUser;
    }

    public CalendarEvent addEvent(LocalDate date, CalendarEvent event) {
        if (currentUser != null) {
            event.setUserId(currentUser.getUid()); // 設置事件的用戶ID
        } else {
            System.err.println("錯誤：嘗試在未登入狀態下添加事件");
            return null;
        }
        return eventService.addEvent(date, event);
    }

    public List<CalendarEvent> getEventsByDate(LocalDate date) {
        if (currentUser != null) {
            return eventService.getEventsByDateAndUserId(date, currentUser.getUid());
        } else {
            System.err.println("錯誤：嘗試在未登入狀態下獲取事件");
            return List.of();
        }
    }

    public void updateEvent(CalendarEvent event) {
        if (currentUser != null) {
            event.setUserId(currentUser.getUid()); // 確保事件仍然屬於當前用戶
            eventService.updateEvent(event);
        } else {
            System.err.println("錯誤：嘗試在未登入狀態下更新事件");
        }
    }

    public void deleteEvent(int id) {
        eventService.deleteEvent(id);
    }
}
