package com.studysync.model;

/**
 * 使用者資料模型類
 * 代表系統中的一個使用者，包含基本的使用者資訊
 * 
 * 此類用於：
 * - 儲存使用者的基本資訊（UID、電子郵件、密碼、出生日期）
 * - 在不同層之間傳遞使用者資料
 * - 提供使用者資料的存取方法
 * 
 * @author StudySync Team
 * @version 1.0
 */
public class User {
    /** 使用者唯一識別碼 */
    private int uid;
    
    /** 使用者電子郵件地址（同時作為登入帳號） */
    private String email;
    
    /** 使用者密碼（應以加密形式儲存） */
    private String password;
    
    /** 使用者出生日期（格式：yyyy-MM-dd） */
    private String birthDate;

    /**
     * 建構一個新的使用者物件
     * 
     * @param uid 使用者唯一識別碼
     * @param email 電子郵件地址
     * @param password 密碼
     * @param birthDate 出生日期（格式：yyyy-MM-dd）
     */
    public User(int uid, String email, String password, String birthDate) {
        this.uid = uid;
        this.email = email;
        this.password = password;
        this.birthDate = birthDate;
    }

    /**
     * 取得使用者唯一識別碼
     * 
     * @return 使用者 UID
     */
    public int getUid() {
        return uid;
    }

    /**
     * 設定使用者唯一識別碼
     * 
     * @param uid 使用者 UID
     */
    public void setUid(int uid) {
        this.uid = uid;
    }

    /**
     * 取得使用者電子郵件地址
     * 
     * @return 電子郵件地址
     */
    public String getEmail() {
        return email;
    }

    /**
     * 設定使用者電子郵件地址
     * 
     * @param email 電子郵件地址
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 取得使用者密碼
     * 
     * @return 密碼
     */
    public String getPassword() {
        return password;
    }

    /**
     * 設定使用者密碼
     * 
     * @param password 密碼
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 取得使用者出生日期
     * 
     * @return 出生日期（格式：yyyy-MM-dd）
     */
    public String getBirthDate() {
        return birthDate;
    }

    /**
     * 設定使用者出生日期
     * 
     * @param birthDate 出生日期（格式：yyyy-MM-dd）
     */
    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }
    
    /**
     * 返回使用者物件的字串表示
     * 
     * @return 包含使用者基本資訊的字串（不包含密碼）
     */
    @Override
    public String toString() {
        return "User{" +
                "uid=" + uid +
                ", email='" + email + '\'' +
                ", birthDate='" + birthDate + '\'' +
                '}';
    }
}
