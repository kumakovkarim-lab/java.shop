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
            "SELECT p.id, p.name, p.category_id, c.name AS category_name, p.price, p.quantity " +
                    "FROM public.products p " +
                    "JOIN public.categories c ON c.id = p.category_id " +
                    "ORDER BY p.id";

    private static final String SELECT_BY_ID =
            "SELECT p.id, p.name, p.category_id, c.name AS category_name, p.price, p.quantity " +
                    "FROM public.products p " +
                    "JOIN public.categories c ON c.id = p.category_id " +
                    "WHERE p.id = ?";

    private static final String INSERT =
            "INSERT INTO public.products (name, category_id, price, quantity) VALUES (?, ?, ?, ?)";

    private static final String UPDATE_QUANTITY =
            "UPDATE public.products SET quantity = ? WHERE id = ?";

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

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
