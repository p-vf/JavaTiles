package server;

import game.Color;
import game.Tile;
import static game.Color.*;

import java.util.*;

public class GameState {
  // check out the following methods to get the elements in the stack:
  // elements() and toArray(Tile[] t);
  Stack<Tile> mainStack;
  ArrayList<Stack<Tile>> exchangeStacks;
  ArrayList<HashSet<Tile>> playerDecks;
  int currentPlayerIdx;

  /**
   * Constructor of the {@code GameState} class.
   * Initializes {@code Gamestate} with a shuffled set of Tiles that get distributed to the playerdecks.
   * @param startPlayerIdx the index of the players that gets 15 instead of 14 tiles and is the first player that gets to make a move.
   */
  public GameState(int startPlayerIdx) {
    currentPlayerIdx = startPlayerIdx;

    // initialize exchange-stacks
    exchangeStacks = new ArrayList<>();
    for (int i = 0; i < 4; i++) {
      exchangeStacks.add(new Stack<>());
    }

    // initialize array with unique tiles.
    Tile[] tiles = new Tile[106];
    for (int i = 0; i < 104; i++) {
      tiles[i] = new Tile(i % 13 + 1, Color.values()[i*4/104]);
    }
    // add two jokers
    tiles[104] = new Tile(0, BLUE);
    tiles[105] = new Tile(0, RED);
    Tile.shuffleTiles(tiles);

    // give 14 tiles to each player except for the player that starts, he gets 15
    playerDecks = new ArrayList<>();
    int counter = 0;
    for (int p = 0; p < 4; p++) {
      HashSet<Tile> temp = new HashSet<>();
      for (int i = 0; i < 14; i++) {
        temp.add(tiles[counter++]);
      }
      if (p == startPlayerIdx) {
        temp.add(tiles[counter++]);
      }
      playerDecks.add(temp);
    }

    // the rest of the tiles get added to the main stack
    mainStack = new Stack<>();
    for (; counter < tiles.length; counter++) {
      mainStack.add(tiles[counter]);
    }
  }


  // for testing purposes:
  public static void main(String[] args) {
    GameState g = new GameState(2);
  }
}
