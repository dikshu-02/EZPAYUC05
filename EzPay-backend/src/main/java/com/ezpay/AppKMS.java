package com.ezpay;
import org.springframework.context.ApplicationContext;



import java.util.Scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ezpay.controller.KeyController;

@SpringBootApplication
public class AppKMS {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(AppKMS.class, args);

        // Get the KeyController bean from the Spring context
        KeyController keyController = context.getBean(KeyController.class);
        //FraudDetectionController fraudController = context.getBean(FraudDetectionController.class);


        Scanner scanner = new Scanner(System.in);

        while (true) {
            // Display the options to the user
            System.out.println("Choose an option:");
            System.out.println("1. Register a new key");
            //System.out.println("2. Login");
            System.out.println("2. Check update and update if req");
            System.out.println("3. Check encryption and decryption");
            //System.out.println("4. Flag Login Attempt");
            System.out.println("4. Exit");

            // Get user choice
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume the newline character

            // Check if the user wants to exit
            if (choice == 4) {
                System.out.println("Exiting...");
                break;
            }

            // Get the customerId from the user
            System.out.println("Enter the customer id:");
            Long customerId = Long.parseLong(scanner.nextLine());

            // Call the appropriate method based on the user's choice
            switch (choice) {
                case 1:
                    // Register a new key
                    keyController.registerKey(customerId);
                    break;
                case 2:
                    // Reset password (Update key)
                    keyController.checkUpdateKey(customerId);
                    break;
                case 3:
                    // Check encryption and decryption
                    try {
                        // Get the plaintext from the user
                        System.out.println("Enter the plaintext to encrypt:");
                        String plaintext = scanner.nextLine();

                        // Encrypt the plaintext
                        String encryptedText = keyController.encryptText(plaintext, customerId);
                        System.out.println("Encrypted text: " + encryptedText);

                        // Decrypt the encrypted text
                        String decryptedText = keyController.decryptText(encryptedText, customerId);
                        System.out.println("Decrypted text: " + decryptedText);
                    } catch (Exception e) {
                        // Handle and print exception details
                        System.out.println("An error occurred during encryption/decryption:");
                        e.printStackTrace();
                    }
                    break;
                
                	
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }

    }
}
