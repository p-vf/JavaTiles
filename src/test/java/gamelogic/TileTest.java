package gamelogic;

import game.Color;
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
    Tile[] deck = new Tile[]{
        new Tile(9, Color.BLUE),
        new Tile(9, Color.RED),
        new Tile(9, Color.YELLOW),
        new Tile(9, Color.BLACK),
        new Tile(0, Color.BLACK),
    };
    assertFalse(Tile.isValidSet(deck, 0, 5));
  }
  @Test
  void isValidSetShouldReturnFalseForColorYellowTwice(){
    Tile[] deck = new Tile[]{
        new Tile(9, Color.BLUE),
        new Tile(9, Color.RED),
        new Tile(9, Color.YELLOW),
        new Tile(9, Color.YELLOW),
    };
    assertFalse(Tile.isValidSet(deck, 0, 4));
  }

  @Test
  void isValidSetShouldReturnFalseForColorRedTwice(){
    Tile[] deck = new Tile[]{
        new Tile(9, Color.BLUE),
        new Tile(9, Color.RED),
        new Tile(9, Color.RED),
        new Tile(9, Color.YELLOW),
    };
    assertFalse(Tile.isValidSet(deck, 0, 4));
  }

  @Test
  void isValidSetShouldReturnFalseForColorBlackTwice(){
    Tile[] deck = new Tile[]{
        new Tile(9, Color.BLUE),
        new Tile(9, Color.BLACK),
        new Tile(9, Color.BLACK),
        new Tile(9, Color.YELLOW),
    };
    assertFalse(Tile.isValidSet(deck, 0, 4));
  }

  @Test
  void isValidSetShouldReturnFalseForColorBlueTwice(){
    Tile[] deck = new Tile[]{
        new Tile(9, Color.BLUE),
        new Tile(9, Color.BLUE),
        new Tile(9, Color.YELLOW),
    };
    assertFalse(Tile.isValidSet(deck, 0, 3));
  }

  @Test
  void isValidSetShouldReturnFalseForDifferentNumber() {
    Tile[] deck = new Tile[]{
        new Tile(9, Color.BLUE),
        new Tile(9, Color.RED),
        new Tile(9, Color.YELLOW),
        new Tile(10, Color.BLACK),
    };
    assertFalse(Tile.isValidSet(deck, 0, 4));
  }

  @Test
  void isValidSetShouldReturnTrueWithJoker() {
    Tile[] deck = new Tile[]{
        new Tile(0, Color.BLUE),
        new Tile(9, Color.RED),
        new Tile(9, Color.YELLOW),
        new Tile(0, Color.BLACK),
    };
    assertTrue(Tile.isValidSet(deck, 0, 4));
  }
  @Test
  void isValidSetShouldReturnTrueWithColorDuplicateDueToJoker() {
    Tile[] deck = new Tile[]{
        new Tile(0, Color.BLUE),
        new Tile(9, Color.RED),
        new Tile(9, Color.YELLOW),
        new Tile(0, Color.YELLOW),
    };
    assertTrue(Tile.isValidSet(deck, 0, 3));
  }

  @Test
  void isValidSetShouldReturnTrueForSmallestSetPossible() {//Ist das nötig?
    Tile[] deck = new Tile[]{
        new Tile(9, Color.BLUE),
        new Tile(9, Color.RED),
        new Tile(9, Color.YELLOW),
    };
    assertTrue(Tile.isValidSet(deck, 0, 3));
  }

  /*
  @Test
  void isJoker() {
  }
*/

  @Test
  void isValidRunShouldReturnFalseForWrongJokerUse( ){
    Tile[] deck = new Tile[]{
        new Tile(8, Color.RED),
        new Tile(9, Color.RED),
        new Tile(0, Color.YELLOW),
        new Tile(10, Color.RED),
    };
    assertFalse(Tile.isValidRun(deck, 0, 4));
  }

  @Test
  void isValidRunShouldReturnFalseForWrongColor(){
    Tile[] deck = new Tile[]{
        new Tile(8, Color.RED),
        new Tile(9, Color.RED),
        new Tile(10, Color.YELLOW),
        new Tile(11, Color.RED),
    };
    assertFalse(Tile.isValidRun(deck, 0, 4));
  }

  @Test
  void isValidRunShouldReturnFalseForWrongNumber(){
    Tile[] deck = new Tile[]{
        new Tile(8, Color.RED),
        new Tile(9, Color.RED),
        new Tile(10, Color.RED),
        new Tile(12, Color.RED),
    };
    assertFalse(Tile.isValidRun(deck, 0, 4));
  }

  @Test
  void isValidRunShouldReturnFalseForRunWithJokerAfterTileNumber13() {
    //TODO: False, wenn der Joker gesetzt wird nach der 13
    Tile[] deck = new Tile[]{
        new Tile(11, Color.RED),
        new Tile(12, Color.RED),
        new Tile(13, Color.RED),
        new Tile(0,Color.YELLOW),
    };
    assertFalse(Tile.isValidRun(deck, 0, 4));
  }


  @Test
  void isValidRunShouldReturnTrueForRunWithJoker() {
    Tile[] deck = new Tile[]{
        new Tile(0, Color.RED),
        new Tile(9, Color.RED),
        new Tile(0, Color.RED),
        new Tile(11, Color.RED),
        new Tile(12, Color.RED),
        new Tile(13, Color.RED),
    };
    assertTrue(Tile.isValidRun(deck, 0, 6));
  }

  @Test
  void isWinningDeckShouldReturnFalseForTooShortGroups() {
    Tile[] deck = new Tile[]{
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
    assertFalse(Tile.isWinningDeck(deck));
  }
  @Test
  void isWinningDeckShouldReturnFalseForBuildingRunFromIndexTenToIndexTwelve() {
    Tile[] deck = new Tile[]{
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
    assertFalse(Tile.isWinningDeck(deck));
  }

  @Test
  void isWinningDeckShouldReturnFalseForBuildingSetFromIndexTenToIndexTwelve() {
    Tile[] deck = new Tile[]{
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
    assertFalse(Tile.isWinningDeck(deck));
  }

  @Test
  void isWinningDeckShouldReturnFalseForNoNullTileInBetween() {
    Tile[] deck = new Tile[]{
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
    assertFalse(Tile.isWinningDeck(deck));
  }

  @Test
  void isWinningDeckShouldReturnTrueForRegularWinningDeck() {
    Tile[] deck = new Tile[]{
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
    assertTrue(Tile.isWinningDeck(deck));
  }


  @Test
  void isWinningDeckForCheatCodeShouldReturnTrue(){


    Tile[] deck = new Tile[]{
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
    assertTrue(Tile.isWinningDeck(deck));
  }



}

