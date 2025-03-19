package com.example.go_backend.controller;

import com.example.go_backend.Board;
import com.example.go_backend.Player;
import com.example.go_backend.Stone;
import com.example.go_backend.service.BoardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

@RestController
@RequestMapping("/api/board")
public class BoardController {

  private final BoardService boardService;

  @Autowired
  public BoardController(BoardService boardService) {
    this.boardService = boardService;
  }

  @GetMapping
  public int[][] getBoard() {
    return boardService.getBoardState();
  }

  @PostMapping("/move")
  public HashMap<String, Object> makeMove(@RequestBody String request) {
    boolean processingError = false;
    int[][] board = new int[9][9];
    int[][] turns = new int[9][9];
    Integer move_player = 1;
    Integer move_row = 0;
    Integer move_col = 0;
    Integer this_turn = 0;
    Player black = new Player('B');
    Player white = new Player('W');

    try {
      ObjectMapper requestMapper = new ObjectMapper();
      Map<String, Object> jsonMap = requestMapper.readValue(request, new TypeReference<Map<String, Object>>() {
      });
      Object moveObject = jsonMap.get("move");
      Map<String, Integer> moveMap = requestMapper.convertValue(moveObject,
          new TypeReference<Map<String, Integer>>() {
          });
      move_row = moveMap.get("row");
      move_col = moveMap.get("col");
      move_player = moveMap.get("player_turn");

      board = requestMapper.convertValue(jsonMap.get("board"), new TypeReference<int[][]>() {
      });
      turns = requestMapper.convertValue(jsonMap.get("turns"), new TypeReference<int[][]>() {
      });
      this_turn = Integer.parseInt(jsonMap.get("this_turn").toString());
      int black_prisoners = Integer.parseInt(jsonMap.get("blackPrisoners").toString());
      int white_prisoners = Integer.parseInt(jsonMap.get("whitePrisoners").toString());
      int ko_x = Integer.parseInt(jsonMap.get("ko_x").toString());
      int ko_y = Integer.parseInt(jsonMap.get("ko_y").toString());
      int ko_player_restriction = Integer.parseInt(jsonMap.get("ko_player_restriction").toString());
      String last_black_move = jsonMap.get("last_black_move").toString();
      String last_white_move = jsonMap.get("last_white_move").toString();
    } catch (JsonProcessingException e) {
      System.err.println(e.getMessage());
      processingError = true;
    }

    Board goban = new Board(9, 9);
    goban.setBoard(board, turns); // board is updated to reflect live state

    // Move was not pass
    String stoneColor = move_player.equals(1) ? "Black" : "White";
    boolean added = goban.addStone(new Stone(stoneColor, move_col, move_row, this_turn));
    if (added == true) {
      turns[move_row][move_col] = this_turn;
      if (move_player == 1) {
        goban.capturePrisoners(black);
      } else {
        goban.capturePrisoners(white);
      }
      move_player *= -1;
      // handle other logic
    }

    HashMap<String, Object> response = new HashMap<>();
    response.put("board", goban.getBoard());
    response.put("move_player", move_player);
    return response;
  }

}
