package com.example.go_backend;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

public class PlayerTest {
  @Test
  public void PlayerConstructorTest() {
    try {
      Player p = new Player('W');
    } catch (Exception e) {
      assertEquals("Player color must be black or white", e.getMessage());
    }
    Player b = new Player('B');
    assertEquals('B', b.getColor());
    assertEquals(0, b.getCapturedPrisoners());
    b.setCapturedPrisoners(1);
    assertEquals(1, b.getCapturedPrisoners());

  }

}
