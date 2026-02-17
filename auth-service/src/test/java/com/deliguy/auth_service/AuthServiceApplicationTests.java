package com.deliguy.auth_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// @SpringBootTest
@SpringBootTest(
  properties = {
    "spring.security.oauth2.resourceserver.jwt.jwk-set-uri="
  }
)
class AuthServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
