package com.example.go_backend;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Board {
  private final int rows;
  private final int cols;
  private Stone[][] board;

  public Board(int rows, int cols) {
    this.rows = rows;
    this.cols = cols;
    this.board = new Stone[rows][cols];

    for (int i = 0; i < this.rows; i++) {
      for (int j = 0; j < this.cols; j++) {
        this.board[i][j] = null;
      }
    }
  }

  public boolean addStone(Stone stone) {
    int x = stone.getX();
    int y = stone.getY();

    // physically can't place
    if (this.board[y][x] != null) {
      System.out
          .println("There is already a stone at (" + String.valueOf(x) + "," + String.valueOf(y) + "). Try again.");
      return false;
    }
    // immediate capture
    int liberties = calculateLiberties(x, y, stone.getColor(), new HashSet<Tuple>());
    if (liberties == 0) {
      System.out.println("Cannot place stone here: immediate capture. Try again.");
      return false;
    }

    // Placing stone
    this.board[y][x] = stone;
    return true;
  }

  public Stone getStone(int x, int y){
    return this.board[y][x];
  }


  public int calculateLiberties(int x, int y, char color, Set<Tuple> visited) {
    // returns the liberties for a stone at (x, y)
    if (x < 0 || y < 0 || x >= this.cols || y >= this.rows) {
      throw new IllegalArgumentException(
          "X and Y must be non-negative and within the size of the board.");
    }

    Tuple coordinates = new Tuple(x, y);
    if (visited.contains(coordinates)) {
      return 10;
    } else {
      visited.add(coordinates);
    }

    int liberties = 0;
    List<Tuple> surrounding = new ArrayList<>();
    surrounding.add(new Tuple(x + 1, y));
    surrounding.add(new Tuple(x - 1, y));
    surrounding.add(new Tuple(x, y + 1));
    surrounding.add(new Tuple(x, y - 1));

    Iterator<Tuple> iterator = surrounding.iterator();
    while (iterator.hasNext()) {
      Tuple neighbor = iterator.next();
      try { // try-catch is used for coordinates that fall outside the size of the board
        if (visited.contains(neighbor))
          continue;
        else if (this.board[neighbor.second][neighbor.first] == null) {
          visited.add(neighbor);
          liberties += 1;
        } else if (this.board[neighbor.second][neighbor.first].getColor() != color) {
          visited.add(neighbor);
        } else {
          liberties += calculateLiberties(neighbor.first, neighbor.second, color, visited);
        }
      } catch (Exception e) {
        continue;
      }
    }
    return liberties;
  }

  
  public void display() {
    System.out.println("BOARD");
    System.out.println("  0 1 2 3 4 5 6 7 8");
    for (int i = 0; i < this.rows; i++) {
      String str = i + " ";
      for (int j = 0; j < this.cols; j++) {
        str += this.board[i][j] == null ? "  " : this.board[i][j].getColor() + " ";
      }
      System.out.println(str);
    }
  }

  // private Set<Tuple> floodFill(int x, int y){
  // }

  // public HashMap<String, Integer> calculateTerritories() {
  // HashMap<String, Integer> territories = new HashMap<>();
  // Set<Tuple> visited = new HashSet<>();

  // for (int i = 0; i < this.rows; i++) {
  // for (int j = 0; j < this.cols; j++) {
  // Tuple coordinates = new Tuple(i, j);
  // if (!visited.contains(coordinates)) {
  // // calculate
  // }
  // }
  // }
  // return territories;
  // }
}
