package client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import client.Clientprotocol.*;

import static utils.NetworkUtils.*;


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
  public Client(Socket socket) throws IOException{
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


        client.send(handleInput(line,client));

      }

      System.out.println("terminating...");
      client.in.close();
      client.out.close();
      client.socket.close();
    }
    catch (IOException e){

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
  public static void ping(Client client){
    client.pingThread = new ClientPingThread(client, 10000);
    client.pingThread.start();
  }

  /**
   * Parses a request string into individual command arguments.
   * This method splits the input request string into separate arguments based on spaces,
   * while respecting quoted strings and escape characters
   *
   * @param request The request string to parse
   * @return An ArrayList containing individual command arguments extracted from the request string
   */
    private static ArrayList<String> parseRequest(String request) {
      char[] chars = request.toCharArray();
      ArrayList<String> command = new ArrayList<>();
      boolean isInsideString = false;
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < chars.length; i++) {
        switch (chars[i]) {
          case ' ':
            if (isInsideString) {
              sb.append(' ');
            } else {
              String currentArg = sb.toString().trim(); // Hier wird das Arg getrimmt
              if (!currentArg.isEmpty()) { // Check if the argument is not empty
                command.add(currentArg);
              }
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
              command.add(sb.toString().trim()); // Trim the argument
              sb = new StringBuilder();
            }
            break;
          default:
            sb.append(chars[i]);
        }
      }
      if (!sb.isEmpty()) {
        command.add(sb.toString().trim()); // Trim the argument
      }
      return command;
    }

  private static ArrayList<String> parseInput(String request) {
    ArrayList<String> command = new ArrayList<>();
    StringBuilder sb = new StringBuilder();
    boolean inQuotes = false;

    for (int i = 0; i < request.length(); i++) {
      char c = request.charAt(i);

      if (c == '"') {
        inQuotes = !inQuotes;
        if (!inQuotes) {
          command.add(sb.insert(0, '"').append('"').toString());
          sb.setLength(0);
        }
      } else if (c == ' ' && !inQuotes) {
        if (sb.length() > 0) {
          command.add(sb.toString());
          sb.setLength(0);
        }
      } else {
        sb.append(c);
      }
    }

    if (sb.length() > 0) {
      command.add(sb.toString());
    }

    return command;
  }
  /**
   * Sends a message to the server.
   *
   * @param str the message to send
   * @throws IOException if an I/O error occurs while sending the message
   */
  public synchronized void send(String str) throws IOException {
    out.write((str + "\r\n").getBytes());
  }

  /**
   * Parses the user input and performs corresponding actions.
   *
   * @param input the input string provided by the user
   * @param client the client object
   * @return a string representing the message to be sent to the server
   */
  public static String handleInput(String input, Client client) {
    ArrayList<String> arguments = parseInput(input);
    String inputCommand = arguments.remove(0);
    switch (inputCommand) {
      case "/nickname":
        String changedName = arguments.get(0);
        nickname = changedName;
        return "NAME "+ changedName;

      case "/chat":
        if (arguments.get(0).equals("/w")) {
          String message = "\""+"(whispered) "+arguments.get(2);

          for(int i = 3; i< arguments.size(); i++){
            message = message + " "+ arguments.get(i);
          }
          message = message + "\"";
          System.out.println("CATC " + "t " + message +" "+ arguments.get(1));
          return "CATC " + "t " + message +" "+ arguments.get(1);

        }
        else {
          String message = "\""+arguments.get(0);

          for(int i = 1; i< arguments.size(); i++){
            message = message + " " + arguments.get(i);
          }
          message = message + "\"";
            return "CATC " + "f " + message;
        }

      case"/logout":
        return "LOGO";

      default:
        return "";
    }

  }
  /**
   * Handles incoming requests from the server and performs corresponding actions.
   *
   * @param request the request received from the server
   * @param client the client object
   */
  public static void handleRequest(String request, Client client) throws IOException {
    String requestCommand;
    ArrayList<String> arguments = decodeProtocolMessage(request);
    requestCommand = arguments.remove(0);
    try{
      RequestType requestType = RequestType.valueOf(requestCommand);

      switch(requestType){

        case CATS:
          String name = arguments.get(2);
          System.out.println(name + ": " + arguments.get(1));
          break;

        case PING:
          client.send("+PING");
          //System.out.println("+PING");
          break;

        case PWIN:
          break;

        case EMPT:
          break;

        default:
          break;
      }
    }
    catch(IllegalArgumentException e){
      LOGGER.debug("IllegalArgument: \"" + requestCommand + "\""); //should look into that starts IllegalArgument Exception at the start

    }
  }



    public static void handleResponse (String request, Client client) throws IOException {
    try{
    ArrayList<String> arguments = decodeProtocolMessage(request);
    String responsecommand = arguments.remove(0);
    String requestWithoutPlus = responsecommand.substring(1);
    ResponseType responseType = ResponseType.valueOf(requestWithoutPlus);
    switch(responseType){

      case PING:
        synchronized(client.pingThread){
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

      case LLOB:
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
        break;}}
      catch(IllegalArgumentException e){
      System.out.println("");
      }
    }




    /**
     * Logs out the client from the server.
     * Closes the socket, input and output streams.
     */
    public void logout () {
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


