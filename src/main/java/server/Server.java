package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * The Server class represents a simple server that listens for client connections
 * on a specified port and handles communication with multiple clients concurrently.
 *
 * @author Pascal von Fellenberg
 * @author Istref Uka
 */
public class Server {
  private static final Logger LOGGER = LogManager.getLogger(Server.class);
  private boolean ENABLE_PING_LOGGING = false;
  private volatile ArrayList<ClientThread> clientList;
  private ServerSocket serverSocket;
  private final ArrayList<Lobby> lobbies = new ArrayList<>();

  // TODO minimize the use of this method
  /**
   * Retrieves the list of active lobbies on the server.
   *
   * @return An ArrayList of active lobbies on the server.
   */
  public ArrayList<Lobby> getLobbies() {
    return lobbies;
  }

  /**
   * Retrieves the list of active client threads connected to the server.
   *
   * @return an ArrayList of {@code ClientThread} objects representing the connected clients.
   */
  public ArrayList<ClientThread> getClientList() {
    return clientList;
  }

  /**
   * Main method to start the server. It accepts a port number as input.
   * Optionally, the second parameter can enable ping logging.
   *
   * @param args The command-line arguments. The first argument is the port number.
   *             The second argument (optional) is a boolean indicating whether to enable ping logging.
   */
  public static void main(String[] args) {
    if (args.length < 1) {
      LOGGER.fatal("No port number given.");
    }
    LOGGER.info("Logging level: " + LOGGER.getLevel().toString());
    Server s = new Server(Integer.parseInt(args[0]));
    if (args.length == 2) {
      s.ENABLE_PING_LOGGING = Boolean.parseBoolean(args[1]);
    }
    s.start();
  }

  /**
   * Constructs a server that listens for incoming connections on the specified port.
   * For each connection, a new {@code ClientThread} is initiated and stored in a list.
   * The server runs indefinitely, accepting new client connections.
   *
   * @param port The port number on which the server will listen.
   */
  private Server(int port) {
    try {
      serverSocket = new ServerSocket(port);
      clientList = new ArrayList<>();
    } catch (IOException e) {
      e.printStackTrace(System.err);
      System.exit(1);
    }
  }

  /**
   * Starts the server, accepting incoming connections from clients.
   * Upon accepting a new connection, a new {@code ClientThread} is created to manage communication
   * with the client.
   */
  public void start() {
    int cnt = 0;
    try {
      System.out.println("Waiting for connection on port: " + serverSocket.getLocalPort() + "..");
      while (true) {
        Socket socket = serverSocket.accept();
        ClientThread eC = new ClientThread(++cnt, socket, this);
        clientList.add(eC);
        Thread eCT = new Thread(eC, "ClientThread-" + cnt);
        eCT.start();
      }
    } catch (IOException e) {
      e.printStackTrace(System.err);
      System.exit(1);
    }
  }

  /**
   * Checks if ping logging is enabled on the server.
   *
   * @return {@code true} if ping logging is enabled, {@code false} otherwise.
   */
  public boolean isPingLoggingEnabled() {
    return ENABLE_PING_LOGGING;
  }

  /**
   * Sends a message to all connected clients except the sender.
   *
   * @param str    The message to be broadcasted to all clients.
   * @param sender The client thread sending the message.
   */
  public void sendToAll(String str, ClientThread sender) {
    for (ClientThread client : clientList) {
      if (client == sender) {
        continue;
      }
      try {
        client.send(str);
      } catch (IOException e) {
        client.logout();
        clientList.remove(client);
      }
    }
  }

  /**
   * Sends a message to a client with the specified nickname.
   *
   * @param str      The message to be sent to the client.
   * @param nickname The nickname of the client to whom the message is to be sent.
   * @return true if the message was sent.
   */
  public boolean sendToNickname(String str, String nickname) {
    boolean sent = false;
    for (ClientThread client : clientList) {
      if (client.getNickname().equals(nickname)) {
        try {
          client.send(str);
          sent = true;
        } catch (IOException e) {
          // TODO bessere Fehlerabhandlung
          e.printStackTrace(System.err);
        }
      }
    }
    return sent;
  }

  /**
   * Removes a client from the server.
   *
   * @param client The client thread to be logged out.
   */
  public synchronized void removeClient(ClientThread client) {
    clientList.remove(client);
  }

  /**
   * Retrieves the list of nicknames of all connected clients.
   *
   * @return An ArrayList containing the nicknames of all connected clients.
   * @author Pascal von Fellenberg
   * @author Istref Uka
   */
  public ArrayList<String> getNicknames() {
    ArrayList<String> nicknames = new ArrayList<>();
    for (ClientThread client : clientList) {
      String clientName = client.getNickname();
      if (clientName != null && !clientName.isEmpty()) {
        nicknames.add(clientName);
      }
    }
    return nicknames;
  }

  /**
   * Gets the lobby in the list lobbies on the server with the specified lobbyNumber.
   * If no lobby with the specified lobbyNumber exists, null is returned.
   *
   * @param lobbyNumber The lobbyNumber of the lobby.
   * @return Lobby with the specified lobbyNumber if it exists, else null.
   */
  public Lobby getLobby(int lobbyNumber) {
    for (int i = 0; i < lobbies.size(); i++) {
      Lobby l = lobbies.get(i);
      if (l.lobbyNumber == lobbyNumber) {
        return l;
      }
    }
    return null;
  }

  /**
   * Lets the client join a lobby.
   *
   * @param lobby  The lobby to which the player wants to join.
   * @param client The player to be added to the lobby.
   * @return {@code true} if and only if the lobby wasn't full and the player was able to join, else {@code false}
   */
  public boolean joinLobby(Lobby lobby, ClientThread client) {
    return lobby.addPlayer(client);
  }

  /**
   * Creates a new lobby and returns the created lobby.
   * Should only be called if the lobby with the specified lobbyNumber doesn't exist yet.
   *
   * @param lobbyNumber The number of the lobby.
   * @return The lobby that was created.
   */
  public Lobby createLobby(int lobbyNumber) {
    // TODO this is only for debugging and should be removed before final build, as it is resource intensive.
    if (getLobby(lobbyNumber) != null) {
      LOGGER.error("Lobby with already existing lobbyNumber created");
    }
    Lobby l = new Lobby(lobbyNumber);
    lobbies.add(l);
    return l;
  }

}

