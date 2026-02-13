package com.warehouse.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Order {
    private final long id;

    private final String username;

    private final int productId;
    private final String productName;
    private final int quantity;
    private final BigDecimal productPrice;

    private final DeliveryMethod deliveryMethod;
    private final Address address;
    private DeliveryStatus deliveryStatus;

    private final BigDecimal deliveryFee;
    private final BigDecimal total;
    private final LocalDateTime createdAt;

    public Order(long id,
                 String username,
                 int productId,
                 String productName,
                 int quantity,
                 BigDecimal productPrice,
                 DeliveryMethod deliveryMethod,
                 Address address) {

        this.id = id;
        this.username = username;

        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.productPrice = productPrice;

        this.deliveryMethod = deliveryMethod;
        this.address = address;
        this.deliveryStatus = DeliveryStatus.PROCESSING;

        this.deliveryFee = deliveryMethod.getFee();
        BigDecimal subtotal = productPrice.multiply(BigDecimal.valueOf(quantity));
        this.total = subtotal.add(deliveryFee);

        this.createdAt = LocalDateTime.now();
    }

    public long getId() { return id; }
    public String getUsername() { return username; }

    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public BigDecimal getProductPrice() { return productPrice; }

    public DeliveryMethod getDeliveryMethod() { return deliveryMethod; }
    public Address getAddress() { return address; }
    public DeliveryStatus getDeliveryStatus() { return deliveryStatus; }

    public BigDecimal getDeliveryFee() { return deliveryFee; }
    public BigDecimal getTotal() { return total; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setDeliveryStatus(DeliveryStatus status) {
        this.deliveryStatus = status;
    }
}
