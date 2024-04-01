package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * The EchoServer class represents a simple server that listens for client connections
 * on a specified port and handles communication with multiple clients concurrently.
 */
public class Server {
  public static final Logger LOGGER = LogManager.getLogger();
  public static boolean ENABLE_PING_LOGGING = false;
  private volatile ArrayList<ClientThread> clientList;
  private ServerSocket serverSocket;
  public final ArrayList<Lobby> lobbies = new ArrayList<>();

  public static void main(String[] args) {
    if (args.length < 1) {
      LOGGER.fatal("No port number given.");
    }
    if (args.length == 2) {
      ENABLE_PING_LOGGING = Boolean.parseBoolean(args[1]);
    }
    LOGGER.info("Logging level: " + LOGGER.getLevel().toString());
    Server s = new Server(Integer.parseInt(args[0]));
  }

  /**
   * Constructs a new EchoServer instance.
   * Creates a ServerSocket and listens for incoming client connections.
   */
  private Server(int port) {
    int cnt = 0;
    try {
      System.out.println("Warte auf Verbindung auf Port " + port + "..");
      serverSocket = new ServerSocket(port);
      clientList = new ArrayList<>();
      while (true) {
        Socket socket = serverSocket.accept();
        ClientThread eC = new ClientThread(++cnt, socket, this);
        clientList.add(eC);
        Thread eCT = new Thread(eC, "ClientThread-" + cnt);
        eCT.start();
      }
    } catch (IOException e) {
      System.err.println(e.toString());
      System.exit(1);
    }
  }

  /**
   * Sends a message to all connected clients except the sender.
   * @param str The message to be broadcasted to all clients.
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
   * @param str The message to be sent to the client.
   * @param nickname The nickname of the client to whom the message is to be sent.
   */
  public void sendToNickname(String str, String nickname) {
    for (ClientThread client : clientList) {
      if (client.nickname.equals(nickname)) {
        try {
          client.send(str);
        } catch (IOException e) {
          // TODO bessere Fehlerabhandlung
          e.printStackTrace(System.err);
        }
      }
    }
  }

  /**
   * Removes a client from the server.
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
      if (client.nickname != null && !client.nickname.isEmpty()) {
        nicknames.add(client.nickname);
      }
    }
    return nicknames;
  }

  /**
   * Gets the lobby in the list lobbies on the server with the specified lobbyNumber.
   * If no lobby with the specified lobbyNumber exists, null is returned.
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
   * Let the client join a lobby.
   * @param lobby The lobby to which the player wants to join.
   * @param client The player to be added to the lobby.
   * @return {@code true} if and only if the lobby wasn't full and the player was able to join, else {@code false}
   */
  public boolean joinLobby(Lobby lobby, ClientThread client) {
    return lobby.addPlayer(client);
  }

  /**
   * Creates a new lobby and returns the created lobby.
   * Should only be called if the lobby with the specified lobbyNumber doesn't exist yet.
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

