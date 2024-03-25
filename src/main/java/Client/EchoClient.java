package Client;

import Server.EchoServer;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * The EchoClient class represents a client in our application.
 * It connects to a server and allows users to send Strings and perform actions.
 * This class handles input/output operations and communication with the server.
 *
 * @author Boran Gökcen
 * @author Robin Gökcen
 * @author Pascal von Fellenberg
 */
public class EchoClient {

  private final Socket socket; // The socket for communication with the server

  public final OutputStream out; // Output stream to send messages to the server
  private final InputStream in; // Input stream to receive messages from the server
  private static final BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
  // Buffered reader for user input

  private Thread pingThread;// Thread responsible for sending periodic PING messages to the server.

  private static String nickname; // Nickname of the player


  /**
   * Constructs a new EchoClient with the given socket.
   *
   * @param socket the socket for communication with the server
   * @throws IOException if an I/O error occurs when creating the client
   */
  public EchoClient(Socket socket) throws IOException{
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
      Socket sock = new Socket(args[0], Integer.parseInt(args[1]));
      EchoClient client = new EchoClient(sock);
      InThread th = new InThread(client.in, client);
      Thread iT = new Thread(th);
      iT.start();

     ping(client);

      LoginClient login = new LoginClient();
      nickname = login.setUsername();
      String logindata = "LOGI " + login.setLobbyNumber() + " " + nickname;

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

      System.err.println(e.toString());
      System.exit(1);
    }


  }
  /**
   * Initiates a new PingThread for the specified EchoClient.
   * This method creates a new thread responsible for sending periodic PING messages
   * to the server to check for responsiveness.
   *
   * @param client the EchoClient for which to start the ping thread
   */
  public static void ping(EchoClient client){
    client.pingThread = new PingThread(client, 10000);
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
  public static String handleInput(String input, EchoClient client) {
    ArrayList<String> arguments = parseRequest(input);
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
  public static void handleRequest(String request, EchoClient client) throws IOException {
    ArrayList<String> arguments = parseRequest(request);
    String requestCommand = arguments.remove(0);
    switch (requestCommand) {
      case "CATS":
        String name = arguments.get(2);
        System.out.println(name + ": " +arguments.get(1));
        break;

      case "+LOGI":
        System.out.println("You have been logged in as: "+ arguments.get(0));
        break;

      case "+NAME":
        nickname = arguments.get(0);
        System.out.println("Your nickname has been changed to: "+nickname);
        break;

      case "+LOGO":
        System.out.println("You have been logged out.");
        client.logout();
        break;

      case "PING":
        //System.out.println("PING");
        client.send("+PING");
        break;

      case "+PING":
        //System.out.println("+PING");
        synchronized (client.pingThread) {
          client.pingThread.notify();
        }
        break;

      default:
        System.out.println(request);
        break;


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
      e.printStackTrace(System.err);
    }
  }


}

