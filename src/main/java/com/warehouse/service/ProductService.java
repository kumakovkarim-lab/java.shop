package com.warehouse.service;

import com.warehouse.exceptions.InsufficientStockException;
import com.warehouse.exceptions.ValidationException;
import com.warehouse.model.Product;
import com.warehouse.repository.AccountRepository;
import com.warehouse.repository.ProductRepository;
import com.warehouse.repository.interfaces.IAccountRepository;

import java.math.BigDecimal;
import java.util.List;

public class ProductService {
    private final ProductRepository repository;
    private final IAccountRepository accountRepository;

    public ProductService(ProductRepository repository, IAccountRepository accountRepository) {
        this.repository = repository;
        this.accountRepository = accountRepository;
    }

    public List<Product> listProducts() {
        return repository.findAll();
    }

    public void addProduct(Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new ValidationException("Product name can not be empty...");
        }
        if (product.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Price MUST be grater than ZERO...");
        }
        repository.add(product);
    }


    public BigDecimal getBalance() {
        return accountRepository.getBalance();
    }

    public Product restock(int productId, int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Restock amount must be positive");

        Product product = repository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        BigDecimal cost = product.getPrice().multiply(new BigDecimal(amount));
        BigDecimal currentBalance = accountRepository.getBalance();

        if (currentBalance.compareTo(cost) < 0) {
            throw new IllegalArgumentException("Insufficient funds! Cost: " + cost + ", Balance: " + currentBalance);
        }

        int newQuantity = product.getQuantity() + amount;
        repository.updateQuantity(productId, newQuantity);
        accountRepository.updateBalance(currentBalance.subtract(cost));

        product.setQuantity(newQuantity);
        return product;
    }

    public Product sell(int productId, int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Sell amount must be positive");

        Product product = repository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (product.getQuantity() < amount) {
            throw new InsufficientStockException("Not enough stock");
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
