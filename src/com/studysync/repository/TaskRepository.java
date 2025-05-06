package com.studysync.repository;

import com.studysync.model.Task;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TaskRepository {
    private final Connection conn;

    public TaskRepository(Connection conn) {
        this.conn = conn;
    }

    public void insert(Task task) throws SQLException {
        String sql = "INSERT INTO tasks (title, due_time, category, completed) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, task.getTitle());
            stmt.setString(2, task.getDueTime().toString());
            stmt.setString(3, task.getCategory());
            stmt.setBoolean(4, task.isCompleted());
            stmt.executeUpdate();
        }
    }

    public List<Task> findAll() throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            while (rs.next()) {
                int uid = rs.getInt("uid");
                String title = rs.getString("title");
                String dueTimeStr = rs.getString("due_time");
                String category = rs.getString("category");
                boolean completed = rs.getBoolean("completed");

                LocalDateTime dueTime = LocalDateTime.parse(dueTimeStr, formatter);
                tasks.add(new Task(uid, title, dueTime, category, completed));
            }
        }
        return tasks;
    }

    public void deleteById(int uid) throws SQLException {
        String sql = "DELETE FROM tasks WHERE uid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, uid);
            stmt.executeUpdate();
        }
    }

    public void markCompleted(int uid) throws SQLException {
        String sql = "UPDATE tasks SET completed = 1 WHERE uid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, uid);
            stmt.executeUpdate();
        }
    }
}
