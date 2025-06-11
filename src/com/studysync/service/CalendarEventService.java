package com.studysync.service;

import com.studysync.model.CalendarEvent;
import com.studysync.repository.CalendarEventRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * 行事曆事件服務類
 * 提供行事曆事件相關的業務邏輯服務
 * 
 * 主要功能：
 * - 處理事件新增邏輯
 * - 提供事件查詢服務（按日期、按使用者）
 * - 處理事件更新和刪除
 * - 封裝資料存取層的 SQL 異常為運行時異常
 * 
 * 業務規則：
 * - 所有資料庫異常都會被轉換為 RuntimeException
 * - 支援多使用者環境的事件隔離
 * 
 * @author StudySync Team
 * @version 1.0
 */
public class CalendarEventService {
    /** 行事曆事件資料存取層實例 */
    private final CalendarEventRepository repository;

    /**
     * 建構行事曆事件服務
     * 
     * @param repository 行事曆事件資料存取層實例
     */
    public CalendarEventService(CalendarEventRepository repository) {
        this.repository = repository;
    }    /**
     * 新增行事曆事件服務
     * 
     * @param date 事件日期
     * @param event 要新增的事件物件
     * @return 新增成功的事件物件（包含自動產生的 ID）
     * @throws RuntimeException 當資料庫操作失敗時拋出
     */
    public CalendarEvent addEvent(LocalDate date, CalendarEvent event) {
        try {
            return repository.insert(event, date);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert event", e);
        }
    }

    /**
     * 根據日期取得所有事件
     * 
     * @param date 要查詢的日期
     * @return 該日期的所有事件清單
     * @throws RuntimeException 當資料庫操作失敗時拋出
     */
    public List<CalendarEvent> getEventsByDate(LocalDate date) {
        try {
            return repository.findByDate(date);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load events", e);
        }
    }
    
    /**
     * 根據日期和使用者 ID 取得事件
     * 只返回指定使用者的事件，提供多使用者隔離
     * 
     * @param date 要查詢的日期
     * @param userId 使用者 ID
     * @return 該使用者在指定日期的事件清單
     * @throws RuntimeException 當資料庫操作失敗時拋出
     */
    public List<CalendarEvent> getEventsByDateAndUserId(LocalDate date, int userId) {
        try {
            return repository.findByDateAndUserId(date, userId);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load events for user", e);
        }
    }    /**
     * 更新事件資訊
     * 
     * @param event 包含更新資料的事件物件
     * @throws RuntimeException 當資料庫操作失敗時拋出
     */
    public void updateEvent(CalendarEvent event) {
        try {
            repository.update(event);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update event", e);
        }
    }

    /**
     * 刪除事件
     * 
     * @param id 要刪除的事件 ID
     * @throws RuntimeException 當資料庫操作失敗時拋出
     */
    public void deleteEvent(int id) {
        try {
            repository.delete(id);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete event", e);
        }
    }
}
