package com.studysync.service;

import com.studysync.repository.CategoryRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 分類服務類
 * 提供任務分類相關的業務邏輯服務
 * 
 * 主要功能：
 * - 提供分類查詢服務（包含/不包含「全部分類」選項）
 * - 處理分類新增邏輯
 * - 處理分類更新和刪除
 * - 提供預設分類列表作為錯誤恢復機制
 * 
 * 業務規則：
 * - 「全部分類」是預設的第一個選項
 * - 當資料庫操作失敗時，提供預設分類列表
 * - 所有操作都有異常處理機制
 * 
 * @author StudySync Team
 * @version 1.0
 */
public class CategoryService {
    /** 分類資料存取層實例 */
    private final CategoryRepository categoryRepository;

    /**
     * 建構分類服務
     * 
     * @param categoryRepository 分類資料存取層實例
     */
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }    /**
     * 取得所有分類（包含「全部分類」選項）
     * 在分類列表的第一位加入「全部分類」選項
     * 
     * @return 包含「全部分類」的完整分類清單，錯誤時返回預設分類列表
     */
    public List<String> getAllCategories() {
        try {
            // 首先添加全部分類選項
            List<String> categories = new ArrayList<>();
            categories.add("全部分類");
            
            // 從資料庫獲取其他類別
            categories.addAll(categoryRepository.findAll());
            return categories;
        } catch (SQLException e) {
            System.err.println("獲取類別列表時發生錯誤: " + e.getMessage());
            e.printStackTrace();
            // 出錯時返回預設類別列表
            List<String> defaultCategories = new ArrayList<>();
            defaultCategories.add("全部分類");
            defaultCategories.add("Studying");
            defaultCategories.add("Homework");
            defaultCategories.add("Writing");
            defaultCategories.add("Meeting");
            defaultCategories.add("Reading");
            return defaultCategories;
        }
    }    /**
     * 取得分類列表（不包含「全部分類」選項）
     * 用於需要純分類選項的場合，如新增任務時的分類選擇
     * 
     * @return 不含「全部分類」的分類清單，錯誤時返回預設分類列表
     */
    public List<String> getCategoriesWithoutAll() {
        try {
            return categoryRepository.findAll();
        } catch (SQLException e) {
            System.err.println("獲取類別列表時發生錯誤: " + e.getMessage());
            e.printStackTrace();
            // 出錯時返回預設類別列表
            List<String> defaultCategories = new ArrayList<>();
            defaultCategories.add("Studying");
            defaultCategories.add("Homework");
            defaultCategories.add("Writing");
            defaultCategories.add("Meeting");
            defaultCategories.add("Reading");
            return defaultCategories;
        }
    }

    /**
     * 新增分類
     * 
     * @param name 分類名稱
     * @return true 表示新增成功，false 表示新增失敗
     */
    public boolean addCategory(String name) {
        try {
            categoryRepository.insert(name);
            return true;
        } catch (SQLException e) {
            System.err.println("新增類別時發生錯誤: " + e.getMessage());
            e.printStackTrace();
            return false;
        }    }

    /**
     * 更新分類名稱
     * 
     * @param oldName 舊的分類名稱
     * @param newName 新的分類名稱
     * @return true 表示更新成功，false 表示更新失敗或新名稱已存在
     */
    public boolean updateCategory(String oldName, String newName) {
        try {
            return categoryRepository.update(oldName, newName);
        } catch (SQLException e) {
            System.err.println("更新類別時發生錯誤: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 刪除分類
     * 
     * @param name 要刪除的分類名稱
     * @return true 表示刪除成功，false 表示刪除失敗
     */
    public boolean deleteCategory(String name) {
        try {
            categoryRepository.delete(name);
            return true;
        } catch (SQLException e) {
            System.err.println("刪除類別時發生錯誤: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}