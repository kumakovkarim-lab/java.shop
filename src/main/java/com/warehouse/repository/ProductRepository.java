package com.warehouse.repository;

import com.warehouse.DatabaseConfig;
import com.warehouse.model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository {
    private static final String SELECT_ALL =
            "SELECT p.id, p.name, p.price, p.quantity, p.category_id, c.name AS cat_name " +
                    "FROM products p " +
                    "JOIN categories c ON p.category_id = c.id ORDER BY p.id";

    private static final String INSERT =
            "INSERT INTO products (name, category_id, price, quantity) VALUES (?, ?, ?, ?)";

    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                products.add(new Product(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getInt("category_id"),
                        resultSet.getString("cat_name"), // Имя категории из JOIN
                        resultSet.getBigDecimal("price"),
                        resultSet.getInt("quantity")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при чтении товаров (JOIN)", e);
        }
        return products;
    }
    public void add(Product product) {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT)) {
            statement.setString(1, product.getName());
            statement.setInt(2, product.getCategoryId()); // Передаем ID (цифру)
            statement.setBigDecimal(3, product.getPrice());
            statement.setInt(4, product.getQuantity());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при добавлении товара", e);
        }
    }
}