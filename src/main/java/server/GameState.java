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
  private final Stack<Tile> mainStack;
  private final ArrayList<Stack<Tile>> exchangeStacks;
  private final ArrayList<OrderedDeck> playerDecks;
  private int currentPlayerIndex;
  private int numberOfDraws = 0;

  /**
   * Replaces the deck of a player with the specified deck.
   *
   * @param playerIndex index of the player (must be a number between 0 and 3)
   * @param deck        deck that the player gets
   */
  public void setPlayerDeck(int playerIndex, OrderedDeck deck) {
    playerDecks.set(playerIndex, deck);
  }

  /**
   * Returns the deck of a player
   *
   * @param playerIndex index of the player (must be a number between 0 and 3)
   * @return            deck of the player on the specified index
   */
  public OrderedDeck getPlayerDeck(int playerIndex) {
    return playerDecks.get(playerIndex);
  }

  /**
   * Returns the index of the player who is to move (the current player).
   *
   * @return index of the current player (must be a number between 0 and 3)
   */
  public int getCurrentPlayerIndex() {
    return currentPlayerIndex;
  }

  /**
   * Constructor of the {@code GameState} class.
   * Initializes {@code GameState} with a shuffled set of Tiles that get distributed to the playerdecks.
   *
   * @param startPlayerIdx the index of the players that gets 15 instead of 14 tiles and is the first player that gets to make a move. (must be a number between 0 and 3)
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
   * @param isMainStack represents the stack from which the player draws; {@code true} for main stack, {@code false} for the player's exchange stack
   * @param playerIndex the index of the player who is drawing the tile (must be a number between 0 and 3)
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
   * Checks if a player can draw a tile, which is true if and only if the player's deck contains 14 tiles.
   *
   * @param playerIndex index of the player to check (must be a number between 0 and 3)
   * @return {@code true} if the player can draw a tile, {@code false} otherwise
   */
  public boolean canDraw(int playerIndex) {
    return playerDecks.get(playerIndex).countTiles() == 14;
  }

  /**
   * Determines if it is the specified player's turn.
   *
   * @param playerIndex index of the player to check (must be between 0 and 3)
   * @return            {@code true} if it is the player's turn, {@code false} otherwise
   */
  public boolean isPlayersTurn(int playerIndex) {
    return currentPlayerIndex == playerIndex;
  }

  /**
   * Puts a specified tile into the exchange stack of the next player and updates the game state.
   *
   * @param tile        tile to be placed into the next player's exchange stack
   * @param playerIndex index of the player who is putting the tile (must be a number between 0 and 3)
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
   * @param playerIndex index of the player to check (must be a number between 0 and 3)
   * @return {@code true} if the player can put a tile, {@code false} otherwise
   */
  public boolean canPutTile(int playerIndex) {
    return playerDecks.get(playerIndex).countTiles() == 15;
  }

  /**
   * Returns the number of rounds played since the start of the game.
   * @return number of rounds played since the start of the game
   */
  public int currentRoundNumber() {
    return numberOfDraws / 4 + 1;
  }

  /**
   * for testing purposes, doesn't get called during normal gameplay
   * @param args isn't needed for execution of method
   */
  public static void main(String[] args) {
    GameState g = new GameState(2);
  }
}
