package com.warehouse;

import com.warehouse.controller.ProductController;
import com.warehouse.model.Product;
import com.warehouse.repository.ProductRepository;
import com.warehouse.service.InsufficientStockException;
import com.warehouse.service.ProductService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class ConsoleApp {
    public static void main(String[] args) {
        ProductRepository repository = new ProductRepository();
        ProductService service = new ProductService(repository);
        ProductController controller = new ProductController(service);

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                printMenu();
                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1" -> listProducts(controller);
                    case "2" -> addProduct(scanner, controller);
                    case "3" -> sellProduct(scanner, controller);
                    case "4" -> {
                        System.out.println("Goodbye!");
                        return;
                    }
                    default -> System.out.println("Unknown option. Try again.");
                }
            }
        }
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("1. List products");
        System.out.println("2. Add product");
        System.out.println("3. Sell product");
        System.out.println("4. Exit");
        System.out.print("Choose an option: ");
    }

    private static void listProducts(ProductController controller) {
        List<Product> products = controller.listProducts();
        if (products.isEmpty()) {
            System.out.println("No products found.");
            return;
        }

        for (Product product : products) {
            System.out.printf("ID: %d | %s | %s | %s | Qty: %d%n",
                    product.getId(),
                    product.getName(),
                    product.getCategory(),
                    product.getPrice(),
                    product.getQuantity());
        }
    }

    private static void addProduct(Scanner scanner, ProductController controller) {
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Category: ");
        String category = scanner.nextLine().trim();
        BigDecimal price = readBigDecimal(scanner, "Price: ");
        int quantity = readInt(scanner, "Quantity: ");

        Product product = new Product(name, category, price, quantity);
        controller.addProduct(product);
        System.out.println("Product added.");
    }

    private static void sellProduct(Scanner scanner, ProductController controller) {
        int productId = readInt(scanner, "Product ID: ");
        int amount = readInt(scanner, "Amount to sell: ");

        try {
            controller.sellProduct(productId, amount);
            System.out.println("Sale completed.");
        } catch (InsufficientStockException | IllegalArgumentException e) {
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

