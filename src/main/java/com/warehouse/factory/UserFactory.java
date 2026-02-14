package com.warehouse.factory;

import com.warehouse.model.Role;
import com.warehouse.model.User;

public class UserFactory {
    public static User createUser(String username, String password, Role role) {
        return new User(username, password, role);
    }
}
