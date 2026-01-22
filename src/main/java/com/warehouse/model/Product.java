package com.warehouse.model;

public class Product {
    private int id;
    private String name;
    private String category;
    private BigDecimal price;
    private int quantity;

    public Product()
}

public Product(String name, String category, BigDecimal price, int quantity) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
    }

public Product(int id, String name, String category, BigDecimal price, int quantity) {
    this.id = id;
    this.name = name;
    this.category = category;
    this.price = price;
    this quantity = quantity;
}
