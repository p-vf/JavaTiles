package server;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import utils.NetworkUtils;
import static utils.NetworkUtils.Protocol;
import static utils.NetworkUtils.encodeProtocolMessage;
import static utils.NetworkUtils.Protocol.ClientRequest;
import static utils.NetworkUtils.Protocol.ServerRequest;

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
  private Lobby lobby;
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

    LOGGER.info("Server: Verbindung " + id + " hergestellt");
    try {
      //send(msg);

      while (true) {
        String request;
        try {
          request = bReader.readLine();
        } catch (IOException e) { break; }

        if(!request.equals("PING") && !request.equals("+PING") || Server.ENABLE_PING_LOGGING) {
          LOGGER.debug("received: " + request);
        }
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
    LOGGER.info("Server: Verbindung " + id + " abgebrochen");
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
    ServerRequest command = ServerRequest.valueOf(cmdStr);
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
    ArrayList<String> arguments = NetworkUtils.decodeProtocolMessage(request);
    String cmdStr = arguments.remove(0);
    // TODO use encodeProtocolMessage()
    try{
      // TODO change this so that incorrect input gets handled
      ClientRequest command = ClientRequest.valueOf(cmdStr);
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
          send(encodeProtocolMessage("+NAME", nickname));
        }
        case LGAM -> {}
        case LLPL -> {
          send(encodeProtocolMessage("+LLPL", NetworkUtils.getEncodedLobbiesWithPlayerList(server.lobbies)));
        }
        case LPLA -> {
        }
        case JLOB -> {
          if (lobby != null) {
            send(encodeProtocolMessage("+JLOB", "f", "Already in Lobby " + lobby.lobbyNumber));
            break;
          }
          int lobbyNumber = Integer.parseInt(arguments.get(0));
          boolean createdNewLobby = false;
          synchronized (server.lobbies) {
            int lobbyIndex = server.lobbyIndex(lobbyNumber);
            if (lobbyIndex == -1) {
              lobbyIndex = server.createLobby(lobbyNumber);
              createdNewLobby = true;
            }
            if (server.joinLobby(lobbyIndex, this)) {
              lobby = server.lobbies.get(lobbyIndex);
              send(encodeProtocolMessage("+JLOB", "t", (createdNewLobby ? "Created new Lobby " : "Joined existing Lobby ") + lobby.lobbyNumber));
            } else {
              send(encodeProtocolMessage("+JLOB", "f", "Lobby " + lobby.lobbyNumber + " full already, couldn't join"));
            }
          }
        }
      }
    }
    catch(IndexOutOfBoundsException | IllegalArgumentException e){
      // in an ideal world, this line should never be reached:
      LOGGER.error("Nachricht vom Client: \"" + request + "\" verursachte folgende Exception: " + e.toString());
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
    if (!str.equals("PING") && !str.equals("+PING") || Server.ENABLE_PING_LOGGING) {
      LOGGER.debug("sent: " + str);
    }
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
    send(encodeProtocolMessage("+LOGI", this.nickname));
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
  private void handleChat(ArrayList<String> arguments) {
    String messageType = arguments.get(0);
    String msg = arguments.get(1);
    String sender = nickname;
    String cmd = encodeProtocolMessage("CATS", messageType, msg, sender);
    switch (messageType) {
      case "b" -> {
        server.sendToAll(cmd, this);
      }
      case "l" -> {
        lobby.sendToLobby(cmd, this);
      }
      case "w" -> {
        server.sendToNickname(cmd, arguments.get(2));
      }
      default -> {
        throw new IllegalArgumentException();
      }
    }
  }
}
