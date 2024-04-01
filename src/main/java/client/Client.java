package client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import utils.NetworkUtils;

import static utils.NetworkUtils.*;
import static utils.NetworkUtils.Protocol.ClientRequest;
import static utils.NetworkUtils.Protocol.ServerRequest;


/**
 * The EchoClient class represents a client in our application.
 * It connects to a server and allows users to send Strings and perform actions.
 * This class handles input/output operations and communication with the server.
 *
 * @author Boran Gökcen
 * @author Robin Gökcen
 * @author Pascal von Fellenberg
 */
public class Client {

  private final Socket socket; // The socket for communication with the server

  public final OutputStream out; // Output stream to send messages to the server
  private final InputStream in; // Input stream to receive messages from the server
  private static final BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
  // Buffered reader for user input

  private Thread pingThread;// Thread responsible for sending periodic PING messages to the server.

  private static String nickname; // Nickname of the player

  public static final Logger LOGGER = LogManager.getLogger();


  /**
   * Constructs a new EchoClient with the given socket.
   *
   * @param socket the socket for communication with the server
   * @throws IOException if an I/O error occurs when creating the client
   */
  public Client(Socket socket) throws IOException {
    this.socket = socket;
    this.out = socket.getOutputStream();
    this.in = socket.getInputStream();
  }


  /**
   * The main method to start the client.
   * It connects to the server, sets up input/output streams,
   * handles user input, and communicates with the server.
   *
   * @param args the command-line arguments to specify the server's hostname and port
   */
  public static void main(String[] args) {
    try {
      // TODO man muss beim Start dieser Funktion den Nicknamen als optionalen Parameter angeben können (in args[2])
      Socket sock = new Socket(args[0], Integer.parseInt(args[1]));
      Client client = new Client(sock);
      InThread th = new InThread(client.in, client);
      Thread iT = new Thread(th);
      iT.start();

      ping(client);

      LoginClient login = new LoginClient();
      nickname = login.setUsername();
      String logindata = "LOGI " + nickname;

      client.send(logindata);


      String line = " ";
      while (true) {
        line = bReader.readLine();
        if (line.equalsIgnoreCase("QUIT")) {
          break;
        }


        client.send(handleInput(line, client));

      }

      System.out.println("terminating...");
      client.in.close();
      client.out.close();
      client.socket.close();
    } catch (IOException e) {

      System.out.println("Your connection to the server has been lost");

    }


  }

  /**
   * Initiates a new PingThread for the specified EchoClient.
   * This method creates a new thread responsible for sending periodic PING messages
   * to the server to check for responsiveness.
   *
   * @param client the EchoClient for which to start the ping thread
   */
  public static void ping(Client client) {
    client.pingThread = new ClientPingThread(client, 10000);
    client.pingThread.start();
  }


  /**
   * Sends a message to the server.
   *
   * @param str the message to send
   * @throws IOException if an I/O error occurs while sending the encoded message
   */
  public synchronized void send(String str) throws IOException {
    out.write((str + "\r\n").getBytes());
  }

  /**
   * Parses the user input and performs corresponding actions.
   *
   * @param input  the input string provided by the user
   * @param client the client object
   * @return a string representing the message to be sent to the server
   */
  public static String handleInput(String input, Client client) {
    String[] argumentsarray = input.split(" ");
    //LOGGER.debug(Arrays.toString(argumentsarray));
    ArrayList<String> arguments = new ArrayList<>(Arrays.asList(argumentsarray));
    String inputCommand = arguments.remove(0);
    String[] todebug = arguments.toArray(new String[0]);
    //LOGGER.debug(Arrays.toString(todebug));
    switch (inputCommand) {
      case "/nickname":
        String changedName = arguments.get(0);
        nickname = changedName;

        return encodeProtocolMessage("NAME", changedName);

      case "/all":
        String allMessage = arguments.get(0);

        for (int i = 1; i < arguments.size(); i++) {
          allMessage = allMessage + " " + arguments.get(i);
        }

        //LOGGER.debug(allMessage);
        String allMessageForServer = encodeProtocolMessage("CATC", "b", allMessage);
        LOGGER.debug(allMessageForServer);
        return allMessageForServer;

      case "/lobby":
        String message = arguments.get(0);

        for (int i = 1; i < arguments.size(); i++) {
          message = message + " " + arguments.get(i);
        }

        //LOGGER.debug(message);
        String messageForServer = encodeProtocolMessage("CATC", "l", message);
        LOGGER.debug(messageForServer);
        return messageForServer;


      case "/whisper":
        String whisperMessage = "(whispered) " + arguments.get(1);
        for (int i = 2; i < arguments.size(); i++) {
          whisperMessage = whisperMessage + " " + arguments.get(i);
        }

        //LOGGER.debug(whisperMessage);
        String whisperMessageForServer = encodeProtocolMessage("CATC", "w", whisperMessage, arguments.get(0));
        LOGGER.debug(whisperMessageForServer);
        return whisperMessageForServer;

      case "/logout":
        return encodeProtocolMessage("LOGO");

      default:
        return input; //just for debug
    }

  }

  /**
   * Handles incoming requests from the server and performs corresponding actions.
   *
   * @param request the request received from the server
   * @param client  the client object
   */
  public static void handleRequest(String request, Client client) throws IOException {
    String requestCommand;
    ArrayList<String> arguments = decodeProtocolMessage(request);
    requestCommand = arguments.remove(0);
    try {
      ServerRequest requestType = ServerRequest.valueOf(requestCommand);

      switch (requestType) {

        case CATS:
          String name = arguments.get(2);
          System.out.println(name + ": " + arguments.get(1));
          break;

        case PING:
          client.send(encodeProtocolMessage("+PING"));
          //System.out.println("+PING");
          break;

        case PWIN:
          break;

        case EMPT:
          break;

        default:
          break;
      }
    } catch (IllegalArgumentException e) {
      LOGGER.error("Request vom Server: \"" + request + "\" verursachte folgende Exception: " + e.toString());
    }
  }


  public static void handleResponse(String request, Client client) throws IOException {
    try {
      ArrayList<String> arguments = decodeProtocolMessage(request);
      String responsecommand = arguments.remove(0);
      String requestWithoutPlus = responsecommand.substring(1);
      ClientRequest responseType = ClientRequest.valueOf(requestWithoutPlus);
      switch (responseType) {

        case PING:
          synchronized (client.pingThread) {
            client.pingThread.notify();
            //System.out.println("PING");
          }
          break;

        case LOGI:
          System.out.println("You have been logged in as: " + arguments.get(0));
          break;

        case NAME:
          nickname = arguments.get(0);
          System.out.println("Your nickname has been changed to: " + nickname);
          break;

        case LOGO:
          System.out.println("You have been logged out.");
          client.logout();
          break;

        case LGAM:
          // TODO lobbies aren't given as multiple arguments, they are all in one argument (the second) so this implementation is wrong.
          //  If there are no lobbies with the requested status, an empty string is sent from the server, which causes an error here.
          if (arguments.get(0).equals("o")) {

            arguments.remove(0);
            String argList = String.join(" ", arguments);
            String[] status = argList.split(",");
            String infos = String.join(":", status);
            String[] splitString = infos.split(":");
            int[] intArray = new int[splitString.length];

            for (int i = 0; i < splitString.length; i++) {
              intArray[i] = Integer.parseInt(splitString[i]);

            }

            int[] lobbies = new int[intArray.length / 2];
            int[] players = new int[intArray.length / 2];

            int indexLobby = 0;
            int indexPlayer = 0;

            for (int i = 0; i < intArray.length; i++) {
              if (i % 2 == 0) {
                lobbies[indexLobby] = intArray[i];
                indexLobby++;
              } else {
                players[indexPlayer] = intArray[i];
                indexPlayer++;
              }
            }

            System.out.println("Offene Lobbies");
            System.out.println("Lobbynummer: \tAnzahl Spieler:");
            for (int i = 0; i < lobbies.length; i++) {
              System.out.println(lobbies[i] + "\t\t\t\t" + players[i]);
            }
          }


          if (arguments.get(0).equals("r")) {

            arguments.remove(0);
            String argList = String.join(" ", arguments);
            String[] status = argList.split(",");
            String infos = String.join(":", status);
            String[] splitString = infos.split(":");
            int[] intArray = new int[splitString.length];

            for (int i = 0; i < splitString.length; i++) {
              intArray[i] = Integer.parseInt(splitString[i]);

            }

            int[] lobbies = new int[intArray.length / 2];
            int[] players = new int[intArray.length / 2];

            int indexLobby = 0;
            int indexPlayer = 0;

            for (int i = 0; i < intArray.length; i++) {
              if (i % 2 == 0) {
                lobbies[indexLobby] = intArray[i];
                indexLobby++;
              } else {
                players[indexPlayer] = intArray[i];
                indexPlayer++;
              }
            }

            System.out.println("Laufende Spiele");
            System.out.println("Lobbynummer: \tAnzahl Spieler:");
            for (int i = 0; i < lobbies.length; i++) {
              System.out.println(lobbies[i] + "\t\t\t\t" + players[i]);
            }
          }

          if (arguments.get(0).equals("f")) {
            arguments.remove(0);
            String argList = String.join(" ", arguments);
            String[] status = argList.split(",");
            String infos = String.join(":", status);
            String[] splitString = infos.split(":");
            int[] intArray = new int[splitString.length];

            for (int i = 0; i < splitString.length; i++) {
              intArray[i] = Integer.parseInt(splitString[i]);

            }

            int[] lobbies = new int[intArray.length / 2];

            int indexLobby = 0;

            for (int i = 0; i < intArray.length; i++) {
              if (i % 2 == 0) {
                lobbies[indexLobby] = intArray[i];
                indexLobby++;
              }
            }

            System.out.println("Beendete Spiele");
            System.out.println("Lobbynummer: \tGewinner:");
            for (int i = 0; i < lobbies.length; i++) {
              System.out.println(lobbies[i] + "\t\t\t\t");
            }
          }
          break;

        case JLOB:
          break;

        case CATC:
          break;

        case STAT:
          break;

        case DRAW:
          break;

        case PUTT:
          break;

        default:
          break;
      }
    } catch (IllegalArgumentException e) {
      LOGGER.error("Response vom Server: \"" + request + "\" verursachte folgende Exception: " + e.toString());
    }
  }


  /**
   * Logs out the client from the server.
   * Closes the socket, input and output streams.
   */
  public void logout() {
    try {
      socket.close();
      bReader.close();
      out.close();
      System.out.println("You have been logged out.");
    } catch (IOException e) {
      System.out.println("You have been logged out.");
      System.exit(0);
    }
  }

}


