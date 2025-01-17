package com.example.go_backend;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class StoneTest {

  Stone whiteStone = new Stone("White", 0,0,1);
  Stone blackStone = new Stone("Black", 1,1,2);

  @Test
  public void getColorTest(){
    assertEquals(whiteStone.getColor(), 'W', "Stone did not have expected color.");
    assertEquals(blackStone.getColor(), 'B', "Stone did not have expected color.");
  }

  @Test
  public void getXTest(){
    assertEquals(whiteStone.getX(), 0, "Stone did not have expected x value.");
    assertEquals(blackStone.getX(), 1, "Stone did not have expected x value.");
  }

  @Test
  public void getYTest(){
    assertEquals(whiteStone.getY(), 0, "Stone did not have expected y value.");
    assertEquals(blackStone.getY(), 1, "Stone did not have expected y value.");
  }

  @Test
  public void getLibertiesTest(){
    assertEquals(whiteStone.getLiberties(), 4, "Stone did not have expected default liberties.");
    whiteStone.setLiberties(3);
    assertEquals(whiteStone.getLiberties(), 3, "Stone did not have accurately set liberties.");
  }

  
}
