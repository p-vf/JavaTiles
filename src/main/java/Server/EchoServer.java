package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * The EchoServer class represents a simple server that listens for client connections
 * on a specified port and handles communication with multiple clients concurrently.
 */
public class EchoServer {
  private volatile ArrayList<EchoClientThread> clientList;
  private ServerSocket serverSocket;

  public static void main(String[] args) {
    EchoServer s = new EchoServer();
  }

  /**
   * Constructs a new EchoServer instance.
   * Creates a ServerSocket and listens for incoming client connections.
   */
  private EchoServer() {
    int cnt = 0;
    try {
      System.out.println("Warte auf Verbindung auf Port 8090..");
      serverSocket = new ServerSocket(8090);
      clientList = new ArrayList<>();
      while (true) {
        Socket socket = serverSocket.accept();
        EchoClientThread eC = new EchoClientThread(++cnt, socket, this);
        clientList.add(eC);
        Thread eCT = new Thread(eC);
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
  public void sendBroadcast(String str, EchoClientThread sender) {
    for (EchoClientThread client : clientList) {
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
  public synchronized void removeClient(EchoClientThread client) {
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
    for (EchoClientThread client : clientList) {
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
    for (EchoClientThread client : clientList) {
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

