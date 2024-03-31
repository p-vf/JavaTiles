package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

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
   * Gets the index in the list lobbies on the server of the lobby with the specified lobbyNumber.
   * If no lobby with the specified lobbyNumber exists, -1 is returned.
   * @param lobbyNumber The number of the lobby.
   * @return The index in the list of lobbies on the server that corresponds to the lobby with the specified lobbyNumber.
   *  If no such lobby exists -1.
   */
  public int lobbyIndex(int lobbyNumber) {
    for (int i = 0; i < lobbies.size(); i++) {
      if (lobbies.get(i).lobbyNumber == lobbyNumber) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Join the lobby with the specified lobbyIndex.
   * @param lobbyIndex The index corresponding to the lobby to which the player wants to join.
   * @param client The player to be added to the lobby.
   * @return {@code true} if and only if the lobby wasn't full and the player was able to join, else {@code false}
   */
  public boolean joinLobby(int lobbyIndex, ClientThread client) {
    return lobbies.get(lobbyIndex).addPlayer(client);
  }

  /**
   * Creates a new lobby and returns the index of the lobby.
   * @param lobbyNumber The number of the lobby.
   * @return index at which the lobby is created.
   */
  public int createLobby(int lobbyNumber) {
    lobbies.add(new Lobby(lobbyNumber));
    return lobbies.size() - 1;
  }
}

