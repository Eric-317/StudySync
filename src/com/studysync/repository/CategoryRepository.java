package com.studysync.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 分類資料存取層
 * 負責處理任務分類相關的資料庫操作
 * 
 * 主要功能：
 * - 查詢所有分類
 * - 新增分類（重複名稱會被忽略）
 * - 更新分類名稱（檢查重複性）
 * - 刪除分類
 * 
 * 資料表結構：
 * - categories (id, name)
 * 
 * @author StudySync Team
 * @version 1.0
 */
public class CategoryRepository {
    /** 資料庫連線物件 */
    private final Connection conn;

    /**
     * 建構分類資料存取層
     * 
     * @param conn 資料庫連線物件
     */
    public CategoryRepository(Connection conn) {
        this.conn = conn;
    }    /**
     * 查詢所有分類
     * 
     * @return 所有分類名稱的清單，按 ID 排序
     * @throws SQLException 當資料庫操作失敗時拋出
     */
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

    /**
     * 新增分類
     * 使用 INSERT IGNORE 避免重複名稱造成錯誤
     * 
     * @param name 分類名稱
     * @throws SQLException 當資料庫操作失敗時拋出
     */
    public void insert(String name) throws SQLException {
        String sql = "INSERT IGNORE INTO categories (name) VALUES (?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
        }
    }    /**
     * 更新分類名稱
     * 先檢查新名稱是否已存在，避免重複
     * 
     * @param oldName 舊的分類名稱
     * @param newName 新的分類名稱
     * @return true 表示更新成功，false 表示新名稱已存在或更新失敗
     * @throws SQLException 當資料庫操作失敗時拋出
     */
    public boolean update(String oldName, String newName) throws SQLException {
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

    /**
     * 刪除分類
     * 
     * @param name 要刪除的分類名稱
     * @throws SQLException 當資料庫操作失敗時拋出
     */
    public void delete(String name) throws SQLException {
        String sql = "DELETE FROM categories WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
        }
    }
}