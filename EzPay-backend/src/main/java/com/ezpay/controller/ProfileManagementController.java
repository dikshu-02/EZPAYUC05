//UC5 added an encryption to the entered address before storing it into the database for update(), also in getCustomerbyID function decrypt before sending
package com.ezpay.controller;

import org.slf4j.Logger;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ezpay.service.KeyService;
import com.ezpay.service.ProfileManagementService;
import com.ezpay.entity.Customer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/customers")
@CrossOrigin(origins = "http://localhost:3000")
public class ProfileManagementController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileManagementController.class);

    @Autowired
    private ProfileManagementService customerService;
    
    @Autowired
    private KeyService keyService;

    // Fetch a customer by ID
    @GetMapping("/by-id/{id}")
    public Customer getCustomerById(@PathVariable Long id) {
        logger.info("Fetching customer with ID: {}", id);
        Customer customer = customerService.getCustomerById(id);
        logger.info("Fetched customer details: {}", customer);
        //2 statements Added by UC5
        customer.setAddress(keyService.decryptText(customer.getAddress(),id));
        customer.setBankAccountNumber(keyService.decryptText(customer.getBankAccountNumber(),id));
        return customer;
    }
    
    
    // Verify password before profile update
    @PostMapping("/verify-password")
    public ResponseEntity<?> verifyPassword(@RequestBody Map<String, String> requestData) {
        String customerId = requestData.get("customerId");
        String password = requestData.get("password");

        boolean isValidPassword = customerService.verifyPassword(Long.parseLong(customerId), password);

        if (isValidPassword) {
            return ResponseEntity.ok("Password verified");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect password");
        }
    }

    // Update customer details using customerId from request header
    @PutMapping("/update")
    public ResponseEntity<?> updateCustomerDetails(@RequestParam Long customerId, @RequestBody Customer updatedCustomer) {
        try {
        	Customer existingCustomer = customerService.getCustomerById(customerId);

            if (existingCustomer == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found");
            }

            // Added by UC5 Encrypt the new address entered before updating into the database
            updatedCustomer.setAddress(keyService.encryptText(updatedCustomer.getAddress(),customerId));
            
            
            // Set updated fields
            existingCustomer.setName(updatedCustomer.getName());
            existingCustomer.setEmail(updatedCustomer.getEmail());
            existingCustomer.setMobileNumber(updatedCustomer.getMobileNumber());
            existingCustomer.setAddress(updatedCustomer.getAddress());
            //existingCustomer.setProfileLastUpdatedDate(LocalDateTime.now());  // Example of updating timestamp
            
            // Save updated customer
            customerService.saveCustomer(existingCustomer);
            return ResponseEntity.ok("Customer updated successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update customer: " + e.getMessage());
        }
    }

}
