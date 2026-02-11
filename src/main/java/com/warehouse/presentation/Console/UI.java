package com.warehouse.presentation.Console;

import com.warehouse.controller.ProductController;
import com.warehouse.controller.UserController;
import com.warehouse.exceptions.AccessDeniedException;
import com.warehouse.exceptions.AuthException;
import com.warehouse.exceptions.InsufficientStockException;
import com.warehouse.exceptions.ValidationException;
import com.warehouse.factory.UserFactory;
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
        this.categoryRepository = new CategoryRepository();
        this.userRepository = new UserRepository();
        this.authService = new AuthService(userRepository);

        ProductService productService = new ProductService(productRepository, accountRepository);
        this.controller = new ProductController(productService);
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
                    if (currentUser != null) {
                        userController.setCurrentUser(currentUser);
                        mainMenu();
                    }
                }
                case "2" -> register();
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
                if (userController.isAdmin()) {
                    switch (choice) {
                        case "1" -> listProducts();
                        case "2" -> addProduct();
                        case "3" -> sellProduct();
                        case "4" -> restockProduct();
                        case "5" -> deleteProduct();
                        case "6" -> {
                            userController.logout();
                            return;
                        }
                        default -> System.out.println("Unknown option.");
                    }
                } else {
                    switch (choice) {
                        case "1" -> listProducts();
                        case "2" -> buyProduct();
                        case "3" -> {
                            userController.logout();
                            return;
                        }
                        default -> System.out.println("Unknown option.");
                    }
                }
            } catch (AccessDeniedException e) {
                System.out.println("Access denied: " + e.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println("\n--- User: " + currentUser.getUsername() + " [" + currentUser.getRole() + "] ---");
        System.out.println("Balance: $" + controller.getBalance());

        if (userController.isAdmin()) {
            System.out.println("1. List products");
            System.out.println("2. Add product");
            System.out.println("3. Sell product");
            System.out.println("4. Restock product");
            System.out.println("5. Delete product");
            System.out.println("6. Logout");
        } else {
            System.out.println("1. List products");
            System.out.println("2. Buy product");
            System.out.println("3. Logout");
        }
        System.out.print("Choose option: ");
    }

    private void login() {
        System.out.println("=== LOGIN ===");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        try {
            currentUser = authService.login(username, password);
            System.out.println("Logged in as " + currentUser.getRole());
        } catch (AuthException e) {
            System.out.println("Login failed: " + e.getMessage());
            currentUser = null;
        }
    }

    private void register() {
        System.out.println("=== REGISTER ===");
        System.out.print("Choose username: ");
        String username = scanner.nextLine().trim();

        if (authService.userExists(username)) {
            System.out.println("Username already exists.");
            return;
        }

        System.out.print("Choose password: ");
        String password = scanner.nextLine().trim();
        System.out.println("Select Role: 1. CLIENT | 2. ADMIN");
        String roleChoice = scanner.nextLine().trim();
        Role role = roleChoice.equals("2") ? Role.ADMIN : Role.CLIENT;

        User newUser = UserFactory.createUser(username, password, role);
        authService.register(newUser);
        System.out.println("Registration successful!");
    }

    private void listProducts() {
        List<Product> products = controller.listProducts();
        if (products.isEmpty()) {
            System.out.println("No products found.");
            return;
        }
        products.forEach(p -> System.out.printf("ID: %d | %s | Category: %s | Price: %s | Qty: %d%n",
                p.getId(), p.getName(), p.getCategoryName(), p.getPrice(), p.getQuantity()));
    }

    private void addProduct() {
        try {
            System.out.print("Name: ");
            String name = scanner.nextLine().trim();
            categoryRepository.printAllCategories();
            int categoryId = readInt("Category ID: ");
            BigDecimal price = readBigDecimal("Price: ");
            int quantity = readInt("Quantity: ");
            controller.addProduct(new Product(name, categoryId, price, quantity));
            System.out.println("Product added.");
        } catch (ValidationException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void buyProduct() {
        try {
            int productId = readInt("Enter Product ID to buy: ");
            int amount = readInt("Enter amount: ");
            controller.sellProduct(productId, amount);
            System.out.println("Purchase successful!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void sellProduct() {
        try {
            int productId = readInt("Product ID: ");
            int amount = readInt("Amount: ");
            controller.sellProduct(productId, amount);
            System.out.println("Sale completed.");
        } catch (InsufficientStockException | IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void restockProduct() {
        try {
            int productId = readInt("Product ID: ");
            int amount = readInt("Amount: ");
            controller.restockProduct(productId, amount);
            System.out.println("Restock completed.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void deleteProduct() {
        int productId = readInt("Enter Product ID to delete: ");
        System.out.print("Are you sure? (yes/no): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("yes")) {
            if (controller.deleteProduct(productId)) {
                System.out.println("Product deleted.");
            } else {
                System.out.println("Product not found.");
            }
        }
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try { return Integer.parseInt(scanner.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Invalid integer."); }
        }
    }

    private BigDecimal readBigDecimal(String prompt) {
        while (true) {
            System.out.print(prompt);
            try { return new BigDecimal(scanner.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Invalid number."); }
        }
    }
}