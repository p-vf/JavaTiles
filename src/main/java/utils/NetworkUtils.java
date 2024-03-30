package utils;


import server.Lobby;

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
    if (!sb.isEmpty()) {
      sb.deleteCharAt(sb.length() - 1);
    }
    return sb.toString();
  }

  public static String getEncodedLobbiesWithPlayerList(ArrayList<Lobby> lobbies) {
    ArrayList<String> msg = new ArrayList<>();
    for (var lobby : lobbies) {
      ArrayList<String> playerNames = new ArrayList<>();
      msg.add(encodeProtocolMessage("Lobby " + lobby.lobbyNumber));
      for (var player : lobby.players) {
        playerNames.add(player.nickname);
      }
      msg.add(encodeProtocolMessage(playerNames));
    }
    return encodeProtocolMessage(msg);
  }

  public static String getBeautifullyFormattedDecodedLobbiesWithPlayerList(String receivedLobbiesList) {
    ArrayList<String> l = decodeProtocolMessage(receivedLobbiesList);
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < l.size(); i++) {
      for (var s : decodeProtocolMessage(l.get(i))) {
        if (i % 2 == 1) {
          sb.append(" ");
        }
        sb.append(s);
        if (i % 2 == 0) {
          sb.append(":");
        }
        sb.append("\n");
      }
    }
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

  /**
   * This enum represents the possible response and request types in the server-client protocol that the server must be able to handle.
   *
   * @author Pascal von Fellenberg
   * @author Istref Uka
   */
  public static class Protocol {
    // for a more detailed description of the commands, see product_documents/networkprotocol.md

    // these are the requests the server can get according to the protocol
    public enum ClientRequest {
      // log in:
      LOGI,
      // log out:
      LOGO,
      // fetch gamestate:
      STAT,
      // draw tile:
      DRAW,
      // put tile on stack:
      PUTT,
      // send message to Chat:
      CATC,
      // check if connection is still working:
      PING,
      // change name:
      NAME,
      // list lobbies:
      LGAM,
      // list players in lobby:
      LLPL,
      // list players on server:
      LPLA,
      // join lobby:
      JLOB,

    }

    // these are the responses the server can get according to the protocol
    public enum ServerRequest {
      // when the game ends due to a player win:
      PWIN,
      // when the game ends due to a draw:
      EMPT,
      // chat message distribution:
      CATS,
      // check if connection is still working:
      PING,
    }
  }
}
