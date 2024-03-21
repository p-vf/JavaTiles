package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class EchoClientThread implements Runnable {
  private int id;
  public String nickname;
  private final EchoServer server;
  private final Socket socket;
  public SyncOutputStreamHandler syncOut;
  private InputStream in;
  private static final int PING_TIMEOUT = 15000;

  public EchoClientThread(int id, Socket socket, EchoServer server) {
    this.id = id;
    this.socket = socket;
    this.server = server;
  }

  // for testing purposes
  public static void main(String[] args) {
    String request = "CATC t \"hallo ich bin emanuel \\\"bruh\\\"\" 3 bruh";
    System.out.println("Result: " + parseRequest(request).toString());
  }

  @Override
  public void run() {
    // TODO handle SocketTimeoutException, SocketException
    String msg = "Server: Verbindung " + id;

    System.out.println(msg + " hergestellt");
    try {
      socket.setSoTimeout(PING_TIMEOUT);
      in = socket.getInputStream();
      syncOut = new SyncOutputStreamHandler(socket.getOutputStream());
      syncOut.writeData((msg + "\r\n").getBytes());

      Thread pthread = new Thread(new PingThread(syncOut, PING_TIMEOUT - 5000));
      pthread.start();

      BufferedReader bReader = new BufferedReader(new InputStreamReader(in));

      while(true){
        String request = bReader.readLine();
        handleRequest(request);
        System.out.println("Recieved: " + request);
      }
    } catch (IOException e) {
      //System.out.println("EchoClientThread with id:" + id);
      if (e instanceof SocketTimeoutException) {
        logout();
      }
      e.printStackTrace(System.err);
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

  private static boolean readFlag(String flag) {
    boolean whisper = false;
    if (flag.equals("t")) {
      whisper = true;
    } else if (flag.equals("f")) {
      //whisper = false;
    } else {
      // TODO sollte nicht so abgehandelt werden..
      throw new Error("flag is neither \"t\" nor \"f\"\n");
    }
    return whisper;
  }
  private void handleRequest(String request) throws IOException {
    try{
    ArrayList<String> arguments = parseRequest(request);
    Protocol command = Protocol.valueOf(arguments.remove(0));
    // TODO handle all cases
    switch (command) {
      case LOGI:
        String newNickname = arguments.get(1);
        ArrayList<String> names = server.getNicknames();

        for (int i = 0; i < names.size(); i++) {
          if(newNickname.equals(names.get(i))){
            newNickname += "_";
            i = 0;
          }
        }

        nickname = newNickname;
        syncOut.writeData(("+LOGI " + nickname + "\r\n").getBytes());
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
        String w = arguments.get(0); // whisper flag
        String msg = arguments.get(1); // chat-message
        String sender = nickname;
        boolean whisper = readFlag(w);
        // TODO implement function that takes care of making a valid sendable command (\r\n, format, etc.)
        //  and notifies the clientthread that there will be a +CATS response from the client if successfull.
        String cmd = "CATS " + w + " \"" + msg + "\" " + sender + "\r\n";
        if (whisper) {
          server.sendMessageToNickname(cmd.getBytes(), arguments.get(2));
        } else {
          server.broadcastMessage(cmd.getBytes(), this);
        }
        syncOut.writeData("+CATC\r\n".getBytes());
        break;
      case CATS:
        break;
      case PING:
        break;
      case PONG:
        break;
      default:
        break;
    }}
    catch(IndexOutOfBoundsException e){
      syncOut.writeData("fehlerhafte Eingabe \r\n".getBytes());
    }
  }

  public void logout() {
    try {
      server.logClientOut(this);
      socket.close();
      in.close();
      syncOut.close();
    } catch (IOException e) {
      e.printStackTrace(System.err);
    }
  }
}
