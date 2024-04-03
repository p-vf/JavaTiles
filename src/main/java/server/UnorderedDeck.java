package server;

import game.Color;
import game.Tile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents a deck that contains different tiles. Is only relevant for server-side calculations (validation).
 */
public class UnorderedDeck {
  public final Logger LOGGER = LogManager.getLogger();

  // array filled with counts (either 0, 1, or 2) for each tile
  // there are 53 distinct tiles in the game, two of each, so 106 in total
  int[] state = new int[53];
  private int tileToIndex(Tile tile) {
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

  public UnorderedDeck(ArrayList<Tile> deck) {
    for (var tile : deck) {
      this.add(tile);
    }
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
