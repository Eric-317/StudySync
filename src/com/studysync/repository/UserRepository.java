package com.studysync.repository;

import com.studysync.model.User;

import java.sql.*;

/**
 * 使用者資料存取層
 * 負責處理使用者相關的資料庫操作
 * 
 * 主要功能：
 * - 新增使用者到資料庫
 * - 根據電子郵件或 ID 查詢使用者
 * - 更新使用者密碼（包含舊密碼驗證）
 * - 更新使用者出生日期
 * 
 * 資料表結構：
 * - users (uid, email, password, birth_date)
 * 
 * @author StudySync Team
 * @version 1.0
 */
public class UserRepository {
    /** 資料庫連線物件 */
    private final Connection conn;

    /**
     * 建構使用者資料存取層
     * 
     * @param conn 資料庫連線物件
     */
    public UserRepository(Connection conn) {
        this.conn = conn;
    }

    /**
     * 新增使用者到資料庫
     * 
     * @param user 要新增的使用者物件
     * @throws SQLException 當資料庫操作失敗時拋出
     */
    public void insert(User user) throws SQLException {
        String sql = "INSERT INTO users (email, password, birth_date) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getBirthDate());
            stmt.executeUpdate();
        }
    }

    /**
     * 根據電子郵件查詢使用者
     * 
     * @param email 使用者電子郵件
     * @return 查詢到的 User 物件，如果不存在則返回 null
     * @throws SQLException 當資料庫操作失敗時拋出
     */
    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int uid = rs.getInt("uid");
                    String pw = rs.getString("password");
                    String birth = rs.getString("birth_date");
                    return new User(uid, email, pw, birth);
                }
            }
        }
        return null;
    }
    
    /**
     * 更新使用者密碼
     * 先驗證舊密碼是否正確，再更新為新密碼
     * 
     * @param userId 使用者 ID
     * @param oldPassword 舊密碼
     * @param newPassword 新密碼
     * @return true 表示更新成功，false 表示舊密碼錯誤或使用者不存在
     * @throws SQLException 當資料庫操作失敗時拋出
     */
    public boolean updatePassword(int userId, String oldPassword, String newPassword) throws SQLException {
        // 先驗證舊密碼是否正確
        String checkSql = "SELECT password FROM users WHERE uid = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, userId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    String currentPassword = rs.getString("password");
                    if (!currentPassword.equals(oldPassword)) {
                        return false; // 舊密碼不正確
                    }
                } else {
                    return false; // 用戶不存在
                }
            }
        }
        
        // 更新密碼
        String updateSql = "UPDATE users SET password = ? WHERE uid = ?";
        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
            updateStmt.setString(1, newPassword);
            updateStmt.setInt(2, userId);
            int rowsAffected = updateStmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * 更新使用者出生日期
     * 
     * @param userId 使用者 ID
     * @param newBirthDate 新的出生日期
     * @return true 表示更新成功，false 表示更新失敗
     * @throws SQLException 當資料庫操作失敗時拋出
     */
    public boolean updateBirthDate(int userId, String newBirthDate) throws SQLException {
        String sql = "UPDATE users SET birth_date = ? WHERE uid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newBirthDate);
            stmt.setInt(2, userId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * 根據使用者 ID 查詢使用者
     * 
     * @param userId 使用者 ID
     * @return 查詢到的 User 物件，如果不存在則返回 null
     * @throws SQLException 當資料庫操作失敗時拋出
     */
    public User findById(int userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE uid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String email = rs.getString("email");
                    String pw = rs.getString("password");
                    String birth = rs.getString("birth_date");
                    return new User(userId, email, pw, birth);
                }
            }
        }
        return null;
    }
}
