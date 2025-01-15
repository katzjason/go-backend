package com.example.go_backend;

public class Stone {
  private String color;
  private int x;
  private int y;
  private int liberties;
  private Boolean isPrisoner;
  private int turn;

  // Parameterized constructor
  public Stone(String color, int x, int y, int turn) {
    this.color = color;
    this.x = x;
    this.y = y;
    this.isPrisoner = false;
    this.liberties = 4;
    this.turn = turn;
  }

  public String toString() {
    return ("Color: " + this.color +
        "; X: " + String.valueOf(this.x) +
        "; Y: " + String.valueOf(this.y) +
        "; Liberties: " + String.valueOf(this.liberties)) + 
        "; isPrisoner: " + String.valueOf(this.isPrisoner) + 
        "; Turn placed: " + String.valueOf(this.turn);
  }

  public char getColor() {
    return this.color.charAt(0);
  }

  public int getX() {
    return this.x;
  }

  public int getY() {
    return this.y;
  }

  public int getLiberties() {
    return this.liberties;
  }

  public void setLiberties(int liberties) {
    this.liberties = liberties;
  }

  public int getTurn() {
    return this.turn;
  }
}
