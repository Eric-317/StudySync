package com.studysync.repository;

import com.studysync.model.CalendarEvent;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class CalendarEventRepository {
    private final Connection conn;

    public CalendarEventRepository(Connection conn) {
        this.conn = conn;
        // 確保 events 資料表存在
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
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    event.setId(keys.getInt(1));
                }
            }
            return event;
        }
    }

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

    // 根據日期和用戶ID查找事件
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

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM events WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
