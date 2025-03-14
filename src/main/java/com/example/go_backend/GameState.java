package com.example.go_backend;

public class GameState {
  private int[][] board;
  private int player;
  private String lastWhiteMove;
  private String lastBlackMove;
  private String ko;
  private int koPlayer;

  public GameState(int[][] board, int player, String lastWhiteMove, String lastBlackMove, String ko, int koPlayer){
    this.board = board;
    this.player = player;
    this.lastWhiteMove = lastWhiteMove;
    this.lastBlackMove = lastBlackMove;
    this.ko = ko;
    this.koPlayer = koPlayer;
  }

  public int[][] getBoard() {
    return this.board;
}

  public int getPlayer() {
      return this.player;
  }

  public String getLastWhiteMove() {
      return this.lastWhiteMove;
  }

  public String getLastBlackMove() {
      return this.lastBlackMove;
  }

  public String getKo() {
    return this.ko;
}

public int getKoPlayer() {
    return this.koPlayer;
}
}
