package com.example.go_backend;

import java.util.Scanner;
import java.util.HashMap;

public class Game {

  public static void main(String args[]) {
    System.out.println("Welcome to 9x9 Go.");
    Scanner scanner = new Scanner(System.in);
    System.out.println("Enter 'A' to use area scoring, or any ofther key to use territory scoring");
    String scoringSelection = scanner.nextLine();
    boolean areaScoring;
    if (scoringSelection.equals("A")){
      System.out.println("Area Scoring Selected");
      areaScoring = true;
    } else {
      System.out.println("Territory Scoring Selected");
      areaScoring = false;
    }
    System.out.println("Enter 'Start' to Start Game");
    String start = scanner.nextLine();
    if (start.equals("Start")) {
      // Start Game
      Board goban = new Board(9, 9);
      Boolean continuePlaying = true;
      Boolean blacksTurn = true;
      int turn = 1;
      int consecutivePasses = 0;
      // Continue Playing
      while (continuePlaying) {
        Boolean added = false;
        Boolean pass = false;
        while (!(added || pass)) {
          System.out.println("Enter X-coordinate, or 'Pass'");
          String input = scanner.nextLine();
          if (input.equals("Pass")) {
            pass = true;
            consecutivePasses++;
          } else {
            consecutivePasses = 0;
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

        if (consecutivePasses >= 2 && !blacksTurn) { // white passed last
          // 1. Calculate Territories & Remove Dead Stones
          HashMap<String, Integer> territoriesMap = goban.calculateTerritories(false);
          int blackScore = territoriesMap.get("Black");
          int whiteScore = territoriesMap.get("White");
          // 2. Form Scores
          if (!areaScoring) {
            // need to subtract prisoners
          }
          // 3. End Game
          System.out.printf("Black's score: %d, White's score: %d", blackScore, whiteScore);
          System.out.println(blackScore > whiteScore ? "Black wins!" : "White wins!");
          break;
        }

        System.out.println("Click space to continue.");
        continuePlaying = scanner.nextLine().equals(" ") ? true : false;
      }
    }
    scanner.close();
  }

}
