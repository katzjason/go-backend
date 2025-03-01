package com.example.go_backend.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/board")
public class BoardController {
  
  @GetMapping
  public int[][] getBoard() {
    int[][] board = new int[2][2];
    board[0][0] = 0;
    board[0][1] = 1;
    board[1][0] = 0;
    board[1][1] = 1;
    return board;
    

  }
  
}
