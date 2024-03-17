package Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class EchoClientThread implements Runnable {
  private int name;
  private Socket socket;
  private OutputStream out;
  private InputStream in;
  private static final int PING_TIMEOUT = 15000;

  public EchoClientThread(int name, Socket socket) {
    this.name = name;
    this.socket = socket;
  }

  // for testing purposes
  public static void main(String[] args) {
    String request = "CATC t \"hallo ich bin emanuel \\\"bruh\\\"\" 3 bruh";
    System.out.println("Result: " + parseRequest(request).toString());
  }

  @Override
  public void run() {
    // TODO handle SocketTimeoutException, SocketException
    String msg = "Server.EchoServer: Verbindung " + name;

    System.out.println(msg + " hergestellt");
    try {
      socket.setSoTimeout(PING_TIMEOUT);
      in = socket.getInputStream();
      out = socket.getOutputStream();

      out.write(("cs108:" + msg + "\r\n").getBytes());
      Thread pthread = new Thread(new PingThread(out, PING_TIMEOUT - 5000));
      pthread.start();

      int c = 0;
      StringBuilder requestBuilder = new StringBuilder();

      while (c != -1) {
        c = in.read();
        requestBuilder.append((char) c);

        char last = requestBuilder.charAt(requestBuilder.length() - 1);
        char secondLast = requestBuilder.charAt(requestBuilder.length() - 2);

        if (secondLast == '\r' && last == '\n') {
          String request = requestBuilder.toString();
          handleRequest(request);
          requestBuilder.delete(0, requestBuilder.length() - 1);
        }
        System.out.write((char) c);
      }

    } catch (IOException e) {
      System.err.println(e.toString());
    }
  }

  /**
   * Diese Methode wandelt die Zeichenfolge, welche vom Client kommt in eine Array von Strings um.
   * Diese hat die gleiche Struktur wie der Parameter request, einfach dass alle Argumente einträge
   * in einer ArrayList sind.
   *
   * @param request Zeichenfolge mit im Netzwerkprotokoll definierter Form.
   * @return eine ArrayList, welche den Command und seine Argumente als String enthält.
   */
  private static ArrayList<String> parseRequest(String request) {
    char[] chars = request.toCharArray();
    // only handles a command with up to 9 arguments..
    ArrayList<String> command = new ArrayList<>();
    boolean isInsideString = false;
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < chars.length; i++) {
      switch (chars[i]) {
        case ' ':
          if (isInsideString) {
            sb.append(' ');
          } else {
            command.add(sb.toString());
            sb = new StringBuilder();
          }
          break;

        case '\\':
          if (i < chars.length - 1 && chars[i + 1] == '"' && isInsideString) {
            sb.append('"');
            i++;
          } else {
            sb.append('\\');
          }
          break;
        case '"':
          if (!isInsideString) {
            isInsideString = true;
          } else {
            isInsideString = false;
            command.add(sb.toString());
            sb = new StringBuilder();
          }
          break;
        default:
          sb.append(chars[i]);
      }
    }
    if (!sb.isEmpty()) {
      command.add(sb.toString());
    }
    return command;
  }

  private void handleRequest(String request) {
    ArrayList<String> arguments = parseRequest(request);
    Protocol command = Protocol.valueOf(arguments.remove(0));
    // TODO handle all cases
    switch (command) {
      case LOGI:
        break;
      case LOGO:
        break;
      case STAT:
        break;
      case DRAW:
        break;
      case PUTT:
        break;
      case PWIN:
        break;
      case EMPT:
        break;
      case CATC:
        break;
      case CATS:
        break;
      case PING:
        break;
      case PONG:
        break;
    }
  }
}
