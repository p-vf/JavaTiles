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
  private static final Logger LOGGER = LogManager.getLogger(Tile.class);

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
    OrderedDeck oDeck = new OrderedDeck(deck);
    System.out.println(oDeck.isWinningDeck());
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

  @Override
  public boolean equals(Object other) {
    if (other instanceof Tile t) {
      return t.color == color && t.number == number;
    }
    return false;
  }
}