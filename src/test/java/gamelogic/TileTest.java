package gamelogic;

import game.Color;
import game.OrderedDeck;
import game.Tile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TileTest {
/*
  @Test
  void shuffleTiles() {
  }
*/


  @Test
  void isValidSetShouldReturnFalseForTooLargeSet() {
    Tile[] deckRow = new Tile[]{
        new Tile(9, Color.BLUE),
        new Tile(9, Color.RED),
        new Tile(9, Color.YELLOW),
        new Tile(9, Color.BLACK),
        new Tile(0, Color.BLACK),
    };
    assertFalse(OrderedDeck.isValidSet(deckRow, 0, 5));
  }
  @Test
  void isValidSetShouldReturnFalseForColorYellowTwice(){
    Tile[] deckRow = new Tile[]{
        new Tile(9, Color.BLUE),
        new Tile(9, Color.RED),
        new Tile(9, Color.YELLOW),
        new Tile(9, Color.YELLOW),
    };
    assertFalse(OrderedDeck.isValidSet(deckRow, 0, 4));
  }

  @Test
  void isValidSetShouldReturnFalseForColorRedTwice(){
    Tile[] deckRow = new Tile[]{
        new Tile(9, Color.BLUE),
        new Tile(9, Color.RED),
        new Tile(9, Color.RED),
        new Tile(9, Color.YELLOW),
    };
    assertFalse(OrderedDeck.isValidSet(deckRow, 0, 4));
  }

  @Test
  void isValidSetShouldReturnFalseForColorBlackTwice(){
    Tile[] deckRow = new Tile[]{
        new Tile(9, Color.BLUE),
        new Tile(9, Color.BLACK),
        new Tile(9, Color.BLACK),
        new Tile(9, Color.YELLOW),
    };
    assertFalse(OrderedDeck.isValidSet(deckRow, 0, 4));
  }

  @Test
  void isValidSetShouldReturnFalseForColorBlueTwice(){
    Tile[] deckRow = new Tile[]{
        new Tile(9, Color.BLUE),
        new Tile(9, Color.BLUE),
        new Tile(9, Color.YELLOW),
    };
    assertFalse(OrderedDeck.isValidSet(deckRow, 0, 3));
  }

  @Test
  void isValidSetShouldReturnFalseForDifferentNumber() {
    Tile[] deckRow = new Tile[]{
        new Tile(9, Color.BLUE),
        new Tile(9, Color.RED),
        new Tile(9, Color.YELLOW),
        new Tile(10, Color.BLACK),
    };
    assertFalse(OrderedDeck.isValidSet(deckRow, 0, 4));
  }

  @Test
  void isValidSetShouldReturnTrueWithJoker() {
    Tile[] deckRow = new Tile[]{
        new Tile(0, Color.BLUE),
        new Tile(9, Color.RED),
        new Tile(9, Color.YELLOW),
        new Tile(0, Color.BLACK),
    };
    assertTrue(OrderedDeck.isValidSet(deckRow, 0, 4));
  }
  @Test
  void isValidSetShouldReturnTrueWithColorDuplicateDueToJoker() {
    Tile[] deckRow = new Tile[]{
        new Tile(0, Color.BLUE),
        new Tile(9, Color.RED),
        new Tile(9, Color.YELLOW),
        new Tile(0, Color.YELLOW),
    };
    assertTrue(OrderedDeck.isValidSet(deckRow, 0, 3));
  }

  @Test
  void isValidSetShouldReturnTrueForSmallestSetPossible() {//Ist das nötig?
    Tile[] deckRow = new Tile[]{
        new Tile(9, Color.BLUE),
        new Tile(9, Color.RED),
        new Tile(9, Color.YELLOW),
    };
    assertTrue(OrderedDeck.isValidSet(deckRow, 0, 3));
  }

  /*
  @Test
  void isJoker() {
  }
*/

  @Test
  void isValidRunShouldReturnFalseForWrongJokerUse( ){
    Tile[] deckRow = new Tile[]{
        new Tile(8, Color.RED),
        new Tile(9, Color.RED),
        new Tile(0, Color.YELLOW),
        new Tile(10, Color.RED),
    };
    assertFalse(OrderedDeck.isValidRun(deckRow, 0, 4));
  }

  @Test
  void isValidRunShouldReturnFalseForWrongColor(){
    Tile[] deckRow = new Tile[]{
        new Tile(8, Color.RED),
        new Tile(9, Color.RED),
        new Tile(10, Color.YELLOW),
        new Tile(11, Color.RED),
    };
    assertFalse(OrderedDeck.isValidRun(deckRow, 0, 4));
  }

  @Test
  void isValidRunShouldReturnFalseForWrongNumber(){
    Tile[] deckRow = new Tile[]{
        new Tile(8, Color.RED),
        new Tile(9, Color.RED),
        new Tile(10, Color.RED),
        new Tile(12, Color.RED),
    };
    assertFalse(OrderedDeck.isValidRun(deckRow, 0, 4));
  }

  @Test
  void isValidRunShouldReturnFalseForRunWithJokerAfterTileNumber13() {
    //TODO: False, wenn der Joker gesetzt wird nach der 13
    Tile[] deckRow = new Tile[]{
        new Tile(11, Color.RED),
        new Tile(12, Color.RED),
        new Tile(13, Color.RED),
        new Tile(0,Color.YELLOW),
    };
    assertFalse(OrderedDeck.isValidRun(deckRow, 0, 4));
  }


  @Test
  void isValidRunShouldReturnTrueForRunWithJoker() {
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

  @Test
  void isWinningDeckShouldReturnFalseForTooShortGroups() {
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
  @Test
  void isWinningDeckShouldReturnFalseForBuildingRunFromIndexTenToIndexTwelve() {
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

  @Test
  void isWinningDeckShouldReturnFalseForBuildingSetFromIndexTenToIndexTwelve() {
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

  @Test
  void isWinningDeckShouldReturnFalseForNoNullTileInBetween() {
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

  @Test
  void isWinningDeckShouldReturnTrueForRegularWinningDeck() {
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


  @Test
  void isWinningDeckForCheatCodeShouldReturnTrue(){


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

