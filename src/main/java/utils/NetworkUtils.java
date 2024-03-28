package utils;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    System.out.println(encodeProtocolMessage("lskdfj", "sldkfj", "weoiablkn"));


  }


  /**
   * Decodes a message conforming to the network protocol into an ArrayList of Strings,
   * the first element being the command name and the rest being arguments to said command.
   *
   * @param request String with a defined format in the network protocol.
   * @return An ArrayList having as first element the command name and the rest being arguments to the command.
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

  /**
   * Takes Strings in an ArrayList and combines them into a String that can be sent via Socket, conforming to the protocols rules.
   *
   * @param command ArrayList of Strings with the first element being the command name and the rest being arguments to said command.
   * @return String encoded in a way that conforms to the network protocol.
   */
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

  /**
   * Takes Strings and combines them into a String that can be sent via Socket, conforming to the protocols rules.
   *
   * @param command Name of the command that is to be encoded
   * @param args Arguments of the command that are to be encoded
   * @return Encoded String
   */
  public static String encodeProtocolMessage(String command, String... args) {
    ArrayList<String> input = new ArrayList<>(List.of(command));
    input.addAll(List.of(args));
    return encodeProtocolMessage(input);
  }
}
