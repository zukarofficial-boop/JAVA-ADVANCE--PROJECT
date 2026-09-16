package com.helpdesk.util;

import java.util.Scanner;

/**
 * Utility helper providing robust console input reading and validation.
 * Prevents input mismatch crashes.
 */
public class InputValidator {

    public static int readInt(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int val = Integer.parseInt(input);
                if (val >= min && val <= max) {
                    return val;
                }
                System.out.printf("  [!] Please enter a number between %d and %d.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("  [!] Invalid number format. Please enter an integer.");
            }
        }
    }

    public static String readNonEmptyString(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("  [!] Input cannot be blank. Please try again.");
        }
    }

    public static String readEmail(Scanner scanner, String prompt) {
        while (true) {
            String email = readNonEmptyString(scanner, prompt);
            if (email.contains("@") && email.contains(".")) {
                return email;
            }
            System.out.println("  [!] Invalid email format (must contain '@' and domain).");
        }
    }

    public static void pressEnterToContinue(Scanner scanner) {
        System.out.print("\nPress ENTER to continue...");
        scanner.nextLine();
    }
}
