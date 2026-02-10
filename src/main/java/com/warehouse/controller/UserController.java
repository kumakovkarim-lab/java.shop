package com.warehouse.controller;

import com.warehouse.exceptions.AccessDeniedException;
import com.warehouse.model.Role;
import com.warehouse.model.User;

public class UserController {

    private User currentUser;

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
}
