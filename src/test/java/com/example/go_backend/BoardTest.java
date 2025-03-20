package com.example.go_backend;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BoardTest {

  @Test
  public void addStoneTest() {
    Player black = new Player('B');
    Player white = new Player('W');
    Board goban = new Board(9, 9);
    assertEquals(goban.getStone(0, 0), null, "Board was not empty after initialization.");
    assertEquals(goban.getStone(1, 1), null, "Board was not empty after initialization.");
    Stone whiteStone = new Stone("White", 0, 0, 1);
    goban.addStone(whiteStone);
    assertEquals(goban.getStone(0, 0), whiteStone, "Stone was not placed on board.");
    Stone blackStone = new Stone("Black", 1, 1, 2);
    goban.addStone(blackStone);
    assertEquals(goban.getStone(1, 1), blackStone, "Stone was not placed on board.");
    Stone repeatStone = new Stone("Black", 0, 0, 4);
    goban.addStone(repeatStone);
    assertEquals(goban.getStone(0, 0), whiteStone, "Stone was placed on top of an existing stone.");
    goban.addStone(new Stone("Black", 2, 0, 6));
    goban.addStone(new Stone("Black", 3, 1, 8));
    goban.addStone(new Stone("Black", 2, 2, 10));
    goban.addStone(new Stone("White", 2, 1, 11));
    assertEquals(goban.getStone(2, 1), null, "Stone was added in an immediate capture area.");

    // testing ko rule
    Board goban2 = new Board(9, 9);
    HashSet<Tuple> visited = new HashSet<Tuple>();
    goban2.addStone(new Stone("White", 2, 0, 2));
    Stone toBeCaptured = new Stone("White", 1, 1, 4);
    goban2.addStone(toBeCaptured);
    goban2.addStone(new Stone("White", 2, 2, 6));
    goban2.addStone(new Stone("White", 3, 1, 8));
    assertEquals(4, goban2.calculateLiberties(1, 1, 'W', visited), "wrong");
    visited.clear();
    goban2.addStone(new Stone("Black", 1, 0, 1));
    goban2.addStone(new Stone("Black", 0, 1, 3));
    goban2.addStone(new Stone("Black", 2, 1, 5));
    assertEquals(goban2.getStone(2, 1), null, "Illegal stone was placed on board.");
    goban2.addStone(new Stone("Black", 1, 2, 7));
    assertEquals(1, goban2.calculateLiberties(1, 1, 'W', visited), "Wrong number of liberties.");
    visited.clear();
    Stone newCaptureStone = new Stone("Black", 2, 1, 9);
    goban2.addStone(newCaptureStone);
    assertEquals(newCaptureStone, goban2.getStone(2, 1), "Stone was not placed on board.");
    assertEquals(toBeCaptured, goban2.getStone(1, 1), "Captured stone removed prematurely.");
    goban2.capturePrisoners(white);
    assertEquals(null, goban2.getStone(1, 1), "Captured stone was not removed.");
    assertEquals(newCaptureStone, goban2.getStone(2, 1), "Legal stone was removed.");

  }

  @Test
  public void calculateLibertiesTest() {
    Board goban = new Board(9, 9);

    // stones outside board should throw an exception
    HashSet<Tuple> visited = new HashSet<Tuple>();
    List<Tuple> xyList = new ArrayList<>();
    xyList.add(new Tuple(-1, 0));
    xyList.add(new Tuple(0, -1));
    xyList.add(new Tuple(-1, -1));
    xyList.add(new Tuple(10, 0));
    xyList.add(new Tuple(0, 11));
    xyList.add(new Tuple(12, 12));

    int[] i = { 0 };
    while (i[0] < xyList.size()) {
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
        goban.calculateLiberties(xyList.get(i[0]).first, xyList.get(i[0]).second, 'B', visited);
      });
      assertEquals("X and Y must be non-negative and within the size of the board.", exception.getMessage());
      i[0]++;
    }

    // stones in the middle of the board
    goban.addStone(new Stone("White", 1, 1, 1));
    assertEquals(4, goban.calculateLiberties(1, 1, 'W', visited));
    visited.clear();
    goban.addStone(new Stone("Black", 1, 0, 1));
    assertEquals(3, goban.calculateLiberties(1, 1, 'W', visited));
    visited.clear();
    goban.addStone(new Stone("Black", 1, 2, 3));
    assertEquals(2, goban.calculateLiberties(1, 1, 'W', visited));
    visited.clear();
    goban.addStone(new Stone("Black", 0, 1, 5));
    assertEquals(1, goban.calculateLiberties(1, 1, 'W', visited));
    visited.clear();
    goban.addStone(new Stone("Black", 2, 1, 7));
    assertEquals(0, goban.calculateLiberties(1, 1, 'W', visited));
    visited.clear();

    // corner cases
    goban.addStone(new Stone("White", 0, 8, 8));
    goban.addStone(new Stone("Black", 0, 7, 9));
    assertEquals(1, goban.calculateLiberties(0, 8, 'W', visited));
    visited.clear();
    goban.addStone(new Stone("Black", 1, 8, 11));
    assertEquals(0, goban.calculateLiberties(0, 8, 'W', visited));
    visited.clear();
    goban.addStone(new Stone("Black", 8, 0, 13));
    goban.addStone(new Stone("White", 8, 1, 14));
    assertEquals(1, goban.calculateLiberties(8, 0, 'B', visited));
    visited.clear();
    goban.addStone(new Stone("White", 7, 0, 14));
    assertEquals(0, goban.calculateLiberties(8, 0, 'B', visited));
    visited.clear();

    // group of stones
    Board goban1 = new Board(9, 9);
    goban1.addStone(new Stone("White", 2, 2, 2));
    assertEquals(4, goban1.calculateLiberties(2, 2, 'W', visited));
    visited.clear();
    goban1.addStone(new Stone("White", 2, 1, 4));
    assertEquals(6, goban1.calculateLiberties(2, 2, 'W', visited));
    visited.clear();
    goban1.addStone(new Stone("White", 3, 1, 6));
    assertEquals(7, goban1.calculateLiberties(2, 2, 'W', visited));
    visited.clear();
    goban1.addStone(new Stone("Black", 3, 2, 7));
    assertEquals(6, goban1.calculateLiberties(2, 2, 'W', visited));
    visited.clear();
    goban1.addStone(new Stone("White", 3, 3, 8));
    assertEquals(6, goban1.calculateLiberties(2, 2, 'W', visited));
    visited.clear();
  }

  @Test
  public void removeStoneTest() {
    Board goban = new Board(9, 9);
    goban.addStone(new Stone("White", 1, 0, 2));
    goban.removeStone(1, 0);
    assertEquals(null, goban.getStone(1, 0));
  }

  @Test
  public void floodFillTest() {
    Board goban = new Board(9, 9);
    HashSet<Tuple> visited = new HashSet<Tuple>();
    goban.addStone(new Stone("White", 0, 0, 2));
    assertEquals(1, goban.floodFill(0, 0, 'W', visited).size());
    visited.clear();
    goban.addStone(new Stone("White", 0, 1, 4));
    goban.addStone(new Stone("Black", 0, 2, 5));
    assertEquals(2, goban.floodFill(0, 0, 'W', visited).size());
    visited.clear();
    assertEquals(1, goban.floodFill(0, 2, 'B', visited).size());
    visited.clear();
    assertEquals(0, goban.floodFill(0, 2, 'W', visited).size());
    visited.clear();
    goban.addStone(new Stone("White", 1, 0, 6));
    goban.addStone(new Stone("White", 1, 1, 8));
    goban.addStone(new Stone("White", 1, 2, 10));
    goban.addStone(new Stone("White", 2, 1, 12));
    assertEquals(6, goban.floodFill(1, 1, 'W', visited).size());
    visited.clear();
  }

  @Test
  public void capturePrisonersTest() {
    Player black = new Player('B');
    Player white = new Player('W');
    Board goban = new Board(9, 9);
    Stone blackStone = new Stone("Black", 1, 1, 1);
    goban.addStone(blackStone);
    goban.addStone(new Stone("White", 1, 0, 2));
    goban.addStone(new Stone("White", 1, 2, 4));
    goban.addStone(new Stone("White", 0, 1, 6));
    goban.addStone(new Stone("White", 2, 1, 8));
    assertEquals(blackStone, goban.getStone(1, 1));
    goban.capturePrisoners(white);
    assertNull(goban.getStone(1, 1));
    goban.removeStone(2, 1);
    assertNull(goban.getStone(2, 1));
    goban.addStone(new Stone("White", 2, 0, 10));
    goban.addStone(new Stone("White", 2, 2, 12));
    Stone blackStone2 = new Stone("Black", 1, 1, 13);
    goban.addStone(blackStone2);
    goban.capturePrisoners(black);
    assertEquals(blackStone2, goban.getStone(1, 1));
    Stone blackStone3 = new Stone("Black", 2, 1, 15);
    goban.addStone(blackStone3);
    assertEquals(blackStone3, goban.getStone(2, 1));
    goban.addStone(new Stone("White", 3, 1, 16));
    goban.capturePrisoners(white);
    assertNull(goban.getStone(1, 1));
    assertNull(goban.getStone(2, 1));
  }

  public Board initializeBoard(char[][] board) { // pass in cases where turn # doesn't matter
    Board goban = new Board(board.length, board[0].length);
    int turn = 1;
    for (int row = 0; row < board.length; row++) {
      for (int col = 0; col < board[0].length; col++) {
        if (board[row][col] == 'B') {
          goban.addStone(new Stone("Black", col, row, turn));
        } else if (board[row][col] == 'W') {
          goban.addStone(new Stone("White", col, row, turn));
        }
        turn += 1;
      }
    }
    return goban;
  }

  @Test
  public void calculateTerritoriesTest() {
    Board goban = new Board(4, 4);
    HashMap<String, Integer> scores = new HashMap<>();
    scores = goban.calculateTerritories(true);
    assertEquals(0, scores.get("Black"));
    assertEquals(0, scores.get("White"));
    goban.addStone(new Stone("Black", 1, 0, 1));
    goban.addStone(new Stone("Black", 1, 1, 3));
    goban.addStone(new Stone("Black", 0, 1, 5));
    goban.addStone(new Stone("White", 3, 2, 2));
    goban.addStone(new Stone("White", 2, 2, 4));
    goban.addStone(new Stone("White", 2, 3, 6));
    scores = goban.calculateTerritories(true);
    assertEquals(4, scores.get("Black"));
    assertEquals(4, scores.get("White"));
    scores = goban.calculateTerritories(false);
    assertEquals(1, scores.get("Black"));
    assertEquals(1, scores.get("White"));
    goban.addStone(new Stone("Black", 0, 0, 7));
    goban.addStone(new Stone("White", 3, 3, 8));
    scores = goban.calculateTerritories(true);
    assertEquals(4, scores.get("Black"));
    assertEquals(4, scores.get("White"));
    scores = goban.calculateTerritories(false);
    assertEquals(0, scores.get("Black"));
    assertEquals(0, scores.get("White"));
    goban.addStone(new Stone("Black", 2, 1, 9));
    goban.addStone(new Stone("White", 1, 2, 10));
    goban.addStone(new Stone("Black", 3, 1, 11));
    scores = goban.calculateTerritories(true);
    assertEquals(8, scores.get("Black"));
    assertEquals(5, scores.get("White")); // checks revisitng colored stones
    scores = goban.calculateTerritories(false);
    assertEquals(2, scores.get("Black"));
    assertEquals(0, scores.get("White"));

    goban = new Board(4, 4);
    goban.addStone(new Stone("Black", 2, 0, 1));
    goban.addStone(new Stone("Black", 2, 1, 3));
    goban.addStone(new Stone("Black", 1, 1, 5));
    goban.addStone(new Stone("Black", 0, 1, 7));
    goban.addStone(new Stone("White", 3, 3, 2));
    scores = goban.calculateTerritories(true);
    assertEquals(6, scores.get("Black"));
    assertEquals(1, scores.get("White"));
    scores = goban.calculateTerritories(false);
    assertEquals(2, scores.get("Black"));
    assertEquals(0, scores.get("White"));
    goban.addStone(new Stone("White", 1, 0, 4));
    scores = goban.calculateTerritories(true);
    assertEquals(4, scores.get("Black"));
    assertEquals(2, scores.get("White"));
    scores = goban.calculateTerritories(false);
    assertEquals(0, scores.get("Black"));
    assertEquals(0, scores.get("White"));

    char[][] new_board = {
        { '_', 'B', '_', '_', '_', '_', '_', '_', '_' },
        { '_', 'B', '_', '_', '_', '_', '_', '_', '_' },
        { 'B', '_', 'B', '_', '_', '_', '_', '_', '_' },
        { '_', 'B', '_', '_', '_', '_', '_', '_', '_' },
        { '_', '_', '_', '_', '_', '_', '_', '_', '_' },
        { '_', '_', '_', '_', '_', '_', '_', '_', '_' },
        { '_', '_', '_', '_', '_', '_', '_', '_', '_' },
        { 'W', 'W', '_', '_', '_', '_', '_', '_', '_' },
        { '_', 'W', '_', '_', '_', '_', '_', '_', '_' },
    };

    goban = initializeBoard(new_board);
    scores = goban.calculateTerritories(true);
    assertEquals(8, scores.get("Black"));
    assertEquals(4, scores.get("White"));

    scores = goban.calculateTerritories(false);
    assertEquals(3, scores.get("Black"));
    assertEquals(1, scores.get("White"));
  }

}
