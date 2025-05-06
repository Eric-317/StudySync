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
}
