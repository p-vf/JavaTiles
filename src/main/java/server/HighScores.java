package server;

import org.apache.commons.lang3.tuple.Triple;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static utils.NetworkUtils.encodeProtocolMessage;

/**
 * This class handles reading, adding, and retrieving high scores.
 */
public class HighScores {
    // path to the file containing the high scores
    private static final Path filePath = Path.of("HighScores.ssv");

    /**
     * Reads high scores from the high scores file. The high scores always contain the name of the player,
     * date and time of the high score and the score itself.
     *
     * @return An ArrayList of type Triple of high score entries.
     * @throws IOException Thrown if an I/O error occurs while reading the high scores file.
     */
    public static ArrayList<Triple<String, String, Integer>> readHighscores() throws IOException {
        ArrayList<Triple<String, String, Integer>> highScores = new ArrayList<>();


        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }
        try (Scanner scanner = new Scanner(filePath)) {
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

    /**
     * Adds a new high score entry to the high scores file.
     * The lower the score and the earlier it was achieved the higher the name in the file.
     *
     * @param name  The name of the player.
     * @param date  The date and time when the score was achieved.
     * @param score The score achieved by the player, must be larger than 1.
     * @throws IOException Thrown if an I/O error occurs while adding the high score entry.
     */
    public static void addEntryToHighscores(String name, String date, int score) throws IOException {
        ArrayList<Triple<String, String, Integer>> highScores = readHighscores();
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

    /**
     * Saves the high score entries to the high scores file.
     *
     * @param entries The high score entries to be saved.
     * @throws IOException Thrown if an I/O error occurs while saving the high score entries.
     */
    private static void saveHighscores(ArrayList<Triple<String, String, Integer>> entries) throws IOException {
        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
        }
        // Benutze BufferedWriter, um in die Datei zu schreiben
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (Triple<String, String, Integer> entry : entries) {
                writer.write(String.format("%s %s %d", entry.getLeft(), entry.getMiddle(), entry.getRight()));
                writer.newLine();
            }
        }
    }

    /**
     * Retrieves the high scores as an encoded string.
     *
     * @return The high scores as an encoded string.
     * @throws IOException Thrown if an I/O error occurs while retrieving the high scores.
     */
    public static String getHighScores() throws IOException {
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


    /**
     * This method is for testing purposes only and is never called during normal operation
     *
     * @param args not relevant
     */
    public static void main(String[] args) {
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu-MM-dd,HH:mm");
            String todaysDate = LocalDateTime.now().format(dtf);
            addEntryToHighscores("Max", todaysDate, 200);
            addEntryToHighscores("Max", todaysDate, 100);
            addEntryToHighscores("Phillip", todaysDate, 100);
            List<Triple<String, String, Integer>> entries = readHighscores();
            for (Triple<String, String, Integer> entry : entries) {
                //System.out.println(entry);
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }
}
