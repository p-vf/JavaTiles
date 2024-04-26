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

class ClientThreadTest {
  GameState gameState;

  Lobby lobby;
  ClientThread clientThread;

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


  @Test
  void isMainStackShouldReturnTrueForMainStack() {
    String args = "m";
    assertTrue(clientThread.isMainStack(args));
  }

  @Test
  void isMainStackShouldThrowExceptionForFalseStack() {
    String args = "s";
    assertThrows(IllegalArgumentException.class, () -> {clientThread.isMainStack(args);});
  }
  @Test
  void isMainStackShouldThrowExceptionForNoStackSpecified() {
    String args = "";
    assertThrows(IllegalArgumentException.class, () -> {clientThread.isMainStack(args);});
  }
  @Test
  void isMainStackShouldReturnFalseForExchangeStack() {
   String args = "e";
    assertFalse(clientThread.isMainStack(args));
  }

   @Test
  void notAllowedToDrawShouldReturnTrueBecauseItIsNotThePlayersTurn() throws IOException, NoSuchFieldException, IllegalAccessException {
     Field playerIndex = ClientThread.class.getDeclaredField("playerIndex");
     playerIndex.setAccessible(true);
     playerIndex.set(clientThread, 0);

     when(clientThread.notAllowedToDraw()).thenCallRealMethod();
     assertTrue(clientThread.notAllowedToDraw());

  }
  @Test
  void notAllowedToDrawShouldReturnTrueBecausePlayerHasAlreadyDrawn() throws IOException, IllegalAccessException, NoSuchFieldException {

    Field playerIndex = ClientThread.class.getDeclaredField("playerIndex");
    playerIndex.setAccessible(true);
    playerIndex.set(clientThread, 1);
    OrderedDeck tiles= new OrderedDeck();
    Tile[][] deck = new Tile[2][12];

    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 8; j++) {
        deck[i][j] = new Tile(0, Color.BLUE);
      }
    }
    tiles.setDeck(deck);
    Field playerDecksField = GameState.class.getDeclaredField("playerDecks");
    playerDecksField.setAccessible(true);
    ArrayList<OrderedDeck> playerDecks = (ArrayList<OrderedDeck>) playerDecksField.get(this.gameState);

    playerDecks.set(1, tiles);

    when(clientThread.notAllowedToDraw()).thenCallRealMethod();
    assertTrue(clientThread.notAllowedToDraw());
  }

  @Test
  void notAllowedToDrawShouldReturnFalseBecausePlayerCanDraw() throws IOException, IllegalAccessException, NoSuchFieldException {

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

  @Test
  void drawShouldEndGameWithNoWinnerWhenNoTileCanBeDrawn() throws IOException, NoSuchFieldException, IllegalAccessException {

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
  @Test
  void drawShouldSendMessageForRegularDrawAction() throws IOException, NoSuchFieldException, IllegalAccessException {

    //TODO: use PipedInputstream to read outcontent
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
  @Test
  void cantPutTileShouldReturnTrueIfItIsNotThePlayersTurn() throws NoSuchFieldException, IllegalAccessException, IOException {
    Field playerIndex = ClientThread.class.getDeclaredField("playerIndex");
    playerIndex.setAccessible(true);
    playerIndex.set(clientThread, 0);

    when(clientThread.cantPutTile()).thenCallRealMethod();
    assertTrue(clientThread.cantPutTile());

  }

  @Test
  void cantPutTileShouldReturnTrueBecausePlayerHasntDrawnYet() throws NoSuchFieldException, IllegalAccessException, IOException {
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

  @Test
  void cantPutTileShouldReturnFalseBecauseItsYourTurnAndYouHaveAlreadyDrawn() throws NoSuchFieldException, IllegalAccessException, IOException {
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

  @Test
  void checkIfValidShouldReturnFalseForInvalidMoveBecauseTileToPuttIsNull() throws IOException, IllegalAccessException, NoSuchFieldException {
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


  @Test
  void checkIfValidShouldReturnFalseForInvalidMoveBecauseDeckHasBeenModified() throws IOException, IllegalAccessException, NoSuchFieldException {
    Tile tile = new Tile(3,Color.BLUE);
    Tile[] modifiedTiles = new Tile[24];

    for (int i = 0; i < 14; i++) {
      modifiedTiles[i] = new Tile(0, Color.BLUE);
    }
    OrderedDeck modifiedDeck = new OrderedDeck(modifiedTiles);

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
    assertFalse(clientThread.checkIfValid(tile, modifiedDeck));
    verify(clientThread, times(1)).checkIfValid(tile, modifiedDeck);
  }


  @Test
  void checkIfValidShouldReturnTrueBecauseDeckHasNotBeenModified() throws IOException, IllegalAccessException, NoSuchFieldException {

    Tile tile = new Tile(3,Color.BLUE);
    Tile[] modifiedTiles = new Tile[24];

    for (int i = 0; i < 14; i++) {
      modifiedTiles[i] = new Tile(0, Color.BLUE);
    }
    OrderedDeck modifiedDeck = new OrderedDeck(modifiedTiles);

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
    assertTrue(clientThread.checkIfValid(tile, modifiedDeck));
    verify(clientThread, times(1)).checkIfValid(tile, modifiedDeck);
  }

  @Test
  void checkIfWonShouldReturnFalseBecauseNoWinningConfigurationAchieved() throws IOException, IllegalAccessException, NoSuchFieldException {

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


  @Test
  void checkIfWonShouldReturnTrueForWinningDeck() throws IOException, IllegalAccessException, NoSuchFieldException {

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
//TODO: GameState.putt
}