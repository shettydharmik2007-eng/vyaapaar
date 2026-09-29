package com.vyaapaar.ui;

import com.vyaapaar.model.User;
import com.vyaapaar.service.AuthService;

/**
 * Main application menu handling role selection, login, registration, and routing.
 */
public class MainMenu {

    private final AuthService authService;

    public MainMenu() {
        this.authService = new AuthService();
    }

    public void start() {
        boolean running = true;
        while (running) {
            ConsoleUtils.printHeader("VYAAPAAR - E-COMMERCE & INVENTORY SYSTEM");
            System.out.println("1. Customer Login");
            System.out.println("2. Customer Registration");
            System.out.println("3. Admin Login");
            System.out.println("4. Exit");
            ConsoleUtils.printDivider();

            int choice = ConsoleUtils.readInt("Enter your choice (1-4): ");

            switch (choice) {
                case 1 -> handleCustomerLogin();
                case 2 -> handleCustomerRegistration();
                case 3 -> handleAdminLogin();
                case 4 -> {
                    ConsoleUtils.printSuccess("Thank you for using Vyaapaar. Goodbye!");
                    running = false;
                }
                default -> ConsoleUtils.printError("Invalid choice! Please choose an option between 1 and 4.");
            }
        }
    }

    private void handleCustomerLogin() {
        ConsoleUtils.printHeader("CUSTOMER LOGIN");
        String email = ConsoleUtils.readString("Enter Email: ");
        String password = ConsoleUtils.readString("Enter Password: ");

        try {
            User user = authService.login(email, password);
            if (authService.isCustomer(user)) {
                ConsoleUtils.printSuccess("Login successful! Welcome, " + user.getFullName());
                CustomerMenu customerMenu = new CustomerMenu(user);
                customerMenu.show();
            } else if (authService.isAdmin(user)) {
                // If admin logs into customer portal
                ConsoleUtils.printSuccess("Welcome Administrator! Opening Customer View...");
                CustomerMenu customerMenu = new CustomerMenu(user);
                customerMenu.show();
            } else {
                ConsoleUtils.printError("Unauthorized account role.");
            }
        } catch (Exception e) {
            ConsoleUtils.printError("Login Failed: " + e.getMessage());
        }
    }

    private void handleCustomerRegistration() {
        ConsoleUtils.printHeader("CUSTOMER REGISTRATION");
        String fullName = ConsoleUtils.readString("Enter Full Name: ");
        String email = ConsoleUtils.readString("Enter Email Address: ");
        String password = ConsoleUtils.readString("Enter Password (min 4 characters): ");
        String phone = ConsoleUtils.readOptionalString("Enter Phone Number: ");
        String address = ConsoleUtils.readString("Enter Delivery Address: ");

        try {
            User registeredUser = authService.registerCustomer(fullName, email, password, phone, address);
            ConsoleUtils.printSuccess("Registration successful! You can now login with: " + registeredUser.getEmail());
        } catch (Exception e) {
            ConsoleUtils.printError("Registration Failed: " + e.getMessage());
        }
    }

    private void handleAdminLogin() {
        ConsoleUtils.printHeader("ADMINISTRATOR LOGIN");
        String email = ConsoleUtils.readString("Enter Admin Email: ");
        String password = ConsoleUtils.readString("Enter Admin Password: ");

        try {
            User user = authService.login(email, password);
            if (!authService.isAdmin(user)) {
                ConsoleUtils.printError("Access Denied! Your account does not have Administrator privileges.");
                return;
            }

            ConsoleUtils.printSuccess("Admin authentication successful! Welcome, " + user.getFullName());
            AdminMenu adminMenu = new AdminMenu(user);
            adminMenu.show();
        } catch (Exception e) {
            ConsoleUtils.printError("Admin Login Failed: " + e.getMessage());
        }
    }
}
