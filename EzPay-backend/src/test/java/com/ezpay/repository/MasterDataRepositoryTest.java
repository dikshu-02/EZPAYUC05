package com.ezpay.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.ezpay.entity.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class MasterDataRepositoryTest {
	
	@Autowired
	private MasterDataRepository masterDataRepository;
	
	private Customer customer;
	
	@BeforeEach
	public void setUp() {
		customer = createUniqueCustomer();
	}
	
	/**
	 * @author rohan
	 * @Purpose This method will generate a random integer
	 * @return method will return a random email String
	 */
static	protected String getRandomEmail() {
		String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuilder salt = new StringBuilder();
		Random rnd = new Random();
		while (salt.length() < 10) { // length of the random string.
			int index = (int) (rnd.nextFloat() * SALTCHARS.length());
			salt.append(SALTCHARS.charAt(index));
		}
		return salt.toString()+"@gmail.com";
	}
	
	
	static  public Customer createUniqueCustomer() {
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
	@Test
	public void testSaveCustomer() {
		masterDataRepository.save(customer);
		assertNotNull(customer.getCustomerId());
	}
	
	@Test
	public void testFindByEmail() {
		masterDataRepository.save(customer);
		List<Customer> found = masterDataRepository.findByEmail(customer.getEmail());
		assertEquals(1, found.size());
		assertEquals(customer.getEmail(), found.get(0).getEmail());
	}
	
	@Test
	public void testFindByNonExistingEmail() {
		List<Customer> found = masterDataRepository.findByEmail("nonexistent@example.com");
		assertTrue(found.isEmpty());
	}
	
	@Test
	public void testUpdateCustomer() {
		masterDataRepository.save(customer);
		customer.setName("Jane Doe");
		masterDataRepository.save(customer);
		Customer updatedCustomer = masterDataRepository.findById(customer.getCustomerId()).orElse(null);
		assertEquals("Jane Doe", updatedCustomer.getName());
	}
	
	@Test
	public void testDeleteCustomer() {
		masterDataRepository.save(customer);
		masterDataRepository.delete(customer);
		Optional<Customer> found = masterDataRepository.findById(customer.getCustomerId());
		assertFalse(found.isPresent());
	}
	
	@Test
	public void testUniqueConstraints() {
		masterDataRepository.save(customer);
		Customer duplicateCustomer = createUniqueCustomer();
		duplicateCustomer.setEmail(customer.getEmail());
		assertThrows(DataIntegrityViolationException.class, () -> {
			masterDataRepository.save(duplicateCustomer);
		});
	}
	
	@Test
	public void testFindAllCustomers() {
		masterDataRepository.save(customer);
		List<Customer> customers = masterDataRepository.findAll();
		assertFalse(customers.isEmpty());
	}
	
	@Test
	public void testSaveCustomerWithNullFields() {
		Customer invalidCustomer = new Customer();
		assertThrows(Exception.class, () -> {
			masterDataRepository.save(invalidCustomer);
		});
	}
}