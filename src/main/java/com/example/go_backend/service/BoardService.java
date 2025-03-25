package com.example.go_backend.service;

import org.springframework.stereotype.Service;

import com.example.go_backend.Board;

@Service
public class BoardService {
  private Board board;

  public BoardService() {
    initializeBoard();
  }

  private void initializeBoard() {
    this.board = new Board(9, 9);
  }

  public int[][] getBoardState() {
    return this.board.getBoard();
  }

}
