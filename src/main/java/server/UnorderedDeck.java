package server;

import game.Color;
import game.Tile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;

import static game.Color.*;

/**
 * Represents a deck that contains different tiles. Is only relevant for server-side calculations (validation).
 */
public class UnorderedDeck {
  public final Logger LOGGER = LogManager.getLogger();

  // array filled with counts (either 0, 1, or 2) for each tile
  // there are 53 distinct tiles in the game, two of each, so 106 in total
  int[] state = new int[53];

  // for testing purposes
  public static void main(String[] args) {
    try {
      for (int i = 0; i < 53; i++) {
        System.out.println(i + ": " + tileToIndex(indexToTile(i)));
      }
      Tile[] tiles = new Tile[]{
          new Tile(1, BLUE),
          new Tile(5, BLUE),
          new Tile(8, BLUE),
          new Tile(11, BLUE),
          new Tile(12, BLUE),
          new Tile(3, BLACK),
          new Tile(1, BLUE),
          new Tile(1, BLUE),
      };
      UnorderedDeck deck = new UnorderedDeck(new ArrayList<>(Arrays.asList(tiles)));
      System.out.println(deck.toStringArray());
    } catch (RuntimeException e) {
      e.printStackTrace(System.err);
    }
  }

  /**
   * This is a bijection from tile to number. It's inverse (indexToTile()) is also a bijection.
   * @param tile
   * @return
   */
  private static int tileToIndex(Tile tile) {
    // TODO implement this
    Color color = tile.getColor();
    int number = tile.getNumber();
    // if the tile is a joker, the index is at the beginning of the array.
    if (number == 0) {
      return 0;
    }
    int colorNumber = Arrays.stream(Color.values()).toList().indexOf(color);
    return colorNumber * 13 + number;
  }

  /**
   * This is a bijection from tile to number. It's inverse (tileToIndex()) is also a bijection.
   * @param index
   * @return
   */
  private static Tile indexToTile(int index) {
    if (index == 0) {
      return new Tile(0, BLACK);
    }
    index--;
    int number = index % 13 + 1;
    Color color = Color.values()[index/13];
    return new Tile(number, color);
  }

  public UnorderedDeck(ArrayList<Tile> deck) {
    for (var tile : deck) {
      this.add(tile);
    }
  }
  public UnorderedDeck() {

  }

  /**
   * Adds a tile to the deck.
   * @param tile tile to be added to deck. If null, nothing is changed about the deck.
   */
  public void add(Tile tile) {
    if (tile == null) {
      return;
    }
    int num = ++state[tileToIndex(tile)];
    if (num > 2) {
      // sanity check, only for debugging
      LOGGER.debug("(should-be-unreachable) added too many tiles to deck");
    }
  }

  /**
   * Removes tile from deck.
   * @param tile tile to be removed. Should not be null
   */
  public void remove(Tile tile) {
    if (tile == null) {
      LOGGER.debug("Tried to remove tile that is null from ServerDeck instance. ");
      return;
    }
    int num = --state[tileToIndex(tile)];
    if (num < 0) {
      // sanity check, only for debugging
      LOGGER.debug("(should-be-unreachable) removed too many tiles from deck");
    }
  }

  public ArrayList<String> toStringArray() {
    ArrayList<String> res = new ArrayList<>();
    for (int idx = 0; idx < state.length; idx++) {
      for (int i = 0; i < state[idx]; i++) {
        res.add(indexToTile(idx).toString());
        System.out.println("bruh");
      }
    }
    return res;
  }

  @Override
  public boolean equals(Object other) {
    if (other == null) {
      return false;
    }
    if (!(other instanceof UnorderedDeck otherDeck)) {
      return false;
    }
    return Arrays.equals(otherDeck.state, state);
  }
}
