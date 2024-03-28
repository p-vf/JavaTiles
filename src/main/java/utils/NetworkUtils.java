package utils;


import java.util.ArrayList;
import java.util.Arrays;

public class NetworkUtils {

  // for testing purposes
  public static void main(String[] args) {
    String req = "CATC t \"\\\"bruh \\\"  skldjf wpeoi -.öelppoweir\" bruh b";
    System.out.println(req);
    System.out.println(decodeProtocolMessage(req));
    req = "hello brother how are you doing";
    System.out.println(req);
    System.out.println(decodeProtocolMessage(req));


    ArrayList<String> msg = new ArrayList<>(Arrays.asList("CATC", "sldkfj", "sldkfj ", "\" helloliaonjasölmükoihjw3efpopodsufijewölk'sdfök32141\"\" \" "));
    System.out.println(msg);
    System.out.println(decodeProtocolMessage(encodeProtocolMessage(msg)));


  }


  /**
   * This method parses a Message of the format from the Protocol.
   * It separates the command and each argument and puts them into an ArrayList.
   *
   * @param request String with a defined format in the network protocol.
   * @return An ArrayList containing the command and its arguments as strings.
   */
  public static ArrayList<String> decodeProtocolMessage(String request) {
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
          isInsideString = !isInsideString;
          break;
        default:
          sb.append(chars[i]);
      }
    }
    command.add(sb.toString());
    return command;
  }

  public static String encodeProtocolMessage(ArrayList<String> command) {
    StringBuilder sb = new StringBuilder();
    for (String s : command) {
      if (s.contains(" ") || s.contains("\"")) {
        sb.append("\"");
        for (char c : s.toCharArray()) {
          if (c == '\"') {
            sb.append("\\\"");
          } else {
            sb.append(c);
          }
        }
        sb.append("\"");
      } else {
        sb.append(s);
      }
      sb.append(" ");
    }
    sb.deleteCharAt(sb.length()-1);
    return sb.toString();
  }
}
