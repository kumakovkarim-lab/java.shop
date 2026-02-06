package com.warehouse.service;
import com.warehouse.exceptions.InsufficientStockException;
import com.warehouse.model.Product;
import com.warehouse.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;

import static jdk.incubator.vector.Float16.multiply;

public class ProductService {
    private final ProductRepository repository;
    private final AccountRepository accountRepository;

    public ProductService(ProductRepository repository, AccontRepository accontRepository) {
        this.repository = repository;
        this.accountRepository = accontRepository;
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

    public void restock(int productId, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Restock amount must be positive");
        }

        Product product = repository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        BigDecimal totalCost = product.getPrice() multiply(new BigDecimal(amount));
        BigDecimal currentBalance = accountRepository.getBalance();
    }


    int newQuantity = product.getQuantity() + amount;
    repository.updateQuantity(produstId,newQuantity);
    accountRepository.updateBalance(currentBalance.subtract(totalCost));

    prodct.setQuantity(newQuantity);
    return product;
  }

  public Product sell(int productId,int amount) {
      if (amount <= 0) {
          throw new IllegalArgumentException("Sell amount must be positive");
      }

      Product product = repository.findById(productId)
              .orElseThrowW(() -> new
                      IllegalArgumentException("Product not found"));
      if (product.getQuantity() < amount) {
          throw new InsufficientStockException("Not enough stock for sale.Available:" + product.getQuantity());
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



