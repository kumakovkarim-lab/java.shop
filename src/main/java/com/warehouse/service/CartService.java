package com.warehouse.service;

import com.warehouse.controller.ProductController;
import com.warehouse.model.Cart;
import com.warehouse.model.CartItem;
import com.warehouse.model.Product;

import java.util.List;

public class CartService {

    private final ProductController productController;
    private final Cart cart = new Cart();

    public CartService(ProductController productController) {
        this.productController = productController;
    }

    public Cart getCart() {
        return cart;
    }

    public void addToCart(int productId, int qty) {
        if (productId <= 0) throw new IllegalArgumentException("Product ID must be > 0");
        if (qty <= 0) throw new IllegalArgumentException("Quantity must be > 0");

        Product product = findProduct(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }

        cart.addOrIncrease(new CartItem(
                product.getId(),
                product.getName(),
                product.getPrice(),
                qty
        ));
    }

    public void removeFromCart(int productId) {
        cart.remove(productId);
    }

    public void clearCart() {
        cart.clear();
    }

    public void showCart() {
        if (cart.isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }

        System.out.println("\n=== CART ===");
        for (var item : cart.getItems()) {
            System.out.println(item);
        }
        System.out.println("TOTAL: " + cart.getTotal());
    }

    private Product findProduct(int productId) {
        List<Product> products = productController.listProducts();
        for (Product p : products) {
            if (p.getId() == productId) return p;
        }
        return null;
    }
}
