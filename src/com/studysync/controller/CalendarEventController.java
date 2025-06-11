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

/**
 * 行事曆事件控制器類
 * 負責處理與行事曆事件相關的業務邏輯控制
 * 作為 View 層和 Service 層之間的橋樑
 * 
 * 主要功能：
 * - 處理事件的新增、刪除、更新
 * - 提供事件查詢和篩選功能
 * - 管理使用者權限控制（確保事件屬於正確的使用者）
 * - 處理多使用者環境下的事件隔離
 * 
 * @author StudySync Team
 * @version 1.0
 */
public class CalendarEventController {
    /** 行事曆事件服務層實例 */
    private final CalendarEventService eventService;
    
    /** 目前登入的使用者 */
    private User currentUser;

    /**
     * 建構行事曆事件控制器
     * 初始化資料庫連線和相關服務
     * 
     * @throws RuntimeException 當資料庫連線失敗時拋出
     */
    public CalendarEventController() {
        try {
            Connection conn = DBUtil.getConnection();
            CalendarEventRepository repo = new CalendarEventRepository(conn);
            this.eventService = new CalendarEventService(repo);
        } catch (SQLException e) {
            throw new RuntimeException("資料庫連線失敗", e);
        }
    }    
    /**
     * 設定目前登入的使用者
     * 用於確保事件操作的權限控制
     * 
     * @param user 目前登入的使用者
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    /**
     * 取得目前登入的使用者
     * 
     * @return 目前登入的使用者，如果未登入則返回 null
     */
    public User getCurrentUser() {
        return currentUser;
    }    /**
     * 新增行事曆事件
     * 只有登入使用者才能新增事件，自動設定事件的使用者 ID
     * 
     * @param date 事件日期
     * @param event 要新增的事件物件
     * @return 新增成功的事件物件，失敗時返回 null
     */
    public CalendarEvent addEvent(LocalDate date, CalendarEvent event) {
        if (currentUser != null) {
            event.setUserId(currentUser.getUid()); // 設置事件的用戶ID
        } else {
            System.err.println("錯誤：嘗試在未登入狀態下添加事件");
            return null;
        }
        return eventService.addEvent(date, event);
    }

    /**
     * 根據日期取得事件
     * 只返回目前登入使用者的事件
     * 
     * @param date 要查詢的日期
     * @return 該日期的使用者事件清單，如果未登入則返回空清單
     */
    public List<CalendarEvent> getEventsByDate(LocalDate date) {
        if (currentUser != null) {
            return eventService.getEventsByDateAndUserId(date, currentUser.getUid());
        } else {
            System.err.println("錯誤：嘗試在未登入狀態下獲取事件");
            return List.of();
        }
    }    /**
     * 更新事件資訊
     * 確保事件仍然屬於目前登入的使用者
     * 
     * @param event 包含更新資料的事件物件
     */
    public void updateEvent(CalendarEvent event) {
        if (currentUser != null) {
            event.setUserId(currentUser.getUid()); // 確保事件仍然屬於當前用戶
            eventService.updateEvent(event);
        } else {
            System.err.println("錯誤：嘗試在未登入狀態下更新事件");
        }
    }

    /**
     * 刪除事件
     * 
     * @param id 要刪除的事件 ID
     */
    public void deleteEvent(int id) {
        eventService.deleteEvent(id);
    }
}
