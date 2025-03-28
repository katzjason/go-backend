package com.example.go_backend.controller;

import com.example.go_backend.Board;
import com.example.go_backend.JavaClient;
import com.example.go_backend.Tuple;
import com.example.go_backend.Player;
import com.example.go_backend.Stone;
import com.example.go_backend.service.BoardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

@RestController
@CrossOrigin(origins = "https://go-frontend-mauve.vercel.app", allowedHeaders = "*")
// @RequestMapping("/api/board")
public class BoardController {

  // private final BoardService boardService;

  // @Autowired
  // public BoardController(BoardService boardService) {
  // this.boardService = boardService;
  // }

  // @GetMapping
  // public int[][] getBoard() {
  // return boardService.getBoardState();
  // }
  // @Value("${FLASK_URL}")
  // private String flask_url;
  private String flask_url = "http://127.0.0.1:5000";

  @PostMapping("/api/board/move")
  public HashMap<String, Object> makeMove(@RequestBody String request) {
    // boolean processingError = false;
    int[][] board = new int[9][9];
    int[][] turns = new int[9][9];
    Integer move_player = 1;
    Integer move_row = 0;
    Integer move_col = 0;
    Integer passed = 0;
    Integer this_turn = 0;
    Player black = new Player('B');
    Player white = new Player('W');
    String last_black_move = "";
    String last_white_move = "";
    Integer blacks_prisoners = 0;
    Integer whites_prisoners = 0;
    Integer ko_x = -1;
    Integer ko_y = -1;
    Integer ko_player_restriction = 0;
    Boolean area_scoring = false;

    try {
      ObjectMapper requestMapper = new ObjectMapper();
      Map<String, Object> jsonMap = requestMapper.readValue(request, new TypeReference<Map<String, Object>>() {
      });
      Object moveObject = jsonMap.get("move");
      Map<String, Integer> moveMap = requestMapper.convertValue(moveObject, new TypeReference<Map<String, Integer>>() {
      });
      move_row = moveMap.get("row");
      move_col = moveMap.get("col");
      passed = moveMap.get("passed");
      move_player = moveMap.get("player_turn");
      board = requestMapper.convertValue(jsonMap.get("board"), new TypeReference<int[][]>() {
      });
      turns = requestMapper.convertValue(jsonMap.get("turns"), new TypeReference<int[][]>() {
      });
      this_turn = Integer.parseInt(jsonMap.get("this_turn").toString());
      blacks_prisoners = Integer.parseInt(jsonMap.get("blacksPrisoners").toString());
      whites_prisoners = Integer.parseInt(jsonMap.get("whitesPrisoners").toString());
      black.setCapturedPrisoners(blacks_prisoners);
      white.setCapturedPrisoners(whites_prisoners);
      ko_x = Integer.parseInt(jsonMap.get("ko_x").toString());
      ko_y = Integer.parseInt(jsonMap.get("ko_y").toString());
      ko_player_restriction = Integer.parseInt(jsonMap.get("ko_player_restriction").toString());
      last_black_move = jsonMap.get("last_black_move").toString();
      last_white_move = jsonMap.get("last_white_move").toString();
      area_scoring = Boolean.parseBoolean(jsonMap.get("area_scoring").toString());

    } catch (JsonProcessingException e) {
      System.err.println(e.getMessage());
      // processingError = true;
    }

    Board goban = new Board(9, 9);
    goban.setBoard(board, turns); // board is updated to reflect live state
    goban.setTurns(turns);
    Tuple ko = new Tuple(ko_x, ko_y);
    char ko_color = ko_player_restriction == 1 ? 'B' : 'W';
    goban.setKo(ko, ko_color);
    HashMap<String, Object> response = new HashMap<>();
    // GAME OVER CONDITION
    boolean game_over = last_black_move.equals("pass") && move_player == -1 && passed == 1;
    if (!game_over) {
      // Move was not pass
      if (passed != 1) {
        String stoneColor = move_player.equals(1) ? "Black" : "White";
        boolean added = goban.addStone(new Stone(stoneColor, move_col, move_row, this_turn));
        if (added == true) {
          turns = goban.getTurns(); // reflects pieces moved
          turns[move_row][move_col] = this_turn; // adds this move
          if (move_player == 1) {
            goban.capturePrisoners(black);
          } else {
            goban.capturePrisoners(white);
          }
          move_player *= -1;
          this_turn += 1;
          ko = goban.getKo();
          ko_player_restriction = goban.getKoColor() == 'B' ? 1 : (goban.getKoColor() == 'W' ? -1 : 0);
        }

      } else {
        move_player *= -1;
        this_turn += 1;
      }
    }

    int new_blacks_score = 0;
    int new_whites_score = 0;
    if (game_over) {
      HashMap<String, Integer> territoriesMap = goban.calculateTerritories(area_scoring);
      new_blacks_score = territoriesMap.get("Black") + black.getCapturedPrisoners();
      new_whites_score = territoriesMap.get("White") + white.getCapturedPrisoners();
    }

    response.put("game_over", game_over);
    response.put("board", goban.getBoard());
    response.put("move_player", move_player);
    response.put("ko", ko);
    response.put("ko_player_restriction", ko_player_restriction);
    response.put("turns", turns);
    response.put("this_turn", this_turn);
    response.put("blacks_prisoners", black.getCapturedPrisoners());
    response.put("whites_prisoners", white.getCapturedPrisoners());
    response.put("blacks_score", new_blacks_score);
    response.put("whites_score", new_whites_score);
    return response;
  }

  @PostMapping("/api/board/ai_move")
  public HashMap<String, Object> getAIMove(@RequestBody String request) {
    int[][] board = new int[9][9];
    int[][] turns = new int[9][9];
    Integer move_player = 1;
    Integer move_row = 0;
    Integer move_col = 0;
    Integer passed = 0;
    Integer this_turn = 0;
    Player black = new Player('B');
    Player white = new Player('W');
    String last_black_move = "";
    String last_white_move = "";
    Integer blacks_prisoners = 0;
    Integer whites_prisoners = 0;
    Integer ko_x = -1;
    Integer ko_y = -1;
    Integer ko_player_restriction = 0;
    Boolean area_scoring = false;
    Boolean game_over = false;

    try {
      ObjectMapper requestMapper = new ObjectMapper();
      Map<String, Object> jsonMap = requestMapper.readValue(request, new TypeReference<Map<String, Object>>() {
      });
      Object moveObject = jsonMap.get("move");
      Map<String, Integer> moveMap = requestMapper.convertValue(moveObject, new TypeReference<Map<String, Integer>>() {
      });
      // move_row = moveMap.get("row");
      // move_col = moveMap.get("col");
      // passed = moveMap.get("passed");
      move_player = moveMap.get("player_turn");
      board = requestMapper.convertValue(jsonMap.get("board"), new TypeReference<int[][]>() {
      });
      turns = requestMapper.convertValue(jsonMap.get("turns"), new TypeReference<int[][]>() {
      });
      this_turn = Integer.parseInt(jsonMap.get("this_turn").toString());
      blacks_prisoners = Integer.parseInt(jsonMap.get("blacksPrisoners").toString());
      whites_prisoners = Integer.parseInt(jsonMap.get("whitesPrisoners").toString());
      black.setCapturedPrisoners(blacks_prisoners);
      white.setCapturedPrisoners(whites_prisoners);
      ko_x = Integer.parseInt(jsonMap.get("ko_x").toString());
      ko_y = Integer.parseInt(jsonMap.get("ko_y").toString());
      ko_player_restriction = Integer.parseInt(jsonMap.get("ko_player_restriction").toString());
      last_black_move = jsonMap.get("last_black_move").toString();
      last_white_move = jsonMap.get("last_white_move").toString();
      area_scoring = Boolean.parseBoolean(jsonMap.get("area_scoring").toString());
      // game_over = last_black_move.equals("pass") && last_white_move.equals("pass")
      // && move_player == -1
      // && passed == 1;

    } catch (JsonProcessingException e) {
      System.err.println(e.getMessage());
    }

    Board goban = new Board(9, 9);
    goban.setBoard(board, turns); // board is updated to reflect live state
    goban.setTurns(turns);
    Tuple ko = new Tuple(ko_x, ko_y);
    char ko_color = ko_player_restriction == 1 ? 'B' : 'W';
    goban.setKo(ko, ko_color);
    HashMap<String, Object> payload = new HashMap<>();
    String aiMove = "temp";

    // make AI request
    try {
      // payload.put("game_over", game_over);
      payload.put("board", goban.getBoard());
      payload.put("move_player", move_player);
      payload.put("ko_x", ko_x);
      payload.put("ko_y", ko_y);
      payload.put("ko_player_restriction", ko_player_restriction);
      payload.put("turns", turns);
      payload.put("this_turn", this_turn);
      payload.put("last_black_move", last_black_move);
      payload.put("last_white_move", last_white_move);
      // payload.put("blacks_prisoners", black.getCapturedPrisoners());
      // payload.put("whites_prisoners", white.getCapturedPrisoners());
      // payload.put("blacks_score", new_blacks_score);
      // payload.put("whites_score", new_whites_score);
      ObjectMapper mapr = new ObjectMapper();
      String jsonPayload = "";

      try {
        jsonPayload = mapr.writeValueAsString(payload);
      } catch (Exception e) {
        e.printStackTrace();
      }
      String pythonApiUrl = "http://127.0.0.1:5000";
      aiMove = JavaClient.sendPostRequest(jsonPayload, pythonApiUrl);
      System.out.println(aiMove);
    } catch (Exception e) {
      System.out.println("ERROR");
    }

    Integer ai_x = 10;
    Integer ai_y = 10;
    if (aiMove == "pass") {
      passed = 1;
    } else {
      ai_x = Integer.parseInt(aiMove.split(",")[0].substring(1));
      ai_y = Integer.parseInt(aiMove.split(",")[1].substring(0, 1));
    }

    // GAME OVER CONDITION
    game_over = last_black_move.equals("pass") && move_player == -1 && passed == 1;
    if (!game_over) {
      // Move was not pass
      if (passed != 1) {
        String stoneColor = move_player.equals(1) ? "Black" : "White";
        boolean added = goban.addStone(new Stone(stoneColor, ai_x, ai_y, this_turn));
        if (added == true) {
          turns = goban.getTurns(); // reflects pieces moved
          turns[ai_x][ai_y] = this_turn; // adds this move
          if (move_player == 1) {
            goban.capturePrisoners(black);
          } else {
            goban.capturePrisoners(white);
          }
          move_player *= -1;
          this_turn += 1;
          ko = goban.getKo();
          ko_player_restriction = goban.getKoColor() == 'B' ? 1 : (goban.getKoColor() == 'W' ? -1 : 0);
        }

      } else {
        move_player *= -1;
        this_turn += 1;
      }
    }

    int new_blacks_score = 0;
    int new_whites_score = 0;
    if (game_over) {
      HashMap<String, Integer> territoriesMap = goban.calculateTerritories(area_scoring);
      new_blacks_score = territoriesMap.get("Black") + black.getCapturedPrisoners();
      new_whites_score = territoriesMap.get("White") + white.getCapturedPrisoners();
    }
    HashMap<String, Object> response = new HashMap<>();
    response.put("game_over", game_over);
    response.put("board", goban.getBoard());
    response.put("move_player", move_player);
    response.put("ko", ko);
    response.put("ko_player_restriction", ko_player_restriction);
    response.put("turns", turns);
    response.put("this_turn", this_turn);
    response.put("blacks_prisoners", black.getCapturedPrisoners());
    response.put("whites_prisoners", white.getCapturedPrisoners());
    response.put("blacks_score", new_blacks_score);
    response.put("whites_score", new_whites_score);
    response.put("last_black_move", last_black_move);
    response.put("last_white_move", last_white_move);
    return response;
  }

}
