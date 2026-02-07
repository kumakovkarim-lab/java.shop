package com.warehouse;

import com.warehouse.controller.ProductController;
import com.warehouse.exceptions.ValidationException;
import com.warehouse.model.Product;
import com.warehouse.repository.AccountRepository;
import com.warehouse.repository.ProductRepository;
import com.warehouse.repository.CategoryRepository;
import com.warehouse.exceptions.InsufficientStockException;
import com.warehouse.service.ProductService;
import com.warehouse.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class ConsoleApp {
    public static void main(String[] args) {
        ProductRepository repository = new ProductRepository();
        AccountRepository accountRepository = new AccountRepository();
        CategoryRepository categoryRepository = new CategoryRepository();
        ProductService service = new ProductService(repository, accountRepository);
        ProductController controller = new ProductController(service);
        UserRepository userRepo = new UserRepository();

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                printMenu(controller);
                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1" -> listProducts(controller);
                    case "2" -> addProduct(scanner, controller, categoryRepository);
                    case "3" -> sellProduct(scanner, controller);
                    case "4" -> restockProduct(scanner, controller);
                    case "5" -> {
                        System.out.println("Goodbye!");
                        return;
                    }
                    default -> System.out.println("Unknown option. Try again.");
                }
            }
        }
    }
    private static void printMenu(ProductController controller) {
        System.out.println("\nCurrent Balance: $" + controller.getBalance());
        System.out.println("1. List products");
        System.out.println("2. Add product");
        System.out.println("3. Sell product");
        System.out.println("4. Restock product");
        System.out.println("5. Exit");
        System.out.print("Choose an option: ");
    }

    private static void listProducts(ProductController controller) {
        List<Product> products = controller.listProducts();
        if (products.isEmpty()) {
            System.out.println("No products found.");
            return;
        }
        for (Product product : products) {
            System.out.printf("ID: %d | %s | Category: %s | Price: %s | Qty: %d\n",
                    product.getId(),
                    product.getName(),
                    product.getCategoryName(),
                    product.getPrice(),
                    product.getQuantity());
        }
    }

    private static void addProduct(Scanner scanner, ProductController controller, CategoryRepository categoryRepository) {
        try {
            System.out.print("Name: ");
            String name = scanner.nextLine().trim();
            categoryRepository.printAllCategories();
            int catId = readInt(scanner, "Enter Category ID: ");
            BigDecimal price = readBigDecimal(scanner, "Price: ");
            int quantity = readInt(scanner, "Quantity: ");

            Product product = new Product(name, catId, price, quantity);
            controller.addProduct(product);
            System.out.println("Product added.");
        } catch (ValidationException e) {
            System.out.println("Validation error: " + e.getMessage());
        }catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }


    }

    private static void sellProduct(Scanner scanner, ProductController controller) {
        int productId = readInt(scanner, "Product ID: ");
        int amount = readInt(scanner, "Amount to sell: ");

        try {
            Product updatedProduct = controller.sellProduct(productId, amount);
            int oldQuantity = updatedProduct.getQuantity() + amount;
            System.out.println("\n* SALES *");
            System.out.printf("Product: %s (ID: %d)%n", updatedProduct.getName(), updatedProduct.getId());
            System.out.printf("Price per unit: %s%n", updatedProduct.getPrice());
            System.out.printf("Quantity sold: %d%n", amount);
            System.out.printf("Total income: %s%n", updatedProduct.getPrice().multiply(new BigDecimal(amount)));
            System.out.println("New Balance: $" + controller.getBalance());
            System.out.println("----------------------------");
            System.out.printf("Remaining stock: %d%n", updatedProduct.getQuantity());
            System.out.println("Sale completed.");
        } catch (InsufficientStockException | IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void restockProduct(Scanner scanner, ProductController controller) {
        int productId = readInt(scanner, "Product ID: ");
        int amount = readInt(scanner, "Amount to restock: ");
        try {
            Product updatedProduct = controller.restockProduct(productId, amount);
            int oldQuantity = updatedProduct.getQuantity() - amount;
            BigDecimal totalCost = updatedProduct.getPrice().multiply(new BigDecimal(amount));
            System.out.println("\n* RESTOCK *");
            System.out.printf("Product: %s (ID: %d)%n", updatedProduct.getName(), updatedProduct.getId());
            System.out.printf("Previous Quantity: %d%n", oldQuantity);
            System.out.printf("Added: %d%n", amount);
            System.out.printf("Total cost: %s%n", totalCost);
            System.out.printf("Current Quantity: %d%n", updatedProduct.getQuantity());
            System.out.println("New Balance: $" + controller.getBalance());
            System.out.println("----------------------------");
            System.out.println("Restock completed.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    private static BigDecimal readBigDecimal(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return new BigDecimal(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
