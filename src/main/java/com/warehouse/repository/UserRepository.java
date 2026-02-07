package com.warehouse.repository;

import com.warehouse.DatabaseConfig;
import com.warehouse.model.User;
import java.sql.*;
import java.util.Optional;

public class UserRepository {
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT username, password, role FROM users WHERE username = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new User(rs.getString("username"), rs.getString("password"), rs.getString("role")));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }
}