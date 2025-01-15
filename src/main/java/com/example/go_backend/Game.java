package com.example.go_backend;

import java.util.Scanner;

public class Game {

  public static void main(String args[]){
    System.out.println("Welcome to 9x9 Go.");
    Scanner scanner = new Scanner(System.in);
    System.out.println("Enter 'Start' to Start Game");
    String start = scanner.nextLine();
    if (start.equals("Start")) {
      // Start Game
      Board goban = new Board(9, 9);
      Boolean continuePlaying = true;
      Boolean blacksTurn = true;
      int turn = 1;
      // Continue Playing
      while (continuePlaying) {
        Boolean added = false;
        Boolean pass = false;
        while (!(added || pass)) {
          System.out.println("Enter X-coordinate, or 'Pass'");
          String input = scanner.nextLine();
          if(input.equals("Pass")){
            pass = true;
          } else {
            Integer x = Integer.valueOf(input);
            System.out.println("Enter Y: ");
            Integer y = Integer.valueOf(scanner.nextLine());
            added = goban.addStone(new Stone((blacksTurn ? "Black" : "White"), x, y, turn)); // true if stone was added
          }
          
        }
        blacksTurn = !blacksTurn;
        turn++;
        goban.capturePrisoners();
        goban.display();

        System.out.println("Click space to continue");
        continuePlaying = scanner.nextLine().equals(" ") ? true : false;
      }
    }
    scanner.close();
  }
  
}
