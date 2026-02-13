package com.warehouse;
import com.warehouse.controller.ProductController;
import com.warehouse.controller.UserController;
import com.warehouse.exceptions.ValidationException;
import com.warehouse.model.Product;
import com.warehouse.model.Role;
import com.warehouse.model.User;
import com.warehouse.repository.AccountRepository;
import com.warehouse.repository.ProductRepository;
import com.warehouse.repository.CategoryRepository;
import com.warehouse.repository.UserRepository;
import com.warehouse.exceptions.InsufficientStockException;
import com.warehouse.service.ProductService;
import com.warehouse.service.AuthService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class ConsoleApp {

    public static void main(String[] args) {
        ProductRepository productRepository = new ProductRepository();
        CategoryRepository categoryRepository = new CategoryRepository();
        AccountRepository accountRepository = new AccountRepository();
        UserRepository userRepository = new UserRepository();

        ProductService productService = new ProductService(productRepository, accountRepository);
        AuthService authService = new AuthService(userRepository);

        ProductController productController = new ProductController(productService);
        UserController userController = new UserController(authService);

        try (Scanner scanner = new Scanner(System.in)) {
            while (!userController.isLoggedIn()) {
                System.out.println("\n--- Warehouse System ---");
                System.out.println("1. Login");
                System.out.println("2. Register");
                System.out.print("Choose: ");
                String authChoice = scanner.nextLine().trim();

                if (authChoice.equals("1")) {
                    handleLogin(scanner, userController);
                } else if (authChoice.equals("2")) {
                    handleRegister(scanner, userController);
                }
            }

            while (true) {
                printMenu(productController, userController);
                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1" -> listProducts(productController);
                    case "2" -> {
                        userController.checkAdmin();
                        addProduct(scanner, productController, categoryRepository);
                    }
                    case "3" -> sellProduct(scanner, productController);
                    case "4" -> {
                        userController.checkAdmin();
                        restockProduct(scanner, productController);
                    }
                    case "5" -> {
                        userController.checkAdmin();
                        deleteProduct(scanner, productController);
                    }
                    case "6" -> {
                        System.out.println("Exiting...");
                        return;
                    }
                    default -> System.out.println("Invalid option.");
                }
            }
        }
    }

    private static void handleLogin(Scanner scanner, UserController userController) {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        try {
            userController.login(username, password);
            System.out.println("Logged in as: " + userController.getCurrentUser().getUsername());
        } catch (Exception e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    private static void handleRegister(Scanner scanner, UserController userController) {
        System.out.print("New Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("New Password: ");
        String password = scanner.nextLine().trim();
        System.out.println("Select Role: 1. CLIENT | 2. ADMIN");
        String roleChoice = scanner.nextLine().trim();
        Role role = roleChoice.equals("2") ? Role.ADMIN : Role.CLIENT;

        try {
            userController.register(username, password, role);
            System.out.println("Registration successful. Please login.");
        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private static void printMenu(ProductController productController, UserController userController) {
        System.out.println("\nUser: " + userController.getCurrentUser().getUsername() + " [" + userController.getCurrentUser().getRole() + "]");
        System.out.println("Current Balance: $" + productController.getBalance());
        System.out.println("1. List products");

        if (userController.isAdmin()) {
            System.out.println("2. Add product");
        }

        System.out.println("3. Sell product");

        if (userController.isAdmin()) {
            System.out.println("4. Restock product");
            System.out.println("5. Delete product");
        }

        System.out.println("6. Exit");
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
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }

    private static void deleteProduct(Scanner scanner, ProductController controller) {
        int productId = readInt(scanner, "Enter Product ID to delete: ");
        System.out.print("Confirm deletion? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.equals("yes")) {
            boolean deleted = controller.deleteProduct(productId);
            if (deleted) {
                System.out.println("Product deleted.");
            } else {
                System.out.println("Product not found.");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private static void sellProduct(Scanner scanner, ProductController controller) {
        int productId = readInt(scanner, "Product ID: ");
        int amount = readInt(scanner, "Amount to sell: ");

        try {
            Product updatedProduct = controller.sellProduct(productId, amount);
            System.out.println("\n* SALES *");
            System.out.printf("Product: %s (ID: %d)%n", updatedProduct.getName(), updatedProduct.getId());
            System.out.printf("Price: %s | Sold: %d | Total: %s%n",
                    updatedProduct.getPrice(),
                    amount,
                    updatedProduct.getPrice().multiply(new BigDecimal(amount)));
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
            System.out.println("Restock successful. New quantity: " + updatedProduct.getQuantity());
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
                System.out.println("Enter a whole number.");
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
                System.out.println("Enter a valid number.");
            }
        }
    }
}