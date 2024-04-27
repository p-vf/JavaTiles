package game;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.UnorderedDeck;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents a deck on the client and on the server.
 * This class makes it easy to create, modify and query
 * decks.
 *
 * @author Robin Gökcen
 * @author Boran Gökcen
 * @author Istref Uka
 * @author Pascal von Fellenberg
 */

public class OrderedDeck {

  private static final Logger LOGGER = LogManager.getLogger(OrderedDeck.class);
  /**
   * height of each deck
   */
  public static final int DECK_HEIGHT = 2;
  /**
   * width of each deck
   */
  public static final int DECK_WIDTH = 12;

  private Tile[][] deck; // The 2D array representing the deck of tiles.

  private void setDeck(Tile[] tileArray) {
    deck = deckFromTileArray(tileArray);
  }

  /**
   * Sets the this.deck to the given deck.
   *
   * @param deck The 2D array representing the deck.
   */
  public void setDeck(Tile[][] deck) {
    for (int i = 0; i < Math.min(DECK_HEIGHT, deck.length); i++) {
      if (deck.length > DECK_HEIGHT) {
        LOGGER.warn("Trying to set deck with too many rows");
      }
      for (int j = 0; j < Math.min(DECK_WIDTH, deck[i].length); j++) {
        if (deck[i].length > DECK_WIDTH) {
          LOGGER.warn("Trying to set deck with too many columns");
        }
        this.deck[i][j] = deck[i][j];
      }
    }
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
   * Constructs a new ClientDeck object with a deck that has a height of 2 and a width of 12 and is filled with null values.
   */
  public OrderedDeck() {
    this.deck = new Tile[DECK_HEIGHT][DECK_WIDTH];
  }

  /**
   * Creates a new deck by populating it with tiles from a given array.
   * The tiles are distributed into rows and columns of the deck.
   * If the specified array of tiles doesn't have 24 elements, the remaining elements in the deck are filled with null values.
   *
   * @param tileArray array of tiles to populate the deck with (must have less than 25 elements)
   */
  public OrderedDeck(Tile[] tileArray) {
    setDeck(tileArray);
  }

  /**
   * Creates a new object by populating the deck with values from the two-dimensional array.
   * The given two-dimensional array doesn't have to have 2 rows and 12 columns but has to have less than 25 elements.
   *
   * @param tileArrays two-dimensional array of tiles to populate the deck with
   */
  public OrderedDeck(Tile[][] tileArrays) {
    Tile[] tileArray = new Tile[DECK_WIDTH * DECK_HEIGHT];
    int index = 0;
    loop: {
      for (Tile[] array : tileArrays) {
        for (Tile tile : array) {
          if (index >= DECK_WIDTH * DECK_HEIGHT) {
            LOGGER.warn("Tried to fill deck with an array that is too large. ");
            break loop;
          }
          tileArray[index++] = tile;
        }
      }
    }
    setDeck(tileArray);
  }


  /**
   * Converts the entire deck into a flat array of tiles.
   *
   * @return an array containing all tiles from the deck in a linear sequence
   */
  public Tile[] toTileArray() {
    Tile[] tileArray = new Tile[24];
    int count = 0;
    for (Tile[] tiles : deck) {
      System.arraycopy(tiles, 0, tileArray, count, tiles.length);
      count = deck[0].length;
    }
    return tileArray;
  }


  /**
   * Adds a sequence of tiles to the deck, filling empty slots in a row-major order.
   *
   * @param tiles The tiles to add to the deck.
   */
  public void addTiles(Tile... tiles) {
    int count = 0;
    if (tiles.length > 0) {
      for (int i = 0; i < deck.length; i++) {
        for (int j = 0; j < deck[0].length && count < tiles.length; j++) {
          if (deck[i][j] == null) {
            deck[i][j] = tiles[count++];
          }
        }
      }
    }
  }

  /**
   * Counts the total number of tiles currently present in the deck.
   *
   * @return the count of tiles in the deck
   */
  public int countTiles() {
    int count = 0;
    for (Tile[] tiles : deck) {
      for (Tile tile : tiles) {
        if (tile != null) {
          count++;
        }
      }
    }
    return count;

  }

  /**
   * Removes a tile from the specified location in the deck by setting it to null.
   *
   * @param row    row index of the tile to remove
   * @param column column index of the tile to remove
   * @return {@code true} if the deck changed due to this operation, {@code false} otherwise
   */
  public boolean removeTile(int row, int column) {
    if (deck[row][column] != null) {
      deck[row][column] = null;
      return true;
    } else {
      return false;
    }
  }

  /**
   * Swaps the positions of two tiles within the deck.
   *
   * @param row1    The row index of the first tile.
   * @param column1 The column index of the first tile.
   * @param row2    The row index of the second tile.
   * @param column2 The column index of the second tile.
   */
  public void swap(int row1, int column1, int row2, int column2) {
    Tile temp = deck[row1][column1];

    deck[row1][column1] = deck[row2][column2];
    deck[row2][column2] = temp;

  }

  /**
   * Returns the "unordered version" of the deck, meaning an instance of {@link UnorderedDeck} that contains each tile as often as it occurs in the deck.
   *
   * @return the unordered version of the deck
   */
  public UnorderedDeck toUnorderedDeck() {
    UnorderedDeck res = new UnorderedDeck();
    Tile[] tiles = toTileArray();
    for (Tile t : tiles) {
      res.add(t);
    }
    return res;
  }


  /**
   * Returns a simple string representation of the deck.
   *
   * @return string representation of the deck
   */
  @Override
  public String toString() {
    return Arrays.deepToString(deck);
  }

  /**
   * Returns a string representation that is easier to look at.
   * For each tile it uses the method {@link Tile#toStringPretty()} to get a colorful representation of the tile using ansi escape codes.
   * The output of this method looks something like the following:
   * <pre>
   *   __|_0|_1|_2|_3|_4|_5|_6|_7|_8|_9|10|11|
   *    0| 3| 4| 5| 6|  |  |  |10|11|JT|  |  |
   *    1|  | 8|  |12|12|  | 3|  |  | 4| 4| 4|
   * </pre>
   *
   * @return a visually formatted string representing the deck with tiles
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
   * Returns a list of strings that all represent tiles, formatted like the {@link Tile#toString()} method does.
   *
   * @return list of tiles in string representation
   */
  public ArrayList<String> toStringArrayList() {
    Tile[] tiles = toTileArray();
    ArrayList<String> res = new ArrayList<>(DECK_HEIGHT * DECK_WIDTH);
    for (int i = 0; i < DECK_HEIGHT * DECK_WIDTH; i++) {
      Tile current = tiles[i];
      if (current == null) {
        res.add("");
      } else {
        res.add(current.toString());
      }
    }
    return res;
  }


  /**
   * Checks whether deck is a winning deck. The deck is a winning deck,
   * if it only contains "runs" and "sets" of length greater than 3.
   * A "run" and a "set" is a section fo the deck either bound by null, the end of the array or the element at index 12.
   * (The last condition is so that the sections makes sense visually, as the deck is presented in two rows of 12)
   * A "run" is a section only containing tiles with the same color and from left to right consecutively ascending numbers.
   * A "set" is a section only containing tiles with the same number but distinct colors.
   * <p>
   * example of a winning configuration: {@code [2:BLUE, 3:BLUE, 4:BLUE, 5:BLUE, 6:BLUE, null, 3:BLUE, 3:RED, 3:BLACK, 3:YELLOW, null, null, 6:BLACK, 7:BLACK, 8:BLACK, 9:BLACK, 10:BLACK, null, null, null, null, null, null, null]}
   * <p>
   * example of a not winning configuration: {@code [2:BLUE, 3:BLUE, 4:BLUE, 5:BLUE, 6:BLUE, null, 2:BLUE, 3:RED, 4:BLACK, 5:YELLOW, null, 6:BLACK, 7:BLACK, 8:BLACK, 9:BLACK, 10:BLACK, null, null, null, null, null, null, null, null]}
   * (the tile 6:BLACK is in a section with length one, as it is the element before index 12)
   * <p>
   * example of a not winning configuration: {@code [2:BLUE, 3:BLUE, 4:BLUE, 5:BLUE, 6:BLUE, null, 4:BLUE, 3:RED, 3:BLACK, 3:YELLOW, null, null, 6:BLACK, 7:BLACK, 8:BLACK, 9:BLACK, 10:BLUE, null, null, null, null, null, null, null]}
   * (The second section has distinct colors but two distinct numbers, the third section has consecutively ascending numbers but more than one distinct color)
   *
   * @return true if and only if the deck is of a winning configuration.
   */
  public boolean isWinningDeck() {
    int formationLength = 0;
    boolean returnValue = true;
    for (Tile[] tileRow : deck) {
      for (int j = 0; j <= DECK_WIDTH; j++) {
        Tile currentTile = null;
        if (j < DECK_WIDTH) {
          currentTile = tileRow[j];
        }
        if (currentTile == null) {
          int from = j - formationLength;
          int to = j;
          String output = Arrays.toString(Arrays.copyOfRange(tileRow, from, to));
          if (formationLength < 3 && formationLength > 0) {
            return false;
          }
          if (formationLength == 0) {
            continue;
          }
          boolean validRun = isValidRun(tileRow, from, to);
          boolean validSet = isValidSet(tileRow, from, to);
          if (validRun) {
            //System.out.println("Valid run: " + output);
          }
          if (validSet) {
            //System.out.println("Valid set: " + output);
          }
          if (!validSet && !validRun) {
            System.out.println("Invalid section: " + output);
            System.out.println("Returning false: Neither a valid set nor a valid run.");
            return false;
          }
          formationLength = 0;

          continue;
        }
        formationLength++;
      }
      formationLength = 0;
    }

    System.out.println("Deck evaluation completed without errors. Deck is winning.");
    return returnValue;
  }


  /**
   * Checks whether a range in a row of the deck is a valid "set" according to the rules of the game.
   *
   * @param deckRow an Array of tiles representing a row of the deck
   * @param from the start of the range (inclusive, must be a number between 0 and the length of the deck minus one)
   * @param to the end of the range (exclusive, must be a number between 1 and the length of the deck)
   * @return {@code true} if the range in the deck is a valid set, {@code false} otherwise
   */
  public static boolean isValidSet(Tile[] deckRow, int from, int to) {
    if (to - from > 4) {
      return false;
    }
    int mask = 0;
    int number = deckRow[from].getNumber();
    for (int i = from; i < to; i++) {
      if (number == 0) { // if first element was a joker
        number = deckRow[i].getNumber();
      }
      Tile currentTile = deckRow[i];
      if (currentTile == null) {
        // should never be reached
        LOGGER.error("Set in deck contains null");
        return false;
      }
      System.out.println(currentTile.getNumber());
      if (currentTile.isJoker()) {
        continue;
      }
      if (currentTile.getNumber() != number) {
        return false;
      }
      switch (currentTile.getColor()) {
        case RED -> {
          if (mask / 1 % 2 == 1) {
            return false;
          }
          mask += 1;
        }
        case BLUE -> {
          if (mask / 2 % 2 == 1) {
            return false;
          }
          mask += 2;
        }
        case YELLOW -> {
          if (mask / 4 % 2 == 1) {
            return false;
          }
          mask += 4;
        }
        case BLACK -> {
          if (mask / 8 % 2 == 1) {
            return false;
          }
          mask += 8;
        }
      }
    }
    return true;
  }

  /**
   * Checks whether a range in the deck is a valid run according to the rules of the game.
   *
   * @param deckRow an Array of tiles representing a row of the deck
   * @param from the start of the range (inclusive, must be a number between 0 and the length of the deck minus one)
   * @param to the end of the range (exclusive, must be a number between 1 and the length of the deck)
   * @return {@code true} if the range in the deck is a valid run, {@code false} otherwise
   */
  public static boolean isValidRun(Tile[] deckRow, int from, int to) {
    //TODO: Darf man einen ValidRun bis zur 1 machen oder bis zur 13?
    Tile firstTile = deckRow[from];
    if (firstTile == null) {
      // this line should never be reached
      LOGGER.error("Range in deck contains null..");
      return false;
    }
    Color color = firstTile.getColor();
    int startNum = firstTile.getNumber();
    int jokerCountBeforeFirstNumber = 0;
    System.out.println("from = " + from);
    for (int i = from; i < to; i++) {
      Tile currentTile = deckRow[i];
      if (currentTile == null) {
        // this line should never be reached
        LOGGER.error("Range in deck contains null..");
        return false;
      }
      if (startNum == 0){
        startNum = currentTile.getNumber();
        color = currentTile.getColor();
        //TODO Joker als erstes, was passiert dann?
      }
      //System.out.println("CURRENTTILE = " + currentTile.getNumber());
      if (currentTile.isJoker()) {
        if(startNum == 0){
          jokerCountBeforeFirstNumber++;
        }
        if(i >= from + 1){ //to avoid nullpointer exception
          if(deckRow[i-1].getNumber() == 13){
            return false;
          }
        }

        continue;
      }
      //System.out.println("jokercount = " + jokerCountBeforeFirstNumber);

      if (currentTile.getColor() != color) {
        return false;
      }
      int compare = i - from + startNum- jokerCountBeforeFirstNumber;
      //System.out.println("compare = " + compare);
      if (currentTile.getNumber() != compare) { //TODO
        System.out.println();
        return false;
      }
    }
    jokerCountBeforeFirstNumber = 0;
    return true;
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
    OrderedDeck newDeck = new OrderedDeck(tileArray);
    System.out.println(newDeck.toStringPretty());
    newDeck.removeTile(1, 1); //should remove 13 YELLOW and replace with null
    System.out.println(newDeck.toStringPretty());
    //System.out.println(Arrays.deepToString(tileArray));
  }

  /**
   * Finds the first occurrence of the specified tile and replaces it with null.
   *
   * @param tile the tile to be removed
   * @throws IllegalArgumentException if the specified tile wasn't found
   */
  public void findAndRemove(Tile tile) throws IllegalArgumentException {
    Tile[] tiles = toTileArray();
    for (int i = 0; i < DECK_HEIGHT * DECK_WIDTH; i++) {
      if (tiles[i] == null) {
        continue;
      }
      if (tiles[i].equals(tile)) {
        tiles[i] = null;
        deck = deckFromTileArray(tiles);
        return;
      }
    }
    throw new IllegalArgumentException("Tried to remove Tile from a deck that doesn't contain the tile");
  }

  /**
   * Finds the first spot in the deck that is null and replaces its value with the specified tile.
   *
   * @param tile tile to be added to the deck
   * @throws IllegalArgumentException if the deck is already full
   */
  public void fillFirstEmptySpot(Tile tile) throws IllegalArgumentException {
    Tile[] tiles = toTileArray();
    for (int i = 0; i < DECK_HEIGHT * DECK_WIDTH; i++) {
      if (tiles[i] == null) {
        tiles[i] = tile;
        deck = deckFromTileArray(tiles);
        return;
      }
    }
    throw new IllegalArgumentException("Tried to add tile to a deck that is already full");
  }

  /**
   * Returns deck from array of tiles in the correct format, meaning a two-dimensional array of tiles with 2 rows and 12 columns.
   * It does this in a way that preserves the order of {@param tileArray}.
   *
   * @param tileArray array of tiles to populate the resulting two-dimensional array with (length must be smaller than 25)
   * @return populated two-dimensional array of tiles populated with the tiles of the specified array of tiles
   */
  private static Tile[][] deckFromTileArray(Tile[] tileArray) {
    if (tileArray.length > DECK_WIDTH * DECK_HEIGHT) {
      throw new IllegalArgumentException("Tile[] tileArray is too big for deck");
    }
    Tile[][] res = new Tile[DECK_HEIGHT][DECK_WIDTH];
    int count = 0;
    loop:
    {
      for (int i = 0; i < DECK_HEIGHT; i++) {
        for (int j = 0; j < DECK_WIDTH; j++) {
          if (count >= tileArray.length) {
            break loop;
          }
          res[i][j] = tileArray[count++];
        }
      }
    }
    return res;
  }
}







