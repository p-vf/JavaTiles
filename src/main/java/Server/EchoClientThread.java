package Server;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

/**
 * This class represents a thread for handling communication with a client in the EchoServer.
 */
public class EchoClientThread implements Runnable {
  private int id;
  public String nickname;
  private final EchoServer server;
  private final Socket socket;
  public OutputStream out;
  private BufferedReader bReader;
  private static final int PING_TIMEOUT = 15000;
  private Thread pingThread;

  /**
   * Constructor of the EchoClientThread class.
   *
   * @param id The id-number of the client which is always larger than 0.
   * @param socket The socket that is used to create the connection between client and server.
   * @param server The server which gets connected to the client.
   *  */
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
      //socket.setSoTimeout(PING_TIMEOUT);
      InputStream in = socket.getInputStream();
      bReader = new BufferedReader(new InputStreamReader(in));

      out = socket.getOutputStream();
      send(msg);

      pingThread = new Thread(new PingThread(this, PING_TIMEOUT - 5000));
      pingThread.start();

      while (true) {
        String request;
        try {
          request = bReader.readLine();
        } catch (IOException e) { break; }

        System.out.println("Received: " + request);
        if (request.charAt(0) == '+') {
          handleResponse(request);
        } else {
          handleRequest(request);
        }
      }
    } catch (IOException e) {
      //System.out.println("EchoClientThread with id:" + id);
      if (e instanceof SocketTimeoutException) {
        logout();
      }
      e.printStackTrace(System.err);
    }
    System.out.println("Server: Verbindung " + id + " abgebrochen");
  }

  /**
   * Behandelt eine Response, wie im Protokoll festgehalten.
   * @param response stellt eine Antwort auf eine vorher gesendete Request dar, muss mit einem "+" anfangen
   */
  private void handleResponse(String response) {
    ArrayList<String> arguments = parseRequest(response);
    String cmdStr = arguments.remove(0);
    cmdStr = cmdStr.substring(1);
    // TODO add log, if cmdStr is not of size 4
    ProtocolResponse command = ProtocolResponse.valueOf(cmdStr);
    switch (command) {
      case PWIN -> {}
      case EMPT -> {}
      case CATS -> {}
      case PING -> {
        pingThread.notify();
      }
    }
  }

  /**
   * Diese Methode wandelt die Zeichenfolge, welche vom Client kommt, in ein Array von Strings um.
   * Diese hat die gleiche Struktur wie der Parameter request, einfach, dass alle Argumente Einträge
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

  /**
   * Liest eine String, und gibt den booleschen Wert zurück, den sie darstellt.
   * @param flag sollte entweder "f" oder "t" sein.
   * @return den Wert, den die String repräsentiert
   * @throws IllegalArgumentException
   */
  private static boolean readFlag(String flag) throws IllegalArgumentException {
    boolean whisper = false;
    if (flag.equals("t")) {
      whisper = true;
    } else if (flag.equals("f")) {
      //whisper = false;
    } else {
      // TODO sollte nicht so abgehandelt werden..
      throw new IllegalArgumentException("flag is neither \"t\" nor \"f\"\n");
    }
    return whisper;
  }

  /**
   * Behandelt eine Request, wie im Protokoll beschrieben.
   * @param request die Request, welche behandelt wird. Sie muss die Form wie im Protokoll definiert haben.
   * @throws IOException
   */
  private void handleRequest(String request) throws IOException {
    try{
      ArrayList<String> arguments = parseRequest(request);
      // TODO change this so that incorrect input gets handled
      ProtocolRequest command = ProtocolRequest.valueOf(arguments.remove(0));
      // TODO handle all cases
      switch (command) {
        case LOGI -> {
          login(Integer.parseInt(arguments.get(0)), arguments.get(1));
        }
        case LOGO -> {
          send("+LOGO");
          logout();
        }
        case STAT -> {}
        case DRAW -> {}
        case PUTT -> {}
        case PWIN -> {}
        case EMPT -> {}
        case CATC -> {
          String w = arguments.get(0); // whisper flag
          String msg = arguments.get(1); // chat-message
          String sender = nickname;
          boolean whisper = readFlag(w);
          // TODO implement function that takes care of making a valid sendable command (\r\n, format, etc.)
          //  and notifies the clientthread that there will be a +CATS response from the client if successfull.
          String cmd = "CATS " + w + " \"" + msg + "\" " + sender;

          // TODO this whisper functionality doesn't work yet
          if (whisper) {
            server.sendToNickname(cmd, arguments.get(2));
          } else {
            server.sendBroadcast(cmd, this);
          }
          send("+CATC");
        }
        case CATS -> {}
        case PING -> send("+PING");
        case NAME -> {
          send("+NAME" + changeName(arguments.get(0)));
        }
        default -> {}
      }
    }
    catch(IndexOutOfBoundsException e){
      send("fehlerhafte Eingabe");
    }
  }

  /**
   * Diese Methode endet die Verbindung zum Client.
   * Sie sollte aufgerufen werden, wenn ein Verbindungsunterbruch
   * erkennt wurde, oder sich der Client ausloggen will.
   */
  public void logout() {
    try {
      server.logClientOut(this);
      socket.close();
      bReader.close();
      out.close();
    } catch (IOException e) {
      e.printStackTrace(System.err);
    }
  }

  /**
   * Diese Methode schickt eine Zeichenfolge zum Client.
   * Diese Zeichenfolge wird mit carriage-return und line-feed ergänzt ("\r\n")
   *
   * @param str Zeichenfolge, welche zum Client geschickt wird
   * @throws IOException Wird geworfen, wenn der OutputStream eine IOException wirft
   */
  public synchronized void send(String str) throws IOException {
    out.write((str + "\r\n").getBytes());
  }

  private void login(int lobbyNum, String nickname) throws IOException {
    // TODO put player into a lobby

    // TODO maybe don't send "+LOGI ..." here but inside the switch statement?
    send("+LOGI " + changeName(nickname));
  }

  /**
   * Diese Methode ändert den Namen des Spielers zum gegebenen Namen, falls dieser noch nicht existiert.
   * Ansonsten wird der Name so abgeändert, dass dieser auf dem Server eindeutig ist.
   *
   * @param newNickname Name, zu welchen der nickname geändert werden soll
   * @return den nickname, der der Client erhalten hat.
   */
  private String changeName(String newNickname) {
    ArrayList<String> names = server.getNicknames();
    String actualNickname = newNickname;
    int counter = 1;

    // Solange ein Duplikat gefunden wird, erhöhe den Zähler und versuche es erneut
    while (names.contains(actualNickname)) {
      actualNickname = newNickname + "_" + counter;
      counter++;
    }
    return actualNickname;
  }
}
