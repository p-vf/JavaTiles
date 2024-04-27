package gamelogic;

import game.Color;
import game.OrderedDeck;
import game.Tile;
import org.junit.jupiter.api.Test;
import server.GameState;
import server.UnorderedDeck;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

public class GameStateTest {
  @Test
void putTileCanNotPutTileBecauseDeckKeptOnServerDoesNotContainTile(){

    GameState gameState = new GameState(0); // Initialisiere GameState
    Tile tileToRemove = new Tile(0, Color.BLUE);// Erstelle ein Tile, das nicht im playerDeck ist
    Tile[] deck = new Tile[24];
    for(int i = 0; i < 14; i++) {
         deck[i] = new Tile(i, Color.BLACK);
    }
    deck[14] = new Tile(5,Color.BLUE);

    OrderedDeck deckForPlayer0 = new OrderedDeck(deck);
    // Sets field playerDecks for player 0.
    gameState.setPlayerDeck(0, deckForPlayer0);


    assertThrows(IllegalArgumentException.class, () -> {
      gameState.putTile(tileToRemove, 0);
    });
  }

  @Test
  void putTileCanPutTileBecauseDeckContainsTile() throws IllegalAccessException, NoSuchFieldException {

    GameState gameState = new GameState(0); // Initialisiere GameState

    Tile tileToRemove = new Tile(0, Color.BLUE);// Erstelle ein Tile, das nicht im playerDeck ist
    Tile[] deck = new Tile[24];
    for(int i = 0; i < 14; i++) {
      deck[i] = new Tile(i, Color.BLACK);
    }
    deck[14] = new Tile(0,Color.BLUE);


    OrderedDeck deckForPlayer0 = new OrderedDeck(deck);

    Field exchangeStacksField = GameState.class.getDeclaredField("exchangeStacks");
    exchangeStacksField.setAccessible(true);




    // Sets field playerDecks for player 0.
    gameState.setPlayerDeck(0, deckForPlayer0);

    gameState.putTile(tileToRemove, 0);
    ArrayList<Stack<Tile>> exchangeStack = (ArrayList<Stack<Tile>>) exchangeStacksField.get(gameState);
    //currentPlayerIndex should be
    assertEquals(1, gameState.getCurrentPlayerIndex());
    assertEquals(tileToRemove, exchangeStack.get(1).get(0));
    deck[14] = null;
    OrderedDeck deckOfPlayer0AfterPut = new OrderedDeck(deck);
    gameState.getPlayerDeck(0);
    assertArrayEquals(deckOfPlayer0AfterPut.getDeck(), gameState.getPlayerDeck(0).getDeck());
  }

}
