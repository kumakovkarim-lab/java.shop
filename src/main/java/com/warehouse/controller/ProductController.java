package com.warehouse.controller;

import com.warehouse.model.Product;
import com.warehouse.service.ProductService;
import java.util.List;

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

    public void restockProduct(int productId, int amount) {
        service.restock(productId, amount);
    }

    public void sellProduct(int productId, int amount) {
        service.sell(productId, amount);
    }
}




