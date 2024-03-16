package Server;

import Server.EchoClientThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {
  public static void main(String[] args) {
    int cnt = 0;
    try {
      System.out.println("Warte auf Verbindung auf Port 8090..");
      ServerSocket echod = new ServerSocket(8090);
      while (true) {
        Socket socket = echod.accept();
        EchoClientThread eC = new EchoClientThread(++cnt, socket);
        Thread eCT = new Thread(eC);
        eCT.start();
      }
    } catch (IOException e) {
      System.err.println(e.toString());
      System.exit(1);
    }
  }
}
