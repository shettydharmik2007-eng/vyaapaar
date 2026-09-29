package com.vyaapaar.ui;

import java.util.Scanner;

/**
 * Reusable utility class for console input reading, formatting, and validation.
 */
public class ConsoleUtils {

    private static final Scanner scanner = new Scanner(System.in);

    private ConsoleUtils() {}

    /**
     * Reads a non-empty string from the console.
     */
    public static String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            if (input != null && !input.trim().isEmpty()) {
                return input.trim();
            }
            System.out.println(">>> [Input Error] Input cannot be empty. Please try again.");
        }
    }

    /**
     * Reads an optional string (can be empty).
     */
    public static String readOptionalString(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine();
        return input != null ? input.trim() : "";
    }

    /**
     * Reads any valid integer from the console.
     */
    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                System.out.println(">>> [Input Error] Invalid number. Please enter a valid integer.");
            }
        }
    }

    /**
     * Reads a strictly positive integer (> 0) from the console.
     */
    public static int readPositiveInt(String prompt) {
        while (true) {
            int value = readInt(prompt);
            if (value > 0) {
                return value;
            }
            System.out.println(">>> [Input Error] Number must be greater than 0.");
        }
    }

    /**
     * Reads a non-negative integer (>= 0) from the console.
     */
    public static int readNonNegativeInt(String prompt) {
        while (true) {
            int value = readInt(prompt);
            if (value >= 0) {
                return value;
            }
            System.out.println(">>> [Input Error] Number cannot be negative.");
        }
    }

    /**
     * Reads a strictly positive decimal value (> 0.0) from the console.
     */
    public static double readPositiveDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                double value = Double.parseDouble(input.trim());
                if (value > 0.0) {
                    return value;
                }
                System.out.println(">>> [Input Error] Amount must be greater than 0.");
            } catch (NumberFormatException e) {
                System.out.println(">>> [Input Error] Invalid decimal number. Please try again.");
            }
        }
    }

    /**
     * Reads a confirmation prompt (Y/N).
     */
    public static boolean readConfirmation(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            if (input != null) {
                String trimmed = input.trim();
                if (trimmed.equalsIgnoreCase("Y") || trimmed.equalsIgnoreCase("YES")) {
                    return true;
                }
                if (trimmed.equalsIgnoreCase("N") || trimmed.equalsIgnoreCase("NO")) {
                    return false;
                }
            }
            System.out.println(">>> [Input Error] Please enter 'Y' for Yes or 'N' for No.");
        }
    }

    /**
     * Prints a formatted visual header.
     */
    public static void printHeader(String title) {
        System.out.println("\n" + "=".repeat(60));
        int totalWidth = 60;
        int padding = Math.max(0, (totalWidth - title.length()) / 2);
        System.out.println(" ".repeat(padding) + title);
        System.out.println("=".repeat(60));
    }

    /**
     * Prints a section divider line.
     */
    public static void printDivider() {
        System.out.println("-".repeat(60));
    }

    /**
     * Prints a standardized success notification.
     */
    public static void printSuccess(String message) {
        System.out.println(">>> [SUCCESS] " + message);
    }

    /**
     * Prints a standardized error notification.
     */
    public static void printError(String message) {
        System.out.println(">>> [ERROR] " + message);
    }
}
