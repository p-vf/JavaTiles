package utils;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.Lobby;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class implements basic functionalities required for the communication specified in the file networkprotocol.md.
 *
 * @author Pascal von Fellenberg
 */
public class NetworkUtils {
    private static final Logger LOGGER = LogManager.getLogger(NetworkUtils.class);

    /**
     * for testing purposes
     *
     * @param args is not used
     */
    public static void main(String[] args) {
        String req = "CATC t \"\\\"bruh \\\"  skldjf wpeoi -.öelppoweir\" bruh b";
        System.out.println(req);
        System.out.println(decodeProtocolMessage(req));
        req = "hello brother how are you doing";
        System.out.println(req);
        System.out.println(decodeProtocolMessage(req));

        ArrayList<String> msg = new ArrayList<>(Arrays.asList("CATC", "sldkfj", "sldkfj ", "\"\" hdlf\\\\\\\\\\\"\" hallo"));
        System.out.println(msg);
        System.out.println(decodeProtocolMessage(encodeProtocolMessage(msg)));

        System.out.println(encodeProtocolMessage("lskdfj", "sldkfj", "weoiablkn"));
    }


    /**
     * Decodes a message conforming to the network protocol into an ArrayList of Strings.
     *
     * @param request String with a defined format in the network protocol.
     * @return An ArrayList having as first element the command name and the rest being arguments to the command.
     */
    public static ArrayList<String> decodeProtocolMessage(String request) {
        char[] chars = (request + " ").toCharArray();
        ArrayList<String> command = new ArrayList<>();
        boolean isInsideString = false;
        boolean wasInsideString = false;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            switch (chars[i]) {
                case ' ' -> {
                    if (isInsideString) {
                        sb.append(' ');
                    } else {
                        int lastIndex = sb.length() - 1;
                        if (lastIndex < 0) {
                            break;
                        }
                        if (sb.charAt(lastIndex) != '%' && wasInsideString) {
                            LOGGER.debug("Message was sent incorrectly.. (there is no space at the end where there should be) message: \"" + request + "\"");
                        } else if (wasInsideString) {
                            sb.deleteCharAt(lastIndex);
                        }
                        wasInsideString = false;
                        command.add(sb.toString());
                        sb = new StringBuilder();
                    }
                }
                case '\\' -> {
                    if (i < chars.length - 1 && chars[i + 1] == '"' && isInsideString) {
                        sb.append('"');
                        i++;
                    } else {
                        sb.append('\\');
                    }
                }
                case '"' -> {
                    if (isInsideString) {
                        wasInsideString = true;
                    }
                    isInsideString = !isInsideString;
                }
                default -> sb.append(chars[i]);
            }
        }
        return command;
    }

    /**
     * Takes Strings in an ArrayList and combines them into a String that can be sent via Socket, conforming to the protocols rules.
     *
     * @param command ArrayList of Strings with the first element being the command name and the rest being arguments to said command.
     *                All the Strings must NOT contain carriage-return ({@code '\r'}) and newline ({@code '\n'}).
     * @return String encoded in a way that conforms to the network protocol.
     */
    public static String encodeProtocolMessage(ArrayList<String> command) {
        // TODO fix bug when an argument has a Backslash at the end
        StringBuilder sb = new StringBuilder();
        for (String s : command) {
            // if carriage return or newline is in string, the string can't be sent via the network protocol as it marks the end of a command.
            if (s.contains("\r") || s.contains("\n")) {
                throw new IllegalArgumentException("Message contains newline or carriage return.");
            }

            // do special handling if argument meets certain conditions.
            if (s.contains(" ") || s.contains("\"") || s.isEmpty()) {
                sb.append("\"");
                for (char c : s.toCharArray()) {
                    if (c == '\"') {
                        sb.append("\\\"");
                    } else {
                        sb.append(c);
                    }
                }
                sb.append("%\""); // add percent before ending double quotes to cover edge case (a backslash as last character of the argument)
            } else {
                sb.append(s);
            }
            sb.append(" ");
        }
        if (!sb.isEmpty()) {
            // remove unnecessary space
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * Takes Strings and combines them into a String that can be sent via Socket, conforming to the protocols rules.
     *
     * @param command Name of the command that is to be encoded
     * @param args    Arguments of the command that are to be encoded.
     *                All the Strings must NOT contain carriage-return ({@code '\r'}) and newline ({@code '\n'}).
     * @return Encoded String
     */
    public static String encodeProtocolMessage(String command, String... args) {
        ArrayList<String> input = new ArrayList<>(List.of(command));
        input.addAll(List.of(args));
        return encodeProtocolMessage(input);
    }

    /**
     * This Method encodes lobbies with the respective playernames in a way such that it can be sent as an argument to the client.
     * On the client side {@code getBeautifullyFormattedDecodedLobbiesWithPlayerList()} should then be used to decode and format the received String beautifully.
     *
     * @param lobbies List of Lobbies currently on the server.
     * @return Encoded String that can be sent as argument to the client.
     */
    public static String getEncodedLobbiesWithPlayerList(ArrayList<Lobby> lobbies) {
        ArrayList<String> msg = new ArrayList<>();
        for (var lobby : lobbies) {
            ArrayList<String> playerNames = new ArrayList<>();
            playerNames.add("" + lobby.lobbyNumber);
            for (var player : lobby.getPlayers()) {
                if (player != null) {
                    playerNames.add(player.getNickname());
                } else {
                    playerNames.add("");
                }
            }
            msg.add(encodeProtocolMessage(playerNames));
        }
        return encodeProtocolMessage(msg);
    }

    /**
     * This method allows the client to decode and format the encoded lobbies-with-players-list
     * (generated by {@code getEncodedLobbiesWithPlayerList()}) so that it is more readable for the user.
     *
     * @param receivedLobbiesList The String that was generated with {@code getEncodedLobbiesWithPlayerList()}.
     * @return A formatted String of the lobbies and the players in them.
     */
    public static String getBeautifullyFormattedDecodedLobbiesWithPlayerList(String receivedLobbiesList) {
        ArrayList<String> lobbies = decodeProtocolMessage(receivedLobbiesList);
        StringBuilder sb = new StringBuilder();
        for (String lobby : lobbies) {
            ArrayList<String> lobbyPlayers = decodeProtocolMessage(lobby);
            for (int j = 0; j < lobbyPlayers.size(); j++) {
                if (j == 0) {
                    sb.append("Lobby ");
                    sb.append(lobbyPlayers.get(j));
                    sb.append(":");
                } else {
                    String playerName = lobbyPlayers.get(j);
                    sb.append("  ");
                    if (playerName.isEmpty()) {
                        sb.append("[empty slot]");
                    } else {
                        sb.append(playerName);
                    }
                }
                sb.append("\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * This enum represents the possible response and request types in the server-client protocol that the server must be able to handle.
     *
     * @author Pascal von Fellenberg
     * @author Istref Uka
     */
    public static class Protocol {
        // for a more detailed description of the commands, see product_documents/networkprotocol.md

        /**
         * This Enum describes either the type of request from the client or the type of response from the server.
         */
        public enum ClientRequest {
            /**
             * log in
             */
            LOGI,

            /**
             * log out
             */
            LOGO,

            /**
             * draw tile
             */
            DRAW,

            /**
             * put tile on stack
             */
            PUTT,

            /**
             * send message to Chat
             */
            CATC,

            /**
             * check if connection is still working
             */
            PING,

            /**
             * change name
             */
            NAME,

            /**
             * list lobbies
             */
            LGAM,

            /**
             * list players in lobby
             */
            LLPL,

            /**
             * list players on server
             */
            LPLA,

            /**
             * join lobby
             */
            JLOB,

            /**
             * ready for game
             */
            REDY,

            /**
             * get winning configuration (from cheatcode)
             */
            WINC,

            /**
             * leave lobby
             */
            LLOB,

            /**
             * get high-scores
             */
            HIGH,

            /**
             * get names from lobby
             */
            RNAM,
            /**
             * send relevant game state to client that just put a tile
             */
            RSTA,
        }

        /**
         * This enum describes either the type of request from the server or the type of response from the client.
         */
        public enum ServerRequest {
            /**
             * start game
             */
            STRT,

            /**
             * end game due to player win
             */
            PWIN,

            /**
             * end game due to draw (no winner)
             */
            EMPT,

            /**
             * send chat message to all relevant clients
             */
            CATS,

            /**
             * check if connection is still working
             */
            PING,

            /**
             * send relevant game state all clients of the same lobby
             */
            STAT,

            /**
             * send name list of lobby
             */
            NAMS,

            /**
             * notify that a player left
             */
            LEFT,

            /**
             * notify that a player joined
             */
            JOND,
        }
    }
}
