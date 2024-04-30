package gamelogic;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import game.Color;
import game.OrderedDeck;
import game.Tile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import server.ClientThread;
import server.GameState;
import server.Lobby;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Tests for the {@link ClientThread} class.
 */
class ClientThreadTest {
  GameState gameState;

  Lobby lobby;
  ClientThread clientThread;

  /**
   * Sets up the test environment before each test method. Could be that some tests need to modify this environment.
   *
   * @throws NoSuchFieldException   When a specified field cannot be found.
   */
  @BeforeEach
  public void setUp() throws NoSuchFieldException {
    clientThread = mock(ClientThread.class);
    lobby = new Lobby(1);
    gameState = new GameState(0);
    Field lobbyOne = ClientThread.class.getDeclaredField("lobby");
    lobbyOne.setAccessible(true);
    Field stateOfGame = Lobby.class.getDeclaredField("gameState");
    stateOfGame.setAccessible(true);
    Field currentPlayerIndex = GameState.class.getDeclaredField("currentPlayerIndex");
    currentPlayerIndex.setAccessible(true);

    try {
      lobbyOne.set(clientThread, lobby);
      stateOfGame.set(lobby, this.gameState);
      currentPlayerIndex.set(this.gameState, 1);

    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }


  /**
   * Tests {@link ClientThread#isMainStack(String)}.
   * It checks if the method returns true for the main stack.
   */
  @Test
  void testOfIsMainStackShouldReturnTrueForMainStack() {
    String args = "m";
    assertTrue(clientThread.isMainStack(args));
  }

  /**
   * Tests {@link ClientThread#isMainStack(String)}.
   * It verifies that the method throws an IllegalArgumentException when an invalid stack identifier is provided.
   */
  @Test
  void testOfIsMainStackShouldThrowExceptionForFalseStack() {
    String args = "s";
    assertThrows(IllegalArgumentException.class, () -> {clientThread.isMainStack(args);});
  }
  /**
   * Tests {@link ClientThread#isMainStack(String)}.
   * It verifies that the method throws an IllegalArgumentException when no stack identifier is provided.
   */
  @Test
  void testOfIsMainStackShouldThrowExceptionForNoStackSpecified() {
    String args = "";
    assertThrows(IllegalArgumentException.class, () -> {clientThread.isMainStack(args);});
  }
  /**
   * Tests {@link ClientThread#isMainStack(String)}.
   * It checks if the method returns false for the exchange stack.
   */
  @Test
  void testOfIsMainStackShouldReturnFalseForExchangeStack() {
   String args = "e";
    assertFalse(clientThread.isMainStack(args));
  }

  /**
   * Tests {@link ClientThread#notAllowedToDraw()}.
   * It verifies that the method returns true because it's not the player's turn.
   *
   * @throws IOException            if an I/O error occurs.
   * @throws NoSuchFieldException   if a specified field cannot be found.
   * @throws IllegalAccessException if the current Java Security Manager denies reflective access to the field.
   */
   @Test
  void testOfNotAllowedToDrawShouldReturnTrueBecauseItIsNotThePlayersTurn() throws IOException, NoSuchFieldException, IllegalAccessException {
     Field playerIndex = ClientThread.class.getDeclaredField("playerIndex");
     playerIndex.setAccessible(true);
     playerIndex.set(clientThread, 0);

     when(clientThread.notAllowedToDraw()).thenCallRealMethod();
     assertTrue(clientThread.notAllowedToDraw());

  }

  /**
   * Tests {@link ClientThread#notAllowedToDraw()}.
   * It verifies that the method returns true because the player has already drawn a tile.
   *
   * @throws IOException            if an I/O error occurs.
   * @throws NoSuchFieldException   if a specified field cannot be found.
   * @throws IllegalAccessException if the current Java Security Manager denies reflective access to the field.
   */
  @Test
  void testOfNotAllowedToDrawShouldReturnTrueBecausePlayerHasAlreadyDrawn() throws IOException, IllegalAccessException, NoSuchFieldException {

    Field playerIndex = ClientThread.class.getDeclaredField("playerIndex");
    playerIndex.setAccessible(true);
    playerIndex.set(clientThread, 1);
    Tile[][] deck = new Tile[2][12];

    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 8; j++) {
        deck[i][j] = new Tile(0, Color.BLUE);
      }
    }
    OrderedDeck tiles= new OrderedDeck(deck);
    Field playerDecksField = GameState.class.getDeclaredField("playerDecks");
    playerDecksField.setAccessible(true);
    ArrayList<OrderedDeck> playerDecks = (ArrayList<OrderedDeck>) playerDecksField.get(this.gameState);

    playerDecks.set(1, tiles);

    when(clientThread.notAllowedToDraw()).thenCallRealMethod();
    assertTrue(clientThread.notAllowedToDraw());
  }

  /**
   * Tests {@link ClientThread#notAllowedToDraw()}.
   * It verifies that the method returns false because the player is allowed to draw a tile.
   *
   * @throws IOException            if an I/O error occurs.
   * @throws IllegalAccessException if the current Java Security Manager denies reflective access to the field.
   * @throws NoSuchFieldException   if a specified field cannot be found.
   */
  @Test
  void testOfNotAllowedToDrawShouldReturnFalseBecausePlayerCanDraw() throws IOException, IllegalAccessException, NoSuchFieldException {

    Field playerIndex = ClientThread.class.getDeclaredField("playerIndex");
    playerIndex.setAccessible(true);
    playerIndex.set(clientThread, 1);
    OrderedDeck tiles= new OrderedDeck();
    Tile[][] deck = new Tile[2][12];

    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 7; j++) {
        deck[i][j] = new Tile(0, Color.BLUE);
      }
    }
    tiles.setDeck(deck);
    Field playerDecksField = GameState.class.getDeclaredField("playerDecks");
    playerDecksField.setAccessible(true);
    ArrayList<OrderedDeck> playerDecks = (ArrayList<OrderedDeck>) playerDecksField.get(this.gameState);

    playerDecks.set(1, tiles);
    when(clientThread.notAllowedToDraw()).thenCallRealMethod();
    assertFalse(clientThread.notAllowedToDraw());
  }

  /**
   * Tests {@link ClientThread#draw(String)}.
   * It verifies that the game ends with no winner when no tile can be drawn by verifying the expected output from the OutputStream.
   * It should only occur that no tile can be drawn when the main stack is empty.
   *
   * @throws IOException            if an I/O error occurs.
   * @throws NoSuchFieldException   if a specified field cannot be found.
   * @throws IllegalAccessException if the current Java Security Manager denies reflective access to the field.
   */
  @Test
  void testOfDrawShouldEndGameWithNoWinnerWhenNoTileCanBeDrawn() throws IOException, NoSuchFieldException, IllegalAccessException {

    Stack<Tile> emptyStack = new Stack<>();
    Field mainStack = GameState.class.getDeclaredField("mainStack");
    mainStack.setAccessible(true);
    mainStack.set(this.gameState, emptyStack);

    String args = "m";


    doCallRealMethod().when(clientThread).draw(any(String.class));
    doCallRealMethod().when(clientThread).send(anyString());

    Field outPutStream = ClientThread.class.getDeclaredField("out");
    outPutStream.setAccessible(true);

    PipedInputStream pipedInputStream = new PipedInputStream();
    PipedOutputStream pipedOutputStream = new PipedOutputStream(pipedInputStream);
    BufferedReader reader = new BufferedReader(new InputStreamReader(pipedInputStream));

    Field playerIndexField = ClientThread.class.getDeclaredField("playerIndex");
    playerIndexField.setAccessible(true);
    playerIndexField.set(clientThread, 0);

    outPutStream.set(clientThread, pipedOutputStream);//outContent is being set to out of clientThread
      try {
        clientThread.draw(args);
        pipedOutputStream.write("\n".getBytes());  // Ensure newline character
        pipedOutputStream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }


    verify(clientThread, times(1)).draw(args);

    List<String> lines = new ArrayList<>();
    String line;
    while ((line = reader.readLine()) != null) {
      lines.add(line);
    }

    assertTrue(lines.contains("+DRAW \"%\""));
    assertTrue(lines.contains("EMPT"));
  }

  /**
   *  Tests {@link ClientThread#draw(String)}.
   *  It verifies that the draw method throws an IllegalArgumentException if you try to draw from
   *  an empty exchange stack, which shouldn't be possible due to earlier checks and the game logic itself.
   */
  @Test
  void testOfDrawShouldThrowIllegalArgumentExceptionForEmptyExchangeStack() throws IOException, NoSuchFieldException, IllegalAccessException {
    ArrayList<Stack<Tile>> exchangeStacks = new ArrayList<>(4);
    exchangeStacks.add(new Stack<>());
    //TODO: Der Fall das vom Exchange Stack gezogen wird, obwohl dieser Empty ist sollte eigentlich unmöglich sein.
    Field exchangeStacksField = GameState.class.getDeclaredField("exchangeStacks");
    exchangeStacksField.setAccessible(true);
    exchangeStacksField.set(this.gameState, exchangeStacks);

    doCallRealMethod().when(clientThread).draw(any(String.class));
    Field playerIndexField = ClientThread.class.getDeclaredField("playerIndex");
    playerIndexField.setAccessible(true);
    playerIndexField.set(clientThread, 0);

    assertThrows(IllegalArgumentException.class, () -> {
      clientThread.draw("e");
    });

  }

  /**
   * Tests {@link ClientThread#draw(String)}.
   * It verifies that the method sends a message for a regular draw action.
   *
   * @throws IOException            if an I/O error occurs.
   * @throws NoSuchFieldException   if a specified field cannot be found.
   * @throws IllegalAccessException if the current Java Security Manager denies reflective access to the field.
   */
  @Test
  void testOfDrawShouldSendMessageForRegularDrawAction() throws IOException, NoSuchFieldException, IllegalAccessException {

    Stack<Tile> stackWithTiles = new Stack<>();


    stackWithTiles.push(new Tile(1, Color.RED));
    stackWithTiles.push(new Tile(2, Color.BLUE));
    stackWithTiles.push(new Tile(3, Color.YELLOW));

    PipedInputStream pipedInputStream = new PipedInputStream();
    PipedOutputStream pipedOutputStream = new PipedOutputStream(pipedInputStream);
    BufferedReader reader = new BufferedReader(new InputStreamReader(pipedInputStream));

    Field mainStackField = GameState.class.getDeclaredField("mainStack");
    mainStackField.setAccessible(true);


    mainStackField.set(this.gameState, stackWithTiles);
    String args = "m";

    doCallRealMethod().when(clientThread).draw(any(String.class));
    doCallRealMethod().when(clientThread).send(anyString());
    Field outPutStream = ClientThread.class.getDeclaredField("out");
    outPutStream.setAccessible(true);

    outPutStream.set(clientThread, pipedOutputStream);//outContent is being set to out of clientThread

        clientThread.draw(args);
        pipedOutputStream.write("\n".getBytes());  // Ensure newline character
        pipedOutputStream.close();

    verify(clientThread, times(1)).draw(args);
    String output = reader.readLine();
    assertEquals("+DRAW 3:YELLOW", output);
  }

  /**
   * Tests {@link ClientThread#cantPutTile()}.
   * It verifies that the method returns true if it's not the player's turn.
   *
   * @throws NoSuchFieldException   if a specified field cannot be found.
   * @throws IllegalAccessException if the current Java Security Manager denies reflective access to the field.
   * @throws IOException            if an I/O error occurs.
   */
  @Test
  void testOfCantPutTileShouldReturnTrueIfItIsNotThePlayersTurn() throws NoSuchFieldException, IllegalAccessException, IOException {
    Field playerIndex = ClientThread.class.getDeclaredField("playerIndex");
    playerIndex.setAccessible(true);
    playerIndex.set(clientThread, 0);

    when(clientThread.cantPutTile()).thenCallRealMethod();
    assertTrue(clientThread.cantPutTile());

  }

  /**
   * Tests {@link ClientThread#cantPutTile()}.
   * It verifies that the method returns true because the player has not yet drawn a tile.
   *
   * @throws NoSuchFieldException   if a specified field cannot be found.
   * @throws IllegalAccessException if the current Java Security Manager denies reflective access to the field.
   * @throws IOException            if an I/O error occurs.
   */
  @Test
  void testOfCantPutTileShouldReturnTrueBecausePlayerHasNotDrawnYet() throws NoSuchFieldException, IllegalAccessException, IOException {
    Field playerIndex = ClientThread.class.getDeclaredField("playerIndex");
    playerIndex.setAccessible(true);
    playerIndex.set(clientThread, 1);
    OrderedDeck tiles= new OrderedDeck();
    Tile[][] deck = new Tile[2][12];

    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 7; j++) {
        deck[i][j] = new Tile(0, Color.BLUE);
      }
    }
    tiles.setDeck(deck);
    Field playerDecksField = GameState.class.getDeclaredField("playerDecks");
    playerDecksField.setAccessible(true);
    ArrayList<OrderedDeck> playerDecks = (ArrayList<OrderedDeck>) playerDecksField.get(this.gameState);

    playerDecks.set(1, tiles);

    when(clientThread.cantPutTile()).thenCallRealMethod();
    assertTrue(clientThread.cantPutTile());

  }

  /**
   * Tests {@link ClientThread#cantPutTile()}.
   * It verifies that the method returns false because it's the player's turn and the player has already drawn a tile.
   *
   * @throws NoSuchFieldException   if a specified field cannot be found.
   * @throws IllegalAccessException if the current Java Security Manager denies reflective access to the field.
   * @throws IOException            if an I/O error occurs.
   */
  @Test
  void testOfCantPutTileShouldReturnFalseBecauseItsYourTurnAndYouHaveAlreadyDrawn() throws NoSuchFieldException, IllegalAccessException, IOException {
    Field playerIndex = ClientThread.class.getDeclaredField("playerIndex");
    playerIndex.setAccessible(true);
    playerIndex.set(clientThread, 1);
    OrderedDeck tiles= new OrderedDeck();
    Tile[][] deck = new Tile[2][12];

    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 7; j++) {
        deck[i][j] = new Tile(0, Color.BLUE);
      }
    }
    deck[0][7] = new Tile(0, Color.BLACK);
    tiles.setDeck(deck);
    Field playerDecksField = GameState.class.getDeclaredField("playerDecks");
    playerDecksField.setAccessible(true);
    ArrayList<OrderedDeck> playerDecks = (ArrayList<OrderedDeck>) playerDecksField.get(this.gameState);

    playerDecks.set(1, tiles);

    when(clientThread.cantPutTile()).thenCallRealMethod();
    assertFalse(clientThread.cantPutTile());
  }

  /**
   * Tests {@link ClientThread#checkIfValid(Tile, OrderedDeck)}.
   * It verifies that the method returns false for an invalid move because the tile to put is null.
   *
   * @throws IOException            if an I/O error occurs.
   * @throws IllegalAccessException if the current Java Security Manager denies reflective access to the field.
   * @throws NoSuchFieldException   if a specified field cannot be found.
   */
  @Test
  void testOfCheckIfValidShouldReturnFalseForInvalidMoveBecauseTileToPuttIsNull() throws IOException, IllegalAccessException, NoSuchFieldException {
    //TODO: why does this testcase work wrong?
    Tile tile = null;
    Tile[] tiles = new Tile[24];

    for (int i = 0; i < 14; i++) {

        tiles[i] = new Tile(0, Color.BLUE);

    }

    Field playerIndex = ClientThread.class.getDeclaredField("playerIndex");
    playerIndex.setAccessible(true);
    playerIndex.set(clientThread, 0);

    OrderedDeck deck = new OrderedDeck(tiles);

    when(clientThread.checkIfValid(any(Tile.class), any(OrderedDeck.class))).thenCallRealMethod();
    assertFalse(clientThread.checkIfValid(tile, deck));
    verify(clientThread, times(1)).checkIfValid(tile, deck);
  }

  /**
   * Tests {@link ClientThread#checkIfValid(Tile, OrderedDeck)}.
   * It verifies that the method returns false for an invalid move because the player does not have the tile he is trying to put.
   *
   * @throws IOException            if an I/O error occurs.
   * @throws IllegalAccessException if the current Java Security Manager denies reflective access to the field.
   * @throws NoSuchFieldException   if a specified field cannot be found.
   */
  @Test
  void testOfCheckIfValidShouldReturnFalseForInvalidMoveBecausePlayerDoesNotHaveTheTileHePuts() throws IOException, IllegalAccessException, NoSuchFieldException {
    Tile tile = new Tile(3,Color.BLUE); // Player shouldn't be able to put this tile because he doesn't have it.
    Tile[] tilesToBeChecked = new Tile[24];

    for (int i = 0; i < 14; i++) {
      tilesToBeChecked[i] = new Tile(0, Color.BLUE);
    }
    OrderedDeck deckToBeChecked = new OrderedDeck(tilesToBeChecked);

    Tile[] tiles = new Tile[24];
    for (int i = 0; i < 14; i++) {
      tiles[i] = new Tile(0, Color.BLUE);
    }
    OrderedDeck deck = new OrderedDeck(tiles);
    ArrayList<OrderedDeck> deckList = new ArrayList<>();
    deckList.add(deck);

    Field actualDeck = GameState.class.getDeclaredField("playerDecks");
    actualDeck.setAccessible(true);
    actualDeck.set(gameState, deckList);

    Field playerIndex = ClientThread.class.getDeclaredField("playerIndex");
    playerIndex.setAccessible(true);
    playerIndex.set(clientThread, 0);

    when(clientThread.checkIfValid(any(Tile.class), any(OrderedDeck.class))).thenCallRealMethod();
    assertFalse(clientThread.checkIfValid(tile, deckToBeChecked));
    verify(clientThread, times(1)).checkIfValid(tile, deckToBeChecked);
  }

  /**
   * Tests {@link ClientThread#checkIfValid(Tile, OrderedDeck)}.
   * It verifies that the method returns true because the player has the tile he is trying to put.
   *
   * @throws IOException            if an I/O error occurs.
   * @throws IllegalAccessException if the current Java Security Manager denies reflective access to the field.
   * @throws NoSuchFieldException   if a specified field cannot be found.
   */
  @Test
  void testOfCheckIfValidShouldReturnTrueBecausePlayerHasTileHePuts() throws IOException, IllegalAccessException, NoSuchFieldException {

    Tile tile = new Tile(3,Color.BLUE);
    Tile[] tilesToBeChecked = new Tile[24];

    for (int i = 0; i < 14; i++) {
      tilesToBeChecked[i] = new Tile(0, Color.BLUE);
    }
    OrderedDeck deckToBeChecked = new OrderedDeck(tilesToBeChecked);

    Tile[] tiles = new Tile[24];
    for (int i = 0; i < 14; i++) {
      tiles[i] = new Tile(0, Color.BLUE);
    }
    tiles[14] = new Tile(3,Color.BLUE);
    OrderedDeck deck = new OrderedDeck(tiles);
    ArrayList<OrderedDeck> deckList = new ArrayList<>();
    deckList.add(deck);

    Field actualDeck = GameState.class.getDeclaredField("playerDecks");
    actualDeck.setAccessible(true);
    actualDeck.set(gameState, deckList);

    Field playerIndex = ClientThread.class.getDeclaredField("playerIndex");
    playerIndex.setAccessible(true);
    playerIndex.set(clientThread, 0);

    when(clientThread.checkIfValid(any(Tile.class), any(OrderedDeck.class))).thenCallRealMethod();
    assertTrue(clientThread.checkIfValid(tile, deckToBeChecked));
    verify(clientThread, times(1)).checkIfValid(tile, deckToBeChecked);
  }

  /**
   * Tests {@link ClientThread#checkIfWon(OrderedDeck)}.
   * It verifies that the method returns false because no winning configuration is achieved.
   *
   * @throws IOException            if an I/O error occurs.
   * @throws IllegalAccessException if the current Java Security Manager denies reflective access to the field.
   * @throws NoSuchFieldException   if a specified field cannot be found.
   */
  @Test
  void testOfCheckIfWonShouldReturnFalseBecauseNoWinningConfigurationAchieved() throws IOException, IllegalAccessException, NoSuchFieldException {

    Tile[] winningTileArray = new Tile[24];

    for (int i = 0; i < 7; i++) {

      winningTileArray[i] = new Tile(i, Color.BLUE);
    }
    winningTileArray[7] = new Tile(3,Color.YELLOW);
    for (int i = 12; i < 18; i++) {

      winningTileArray[i] = new Tile(i - 12, Color.BLACK);
    }
    Field nickname = ClientThread.class.getDeclaredField("nickname");
    nickname.setAccessible(true);
    nickname.set(clientThread, "Pascal");

    when(clientThread.checkIfWon(any(OrderedDeck.class))).thenCallRealMethod();
    OrderedDeck winningDeck = new OrderedDeck(winningTileArray);
    assertFalse(clientThread.checkIfWon(winningDeck));
    verify(clientThread, times(1)).checkIfWon(winningDeck);
  }

  /**
   * Tests {@link ClientThread#checkIfWon(OrderedDeck)}.
   * It verifies that the method returns true for a winning deck.
   *
   * @throws IOException            if an I/O error occurs.
   * @throws IllegalAccessException if the current Java Security Manager denies reflective access to the field.
   * @throws NoSuchFieldException   if a specified field cannot be found.
   */
  @Test
  void testOfCheckIfWonShouldReturnTrueForWinningDeck() throws IOException, IllegalAccessException, NoSuchFieldException {

    Tile[] winningTileArray = new Tile[24];

    for (int i = 0; i < 7; i++) {

      winningTileArray[i] = new Tile(i, Color.BLUE);
    }
    for (int i = 12; i < 19; i++) {

      winningTileArray[i] = new Tile(i - 12, Color.BLACK);
    }
    Field nickname = ClientThread.class.getDeclaredField("nickname");
    nickname.setAccessible(true);
    nickname.set(clientThread, "Pascal");

    when(clientThread.checkIfWon(any(OrderedDeck.class))).thenCallRealMethod();
    OrderedDeck winningDeck = new OrderedDeck(winningTileArray);
    assertTrue(clientThread.checkIfWon(winningDeck));
    verify(clientThread, times(1)).checkIfWon(winningDeck);
  }
}