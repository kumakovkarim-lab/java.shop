package com.warehouse.controller;

import com.warehouse.model.Product;
import com.warehouse.service.ProductService;
import java.math.BigDecimal;
import java.util.List;

public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    public List<Product> listProducts() {
        return productService.listProducts();
    }

    public void addProduct(Product product) {
        productService.addProduct(product);
    }

    public Product sellProduct(int productId, int amount) {
        return productService.sell(productId, amount);
    }

    public Product restockProduct(int productId, int amount) {
        return productService.restock(productId, amount);
    }

    public boolean deleteProduct(int productId) {
        return productService.deleteProduct(productId);
    }

    public BigDecimal getBalance() {
        return productService.getBalance();
    }
}