package server;

import game.OrderedDeck;
import game.Tile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import static utils.NetworkUtils.encodeProtocolMessage;

/**
 * This class represents a game lobby in a server, handling player connections, game state management,
 * and communication between clients in our tile-based game. It supports adding players to the lobby, starting the game,
 * validating moves, and managing the game lifecycle including the game's start, ongoing state, and conclusion.
 * <p>
 * This class uses {@link ClientThread} to manage individual client connections, {@link GameState} to track the
 * current state of the game, and defines {@link LobbyState} to represent the lobby's status. It facilitates
 * network communication through utility methods, ensuring all players are synchronized with the game's progress.
 *
 * @author Pascal von Fellenberg
 * @author Istref Uka
 */

public class Lobby {
    private final static Logger LOGGER = LogManager.getLogger(Lobby.class);
    /**
     * Represents the lobby number of the lobby instance.
     */
    public final int lobbyNumber;
    private ArrayList<ClientThread> players;
    private LobbyState lobbyState;
    GameState gameState;
    private String winnerName;


    /**
     * Retrieves the number of players in the lobby.
     *
     * @return The number of players in the lobby.
     */
    public int getNumberOfPlayers() {
        int count = 0;
        for (ClientThread player : players) {
            if (player != null) {
                count++;
            }
        }
        return count;
    }

    /**
     * Retrieves the current state of the lobby.
     *
     * @return The current state of the lobby.
     */
    public LobbyState getLobbyState() {
        return lobbyState;
    }

    /**
     * Retrieves the name of the winner.
     *
     * @return The name of the winner.
     */
    public String getWinnerName() {
        return winnerName;
    }

    /**
     * Constructs a new Lobby instance with a specified lobby number. Upon creation, the lobby is initialized
     * as open, ready to accept players. This constructor sets the initial state of the lobby and prepares an
     * empty list to hold players who join.
     *
     * @param lobbyNumber the unique identifier for this lobby.
     */
    public Lobby(int lobbyNumber) {
        this.lobbyNumber = lobbyNumber;
        players = new ArrayList<>();
        lobbyState = LobbyState.OPEN;
    }

    /**
     * Retrieves the list of players in the lobby.
     *
     * @return An ArrayList containing the ClientThread instances of all players in the lobby.
     */
    public ArrayList<ClientThread> getPlayers() {
        // TODO minimize the use of this method
        return players;
    }


    /**
     * Starts the game.
     *
     * @param startPlayerIdx The index of the player that starts the game.
     * @return {@code true} if and only if the game was started successfully.
     */
    public boolean startGame(int startPlayerIdx) {
        if (players.size() != 4 || players.contains(null)) {
            return false;
        }
        gameState = new GameState(startPlayerIdx);
        lobbyState = LobbyState.RUNNING;
        return true;
    }

    /**
     * Adds a player to the lobby, if the lobby isn't full.
     *
     * @param client The client that should be added to the lobby.
     * @return {@code true} if and only if the player was successfully added to the lobby.
     */
    public boolean addPlayer(ClientThread client) {
        int oldPlayerCount = getNumberOfPlayers();
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i) == null) {
                players.set(i, client);
                if (gameState != null) {
                    try {
                        client.send(encodeProtocolMessage("STRT", encodeProtocolMessage(gameState.getPlayerDeck(i).toStringArrayList()), Integer.toString(i)));
                        client.send(getStatProtocolString());
                    } catch (IOException e) {
                        LOGGER.error("Lobby.addPlayer: IOException thrown" + e.getMessage());
                        return false;
                    }
                }
                if (oldPlayerCount == 3) {
                    lobbyState = LobbyState.RUNNING;
                }
                return true;
            }
        }
        if (oldPlayerCount < 4) {
            players.add(client);
            return true;
        }
        return false;
    }

    /**
     * Retrieves the index of a given client in the list of players.
     *
     * @param client The ClientThread instance for which the index is to be found.
     * @return the index of the specified client in the player list; returns -1 if the client is not found.
     */
    public int getPlayerIndex(ClientThread client) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i) == client) {
                return i;
            }
        }
        return -1;
    }


    /**
     * Sends a String to all clients in a lobby except for the sender.
     *
     * @param cmd    A String that conforms to the network-protocol, should be a CATS-Command.
     * @param sender The client that sent the message (to which the message should not be sent).
     * @throws IOException If an I/O error occurs while sending the game state to the client.
     */
    public void sendToLobby(String cmd, ClientThread sender) throws IOException {
        for (var p : players) {
            if (p == sender) {
                continue;
            }
            if (p != null) {
                p.send(cmd);
            }
        }
    }


    /**
     * Creates a String containing the nicknames of players in the lobby.
     * The nicknames are encoded as a protocol message.
     * If a player has not yet joined the lobby, their nickname is represented as an empty string.
     *
     * @return A protocol-encoded String containing the nicknames of players in the lobby.
     */
    public String getNicknameList() {
        int length = 4;
        StringBuilder sb = new StringBuilder();
        for (ClientThread currentPlayer : players) {
            if (currentPlayer == null) {
                sb.append(encodeProtocolMessage("") + " ");
                continue;
            }
            if ((currentPlayer.getPlayerIndex() == -1)) {
                sb.append(" ");
            } else if (currentPlayer.getPlayerIndex() != -1) {
                String nickname = currentPlayer.getNickname();
                sb.append(encodeProtocolMessage(nickname) + " ");
            }
        }
        while (players.size() < length) {
            sb.append("\"%\"" + " ");
            length--;
        }
        if (!sb.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * Returns a String that conforms to the STAT command in the network-protocol
     * with the information relevant to the lobby. <br>
     * This means that it has the following form:<br>
     * {@code "STAT <exchangestacks> <currentplayerindex>"} <br>
     * Example:<br>
     * {@code "STAT "3:RED \"%\" \"%\" 13:BLUE" 2"}
     *
     * @return String conforming to the STAT command specified by the network-protocol with the information of the lobby
     */
    public String getStatProtocolString() {
        String exchangeStacks = Tile.tileArrayToProtocolArgument(gameState.getVisibleTiles());
        String currentPlayerIdx = Integer.toString(gameState.getCurrentPlayerIndex());
        return encodeProtocolMessage("STAT", exchangeStacks, currentPlayerIdx);
    }


    /**
     * Validates a player's move by ensuring that the tile they wish to move is not null and that the resulting
     * tile configuration matches the server's current state of the game.
     *
     * @param tile       The tile that the player wants to put.
     * @param clientDeck The deck before the move
     * @param playerIdx  The index of the player making the move.
     * @return true if the move is valid and the resulting tile configuration matches the server's deck for the player;
     * false otherwise, including when the tile to move is null.
     */
    public boolean validateMove(Tile tile, OrderedDeck clientDeck, int playerIdx) {
        if (tile == null) {
            LOGGER.debug("move not valid: the moved tile can't be null");
            return false;
        }
        UnorderedDeck clientUnorderedDeck = clientDeck.toUnorderedDeck();
        clientUnorderedDeck.add(tile);
        OrderedDeck serverDeck = gameState.getPlayerDeck(playerIdx);
        UnorderedDeck serverUnorderedDeck = serverDeck.toUnorderedDeck();
        boolean equal = clientUnorderedDeck.equals(serverUnorderedDeck);
        // CLEANUP remove this debugging statement once everything works
        if (!equal) {
            LOGGER.debug(UnorderedDeck.showDiffDebug(serverUnorderedDeck, clientUnorderedDeck, "serverDeck", "clientDeck"));
        }
        return equal;
    }

    /**
     * Removes a player from the game at the specified index.
     *
     * @param playerIndex The index of the player to be removed.
     * @throws IOException If an I/O error occurs while sending the removal notification to other clients.
     *                     This might happen due to network issues or problems with the client connections.
     */
    public void removePlayer(int playerIndex) throws IOException {
        lobbyState = LobbyState.OPEN;
        players.set(playerIndex, null);
    }

    /**
     * Marks the game as finished and records the winner's name.
     *
     * @param winnerName The name of the player who won the game.
     * @throws IOException if an error occurs while entering the winners name in the file.
     */
    public void finishGame(String winnerName) throws IOException {
        this.winnerName = winnerName;

        if (!winnerName.equals("")) {
            int score = gameState.currentRoundNumber();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu-MM-dd,HH:mm");
            String todaysDate = LocalDateTime.now().format(dtf);
            HighScores.addEntryToHighscores(winnerName, todaysDate, score);
        }
        gameState = null;
        for (ClientThread p : players) {
            // so that the players can start a game if they feel like it.
            p.setIsReady(false);
        }
        lobbyState = LobbyState.FINISHED;
    }

    /**
     * Returns a protocol-encoded string conforming to the RSTA command used in the network protocol.
     * This string contains the serialized data of the visible tiles on the game board along with the current player's index.
     * The method formats the data for network transmission, ensuring that all clients in the lobby can synchronize
     * their state with the server effectively.
     *
     * @return A String encoded for the RSTA command containing the current state of the game's visible tiles
     * and the index of the player whose turn is current.
     */
    public String getRstaProtocolString() {
        if (gameState == null) {
            return encodeProtocolMessage("+RSTA", encodeProtocolMessage(new ArrayList<>(Arrays.asList("", "", "", ""))), "0");
        }
        String exchangeStacks = Tile.tileArrayToProtocolArgument(gameState.getVisibleTiles());
        String currentPlayerIdx = Integer.toString(gameState.getCurrentPlayerIndex());
        return encodeProtocolMessage("+RSTA", exchangeStacks, currentPlayerIdx);
    }

    /**
     * This enum represents the state of the lobby relevant to all the players on the server.
     */
    public enum LobbyState {

        /**
         * when the game hasn't started yet or a player left it
         */
        OPEN,
        /**
         * when the game is running
         */
        RUNNING,
        /**
         * when the game has finished
         */
        FINISHED,
    }
}


