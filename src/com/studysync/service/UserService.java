package com.studysync.service;

import com.studysync.model.User;
import com.studysync.repository.UserRepository;
import com.studysync.util.DBUtil;

import java.sql.Connection;
import java.sql.SQLException;

public class UserService {
    private final UserRepository userRepository;

    public UserService() {
        try {
            Connection conn = DBUtil.getConnection();
            this.userRepository = new UserRepository(conn);
        } catch (SQLException e) {
            throw new RuntimeException("建立使用者資料庫連線失敗", e);
        }
    }

    public boolean register(String email, String password, String birthDate) {
        try {
            if (userRepository.findByEmail(email) != null) {
                return false;
            }
            User user = new User(-1, email, password, birthDate);
            userRepository.insert(user);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

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
}
