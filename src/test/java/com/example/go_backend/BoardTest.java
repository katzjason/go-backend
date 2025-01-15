package com.example.go_backend;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BoardTest {

  @Test
  public void addStoneTest(){
    Board goban = new Board(9,9);
    assertEquals(goban.getStone(0,0), null, "Board was not empty after initialization.");
    assertEquals(goban.getStone(1,1), null, "Board was not empty after initialization.");
    Stone whiteStone = new Stone("White", 0,0);
    goban.addStone(whiteStone);
    assertEquals(goban.getStone(0,0), whiteStone, "Stone was not placed on board.");
    Stone blackStone = new Stone("Black", 1,1);
    goban.addStone(blackStone);
    assertEquals(goban.getStone(1,1), blackStone, "Stone was not placed on board.");
    Stone repeatStone = new Stone("Black", 0,0);
    goban.addStone(repeatStone);
    assertEquals(goban.getStone(0,0), whiteStone, "Stone was placed on top of an existing stone.");
    goban.addStone(new Stone("Black", 2,0));
    goban.addStone(new Stone("Black", 3,1));
    goban.addStone(new Stone("Black", 2,2));
    goban.addStone(new Stone("White", 2, 1));
    assertEquals(goban.getStone(2,1), null, "Stone was added in an immediate capture area.");
  }


  @Test
  public void calculateLibertiesTest(){
    Board goban = new Board(9,9);

    // stones outside board should throw an exception
    HashSet<Tuple> visited = new HashSet<Tuple>();
    List<Tuple> xyList = new ArrayList<>();
    xyList.add(new Tuple(-1, 0));
    xyList.add(new Tuple(0, -1));
    xyList.add(new Tuple(-1, -1));
    xyList.add(new Tuple(10, 0));
    xyList.add(new Tuple(0, 11));
    xyList.add(new Tuple(12, 12));

    int[] i = {0};
    while (i[0] < xyList.size()){
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
        goban.calculateLiberties(xyList.get(i[0]).first, xyList.get(i[0]).second, 'B', visited);
      });
      assertEquals("X and Y must be non-negative and within the size of the board.", exception.getMessage());
      i[0]++;
    }

    // stones in the middle of the board
    goban.addStone(new Stone("White", 1, 1));
    assertEquals(4, goban.calculateLiberties(1,1,'W',visited));
    visited.clear();
    goban.addStone(new Stone("Black", 1, 0));
    assertEquals(3, goban.calculateLiberties(1,1,'W',visited));
    visited.clear();
    goban.addStone(new Stone("Black", 1, 2));
    assertEquals(2, goban.calculateLiberties(1,1,'W',visited));
    visited.clear();
    goban.addStone(new Stone("Black", 0, 1));
    assertEquals(1, goban.calculateLiberties(1,1,'W',visited));
    visited.clear();
    goban.addStone(new Stone("Black", 2, 1));
    assertEquals(0, goban.calculateLiberties(1,1,'W',visited));
    visited.clear();

    // corner  cases
    goban.addStone(new Stone("White", 0,8));
    goban.addStone(new Stone("Black", 0, 7));
    assertEquals(1, goban.calculateLiberties(0,8,'W',visited));
    visited.clear();
    goban.addStone(new Stone("Black", 1, 8));
    assertEquals(0, goban.calculateLiberties(0,8,'W',visited));
    visited.clear();
    goban.addStone(new Stone("Black", 8,0));
    goban.addStone(new Stone("White", 8, 1));
    assertEquals(1, goban.calculateLiberties(8,0,'B',visited));
    visited.clear();
    goban.addStone(new Stone("White", 7, 0));
    assertEquals(0, goban.calculateLiberties(8,0,'B',visited));
    visited.clear();

    // group of stones
    Board goban1 = new Board(9,9);
    goban1.addStone(new Stone("White", 2,2));
    assertEquals(4, goban1.calculateLiberties(2,2,'W',visited));
    visited.clear();
    goban1.addStone(new Stone("White", 2,1));
    assertEquals(6, goban1.calculateLiberties(2,2,'W',visited));
    visited.clear();
    goban1.addStone(new Stone("White", 3,1));
    assertEquals(7, goban1.calculateLiberties(2,2,'W',visited));
    visited.clear();
    goban1.addStone(new Stone("Black", 3,2));
    assertEquals(6, goban1.calculateLiberties(2,2,'W',visited));
    visited.clear();
    goban1.addStone(new Stone("White", 3,3));
    assertEquals(6, goban1.calculateLiberties(2,2,'W',visited));
    visited.clear();
    }

    @Test
    public void removeStoneTest(){
      Board goban = new Board(9,9);
      goban.addStone(new Stone("White", 1, 0));
      goban.removeStone(1, 0);
      assertEquals(null, goban.getStone(1,0));
    }

    @Test
    public void floodFillTest(){
      Board goban = new Board(9,9);
      HashSet<Tuple> visited = new HashSet<Tuple>();
      goban.addStone(new Stone("White", 0, 0));
      assertEquals(1, goban.floodFill(0, 0, 'W', visited).size());
      visited.clear();
      goban.addStone(new Stone("White", 0, 1));
      goban.addStone(new Stone("Black", 0, 2));
      assertEquals(2, goban.floodFill(0, 0, 'W', visited).size());
      visited.clear();
      assertEquals(1, goban.floodFill(0, 2, 'B', visited).size());
      visited.clear();
      assertEquals(0, goban.floodFill(0, 2, 'W', visited).size());
      visited.clear();
      goban.addStone(new Stone("White", 1, 0));
      goban.addStone(new Stone("White", 1, 1));
      goban.addStone(new Stone("White", 1, 2));
      goban.addStone(new Stone("White", 2, 1));
      assertEquals(6, goban.floodFill(1, 1, 'W', visited).size());
      visited.clear();
    }


    @Test
    public void capturePrisonersTest(){
      Board goban = new Board(9,9);
      Stone blackStone = new Stone("Black", 1, 1);
      goban.addStone(blackStone);
      goban.addStone(new Stone("White", 1, 0));
      goban.addStone(new Stone("White", 1, 2));
      goban.addStone(new Stone("White", 0, 1));
      goban.addStone(new Stone("White", 2, 1));
      assertEquals(blackStone, goban.getStone(1, 1));
      goban.capturePrisoners();
      assertNull(goban.getStone(1,1));
      goban.removeStone(2, 1);
      assertNull(goban.getStone(2, 1));
      goban.addStone(new Stone("White", 2, 0));
      goban.addStone(new Stone("White", 2, 2));
      Stone blackStone2 = new Stone("Black", 1, 1);
      goban.addStone(blackStone2);
      goban.capturePrisoners();
      assertEquals(blackStone2, goban.getStone(1, 1));
      Stone blackStone3 = new Stone("Black", 2, 1);
      goban.addStone(blackStone3);
      assertEquals(blackStone3, goban.getStone(2, 1));
      goban.addStone(new Stone("White", 3, 1));
      goban.capturePrisoners();
      assertNull(goban.getStone(1, 1));
      assertNull(goban.getStone(2, 1));
    }
  }
