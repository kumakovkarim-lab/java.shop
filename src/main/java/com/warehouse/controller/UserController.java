package com.warehouse.controller;

import com.warehouse.exceptions.AccessDeniedException;
import com.warehouse.exceptions.AuthException;
import com.warehouse.model.Role;
import com.warehouse.model.User;
import com.warehouse.service.AuthService;

public class UserController {
    private AuthService authService;
    private User currentUser;

    public UserController() {
    }

    public UserController(AuthService authService) {
        this.authService = authService;
    }

    public void login(String username, String password) throws AuthException {
        this.currentUser = authService.login(username, password);
    }

    public void register(String username, String password, Role role) {
        User newUser = new User(username, password, role);
        authService.register(newUser);
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == Role.ADMIN;
    }

    public void checkAdmin() {
        if (!isAdmin()) {
            throw new AccessDeniedException("Admin privileges required.");
        }
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public void logout() {
        this.currentUser = null;
    }
}