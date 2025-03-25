package com.example.go_backend;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Arrays;

public class Board {
  private final int rows;
  private final int cols;
  private Stone[][] board;
  private Tuple ko;
  private char koColor;
  private int[][] turns;

  public Board(int rows, int cols) {
    this.rows = rows;
    this.cols = cols;
    this.board = new Stone[rows][cols];
    this.ko = new Tuple(rows + 1, cols + 1);
    this.koColor = '\u0000';
    this.turns = new int[rows][cols];

    for (int i = 0; i < this.rows; i++) {
      for (int j = 0; j < this.cols; j++) {
        this.board[i][j] = null;
      }
    }
  }

  public int[][] getBoard() {
    int[][] intBoard = new int[this.rows][this.cols];
    for (int row = 0; row < this.rows; row++) {
      for (int col = 0; col < this.cols; col++) {
        intBoard[row][col] = this.board[row][col] == null ? 0 : (this.board[row][col].getColor() == 'B' ? 1 : -1);
      }
    }
    return intBoard;
  }

  public void setBoard(int[][] newBoard, int[][] turns) {
    if (newBoard.length != this.rows || newBoard[0].length != this.cols) {
      throw new IllegalArgumentException(
          "Mismatched board sizes; Cannot set board.");
    }
    for (int row = 0; row < this.rows; row++) {
      for (int col = 0; col < this.cols; col++) {
        if (newBoard[row][col] != 0) {
          String stoneColor = newBoard[row][col] == 1 ? "B" : "W";
          this.board[row][col] = new Stone(stoneColor, col, row, turns[row][col]);
        }
      }
    }
  }

  public int[][] getTurns() {
    return this.turns;
  }

  public void setTurns(int[][] new_turns) {
    if (new_turns.length != this.rows || new_turns[0].length != this.cols) {
      throw new IllegalArgumentException(
          "Mismatched board sizes; Cannot set turns.");
    }
    for (int row = 0; row < this.rows; row++) {
      for (int col = 0; col < this.cols; col++) {
        this.turns[row][col] = new_turns[row][col];
      }
    }
  }

  public Stone getStone(int x, int y) {
    return this.board[y][x];
  }

  public Tuple getKo() {
    return this.ko;
  }

  public char getKoColor() {
    return this.koColor;
  }

  public void setKo(Tuple ko, char color) {
    this.ko = ko;
    this.koColor = color;
  }

  public boolean addStone(Stone stone) {
    int x = stone.getX(), y = stone.getY();

    // Physically cannot place stone
    if (this.board[y][x] != null) {
      System.out
          .println("There is already a stone at (" + String.valueOf(x) + "," + String.valueOf(y) + "). Try again.");
      return false;
    } else if (x == this.ko.first && y == this.ko.second && stone.getColor() == this.koColor) { // ko rule
      System.out.println("Cannot place stone here: Ko rule. Try again.");
      return false;
    }

    // Temporarily adding stone
    this.board[y][x] = stone;
    int liberties = calculateLiberties(x, y, stone.getColor(), new HashSet<Tuple>());

    // Legal move to place stone?
    if (liberties != 0) {
      setKo(new Tuple(this.cols + 1, this.rows + 1), '\u0000');
      return true;
    }

    // Liberties are zero but capture is made
    List<Tuple> surrounding = new ArrayList<>();
    surrounding
        .addAll(Arrays.asList(new Tuple(x + 1, y), new Tuple(x - 1, y), new Tuple(x, y + 1), new Tuple(x, y - 1)));
    HashSet<Tuple> visited = new HashSet<>();
    char neighborColor = stone.getColor() == 'B' ? 'W' : 'B';
    while (surrounding.size() > 0) {
      Tuple coord = surrounding.remove(surrounding.size() - 1);
      int x1 = coord.first;
      int y1 = coord.second;
      if (x1 >= this.cols || x1 < 0 || y1 >= this.rows || y1 < 0) {
        continue;
      } else if (visited.contains(coord)) {
        continue;
      } else {
        visited.add(coord);
      }

      if (this.board[y1][x1] == null) {
        continue;
      } else if (this.board[y1][x1].getColor() == stone.getColor()) {
        surrounding
            .addAll(Arrays.asList(new Tuple(x1 + 1, y1), new Tuple(x1 - 1, y1), new Tuple(x1, y1 + 1),
                new Tuple(x1, y1 - 1)));
        continue;
      } else if (calculateLiberties(x1, y1, neighborColor, new HashSet<Tuple>()) == 0) {
        return true;
      }
    }

    // liberties are zero and no captures are made.
    removeStone(x, y);
    System.out.println("Cannot place stone here: immediate capture. Try again.");
    return false;
  }

  public void removeStone(int x, int y) {
    if (x < 0 || y < 0 || x >= this.cols || y >= this.rows) {
      throw new IllegalArgumentException(
          "X and Y must be non-negative and within the size of the board.");
    } else if (this.getStone(x, y) == null) {
      System.out.printf("No stone to remove at: (%d,%d)%n", x, y);
    } else {
      this.board[y][x] = null;
    }
  }

  public int calculateLiberties(int x, int y, char color, Set<Tuple> visited) {
    // returns the liberties for a stone at (x, y)
    if (x < 0 || y < 0 || x >= this.cols || y >= this.rows) {
      throw new IllegalArgumentException(
          "X and Y must be non-negative and within the size of the board.");
      // } else if (this.board[y][x] == null) {
      // throw new IllegalArgumentException(
      // "Null at X and Y; Cannot calculate liberties");
    } else if (this.board[y][x] != null && this.board[y][x].getColor() != color) {
      throw new IllegalArgumentException(
          "Stone at X and Y doesn't match provided color for calculateLiberties.");
    }

    Tuple coordinates = new Tuple(x, y);
    if (!visited.contains(coordinates)) {
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

  public HashMap<String, Integer> calculateTerritories(boolean areaCounting) {
    // areaCounting flags whether to use territory counting or area counting
    HashMap<String, Integer> territoriesMap = new HashMap<>(); // { "Black": ..., "White": ... }
    int blackTerritory = 0;
    int whiteTerritory = 0;
    int blackStones = 0;
    int whiteStones = 0;

    Set<Tuple> coords = new HashSet<>(); // board coordinates we must visit
    for (int row = 0; row < this.rows; row++) {
      for (int col = 0; col < this.cols; col++) {
        coords.add(new Tuple(col, row));
      }
    }

    Set<Tuple> visited = new HashSet<>();
    while (coords.size() > 0) {
      Tuple thisCoord = null;
      for (Tuple coord : coords) { // pop coordinate from set
        thisCoord = coord;
        break;
      }

      visited.add(thisCoord); // moving coord from new to visited
      coords.remove(thisCoord);
      int x = thisCoord.first;
      int y = thisCoord.second;

      if (getStone(x, y) == null) { // empty space
        char borderColor = '\u0000';
        boolean neutral = false;
        int territorySize = 1;
        Queue<Tuple> toVisit = new LinkedList<>();
        toVisit.add(new Tuple(x + 1, y));
        toVisit.add(new Tuple(x - 1, y));
        toVisit.add(new Tuple(x, y + 1));
        toVisit.add(new Tuple(x, y - 1));

        while (toVisit.size() > 0) {
          thisCoord = toVisit.poll();
          x = thisCoord.first;
          y = thisCoord.second;
          if (x < 0 || x > this.cols - 1 || y < 0 || y > this.rows - 1) {
            continue;
          }

          Stone thisStone = getStone(x, y);

          if (visited.contains(thisCoord)) {
            if (thisStone != null) {
              if (borderColor == '\u0000') {
                borderColor = thisStone.getColor();
              } else if (thisStone.getColor() != borderColor) {
                neutral = true;
              }
            }
            continue;
          }

          visited.add(thisCoord);
          coords.remove(thisCoord);

          if (thisStone == null) {
            territorySize += 1;
            toVisit.add(new Tuple(x + 1, y));
            toVisit.add(new Tuple(x - 1, y));
            toVisit.add(new Tuple(x, y + 1));
            toVisit.add(new Tuple(x, y - 1));
          } else {
            char thisColor = thisStone.getColor();
            if (borderColor == '\u0000') {
              borderColor = thisColor;
            } else if (thisStone.getColor() != borderColor) {
              neutral = true;
            }

            if (thisColor == 'B') {
              blackStones += 1;
            } else {
              whiteStones += 1;
            }
          }
        }

        if (!neutral) {
          if (borderColor == 'W') {
            whiteTerritory += territorySize;
          } else if (borderColor == 'B') {
            blackTerritory += territorySize;
          }
        }
      } else if (getStone(x, y).getColor() == 'B') {
        blackStones += 1;
      } else if (getStone(x, y).getColor() == 'W') {
        whiteStones += 1;
      }
    }

    if (areaCounting) {
      territoriesMap.put("Black", blackTerritory + blackStones);
      territoriesMap.put("White", whiteTerritory + whiteStones);
    } else {
      territoriesMap.put("Black", blackTerritory);
      territoriesMap.put("White", whiteTerritory);
    }
    return territoriesMap;
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

  // returns a set of tuples containing the chain forming this group
  public Set<Tuple> floodFill(int x, int y, char color, Set<Tuple> visited) {
    Tuple coordinates = new Tuple(x, y);
    Set<Tuple> group = new HashSet<>();
    if (!visited.contains(coordinates)) {
      visited.add(coordinates);
      if (getStone(x, y).getColor() == color) {
        group.add(coordinates);
      } else {
        return group;
      }
    }

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
        } else if (this.board[neighbor.second][neighbor.first].getColor() != color) {
          visited.add(neighbor);
        } else {
          group.addAll(floodFill(neighbor.first, neighbor.second, color, visited));
        }
      } catch (Exception e) {
        continue;
      }
    }
    return group;
  }

  // removes captured prisoners from board
  public void capturePrisoners(Player player) {
    // removing ko piece
    if (getKo().first < this.cols &&
        getKo().first >= 0 &&
        getKo().second < this.rows &&
        getKo().second >= 0) {
      removeStone(getKo().first, getKo().second);
      player.setCapturedPrisoners(player.getCapturedPrisoners() + 1);
      this.turns[getKo().second][getKo().first] = 0;
    }

    Set<Tuple> visited = new HashSet<>();
    for (int i = 0; i < this.rows; i++) {
      for (int j = 0; j < this.cols; j++) {
        Tuple coordinate = new Tuple(i, j);
        if (!visited.contains(coordinate)) {
          visited.add(coordinate);
          Stone thisStone = this.getStone(i, j);
          if (thisStone != null) {
            Set<Tuple> group = floodFill(i, j, thisStone.getColor(), new HashSet<Tuple>()); // get group
            if (this.calculateLiberties(i, j, this.getStone(i, j).getColor(), new HashSet<Tuple>()) == 0) { // get
                                                                                                            // liberties
              for (Tuple stone : group) { // remove stones
                this.turns[stone.second][stone.first] = 0;
                removeStone(stone.first, stone.second);
                player.setCapturedPrisoners(player.getCapturedPrisoners() + 1);
              }
            }
          }
        }
      }
    }
  }
}
