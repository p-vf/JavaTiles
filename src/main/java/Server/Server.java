package Server;

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
  private volatile ArrayList<ClientThread> clientList;
  private ServerSocket serverSocket;

  public static void main(String[] args) {
    if (args.length < 1) {
      LOGGER.fatal("No port number given.");
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
  public void sendBroadcast(String str, ClientThread sender) {
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
}

