package game;


import java.util.ArrayList;
import java.util.Arrays;
import utils.NetworkUtils;

import static utils.NetworkUtils.encodeProtocolMessage;

/**
 * Represents a tile in the game.
 * If the number is 0, the tile represents a joker and the color is irrelevant,
 * else it is a normal tile with a number and a color.
 */
public class Tile {

  public int number;
  public Color color;
  public Tile(int number, Color color) {
    this.number = number;
    this.color = color;
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
      tileStringList.add(tile.toString());
    }
    return encodeProtocolMessage(tileStringList);
  }


  /**
   * Shows if the tile is a joker.
   * @return true iff the tile is a joker.
   */
  public boolean isJoker() {
    return number == 0;
  }

  @Override
  public String toString() {
    return  number + ":"+ color.toString();
  }}