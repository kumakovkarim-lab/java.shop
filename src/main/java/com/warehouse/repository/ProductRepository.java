package com.warehouse.repository;

import com.warehouse.DatabaseConfig;
import com.warehouse.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

public class ProductRepository {

    private static final String SELECT_ALL =
            "SELECT p.id, p.name, p.price, p.quantity, p.category_id, c.name AS cat_name " +
                    "FROM public.products p " +
                    "JOIN public.categories c ON p.category_id = c.id " +
                    "ORDER BY p.id";

    private static final String SELECT_BY_ID =
            "SELECT p.id, p.name, p.price, p.quantity, p.category_id, c.name AS cat_name " +
                    "FROM public.products p " +
                    "JOIN public.categories c ON p.category_id = c.id " +
                    "WHERE p.id = ?";

    private static final String INSERT =
            "INSERT INTO public.products (name, category_id, price, quantity) VALUES (?, ?, ?, ?)";

    private static final String UPDATE_QUANTITY =
            "UPDATE public.products SET quantity = ? WHERE id = ?";

    private static final String DELETE_BY_ID =
            "DELETE FROM public.products WHERE id = ?";

    private static final String UPDATE_PRICE =
            "UPDATE public.products SET price = ? WHERE id = ?";

    private static final String UPDATE_PRODUCT =
            "UPDATE public.products SET name = ?, category_id = ?, price = ?, quantity = ? WHERE id = ?";

    private static final String SELECT_BY_CATEGORY =
            "SELECT p.id, p.name, p.price, p.quantity, p.category_id, c.name AS cat_name " +
                    "FROM public.products p " +
                    "JOIN public.categories c ON p.category_id = c.id " +
                    "WHERE p.category_id = ? " +
                    "ORDER BY p.id";

    private static final String SEARCH_BY_NAME =
            "SELECT p.id, p.name, p.price, p.quantity, p.category_id, c.name AS cat_name " +
                    "FROM public.products p " +
                    "JOIN public.categories c ON p.category_id = c.id " +
                    "WHERE LOWER(p.name) LIKE LOWER(?) " +
                    "ORDER BY p.id";

    private static final String SELECT_FOR_DUPLICATE =
            "SELECT p.id, p.name, p.price, p.quantity, p.category_id, c.name AS cat_name " +
                    "FROM public.products p " +
                    "JOIN public.categories c ON p.category_id = c.id " +
                    "WHERE LOWER(p.name) = LOWER(?) AND p.price = ? AND p.category_id = ?";

    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                products.add(mapRowToProduct(resultSet));
            }
            return products;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load products", e);
        }
    }

    public Optional<Product> findById(int id) {
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) return Optional.of(mapRowToProduct(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load product by id", e);
        }
    }

    public Optional<Product> findByNamePriceAndCategory(String name, BigDecimal price, int categoryId) {
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_FOR_DUPLICATE)) {
            statement.setString(1, name.trim());
            statement.setBigDecimal(2, price);
            statement.setInt(3, categoryId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) return Optional.of(mapRowToProduct(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check for duplicate product", e);
        }
    }

    public void add(Product product) {
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT)) {
            statement.setString(1, product.getName());
            statement.setInt(2, product.getCategoryId());
            statement.setBigDecimal(3, product.getPrice());
            statement.setInt(4, product.getQuantity());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert product", e);
        }
    }

    public void updateQuantity(int id, int newQuantity) {
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_QUANTITY)) {
            statement.setInt(1, newQuantity);
            statement.setInt(2, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update product quantity", e);
        }
    }

    private Product mapRowToProduct(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("category_id"),
                rs.getString("cat_name"),
                rs.getBigDecimal("price"),
                rs.getInt("quantity")
        );
    }
}