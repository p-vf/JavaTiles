package game;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static utils.NetworkUtils.encodeProtocolMessage;

/**
 * Represents a tile in the game.
 * If the number is 0, the tile represents a joker and the color is irrelevant,
 * else it is a normal tile with a number and a color.
 */
public class Tile {
  public static final Logger LOGGER = LogManager.getLogger(Tile.class);

  private final int number;
  private final Color color;
  public Tile(int number, Color color) {
    if (number > 13 || number < 0) {
      throw new IllegalArgumentException("Tile has number outside the required range.");
    }
    this.number = number;
    this.color = color;
  }

  public int getNumber() {
    return number;
  }
  public Color getColor() {
    return color;
  }

  /**
   * Parses the tile that tileString represents.
   * @param tileString The string representing a tile. must be of the following format: {@code "<number>:<color>"}
   * @return the tile that tileString represents. If tileString is empty, null.
   * @throws IllegalArgumentException if the tileString is of incorrect format
   */
  public static Tile parseTile(String tileString) throws IllegalArgumentException {
    if (tileString.isEmpty()) {
      return null;
    }
    String[] fields = tileString.split(":");
    if (fields.length != 2) {
      throw new IllegalArgumentException("Incorrect amount of fields for Tile (must be 2, is " + fields.length + ").");
    }
    int tileNumber = Integer.parseInt(fields[0]);
    if (tileNumber > 13 || tileNumber < 0) {
      throw new IllegalArgumentException("Number " + tileNumber + " is no valid value for a tile (must be between 0 and 13, inclusive)");
    }
    Color tileColor = Color.valueOf(fields[1]);

    return new Tile(tileNumber, tileColor);
  }

  public static Tile[] stringsToTileArray(ArrayList<String> stringList) throws IllegalArgumentException {
    Tile[] tileArray = new Tile[stringList.size()];
    String tileString;
    for (int i = 0; i < stringList.size(); i++){
      tileString = stringList.get(i);
      Tile tile = parseTile(tileString);
      tileArray[i] = tile;
    }
    return tileArray;
  }


  public static String tileArrayToProtocolArgument(Tile[] tileArray){
    ArrayList<String> tileStringList= new ArrayList<>();
    for (Tile tile : tileArray) {
      if (tile == null) {
        tileStringList.add("");
      } else {
        tileStringList.add(tile.toString());
      }
    }
    return encodeProtocolMessage(tileStringList);
  }


  /**
   * Fisher-Yates shuffle for the shuffling of the tile array.
   * @param tiles Array of tiles to be shuffled
   */
  public static void shuffleTiles(Tile[] tiles) {
    Random rnd = new Random();
    for (int i = 0; i < tiles.length; i++) {
      int j = rnd.nextInt(i, tiles.length);
      Tile t = tiles[i];
      tiles[i] = tiles[j];
      tiles[j] = t;
    }
  }


  /**
   * Shows if the tile is a joker.
   * @return true iff the tile is a joker.
   */
  public boolean isJoker() {
    return number == 0;
  }


  public static void main(String[] args) {
    Tile[] deck = new Tile[]{
        null,//0
        null,//1
        new Tile(0, Color.BLUE), //3
        new Tile(9, Color.BLUE), //4
        new Tile(10, Color.BLUE), //5
        new Tile(11, Color.BLUE), //6
        new Tile(0, Color.BLUE), //6
        new Tile(12, Color.BLUE), //7
        null,//6
        new Tile(10, Color.YELLOW),//7
        new Tile(10, Color.BLACK),//7
        new Tile(10, Color.BLUE),//8
        new Tile(0, Color.BLUE),//9
        null,//10
        null,//23
        new Tile(13, Color.YELLOW),//11
        new Tile(13, Color.BLACK),//12
        new Tile(13, Color.RED),//13
        null,//14
        null,//15
        new Tile(4, Color.RED),//16
        new Tile(5, Color.RED),//17
        new Tile(6, Color.RED),//18
        null,//20
        null,// 21
        null,// 22


    };
    isWinningDeck(deck);
    System.out.println(isWinningDeck(deck));
    isValidRun(deck,2,8);
    //isValidRun(deck, 2, 7);
    //isValidRun(deck, 19, 22);
    //System.out.println(isValidRun(deck, 2,5));
    //System.out.println(isValidSet(deck,7,11));
    //System.out.println(isValidSet(deck, 13, 16));
    //System.out.println(isValidRun(deck, 18, 22));
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
   * @param deck The array representing the deck. Is assumed to have 14 non-null entries.
   * @return true if and only if the deck is of a winning configuration.
   */
  public static boolean isWinningDeck(Tile[] deck) {
    int formationLength = 0;
    boolean returnValue = true;
    for (int i = 0; i <= deck.length; i++) {
      Tile currentTile = null;
      if (i < deck.length) {
        currentTile = deck[i];
      }
      if (currentTile == null) {
        int from = i - formationLength;
        int to = i;
        String output = Arrays.toString(Arrays.copyOfRange(deck, from, to));
        if (formationLength < 3 && formationLength > 0) {
          System.out.println("Formation of length " + formationLength + " too short");
          System.out.println("Formation: " + output);
          System.out.println("Returning false: Formation length is less than 3.");
          return false;
        }
        if (formationLength == 0) {
          System.out.println("Skipping evaluation due to zero length formation at index " + i);
          continue;
        }
        boolean validRun = isValidRun(deck, from, to);
        boolean validSet = isValidSet(deck, from, to);
        if (validRun) {
          System.out.println("Valid run: " + output);
        }
        if (validSet) {
          System.out.println("Valid set: " + output);
        }
        if (!validSet && !validRun) {
          System.out.println("Invalid section: " + output);
          System.out.println("Returning false: Neither a valid set nor a valid run.");
          return false;
        }
        formationLength = 0;

        continue;
      }
      if (i == 12 && currentTile != null) {
        System.out.println("Special case for index 12. Restarting formation length due to non-null tile at index 12.");
        formationLength = 0;
      }
      formationLength++;
    }
    System.out.println("Deck evaluation completed without errors. Deck is winning.");
    return returnValue;
  }


  /**
   * Checks whether a range in the deck is a valid set according to the rules of the game.
   *
   * @param deck an Array of tiles representing the deck
   * @param from the start of the range (inclusive)
   * @param to the end of the range (exclusive)
   * @return true if and only if the range in the deck is a valid set
   */
  private static boolean isValidSet(Tile[] deck, int from, int to) {
    if (to - from > 4) {
      return false;
    }
    int mask = 0;
    int number = deck[from].getNumber();
    for (int i = from; i < to; i++) {
      if (number == 0){
        number = deck[i].getNumber();
      }
      Tile currentTile = deck[i];
      if (currentTile == null) {
        // should never be reached
        LOGGER.error("Set in deck contains null");
        return false;
      }
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
   * @param deck an Array of tiles representing the deck
   * @param from the start of the range (inclusive)
   * @param to the end of the range (exclusive)
   * @return true if and only if the range in the deck is a valid run
   */
  private static boolean isValidRun(Tile[] deck, int from, int to) {
    Tile firstTile = deck[from];
    Color color = firstTile.getColor();
    int startNum = firstTile.getNumber();
    int jokerCountBeforeFirstNumber = 0;
    System.out.println("from = " + from);
    for (int i = from; i < to; i++) {
      Tile currentTile = deck[i];
      if(startNum == 0){
        startNum = currentTile.getNumber();
        //TODO Joker als erstes, was passiert dann?
      }

      if (currentTile.isJoker()) {
        if(startNum == 0){
          jokerCountBeforeFirstNumber++;
        }
        continue;
      }
      System.out.println("jokercount = " + jokerCountBeforeFirstNumber);
      if (currentTile == null) {
        // this line should never be reached
        LOGGER.error("Range in deck contains null..");
        return false;
      }

      if (currentTile.getColor() != color) {
        return false;
      }
      int compare = i - from + startNum- jokerCountBeforeFirstNumber;
      System.out.println("compare = " + compare);
      if (currentTile.getNumber() != compare) { //TODO
        System.out.println();
        return false;
      }
    }
    jokerCountBeforeFirstNumber = 0;
    return true;
  }

  public String toStringPretty() {

    if (this.isJoker()) {
      return "\u001B[5mJT\u001B[25m";
    }
    return
        // set color
        color.toAnsiColor() +
        // pad with spaces
        String.format("%2d", number) +
        Color.ansiReset();
  }

  @Override
  public String toString() {
    return  number + ":"+ color.toString();
  }
}