package com.studysync.service;

import com.studysync.model.User;
import com.studysync.repository.UserRepository;
import com.studysync.util.DBUtil;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 使用者服務類
 * 提供使用者相關的業務邏輯服務
 * 
 * 主要功能：
 * - 處理使用者註冊邏輯（包含重複檢查）
 * - 處理使用者登入驗證
 * - 處理密碼變更（包含舊密碼驗證）
 * - 處理使用者資料更新
 * - 提供使用者查詢服務
 * 
 * 業務規則：
 * - 電子郵件不可重複
 * - 密碼變更需要驗證舊密碼
 * - 所有資料庫操作都有例外處理
 * 
 * @author StudySync Team
 * @version 1.0
 */
public class UserService {
    /** 使用者資料存取層實例 */
    private final UserRepository userRepository;

    /**
     * 建構使用者服務
     * 初始化資料庫連線和使用者資料存取層
     * 
     * @throws RuntimeException 當資料庫連線失敗時拋出
     */
    public UserService() {
        try {
            Connection conn = DBUtil.getConnection();
            this.userRepository = new UserRepository(conn);
        } catch (SQLException e) {
            throw new RuntimeException("建立使用者資料庫連線失敗", e);
        }
    }

    /**
     * 使用者註冊服務
     * 檢查電子郵件是否已存在，如果不存在則建立新使用者
     * 
     * @param email 使用者電子郵件
     * @param password 使用者密碼
     * @param birthDate 使用者出生日期
     * @return true 表示註冊成功，false 表示註冊失敗（電子郵件已存在或資料庫錯誤）
     */
    public boolean register(String email, String password, String birthDate) {
        try {
            // 檢查電子郵件是否已存在
            if (userRepository.findByEmail(email) != null) {
                return false; // 電子郵件已存在
            }
            
            // 建立新使用者（UID 設為 -1，由資料庫自動產生）
            User user = new User(-1, email, password, birthDate);
            userRepository.insert(user);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 使用者登入驗證服務
     * 驗證電子郵件和密碼是否正確
     * 
     * @param email 使用者電子郵件
     * @param password 使用者密碼
     * @return 登入成功時返回 User 物件，失敗時返回 null
     */
    public User login(String email, String password) {
        try {
            User user = userRepository.findByEmail(email);
            if (user != null && user.getPassword().equals(password)) {
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 密碼變更服務
     * 驗證舊密碼後更新為新密碼
     * 
     * @param userId 使用者 ID
     * @param oldPassword 舊密碼
     * @param newPassword 新密碼
     * @return true 表示密碼變更成功，false 表示變更失敗
     */
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        try {
            return userRepository.updatePassword(userId, oldPassword, newPassword);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 更新使用者出生日期服務
     * 
     * @param userId 使用者 ID
     * @param newBirthDate 新的出生日期
     * @return true 表示更新成功，false 表示更新失敗
     */
    public boolean updateBirthDate(int userId, String newBirthDate) {
        try {
            return userRepository.updateBirthDate(userId, newBirthDate);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 根據使用者 ID 查詢使用者資訊
     * 
     * @param userId 使用者 ID
     * @return 查詢到的 User 物件，如果不存在或發生錯誤則返回 null
     */
    public User getUserById(int userId) {
        try {
            return userRepository.findById(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
