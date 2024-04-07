package client;

import java.io.*;
import java.net.Socket;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

import game.Color;
import game.Tile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import utils.NetworkUtils;

import javax.swing.*;

import static game.Tile.*;
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

  public static int playerID = 4;

  public static int CurrentPlayerID = 5;

  public static final Logger LOGGER = LogManager.getLogger(Client.class);

  public static ClientDeck yourDeck = new ClientDeck();

  public static Tile[] exchangestacks;

  private static GUIThread guiThread;

  private static boolean lobby = false;





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
      guiThread = new GUIThread(client);
      Thread gThread = new Thread(guiThread);
      gThread.start();
      InThread th = new InThread(client.in, client, guiThread);
      Thread iT = new Thread(th);
      iT.start();


      ping(client);


      String logindata = "LOGI " + login();

      client.send(logindata);


      String line = " ";
      while (true) {
        line = bReader.readLine();
        if (line.equalsIgnoreCase("QUIT")) {
          break;
        }

        String messageToSend = client.handleInput(line);
        if (messageToSend == null || messageToSend.isEmpty()) {
          continue;
        }
        LOGGER.debug("sent: " + messageToSend);
        client.send(messageToSend);

      }

      System.out.println("terminating...");
      System.exit(0);
    } catch (IOException e) {
      System.out.println("Your connection to the server has been lost");
      System.exit(0);


    }


  }


  public static String login() throws IOException {
    while (true) {
      System.out.println("Enter username:");
      String username = bReader.readLine();
      if (username.isEmpty()) {
        username = System.getProperty("user.name");
        return username;
      }
      if (username.contains(" ") || username.contains("\"")) {
        System.out.println("Your nickname mustn't contain blank spaces or quotation marks");

      } else {
        return username;
      }
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
    if (str != null) {
      out.write((str + "\r\n").getBytes());
    }
  }

  /**
   * Parses the user input and performs corresponding actions.
   *
   * @param input the input string provided by the user
   * @return a string representing the message to be sent to the server
   */
  public String handleInput(String input) throws IOException {
    try {
      String[] argumentsarray = input.split(" ");
      //LOGGER.debug(Arrays.toString(argumentsarray));
      ArrayList<String> arguments = new ArrayList<>(Arrays.asList(argumentsarray));
      String inputCommand = arguments.remove(0);
      String[] todebug = arguments.toArray(new String[0]);

      switch (inputCommand) {
        case "/nickname":
          String changedName = login();
          return encodeProtocolMessage("NAME", changedName);


        case "/chat":
          if (arguments.get(0).equals("/all")) {

            if(nickname==null){
              guiThread.updateChat("You need to login first.");
              return null;
            }
            String allMessage = arguments.get(1);

            for (int i = 2; i < arguments.size(); i++) {
              allMessage = allMessage + " " + arguments.get(i);
            }

            //LOGGER.debug(allMessage);
            String allMessageForServer = encodeProtocolMessage("CATC", "b", allMessage);
            return allMessageForServer;
          }
          if (arguments.get(0).equals("/whisper")) {
            String whisperMessage = arguments.get(2);
            for (int i = 3; i < arguments.size(); i++) {
              whisperMessage = whisperMessage + " " + arguments.get(i);
            }

            //LOGGER.debug(whisperMessage);
            String whisperMessageForServer = encodeProtocolMessage("CATC", "w", whisperMessage, arguments.get(1));
            return whisperMessageForServer;

          }

          else {
            if (lobby == true) {

              String message = arguments.get(0);

              for (int i = 1; i < arguments.size(); i++) {
                message = message + " " + arguments.get(i);
              }

              //LOGGER.debug(message);
              String messageForServer = encodeProtocolMessage("CATC", "l", message);
              return messageForServer;

            } else {
              guiThread.updateChat("You are not in a lobby right now. Please join a lobby first.");
              return null;
            }
          }



        case "/swap":

          if (arguments.get(0).matches("\\d+") && arguments.get(1).matches("\\d+") && arguments.get(2).matches("\\d+") && arguments.get(3).matches("\\d+")) {
            int row = Integer.parseInt(arguments.get(0));
            int col = Integer.parseInt(arguments.get(1));
            int row2 = Integer.parseInt(arguments.get(2));
            int col2 = Integer.parseInt(arguments.get(3));

            if ((row > 1) || (row2 > 1) || (col > 11) || (col2 > 11)) {
              System.out.println("The max indices are: row:1 and column:11");
              return null;
            } else {
              yourDeck.swap(Integer.parseInt(arguments.get(0)), col, row2, col2);
              showDeck();

              return null;
            }
          }
          else{
            System.out.println("invalid command");
            return null;
          }


        case "/logout":
          return encodeProtocolMessage("LOGO");

        case "/ready":
          if(lobby == true) {
            return encodeProtocolMessage("REDY");
          }else{
            System.out.println("You are not in a lobby right now. Please join a lobby first");
          }

        case "/joinlobby":
          if (arguments.size() > 0) {
            String number = arguments.get(0);
            try {
              int num = Integer.parseInt(number);
              return encodeProtocolMessage("JLOB", String.valueOf(num));
            } catch (NumberFormatException e) {
              return "Invalid input for lobbynumber";
            }
          } else {
            return "You must provide a number to enter a lobby";
          }


        case "/draw":
          if (CurrentPlayerID != playerID) {
            System.out.println("You can only draw on your turn");
            return null;
          }
          if (yourDeck.countTiles()>=15) {
            System.out.println("You've already drawn a tile");
            return null;
          }
          if (arguments.get(0).equals("m")) {

            return encodeProtocolMessage("DRAW", "m");

          }
          if (arguments.get(0).equals("e")) {

            return encodeProtocolMessage("DRAW", "e");
          } else {
            System.out.println("Your draw command should look like /draw m or /draw e");
            return null;
          }

        case "/putt":

          if (playerID != CurrentPlayerID) {
            System.out.println("It's currently not your turn.");
            return null;

          }
          if(yourDeck.countTiles()<15){
            System.out.println("You need to draw first.");
            return null;
          }
          if (!(arguments.get(0).matches("\\d+") && arguments.get(1).matches("\\d+"))) {
            System.out.println("The indices should be numbers.");
            return null;

          }
          int row = Integer.parseInt(arguments.get(0));
          int column = Integer.parseInt(arguments.get(1));

          if ((row > 1) || (column > 11)) {
            System.out.println("The max indices are: row:1 and column:11");
            return null;
          }

          if (yourDeck.getTile(row, column) == null) {
            System.out.println("Please choose an existing Tile.");
            return null;
          }

          Tile tileToPut = yourDeck.getTile(row, column);
          yourDeck.removeTile(row, column); //removes the Tile from the deck;
          String tileString = tileToPut.toString();
          Tile[] tileArray = yourDeck.DeckToTileArray();
          String DeckToBeSent = tileArrayToProtocolArgument(tileArray);
          showDeck();
          return encodeProtocolMessage("PUTT", tileString, DeckToBeSent);


        case "/listplayers":
          return encodeProtocolMessage("LPLA");

        case "/listlobbies":
          return encodeProtocolMessage("LLPL");

        case "/deck":
          if (yourDeck != null) {
            showDeck();
          }
          return null;

        case "/listgames":

          if(arguments.get(0).equals("o")){
          return encodeProtocolMessage("LGAM", "o");}

          if(arguments.get(0).equals("r")){
            return encodeProtocolMessage("LGAM", "r");}

          if(arguments.get(0).equals("f")){
            return encodeProtocolMessage("LGAM", "f");}




        default:
          System.out.println("invalid command");
          return null;
      }
    } catch (IndexOutOfBoundsException e) {
      System.out.println("invalid command");
      return null;
    }
  }




  /**
   * Handles incoming requests from the server and performs corresponding actions.
   *
   * @param request the request received from the server
   * @param client  the client object
   */
  public static void handleRequest(String request, Client client, GUIThread guiThread) throws IOException {
    String requestCommand;
    ArrayList<String> arguments = decodeProtocolMessage(request);
    requestCommand = arguments.remove(0);
    try {
      ServerRequest requestType = ServerRequest.valueOf(requestCommand);

      switch (requestType) {

        case CATS:
          String name = arguments.get(2);
          if(arguments.get(0).equals("b")){

            guiThread.updateChat(name +" sent to all: " + arguments.get(1));

          }
          if(arguments.get(0).equals("w")){
            guiThread.updateChat(name +" whispered: " + arguments.get(1));
          }
          if(arguments.get(0).equals("l")){
          guiThread.updateChat(name + ": " + arguments.get(1));}
          //hier handeln ob whisper broadcast etc mit case distinction


          //
          break;

        case PING:
          client.send(encodeProtocolMessage("+PING"));
          //System.out.println("+PING");
          break;

        case STRT:
          playerID = Integer.parseInt(arguments.get(1));
          ArrayList<String> tilesStrt = decodeProtocolMessage(arguments.get(0));
          if (tilesStrt.size() == 15) {
            System.out.println("It's your turn.");
            CurrentPlayerID = playerID;

          }
          Tile[] tilesArrayStrt = stringsToTileArray(tilesStrt);
          yourDeck.createDeckwithTileArray(tilesArrayStrt);
          showDeck();
          client.send(encodeProtocolMessage("+STRT"));
          break;



        case PWIN:
          System.out.println(arguments.get(0) + " won.");
          client.send(encodeProtocolMessage("+PWIN"));
          break;

        case EMPT:
          System.out.println("The game ended with a draw:");
          client.send("+EMPT");
          break;

        case STAT:
          ArrayList<String> tileList = decodeProtocolMessage(arguments.get(0));
          exchangestacks = stringsToTileArray(tileList);

          showExchangeStacks();
          if(Integer.parseInt(arguments.get(1))==playerID){
            System.out.println("It's your turn.");
            CurrentPlayerID = playerID;

          } else {
            System.out.println("It's " + arguments.get(1) + "'s turn.");
          }

          break;


        default:
          break;
      }
    } catch (IllegalArgumentException e) {
      LOGGER.error("request from server: \"" + request + "\" caused following Exception: " + e.toString());
    }
  }


  public static void handleResponse(String request, Client client, GUIThread guiThread) throws IOException {
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
          nickname = arguments.get(0);
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

          if (arguments.get(1).isEmpty()) {
            System.out.println("No lobbies with this status");
            break;
          }


          if (arguments.get(0).equals("o")) {


            String argList = arguments.get(1);
            String[] status = argList.split(" ");
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

            System.out.println("Open Lobbies");
            System.out.println("Lobbynumber: \tNumber of players:");
            for (int i = 0; i < lobbies.length; i++) {
              System.out.println(lobbies[i] + "\t\t\t\t" + players[i]);
            }
          }


          if (arguments.get(0).equals("r")) {


            String argList = arguments.get(1);
            String[] status = argList.split(",");
            String infos = String.join(":", status);
            String[] splitString = infos.split(":");
            int[] intArray = new int[splitString.length];

            for (int i = 0; i < splitString.length; i++) {
              intArray[i] = Integer.parseInt(splitString[i]);

            }


            System.out.println("Ongoing games");
            System.out.println("Lobbynumber:");
            for (int i = 0; i < intArray.length; i++) {
              System.out.println(intArray[i]);
            }
          }

          if (arguments.get(0).equals("f")) {

            String argList = arguments.get(1);
            String[] status = argList.split(",");
            String infos = String.join(":", status);
            String[] splitString = infos.split(":");
            String[] StringArray = new String[splitString.length];

            for (int i = 0; i < splitString.length; i++) {
              StringArray[i] = splitString[i];

            }

            String[] lobbies = new String[StringArray.length / 2];
            String[] winners = new String[StringArray.length / 2];

            int indexLobby = 0;
            int indexPlayer = 0;

            for (int i = 0; i < StringArray.length; i++) {
              if (i % 2 == 0) {
                lobbies[indexLobby] = StringArray[i];
                indexLobby++;
              } else {
                winners[indexPlayer] = StringArray[i];
                indexPlayer++;
              }
            }

            System.out.println("Finished games");
            System.out.println("Lobbynumber: \tWinners:");
            for (int i = 0; i < lobbies.length; i++) {
              System.out.println(lobbies[i] + "\t\t\t\t" + winners[i]);
            }
          }
          break;

        case JLOB:
          String confirmation = arguments.get(0);
          if (confirmation.equals("t")) {
            System.out.println("Joined lobby successfully");
            lobby = true;
          } else {
            System.out.println("Unsuccessful lobby connection");
          }

          break;

        case CATC:
          if(arguments.get(0).equals("l")){
            guiThread.updateChat("You:"+arguments.get(1));
          }
          if(arguments.get(0).equals("w")){
            guiThread.updateChat("You whispered to "+arguments.get(2)+": "+arguments.get(1));
          }
          if(arguments.get(0).equals("b")){
            guiThread.updateChat("You sent to all: "+arguments.get(1));
          }
          break;


        case DRAW:
          yourDeck.addTheseTiles(parseTile(arguments.get(0)));
          showDeck();
          break;

        case PUTT:
          if (arguments.get(0).equals("t")) {
            System.out.println("Valid input");

            if (arguments.get(1).equals("t")) {
              System.out.println("You won!");
            }
          } else {
            System.out.println("Stop cheating!!");
          }
          break;

        case REDY:
          System.out.println("ready to play!");
          break;

        case LPLA:
          ArrayList<String> playerList = decodeProtocolMessage(arguments.get(0));
          for (int i = 0; i < playerList.size(); i++) {
            if (!(arguments.get(0).equals("null"))) {
              System.out.println(playerList.get(i));
            }
          }
          break;

        case LLPL:
          System.out.println(getBeautifullyFormattedDecodedLobbiesWithPlayerList(arguments.get(0)));
          break;


        default:
          break;
      }
    } catch (IllegalArgumentException e) {
      LOGGER.error("response from server: \"" + request + "\" caused the following Exception: " + e.toString());
    }
  }

  private static void showDeck() {
    System.out.println("your Deck:");
    System.out.println(yourDeck.toStringPretty());
  }

  private static void showExchangeStacks() {
    StringBuilder res = new StringBuilder();
    res.append("Exchange stacks:\n");
    for (int i = 0; i < 4; i++) {
      if (i == playerID) {
        res.append("vvvv");
      } else {
        res.append("   ");
      }
    }
    res.append("\n");
    res.append("|");
    for (int i = 0; i < 4; i++) {
      if (exchangestacks[i] == null) {
        res.append("  ");
      } else {
        res.append(exchangestacks[i].toStringPretty());
      }
      res.append("|");
    }
    res.append("\n");
    for (int i = 0; i < 4; i++) {
      if (i == playerID) {
        res.append("^^^^");
      } else {
        res.append("   ");
      }
    }
    System.out.println(res.toString());
  }


  /**
   * Logs out the client from the server.
   * Closes the socket, input and output streams.
   */
  public void logout() {
    try {
      System.exit(0);
      socket.close();
      bReader.close();
      out.close();
      System.exit(0);
      System.out.println("You have been logged out.");

    } catch (IOException e) {
      System.out.println("You have been logged out.");
      System.exit(0);
    }
  }

}


