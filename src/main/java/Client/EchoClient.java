package Client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;


public class EchoClient {
  private static final int PING_TIMEOUT = 15000;
  public static SyncOutputStreamHandler syncOut;

  private String nickname = System.getProperty("user.name");

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

  public static String handleInput(String input){
     ArrayList<String> arguments = parseRequest(input);
     String inputcommand = arguments.get(0);
     switch(inputcommand){
       //case "/nickname":
         //break;

       case"/chat":
         String message = "CATC"+arguments.get(1);
         return message;

       default:
         return input;
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

