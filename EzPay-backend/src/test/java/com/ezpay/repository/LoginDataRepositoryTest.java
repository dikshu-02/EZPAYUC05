package com.ezpay.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.ezpay.entity.Customer;
import com.ezpay.entity.LoginData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class LoginDataRepositoryTest {
	
	@Autowired
	private LoginDataRepository loginDataRepository;
	@Autowired
	private MasterDataRepository masterDataRepository;
	
	private LoginData loginData;
	private Customer customer;
	
	@BeforeEach
	public void setUp() {
		loginData = createUniqueLoginData();
		customer = createUniqueCustomer();
		
	}
	/**
	 * @author rohan
	 * @Purpose This method will generate a random integer
	 * @return method will return a random email String
	 */
	protected String getRandomEmail() {
		String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuilder salt = new StringBuilder();
		Random rnd = new Random();
		while (salt.length() < 10) { // length of the random string.
			int index = (int) (rnd.nextFloat() * SALTCHARS.length());
			salt.append(SALTCHARS.charAt(index));
		}
		return salt.toString()+"@gmail.com";
	}
	
	public Customer createUniqueCustomer() {
		Customer customer = new Customer();
		customer.setName("John Doe");
		customer.setEmail(getRandomEmail()); // Unique email
		// creating unique mobile number for tests
		Random rnd = new Random();
		customer.setMobileNumber(String.valueOf(100000 + rnd.nextInt(900000)));
		customer.setAddress("123 Main St");
		customer.setDob(LocalDateTime.now());
		customer.setGender("Male");
		customer.setProfileCreationDate(LocalDateTime.now());
		customer.setProfileLastUpdatedDate(LocalDateTime.now());
		customer.setIsProfileInfoSet(true);
		// creating unique upi id for tests
		customer.setUpiId(customer.getName()+"@"+customer.getMobileNumber());
		// creating unique account number for tests
		customer.setBankAccountNumber(String.valueOf(100000 + rnd.nextInt(900000)));
		customer.setIfscCode("IFSC123");
		customer.setAccountType(1);
		return customer;
	}
	private LoginData createUniqueLoginData() {
		LoginData loginData = new LoginData();
		loginData.setUserId("user" + System.currentTimeMillis()); // Unique user ID
		loginData.setPasswordHash("hashedPassword123");
		loginData.setBlockedCode(0);
		// Assume there's a method to set Customer
		loginData.setCustomer(customer);
		return loginData;
	}
	
	@Test
	public void testSaveLoginData() {
		loginDataRepository.save(loginData);
		assertNotNull(loginData.getUserId());
	}
	
	@Test
	public void testFindByUserId() {
		loginDataRepository.save(loginData);
		Optional<LoginData> found = loginDataRepository.findByUserId(loginData.getUserId());
		assertTrue(found.isPresent());
		assertEquals(loginData.getUserId(), found.get().getUserId());
	}
	
	@Test
	public void testFindByNonExistingUserId() {
		Optional<LoginData> found = loginDataRepository.findByUserId("nonexistentUser");
		assertFalse(found.isPresent());
	}
	
	@Test
	public void testUpdateLoginData() {
		loginDataRepository.save(loginData);
		loginData.setPasswordHash("newHashedPassword456");
		loginDataRepository.save(loginData);
		Optional<LoginData> updatedLoginData = loginDataRepository.findByUserId(loginData.getUserId());
		assertTrue(updatedLoginData.isPresent());
		assertEquals("newHashedPassword456", updatedLoginData.get().getPasswordHash());
	}
	
	@Test
	public void testDeleteLoginData() {
		loginDataRepository.save(loginData);
		loginDataRepository.delete(loginData);
		Optional<LoginData> found = loginDataRepository.findByUserId(loginData.getUserId());
		assertFalse(found.isPresent());
	}
	
	@Test
	public void testUniqueConstraintsOnUserId() {
		loginDataRepository.save(loginData);
		LoginData duplicateLoginData = createUniqueLoginData();
		duplicateLoginData.setUserId(loginData.getUserId());
		assertThrows(InvalidDataAccessApiUsageException.class, () -> {
			loginDataRepository.save(duplicateLoginData);
		});
	}
	
	@Test
	public void testSaveLoginDataWithNullFields() {
		LoginData invalidLoginData = new LoginData();
		assertThrows(Exception.class, () -> {
			loginDataRepository.save(invalidLoginData);
		});
	}
}