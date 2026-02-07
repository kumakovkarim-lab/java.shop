package com.warehouse.repository;

import com.warehouse.DatabaseConfig;
import com.warehouse.model.User;
import java.sql.*;
import java.util.Optional;

public class UserRepository {

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM public.users WHERE username = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setRole(rs.getString("role"));
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user", e);
        }
        return Optional.empty();
    }
}