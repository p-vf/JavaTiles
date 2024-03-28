package server;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.NetworkUtils;

/**
 * This class represents a thread for handling communication with a client by reading and responding to inputs.
 *
 * @author Pascal von Fellenberg
 * @author Istref Uka
 */
public class ClientThread implements Runnable {
  public static final Logger LOGGER = LogManager.getLogger();
  public int id;
  public String nickname;
  private final Server server;
  private final Socket socket;
  public OutputStream out;
  private BufferedReader bReader;
  private static final int PING_TIMEOUT = 15000;
  private final ServerPingThread pingThread;

  /**
   * Constructor of the EchoClientThread class.
   *
   * @param id The id-number of the client which is always larger than 0.
   * @param socket The socket that is used to create the connection between client and server.
   * @param server The server which gets connected to the client.
   *  */
  public ClientThread(int id, Socket socket, Server server) {
    this.id = id;
    this.socket = socket;
    this.server = server;
    try {
      InputStream in = socket.getInputStream();
      bReader = new BufferedReader(new InputStreamReader(in));
      out = socket.getOutputStream();
    } catch(IOException e) {
      e.printStackTrace(System.err);
    }

    pingThread = new ServerPingThread(this, PING_TIMEOUT);
    pingThread.setName("PingThread-" + id);
    pingThread.start();
  }

  // for testing purposes
  public static void main(String[] args) {
    String request = "CATC t \"hallo ich bin emanuel \\\"bruh\\\"\" 3 bruh";
    System.out.println("Result: " + NetworkUtils.decodeProtocolMessage(request).toString());
  }

  @Override
  public void run() {
    // TODO handle SocketTimeoutException, SocketException
    String msg = "Server: Verbindung " + id;

    LOGGER.debug(msg + " hergestellt");
    try {
      send(msg);

      while (true) {
        String request;
        try {
          request = bReader.readLine();
        } catch (IOException e) { break; }

        LOGGER.debug("received: " + request);
        if (!request.isEmpty() && request.charAt(0) == '+') { //request kann null sein, wenn es
          // keine Readline gibt, falls Client Verbindung verliert.
          handleResponse(request);
        } else {
          handleRequest(request);
        }
      }
    } catch (IOException | NullPointerException e) {
      //System.out.println("EchoClientThread with id:" + id);
      if (e instanceof SocketTimeoutException) {
        logout();
      }
      e.printStackTrace(System.err);
    }
    LOGGER.debug("Server: Verbindung " + id + " abgebrochen");
  }

  /**
   * Handles a Response as documented in the protocol.
   *
   * @param response Represents a response to a previously sent request, must start with a "+".
   */
  private void handleResponse(String response) {
    ArrayList<String> arguments = NetworkUtils.decodeProtocolMessage(response);
    String cmdStr = arguments.remove(0);
    cmdStr = cmdStr.substring(1);
    // TODO add log, if cmdStr is not of size 4
    Protocol.Response command = Protocol.Response.valueOf(cmdStr);
    switch (command) {
      case PWIN -> {}
      case EMPT -> {}
      case CATS -> {}
      case PING -> {
        synchronized (pingThread) {
          pingThread.receivedResponse = true;
        }
      }
    }
  }


  /**
   * Reads a string and returns the boolean value it represents.
   *
   * @param flag Should be either "f" or "t".
   * @return The value represented by the string.
   * @throws IllegalArgumentException
   */

  private static boolean readFlag(String flag) throws IllegalArgumentException {
    boolean whisper = false;
    if (flag.equals("t")) {
      whisper = true;
    } else if (!flag.equals("f")) {
      // Falls flag weder "t" noch "f" ist, wird whisper auf false gesetzt
      // Alternativ könnte man auch eine Meldung ausgeben oder andere Aktionen ausführen
      whisper = false;
    }
    return whisper;
  }

  /**
   * Handles a request as described in the protocol.
   *
   * @param request The request being handled. It must have the form defined in the protocol.
   * @throws IOException
   */

  private void handleRequest(String request) throws IOException {
    try{
      ArrayList<String> arguments = NetworkUtils.decodeProtocolMessage(request);
      // TODO change this so that incorrect input gets handled
      Protocol.Request command = Protocol.Request.valueOf(arguments.remove(0));
      // TODO handle all cases
      switch (command) {
        case LOGI -> {
          login(arguments.get(0));
        }
        case LOGO -> {
          send("+LOGO");
          logout();
        }
        case STAT -> {}
        case DRAW -> {}
        case PUTT -> {}
        case CATC -> {
          handleChat(arguments);
          send("+CATC");
        }
        case PING -> send("+PING");
        case NAME -> {
          changeName(arguments.get(0));
          send("+NAME " + nickname);
        }
        case LLOB -> {}
        case JLOB -> {}
      }
    }
    catch(IndexOutOfBoundsException | NumberFormatException e){
      send("fehlerhafte Eingabe");
    }
  }

  /**
   * Ends the connection to the client.
   * This method should be called when a connection interruption is detected or when the client wants to log out.
   */

  public void logout() {
    try {
      server.removeClient(this);
      socket.close();
      bReader.close();
      out.close();
    } catch (IOException e) {
      e.printStackTrace(System.err);
    }
  }

  /**
   * Sends a string to the client.
   * This string is supplemented with carriage return and line feed ("\r\n").
   *
   * @param str String to be sent to the client.
   * @throws IOException Thrown if the OutputStream throws an IOException.
   */
  public synchronized void send(String str) throws IOException {
    out.write((str + "\r\n").getBytes());
    LOGGER.debug("sent: " + str);
  }

  /**
   * Logs the player in with a given nickname
   *
   * @param newNickname The new nickname of the player.
   * @throws IOException If an I/O error occurs while logging in.
   */
  private void login(String newNickname) throws IOException {
    // TODO maybe don't send "+LOGI ..." here but inside the switch statement?
    changeName(newNickname);
    send("+LOGI " + this.nickname);
  }


  /**
   * Changes the player's name to the given name if it does not already exist.
   * Otherwise, the name is modified so that it is unique on the server.
   *
   * @param newNickname The name to which the nickname should be changed.
   * @return The nickname received by the client.
   */
  private void changeName(String newNickname) {
    ArrayList<String> names = server.getNicknames();
    String actualNickname = newNickname;
    int counter = 1;

    // Solange ein Duplikat gefunden wird, erhöhe den Zähler und versuche es erneut
    while (names.contains(actualNickname)) {
      actualNickname = newNickname + "_" + counter;
      counter++;
    }
    nickname = actualNickname;
  }

  /**
   * Handles a chat request by either sending it to everyone or just to one specific person.
   *
   * @param arguments The arguments of the chat request.
   */
  private void handleChat(ArrayList<String> arguments){
    String w = arguments.get(0); // whisper flag
    String msg = arguments.get(1); // chat-message
    String sender = nickname;
    boolean whisper = readFlag(w);
    // TODO implement function that takes care of making a valid sendable command (\r\n, format, etc.)
    //  and notifies the clientthread that there will be a +CATS response from the client if successfull.
    String cmd = "CATS " + w + " \"" + msg + "\" " + sender;

    if (whisper) {
      server.sendToNickname(cmd, arguments.get(2));
    } else {
      server.sendBroadcast(cmd, this);
    }

  }
}
