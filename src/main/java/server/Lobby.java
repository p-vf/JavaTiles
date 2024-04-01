package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;

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
    if (players.size() != 4) {
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
    if (players.size() < 4) {
      players.add(client);
      return true;
    }
    return false;
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
}


