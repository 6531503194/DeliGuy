package com.deliguy.delivery;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// @SpringBootTest
@SpringBootTest(
  properties = {
    "spring.security.oauth2.resourceserver.jwt.jwk-set-uri="
  }
)
class DeliveryApplicationTests {

	@Test
	void contextLoads() {
	}

}
