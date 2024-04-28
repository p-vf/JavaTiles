package game;


import java.util.ArrayList;
import java.util.Random;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

import static utils.NetworkUtils.encodeProtocolMessage;

/**
 * Represents a tile in the game.
 * If the number is 0, the tile represents a joker and the color is irrelevant,
 * else it is a normal tile with a number and a color.
 *
 * @author Pascal von Fellenberg
 * @author Boran Gökcen
 */
public class Tile {
  //private static final Logger LOGGER = LogManager.getLogger(Tile.class);

  private final int number;
  private final Color color;

  /**
   * Constructs a Tile with the specified number and color.
   *
   * @param number number of the tile (must be between 0 and 13)
   * @param color  color of the tile
   */
  public Tile(int number, Color color) {
    if (number > 13 || number < 0) {
      throw new IllegalArgumentException("Tile has number outside the required range.");
    }
    this.number = number;
    this.color = color;
  }

  /**
   * Returns the number of the tile
   *
   * @return number of the tile
   */
  public int getNumber() {
    return number;
  }

  /**
   * Returns the color of the tile.
   *
   * @return color of the tile
   */
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

  /**
   * Returns an array of tiles given a list of strings that each represent a tile.
   * In the strings in {@param stringTileList} must be of the format that {@link Tile#parseTile(String tileString)} requires {@code tileString} to be.
   *
   * @param stringTileList list of strings that represent tiles
   * @return array of tiles as represented in the input array
   * @throws IllegalArgumentException if at least one of the strings in {@param stringTileList} isn't of the correct format
   */
  public static Tile[] stringsToTileArray(ArrayList<String> stringTileList) throws IllegalArgumentException {
    Tile[] tileArray = new Tile[stringTileList.size()];
    String tileString;
    for (int i = 0; i < stringTileList.size(); i++){
      tileString = stringTileList.get(i);
      Tile tile = parseTile(tileString);
      tileArray[i] = tile;
    }
    return tileArray;
  }

  /**
   * Converts an array of tiles into a string that represents a list of strings as specified by the network protocol.
   * This example shows what the output looks like;
   * <pre>
   *   String result = tileArrayToProtocolArgument(new Tile[] {new Tile(1, Color.RED), null, new Tile(2, Color.BLUE)});
   * </pre>The variable{@code result} has the following value after the code snippet is executed: {@code "1:RED \"%\" 2:BLUE"}
   *
   * @param tileArray array of tiles to be converted to a protocol argument
   * @return          string that can be sent as a single argument representing the specified array of tiles
   */
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
   * Shuffles the given array of tiles with a Fisher-Yates shuffle.
   *
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
   * Returns whether the tile is a joker.
   *
   * @return {@code true} if the tile is a joker, {@code false} otherwise
   */
  public boolean isJoker() {
    return number == 0;
  }


  /**
   * For testing the Tile class, never gets called in normal operation.
   * @param args not used
   */
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
    OrderedDeck oDeck = new OrderedDeck(deck);
    System.out.println(oDeck.isWinningDeck());
  }

  /**
   * Converts the tile into a pretty string, colored with ansi escape codes.
   * The resulting string is always of length 2.
   *
   * @return string containing a number that is colored using ansi escape codes or {@code "JT"} if the tile is a joker
   */
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
  /**
   * Returns a string representation of the tile.
   * <p>
   * The string representation includes the tile's number and color separated by a colon.
   * </p>
   *
   * @return a string representation of the tile
   */
  @Override
  public String toString() {
    return  number + ":"+ color.toString();
  }

  /**
   * Indicates whether some other object is "equal to" this tile.
   * <p>
   * This method compares the color and number of the tile with another object.
   * </p>
   *
   * @param other the object to compare with
   * @return {@code true} if this tile is the same as the {@code other} object; {@code false} otherwise
   */
  @Override
  public boolean equals(Object other) {
    if (other instanceof Tile t) {
      return t.color == color && t.number == number;
    }
    return false;
  }
}