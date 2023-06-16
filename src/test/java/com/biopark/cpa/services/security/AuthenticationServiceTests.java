package com.biopark.cpa.services.security;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import com.biopark.cpa.dto.auth.AuthenticationResponse;
import com.biopark.cpa.form.auth.LoginRequest;
import com.biopark.cpa.repository.auth.BlackListTokenRepository;

@SpringBootTest
public class AuthenticationServiceTests {
	@Autowired
	private AuthenticationService authenticationService;

	@Autowired
	private BlackListTokenRepository blackListTokenRepository;

	@Value("${adminAccount}")
	private String adminEmail;
	@Value("${adminPassword}")
	private String adminPass;

	@Value("${userAccount}")

	@Test
	void authenticateLogin() {
		AuthenticationResponse authenticationResponse = authenticationService.authenticate(
				LoginRequest.builder().email(adminEmail).password(adminPass).build());

		assertAll("Error in login authentication",
				() -> assertEquals(
						AuthenticationResponse.builder().status(HttpStatus.FORBIDDEN)
								.level(null).token(null).build(),
						authenticationService.authenticate(LoginRequest.builder()
								.email("invalid@gmail.com")
								.password("12345678")
								.build()),
						"Allowed invalid email"),

				() -> assertEquals(HttpStatus.OK, authenticationResponse.getStatus(), "wrong status"),
				() -> assertEquals("ROLE_CPA", authenticationResponse.getLevel(), "wrong role"),
				() -> assertNotEquals(null, authenticationResponse.getToken(), "token is null"));
	}

	@Test
	void authenticate() {
		AuthenticationResponse authenticationResponse = authenticationService.authenticate(
				LoginRequest.builder().email(adminEmail).password(adminPass).build());

		String token = authenticationResponse.getToken();
		AuthenticationResponse response = authenticationService.authenticate("Bearer " + token);

		assertAll("Error in authentication token",
				() -> assertEquals(authenticationResponse, response, "Error in authentication token"));
	}

	@Test
	void logout() {
		AuthenticationResponse authenticationResponse = authenticationService.authenticate(
				LoginRequest.builder().email(adminEmail).password(adminPass).build());

		String token = authenticationResponse.getToken();
		Boolean bool = authenticationService.logout("Bearer " + token);
		var tokenInvalid = blackListTokenRepository.findByToken(token);

		assertAll("Error in logout",
				() -> assertEquals(true, bool, "Failed in logout"),
				() -> assertEquals(true, tokenInvalid.isPresent(), "token não registrado no banco"));
	}
}
