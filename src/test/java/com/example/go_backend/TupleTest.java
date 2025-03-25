package com.example.go_backend;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

public class TupleTest {

  Tuple t1 = new Tuple(1, 1);

  @Test
  public void equalsTest() {

    Tuple t2 = new Tuple(1, 1);
    Tuple t3 = new Tuple(1, 2);
    Board newBoard = new Board(9, 9);
    assertEquals(true, t1.equals(t1));
    assertEquals(true, t1.equals(t2));
    assertEquals(false, t1.equals(t3));
    assertEquals(false, t1.equals(newBoard));
    assertEquals(false, t1.equals(null));
  }

  public void toStringTest() {
    assertEquals("(1,1)", t1.toString());
  }

  public void hashCodeTest() {
    assertEquals(54, t1.hashCode());
  }

}
