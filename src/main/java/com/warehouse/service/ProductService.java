package com.warehouse.service;

import com.warehouse.model.Product;
import com.warehouse.repository.AccountRepository;
import com.warehouse.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;

public class ProductService {
    private final ProductRepository repository;
    private final AccountRepository accountRepository;

    public ProductService(ProductRepository repository, AccountRepository accountRepository) {
        this.repository = repository;
        this.accountRepository = accountRepository;
    }

    public List<Product> listProducts() {
        return repository.findAll();
    }

    public void addProduct(Product product) {
        repository.add(product);
    }

    public BigDecimal getCurrentBalance() {
        return accountRepository.getBalance();
    }

    public Product restock(int productId, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Restock amount must be positive");
        }

        Product product = repository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        BigDecimal totalCost = product.getPrice().multiply(new BigDecimal(amount));
        BigDecimal currentBalance = accountRepository.getBalance();

        if (currentBalance.compareTo(totalCost) < 0) {
            throw new IllegalArgumentException("Insufficient funds! Cost: " + totalCost + ", Balance: " + currentBalance);
        }


        int newQuantity = product.getQuantity() + amount;
        repository.updateQuantity(productId, newQuantity);
        accountRepository.updateBalance(currentBalance.subtract(totalCost));

        product.setQuantity(newQuantity);
        return product;
    }

    public Product sell(int productId, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Sell amount must be positive");
        }

        Product product = repository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (product.getQuantity() < amount) {
            throw new InsufficientStockException("Not enough stock for sale. Available: " + product.getQuantity());
        }


        BigDecimal income = product.getPrice().multiply(new BigDecimal(amount));
        BigDecimal currentBalance = accountRepository.getBalance();

        int newQuantity = product.getQuantity() - amount;
        repository.updateQuantity(productId, newQuantity);
        accountRepository.updateBalance(currentBalance.add(income));

        product.setQuantity(newQuantity);
        return product;
    }
}