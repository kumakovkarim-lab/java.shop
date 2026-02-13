package com.warehouse.service;

import com.warehouse.controller.ProductController;
import com.warehouse.exceptions.InsufficientStockException;
import com.warehouse.model.Address;
import com.warehouse.model.DeliveryMethod;
import com.warehouse.model.Order;
import com.warehouse.model.Product;
import com.warehouse.repository.interfaces.OrderRepository;

import java.util.List;

public class OrderService {

    private final ProductController productController;
    private final OrderRepository orderRepository;

    public OrderService(ProductController productController, OrderRepository orderRepository) {
        this.productController = productController;
        this.orderRepository = orderRepository;
    }

    public Order createOrder(String username,
                             int productId,
                             int quantity,
                             DeliveryMethod method,
                             Address address) throws InsufficientStockException {

        Product sold = productController.sellProduct(productId, quantity);

        Order order = new Order(
                0L,
                username,
                sold.getId(),
                sold.getName(),
                quantity,
                sold.getPrice(),
                method,
                address
        );

        return orderRepository.save(order);
    }

    public List<Order> listOrdersByUser(String username) {
        return orderRepository.findAll()
                .stream()
                .filter(o -> o.getUsername().equals(username))
                .toList();
    }
}
