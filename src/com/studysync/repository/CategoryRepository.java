package com.studysync.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {
    private final Connection conn;

    public CategoryRepository(Connection conn) {
        this.conn = conn;
    }

    public List<String> findAll() throws SQLException {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT name FROM categories ORDER BY id";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                categories.add(rs.getString("name"));
            }
        }
        return categories;
    }

    public void insert(String name) throws SQLException {
        String sql = "INSERT IGNORE INTO categories (name) VALUES (?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
        }
    }    public boolean update(String oldName, String newName) throws SQLException {
        // 先檢查新名稱是否已存在
        String checkSql = "SELECT COUNT(*) FROM categories WHERE name = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, newName);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return false; // 新名稱已存在，取消更新
                }
            }
        }
        
        // 執行更新操作
        String sql = "UPDATE categories SET name = ? WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newName);
            stmt.setString(2, oldName);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public void delete(String name) throws SQLException {
        String sql = "DELETE FROM categories WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
        }
    }
}