package com.example.go_backend;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GoBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}

@SpringBootTest
class BoardTests {

	@Test
	void testAdd(){
		Board goban = new Board();
		assertEquals(goban.Add(1,2), 3, "1+2 should equal 3.");
	}

}
