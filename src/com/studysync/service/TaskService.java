// TaskService.java
package com.studysync.service;

import com.studysync.model.Task;
import com.studysync.repository.TaskRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 任務服務類
 * 提供任務相關的業務邏輯服務
 * 
 * 主要功能：
 * - 處理任務新增邏輯
 * - 提供任務查詢服務（全部、按使用者、按條件篩選）
 * - 處理任務更新和刪除
 * - 管理任務完成狀態
 * - 提供任務統計資訊
 * 
 * 業務規則：
 * - 新任務預設為未完成狀態
 * - 任務必須屬於特定使用者
 * - 支援向後相容性
 * 
 * @author StudySync Team
 * @version 1.0
 */
public class TaskService {
    /** 任務資料存取層實例 */
    private final TaskRepository taskRepository;

    /**
     * 建構任務服務
     * 
     * @param taskRepository 任務資料存取層實例
     */
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
    
    /**
     * 新增任務服務
     * 建立新任務並儲存到資料庫
     * 
     * @param title 任務標題
     * @param category 任務分類
     * @param dueTime 任務到期時間
     * @param userId 任務所屬的使用者 ID
     * @return 新增成功的 Task 物件，失敗時返回 null
     */
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
    
    /**
     * 新增任務服務（保持向後相容性）
     * 使用預設使用者 ID (-1)
     * 
     * @param title 任務標題
     * @param category 任務分類
     * @param dueTime 任務到期時間
     * @return 新增成功的 Task 物件，失敗時返回 null
     */
    public Task addTask(String title, String category, LocalDateTime dueTime) {
        return addTask(title, category, dueTime, -1); // 使用預設用戶ID
    }

    /**
     * 取得所有任務
     * 
     * @return 所有任務的清單，失敗時返回空清單
     */
    public List<Task> getAllTasks() {
        try {
            return taskRepository.findAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }
    
    /**
     * 根據使用者 ID 取得任務
     * 只返回指定使用者的任務
     * 
     * @param userId 使用者 ID
     * @return 該使用者的任務清單，失敗時返回空清單
     */
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
