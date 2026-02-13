package com.warehouse.service;

import com.warehouse.model.User;
import com.warehouse.repository.UserRepository;
import com.warehouse.exceptions.AuthException;

public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Логин
    public User login(String username, String password) throws AuthException {
        return userRepository.findByUsername(username)
                .filter(u -> u.getPassword().equals(password))
                .orElseThrow(() -> new AuthException("Invalid username or password"));
    }

    // Проверка существования
    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    // Регистрация нового пользователя
    public User register(User user) {
        return userRepository.save(user);
    }
}
