package com.example.go_backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Scanner;
import java.util.HashMap;

public class Game {

  public static void main(String args[]) {
    Player black = new Player('B');
    Player white = new Player('W');
    System.out.println("Welcome to 9x9 Go.");
    Scanner scanner = new Scanner(System.in);
    System.out.println("Enter 'A' to use area scoring, or any other key to use territory scoring");
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

    
    String jsonPayload = "";
    if (start.equals("Start")) {
      // Start Game
      Board goban = new Board(9, 9);
      
      try{
        GameState state = new GameState(goban.getBoard(), 1, "","", "", 0);
        ObjectMapper objectMapper = new ObjectMapper();
        jsonPayload = objectMapper.writeValueAsString(state);
        System.out.println("Json payload:");
        System.out.println(jsonPayload);
        String response = JavaClient.sendPostRequest(jsonPayload);
        System.out.println("Move: ");
        System.out.println(response);
        //System.out.println(jsonPayload1);
      } catch (Exception e){
        System.out.println("EXCEPTION");
      }
      
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

        if(blacksTurn){
          goban.capturePrisoners(black);
        } else {
          goban.capturePrisoners(white);
        }
        
        blacksTurn = !blacksTurn;
        turn++;
        goban.display();

        if (consecutivePasses >= 2 && !blacksTurn) { // white needs to pass last
          // 1. Calculate Territories & Remove Dead Stones
          HashMap<String, Integer> territoriesMap = goban.calculateTerritories(areaScoring);
          int blackScore = territoriesMap.get("Black") + black.getCapturedPrisoners();
          int whiteScore = territoriesMap.get("White") + white.getCapturedPrisoners();
          // 2. End Game
          System.out.printf("Black's prisoners: %d, White's prisoners: % d", black.getCapturedPrisoners(), white.getCapturedPrisoners());
          System.out.print("\n");
          System.out.printf("Black's score: %d, White's score: % d", blackScore, whiteScore);
          System.out.print("\n");
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
