package com.studysync.service;

import com.studysync.model.CalendarEvent;
import com.studysync.repository.CalendarEventRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class CalendarEventService {
    private final CalendarEventRepository repository;

    public CalendarEventService(CalendarEventRepository repository) {
        this.repository = repository;
    }

    public CalendarEvent addEvent(LocalDate date, CalendarEvent event) {
        try {
            return repository.insert(event, date);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert event", e);
        }
    }

    public List<CalendarEvent> getEventsByDate(LocalDate date) {
        try {
            return repository.findByDate(date);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load events", e);
        }
    }
    
    // 根據日期和用戶ID獲取事件
    public List<CalendarEvent> getEventsByDateAndUserId(LocalDate date, int userId) {
        try {
            return repository.findByDateAndUserId(date, userId);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load events for user", e);
        }
    }

    public void updateEvent(CalendarEvent event) {
        try {
            repository.update(event);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update event", e);
        }
    }

    public void deleteEvent(int id) {
        try {
            repository.delete(id);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete event", e);
        }
    }
}
