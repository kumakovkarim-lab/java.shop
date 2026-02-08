package com.warehouse.presentation.Console;

import com.warehouse.controller.ProductController;
import com.warehouse.controller.UserController;
import com.warehouse.exceptions.AccessDeniedException;
import com.warehouse.exceptions.AuthException;
import com.warehouse.exceptions.InsufficientStockException;
import com.warehouse.exceptions.ValidationException;
import com.warehouse.model.Product;
import com.warehouse.model.Role;
import com.warehouse.model.User;
import com.warehouse.repository.*;
import com.warehouse.service.AuthService;
import com.warehouse.service.ProductService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class UI {

    private final Scanner scanner = new Scanner(System.in);

    private final ProductController controller;
    private final UserController userController = new UserController();
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    private User currentUser;

    public UI() {
        ProductRepository productRepository = new ProductRepository();
        AccountRepository accountRepository = new AccountRepository();
        categoryRepository = new CategoryRepository();
        userRepository = new UserRepository();
        authService = new AuthService(userRepository);

        ProductService productService =
                new ProductService(productRepository, accountRepository);

        controller = new ProductController(productService);
    }

    public void start() {
        while (true) {
            System.out.println("=== WAREHOUSE APP ===");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    login();
                    userController.setCurrentUser(currentUser);
                    mainMenu(); // Переходим в основное меню после логина
                }
                case "2" -> register(); // Регистрация нового пользователя
                case "3" -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Unknown option. Try again.\n");
            }
        }
    }

    private void mainMenu() {
        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> listProducts();
                    case "2" -> addProduct();
                    case "3" -> sellProduct();
                    case "4" -> restockProduct();
                    case "5" -> {
                        System.out.println("Logging out...");
                        currentUser = null;
                        return; // Выход в верхнее меню (логин/регистрация)
                    }
                    default -> System.out.println("Unknown option.");
                }
            } catch (AccessDeniedException e) {
                System.out.println("Access denied: " + e.getMessage());
            }
        }
    }

    private void login() {
        System.out.println("=== LOGIN ===");

        while (true) {
            System.out.print("Username: ");
            String username = scanner.nextLine().trim();

            System.out.print("Password: ");
            String password = scanner.nextLine().trim();

            try {
                currentUser = authService.login(username, password);
                System.out.println("Logged in as " + currentUser.getRole());
                return;
            } catch (AuthException e) {
                System.out.println("Login failed: " + e.getMessage());
            }
        }
    }

    private void register() {
        System.out.println("=== REGISTER ===");

        while (true) {
            System.out.print("Choose username: ");
            String username = scanner.nextLine().trim();

            if (authService.userExists(username)) {
                System.out.println("Username already exists. Try another.");
                continue;
            }

            System.out.print("Choose password: ");
            String password = scanner.nextLine().trim();

            // Создаем нового пользователя с ролью CLIENT
            User newUser = new User(username, password, Role.CLIENT);
            authService.register(newUser);

            System.out.println("Registration successful! You can now log in.");
            break;
        }
    }


    private void printMenu() {
        System.out.println("\n==============================");
        System.out.println("Balance: $" + controller.getBalance());
        System.out.println("1. List products");

        if (currentUser.getRole() == Role.ADMIN) {
            System.out.println("2. Add product");
            System.out.println("3. Sell product");
            System.out.println("4. Restock product");
        }

        System.out.println("5. Exit");
        System.out.print("Choose option: ");
    }

    private void listProducts() {
        List<Product> products = controller.listProducts();

        if (products.isEmpty()) {
            System.out.println("No products found.");
            return;
        }

        for (Product p : products) {
            System.out.printf(
                    "ID: %d | %s | Category: %s | Price: %s | Qty: %d%n",
                    p.getId(),
                    p.getName(),
                    p.getCategoryName(),
                    p.getPrice(),
                    p.getQuantity()
            );
        }
    }

    private void addProduct() {
        try {
            System.out.print("Name: ");
            String name = scanner.nextLine().trim();
            categoryRepository.printAllCategories();
            int categoryId = readInt("Category ID: ");
            BigDecimal price = readBigDecimal("Price: ");
            int quantity = readInt("Quantity: ");

            Product product = new Product(name, categoryId, price, quantity);
            controller.addProduct(product);

            System.out.println("Product added successfully.");
        } catch (ValidationException e) {
            System.out.println("Validation error: " + e.getMessage());
        }
    }

    private void sellProduct() {
        try {
            int productId = readInt("Product ID: ");
            int amount = readInt("Amount to sell: ");

            Product product = controller.sellProduct(productId, amount);

            System.out.println("\n=== SALE ===");
            System.out.println("Product: " + product.getName());
            System.out.println("Sold: " + amount);
            System.out.println("Remaining: " + product.getQuantity());
            System.out.println("Balance: $" + controller.getBalance());
        } catch (InsufficientStockException | IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void restockProduct() {
        try {
            int productId = readInt("Product ID: ");
            int amount = readInt("Amount to restock: ");

            Product product = controller.restockProduct(productId, amount);

            System.out.println("\n=== RESTOCK ===");
            System.out.println("Product: " + product.getName());
            System.out.println("Added: " + amount);
            System.out.println("Current qty: " + product.getQuantity());
            System.out.println("Balance: $" + controller.getBalance());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /* ================= HELPERS ================= */

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid integer.");
            }
        }
    }

    private BigDecimal readBigDecimal(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return new BigDecimal(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid number.");
            }
        }
    }
}