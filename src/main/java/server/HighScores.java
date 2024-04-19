package server;

import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;

public class HighScores {
  static final String csvPath = "";

  public HighScores(String csvPath) {}


  public ArrayList<Triple<String, String, Integer>> readHighscores() {
    // TODO read values and return them
    return null;
  }

  public void addEntryToHighscores(String name, String date, int score) {
    // TODO read Highscores (arraylist)
    // TODO insert entry into arraylist
    // TODO save the updated Highscores (arraylist)
  }

  private void saveHighscores(ArrayList<Triple<String, String, Integer>> entries) {
    // TODO save Highscores
  }
}
