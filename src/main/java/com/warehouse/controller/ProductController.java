package com.warehouse.controller;

import com.warehouse.model.Product;
import com.warehouse.service.ProductService;
import java.util.List;
import java.math.BigDecimal; // Импортируем для баланса

public class ProductController {
    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    public List<Product> listProducts() {
        return service.listProducts();
    }

    public void addProduct(Product product) {
        service.addProduct(product);
    }

    public Product restockProduct(int productId, int amount) {
        return service.restock(productId, amount);
    }

    public Product sellProduct(int productId, int amount) {
        return service.sell(productId, amount);
    }

    public BigDecimal getBalance() {
        return service.getCurrentBalance();
    }
}
