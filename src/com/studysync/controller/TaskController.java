package com.studysync.controller;

import com.studysync.model.Task;
import com.studysync.repository.TaskRepository;
import com.studysync.service.TaskService;
import com.studysync.util.DBUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class TaskController {
    private final TaskService taskService;

    public TaskController() {
        try {
            Connection conn = DBUtil.getConnection();
            TaskRepository taskRepository = new TaskRepository(conn);
            this.taskService = new TaskService(taskRepository);
        } catch (SQLException e) {
            throw new RuntimeException("資料庫連線失敗", e);
        }
    }

    public void addTask(String title, String category, LocalDateTime dueTime) {
        taskService.addTask(title, category, dueTime);
    }

    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    public void deleteTask(int uid) {
        taskService.deleteTask(uid);
    }

    public void markTaskCompleted(int uid) {
        taskService.markTaskCompleted(uid);
    }
}
