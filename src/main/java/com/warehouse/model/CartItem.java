package com.warehouse.model;

import java.math.BigDecimal;

public class CartItem {
    private final int productId;
    private final String productName;
    private final BigDecimal price;
    private int quantity;

    public CartItem(int productId, String productName, BigDecimal price, int quantity) {
        if (productId <= 0) throw new IllegalArgumentException("productId must be > 0");
        if (productName == null || productName.isBlank()) throw new IllegalArgumentException("productName is empty");
        if (price == null) throw new IllegalArgumentException("price is null");
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be > 0");

        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be > 0");
        this.quantity = quantity;
    }

    public BigDecimal getLineTotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public String toString() {
        return "ID: " + productId + " | " + productName + " | price=" + price + " | qty=" + quantity +
                " | lineTotal=" + getLineTotal();
    }
}
