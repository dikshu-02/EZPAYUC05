package com.ezpay;

import com.ezpay.controller.LoginController;
import com.ezpay.controller.RegistrationController;
import com.ezpay.repository.LoginDataRepository;
import com.ezpay.repository.MasterDataRepository;
import com.ezpay.service.LoginService;
import com.ezpay.service.RegistrationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SmokeTest {
	@Autowired
	private LoginController loginController;
	@Autowired
	private RegistrationController registrationController;
	@Autowired
	private LoginDataRepository loginDataRepository;
	@Autowired
	private MasterDataRepository masterDataRepository;
	@Autowired
	private LoginService loginService;
	@Autowired
	private RegistrationService registrationService;
	
	@Test
	void contextLoads() {
		Assertions.assertNotNull(loginController);
		Assertions.assertNotNull(registrationController);
		Assertions.assertNotNull(loginDataRepository);
		Assertions.assertNotNull(masterDataRepository);
		Assertions.assertNotNull(loginService);
		Assertions.assertNotNull(registrationService);
	}
}
