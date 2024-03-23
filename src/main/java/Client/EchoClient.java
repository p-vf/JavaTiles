package Client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;


public class EchoClient {
  private static final int PING_TIMEOUT = 15000;
  public static SyncOutputStreamHandler syncOut;

  private static String nickname = System.getProperty("user.name");


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
              String currentArg = sb.toString();
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
      return command;}
  public static String handleInput(String input) {
    ArrayList<String> arguments = parseRequest(input);
    String inputCommand = arguments.remove(0);
    switch (inputCommand) {
      case "/nickname":
        String changedName = arguments.get(0);
        nickname = changedName;
        return "NAME "+ changedName;

      case "/chat":
        if (arguments.get(0).equals("/w")) {
          String message = "\""+arguments.get(2);

          for(int i = 3; i< arguments.size(); i++){
            message = message + " "+ arguments.get(i);
          }
          message = message + "\"";

          return "CATC " + "t " + message +" "+ arguments.get(1);

        }
        else {
          String message = "\""+arguments.get(0);

          for(int i = 1; i< arguments.size(); i++){
            message = message +" "+ arguments.get(i);
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

  public void handleRequest(String request){
    ArrayList<String> arguments = parseRequest(request);
    System.out.println(arguments.toString());
    String requestCommand = arguments.remove(0);
    switch (requestCommand) {
      case "CATS":
        String name = arguments.get(2);
        System.out.println(name + ": " +arguments.get(1));

      default:
        System.out.println(request);
        break;


    }
  }




  public static void main(String[] args) {
    try {
      Socket sock = new Socket(args[0], Integer.parseInt(args[1]));
      InputStream in = sock.getInputStream();
      OutputStream out = sock.getOutputStream();
      syncOut = new SyncOutputStreamHandler(out);
      InThread th = new InThread(in);
      Thread iT = new Thread(th);
      iT.start();

      Thread cpthread = new Thread(new ClientPingThread(syncOut, 10000));
      cpthread.start();

      sock.setSoTimeout(PING_TIMEOUT);






      LoginClient login = new LoginClient();
      nickname = login.setUsername();
      String logindata = "LOGI " + login.setLobbyNumber()+ " "+ nickname ;

      syncOut.writeData(logindata.getBytes());
      syncOut.writeData("\r\n".getBytes());


      BufferedReader conin = new BufferedReader(new InputStreamReader(System.in));
      String line = " ";
      while (true) {
        line = conin.readLine();
        if (line.equalsIgnoreCase("QUIT")) {
          break;
        }



        syncOut.writeData(handleInput(line).getBytes());
        syncOut.writeData("\r\n".getBytes());

      }

      System.out.println("terminating...");
      in.close();
      out.close();
      sock.close();
    } catch (IOException e) {

      System.err.println(e.toString());
      System.exit(1);
    }
  }


  private void setNickname(String nickname) {
    this.nickname = nickname;

  }
}

