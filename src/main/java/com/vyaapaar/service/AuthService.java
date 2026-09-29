package com.vyaapaar.service;

import com.vyaapaar.dao.UserDao;
import com.vyaapaar.model.User;

/**
 * Service handling User Authentication, Registration, and Session Validation.
 */
public class AuthService {

    private final UserDao userDao;

    public AuthService() {
        this.userDao = new UserDao();
    }

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Registers a new customer after validating inputs and checking for duplicate emails.
     *
     * @param fullName User's full name
     * @param email User's email address
     * @param password Plain-text password
     * @param phone Contact phone number
     * @param address Delivery address
     * @return Registered User object
     * @throws IllegalArgumentException if validation fails or email is already registered
     */
    public User registerCustomer(String fullName, String email, String password, String phone, String address) {
        // 1. Validation of required fields
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be empty.");
        }
        if (email == null || !email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("A valid email address is required.");
        }
        if (password == null || password.trim().length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters long.");
        }
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Shipping address is required.");
        }

        // 2. Check for duplicate email
        User existingUser = userDao.findByEmail(email.trim().toLowerCase());
        if (existingUser != null) {
            throw new IllegalArgumentException("An account with email '" + email + "' already exists.");
        }

        // 3. Create and register user
        User newUser = new User(
                fullName.trim(),
                email.trim().toLowerCase(),
                password,
                "CUSTOMER",
                phone != null ? phone.trim() : "",
                address.trim()
        );

        boolean success = userDao.registerUser(newUser);
        if (!success) {
            throw new RuntimeException("Failed to register user. Database insertion error.");
        }

        return newUser;
    }

    /**
     * Authenticates a user by email and password.
     *
     * @param email User's email
     * @param password User's password
     * @return Authenticated User object
     * @throws IllegalArgumentException if credentials are missing or invalid
     */
    public User login(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }

        User user = userDao.findByEmail(email.trim().toLowerCase());
        if (user == null) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        return user;
    }

    /**
     * Helper to verify if an authenticated user is an administrator.
     */
    public boolean isAdmin(User user) {
        return user != null && "ADMIN".equalsIgnoreCase(user.getRole());
    }

    /**
     * Helper to verify if an authenticated user is a regular customer.
     */
    public boolean isCustomer(User user) {
        return user != null && "CUSTOMER".equalsIgnoreCase(user.getRole());
    }
}
