package server;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

import game.Color;
import game.OrderedDeck;
import game.Tile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import utils.NetworkUtils;

import static utils.NetworkUtils.*;
import static utils.NetworkUtils.Protocol.ClientRequest;
import static utils.NetworkUtils.Protocol.ServerRequest;


/**
 * This class represents a thread for handling communication with a client by reading and responding to inputs.
 *
 * @author Pascal von Fellenberg
 * @author Istref Uka
 */
public class ClientThread implements Runnable {
  private static final Logger LOGGER = LogManager.getLogger(ClientThread.class);
  private final int id;
  private String nickname;
  private final Server server;
  private Lobby lobby;
  private int playerIndex = -1;
  private final Socket socket;
  private OutputStream out;
  private BufferedReader bReader;
  private static final int PING_TIMEOUT = 15000;
  private final ServerPingThread pingThread;
  private boolean isReady = false;
  private volatile boolean isRunning = true;


  /**
   * Retrieves the unique identifier of this client thread.
   *
   * @return The ID of this client thread.
   */
  public int getId() {
    return id;
  }

  /**
   * Retrieves the nickname of this client.
   *
   * @return The nickname of the client.
   */
  public String getNickname() {
    return nickname;
  }

  /**
   * Retrieves the index of the player associated with this client in the current lobby.
   *
   * @return The index of the player in the lobby, or -1 if not assigned to any player.
   */
  public int getPlayerIndex() {
    return playerIndex;
  }

  /**
   * Sets the readiness status of the client. <p>
   * This status is used to determine when all the players in a lobby are ready to start a game.
   *
   * @param newValue The new readiness status.
   */
  public void setIsReady(boolean newValue) {
    isReady = newValue;
  }

  /**
   * Constructor of the EchoClientThread class. <p>
   * Sets up all the streams and starts a {@link ServerPingThread}.
   *
   * @param id     The id-number of the client which is always larger than 0.
   * @param socket The socket that is used to create the connection between client and server.
   * @param server The server which gets connected to the client.
   */
  public ClientThread(int id, Socket socket, Server server) {
    this.id = id;
    this.socket = socket;
    this.server = server;
    try {
      InputStream in = socket.getInputStream();
      bReader = new BufferedReader(new InputStreamReader(in));
      out = socket.getOutputStream();
    } catch (IOException e) {
      e.printStackTrace(System.err);
    }
    // TODO the ping thread should probably not be started here.. (but in the run() method)
    pingThread = new ServerPingThread(this, PING_TIMEOUT);
    pingThread.setName("PingThread-" + id);
    pingThread.start();
  }

  @Override
  public void run() {

    LOGGER.info("Server: Connection " + id + " established");
    try {
      //send(msg);

      while (isRunning) {
        String request;
        request = bReader.readLine();


        if (!request.equals("PING") && !request.equals("+PING") || server.isPingLoggingEnabled()) {
          LOGGER.debug("received: " + request);
        }
        if (!request.isEmpty() && request.charAt(0) == '+') { //request kann null sein, wenn es
          // keine Readline gibt, falls Client Verbindung verliert.
          handleResponse(request);
        } else {
          handleRequest(request);
        }
      }
    } catch (IOException | NullPointerException e) {
      if (e instanceof SocketException) {
        LOGGER.info("Connection was interrupted unexpectedly");
      } else {
        e.printStackTrace(System.err);
      }
    } finally {
      logout();
    }
    LOGGER.info("Connection " + id + " ended");
  }

  /**
   * Handles a Response as documented in the protocol.
   *
   * @param response Represents a response to a previously sent request, must start with a "+".
   */
  private void handleResponse(String response) {
    ArrayList<String> arguments = decodeProtocolMessage(response);
    String cmdStr = arguments.remove(0);
    cmdStr = cmdStr.substring(1);
    ServerRequest command = ServerRequest.valueOf(cmdStr);
    switch (command) {
      case STRT, PWIN, EMPT, CATS, STAT, NAMS, LEFT, JOND -> {
      }
      case PING -> {
        synchronized (pingThread) {
          pingThread.receivedResponse = true;
        }
      }
    }
  }


  /**
   * Reads a string and returns the boolean value it represents.
   *
   * @param flag Should be either "f" or "t".
   * @return The value represented by the string.
   * @throws IllegalArgumentException
   */
  private static boolean readFlag(String flag) throws IllegalArgumentException {
    boolean whisper = false;
    if (flag.equals("t")) {
      whisper = true;
    } else if (!flag.equals("f")) {
      // Falls flag weder "t" noch "f" ist, wird whisper auf false gesetzt
      // Alternativ könnte man auch eine Meldung ausgeben oder andere Aktionen ausführen
      whisper = false;
    }
    return whisper;
  }

  /**
   * Handles a request as described in the protocol. <p>
   * This method utilizes the following methods (list not entirely complete): <p>
   * in the case of a LOGI request:
   * <ul>
   *   <li>{@link ClientThread#login(String newNickName)}</li>
   * </ul>
   * <p>
   * in the case of a LOGO request:
   * <ul>
   *   <li>{@link ClientThread#logout()}</li>
   * </ul>
   * <p>
   * in the case of a DRAW request:
   * <ul>
   *   <li>{@link ClientThread#notAllowedToDraw()}</li>
   *   <li>{@link ClientThread#draw(String stackName)}</li>
   * </ul>
   * <p>
   * in the case of a HIGH request:
   * <ul>
   * <li>{@link HighScores#getHighScores()}</li>
   * </ul>
   * <p>
   * in the case of a PUTT request:
   * <ul>
   *   <li>{@link ClientThread#cantPutTile()}</li>
   *   <li>{@link ClientThread#checkIfValid(Tile tile, OrderedDeck deck)}</li>
   *   <li>{@link ClientThread#checkIfWon(OrderedDeck deck)}</li>
   *   <li>{@link GameState#putTile(Tile tile, int playerIndex)}</li>
   *   <li>{@link ClientThread#sendState()}</li>
   * </ul>
   * <p>
   * in the case of a CATC request:
   * <ul>
   *   <li>{@link ClientThread#handleChat(ArrayList arguments)}</li>
   * </ul>
   * <p>
   * in the case of a NAME request:
   * <ul>
   *   <li>{@link ClientThread#changeName(String newNickname)}</li>
   *   <li>{@link ClientThread#sendNicknameList()}</li>
   * </ul>
   * <p>
   * in the case of a LGAM request:
   * <ul>
   *   <li>{@link ClientThread#sendLobbiesWithState(String lobbyState)}</li>
   * </ul>
   * <p>
   * in the case of a LPLA request:
   * <ul>
   *   <li>{@link ClientThread#listPlayersConnectedToServer()}</li>
   * </ul>
   * <p>
   * in the case of a JLOB request:
   * <ul>
   *   <li>{@link ClientThread#joinOrCreateLobby(int lobbyNumber)}</li>
   *   <li>{@link ClientThread#sendNicknameList()}</li>
   * </ul>
   * <p>
   * in the case of a LLOB request:
   * <ul>
   *   <li>{@link ClientThread#removeFromLobby()}</li>
   * </ul>
   * <p>
   * in the case of a REDY request:
   * <ul>
   *   <li>{@link ClientThread#notAllReady()}</li>
   *   <li>{@link ClientThread#distributeDecks()}</li>
   * </ul>
   * <p>
   * in the case of a WINC request:
   * <ul>
   *   <li>{@link ClientThread#activateCheatCode()}</li>
   * </ul>
   *
   * @param request The request being handled. It must have the form defined in the protocol.
   * @throws IOException
   */

  private void handleRequest(String request) throws IOException {
    ArrayList<String> arguments = decodeProtocolMessage(request);
    String cmdStr = arguments.remove(0);
    try {
      ClientRequest command = ClientRequest.valueOf(cmdStr);
      // TODO handle all cases
      switch (command) {
        case LOGI -> {
          login(arguments.get(0));
        }
        case LOGO -> {
          send("+LOGO");
          logout();
        }
        case DRAW -> {
          if (notAllowedToDraw()) break;
          // TODO put much of this functionality into a method in class GameState (or somewhere where it makes sense)
          String stackName = arguments.get(0);
          draw(stackName);
        }
        case HIGH -> {
          send(encodeProtocolMessage("+HIGH", HighScores.getHighScores()));
        }

        case PUTT -> {
          //checks if player can put a tile, based on the Tile itself and wether his deck is valid
          Tile tile = Tile.parseTile(arguments.get(0));
          OrderedDeck deck = new OrderedDeck(Tile.stringsToTileArray(decodeProtocolMessage(arguments.get(1))));
          //TODO refactor so that you can give a tile and a tilearray as parameters to CheckiIfValid and CheckIfWOn
          if (cantPutTile()) return;
          if (!checkIfValid(tile, deck)) return;
          if (checkIfWon(deck)) return;
          send(encodeProtocolMessage("+PUTT", "t", "f"));
          lobby.gameState.putTile(tile, playerIndex);
          sendState();
        }
        case CATC -> {
          handleChat(arguments);
        }
        case PING -> send("+PING");
        case NAME -> {
          changeName(arguments.get(0));
          send(encodeProtocolMessage("+NAME", nickname));
          if (lobby != null && playerIndex >= 0) {
            sendNicknameList();
          }
        }
        case LGAM -> {
          sendLobbiesWithState(arguments.get(0));
        }
        case LLPL -> {
          send(encodeProtocolMessage("+LLPL", NetworkUtils.getEncodedLobbiesWithPlayerList(server.getLobbies())));
        }
        case LPLA -> {
          listPlayersConnectedToServer();
        }
        case JLOB -> {
          if (!joinOrCreateLobby(Integer.parseInt(arguments.get(0)))) {
            break;
          }
          lobby.sendToLobby(encodeProtocolMessage("JOND", nickname), null);
          sendNicknameList();
        }
        case LLOB -> {
          removeFromLobby();
        }
        case REDY -> {
          if (notAllReady()) break;
          distributeDecks();
        }
        case WINC -> {
          activateCheatCode();
        }
        case RNAM -> {
          String nicknames = lobby.getNicknameList();
          send(encodeProtocolMessage("+RNAM", nicknames));
        }
        case RSTA ->{
          sendStateToClientWhoJustPutTile();
        }
      }
    } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
      // in an ideal world, this line should never be reached:
      LOGGER.error("Nachricht vom Client: \"" + request + "\" verursachte folgende Exception: " + e.toString());
    }
  }

  /**
   * Draws from the specified stack (either from main or exchange stack) and sends a DRAW. <p>
   * If there is an attempt at drawing from the empty main stack, the game is ended with no winner.
   * This method utilizes the {@link ClientThread#endGameWithNoWinner()} method for this case.
   * Otherwise, the method {@link ClientThread#sendState()} is used to inform each client of the new exchange stack configuration.
   *
   * @param stackName must either be "m" for main stack or "e" for exchange stack.
   * @throws IOException              if {@link ClientThread#send(String)} fails to send the response.
   * @throws IllegalArgumentException if exchange stack is empty, and you still reach this method.
   */
  public void draw(String stackName) throws IOException, IllegalArgumentException {
    boolean isMainStack = isMainStack(stackName);
    Tile tile = lobby.gameState.drawTile(isMainStack, playerIndex);
    if (tile == null && stackName.equals("e")) {
      //code should never reach this line because of previous checks in handleRequest in case DRAW
      throw new IllegalArgumentException("exchangestack is empty");
    }
    if (tile == null) {
      endGameWithNoWinner();
    } else {
      String tileString = tile.toString();
      send(encodeProtocolMessage("+DRAW", tileString));
      sendState();
    }
  }

  /**
   * Checks if the player is allowed to draw a tile and sends a empty DRAW response to the client if he is not allowed to draw. <p>
   * The player is only allowed to draw if there are 4 players in the lobby, his index matches with the currentPlayerIndex of the gameState (meaning it's his turn), and he has 14 tiles in his deck. <p>
   * If the player is not allowed to draw, this method sends {@code "+DRAW \"%\" <debugmessage>"} to the client, else it doesn't send anything.
   * This method should only be used in the
   *
   * @return true if the player is not allowed to draw, false otherwise.
   * @throws IOException If an I/O error occurs while sending the response.
   */
  public boolean notAllowedToDraw() throws IOException {
    //if (lobby.getNumberOfPlayers() < 4) {
    //  send(encodeProtocolMessage("+DRAW", "", "One or more players left the game, please wait until the lobby is full again"));
    //  return true;
    //}
    if (!lobby.gameState.isPlayersTurn(playerIndex)) {
      send(encodeProtocolMessage("+DRAW", "", "It is not your turn.. have some patience"));
      return true;
    }
    if (!lobby.gameState.canDraw(playerIndex)) {
      send(encodeProtocolMessage("+DRAW", "", "You shall not draw!"));
      return true;
    }
    return false;
  }

  /**
   * Checks if the given stack name is the name for the main stack or the name for the exchange stack.
   * Returns {@code true} if {@param stackName} is {@code "m"}, {@code false} if {@param stackName} is {@code "e"}
   * and throws an {@link IllegalArgumentException} if the {@param stackName} has neither of those values.
   *
   * @param stackName The name of the stack. Must be either "m" for main stack or "e" for exchange stack.
   * @return true if the stack is the main stack, false otherwise.
   * @throws IllegalArgumentException if the stack name specified is neither the main stack nor the exchange stack.
   */
  public static boolean isMainStack(String stackName) {
    boolean isMainStack;
    switch (stackName) {
      case "e" -> {
        isMainStack = false;
      }
      case "m" -> {
        isMainStack = true;
      }
      default -> {
        throw new IllegalArgumentException("No Stack with name: " + stackName);
      }
    }
    return isMainStack;
  }

  /**
   * Checks if the player's move is valid, if not it sends a PUTT response with the corresponding flag values to the client.
   * This means that in the case of an invalid move, this method sends {@code "+PUTT f f"} to the client.
   * The method {@link Lobby#validateMove(Tile, OrderedDeck, int)} is used to validate the move.
   *
   * @param tile The tile to be placed.
   * @param deck The player's deck.
   * @return true if the move is valid, false otherwise.
   * @throws IOException If an I/O error occurs while sending the response.
   */
  public boolean checkIfValid(Tile tile, OrderedDeck deck) throws IOException {
    boolean isValid = lobby.validateMove(tile, deck, playerIndex);
    if (!isValid) {
      LOGGER.info("Player " + playerIndex + " did an invalid move: put " + tile + " on the next stack and had " + deck.toString() + " as a deck.");
      send(encodeProtocolMessage("+PUTT", "f", "f"));
      return false;
    }
    return true;
  }

  /**
   * Checks if the player has won, if that is the case ends the game and sends all the protocol responses and requests necessary (PWIN requests and PUTT response). <p>
   * The method {@link OrderedDeck#isWinningDeck()} is used to check if the player has won.
   * If the player has won, the game is ended with {@link Lobby#finishGame(String)},
   * a PWIN request is sent to all clients in the lobby,
   * and the following PUTT response is sent to the client: {@code "+PUTT t t"}
   *
   * @param deck The player's deck.
   * @return true if the player has won, false otherwise.
   * @throws IOException If an I/O error occurs while sending the response.
   */
  public boolean checkIfWon(OrderedDeck deck) throws IOException {
    //OrderedDeck clientDeck = new OrderedDeck(tileArray);
    if (deck.isWinningDeck()) {
      lobby.finishGame(nickname);
      lobby.sendToLobby(encodeProtocolMessage("PWIN", nickname), null);
      send(encodeProtocolMessage("+PUTT", "t", "t"));
      return true;
    }
    return false;
  }

  /**
   * Checks if the player is allowed to put a tile on the next exchange stack, sends a PUTT response accordingly. <p>
   * The player is only allowed to put a tile on the next exchange stack,
   * if the number of players in the lobby is exactly 4, it is his turn, and he has exactly 15 tiles in his deck.
   * If the player is not allowed to put a tile on the next exchange stack, this method sends a PUTT response like the following to the client: {@code "+PUTT f f <debugmessage>"}
   *
   * @return true if the player is not allowed to put a tile, false otherwise.
   * @throws IOException If an I/O error occurs while sending the response.
   */
  public boolean cantPutTile() throws IOException {
    //if (lobby.getNumberOfPlayers() < 4) {
    //  send(encodeProtocolMessage("+PUTT", "f", "f", "One or more players left the game, please wait until the lobby is full again"));
    //  return true;
    //}
    // this checks if it's the players turn rn
    if (!lobby.gameState.isPlayersTurn(playerIndex)) {
      send(encodeProtocolMessage("+PUTT", "f", "f", "It is not your turn.. have some patience"));
      return true;
    }
    //checks if player can put a tile, based on the amount of tiles in the deck
    if (!lobby.gameState.canPutTile(playerIndex)) {
      send(encodeProtocolMessage("+PUTT", "f", "f", "You shall not put a tile!"));
      return true;
    }
    return false;
  }

  /**
   * Sends a LGAM response with the lobbies with the in {@param lobbyState} specified state. <p>
   * If {@param lobbyState} is {@code "o"} the open lobbies (games) are sent,
   * if {@param lobbyState} is {@code "r"} the running lobbies (games) are sent,
   * and if {@param lobbyState} is {@code "f"} the finished lobbies (games) are sent. <p>
   * Example: {@code "+LGAM o \"1:3 42:2%\""} is sent to the client <p>
   * In this example, the server has two lobbies; lobby 1 has 3 players, lobby 42 has 2 players.
   *
   * @param lobbyState which can be either "o" for open lobbies, "r" for running lobbies or "f" for finished lobbies.
   * @throws IOException              If an I/O error occurs while sending the response.
   * @throws IllegalArgumentException if lobbyStatus is not equal to either of the parameters specified above.
   */
  private void sendLobbiesWithState(String lobbyState) throws IOException {
    StringBuilder sb = new StringBuilder();
    ArrayList<Lobby> l;
    switch (lobbyState) {
      case "o" -> {
        l = listLobbiesWithState(Lobby.LobbyState.OPEN);
        for (var lobby : l) {
          sb.append(lobby.lobbyNumber);
          sb.append(":");
          sb.append(lobby.getNumberOfPlayers());
          sb.append(" ");
        }
        // delete unnecessary space
        if (!sb.isEmpty()) {
          sb.deleteCharAt(sb.length() - 1);
        }
      }
      case "r" -> {
        l = listLobbiesWithState(Lobby.LobbyState.RUNNING);
        for (var lobby : l) {
          sb.append(lobby.lobbyNumber);
          sb.append(" ");
        }
        // delete unnecessary space
        if (!sb.isEmpty()) {
          sb.deleteCharAt(sb.length() - 1);
        }
      }
      case "f" -> {
        l = listLobbiesWithState(Lobby.LobbyState.FINISHED);
        for (var lobby : l) {
          sb.append(lobby.lobbyNumber);
          sb.append(":");
          String winnerName = lobby.getWinnerName();
          if (winnerName != null) {
            sb.append(winnerName);
          }
          sb.append(" ");
        }
        // delete unnecessary space
        if (!sb.isEmpty()) {
          sb.deleteCharAt(sb.length() - 1);
        }
      }
      default -> {
        throw new IllegalArgumentException();
      }
    }
    send(encodeProtocolMessage("+LGAM", lobbyState, sb.toString()));
  }

  /**
   * Sets the deck of the client to a winning deck and sends it as a WINC response.
   * The deck of the client gets set so that it has as many tiles as the client had before the cheat code was activated.
   *
   * @throws IOException If an I/O error occurs while sending the response.
   */
  private void activateCheatCode() throws IOException {
    ArrayList<Tile> winnerConf = new ArrayList<>(Arrays.asList(new Tile[]{
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
        null,
        new Tile(1, Color.YELLOW),
        new Tile(2, Color.YELLOW),
        new Tile(3, Color.YELLOW),
        new Tile(4, Color.YELLOW),
        new Tile(5, Color.YELLOW),
        new Tile(6, Color.YELLOW),
        new Tile(7, Color.YELLOW),
    }));
    if (!lobby.gameState.canDraw(playerIndex)) { // player has 15 tiles
      winnerConf.add(new Tile(0, Color.YELLOW));
    }
    OrderedDeck winnerDeck = new OrderedDeck(winnerConf.toArray(new Tile[]{}));
    lobby.gameState.setPlayerDeck(playerIndex, winnerDeck);
    ArrayList<String> stringTiles = winnerDeck.toStringArrayList();
    String deckString = encodeProtocolMessage(stringTiles);
    send(encodeProtocolMessage("+WINC", deckString));
  }

  /**
   * Initializes the game state of the lobby that the player is in and sends a STRT request to each player in the lobby. <p>
   * To initialize the {@link Lobby#gameState}, the method {@link Lobby#startGame(int startPlayerIndex)} is used.
   *
   * @throws IOException If an I/O error occurs while sending the response.
   */
  private void distributeDecks() throws IOException {
    LOGGER.debug("All players ready!");
    Random rnd = new Random();
    int startPlayerIdx = rnd.nextInt(4);
    if (!lobby.startGame(startPlayerIdx)) {
      LOGGER.debug("lobby wasn't able to start.");
    }
    for (int i = 0; i < lobby.getPlayers().size(); i++) {
      OrderedDeck deck = lobby.gameState.getPlayerDeck(i);
      ArrayList<String> stringTiles = deck.toStringArrayList();
      String deckString = encodeProtocolMessage(stringTiles);
      lobby.getPlayers().get(i).send(encodeProtocolMessage("STRT", deckString, Integer.toString(i)));
    }
  }

  /**
   * Checks whether all players are ready and sends a REDY response to the client.
   *
   * @return true if not all players are ready.
   * @throws IOException If an I/O error occurs while sending the response.
   */
  private boolean notAllReady() throws IOException {
    isReady = true;
    send(encodeProtocolMessage("+REDY"));
    if (lobby.getNumberOfPlayers() != 4) {
      return true;
    }
    boolean allPlayersReady = true;
    for (var p : lobby.getPlayers()) {
      if (p == null || !p.isReady) {
        allPlayersReady = false;
        break;
      }

    }
    return !allPlayersReady;
  }

  /**
   * Sends an LPLA response to the client.
   * The LPLA response contains all players connected to the server.
   *
   * @throws IOException If an I/O error occurs while sending the response.
   */
  private void listPlayersConnectedToServer() throws IOException {
    ArrayList<ClientThread> clientNames = server.getClientList();
    StringBuilder namesServer = new StringBuilder();
    for (int i = 0; i < clientNames.size(); i++) {
      namesServer.append(clientNames.get(i).nickname + " ");
    }
    if (!namesServer.isEmpty()) {
      namesServer.deleteCharAt(namesServer.length() - 1);
    }
    send(encodeProtocolMessage("+LPLA", namesServer.toString()));
  }

  /**
   * Ends the game with no winner. This case occurs when there are no more tiles to be drawn from the
   * main stack. Sends a DRAW response with empty tile argument to the client and an EMPT request to all players in the lobby.
   *
   * @throws IOException If an I/O error occurs while sending the response.
   */
  private void endGameWithNoWinner() throws IOException {
    System.out.println("Game ended with no winner");
    send(encodeProtocolMessage("+DRAW", ""));
    // TODO (IMPORTANT!!!) should this not be a sendToLobby() call??
    lobby.sendToLobby("EMPT", null);
    lobby.finishGame("");
  }

  /**
   * Sends the specified string to the client.
   * The provided string gets a carriage return and line feed ("\r\n") at the end of it before being sent.
   * The provided string must NOT contain ANY Carriage returns or line feeds.
   *
   * @param str String to be sent to the client; must not contain any carriage returns or line feeds.
   * @throws IOException if the OutputStream throws an IOException.
   */
  public synchronized void send(String str) throws IOException {
    out.write((str + "\r\n").getBytes());
    if (!str.equals("PING") && !str.equals("+PING") || server.isPingLoggingEnabled()) {
      LOGGER.debug("sent: " + str);
    }
  }

  /**
   * Logs the player in with a given nickname.
   * To do that, the method {@link ClientThread#changeName(String newNickname)} is called.
   *
   * @param newNickname The new nickname of the player.
   * @throws IOException If an I/O error occurs while sending the response.
   */
  private void login(String newNickname) throws IOException {
    changeName(newNickname);
    send(encodeProtocolMessage("+LOGI", this.nickname));
  }

  /**
   * Ends the connection to the client by cleaning up all streams and removing the player from the server.
   * This method should be called when a connection interruption is detected or when the client wants to log out.
   */
  public void logout() {
    isRunning = false;
    try {
      // check if the player is in a lobby; playerIndex is -1 if the game hasn't started yet
      if (lobby != null && playerIndex >= 0) {
        lobby.removePlayer(playerIndex);
        playerIndex = -1;
        lobby.sendToLobby(encodeProtocolMessage("LEFT", nickname), null);
        sendNicknameList();
      }
      server.removeClient(this);
      socket.close();
      bReader.close();
      out.close();
    } catch (IOException e) {
      e.printStackTrace(System.err);
    }
  }


  /**
   * Changes the player's name to a (possibly) altered version of the provided name.
   * The string is only altered if there already is a player with the name that was provided to this method.
   *
   * @param newNickname The name to which the nickname should be changed.
   */
  private void changeName(String newNickname) {
    ArrayList<String> names = server.getNicknames();
    String actualNickname = newNickname;
    int counter = 1;

    // Solange ein Duplikat gefunden wird, erhöhe den Zähler und versuche es erneut
    while (names.contains(actualNickname)) {
      actualNickname = newNickname + "_" + counter;
      counter++;
    }
    nickname = actualNickname;
  }

  /**
   * Handles a CATC request. <p>
   * This is done with the arguments of the CATC request of the client. <p>
   * Depending on whether the first argument is {@code "b"} (for broadcast), {@code "l"} (for lobby), or {@code "w"} (for whisper),
   * the methods {@link Server#sendToAll(String str, ClientThread sender)}, {@link Lobby#sendToLobby(String str, ClientThread sender)}, or {@link ClientThread#send(String str)}
   * are used respectively to send a CATS request containing the message and message type.
   *
   * @param arguments The arguments of the CATC request.
   */
  private void handleChat(ArrayList<String> arguments) throws IOException {
    String messageType = arguments.get(0);
    String msg = arguments.get(1);
    String sender = nickname;
    String cmd = encodeProtocolMessage("CATS", messageType, msg, sender);
    switch (messageType) {
      case "b" -> {
        server.sendToAll(cmd, this);
      }
      case "l" -> {
        if (this.lobby == null) {
          LOGGER.error("Client should not send to lobby without being in one");
          return;
        }
        lobby.sendToLobby(cmd, this);
      }
      case "w" -> {
        if (server.sendToNickname(cmd, arguments.get(2))) {
          send(encodeProtocolMessage("+CATC", "w", arguments.get(1), arguments.get(2), "t"));
        } else {
          send(encodeProtocolMessage("+CATC", "w", "", arguments.get(2), "f"));
        }
        return;
      }
      default -> {
        throw new IllegalArgumentException("Should be one of \"b\", \"l\" or \"w\", was \"" + messageType + "\"");
      }
    }
    send(encodeProtocolMessage("+CATC", arguments.get(0), arguments.get(1)));
  }

  /**
   * Puts the client in either an existing lobby or creates a new one with the player in it and returns if it was successful.
   * This method utilizes the methods {@link Server#joinLobby(Lobby lobby, ClientThread client)} or {@link Server#createLobby(int lobbyNumber)} depending on the case.
   * If the lobby that the client wanted to join is already full (has 4 players) then this method returns {@code false}.
   * It then sends a JLOB response with the joinsuccessful flag set to {@code "t"} or {@code "f"} depending on whether it was successful.
   *
   * @param lobbyNumber The number of the lobby that the client wants to join or create.
   * @return {@code true} iff the client joined a lobby successfully.
   * @throws IOException Whenever send() throws an IOException.
   */
  private boolean joinOrCreateLobby(int lobbyNumber) throws IOException {
    if (lobby != null) {
      send(encodeProtocolMessage("+JLOB", "f", "Already in Lobby " + lobby.lobbyNumber));
      return false;
    }
    boolean createdNewLobby = false;
    //synchronized (server.lobbies) { // TODO is this synchronized block necessary?
    Lobby potentialLobby = server.getLobby(lobbyNumber);
    if (potentialLobby == null) {
      potentialLobby = server.createLobby(lobbyNumber);
      createdNewLobby = true;
    }
    if (server.joinLobby(potentialLobby, this)) {
      send(encodeProtocolMessage("+JLOB", "t", (createdNewLobby ? "Created new Lobby " : "Joined existing Lobby ") + lobbyNumber));
      lobby = potentialLobby;
      playerIndex = lobby.getPlayerIndex(this);
      return true;
    }
    send(encodeProtocolMessage("+JLOB", "f", "Lobby " + lobbyNumber + " full already, couldn't join"));
    return false;
    //}
  }

  /**
   * Returns a list of all lobbies with the given state.
   *
   * @param state state of the lobbies to be returned.
   * @return A list of all lobbies with the given state.
   */
  private ArrayList<Lobby> listLobbiesWithState(Lobby.LobbyState state) {
    ArrayList<Lobby> lobbiesWithStatus = new ArrayList<>();
    for (var lobby : server.getLobbies()) {
      if (lobby.getLobbyState() == state) {
        lobbiesWithStatus.add(lobby);
      }
    }
    return lobbiesWithStatus;
  }

  /**
   * Sends STAT request to all clients. <p>
   * This method utilizes the method {@link Lobby#getStatProtocolString()} to get the string to send to all players in the lobby.
   *
   * @throws IOException If an I/O error occurs.
   */
  private void sendState() throws IOException {
    lobby.sendToLobby(lobby.getStatProtocolString(), null);
  }

  /**
   * Sends the current state of the game to the client who just made a move. This method is typically called after a player has successfully put a tile.
   * It retrieves the state of the game using the {@link Lobby#getRstaProtocolString()} method and sends it to the client.
   * This helps ensure that the client's view of the game state is synchronized with the server after their action.
   *
   * @throws IOException If an I/O error occurs while sending the game state to the client.
   */
  private void sendStateToClientWhoJustPutTile() throws IOException {
    String state = lobby.getRstaProtocolString();
    send(state);
  }

  /**
   * Sends the list of nicknames of players in the lobby to all clients.
   *
   * @throws IOException if an I/O error occurs.
   */
  private void sendNicknameList() throws IOException {
    String names = lobby.getNicknameList();
    lobby.sendToLobby(encodeProtocolMessage("NAMS", names), null);
  }

  /**
   * Removes the client from the lobby and sends a LLOB response.
   * If the player wasn't in a lobby, a LLOB with the flag {@code "f"}, else with the flag {@code "t"} is sent.
   *
   * @throws IOException if an I/O error occurs.
   */
  private void removeFromLobby() throws IOException {
    if (lobby != null && playerIndex >= 0) {
      lobby.removePlayer(playerIndex);
      playerIndex = -1;
      send(encodeProtocolMessage("+LLOB", "t"));
      lobby.sendToLobby(encodeProtocolMessage("LEFT", nickname), null);
      sendNicknameList();
      lobby = null;
    } else {
      send(encodeProtocolMessage("+LLOB", "f"));
    }
  }
}
