package server;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalDateTime;
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

import java.time.format.DateTimeFormatter;


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
   * Sets the readiness status of the client.
   *
   * @param newValue The new readiness status.
   */
  public void setIsReady(boolean newValue) {
    isReady = newValue;
  }

  /**
   * Constructor of the EchoClientThread class.
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
    pingThread = new ServerPingThread(this, PING_TIMEOUT);
    pingThread.setName("PingThread-" + id);
    pingThread.start();
  }

  // for testing purposes
  public static void main(String[] args) {
    String request = "CATC t \"hallo ich bin emanuel \\\"bruh\\\"\" 3 bruh";
    System.out.println("Result: " + decodeProtocolMessage(request).toString());
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
    // TODO add log, if cmdStr is not of size 4
    ServerRequest command = ServerRequest.valueOf(cmdStr);
    switch (command) {
      case PWIN, EMPT, CATS, STAT, NAMS, LEFT, JOND -> {
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
   * Handles a request as described in the protocol.
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
        case HIGH ->{
          send(encodeProtocolMessage("+HIGH", HighScores.getHighScores()));
        }

        case PUTT -> {
          //checks if player can put a tile, based on the Tile itself and wether his deck is valid
          Tile tile = Tile.parseTile(arguments.get(0));
          OrderedDeck deck = new OrderedDeck(Tile.stringsToTileArray(decodeProtocolMessage(arguments.get(1))));
          //TODO refactor so that you can give a tile and a tilearray as parameters to CheckiIfValid and CheckIfWOn
          if(cantPutTile()) return;
          if(!checkIfValid(tile, deck)) return;
          if(checkIfWon(deck)) return;
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
          listDemandedGamestatus(arguments.get(0));
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
      }
    } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
      // in an ideal world, this line should never be reached:
      LOGGER.error("Nachricht vom Client: \"" + request + "\" verursachte folgende Exception: " + e.toString());
    }
  }

  /**
   * Using this method you can either draw from the main stack or the exchange stack. If the main stack is
   * empty and you try to draw from it, then the game will be ended with no winner. Sends response and game state accordingly to client.
   *
   * @param stackName should either be "m" for main stack or "e" for exchange stack.
   * @throws IOException if send method fails.
   */
  public void draw(String stackName) throws IOException {
    boolean isMainStack = isMainStack(stackName);
    Tile tile = lobby.gameState.drawTile(isMainStack, playerIndex);
    String tileString;
    if (tile == null) {
      endGameWithNoWinner();
    } else {
      tileString = tile.toString();
      send(encodeProtocolMessage("+DRAW", tileString));
      sendState();
    }
  }

  /**
   * Checks if the player is allowed to draw a tile. The player is allowed to draw only if it's the payers turn
   * and he hasn't already drawn a tile. Sends response accordingly to client.
   *
   * @return true if the player is not allowed to draw, false otherwise.
   * @throws IOException If an I/O error occurs while sending the response.
   */
  public boolean notAllowedToDraw() throws IOException {
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
   * Checks if a given stack is the main stack.
   *
   * @param stackName The name of the stack. Must be "m" for main stack or "e" for exchange stack.
   * @return true if the stack is the main stack, false otherwise.
   * @throws IllegalArgumentException if the stack specified is neither the main stack nor the exchange stack.
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
   * Checks if the player's move is valid. Sends response accordingly to client.
   *
   * @param tile The tile to be placed.
   * @param deck The player's deck.
   * @return true if the move is valid, false otherwise.
   * @throws IOException If an I/O error occurs while sending the response.
   */
  public boolean checkIfValid(Tile tile, OrderedDeck deck) throws IOException {
    boolean isValid = lobby.validateMove(tile, deck, playerIndex);
    if (!isValid) {
      LOGGER.error("Player " + playerIndex + " did an invalid move: put " + tile + " on the next stack and had " + deck.toString() + " as a deck.");
      send(encodeProtocolMessage("+PUTT", "f", "f"));
      return false;
    }
    return true;
  }

  /**
   * Checks if the player has won. Sends response accordingly to client.
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
   * Checks if the player is allowed to put a tile. Sends response accordingly to client.
   *
   * @return true if the player is not allowed to put a tile, false otherwise.
   * @throws IOException If an I/O error occurs while sending the response.
   */
  public boolean cantPutTile() throws IOException {
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
   * This method shows sends the demanded game status to the client which can either be open lobbies, running lobbies meaning
   * lobbies that are currently playing or finished lobbies meaning lobbies that have finished playing. Sends response accordingly to client.
   *
   * @param gameStatus which can be either "o" for open lobbies, "r" for running lobbies or "f" for finished lobbies.
   * @throws IOException If an I/O error occurs while sending the response.
   * @throws IllegalArgumentException if gameStatus is not equal to either of the parameters specified above.
   */
  private void listDemandedGamestatus(String gameStatus) throws IOException {
    StringBuilder sb = new StringBuilder();
    ArrayList<Lobby> l;
    switch (gameStatus) {
      case "o" -> {
        l = listLobbiesWithStatus(Lobby.LobbyState.OPEN);
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
        l = listLobbiesWithStatus(Lobby.LobbyState.RUNNING);
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
        l = listLobbiesWithStatus(Lobby.LobbyState.FINISHED);
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
    send(encodeProtocolMessage("+LGAM", gameStatus, sb.toString()));
  }

  /**
   * This method is used when a player wants to cheat and win. It sends to the client a winning deck.
   *
   * @throws IOException If an I/O error occurs while sending the response.
   */
  private void activateCheatCode() throws IOException {
    // TODO update the winner-configuration to be more overpowered
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
   * Distributes deck to clients as soon as all clients are ready to play.
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
   * Checks if all players are ready or not.
   *
   * @return true if not all players are ready.
   * @throws IOException If an I/O error occurs while sending the response.
   */
  private boolean notAllReady() throws IOException {
    isReady = true;
    send(encodeProtocolMessage("+REDY"));
    if (lobby.getPlayers().size() != 4) {
      return true;
    }
    boolean allPlayersReady = true;
    for (var p : lobby.getPlayers()) {
      if (p == null || !p.isReady) {
        allPlayersReady = false;
        break;
      }

    }
    if (!allPlayersReady) {
      return true;
    }
    return false;
  }

  /**
   * This method lists all the players that are connected to the server.
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
   * main stack. Sends response accordingly to the client.
   *
   * @throws IOException If an I/O error occurs while sending the response.
   */
  private void endGameWithNoWinner() throws IOException {
    String tileString;
    tileString = "";
    send(encodeProtocolMessage("+DRAW", tileString));
    send("EMPT");
    lobby.finishGame("");
  }

  /**
   * Sends a string to the client.
   * This string is supplemented with carriage return and line feed ("\r\n").
   *
   * @param str String to be sent to the client.
   * @throws IOException Thrown if the OutputStream throws an IOException.
   */
  public synchronized void send(String str) throws IOException {
    out.write((str + "\r\n").getBytes());
    if (!str.equals("PING") && !str.equals("+PING") || server.isPingLoggingEnabled()) {
      LOGGER.debug("sent: " + str);
    }
  }

  /**
   * Logs the player in with a given nickname
   *
   * @param newNickname The new nickname of the player.
   * @throws IOException If an I/O error occurs while logging in.
   */
  private void login(String newNickname) throws IOException {
    // TODO maybe don't send "+LOGI ..." here but inside the switch statement?
    changeName(newNickname);
    send(encodeProtocolMessage("+LOGI", this.nickname));
  }

  /**
   * Ends the connection to the client.
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
   * Changes the player's name to the given name if it does not already exist.
   * Otherwise, the name is modified so that it is unique on the server.
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
   * Handles a chat request by either sending it to everyone or just to one specific person.
   *
   * @param arguments The arguments of the chat request.
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
   * Joins the lobby specified with lobbyNumber. If such a lobby doesn't exist it creates one.
   * It then sends a message depending on if it created a new one, joined successfully or wasn't able to join because the lobby was already full.
   *
   * @param lobbyNumber The number of the lobby that the client wants to join or create.
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
   * Returns a list of all lobbies with the given status.
   *
   * @param status The status of the lobbies to be returned.
   * @return A list of all lobbies with the given status.
   */
  private ArrayList<Lobby> listLobbiesWithStatus(Lobby.LobbyState status) {
    ArrayList<Lobby> lobbiesWithStatus = new ArrayList<>();
    for (var lobby : server.getLobbies()) {
      if (lobby.getLobbyState() == status) {
        lobbiesWithStatus.add(lobby);
      }
    }
    return lobbiesWithStatus;
  }

  /**
   * Sends the current state of the game to all clients.
   *
   * @throws IOException If send() throws an IOException.
   */
  private void sendState() {
    String exchangeStacks = Tile.tileArrayToProtocolArgument(lobby.gameState.getVisibleTiles());
    String currentPlayerIdx = Integer.toString(lobby.gameState.getCurrentPlayerIndex());
    lobby.sendToLobby(encodeProtocolMessage("STAT", exchangeStacks, currentPlayerIdx), null);
  }

  /**
   * Sends the list of nicknames of players in the lobby to all clients.
   *
   * @throws IOException Thrown if an I/O error occurs while sending the nickname list.
   */
  private void sendNicknameList() throws IOException {
    String names = lobby.getNicknameList();
    lobby.sendToLobby(encodeProtocolMessage("NAMS", names), null);
  }

  /**
   * Removes the client from the lobby.
   * If the client is not in a lobby, sends a failure message.
   *
   * @throws IOException Thrown if an I/O error occurs while removing the client from the lobby.
   */
  private void removeFromLobby() throws IOException {
    if (lobby != null && playerIndex >= 0) {
      lobby.removePlayer(playerIndex);
      playerIndex = -1;
      send(encodeProtocolMessage("+LLOB", "t"));
      lobby.sendToLobby(encodeProtocolMessage("LEFT",nickname), null);
      sendNicknameList();
      lobby = null;
    }
    else{
      send(encodeProtocolMessage("+LLOB", "f"));
    }
  }
}
