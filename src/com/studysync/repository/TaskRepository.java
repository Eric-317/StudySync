package com.studysync.repository;

import com.studysync.model.Task;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 任務資料存取層
 * 負責處理任務相關的資料庫操作
 * 
 * 主要功能：
 * - 新增任務到資料庫
 * - 查詢任務（全部、按使用者、按條件篩選）
 * - 更新任務資訊
 * - 刪除任務
 * - 標記任務完成狀態
 * 
 * 資料表結構：
 * - tasks (uid, title, due_time, category, completed, user_id)
 * 
 * @author StudySync Team
 * @version 1.0
 */
public class TaskRepository {
    /** 資料庫連線物件 */
    private final Connection conn;

    /**
     * 建構任務資料存取層
     * 
     * @param conn 資料庫連線物件
     */
    public TaskRepository(Connection conn) {
        this.conn = conn;
    }
    
    /**
     * 新增任務到資料庫
     * 
     * @param task 要新增的任務物件
     * @return 新增成功的任務 ID
     * @throws SQLException 當資料庫操作失敗時拋出
     */
    public int insert(Task task) throws SQLException {
        String sql = "INSERT INTO tasks (title, due_time, category, completed, user_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, task.getTitle());
            
            // 將 LocalDateTime 轉換為 MySQL 可接受的格式
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = task.getDueTime().format(formatter);
            stmt.setString(2, formattedDateTime);
            
            stmt.setString(3, task.getCategory());
            stmt.setBoolean(4, task.isCompleted());
            stmt.setInt(5, task.getUserId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("任務建立失敗，沒有資料被插入");
            }
            
            // 取得自動產生的主鍵並設定到任務物件中
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    task.setUid(id); // 設置生成的ID到任務物件
                    System.out.println("新任務創建成功: ID=" + id + ", 標題=" + task.getTitle());
                    return id;
                } else {
                    throw new SQLException("任務建立失敗，無法取得ID");
                }
            }
        }
    }
    
    /**
     * 查詢所有任務
     * 
     * @return 所有任務的清單
     * @throws SQLException 當資料庫操作失敗時拋出
     */
    public List<Task> findAll() throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks ORDER BY uid DESC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            while (rs.next()) {
                try {
                    int uid = rs.getInt("uid");
                    String title = rs.getString("title");
                    String dueTimeStr = rs.getString("due_time");
                    String category = rs.getString("category");
                    boolean completed = rs.getBoolean("completed");
                    int userId = rs.getInt("user_id"); // 讀取用戶ID

                    LocalDateTime dueTime;
                    try {
                        // 嘗試解析日期時間，處理可能的格式問題
                        dueTime = LocalDateTime.parse(dueTimeStr, formatter);
                    } catch (Exception e) {
                        System.err.println("日期時間格式解析錯誤: " + dueTimeStr + ", 錯誤: " + e.getMessage());
                        // 如果格式不正確，使用當前時間
                        dueTime = LocalDateTime.now();
                    }
                    
                    Task task = new Task(uid, title, dueTime, category, completed, userId);
                    tasks.add(task);
                    System.out.println("載入任務: ID=" + uid + ", 標題=" + title);
                    
                } catch (Exception e) {
                    System.err.println("解析任務時發生錯誤: " + e.getMessage());
                    e.printStackTrace();
                    // 繼續處理下一個任務
                }
            }
        }
        
        System.out.println("共載入 " + tasks.size() + " 個任務");
        return tasks;
    }
    
    // 根據用戶ID獲取任務
    public List<Task> findByUserId(int userId) throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE user_id = ? ORDER BY uid DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            while (rs.next()) {
                try {
                    int uid = rs.getInt("uid");
                    String title = rs.getString("title");
                    String dueTimeStr = rs.getString("due_time");
                    String category = rs.getString("category");
                    boolean completed = rs.getBoolean("completed");

                    LocalDateTime dueTime;
                    try {
                        dueTime = LocalDateTime.parse(dueTimeStr, formatter);
                    } catch (Exception e) {
                        System.err.println("日期時間格式解析錯誤: " + dueTimeStr + ", 錯誤: " + e.getMessage());
                        dueTime = LocalDateTime.now();
                    }
                    
                    Task task = new Task(uid, title, dueTime, category, completed, userId);
                    tasks.add(task);
                } catch (Exception e) {
                    System.err.println("解析任務時發生錯誤: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        
        System.out.println("用戶 " + userId + " 共載入 " + tasks.size() + " 個任務");
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
    }    public void update(Task task) throws SQLException {
        String sql = "UPDATE tasks SET title = ?, due_time = ?, category = ?, completed = ?, user_id = ? WHERE uid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, task.getTitle());
            
            // 將 LocalDateTime 轉換為 MySQL 可接受的格式
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = task.getDueTime().format(formatter);
            stmt.setString(2, formattedDateTime);
            
            stmt.setString(3, task.getCategory());
            stmt.setBoolean(4, task.isCompleted());
            stmt.setInt(5, task.getUserId());
            stmt.setInt(6, task.getUid());
            int affectedRows = stmt.executeUpdate();
            
            System.out.println("更新任務: ID=" + task.getUid() + ", 結果: " + (affectedRows > 0 ? "成功" : "失敗"));
        }
    }

    public List<Task> findByUserIdAndCategory(int userId, String category) throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE user_id = ? AND category = ? ORDER BY uid DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, category);
            ResultSet rs = stmt.executeQuery();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            while (rs.next()) {
                tasks.add(parseTask(rs, formatter, userId));
            }
        }
        return tasks;
    }

    public List<Task> findByUserIdAndDueDateRange(int userId, LocalDateTime start, LocalDateTime end) throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE user_id = ? AND due_time >= ? AND due_time <= ? ORDER BY uid DESC";
        DateTimeFormatter dbFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, start.format(dbFormatter));
            stmt.setString(3, end.format(dbFormatter));
            ResultSet rs = stmt.executeQuery();
            DateTimeFormatter resultFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            while (rs.next()) {
                tasks.add(parseTask(rs, resultFormatter, userId));
            }
        }
        return tasks;
    }

    public List<Task> findByUserIdCategoryAndDueDateRange(int userId, String category, LocalDateTime start, LocalDateTime end) throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE user_id = ? AND category = ? AND due_time >= ? AND due_time <= ? ORDER BY uid DESC";
        DateTimeFormatter dbFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, category);
            stmt.setString(3, start.format(dbFormatter));
            stmt.setString(4, end.format(dbFormatter));
            ResultSet rs = stmt.executeQuery();
            DateTimeFormatter resultFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            while (rs.next()) {
                tasks.add(parseTask(rs, resultFormatter, userId));
            }
        }
        return tasks;
    }

    public List<Task> findByUserIdAndStatus(int userId, boolean completed) throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE user_id = ? AND completed = ? ORDER BY uid DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setBoolean(2, completed);
            ResultSet rs = stmt.executeQuery();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            while (rs.next()) {
                tasks.add(parseTask(rs, formatter, userId));
            }
        }
        return tasks;
    }

    public List<Task> findByUserIdCategoryAndStatus(int userId, String category, boolean completed) throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE user_id = ? AND category = ? AND completed = ? ORDER BY uid DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, category);
            stmt.setBoolean(3, completed);
            ResultSet rs = stmt.executeQuery();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            while (rs.next()) {
                tasks.add(parseTask(rs, formatter, userId));
            }
        }
        return tasks;
    }

    private Task parseTask(ResultSet rs, DateTimeFormatter formatter, int userId) throws SQLException {
        int uid = rs.getInt("uid");
        String title = rs.getString("title");
        String dueTimeStr = rs.getString("due_time");
        String category = rs.getString("category");
        boolean completed = rs.getBoolean("completed");
        LocalDateTime dueTime = null;
        if (dueTimeStr != null) {
            try {
                dueTime = LocalDateTime.parse(dueTimeStr, formatter);
            } catch (Exception e) {
                System.err.println("日期時間格式解析錯誤: " + dueTimeStr + ", 錯誤: " + e.getMessage());
                // Consider how to handle parse errors, e.g., set to null or a default
            }
        }
        return new Task(uid, title, dueTime, category, completed, userId);
    }
}
