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


  public Lobby(int lobbyNumber) {
    this.lobbyNumber = lobbyNumber;
    players = new ArrayList<>();
    lobbyState = LobbyState.OPEN;
  }

  public boolean startGame(int startPlayerIdx) {
    if (players.size() != 4) {
      return false;
    }
    gameState = new GameState(startPlayerIdx);
    lobbyState = LobbyState.RUNNING;
    return true;
  }

  public boolean addPlayer(ClientThread client) {
    if (players.size() < 4) {
      players.add(client);
      return true;
    }
    return false;
  }


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


