package client;

import game.Color;
import game.Tile;

import java.util.Arrays;

/**
 * Represents a deck of tiles used by a client in a game scenario.
 * This class manages operations related to the deck, such as creating,
 * modifying, and querying the deck's contents.
 */

public class ClientDeck {

  private Tile[][] deck; // The 2D array representing the deck of tiles.

  /**
   * Constructs a new ClientDeck object with a default size of 2 rows and 12 columns.
   * The deck is initialized as a 2D array of Tile objects.
   */
  public ClientDeck() {
    this.deck = new Tile[2][12];
  }

  /**
   * Retrieves the 2D array representing the deck of tiles.
   *
   * @return The deck of tiles as a 2D array of Tile objects.
   */
  public Tile[][] getDeck() {
    return deck;
  }

  /**
   * Sets the this.deck to the given deck.
   *
   * @param deck The 2D array representing the deck.
   */
  public void setDeck(Tile[][] deck) {
    for (int i = 0; i < deck.length; i++) {
      for (int j = 0; j < deck[i].length; j++) {
        this.deck[i][j] = deck[i][j];

      }
    }
  }

  /**
   * Retrieves a specific tile from the deck based on its column and row indices.
   *
   * @param column The column index of the desired tile.
   * @param row    The row index of the desired tile.
   * @return The tile located at the specified column and row in the deck.
   */
  public Tile getTile(int column, int row) {
    return deck[column][row];
  }


  /**
   * Creates a new deck by populating it with tiles from a given array.
   * The tiles are distributed into rows and columns of the deck.
   *
   * @param tileArray The array of tiles to populate the deck with.
   */
  public Tile[][] createDeckwithTileArray(Tile[] tileArray) {
    Tile[][] newDeck = new Tile[deck.length][deck[0].length];
    int count = 0;
    loop:
    {
      for (int i = 0; i < deck.length; i++) {
        for (int j = 0; j < deck[i].length; j++) {
          if (count >= tileArray.length) {
            break loop;
          }
          newDeck[i][j] = tileArray[count++];
        }
      }
    }
    return newDeck;
  }

  /**
   * Converts the entire deck into a flat array of tiles.
   *
   * @return An array containing all tiles from the deck in a linear sequence.
   */
  public Tile[] DeckToTileArray() {
    Tile[] tileArray = new Tile[24];
    int count = 0;
    for (int i = 0; i < deck.length; i++) {
      for (int j = 0; j < deck[i].length; j++) {
        tileArray[j + count] = deck[i][j];
      }
      count = deck[0].length;
    }
    return tileArray;
  }


  /**
   * Adds a sequence of tiles to the deck, filling empty slots in a row-major order.
   *
   * @param tileArray The tiles to add to the deck.
   */
  public void addTheseTiles(Tile... tileArray) {
    int count = 0;
    if (tileArray.length > 0) {
      for (int i = 0; i < deck.length; i++) {
        for (int j = 0; j < deck[0].length && count < tileArray.length; j++) {
          if (deck[i][j] == null) {
            deck[i][j] = tileArray[count];
            count++;
          }
        }
      }
    }
  }

  /**
   * Counts the total number of tiles currently present in the deck.
   *
   * @return The count of tiles in the deck.
   */
 public int countTiles() {
    int count = 0;
    for (int i = 0; i < deck.length; i++) {
      for (int j = 0; j < deck[i].length; j++) {
        if (deck[i][j]!= null) {
          count++;
        }
      }
    }
    return count;

 }

  /**
   * Removes a tile from the specified location in the deck by setting it to null.
   *
   * @param row    The row index of the tile to remove.
   * @param column The column index of the tile to remove.
   */
  public void removeTile(int row, int column) {
    Tile[][] newDeck = new Tile[deck.length][deck[0].length];
    for (int i = 0; i < deck.length; i++) {
      for (int j = 0; j < deck[i].length; j++) {
        if ((i != row) || (j != column)) {
          newDeck[i][j] = deck[i][j];
        }

      }
    }
    for (int k = 0; k < deck.length; k++) {
      for (int l = 0; l < deck[k].length; l++) {
        deck[k][l] = newDeck[k][l];
      }
    }
  }

  /**
   * Swaps the positions of two tiles within the deck.
   *
   * @param row    The row index of the first tile.
   * @param column The column index of the first tile.
   * @param row1   The row index of the second tile.
   * @param column1 The column index of the second tile.
   */
  public void swap(int row, int column, int row1, int column1) {

    Tile tileToSwap = deck[row1][column1];
    Tile tileToSwap2 = deck[row][column];

    deck[row][column] = tileToSwap;
    deck[row1][column1] = tileToSwap2;

  }


  /**
   * Returns a string representation of the deck in a deep format.
   *
   * @return A string representation of the deck, including all tiles.
   */
  @Override
  public String toString() {
    return Arrays.deepToString(deck);
  }

  /**
   * Returns a pretty formatted string representation of the deck.
   * The deck is displayed in a structured grid format with row and column labels.
   *
   * @return A visually formatted string representing the deck with tiles.
   */
  public String toStringPretty() {
    StringBuilder res = new StringBuilder();
    res.append("__|_0|_1|_2|_3|_4|_5|_6|_7|_8|_9|10|11|\n");
    for (int i = 0; i < 2; i++) {
      res.append(String.format("%d |", i));
      for (int j = 0; j < 12; j++) {
        Tile currentTile = deck[i][j];
        if (currentTile == null) {
          res.append("  |");
          continue;
        }
        res.append(currentTile.toStringPretty());
        res.append("|");
      }
      if (i == 0) {
        res.append("\n");
      }
    }
    return res.toString();
  }

  /**
   * Main method for testing the functionalities of the ClientDeck class.
   *
   * @param args The command-line arguments (not used).
   */
  public static void main(String[] args) {
    Tile[] tileArray = new Tile[24];

    tileArray[0] = new Tile(0, Color.BLACK);
    tileArray[1] = new Tile(1, Color.YELLOW);
    tileArray[2] = new Tile(2, Color.BLUE);
    tileArray[3] = new Tile(3, Color.RED);
    tileArray[4] = new Tile(4, Color.BLACK);
    tileArray[5] = new Tile(5, Color.YELLOW);
    tileArray[6] = new Tile(6, Color.BLUE);
    tileArray[7] = new Tile(7, Color.RED);
    tileArray[8] = new Tile(8, Color.BLACK);
    tileArray[9] = new Tile(9, Color.YELLOW);
    tileArray[10] = new Tile(10, Color.BLUE);
    tileArray[11] = new Tile(11, Color.RED);
    tileArray[12] = new Tile(12, Color.BLACK);
    tileArray[13] = new Tile(13, Color.YELLOW);
    tileArray[14] = new Tile(0, Color.BLUE);
    tileArray[15] = new Tile(1, Color.RED);
    tileArray[16] = new Tile(2, Color.BLACK);
    tileArray[17] = new Tile(3, Color.YELLOW);
    tileArray[18] = new Tile(4, Color.BLUE);
    tileArray[19] = new Tile(5, Color.RED);
    tileArray[20] = new Tile(6, Color.BLACK);
    tileArray[21] = new Tile(7, Color.YELLOW);
    tileArray[22] = new Tile(8, Color.BLUE);
    tileArray[23] = new Tile(9, Color.RED);

    //System.out.println(Arrays.deepToString(tileArray));
    ClientDeck newDeck = new ClientDeck();
    newDeck.createDeckwithTileArray(tileArray);
    System.out.println(newDeck);
    newDeck.removeTile(1, 1); //should remove 13 YELLOW and replace with null
    System.out.println(newDeck);
    Tile[] tileArray2 = newDeck.DeckToTileArray();
    //System.out.println(Arrays.deepToString(tileArray));
  }

}







