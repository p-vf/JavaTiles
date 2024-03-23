package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class EchoServer {
  private volatile ArrayList<EchoClientThread> clientList;
  private ServerSocket serverSocket;

  public static void main(String[] args) {
    EchoServer s = new EchoServer();
  }

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

  private void sendBroadcast(String str) {
    for (EchoClientThread client : clientList) {
      try {
        client.send(str);
      } catch (IOException e) {
        client.logout();
        clientList.remove(client);
      }
    }
  }

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

  public synchronized void logClientOut(EchoClientThread client) {
    clientList.remove(client);
  }

  public ArrayList<String> getNicknames() {
    ArrayList<String> nicknames = new ArrayList<>();
    for (EchoClientThread client : clientList) {
      if (client.nickname != null && !client.nickname.isEmpty()) {
        nicknames.add(client.nickname);
      }
    }
    return nicknames;
  }

  public void sendToNickname(String str, String nickname) {
    for (var client : clientList) {
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

