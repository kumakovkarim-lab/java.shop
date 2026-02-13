package com.warehouse.presentation.Console;

import com.warehouse.controller.ProductController;
import com.warehouse.exceptions.AuthException;
import com.warehouse.exceptions.InsufficientStockException;
import com.warehouse.exceptions.ValidationException;
import com.warehouse.model.*;
import com.warehouse.repository.*;
import com.warehouse.repository.interfaces.OrderRepository;
import com.warehouse.service.AuthService;
import com.warehouse.service.CartService;
import com.warehouse.service.OrderService;
import com.warehouse.service.ProductService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class UI {

    private final Scanner scanner = new Scanner(System.in);

    private final ProductController productController;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    private final OrderService orderService;
    private final CartService cartService;

    private User currentUser;

    public UI() {
        ProductRepository productRepository = new ProductRepository();
        AccountRepository accountRepository = new AccountRepository();
        categoryRepository = new CategoryRepository();

        userRepository = new UserRepository();
        authService = new AuthService(userRepository);

        ProductService productService = new ProductService(productRepository, accountRepository);
        productController = new ProductController(productService);

        OrderRepository orderRepository = new PostgresOrderRepository();
        orderService = new OrderService(productController, orderRepository);

        cartService = new CartService(productController);
    }

    public void start() {
        while (true) {
            System.out.println("\n=== ONLINE SHOP ===");
            System.out.println("1) Login");
            System.out.println("2) Register (CLIENT)");
            System.out.println("3) Exit");
            System.out.print("Choose: ");

            String c = scanner.nextLine().trim();

            switch (c) {
                case "1" -> {
                    login();
                    mainMenu();
                }
                case "2" -> registerClient();
                case "3" -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Unknown option.");
            }
        }
    }

    private void mainMenu() {
        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();

            try {
                if (currentUser.getRole() == Role.CLIENT) {
                    switch (choice) {
                        case "1" -> listProducts();
                        case "2" -> addToCart();
                        case "3" -> showCart();
                        case "4" -> removeFromCart();
                        case "5" -> clearCart();
                        case "6" -> checkoutCartWithDelivery();
                        case "7" -> myOrders();
                        case "8" -> {
                            logout();
                            return;
                        }
                        default -> System.out.println("Unknown option.");
                    }
                } else if (currentUser.getRole() == Role.ADMIN) {
                    switch (choice) {
                        case "1" -> listProducts();
                        case "2" -> addProduct();
                        case "3" -> sellProduct();
                        case "4" -> restockProduct();
                        case "5" -> {
                            logout();
                            return;
                        }
                        default -> System.out.println("Unknown option.");
                    }
                } else {
                    System.out.println("Unknown role.");
                }
            } catch (ValidationException e) {
                System.out.println("Validation error: " + e.getMessage());
            } catch (InsufficientStockException e) {
                System.out.println("Stock error: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println("\n==============================");
        System.out.println("User: " + currentUser.getUsername() + " [" + currentUser.getRole() + "]");
        System.out.println("Balance: $" + productController.getBalance());
        System.out.println("1) List products");

        if (currentUser.getRole() == Role.CLIENT) {
            System.out.println("2) Add to cart");
            System.out.println("3) Show cart");
            System.out.println("4) Remove from cart");
            System.out.println("5) Clear cart");
            System.out.println("6) Checkout (delivery + pay)");
            System.out.println("7) My orders");
            System.out.println("8) Logout");
        }

        if (currentUser.getRole() == Role.ADMIN) {
            System.out.println("2) Add product");
            System.out.println("3) Sell product");
            System.out.println("4) Restock product");
            System.out.println("5) Logout");
        }

        System.out.print("Choose: ");
    }

    private void login() {
        System.out.println("\n=== LOGIN ===");
        while (true) {
            System.out.print("Username: ");
            String username = scanner.nextLine().trim();

            System.out.print("Password: ");
            String password = scanner.nextLine().trim();

            try {
                currentUser = authService.login(username, password);
                System.out.println("Logged in as: " + currentUser.getUsername() + " [" + currentUser.getRole() + "]");
                return;
            } catch (AuthException e) {
                System.out.println("Login failed: " + e.getMessage());
            }
        }
    }

    private void registerClient() {
        System.out.println("\n=== REGISTER (CLIENT) ===");
        while (true) {
            System.out.print("Choose username: ");
            String username = scanner.nextLine().trim();

            if (authService.userExists(username)) {
                System.out.println("Username already exists.");
                continue;
            }

            System.out.print("Choose password: ");
            String password = scanner.nextLine().trim();

            User u = new User(username, password, Role.CLIENT);
            authService.register(u);

            System.out.println("Registration successful! Now login.");
            return;
        }
    }

    private void logout() {
        System.out.println("Logging out...");
        cartService.clearCart();
        currentUser = null;
    }

    private void listProducts() {
        List<Product> products = productController.listProducts();
        if (products.isEmpty()) {
            System.out.println("No products found.");
            return;
        }

        System.out.println("\n=== PRODUCTS ===");
        for (Product p : products) {
            System.out.printf(
                    "ID: %d | %s | Category: %s | Price: %s | Qty: %d%n",
                    p.getId(), p.getName(), p.getCategoryName(), p.getPrice(), p.getQuantity()
            );
        }
    }

    private void addToCart() {
        listProducts();
        int productId = readInt("Enter Product ID to add: ");
        int qty = readInt("Enter quantity: ");
        cartService.addToCart(productId, qty);
        System.out.println("Added to cart.");
    }

    private void showCart() {
        cartService.showCart();
    }

    private void removeFromCart() {
        int productId = readInt("Enter Product ID to remove: ");
        cartService.removeFromCart(productId);
        System.out.println("Removed from cart (if existed).");
    }

    private void clearCart() {
        cartService.clearCart();
        System.out.println("Cart cleared.");
    }

    private void checkoutCartWithDelivery() throws InsufficientStockException {
        if (cartService.getCart().isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }

        cartService.showCart();

        DeliveryMethod method = chooseDeliveryMethod();
        Address address = null;
        if (method != DeliveryMethod.PICKUP) {
            address = readAddress();
        }

        System.out.print("Confirm payment for TOTAL " + cartService.getCart().getTotal() + "? (yes/no): ");
        String pay = scanner.nextLine().trim().toLowerCase();
        if (!pay.equals("yes")) {
            System.out.println("Payment cancelled.");
            return;
        }

        // Создаем заказ на каждый товар из корзины
        for (CartItem item : cartService.getCart().getItems()) {
            orderService.createOrder(
                    currentUser.getUsername(),
                    item.getProductId(),
                    item.getQuantity(),
                    method,
                    address
            );
        }

        cartService.clearCart();
        System.out.println("\n✅ CHECKOUT SUCCESS! Orders saved to DB.");
        System.out.println("Balance: $" + productController.getBalance());
    }

    private void myOrders() {
        List<Order> orders = orderService.listOrdersByUser(currentUser.getUsername());
        if (orders.isEmpty()) {
            System.out.println("No orders yet.");
            return;
        }

        System.out.println("\n=== MY ORDERS ===");
        for (Order o : orders) {
            System.out.printf(
                    "#%d | %s x%d | delivery=%s (%s) | total=%s | %s%n",
                    o.getId(),
                    o.getProductName(),
                    o.getQuantity(),
                    o.getDeliveryMethod(),
                    o.getDeliveryStatus(),
                    o.getTotal(),
                    o.getCreatedAt()
            );
        }
    }

    private DeliveryMethod chooseDeliveryMethod() {
        System.out.println("\nChoose delivery method:");
        System.out.println("1) PICKUP (free)");
        System.out.println("2) COURIER ($5.00)");
        System.out.println("3) EXPRESS ($12.00)");
        int c = readInt("Choose: ");

        return switch (c) {
            case 1 -> DeliveryMethod.PICKUP;
            case 2 -> DeliveryMethod.COURIER;
            case 3 -> DeliveryMethod.EXPRESS;
            default -> throw new IllegalArgumentException("Invalid delivery option");
        };
    }

    private Address readAddress() {
        System.out.print("City: ");
        String city = scanner.nextLine().trim();

        System.out.print("Street: ");
        String street = scanner.nextLine().trim();

        System.out.print("House: ");
        String house = scanner.nextLine().trim();

        System.out.print("Phone: ");
        String phone = scanner.nextLine().trim();

        return new Address(city, street, house, phone);
    }

    private void addProduct() throws ValidationException {
        System.out.println("\n=== ADD PRODUCT ===");
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();

        categoryRepository.printAllCategories();
        int categoryId = readInt("Category ID: ");

        BigDecimal price = readBigDecimal("Price: ");
        int qty = readInt("Quantity: ");

        Product product = new Product(name, categoryId, price, qty);
        productController.addProduct(product);

        System.out.println("Product added.");
    }

    private void sellProduct() throws InsufficientStockException {
        System.out.println("\n=== SELL PRODUCT ===");
        int productId = readInt("Product ID: ");
        int amount = readInt("Amount to sell: ");

        Product p = productController.sellProduct(productId, amount);
        System.out.println("Sold successfully. Remaining qty: " + p.getQuantity());
        System.out.println("Balance: $" + productController.getBalance());
    }

    private void restockProduct() {
        System.out.println("\n=== RESTOCK PRODUCT ===");
        int productId = readInt("Product ID: ");
        int amount = readInt("Amount to restock: ");

        Product p = productController.restockProduct(productId, amount);
        System.out.println("Restock successful. New qty: " + p.getQuantity());
        System.out.println("Balance: $" + productController.getBalance());
    }

    private int readInt(String prompt) {
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

    private BigDecimal readBigDecimal(String prompt) {
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
