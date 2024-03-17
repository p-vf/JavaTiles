package Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class EchoClientThread implements Runnable {
  private int name;
  private Socket socket;
  public EchoClientThread(int name, Socket socket) {
    this.name = name;
    this.socket = socket;
  }
  @Override
  public void run() {
    String msg = "Server.EchoServer: Verbindung " + name;
    System.out.println(msg + " hergestellt");
    try {
      InputStream in = socket.getInputStream();
      OutputStream out = socket.getOutputStream();
      out.write(("cs108:"+msg+"\r\n").getBytes());
      int c;
      while ((c = in.read()) != -1) {
        out.write((char) c);
        System.out.write((char) c);
      }
    } catch (IOException e) {
      System.err.println(e.toString());
    }
  }
}
