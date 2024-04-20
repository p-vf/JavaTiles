package server;

import org.apache.commons.lang3.tuple.Triple;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static utils.NetworkUtils.encodeProtocolMessage;

public class HighScores {
  private final Path path = Path.of("/Users/istrefuka/Downloads/Gruppe-5/project_documents/Highscores.ssv");

  public HighScores() {
  }

  public List<Triple<String, String, Integer>> readHighscores() throws IOException {
    List<Triple<String, String, Integer>> highScores = new ArrayList<>();

    // Benutze Scanner um die Datei zu lesen
    try (Scanner scanner = new Scanner(path)) {
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        String[] values = line.split("\\s+");
        if (values.length >= 3) {
          String name = values[0];
          String date = values[1];
          int score;
          try {
            score = Integer.parseInt(values[2]);
          } catch (NumberFormatException e) {
            // Handle the case where the score is not a valid integer
            continue;
          }
          highScores.add(Triple.of(name, date, score));
        }
      }
    }
    return highScores;
  }

  public void addEntryToHighscores(String name, String date, int score) throws IOException {
    List<Triple<String, String, Integer>> highScores = readHighscores();
    Triple<String, String, Integer> newEntry = Triple.of(name, date, score);

    // Finde die Stelle, an der der neue Eintrag eingefügt werden soll
    int positionToInsert = 0;
    for (Triple<String, String, Integer> entry : highScores) {
      // Da wir eine aufsteigende Sortierung haben wollen, brechen wir ab,
      // sobald wir einen Score finden, der größer als der aktuelle ist
      if (entry.getRight() > score) {
        break;
      }
      positionToInsert++;
    }

    // Füge den neuen Eintrag an der gefundenen Position ein
    highScores.add(positionToInsert, newEntry);
    saveHighscores(highScores);
  }


  private void saveHighscores(List<Triple<String, String, Integer>> entries) throws IOException {
    // Benutze BufferedWriter um in die Datei zu schreiben
    try (BufferedWriter writer = Files.newBufferedWriter(path)) {
      for (Triple<String, String, Integer> entry : entries) {
        writer.write(String.format("%s %s %d", entry.getLeft(), entry.getMiddle(), entry.getRight()));
        writer.newLine();
      }
    }
  }

  public String getHighScores() throws IOException {
    List<Triple<String, String, Integer>> highScores = readHighscores();
    ArrayList<String> encodedHighScores = new ArrayList<>();

    for (Triple<String, String, Integer> scoreEntry : highScores) {
      ArrayList<String> entry = new ArrayList<>();
      entry.add(scoreEntry.getLeft()); // Name
      entry.add(scoreEntry.getMiddle()); // Datum
      entry.add(String.valueOf(scoreEntry.getRight())); // Score
      encodedHighScores.add(encodeProtocolMessage(entry));
    }

    return encodeProtocolMessage(encodedHighScores);
  }


  // only for test purposes
  public static void main(String[] args) {
    HighScores highScores = new HighScores();
    try {
      highScores.addEntryToHighscores("Max", "2024-04-20", 200);
      highScores.addEntryToHighscores("Max", "2024-04-20", 100);
      highScores.addEntryToHighscores("Phillip", "2024-04-20", 100);
      List<Triple<String, String, Integer>> entries = highScores.readHighscores();
      for (Triple<String, String, Integer> entry : entries) {
        System.out.println(entry);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
