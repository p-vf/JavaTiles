package game;


/**
 * Represents a tile in the game.
 * If the number is 0, the tile represents a joker and the color is irrelevant,
 * else it is a normal tile with a number and a color.
 */
public class Tile {
  // represents the id of the tile, so that each tile is unique.
  // maybe we can remove this field.
  int id;
  // the number of the tile, from 0 to and including 13;
  // if 0, it represents the joker, else it is a normal tile.
  public int number;
  public Color color;
  public Tile(int id, int number, Color color) {
    this.id = id;
    this.number = number;
    this.color = color;
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
    return String.format("(id: %d, %d, %s)", id, number, color.toString());
  }
}
