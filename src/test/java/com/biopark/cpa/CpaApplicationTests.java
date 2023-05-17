package com.biopark.cpa;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
@SpringBootTest
class CpaApplicationTests {
	
	@Test
	void contextLoads() {
		int expected = 3;
		int actual = 3;
		
		assertAll("wrong value", 
			() -> assertEquals(expected, actual)
		);
	}

}
