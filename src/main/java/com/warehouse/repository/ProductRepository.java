package com.warehouse.repository;
import com.warehouse.DatabaseConfig;
import com.warehouse.model.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductRepository {
    private static final String SELECT_ALL =
            "SELECT id, name, category, price, quantity FROM products ORDER BY id";
    private static final String SELECT_BY_ID =
            "SELECT id, name, category, price, quantity FROM products WHERE id = ?";
    private static final String INSERT =
            "INSERT INTO products (name, category, price, quantity) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUANTITY =
            "UPDATE products SET quantity = ? W" +
                    "HERE id = ?";

    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                products.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load products", e);
        }
        return products;
    }

    public Optional<Product> findById(int id) {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID)) {

            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load product by id", e);
        }
        return Optional.empty();
    }

    public void add(Product product) {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT)) {

            statement.setString(1, product.getName());
            statement.setString(2, product.getCategory());
            statement.setBigDecimal(3, product.getPrice());
            statement.setInt(4, product.getQuantity());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert product", e);
        }
    }

    public void updateQuantity(int id, int newQuantity) {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_QUANTITY)) {

            statement.setInt(1, newQuantity);
            statement.setInt(2, id);
            statement.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException("Failed to update product quantity", e);
        }
    }

    private Product mapRow(ResultSet resultSet) throws SQLException {
        return new Product(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("category"),
                resultSet.getBigDecimal("price"),
                resultSet.getInt("quantity")
        );
    }
}

