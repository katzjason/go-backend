package com.example.go_backend.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/board")
public class BoardController {
  
  @GetMapping
  public int[][] getBoard() {
    int[][] board = new int[3][3];
    board[1][1] = -1;
    board[2][1] = 1;
    board[1][0] = 0;
    board[0][1] = 1;
    board[1][2] = 0;
    return board;
    

  }
  
}
