package com.example.go_backend;

public class Player {
  private final char color;
  private int capturedPrisoners;

  public Player(char color){
    if (color != 'B' && color != 'W'){
      throw new IllegalArgumentException("Player color must be black or white");
    }

    this.color = color;
    this.capturedPrisoners = 0;
  }

  public char getColor(){
    return this.color;
  }

  public int getCapturedPrisoners(){
    return this.capturedPrisoners;
  }

  public void setCapturedPrisoners(int prisoners){
    this.capturedPrisoners = prisoners;
  }
  
}
