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
  public static final Logger LOGGER = LogManager.getLogger(ClientThread.class);
  public int id;
  public String nickname;
  private final Server server;
  private Lobby lobby;
  public int playerIndex = -1;
  private final Socket socket;
  public OutputStream out;
  private BufferedReader bReader;
  private static final int PING_TIMEOUT = 15000;
  private final ServerPingThread pingThread;
  public boolean isReady = false;
  public volatile boolean isRunning = true;

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
    // TODO handle SocketTimeoutException, SocketException

    LOGGER.info("Server: Connection " + id + " established");
    try {
      //send(msg);

      while (isRunning) {
        String request;
        request = bReader.readLine();


        if (!request.equals("PING") && !request.equals("+PING") || Server.ENABLE_PING_LOGGING) {
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
      case PWIN -> {
      }
      case EMPT -> {
      }
      case CATS -> {
      }
      case STAT -> {
      }
      case PING -> {
        synchronized (pingThread) {
          pingThread.receivedResponse = true;
        }
      }
      case NAMS ->{

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
    // TODO use encodeProtocolMessage()
    try {
      // TODO change this so that incorrect input gets handled
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
          draw(arguments);


        }

        case PUTT -> {
          // TODO put much of this functionality into a method in class GameState (or somewhere where it makes sense)
          //checks if player can put a tile, based on the Tile itself and wether his deck is valid
          checkIfValidOrWon(arguments);
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
          listDemandedGamestatus(arguments);
        }
        case LLPL -> {
          send(encodeProtocolMessage("+LLPL", NetworkUtils.getEncodedLobbiesWithPlayerList(server.lobbies)));
        }
        case LPLA -> {
          listPlayersConnectedToServer();
        }
        case JLOB -> {
          joinOrCreateLobby(Integer.parseInt(arguments.get(0)));
        }
        case REDY -> {
          if (notAllReady()) break;
          distributeDecks();
        }
        case WINC -> {
          activateCheatCode();
        }
      }
    } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
      // in an ideal world, this line should never be reached:
      LOGGER.error("Nachricht vom Client: \"" + request + "\" verursachte folgende Exception: " + e.toString());
    }
  }

  private void draw(ArrayList<String> arguments) throws IOException {
    boolean isMainStack = isMainStack(arguments);
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

  private boolean notAllowedToDraw() throws IOException {
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

  private static boolean isMainStack(ArrayList<String> arguments) {
    String pullStackName = arguments.get(0);
    boolean isMainStack;
    switch (pullStackName) {
      case "e" -> {
        isMainStack = false;
      }
      case "m" -> {
        isMainStack = true;
      }
      default -> {
        throw new IllegalArgumentException("No Stack with name: " + pullStackName);
      }
    }
    return isMainStack;
  }

  private void checkIfValidOrWon(ArrayList<String> arguments) throws IOException {
    if (cantPutTile()) {
      return;
    }
    String tileString = arguments.get(0);
    Tile tile = Tile.parseTile(tileString);
    Tile[] tileArray = Tile.stringsToTileArray(decodeProtocolMessage(arguments.get(1)));
    OrderedDeck clientDeck = new OrderedDeck(tileArray);
    boolean isValid = lobby.validateMove(tile, clientDeck, playerIndex);
    boolean isWon = false;
    if (!isValid) {
      LOGGER.error("Player " + playerIndex + " did an invalid move: put " + tile + " on the next stack and had " + Arrays.toString(tileArray) + " as a deck.");
      send(encodeProtocolMessage("+PUTT", "f", "f"));
      return;
    }
    if (Tile.isWinningDeck(tileArray)) {
      isWon = true;
      lobby.finishGame(nickname);
      server.sendToAll(encodeProtocolMessage("PWIN", nickname), this);
    }
    send(encodeProtocolMessage("+PUTT", "t", isWon ? "t" : "f"));

    lobby.gameState.putTile(tile, playerIndex);

    sendState();
  }

  private boolean cantPutTile() throws IOException {
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

  private void listDemandedGamestatus(ArrayList<String> arguments) throws IOException {
    StringBuilder sb = new StringBuilder();
    String gameStatus = arguments.get(0);
    ArrayList<Lobby> l;
    switch (gameStatus) {
      case "o" -> {
        l = listLobbiesWithStatus(Lobby.LobbyState.OPEN);
        for (var lobby : l) {
          sb.append(lobby.lobbyNumber);
          sb.append(":");
          sb.append(lobby.players.size());
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
          if (lobby.winnerName != null) {
            sb.append(lobby.winnerName);
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

  private void activateCheatCode() throws IOException {
    // TODO update the winner-configuration to be more overpowered
    ArrayList<Tile> winnerConf = new ArrayList<>(Arrays.asList(new Tile[]{
        new Tile(0, Color.BLACK),
        new Tile(1, Color.BLUE),
        new Tile(2, Color.BLUE),
        new Tile(3, Color.BLUE),
        new Tile(4, Color.BLUE),
        new Tile(1, Color.RED),
        new Tile(2, Color.RED),
        new Tile(3, Color.RED),
        new Tile(4, Color.RED),
        new Tile(1, Color.YELLOW),
        new Tile(2, Color.YELLOW),
        new Tile(3, Color.YELLOW),
        new Tile(4, Color.YELLOW),
        new Tile(5, Color.YELLOW),
    }));
    if (lobby.gameState.playerDecks.get(playerIndex).countTiles() == 15) {
      winnerConf.add(new Tile(0, Color.YELLOW));
    }
    OrderedDeck winnerDeck = new OrderedDeck(winnerConf.toArray(new Tile[]{}));
    lobby.gameState.playerDecks.set(playerIndex, winnerDeck);
    ArrayList<String> stringTiles = winnerDeck.toStringArrayList();
    String deckString = encodeProtocolMessage(stringTiles);
    send(encodeProtocolMessage("+WINC", deckString));
  }

  private void distributeDecks() throws IOException {
    LOGGER.debug("All players ready!");
    Random rnd = new Random();
    int startPlayerIdx = rnd.nextInt(4);
    if (!lobby.startGame(startPlayerIdx)) {
      LOGGER.debug("lobby wasn't able to start.");
    }
    for (int i = 0; i < lobby.players.size(); i++) {
      OrderedDeck deck = lobby.gameState.playerDecks.get(i);
      ArrayList<String> stringTiles = deck.toStringArrayList();
      String deckString = encodeProtocolMessage(stringTiles);
      lobby.players.get(i).send(encodeProtocolMessage("STRT", deckString, Integer.toString(i)));
    }
  }

  private boolean notAllReady() throws IOException {
    isReady = true;
    send(encodeProtocolMessage("+REDY"));
    if (lobby.players.size() != 4) {
      return true;
    }
    boolean allPlayersReady = true;
    for (var p : lobby.players) {
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
    if (!str.equals("PING") && !str.equals("+PING") || Server.ENABLE_PING_LOGGING) {
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
      }
      server.removeClient(this);
      if (lobby != null && playerIndex >= 0) {
        sendNicknameList();
      }
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
   * @return The nickname received by the client.
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
  private void joinOrCreateLobby(int lobbyNumber) throws IOException {
    if (lobby != null) {
      send(encodeProtocolMessage("+JLOB", "f", "Already in Lobby " + lobby.lobbyNumber));
      return;
    }
    boolean createdNewLobby = false;
    synchronized (server.lobbies) {
      Lobby potentialLobby = server.getLobby(lobbyNumber);
      if (potentialLobby == null) {
        potentialLobby = server.createLobby(lobbyNumber);
        createdNewLobby = true;
      }
      if (server.joinLobby(potentialLobby, this)) {
        send(encodeProtocolMessage("+JLOB", "t", (createdNewLobby ? "Created new Lobby " : "Joined existing Lobby ") + lobbyNumber));
        lobby = potentialLobby;
        playerIndex = lobby.getPlayerIndex(this);
      } else {
        send(encodeProtocolMessage("+JLOB", "f", "Lobby " + lobbyNumber + " full already, couldn't join"));
      }
    }
  }

  /**
   * Returns a list of all lobbies with the given status.
   *
   * @param status The status of the lobbies to be returned.
   * @return A list of all lobbies with the given status.
   */
  private ArrayList<Lobby> listLobbiesWithStatus(Lobby.LobbyState status) {
    ArrayList<Lobby> lobbiesWithStatus = new ArrayList<>();
    for (var lobby : server.lobbies) {
      if (lobby.lobbyState == status) {
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
    String currentPlayerIdx = Integer.toString(lobby.gameState.currentPlayerIdx);
    server.sendToAll(encodeProtocolMessage("STAT", exchangeStacks, currentPlayerIdx), null);
  }

  private void sendNicknameList() throws IOException {
    String names = lobby.getNicknameList();
    send(encodeProtocolMessage("NAMS", names));
  }
}
