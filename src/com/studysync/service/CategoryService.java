package com.studysync.service;

import com.studysync.repository.CategoryRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

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
    }

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

    public boolean addCategory(String name) {
        try {
            categoryRepository.insert(name);
            return true;
        } catch (SQLException e) {
            System.err.println("新增類別時發生錯誤: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }    public boolean updateCategory(String oldName, String newName) {
        try {
            return categoryRepository.update(oldName, newName);
        } catch (SQLException e) {
            System.err.println("更新類別時發生錯誤: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

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