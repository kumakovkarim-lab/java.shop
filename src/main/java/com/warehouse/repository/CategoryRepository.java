package com.warehouse.repository;

import com.warehouse.DatabaseConfig;
import java.sql.*;

public class CategoryRepository {
    public void printAllCategories() {
        String sql = "SELECT id, name FROM categories";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n* Доступные категории *");
            while (rs.next()) {
                System.out.printf("[%d] %s\n", rs.getInt("id"), rs.getString("name"));
            }
        } catch (SQLException e) {
            System.out.println("Ошибка загрузки категорий: " + e.getMessage());
        }
    }
}
