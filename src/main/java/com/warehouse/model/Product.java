package com.warehouse.model;
import java.math.BigDecimal;

public class Product {
    private int id;
    private String name;
    private BigDecimal price;
    private int quantity;
    private int categoryId;
    private String categoryName;

    public Product() {
    }

    public Product(String name, int categoryId, BigDecimal price, int quantity) {
        this.name = name;
        this.categoryId = categoryId;
        this.price = price;
        this.quantity = quantity;
    }

    public Product(int id, String name, int categoryId, String catrgoryName, BigDecimal price, int quantity) {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
        this.categoryName = catrgoryName;
        this.price = price;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public int getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    public String getCategoryName() {
        return categoryName;
    }
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

}