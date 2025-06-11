package com.studysync.controller;

import com.studysync.model.User;
import com.studysync.service.UserService;

/**
 * 使用者控制器類
 * 負責處理與使用者相關的業務邏輯控制
 * 作為 View 層和 Service 層之間的橋樑
 * 
 * 主要功能：
 * - 處理使用者註冊請求
 * - 處理使用者登入驗證
 * - 處理密碼變更
 * - 處理使用者資料更新
 * - 提供使用者查詢功能
 * 
 * @author StudySync Team
 * @version 1.0
 */
public class UserController {
    /** 使用者服務層實例 */
    private final UserService userService = new UserService();

    /**
     * 處理使用者註冊請求
     * 
     * @param email 使用者電子郵件
     * @param password 使用者密碼
     * @param birthDate 使用者出生日期（格式：yyyy-MM-dd）
     * @return true 表示註冊成功，false 表示註冊失敗
     */
    public boolean register(String email, String password, String birthDate) {
        return userService.register(email, password, birthDate);
    }

    /**
     * 處理使用者登入請求
     * 
     * @param email 使用者電子郵件
     * @param password 使用者密碼
     * @return 登入成功時返回 User 物件，失敗時返回 null
     */
    public User login(String email, String password) {
        return userService.login(email, password);
    }
    
    /**
     * 處理密碼變更請求
     * 
     * @param userId 使用者 ID
     * @param oldPassword 舊密碼
     * @param newPassword 新密碼
     * @return true 表示密碼變更成功，false 表示變更失敗
     */
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        return userService.changePassword(userId, oldPassword, newPassword);
    }
    
    /**
     * 處理使用者出生日期更新請求
     * 
     * @param userId 使用者 ID
     * @param newBirthDate 新的出生日期（格式：yyyy-MM-dd）
     * @return true 表示更新成功，false 表示更新失敗
     */
    public boolean updateBirthDate(int userId, String newBirthDate) {
        return userService.updateBirthDate(userId, newBirthDate);
    }
    
    /**
     * 根據使用者 ID 查詢使用者資訊
     * 
     * @param userId 使用者 ID
     * @return 查詢到的 User 物件，如果不存在則返回 null
     */
    public User getUserById(int userId) {
        return userService.getUserById(userId);
    }
}
