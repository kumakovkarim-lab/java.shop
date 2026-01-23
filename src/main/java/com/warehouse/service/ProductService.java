package com.warehouse.service;

import com.warehouse.model.Product;
import com.warehouse.repository.ProductRepository;

import java.util.List;

public class ProductService {
    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<Product> listProducts() {
        return repository.findAll();
    }

    public void addProduct(Product product) {
        repository.add(product);
    }

    public void restock(int productId, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Restock amount must be positive");
        }

        Product product = repository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        int newQuantity = product.getQuantity() + amount;
        repository.updateQuantity(productId, newQuantity);
    }

    public void sell(int productId, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Sell amount must be positive");
        }

        Product product = repository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (product.getQuantity() < amount) {
            throw new InsufficientStockException("Not enough stock for sale");
        }


        int newQuantity = product.getQuantity() - amount;
        repository.updateQuantity(productId, newQuantity);
    }
}

