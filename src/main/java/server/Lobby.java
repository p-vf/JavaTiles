package server;

import game.Color;
import game.Tile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import game.Color;

public class Lobby {
  public final static Logger LOGGER = LogManager.getLogger();
  public final int lobbyNumber;
  public ArrayList<ClientThread> players;
  public LobbyState lobbyState;
  public GameState gameState;
  public ClientThread winner;

  // for testing purposes
  public static void main(String[] args) {
    /*
    try {
      Tile[] tiles = new Tile[]{
          new Tile(1, 2, Color.BLUE),
          new Tile(2, 2, Color.RED),
          new Tile(3, 2, Color.BLACK),
          null,
          new Tile(4, 4, Color.BLUE),
          new Tile(5, 5, Color.BLUE),
          new Tile(6, 6, Color.BLUE),
          new Tile(7, 7, Color.BLUE),
          null,
          new Tile(9, 4, Color.BLUE),
          new Tile(10, 5, Color.BLUE),
          new Tile(11, 6, Color.BLUE),
          null,
          null,
          new Tile(12, 7, Color.BLUE),
          new Tile(13, 8, Color.BLUE),
          new Tile(14, 9, Color.BLUE),
          null,
          new Tile(15, 10, Color.BLUE),
          new Tile(16, 11, Color.BLUE),
          new Tile(17, 12, Color.BLUE),
          null,
          new Tile(18, 2, Color.BLUE),
          new Tile(19, 2, Color.RED),
          new Tile(20, 2, Color.BLACK),
          null,
          null,
          null,
      };
      System.out.println(checkIfWon(tiles));
    } catch (RuntimeException e) {
      e.printStackTrace(System.err);
    }
    */
  }


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
      System.out.print(UnorderedDeck.showDiffDebug(serverDeck, clientDeck, "serverDeck", "clientDeck"));
    }
    return equal;
  }




}


