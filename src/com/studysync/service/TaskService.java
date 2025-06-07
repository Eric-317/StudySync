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
    
    public Task addTask(String title, String category, LocalDateTime dueTime, int userId) {
        Task task = new Task(title, dueTime, category, false, userId);
        try {
            int id = taskRepository.insert(task);
            task.setUid(id);
            return task;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // 保持向後兼容性
    public Task addTask(String title, String category, LocalDateTime dueTime) {
        return addTask(title, category, dueTime, -1); // 使用預設用戶ID
    }

    public List<Task> getAllTasks() {
        try {
            return taskRepository.findAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }
    
    // 根據用戶ID獲取任務
    public List<Task> getTasksByUserId(int userId) {
        try {
            return taskRepository.findByUserId(userId);
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

    public void updateTask(Task task) {
        try {
            taskRepository.update(task);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Task> getTasks(int userId, String filterType, String category) {
        try {
            // This is a placeholder. You need to implement the actual filtering logic
            // in TaskRepository or here in TaskService based on filterType and category.
            // For now, it defaults to returning all tasks for the user if not 'all' or specific category.
            
            // Example of how you might start to structure the logic:
            if ("all".equalsIgnoreCase(filterType)) {
                if ("全部分類".equalsIgnoreCase(category) || category == null || category.trim().isEmpty()) {
                    return taskRepository.findByUserId(userId); // All tasks for the user
                } else {
                    return taskRepository.findByUserIdAndCategory(userId, category); // Filter by specific category
                }
            } else if ("today".equalsIgnoreCase(filterType)) {
                // You'll need a method in TaskRepository like findByUserIdAndDueDateRange
                // or filter them here after fetching all tasks for the user.
                // This is a simplified example:
                LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
                LocalDateTime endOfDay = LocalDateTime.now().toLocalDate().atTime(23, 59, 59);
                if ("全部分類".equalsIgnoreCase(category) || category == null || category.trim().isEmpty()) {
                    return taskRepository.findByUserIdAndDueDateRange(userId, startOfDay, endOfDay);
                } else {
                    return taskRepository.findByUserIdCategoryAndDueDateRange(userId, category, startOfDay, endOfDay);
                }
            } else if ("completed".equalsIgnoreCase(filterType)) {
                if ("全部分類".equalsIgnoreCase(category) || category == null || category.trim().isEmpty()) {
                    return taskRepository.findByUserIdAndStatus(userId, true);
                } else {
                    return taskRepository.findByUserIdCategoryAndStatus(userId, category, true);
                }
            }
            // Fallback or if filterType is a specific category name (if you choose to support that here)
            return taskRepository.findByUserIdAndCategory(userId, category); // Default to category filter if filterType is not recognized

        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
