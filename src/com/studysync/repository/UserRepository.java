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
}
