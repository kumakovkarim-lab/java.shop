package com.warehouse.repository.interfaces;

import com.warehouse.model.Order;
import java.util.List;

public interface OrderRepository {
    Order save(Order order);
    List<Order> findAll();
}
