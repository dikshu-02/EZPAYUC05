package com.ezpay.controller;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.ezpay.service.LoginService;

import java.util.HashMap;
import java.util.Map;

public class LoginControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Mock
	private LoginService loginService;
	
	@InjectMocks
	private LoginController loginController;
	
	private ObjectMapper objectMapper;
	
	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();
		objectMapper = new ObjectMapper();
	}
	
	@Test
	public void testCheckUserId_UserIdExists() throws Exception {
		JsonNode payload = objectMapper.createObjectNode().put("userId", "existingUser");
		when(loginService.checkUserId("existingUser")).thenReturn(true);
		
		mockMvc.perform(put("/api/check_user_id")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(payload)))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("User ID already exists"));
	}
	
	@Test
	public void testCheckUserId_UserIdNotExists() throws Exception {
		JsonNode payload = objectMapper.createObjectNode().put("userId", "newUser");
		when(loginService.checkUserId("newUser")).thenReturn(false);
		
		mockMvc.perform(put("/api/check_user_id")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(payload)))
				.andExpect(status().isOk())
				.andExpect(content().string("User ID not present "));
	}
	
	@Test
	public void testLoginUser_Success() throws Exception {
		JsonNode payload = objectMapper.createObjectNode()
				.put("userId", "user123")
				.put("password", "password123");
		
		when(loginService.authenticate("user123", "password123")).thenReturn(true);
		when(loginService.getIsProfileInfoSetStatus("user123")).thenReturn(true);
		when(loginService.getCustomerId("user123")).thenReturn(1L);
		
		mockMvc.perform(post("/api/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(payload)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Login successful"))
				.andExpect(jsonPath("$.profileInfoSetStatus").value(true))
				.andExpect(jsonPath("$.customerId").value(1));
	}
	
	@Test
	public void testLoginUser_Failure() throws Exception {
		JsonNode payload = objectMapper.createObjectNode()
				.put("userId", "user123")
				.put("password", "wrongPassword");
		
		when(loginService.authenticate("user123", "wrongPassword")).thenReturn(false);
		
		mockMvc.perform(post("/api/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(payload)))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.message").value("Invalid user ID or password"));
	}
	
	@Test
	public void testRegisterUser_Success() throws Exception {
		JsonNode payload = objectMapper.createObjectNode()
				.put("userId", "newUser")
				.put("password", "password123");
		
		when(loginService.registerUser("newUser", "password123")).thenReturn(true);
		
		mockMvc.perform(post("/api/register-user")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(payload)))
				.andExpect(status().isOk())
				.andExpect(content().string("User registered successfully"));
	}
	
	@Test
	public void testRegisterUser_UserIdExists() throws Exception {
		JsonNode payload = objectMapper.createObjectNode()
				.put("userId", "existingUser")
				.put("password", "password123");
		
		when(loginService.registerUser("existingUser", "password123")).thenReturn(false);
		
		mockMvc.perform(post("/api/register-user")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(payload)))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("User ID already exists"));
	}
}