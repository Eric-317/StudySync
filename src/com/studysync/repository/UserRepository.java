package com.studysync.repository;

import com.studysync.model.User;

import java.sql.*;

public class UserRepository {
    private final Connection conn;

    public UserRepository(Connection conn) {
        this.conn = conn;
    }

    public void insert(User user) throws SQLException {
        String sql = "INSERT INTO users (email, password, birth_date) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getBirthDate());
            stmt.executeUpdate();
        }
    }

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
    
    public boolean updateBirthDate(int userId, String newBirthDate) throws SQLException {
        String sql = "UPDATE users SET birth_date = ? WHERE uid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newBirthDate);
            stmt.setInt(2, userId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
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
