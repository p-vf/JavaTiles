package server;

import java.util.ArrayList;

public class Lobby implements Runnable {
  public final int lobbyNumber;
  public ArrayList<ClientThread> players;
  public LobbyState lobbyState;
  public GameState gameState;


  public Lobby(int lobbyNumber) {
    this.lobbyNumber = lobbyNumber;
    lobbyState = LobbyState.OPEN;
  }

  public void startGame(int startPlayerIdx) {
    gameState = new GameState(startPlayerIdx);
    lobbyState = LobbyState.RUNNING;
  }

  @Override
  public void run() {

  }
}


