package com.warehouse.repository;

import com.warehouse.model.Order;
import com.warehouse.repository.interfaces.OrderRepository;

import java.util.ArrayList;
import java.util.List;

public class InMemoryOrderRepository implements OrderRepository {
    private final List<Order> orders = new ArrayList<>();

    @Override
    public Order save(Order order) {
        orders.add(order);
        return order;
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orders);
    }
}
