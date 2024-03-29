package server;

import java.util.ArrayList;

public class Lobby {
  public final int lobbyNumber;
  public ArrayList<ClientThread> players;
  public LobbyState lobbyState;
  public GameState gameState;


  public Lobby(int lobbyNumber) {
    this.lobbyNumber = lobbyNumber;
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
}


