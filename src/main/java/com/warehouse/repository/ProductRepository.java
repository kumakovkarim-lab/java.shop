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

    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        try (Connection connection = DatabaseConfig.getConnection();
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
        try (Connection connection = DatabaseConfig.getConnection();
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

    public List<Product> findByCategoryId(int categoryId) {
        List<Product> products = new ArrayList<>();
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BY_CATEGORY)) {

            statement.setInt(1, categoryId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) products.add(mapRowToProduct(rs));
            }
            return products;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to load products by category", e);
        }
    }

    public List<Product> searchByName(String text) {
        List<Product> products = new ArrayList<>();
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(SEARCH_BY_NAME)) {

            statement.setString(1, "%" + (text == null ? "" : text.trim()) + "%");
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) products.add(mapRowToProduct(rs));
            }
            return products;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to search products", e);
        }
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
            throw new RuntimeException("Failed to insert product", e);
        }
    }

    public void updateQuantity(int id, int newQuantity) {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_QUANTITY)) {

            statement.setInt(1, newQuantity);
            statement.setInt(2, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update product quantity", e);
        }
    }

    public void updatePrice(int id, java.math.BigDecimal newPrice) {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_PRICE)) {

            statement.setBigDecimal(1, newPrice);
            statement.setInt(2, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update product price", e);
        }
    }

    public void updateProduct(int id, Product product) {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_PRODUCT)) {

            statement.setString(1, product.getName());
            statement.setInt(2, product.getCategoryId());
            statement.setBigDecimal(3, product.getPrice());
            statement.setInt(4, product.getQuantity());
            statement.setInt(5, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update product", e);
        }
    }

    public boolean deleteById(int id) {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_BY_ID)) {

            statement.setInt(1, id);
            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete product", e);
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
