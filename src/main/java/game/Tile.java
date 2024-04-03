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

  int number;
  Color color;
  public Tile(int number, Color color) {
    this.number = number;
    this.color = color;
  }

  public static String[] parseTile(String tileString){
    StringBuilder tileNumber = new StringBuilder();
    StringBuilder tileColor = new StringBuilder();
    int j;
    int k;

    for(j = 0; tileString.charAt(j)!=':'; j++){
      tileNumber.append(tileString.charAt(j));
    }
    for(k = j+1; k<tileString.length(); k++){
      tileColor.append(tileString.charAt(k));

    }

    return new String[]{tileNumber.toString(),tileColor.toString()};

  }

  public static Tile[] stringsToTileArray(ArrayList<String> stringList){
    Tile[] tileArray = new Tile[stringList.size()];
    String tileString;
    for(int i = 0; i<stringList.size(); i++){
      tileString = stringList.get(i);
      String[] tileFieldsArray = parseTile(tileString);
      int tileNumber = Integer.parseInt(tileFieldsArray[0]);
      Color tileColor = Color.valueOf(tileFieldsArray[1]);
      Tile tile = new Tile(tileNumber,tileColor);
      tileArray[i] = tile;

    }
    return tileArray;

  }

  public static Tile stringToTile(String tileString){
    String[] tileFieldsArray = parseTile(tileString);
    int tileNumber = Integer.parseInt(tileFieldsArray[0]);
    Color tileColor = Color.valueOf(tileFieldsArray[1]);
    Tile tile = new Tile(tileNumber,tileColor);

    return tile;
  }


  public static String tileArraytoString(Tile[] tileArray){
    ArrayList<String> tileStringList= new ArrayList<>();
    for(int i = 0; i<tileArray.length; i++){
      tileStringList.add(tileArray[i].toString());
    }
    String tilesAsString = encodeProtocolMessage(tileStringList);

    return tilesAsString;
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