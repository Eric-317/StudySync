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

/**
 * 任務控制器類
 * 負責處理與任務管理相關的業務邏輯控制
 * 作為 View 層和 Service 層之間的橋樑
 * 
 * 主要功能：
 * - 處理任務的新增、刪除、更新
 * - 管理任務的完成狀態
 * - 提供任務查詢和篩選功能
 * - 處理任務分類管理
 * - 維護目前登入使用者的任務操作權限
 * 
 * @author StudySync Team
 * @version 1.0
 */
public class TaskController {
    /** 任務服務層實例 */
    private final TaskService taskService;
    
    /** 分類服務層實例 */
    private final CategoryService categoryService;
    
    /** 目前登入的使用者 */
    private User currentUser;
    
    /**
     * 建構任務控制器
     * 初始化資料庫連線和相關服務
     * 
     * @throws RuntimeException 當資料庫連線失敗時拋出
     */
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
    
    /**
     * 取得分類服務實例
     * 
     * @return CategoryService 實例
     */
    public CategoryService getCategoryService() {
        return categoryService;
    }
    
    /**
     * 設定目前登入的使用者
     * 用於確保任務操作的權限控制
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
    }
    
    /**
     * 新增任務
     * 只有登入使用者才能新增任務
     * 
     * @param title 任務標題
     * @param category 任務分類
     * @param dueTime 任務到期時間
     * @return 新增成功的 Task 物件，失敗時返回 null
     */
    public Task addTask(String title, String category, LocalDateTime dueTime) {
        if (currentUser != null) {
            return taskService.addTask(title, category, dueTime, currentUser.getUid());
        } else {
            // 用戶未登入時，無法添加任務
            System.err.println("錯誤：嘗試在未登入狀態下添加任務");
            return null;
        }
    }

    /**
     * 取得目前使用者的所有任務
     * 
     * @return 任務清單，如果使用者未登入則返回空清單
     */
    public List<Task> getAllTasks() {
        if (currentUser != null) {
            return taskService.getTasksByUserId(currentUser.getUid());
        } else {
            // 用戶未登入時，返回空列表
            System.err.println("錯誤：嘗試在未登入狀態下獲取任務");
            return List.of();
        }
    }

    /**
     * 根據篩選條件取得任務
     * 
     * @param filterType 篩選類型（如：今日、即將到期等）
     * @param category 任務分類
     * @param user 使用者物件
     * @return 符合條件的任務清單
     */
    public List<Task> getTasks(String filterType, String category, User user) {
        if (user == null) {
            System.err.println("錯誤：用戶未登入，無法獲取任務");
            return List.of();
        }
        return taskService.getTasks(user.getUid(), filterType, category);
    }

    /**
     * 刪除任務
     * 
     * @param uid 任務的唯一識別碼
     */
    public void deleteTask(int uid) {
        taskService.deleteTask(uid);
    }

    /**
     * 將任務標記為已完成
     * 
     * @param uid 任務的唯一識別碼
     */
    public void markTaskCompleted(int uid) {
        taskService.markTaskCompleted(uid);
    }

    /**
     * 更新任務資訊
     * 
     * @param task 要更新的任務物件
     */
    public void updateTask(Task task) {
        taskService.updateTask(task);
    }
    
    /**
     * 批次更新任務分類
     * 當使用者修改分類名稱時，更新所有相關任務的分類
     * 
     * @param oldCategory 舊的分類名稱
     * @param newCategory 新的分類名稱
     */
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
