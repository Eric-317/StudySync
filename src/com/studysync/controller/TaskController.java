package com.studysync.controller;

import com.studysync.model.Task;
import com.studysync.model.User;
import com.studysync.repository.CategoryRepository;
import com.studysync.repository.TaskRepository;
import com.studysync.service.CategoryService;
import com.studysync.service.TaskService;
import com.studysync.util.DBUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class TaskController {
    private final TaskService taskService;
    private final CategoryService categoryService;
    private User currentUser;  // 添加當前用戶引用
    
    public TaskController() {
        try {
            Connection conn = DBUtil.getConnection();
            TaskRepository taskRepository = new TaskRepository(conn);
            CategoryRepository categoryRepository = new CategoryRepository(conn);
            this.taskService = new TaskService(taskRepository);
            this.categoryService = new CategoryService(categoryRepository);
        } catch (SQLException e) {
            throw new RuntimeException("資料庫連線失敗", e);
        }
    }
    
    public CategoryService getCategoryService() {
        return categoryService;
    }
    
    // 設置當前用戶
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    // 獲取當前用戶
    public User getCurrentUser() {
        return currentUser;
    }
    
    public Task addTask(String title, String category, LocalDateTime dueTime) {
        if (currentUser != null) {
            return taskService.addTask(title, category, dueTime, currentUser.getUid());
        } else {
            // 用戶未登入時，無法添加任務
            System.err.println("錯誤：嘗試在未登入狀態下添加任務");
            return null;
        }
    }

    public List<Task> getAllTasks() {
        if (currentUser != null) {
            return taskService.getTasksByUserId(currentUser.getUid());
        } else {
            // 用戶未登入時，返回空列表
            System.err.println("錯誤：嘗試在未登入狀態下獲取任務");
            return List.of();
        }
    }

    public List<Task> getTasks(String filterType, String category, User user) {
        if (user == null) {
            System.err.println("錯誤：用戶未登入，無法獲取任務");
            return List.of();
        }
        return taskService.getTasks(user.getUid(), filterType, category);
    }

    public void deleteTask(int uid) {
        taskService.deleteTask(uid);
    }

    public void markTaskCompleted(int uid) {
        taskService.markTaskCompleted(uid);
    }

    public void updateTask(Task task) {
        taskService.updateTask(task);
    }
    
    public void updateTasksCategory(String oldCategory, String newCategory) {
        List<Task> tasks = getAllTasks();
        for (Task task : tasks) {
            if (task.getCategory() != null && task.getCategory().equals(oldCategory)) {
                task.setCategory(newCategory);
                updateTask(task);
            }
        }
    }
}
