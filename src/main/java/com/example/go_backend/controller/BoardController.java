package com.example.go_backend.controller;

import com.example.go_backend.service.BoardService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;

@RestController
@RequestMapping("/api/board")
public class BoardController {

  private final BoardService boardService;

  @Autowired
  public BoardController(BoardService boardService){
    this.boardService = boardService;
  }
  
  
  @GetMapping
  public int[][] getBoard() {
    return boardService.getBoardState();
  }

  @PostMapping("/move")
  public HashMap<String, Object> makeMove(@RequestBody String move){
    boolean isLegal = true;
    HashMap<String, Object> response = new HashMap<>();
    return response;
  }


  
}
