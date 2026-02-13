package com.warehouse.repository;

import com.warehouse.db.Db;
import com.warehouse.model.Address;
import com.warehouse.model.DeliveryMethod;
import com.warehouse.model.DeliveryStatus;
import com.warehouse.model.Order;
import com.warehouse.repository.interfaces.OrderRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PostgresOrderRepository implements OrderRepository {

    @Override
    public Order save(Order order) {
        String sql = """
            INSERT INTO orders
            (username, product_id, product_name, quantity, product_price,
             delivery_method, delivery_status, delivery_fee,
             city, street, house, phone,
             total, created_at)
            VALUES
            (?, ?, ?, ?, ?,
             ?, ?, ?,
             ?, ?, ?, ?,
             ?, ?)
            RETURNING id;
        """;

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, order.getUsername());
            ps.setInt(2, order.getProductId());
            ps.setString(3, order.getProductName());
            ps.setInt(4, order.getQuantity());
            ps.setBigDecimal(5, order.getProductPrice());

            ps.setString(6, order.getDeliveryMethod().name());
            ps.setString(7, order.getDeliveryStatus().name());
            ps.setBigDecimal(8, order.getDeliveryFee());

            Address a = order.getAddress();
            ps.setString(9, a == null ? null : a.getCity());
            ps.setString(10, a == null ? null : a.getStreet());
            ps.setString(11, a == null ? null : a.getHouse());
            ps.setString(12, a == null ? null : a.getPhone());

            ps.setBigDecimal(13, order.getTotal());
            ps.setTimestamp(14, Timestamp.valueOf(order.getCreatedAt()));

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                long id = rs.getLong(1);

                Order saved = new Order(
                        id,
                        order.getUsername(),
                        order.getProductId(),
                        order.getProductName(),
                        order.getQuantity(),
                        order.getProductPrice(),
                        order.getDeliveryMethod(),
                        order.getAddress()
                );
                saved.setDeliveryStatus(order.getDeliveryStatus());
                return saved;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Order> findAll() {
        String sql = "SELECT * FROM orders ORDER BY created_at DESC";
        List<Order> list = new ArrayList<>();

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Address address = null;
                String city = rs.getString("city");
                if (city != null) {
                    address = new Address(
                            city,
                            rs.getString("street"),
                            rs.getString("house"),
                            rs.getString("phone")
                    );
                }

                Order order = new Order(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getBigDecimal("product_price"),
                        DeliveryMethod.valueOf(rs.getString("delivery_method")),
                        address
                );

                order.setDeliveryStatus(DeliveryStatus.valueOf(rs.getString("delivery_status")));
                list.add(order);
            }

            return list;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
