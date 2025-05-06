// TaskService.java
package com.studysync.service;

import com.studysync.model.Task;
import com.studysync.repository.TaskRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void addTask(String title, String category, LocalDateTime dueTime) {
        Task task = new Task(title, dueTime, category, false);
        try {
            taskRepository.insert(task);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Task> getAllTasks() {
        try {
            return taskRepository.findAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public void deleteTask(int uid) {
        try {
            taskRepository.deleteById(uid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void markTaskCompleted(int uid) {
        try {
            taskRepository.markCompleted(uid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
