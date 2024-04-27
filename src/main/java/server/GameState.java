package server;

import game.Color;
import game.OrderedDeck;
import game.Tile;

import static game.Color.*;

import java.util.*;

/**
 * The GameState class handles the game state for our tile-based board game. It manages tile distribution
 * among players, tracks the main and exchange stacks, and maintains each player's personal deck. Key functionalities
 * include initializing the game with shuffled tiles, enabling players to draw or exchange tiles, and managing turn
 * order. It provides methods to check game conditions like visibility of tiles, drawing eligibility, and turn validation.
 *
 * @author Pascal von Fellenberg
 * @author Istref Uka
 */
public class GameState {
  // check out the following methods to get the elements in the stack:
  // elements() and toArray(Tile[] t);
  private Stack<Tile> mainStack;
  private ArrayList<Stack<Tile>> exchangeStacks;
  private ArrayList<OrderedDeck> playerDecks;
  private int currentPlayerIndex;
  private int numberOfDraws = 0;

  public void setPlayerDeck(int playerIndex, OrderedDeck deck) {
    playerDecks.set(playerIndex, deck);
  }

  public OrderedDeck getPlayerDeck(int playerIndex) {
    return playerDecks.get(playerIndex);
  }

  public int getCurrentPlayerIndex() {
    return currentPlayerIndex;
  }

  /**
   * Constructor of the {@code GameState} class.
   * Initializes {@code Gamestate} with a shuffled set of Tiles that get distributed to the playerdecks.
   *
   * @param startPlayerIdx the index of the players that gets 15 instead of 14 tiles and is the first player that gets to make a move.
   */
  public GameState(int startPlayerIdx) {
    currentPlayerIndex = startPlayerIdx;

    // initialize exchange-stacks
    exchangeStacks = new ArrayList<>();
    for (int i = 0; i < 4; i++) {
      exchangeStacks.add(new Stack<>());
    }

    // initialize array with unique tiles.
    Tile[] tiles = new Tile[106];
    for (int i = 0; i < 104; i++) {
      tiles[i] = new Tile(i % 13 + 1, Color.values()[i * 4 / 104]);
    }
    // add two jokers
    tiles[104] = new Tile(0, BLUE);
    tiles[105] = new Tile(0, RED);
    Tile.shuffleTiles(tiles);

    // give 14 tiles to each player except for the player that starts, he gets 15
    playerDecks = new ArrayList<>();
    int counter = 0;
    for (int p = 0; p < 4; p++) {
      Tile[] deckTiles = new Tile[15];
      for (int i = 0; i < 14; i++) {
        deckTiles[i] = tiles[counter++];
      }
      if (p == startPlayerIdx) {
        deckTiles[14] = tiles[counter++];
      }
      playerDecks.add(new OrderedDeck(deckTiles));
    }

    // the rest of the tiles get added to the main stack
    mainStack = new Stack<>();
    for (; counter < tiles.length; counter++) {
      mainStack.add(tiles[counter]);
    }
  }

  /**
   * Returns an array of the top visible tiles from each exchange stack.
   *
   * @return an array containing the top tile from each of the four exchange stacks.
   * If an exchange stack is empty, its corresponding array element is null.
   */
  public Tile[] getVisibleTiles() {
    Tile[] tiles = new Tile[4];
    for (int i = 0; i < 4; i++) {
      Stack<Tile> stack = exchangeStacks.get(i);
      if (stack.isEmpty()) {
        tiles[i] = null;
        continue;
      }
      tiles[i] = stack.peek();
    }
    return tiles;
  }

  /**
   * Draws a tile for the specified player from either the main stack or their exchange stack.
   *
   * @param isMainStack true to draw from the main stack, false to draw from the player's exchange stack.
   * @param playerIndex the index of the player who is drawing the tile.
   * @return the drawn tile, or null if the chosen stack is empty.
   */
  public Tile drawTile(boolean isMainStack, int playerIndex) {
    Stack<Tile> stack;
    if (isMainStack) {
      stack = mainStack;
    } else {
      stack = exchangeStacks.get(playerIndex);
    }
    Tile tile;
    try {
      tile = stack.pop();
    } catch (EmptyStackException e) {
      tile = null;
    }
    playerDecks.get(playerIndex).fillFirstEmptySpot(tile);
    numberOfDraws++;
    return tile;
  }

  /**
   * Checks if a player can draw a tile, which is true when the player's deck contains 14 tiles.
   *
   * @param playerIndex the index of the player to check.
   * @return true if the player can draw a tile, false otherwise.
   */
  public boolean canDraw(int playerIndex) {
    return playerDecks.get(playerIndex).countTiles() == 14;
  }

  /**
   * Determines if it is the specified player's turn.
   *
   * @param playerIndex the index of the player to check.
   * @return true if it is the player's turn, false otherwise.
   */
  public boolean isPlayersTurn(int playerIndex) {
    return currentPlayerIndex == playerIndex;
  }

  /**
   * Puts a specified tile into the exchange stack of the next player and updates the game state.
   *
   * @param tile        the tile to be placed into the next player's exchange stack.
   * @param playerIndex the index of the player who is putting the tile.
   */
  public void putTile(Tile tile, int playerIndex) {

    // remove tile that the player chose from the playerDeck
    playerDecks.get(playerIndex).findAndRemove(tile);
    // add the tile to the exchangeStack of the next player.
    exchangeStacks.get((playerIndex + 1) % 4).push(tile);
    // update the current player index
    currentPlayerIndex += 1;
    currentPlayerIndex %= 4;
  }

  /**
   * Checks if a player can put a tile into the exchange stack, which is true when the player's deck contains 15 tiles.
   *
   * @param playerIndex the index of the player to check.
   * @return true if the player can put a tile, false otherwise.
   */
  public boolean canPutTile(int playerIndex) {
    return playerDecks.get(playerIndex).countTiles() == 15;
  }

  public int currentRoundNumber() {
    return numberOfDraws / 4 + 1;
  }
  // for testing purposes:
  // doesn't get called during normal gameplay

  public static void main(String[] args) {
    GameState g = new GameState(2);
  }
}
