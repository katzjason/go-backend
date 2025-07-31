class Board:
  def __init__(self, rows, cols):
    self._last_black_move = None
    self._last_white_move = None
    self._blacks_prisoners = 0
    self._whites_prisoners = 0
    self._rows = rows
    self._cols = cols
    self.player = 1
    self._ko = None
    self._ko_player = None
    #self._board = np.zeros((rows, cols))
    self._board = [[ 0, 0, 0, 0, 0, 1,-1,-1,-1],
                   [ 0, 0, 0, 0, 0, 1,-1,-1,-1],
                   [ 0, 0, 0, 0, 0, 1,-1,-1,-1],
                   [ 0, 0, 0, 0, 0, 1, 1, 0, 1],
                   [ 0, 0, 0, 0, 0, 0, 0, 0, 0],
                   [ 0, 0, 0, 0, 0, 0, 0, 0, 0],
                   [ 0, 0, 0, 0, 0, 0, 0, 0, 0],
                   [ 0, 0, 0, 0, 0, 0, 0, 0, 0],
                   [ 0, 0, 0, 0, 0, 0, 0, 0, 0]
                  ]


  def get_board(self):
    return self._board


  def get_rows(self):
    return self._rows


  def get_cols(self):
    return self._cols


  def percentFilled(self):
    print(np.count_nonzero(self._board))
    return np.count_nonzero(self._board) / (self._rows * self._cols)


  def _calculate_liberties(self, row, col, visited):
    if row < 0 or row >= self._rows or col < 0 or col >= self._cols:
      raise ValueError("Coordinate outside board")
    elif self._board[row][col] == 0:
      raise ValueError("Empty coordinates can't have liberties")

    player = 1 if self._board[row][col] == 1 else -1

    if (col, row) not in visited:
      visited.add((col, row))

    liberties = 0
    surrounding = [(col-1, row), (col+1, row), (col, row+1), (col, row-1)]

    while len(surrounding) > 0:
      (surr_col, surr_row) = surrounding.pop()
      try:
        if (surr_col, surr_row) in visited:
          continue
        elif self._board[surr_row][surr_col] == 0:
          liberties += 1
        elif self._board[surr_row][surr_col] != player:
          visited.add((surr_col, surr_row))
        else:
          liberties += self._calculate_liberties(surr_row, surr_col, visited)
      except:
        continue
    return liberties


  def get_legal_actions(self):
    legal_actions = ["pass"] if self.percentFilled() > 0.75 else []
    i = len(legal_actions)
    for row in range(self._rows):
      for col in range(self._cols):
        added = False
        if self._board[row][col] != 0:
          continue
        elif (col, row) == self._ko and self._ko_player == -self.player: #ko
          continue

        self._board[row][col] = self.player # temporarily adding stone
        surrounding = [(col-1, row), (col+1, row), (col, row+1), (col, row-1)]
        while len(surrounding) > 0:
          (surr_col, surr_row) = surrounding.pop()
          try: # capture opponent?
            if self._board[surr_row][surr_col] == -self.player and self._calculate_liberties(row=surr_row, col=surr_col, visited=set()) == 0:
              self._ko = (surr_col, surr_row)
              self._ko_player = self.player
              legal_actions.append((col, row))
              i += 1
              added = True
              break
          except:
            continue

        if self._calculate_liberties(row=row, col=col, visited=set()) != 0 and not added: # not immediately captured
          legal_actions.append((col, row))
          self._ko = None
          self._ko_player = None
          i += 1

        self._board[row][col] = 0 # reset board
    print("legal moves found: " + str(i) + " for player: " + str(self.player))
    return legal_actions


  def is_game_over(self):
    if self._last_black_move == "pass" and self._last_white_move == "pass" and self.player == 1:
      return True
    # last_move = self._last_black_move if self.player == 1 else self._last_white_move
    # if self.get_legal_actions() == ["pass"] and last_move == "pass":
    #   return True

    for row in range(self._rows):
      for col in range(self._cols):
        if self._board[row][col] == 0:
          return False

    return True


  def _calculate_territories(self, area_counting):
    territories_map = {}
    black_territories, white_territories = 0,0
    black_stones, white_stones = 0,0
    neutral = False

    coords = set()
    for row in range(self._rows):
      for col in range(self._cols):
        coords.add((col, row))

    # visited = set()
    while len(coords) > 0:
      for coord in coords:
        (x, y) = coord
        # visited.add((x, y))
        coords.remove((x, y))
        break

      if self._board[y][x] == 0:
        border_color = 0
        territory_size = 1
        to_visit = deque()
        to_visit.append((x+1, y))
        to_visit.append((x-1, y))
        to_visit.append((x, y+1))
        to_visit.append((x, y-1))

        while len(to_visit) > 0:
          (this_x, this_y) = to_visit.popleft()
          if this_x < 0 or this_x >= self._cols or this_y < 0 or this_y >= self._rows:
            continue

          if (this_x, this_y) not in coords: # visited
            if self._board[this_y][this_x] != 0:
              if border_color == 0:
                border_color = self._board[this_y][this_x]
              elif self._board[this_y][this_x] != border_color:
                neutral = True
            continue

          coords.remove((this_x, this_y))

          if self._board[this_y][this_x] == 0:
            territory_size += 1
            to_visit.append((this_x+1, this_y))
            to_visit.append((this_x-1, this_y))
            to_visit.append((this_x, this_y+1))
            to_visit.append((this_x, this_y-1))
          else:
            thisColor = self._board[this_y][this_x]
            if border_color == 0:
              border_color = thisColor
            elif thisColor != border_color:
              neutral = True

            if thisColor == 1:
              black_stones += 1
            else:
              white_stones += 1

        if not neutral:
          if border_color == 1:
            black_territories += territory_size
          elif border_color == -1:
            white_territories += territory_size

      elif self._board[y][x] == 1:
        black_stones += 1
      elif self._board[y][x] == -1:
        white_stones += 1

    if area_counting:
      territories_map["Black"] = black_territories + black_stones
      territories_map["White"] = white_territories + white_stones
    else:
      territories_map["Black"] = black_territories
      territories_map["White"] = white_territories

    return territories_map


  def get_score(self):
    territories_map = self._calculate_territories(False)
    if self.player == 1:
      return territories_map["Black"] + self._blacks_prisoners
    else:
      return territories_map["White"] + self._whites_prisoners


  def count_group(self, col, row):
    if col < 0 or col >= self._cols or row < 0 or row >= self._rows:
      raise ValueError("Coordinate outside board")

    count = 1
    piece = self._board[row][col]
    visited = set()
    visited.add((col, row))
    surrounding = []
    surrounding.append((col-1, row))
    surrounding.append((col+1, row))
    surrounding.append((col, row-1))
    surrounding.append((col, row+1))

    while len(surrounding) > 0:
      (surr_col, surr_row) = surrounding.pop()
      if (surr_col, surr_row) in visited:
          continue
      else:
        visited.add((surr_col, surr_row))

      if surr_col < 0 or surr_col >= self._cols or surr_row < 0 or surr_row >= self._rows:
        continue
      elif self._board[surr_row][surr_col] == piece:
        count += 1
        surrounding.append((surr_col-1, surr_row))
        surrounding.append((surr_col+1, surr_row))
        surrounding.append((surr_col, surr_row-1))
        surrounding.append((surr_col, surr_row+1))

    return count

  def _flood_fill(self, col, row, player, visited):
    group = []
    if (col, row) not in visited:
      visited.add((col, row))
      if self._board[row][col] == player:
        group.append((col, row))
      else:
        return group
    surrounding = [(col+1, row), (col-1, row), (col, row+1), (col, row-1)]
    while len(surrounding) > 0:
      try:
        (surr_col, surr_row) = surrounding.pop()
        if (surr_col, surr_row) in visited:
          continue
        elif self._board[surr_row][surr_col] == 0:
          visited.add((surr_col, surr_row))
        elif self._board[surr_row][surr_col] != player:
          visited.add((surr_col, surr_row))
        else:
          group += self._flood_fill(surr_col, surr_row, player, visited)
      except:
        continue
    return group


  def _capture_prisoners(self):
    visited = set()
    for row in range(self._rows):
      for col in range(self._cols):
        if (col, row) not in visited:
          visited.add((col, row))
          if self._board[row][col] != 0:
            group = self._flood_fill(col, row, self._board[row][col], set())
            if self._calculate_liberties(row, col, set()) == 0:
              if self._board[row][col] == -1:
                self._blacks_prisoners += len(group)
              else:
                self._whites_prisoners += len(group)
              for (x, y) in group:
                self._board[y][x] = 0


  def move(self, col, row, passed):
    if row < 0 or row >= self._rows or col < 0 or col >= self._cols:
      raise ValueError("Coordinate outside board")
    elif self._board[row][col] != 0 and not passed:
      raise ValueError("Invalid move")

    if not passed:
      self._board[row][col] = self.player

    if self.player == 1:
      self._last_black_move = "pass" if passed else (col, row)
    else:
      self._last_white_move = "pass" if passed else (col, row)

    self._capture_prisoners()
    self.player *= -1

    return self