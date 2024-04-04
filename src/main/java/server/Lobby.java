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
    // should work but didn't test yet
    if (tile == null) {
      return false;
    }
    ArrayList<Tile> temp = new ArrayList<>(Arrays.asList(tileArray));
    temp.add(tile);
    UnorderedDeck clientDeck = new UnorderedDeck(temp);

    UnorderedDeck serverDeck = gameState.playerDecks.get(playerIdx);
    return clientDeck.equals(serverDeck);
  }



  /**
   * Checks whether tileArray is a winning deck. The tileArray is a valid deck,
   * if it only contains "runs" and "sets" of length greater than 3.
   * A "run" and a "set" is a section fo the tileArray either bound by null, the end of the array or the element at index 12.
   * (The last condition is so that the sections makes sense visually, as the deck is presented in two rows of 12)
   * A "run" is a section only containing tiles with the same color and from left to right consecutively ascending numbers.
   * A "set" is a section only containing tiles with the same number but distinct colors.
   * <p>
   * example of a winning configuration: {@code [2:BLUE, 3:BLUE, 4:BLUE, 5:BLUE, 6:BLUE, null, 3:BLUE, 3:RED, 3:BLACK, 3:YELLOW, null, null, 6:BLACK, 7:BLACK, 8:BLACK, 9:BLACK, 10:BLACK, null, null, null, null, null, null, null]}
   * <p>
   * example of a not winning configuration: {@code [2:BLUE, 3:BLUE, 4:BLUE, 5:BLUE, 6:BLUE, null, 2:BLUE, 3:RED, 4:BLACK, 5:YELLOW, null, 6:BLACK, 7:BLACK, 8:BLACK, 9:BLACK, 10:BLACK, null, null, null, null, null, null, null, null]}
   * (the tile 6:BLACK is in a section with length one, as it is the element before index 12)
   * <p>
   * example of a not winning configuration: {@code [2:BLUE, 3:BLUE, 4:BLUE, 5:BLUE, 6:BLUE, null, 4:BLUE, 3:RED, 3:BLACK, 3:YELLOW, null, null, 6:BLACK, 7:BLACK, 8:BLACK, 9:BLACK, 10:BLUE, null, null, null, null, null, null, null]}
   * (The second section has distinct colors but two distinct numbers, the third section has consecutively ascending numbers but more than one distinct color)
   *
   * @param tileArray The array representing the deck
   * @return true if and only if the deck is of a winning configuration.
   */
  public static boolean isWinning(Tile[] tileArray) {
    // should work
    int formationLength = 0;
    boolean returnValue = true;
    for (int i = 0; i < tileArray.length; i++) {
      Tile currentTile = tileArray[i];
      if (currentTile == null || i == tileArray.length - 1 || i == 12) {
        if (formationLength < 3 && formationLength > 0) {
          System.out.println("Formation too short");
          //returnValue = false;
          return false;
        }
        if (formationLength == 0) {
          continue;
        }
        int from = i - formationLength;
        int to = i;
        boolean validRun = isValidRun(tileArray, from, to);
        boolean validSet = isValidSet(tileArray, from, to);
        String output = Arrays.toString(Arrays.copyOfRange(tileArray, from, to));
        if (validRun) {
          System.out.println("valid run: " + output);
        }
        if (validSet) {
          System.out.println("valid set: " + output);
        }

        if (!validSet && !validRun) {
          System.out.println("invalid section: " + output);
          return false;
          //returnValue = false;
        }
        formationLength = 0;
        if (i == 12 && currentTile != null) {
          formationLength = 1;
        }
        continue;
      }
      formationLength++;
    }


    return returnValue;
  }

  private static boolean isValidSet(Tile[] tileArray, int from, int to) {
    if (to - from > 4) {
      return false;
    }
    int mask = 0;
    int number = tileArray[from].getNumber();
    for (int i = from; i < to; i++) {
      Tile currentTile = tileArray[i];
      if (currentTile == null) {
        LOGGER.error("this should not be null");
        return false;
      }
      if (currentTile.getNumber() != number) {
        return false;
      }
      switch (currentTile.getColor()) {
        case RED -> {
          if (mask / 1 % 2 == 1) {
            return false;
          }
          mask += 1;
        }
        case BLUE -> {
          if (mask / 2 % 2 == 1) {
            return false;
          }
          mask += 2;
        }
        case YELLOW -> {
          if (mask / 4 % 2 == 1) {
            return false;
          }
          mask += 4;
        }
        case BLACK -> {
          if (mask / 8 % 2 == 1) {
            return false;
          }
          mask += 8;
        }
      }
    }
    return true;
  }
  private static boolean isValidRun(Tile[] tileArray, int from, int to) {
    Tile firstTile = tileArray[from];
    Color color = firstTile.getColor();
    int startNum = firstTile.getNumber();
    for (int i = from; i < to; i++) {
      Tile currentTile = tileArray[i];
      if (currentTile == null) {
        // this line should never be reached
        LOGGER.error("Range in tileArray contains null..");
        return false;
      }
      if (currentTile.getColor() != color) {
        return false;
      }
      if (currentTile.getNumber() != i - from + startNum) {
        return false;
      }
    }
    return true;
  }
}


