package com.studysync.controller;

import com.studysync.model.User;
import com.studysync.service.UserService;

public class UserController {
    private final UserService userService = new UserService();

    public boolean register(String email, String password, String birthDate) {
        return userService.register(email, password, birthDate);
    }

    public User login(String email, String password) {
        return userService.login(email, password);
    }
    
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        return userService.changePassword(userId, oldPassword, newPassword);
    }
    
    public boolean updateBirthDate(int userId, String newBirthDate) {
        return userService.updateBirthDate(userId, newBirthDate);
    }
    
    public User getUserById(int userId) {
        return userService.getUserById(userId);
    }
}
