package com.ezpay.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.ezpay.entity.Customer;
import com.ezpay.entity.LoginData;
import com.ezpay.repository.LoginDataRepository;
import com.ezpay.utils.PasswordUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

public class LoginServiceTest {
	
	@InjectMocks
	private LoginService loginService;
	
	@Mock
	private LoginDataRepository loginDataRepository;
	
	@Mock
	private RegistrationService registrationService;
	
	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	public void testAuthenticate_Success() {
		String userId = "user123";
		String password = "password123";
		String hashedPassword = PasswordUtils.hashPassword(password);
		
		LoginData loginData = new LoginData();
		loginData.setUserId(userId);
		loginData.setPasswordHash(hashedPassword);
		
		when(loginDataRepository.findById(userId)).thenReturn(Optional.of(loginData));
		
		boolean result = loginService.authenticate(userId, password);
		assertTrue(result);
	}
	
	@Test
	public void testAuthenticate_Failure_UserNotFound() {
		String userId = "user123";
		String password = "password123";
		
		when(loginDataRepository.findById(userId)).thenReturn(Optional.empty());
		
		boolean result = loginService.authenticate(userId, password);
		assertFalse(result);
	}
	
	@Test
	public void testAuthenticate_Failure_InvalidPassword() {
		String userId = "user123";
		String password = "wrongPassword";
		String hashedPassword = PasswordUtils.hashPassword("correctPassword");
		
		LoginData loginData = new LoginData();
		loginData.setUserId(userId);
		loginData.setPasswordHash(hashedPassword);
		
		when(loginDataRepository.findById(userId)).thenReturn(Optional.of(loginData));
		
		boolean result = loginService.authenticate(userId, password);
		assertFalse(result);
	}
	
	@Test
	public void testCheckUserId_Exists() {
		String userId = "existingUser";
		
		when(loginDataRepository.findById(userId)).thenReturn(Optional.of(new LoginData()));
		
		boolean result = loginService.checkUserId(userId);
		assertTrue(result);
	}
	
	@Test
	public void testCheckUserId_NotExists() {
		String userId = "nonExistingUser";
		
		when(loginDataRepository.findById(userId)).thenReturn(Optional.empty());
		
		boolean result = loginService.checkUserId(userId);
		assertFalse(result);
	}
	
	@Test
	public void testRegisterUser_Success() {
		String userId = "newUser";
		String password = "password123";
		
		when(loginDataRepository.findByUserId(userId)).thenReturn(Optional.empty());
		Customer customer = new Customer();
		customer.setIsProfileInfoSet(true);
		when(registrationService.AddTempProfileDetailsAndSave(userId)).thenReturn(customer);
		
		boolean result = loginService.registerUser(userId, password);
		assertTrue(result);
		
		verify(loginDataRepository).save(any(LoginData.class));
	}
	
	@Test
	public void testRegisterUser_UserIdExists() {
		String userId = "existingUser";
		String password = "password123";
		
		when(loginDataRepository.findByUserId(userId)).thenReturn(Optional.of(new LoginData()));
		
		boolean result = loginService.registerUser(userId, password);
		assertFalse(result);
		
		verify(loginDataRepository, never()).save(any(LoginData.class));
	}
	
	@Test
	public void testGetIsProfileInfoSetStatus() {
		String userId = "user123";
		LoginData loginData = new LoginData();
		Customer customer = new Customer();
		customer.setIsProfileInfoSet(true);
		loginData.setCustomer(customer);
		
		when(loginDataRepository.findById(userId)).thenReturn(Optional.of(loginData));
		
		boolean result = loginService.getIsProfileInfoSetStatus(userId);
		assertTrue(result);
	}
	
	@Test
	public void testGetCustomerId() {
		String userId = "user123";
		Long customerId = 1L;
		LoginData loginData = new LoginData();
		Customer customer = new Customer();
		customer.setCustomerId(customerId);
		loginData.setCustomer(customer);
		
		when(loginDataRepository.findById(userId)).thenReturn(Optional.of(loginData));
		
		Long result = loginService.getCustomerId(userId);
		assertEquals(customerId, result);
	}
}