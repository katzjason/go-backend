package com.example.go_backend;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

public class GameStateTest {

  @Test
  public void GameStateConstructorTest() {
    int[][] board = new int[9][9];
    GameState state = new GameState(board, 1, "1,1", "2,2", "2,2", 1);
    assertEquals(board, state.getBoard());
    assertEquals(1, state.getPlayer());
    assertEquals("1,1", state.getLastWhiteMove());
    assertEquals("2,2", state.getLastBlackMove());
    assertEquals("2,2", state.getKo());
    assertEquals(1, state.getKoPlayer());

  }
}
