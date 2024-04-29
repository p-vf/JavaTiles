package gamelogic;

import game.Color;
import game.OrderedDeck;
import game.Tile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link OrderedDeck} class.
 */
class OrderedDeckTest {

  /**
   * Verifies that {@link OrderedDeck#isValidSet(Tile[], int, int)} returns false
   * for a set that contains too many tiles.
   */
  @Test
  void testOfIsValidSetShouldReturnFalseForTooLargeSet() {
    Tile[] deckRow = new Tile[]{
        new Tile(9, Color.BLUE),
        new Tile(9, Color.RED),
        new Tile(9, Color.YELLOW),
        new Tile(9, Color.BLACK),
        new Tile(0, Color.BLACK),
    };
    assertFalse(OrderedDeck.isValidSet(deckRow, 0, 5));
  }

  /**
   * Verifies that {@link OrderedDeck#isValidSet(Tile[], int, int)} returns false
   * for a set where the color yellow appears twice, since all colors should be distinct.
   */
  @Test
  void testOfIsValidSetShouldReturnFalseForColorYellowTwice(){
    Tile[] deckRow = new Tile[]{
        new Tile(9, Color.BLUE),
        new Tile(9, Color.RED),
        new Tile(9, Color.YELLOW),
        new Tile(9, Color.YELLOW),
    };
    assertFalse(OrderedDeck.isValidSet(deckRow, 0, 4));
  }

  /**
   * Verifies that {@link OrderedDeck#isValidSet(Tile[], int, int)} returns false
   * for a set where the color red appears twice, since all colors should be distinct.
   */
  @Test
  void testOfIsValidSetShouldReturnFalseForColorRedTwice(){
    Tile[] deckRow = new Tile[]{
        new Tile(9, Color.BLUE),
        new Tile(9, Color.RED),
        new Tile(9, Color.RED),
        new Tile(9, Color.YELLOW),
    };
    assertFalse(OrderedDeck.isValidSet(deckRow, 0, 4));
  }

  /**
   * Verifies that {@link OrderedDeck#isValidSet(Tile[], int, int)} returns false
   * for a set where the color black appears twice, since all colors should be distinct.
   */
  @Test
  void testOfIsValidSetShouldReturnFalseForColorBlackTwice(){
    Tile[] deckRow = new Tile[]{
        new Tile(9, Color.BLUE),
        new Tile(9, Color.BLACK),
        new Tile(9, Color.BLACK),
        new Tile(9, Color.YELLOW),
    };
    assertFalse(OrderedDeck.isValidSet(deckRow, 0, 4));
  }

  /**
   * Verifies that {@link OrderedDeck#isValidSet(Tile[], int, int)} returns false
   * for a set where the color blue appears twice, since all colors should be distinct.
   */
  @Test
  void testOfIsValidSetShouldReturnFalseForColorBlueTwice(){
    Tile[] deckRow = new Tile[]{
        new Tile(9, Color.BLUE),
        new Tile(9, Color.BLUE),
        new Tile(9, Color.YELLOW),
    };
    assertFalse(OrderedDeck.isValidSet(deckRow, 0, 3));
  }

  /**
   * Verifies that {@link OrderedDeck#isValidSet(Tile[], int, int)} returns false
   * for a set where not all numbers are the same.
   */
  @Test
  void testOfIsValidSetShouldReturnFalseForDifferentNumber() {
    Tile[] deckRow = new Tile[]{
        new Tile(9, Color.BLUE),
        new Tile(9, Color.RED),
        new Tile(9, Color.YELLOW),
        new Tile(10, Color.BLACK),
    };
    assertFalse(OrderedDeck.isValidSet(deckRow, 0, 4));
  }

  /**
   * Verifies that {@link OrderedDeck#isValidSet(Tile[], int, int)} returns true
   * for a set where the joker is being used.
   */
  @Test
  void testOfIsValidSetShouldReturnTrueWithJokerUse() {
    Tile[] deckRow = new Tile[]{
        new Tile(0, Color.BLUE),
        new Tile(9, Color.RED),
        new Tile(9, Color.YELLOW),
        new Tile(0, Color.BLACK),
    };
    assertTrue(OrderedDeck.isValidSet(deckRow, 0, 4));
  }

  /**
   * Verifies that {@link OrderedDeck#isValidSet(Tile[], int, int)} returns true
   * for a set where the joker is being used twice, once even creating a color duplicate of the color yellow,
   * but due to the game rules it is still a valid set.
   */
  @Test
  void testOfIsValidSetShouldReturnTrueWithColorDuplicateDueToJoker() {
    Tile[] deckRow = new Tile[]{
        new Tile(0, Color.BLUE),
        new Tile(9, Color.RED),
        new Tile(9, Color.YELLOW),
        new Tile(0, Color.YELLOW),
    };
    assertTrue(OrderedDeck.isValidSet(deckRow, 0, 3));
  }

  /**
   * Verifies that {@link OrderedDeck#isValidSet(Tile[], int, int)} returns true
   * for a set with three tiles, which is the smallest set allowed.
   */
  @Test
  void testOfIsValidSetShouldReturnTrueForSmallestSetPossible() {//Ist das nötig?
    Tile[] deckRow = new Tile[]{
        new Tile(9, Color.BLUE),
        new Tile(9, Color.RED),
        new Tile(9, Color.YELLOW),
    };
    assertTrue(OrderedDeck.isValidSet(deckRow, 0, 3));
  }

  /**
   * Verifies that {@link OrderedDeck#isValidRun(Tile[], int, int)} returns false
   * for a run where the joker is used incorrectly. In this case the joker is used incorrectly, because
   * the joker can't be used between the number 9 and 10 in a run since it can't substitute a correct tile because there
   * should be no tile at all between tile number 9 and 10 in a run.
   */
  @Test
  void testOfIsValidRunShouldReturnFalseForWrongJokerUse( ){
    Tile[] deckRow = new Tile[]{
        new Tile(8, Color.RED),
        new Tile(9, Color.RED),
        new Tile(0, Color.YELLOW),
        new Tile(10, Color.RED),
    };
    assertFalse(OrderedDeck.isValidRun(deckRow, 0, 4));
  }


  /**
   * Verifies that {@link OrderedDeck#isValidRun(Tile[], int, int)} returns false
   * for a run where not all the tiles have the same color.
   */
  @Test
  void testOfIsValidRunShouldReturnFalseForWrongColor(){
    Tile[] deckRow = new Tile[]{
        new Tile(8, Color.RED),
        new Tile(9, Color.RED),
        new Tile(10, Color.YELLOW),
        new Tile(11, Color.RED),
    };
    assertFalse(OrderedDeck.isValidRun(deckRow, 0, 4));
  }

  /**
   * Verifies that {@link OrderedDeck#isValidRun(Tile[], int, int)} returns false
   * for a run where tiles don't have consecutive numbers.
   */
  @Test
  void testOfIsValidRunShouldReturnFalseForWrongNumber(){
    Tile[] deckRow = new Tile[]{
        new Tile(8, Color.RED),
        new Tile(9, Color.RED),
        new Tile(10, Color.RED),
        new Tile(12, Color.RED),
    };
    assertFalse(OrderedDeck.isValidRun(deckRow, 0, 4));
  }

  /**
   * Verifies that {@link OrderedDeck#isValidRun(Tile[], int, int)} returns false
   * for a run where the joker is placed after tile number 13, since a run is not defined to be
   * continuous.
   */
  @Test
  void testOfIsValidRunShouldReturnFalseForRunWithJokerAfterTileNumber13() {
    //TODO: False, wenn der Joker gesetzt wird nach der 13
    Tile[] deckRow = new Tile[]{
        new Tile(11, Color.RED),
        new Tile(12, Color.RED),
        new Tile(13, Color.RED),
        new Tile(0,Color.YELLOW),
    };
    assertFalse(OrderedDeck.isValidRun(deckRow, 0, 4));
  }

  /**
   * Verifies that {@link OrderedDeck#isValidRun(Tile[], int, int)} returns true
   * for a run where the joker is used correctly.
   */
  @Test
  void testOfIsValidRunShouldReturnTrueForRunWithJoker() {
    Tile[] deckRow = new Tile[]{
        new Tile(0, Color.RED),
        new Tile(9, Color.RED),
        new Tile(0, Color.RED),
        new Tile(11, Color.RED),
        new Tile(12, Color.RED),
        new Tile(13, Color.RED),
    };
    assertTrue(OrderedDeck.isValidRun(deckRow, 0, 6));
  }

  /**
   * Verifies that {@link OrderedDeck#isWinningDeck()} returns false
   * for a deck with a too short group. Said group is the tile 4, red which is alone.
   */
  @Test
  void testOfIsWinningDeckShouldReturnFalseForTooShortGroups() {
    Tile[] deckArray = new Tile[]{
        null,//1
        new Tile(0, Color.BLUE), //3
        new Tile(9, Color.BLUE), //4
        new Tile(10, Color.BLUE), //5
        new Tile(11, Color.BLUE), //6
        new Tile(12, Color.BLUE), //6
        new Tile(13, Color.BLUE), //7
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
        null,//20
        null,
        null,
        null,
    };
    OrderedDeck d = new OrderedDeck(deckArray);
    assertFalse(d.isWinningDeck());
  }

  /**
   * Verifies that {@link OrderedDeck#isWinningDeck()} returns false
   * for a run from index ten to index 12 because runs and sets have to be in the same row of the
   * OrderedDeck.
   */
  @Test
  void testOfIsWinningDeckShouldReturnFalseForBuildingRunFromIndexTenToIndexTwelve() {
    Tile[] deckArray = new Tile[]{
        null,//1
        new Tile(0, Color.BLUE), //3
        new Tile(9, Color.BLUE), //4
        new Tile(10, Color.BLUE), //5
        new Tile(11, Color.BLUE), //6
        new Tile(12, Color.BLUE), //6
        new Tile(13, Color.BLUE), //7
        null,//6
        null,
        null,
        new Tile(10, Color.BLUE),//8
        new Tile(11, Color.BLUE),//11
        new Tile(12, Color.BLUE),//7
        null,//10
        null,//23
        new Tile(13, Color.YELLOW),//11
        new Tile(13, Color.BLACK),//12
        new Tile(13, Color.RED),//13
        null,//14
        null,//15
        new Tile(4, Color.RED),//16
        null,//20
        null,
        null,
    };
    OrderedDeck d = new OrderedDeck(deckArray);
    assertFalse(d.isWinningDeck());
  }

  /**
   * Verifies that {@link OrderedDeck#isWinningDeck()} returns false
   * for a set from index ten to index 12 because runs and sets have to be in the same row of the
   * OrderedDeck.
   */
  @Test
  void testOfIsWinningDeckShouldReturnFalseForBuildingSetFromIndexTenToIndexTwelve() {
    Tile[] deckArray = new Tile[]{
        null,//1
        new Tile(0, Color.BLUE), //3
        new Tile(9, Color.BLUE), //4
        new Tile(10, Color.BLUE), //5
        new Tile(11, Color.BLUE), //6
        new Tile(12, Color.BLUE), //6
        new Tile(13, Color.BLUE), //7
        null,//6
        null,
        null,
        new Tile(10, Color.BLUE),//8
        new Tile(10, Color.YELLOW),//11
        new Tile(10, Color.BLACK),//7
        null,//10
        null,//23
        new Tile(13, Color.YELLOW),//11
        new Tile(13, Color.BLACK),//12
        new Tile(13, Color.RED),//13
        null,//14
        null,//15
        new Tile(4, Color.RED),//16
        null,//20
        null,
        null,
    };
    OrderedDeck d = new OrderedDeck(deckArray);
    assertFalse(d.isWinningDeck());
  }

  /**
   * Verifies that {@link OrderedDeck#isWinningDeck()} returns false
   * if there is no null tile between two separate groups.
   */
  @Test
  void testOfIsWinningDeckShouldReturnFalseForNoNullTileInBetween() {
    Tile[] deckArray = new Tile[]{
        null,//0
        null,//1
        new Tile(0, Color.BLUE), //3
        new Tile(9, Color.BLUE), //4
        new Tile(10, Color.BLUE), //5
        new Tile(11, Color.BLUE), //6
        new Tile(12, Color.BLUE), //6
        new Tile(13, Color.BLUE), //7
        new Tile(10, Color.YELLOW),//7
        new Tile(10, Color.BLACK),//7
        new Tile(10, Color.BLUE),//8
        new Tile(0, Color.BLUE),//11
        new Tile(11,Color.BLACK),
        new Tile(11,Color.BLUE),
        new Tile(11,Color.RED),
        new Tile(11,Color.YELLOW),
        null,
        null,
        null,//14
        null,//15
        null,
        null,//20
        null,
        null,
    };
    OrderedDeck d = new OrderedDeck(deckArray);
    assertFalse(d.isWinningDeck());
  }

  /**
   * Verifies that {@link OrderedDeck#isWinningDeck()} returns true
   * for a regular winning deck.
   */
  @Test
  void testOfIsWinningDeckShouldReturnTrueForRegularWinningDeck() {
    Tile[] deckArray = new Tile[]{
        null,//1
        new Tile(0, Color.BLUE), //3
        new Tile(9, Color.BLUE), //4
        new Tile(10, Color.BLUE), //5
        new Tile(11, Color.BLUE), //6
        new Tile(12, Color.BLUE), //6
        new Tile(13, Color.BLUE), //7
        null,//6
        new Tile(10, Color.BLUE),//8
        new Tile(0, Color.BLUE),
        new Tile(12, Color.BLUE),
        new Tile(13, Color.BLUE),//11
        null,//23
        new Tile(13, Color.YELLOW),//11
        new Tile(13, Color.BLACK),//12
        new Tile(13, Color.RED),//13
        new Tile(13, Color.BLUE),//16
        null,//15
        null,
        null,//20
        null,
        null,
        null,
        null,
    };
    OrderedDeck d = new OrderedDeck(deckArray);
    assertTrue(d.isWinningDeck());
  }


  /**
   * Verifies that {@link OrderedDeck#isWinningDeck()} returns true
   * for a winning deck created with the implemented cheat code.
   */
  @Test
  void testOfIsWinningDeckForCheatCodeShouldReturnTrue(){


    Tile[] deckArray = new Tile[]{
        new Tile(0, Color.BLACK),
        new Tile(2, Color.BLUE),
        new Tile(3, Color.BLUE),
        null,
        new Tile(1, Color.RED),
        new Tile(2, Color.RED),
        new Tile(3, Color.RED),
        new Tile(4, Color.RED),
        null,
        null,
        null,
        null,//11
        new Tile(1, Color.YELLOW),
        new Tile(2, Color.YELLOW),
        new Tile(3, Color.YELLOW),
        new Tile(4, Color.YELLOW),
        new Tile(5, Color.YELLOW),
        new Tile(6, Color.YELLOW),
        new Tile(7, Color.YELLOW),
        null,
        null,
        null,
        null,
        null,
    };
    OrderedDeck d = new OrderedDeck(deckArray);
    assertTrue(d.isWinningDeck());
  }



}

