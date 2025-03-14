package com.example.go_backend.service;

import org.springframework.stereotype.Service;

import com.example.go_backend.Board;
import com.example.go_backend.Stone;

@Service
public class BoardService {
  private Board board;

  public BoardService(){
    initializeBoard();
  }

  private void initializeBoard(){
    this.board = new Board(9,9);
  }

  public int[][] getBoardState(){
    this.board.addStone(new Stone("B", 5,5, 1));
    this.board.addStone(new Stone("W", 6,7, 2));
    return this.board.getBoard();
  }



  
}
