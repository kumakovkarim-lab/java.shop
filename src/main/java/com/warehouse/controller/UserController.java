package com.warehouse.controller;

import com.warehouse.exceptions.AccessDeniedException;
import com.warehouse.exceptions.AuthException;
import com.warehouse.model.Role;
import com.warehouse.model.User;
import com.warehouse.service.AuthService;

public class UserController {
    private final AuthService authService;
    private User currentUser;

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

    public void logout() {
        this.currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public User getCurrentUser() {
        if (currentUser == null) {
            throw new IllegalStateException("Not logged in.");
        }
        return currentUser;
    }

    public String getCurrentUsername() {
        return getCurrentUser().getUsername();
    }

    public Role getCurrentRole() {
        return getCurrentUser().getRole();
    }

    public boolean isAdmin() {
        return isLoggedIn() && currentUser.getRole() == Role.ADMIN;
    }

    public boolean isClient() {
        return isLoggedIn() && currentUser.getRole() == Role.CLIENT;
    }

    public void requireLogin() {
        if (!isLoggedIn()) {
            throw new IllegalStateException("Please login first.");
        }
    }

    public void checkAdmin() {
        requireLogin();
        if (!isAdmin()) {
            throw new AccessDeniedException("Admin privileges required.");
        }
    }

    public void checkClient() {
        requireLogin();
        if (!isClient()) {
            throw new AccessDeniedException("Client privileges required.");
        }
    }
}
