package com.studysync.repository;

import com.studysync.model.CalendarEvent;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 行事曆事件資料存取層
 * 負責處理行事曆事件相關的資料庫操作
 * 
 * 主要功能：
 * - 新增行事曆事件到資料庫
 * - 根據日期查詢事件
 * - 根據日期和使用者查詢事件
 * - 更新事件資訊
 * - 刪除事件
 * - 自動建立事件資料表
 * 
 * 資料表結構：
 * - events (id, event_date, event_time, description, event_type, user_id)
 * 
 * @author StudySync Team
 * @version 1.0
 */
public class CalendarEventRepository {
    /** 資料庫連線物件 */
    private final Connection conn;

    /**
     * 建構行事曆事件資料存取層
     * 自動檢查並建立 events 資料表
     * 
     * @param conn 資料庫連線物件
     */    public CalendarEventRepository(Connection conn) {
        this.conn = conn;
        // 確保 events 資料表存在，如果不存在則自動建立
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS events ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "event_date DATE NOT NULL, "
                    + "event_time TIME, "
                    + "description VARCHAR(255), "
                    + "event_type VARCHAR(50) DEFAULT 'General', "
                    + "user_id INT, "
                    + "FOREIGN KEY (user_id) REFERENCES users(uid) ON DELETE CASCADE"
                    + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 新增行事曆事件到資料庫
     * 
     * @param event 要新增的事件物件
     * @param date 事件日期
     * @return 新增成功的事件物件（包含自動產生的 ID）
     * @throws SQLException 當資料庫操作失敗時拋出
     */
    public CalendarEvent insert(CalendarEvent event, LocalDate date) throws SQLException {
        String sql = "INSERT INTO events (event_date, event_time, description, user_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDate(1, Date.valueOf(date));
            if (event.getTime() != null) {
                stmt.setTime(2, Time.valueOf(event.getTime()));
            } else {
                stmt.setNull(2, Types.TIME);
            }
            stmt.setString(3, event.getDescription());
            stmt.setInt(4, event.getUserId());
            stmt.executeUpdate();
            
            // 取得自動產生的主鍵並設定到事件物件中
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    event.setId(keys.getInt(1));
                }
            }
            return event;
        }
    }

    /**
     * 根據日期查詢所有事件
     * 
     * @param date 要查詢的日期
     * @return 該日期的所有事件清單，按時間排序
     * @throws SQLException 當資料庫操作失敗時拋出
     */
    public List<CalendarEvent> findByDate(LocalDate date) throws SQLException {
        String sql = "SELECT id, event_time, description, user_id FROM events WHERE event_date = ? ORDER BY event_time";
        List<CalendarEvent> events = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(date));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    Time time = rs.getTime("event_time");
                    LocalTime localTime = time != null ? time.toLocalTime() : null;
                    String desc = rs.getString("description");
                    int userId = rs.getInt("user_id");
                    CalendarEvent evt = new CalendarEvent(id, localTime, desc, false, userId);
                    events.add(evt);
                }
            }
        }
        return events;
    }

    /**
     * 根據日期和使用者 ID 查詢事件
     * 只返回指定使用者的事件
     * 
     * @param date 要查詢的日期
     * @param userId 使用者 ID
     * @return 該使用者在指定日期的事件清單，按時間排序
     * @throws SQLException 當資料庫操作失敗時拋出
     */
    public List<CalendarEvent> findByDateAndUserId(LocalDate date, int userId) throws SQLException {
        String sql = "SELECT id, event_time, description FROM events WHERE event_date = ? AND user_id = ? ORDER BY event_time";
        List<CalendarEvent> events = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(date));
            stmt.setInt(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    Time time = rs.getTime("event_time");
                    LocalTime localTime = time != null ? time.toLocalTime() : null;
                    String desc = rs.getString("description");
                    CalendarEvent evt = new CalendarEvent(id, localTime, desc, false, userId);
                    events.add(evt);
                }
            }
        }
        return events;
    }

    /**
     * 更新事件資訊
     * 
     * @param event 包含更新資料的事件物件
     * @throws SQLException 當資料庫操作失敗時拋出
     */
    public void update(CalendarEvent event) throws SQLException {
        String sql = "UPDATE events SET event_time = ?, description = ?, user_id = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (event.getTime() != null) {
                stmt.setTime(1, Time.valueOf(event.getTime()));
            } else {
                stmt.setNull(1, Types.TIME);
            }
            stmt.setString(2, event.getDescription());
            stmt.setInt(3, event.getUserId());
            stmt.setInt(4, event.getId());
            stmt.executeUpdate();
        }
    }

    /**
     * 根據事件 ID 刪除事件
     * 
     * @param id 要刪除的事件 ID
     * @throws SQLException 當資料庫操作失敗時拋出
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM events WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
