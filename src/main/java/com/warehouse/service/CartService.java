package com.warehouse.service;

import com.warehouse.model.Cart;
import com.warehouse.model.CartItem;
import com.warehouse.model.Product;
import com.warehouse.repository.ProductRepository;

public class CartService {

    private final ProductRepository productRepository = new ProductRepository();
    private final Cart cart = new Cart();

    public void addToCart(Long productId, int quantity) {
        Product product = productRepository.findById(productId);

        if (product == null) {
            throw new RuntimeException("Product not found");
        }

        CartItem item = new CartItem(
                product.getId(),
                product.getName(),
                product.getPrice(),
                quantity
        );

        cart.addItem(item);
    }

    public void removeFromCart(Long productId) {
        cart.removeItem(productId);
    }

    public void showCart() {
        System.out.println("🛒 YOUR CART:");

        cart.getItems().forEach(item ->
                System.out.println(
                        item.getProductName() +
                                " | qty: " + item.getQuantity() +
                                " | total: " + item.getTotalPrice()
                )
        );

        System.out.println("Total amount: " + cart.getTotalAmount());
    }

    public Cart getCart() {
        return cart;
    }

    public void clearCart() {
        cart.clear();
    }
}
