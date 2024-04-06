package server;

import game.Tile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Lobby {
  public final static Logger LOGGER = LogManager.getLogger();
  public final int lobbyNumber;
  public ArrayList<ClientThread> players;
  public LobbyState lobbyState;
  public GameState gameState;
  public ClientThread winner;


  public Lobby(int lobbyNumber) {
    this.lobbyNumber = lobbyNumber;
    players = new ArrayList<>();
    lobbyState = LobbyState.OPEN;
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
    int playerCount = 0;
    for (int i = 0; i < players.size(); i++) {
      if (players.get(i) != null) {
        playerCount++;
      } else {
        players.set(i, client);
        if (gameState != null) {
          try {
            client.send(NetworkUtils.encodeProtocolMessage("STRT", gameState.playerDecks.get(i).toString(), Integer.toString(i)));
          } catch (IOException e) {
            LOGGER.error("Lobby.addPlayer: IOException thrown" + e.getMessage());
            return false;
          }
        }
        return true;
      }
    }
    if (playerCount < 4) {
      players.add(client);
      return true;
    }
    return false;
  }

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
   * @param cmd A String that conforms to the network-protocol, should be a CATS-Command.
   * @param sender The client that sent the message (to which the message should not be sent).
   */
  public void sendToLobby(String cmd, ClientThread sender) {
    for (var p : players) {
      if (p == sender) {
        continue;
      }
      try {
        p.send(cmd);
      } catch (IOException e) {
        LOGGER.error("From Lobby.sendToLobby():" + e.getMessage());
      }
    }
  }

  public boolean validateMove(Tile tile, Tile[] tileArray, int playerIdx) {
    if (tile == null) {
      LOGGER.debug("move not valid: the moved tile can't be null");
      return false;
    }
    ArrayList<Tile> temp = new ArrayList<>(Arrays.asList(tileArray));
    temp.add(tile);
    UnorderedDeck clientDeck = new UnorderedDeck(temp);

    UnorderedDeck serverDeck = gameState.playerDecks.get(playerIdx);
    boolean equal = clientDeck.equals(serverDeck);
    // CLEANUP remove this debugging statement once everything works
    if (!equal) {
      LOGGER.debug(UnorderedDeck.showDiffDebug(serverDeck, clientDeck, "serverDeck", "clientDeck"));
    }
    return equal;
  }

  public void removePlayer(int playerIndex) throws IOException {
    // TODO send message to all other clients that a player has been removed.
    lobbyState = LobbyState.OPEN;
    players.set(playerIndex, null);
    for (int i = 0; i < players.size(); i++) {
      if (players.get(i) != null) {
        //players.get(i).send(NetworkUtils.encodeProtocolMessage("LEFT", Integer.toString(i)));
      }
    }
  }

  /**
   * Represents the rough state of the lobby.
   */
  public enum LobbyState {
    // TODO maybe add more states (when someone leaved etc.)

    // when the game hasn't started yet
    OPEN,
    // when the game is running
    RUNNING,
    // when the game is finished
    FINISHED,
  }
}


