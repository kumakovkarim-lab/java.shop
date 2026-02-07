package com.warehouse.repository;

import com.warehouse.DatabaseConfig;
import com.warehouse.model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductRepository {
    private static final String SELECT_ALL =
            "SELECT p.id, p.name, p.price, p.quantity, p.category_id, c.name AS cat_name " +
                    "FROM products p " +
                    "JOIN categories c ON p.category_id = c.id ORDER BY p.id";

    private static final String SELECT_BY_ID =
            "SELECT p.id, p.name, p.price, p.quantity, p.category_id, c.name AS cat_name " +
                    "FROM products p " +
                    "JOIN categories c ON p.category_id = c.id WHERE p.id = ?";

    private static final String INSERT =
            "INSERT INTO products (name, category_id, price, quantity) VALUES (?, ?, ?, ?)";

    private static final String UPDATE_QUANTITY =
            "UPDATE products SET quantity = ? WHERE id = ?";

    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                products.add(mapRowToProduct(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return products;
    }

    public Optional<Product> findById(int id) {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID)) {

            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRowToProduct(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public void add(Product product) {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT)) {
            statement.setString(1, product.getName());
            statement.setInt(2, product.getCategoryId());
            statement.setBigDecimal(3, product.getPrice());
            statement.setInt(4, product.getQuantity());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateQuantity(int id, int newQuantity) {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_QUANTITY)) {
            statement.setInt(1, newQuantity);
            statement.setInt(2, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Product mapRowToProduct(ResultSet resultSet) throws SQLException {
        return new Product(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getInt("category_id"),
                resultSet.getString("cat_name"),
                resultSet.getBigDecimal("price"),
                resultSet.getInt("quantity")
        );
    }
}