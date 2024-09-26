package com.ezpay.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ezpay.entity.Customer;
import com.ezpay.repository.MasterDataRepository;
import com.ezpay.service.RegistrationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.ezpay.repository.MasterDataRepositoryTest;

public class RegistrationControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	
	
	@Mock
	private RegistrationService registrationService;
	@InjectMocks
	private RegistrationController registrationController;
	
	private ObjectMapper objectMapper;
	
	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(registrationController).build();
		objectMapper = new ObjectMapper();
	}
	
	@Test
	public void testCheckEmail() throws Exception {
		ObjectNode payload = objectMapper.createObjectNode();
		payload.put("email", "test@example.com");
		mockMvc.perform(MockMvcRequestBuilders.put("/api/check_if_email_present")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(payload)))
				.andExpect(MockMvcResultMatchers.status().is(401))
				.andExpect(MockMvcResultMatchers.content().string("Email already registered"));
	}
	@Test
	public void testAddProfileDetails_Success() throws Exception {
		JsonNode payload = objectMapper.createObjectNode().put("name", "John Doe");
		when(registrationService.AddInitialProfileDetails(payload)).thenReturn(true);
		
		mockMvc.perform(put("/api/add-profile-details")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(payload)))
				.andExpect(status().isOk())
				.andExpect(content().string("Profile creation Completed"));
	}
	
	@Test
	public void testAddProfileDetails_Failure() throws Exception {
		JsonNode payload = objectMapper.createObjectNode();
		when(registrationService.AddInitialProfileDetails(payload)).thenReturn(false);
		
		mockMvc.perform(put("/api/add-profile-details")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(payload)))
				.andExpect(status().isUnauthorized())
				.andExpect(content().string("Profile Details missing"));
	}
	
	
	@Test
	public void testGetProfile_NotFound() throws Exception {
		Long customerId = 1L;
		when(registrationService.getCustomerProfile(customerId)).thenReturn(null);
		
		mockMvc.perform(get("/api/view-profile")
						.header("Key", customerId))
				.andExpect(status().isNotFound());
	}
}