package com.ezpay.service;
//author:Aman Rauth

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ezpay.entity.Customer;
import com.ezpay.entity.LoginData;
import com.ezpay.repository.LoginDataRepository;
import com.ezpay.repository.ProfileManagementRepository;
import com.ezpay.utils.PasswordUtils;
@Service
public class ProfileManagementService {
    @Autowired
    private ProfileManagementRepository customerRepository;
    @Autowired
    private LoginDataRepository loginDataRepository;

    public Customer getCustomerById(Long customerId) {  
        return customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }
    //to add a new customer
    public Customer addCustomer(Customer customer) {
        return customerRepository.save(customer);
    }
    
    // Verify the password for a customer using BCrypt hashing
    //Author:Snehal
    public boolean verifyPassword(Long customerId, String inputPassword) {
        // Find the customer
        Customer customer = customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Fetch LoginData for the customer
        LoginData loginData = loginDataRepository.findByCustomer(customer)
                .orElseThrow(() -> new RuntimeException("Login data not found for the customer"));

        // Use PasswordUtils to compare the hashed password with the input password
        return PasswordUtils.verifyPassword(inputPassword, loginData.getPasswordHash());
    }
    
    // Update customer profile
    //Author:Snehal
    //Helps in saving the updated profile
    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }
    public Customer updateProfile(Customer customer) {
    	Customer existingProfile = customerRepository.findByCustomerId(customer.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        existingProfile.setName(customer.getName());
        existingProfile.setEmail(customer.getEmail());
        existingProfile.setMobileNumber(customer.getMobileNumber());
        existingProfile.setAddress(customer.getAddress());
        existingProfile.setIfscCode(customer.getIfscCode());
        existingProfile.setBankAccountNumber(customer.getBankAccountNumber());
        existingProfile.setGender(customer.getGender());
        existingProfile.setDob(customer.getDob());
        existingProfile.setAccountType(customer.getAccountType());
        existingProfile.setProfilePictureUrl(customer.getProfilePictureUrl());
        return customerRepository.save(existingProfile);
    }
    
}




/*
@Service
public class ProfileManagementService {
	@Autowired
    private ProfileManagementRepository customerRepository;

    public List<CustomerProjection> getCustomersByName(String name) {
        return customerRepository.findByName(name);
    }
    public Customer addCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public int updateCustomerDetails(int customerId, String name, String email, String mobileNumber, String address, String ifscCode, String accNo) {
        return customerRepository.updateCustomerDetails(customerId, name, email, mobileNumber, address, ifscCode, accNo);
    }
    //Author:SNehal
    
    public Customer getCustomerById(int customerId) {
        return customerRepository.findCustomerById(customerId) 
            .orElseThrow(() -> new RuntimeException("Customer not found"));
    }


}
*/