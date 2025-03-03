package server;

import game.Color;
import game.Tile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;

import static game.Color.*;

/**
 * This class represents a deck that contains different tiles.
 * It represents the tiles in an array that contains the counts of each tile,
 * meaning it doesn't care about the order of the tiles in the deck, making some calculations more efficient and reliable.
 * <p>
 * This class is only relevant for server-side calculations (validation).
 */
public class UnorderedDeck {
    private final Logger LOGGER = LogManager.getLogger(UnorderedDeck.class);

    // array filled with counts (either 0, 1, or 2) for each tile
    // there are 53 distinct tiles in the game, two of each, so 106 in total
    int[] tileCounts = new int[53];

    /**
     * Default constructor for UnorderedDeck. Initializes an empty deck.
     */
    public UnorderedDeck() {
        // tileCounts already has a default value so this constructor doesn't have to do anything.
    }

    /**
     * Constructs an UnorderedDeck instance from a list of {@link Tile} objects. <p>
     * This constructor initializes the deck with the tiles in the given arraylist,
     * meaning it adds as many occurrences of each tile as there are in the given arraylist.
     *
     * @param deck the list of {@link Tile} objects to initialize the deck with.
     */
    public UnorderedDeck(ArrayList<Tile> deck) {
        for (var tile : deck) {
            this.add(tile);
        }
    }

    /**
     * This method is for testing purposes only and is never called during normal operation
     *
     * @param args not relevant
     */
    public static void main(String[] args) {
        try {
            for (int i = 0; i < 53; i++) {
                System.out.println(i + ": " + tileToIndex(indexToTile(i)));
            }
            Tile[] tiles = new Tile[]{
                    new Tile(1, BLUE),
                    new Tile(5, BLUE),
                    new Tile(8, BLUE),
                    new Tile(11, BLUE),
                    new Tile(12, BLUE),
                    new Tile(3, BLACK),
                    new Tile(1, BLUE),
                    new Tile(1, BLUE),
            };
            UnorderedDeck deck = new UnorderedDeck(new ArrayList<>(Arrays.asList(tiles)));
            System.out.println(deck.toStringArray());
        } catch (RuntimeException e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * This is a bijection from tile to number. It's inverse (indexToTile()) is also a bijection.
     *
     * @param tile the tile to convert to its corresponding index.
     * @return the index corresponding to the given tile.
     */
    private static int tileToIndex(Tile tile) {
        Color color = tile.getColor();
        int number = tile.getNumber();
        // if the tile is a joker, the index is at the beginning of the array.
        if (number == 0) {
            return 0;
        }
        int colorNumber = Arrays.stream(Color.values()).toList().indexOf(color);
        return colorNumber * 13 + number;
    }

    /**
     * This is a bijection from tile to number. It's inverse (tileToIndex()) is also a bijection.
     *
     * @param index the index to convert to its corresponding tile.
     * @return the tile corresponding to the given index.
     */
    private static Tile indexToTile(int index) {
        if (index == 0) {
            return new Tile(0, BLACK);
        }
        index--;
        int number = index % 13 + 1;
        Color color = Color.values()[index / 13];
        return new Tile(number, color);
    }

    /**
     * Adds a tile to the deck.
     *
     * @param tile tile to be added to deck. If null, nothing is changed about the deck.
     */
    public void add(Tile tile) {
        if (tile == null) {
            return;
        }
        int num = ++tileCounts[tileToIndex(tile)];
        if (num > 2) {
            // sanity check, only for debugging
            LOGGER.debug("added too many tiles to deck");
        }
    }

    /**
     * Calculates the total number of tiles in the deck.
     *
     * @return the total count of tiles in the deck.
     */
    public int size() {
        int sum = 0;
        for (int count : tileCounts) {
            sum += count;
        }
        return sum;
    }

    /**
     * Removes tile from the deck.
     *
     * @param tile tile to be removed. Should not be null
     */
    public void remove(Tile tile) {
        if (tile == null) {
            LOGGER.debug("Tried to remove null from UnorderedDeck instance. ");
            return;
        }
        int num = --tileCounts[tileToIndex(tile)];
        if (num < 0) {
            // sanity check, only for debugging
            LOGGER.debug("(should-be-unreachable) negative number of tiles in deck");
        }
    }

    /**
     * Converts the deck into an {@link ArrayList} of {@link String} objects representing the tiles in the deck.
     *
     * @return an {@link ArrayList} of {@link String}, each representing a tile in the deck.
     */
    public ArrayList<String> toStringArray() {
        ArrayList<String> res = new ArrayList<>();
        for (int idx = 0; idx < tileCounts.length; idx++) {
            for (int i = 0; i < tileCounts[idx]; i++) {
                res.add(indexToTile(idx).toString());
            }
        }
        return res;
    }

    /**
     * Returns a human-readable string that shows the difference between two decks.
     * Should only be used for debugging purposes.
     *
     * @param a     first deck
     * @param b     second deck
     * @param nameA name of the first deck
     * @param nameB name of the second deck
     * @return human-readable string that describes how the two decks are different
     */
    public static String showDiffDebug(UnorderedDeck a, UnorderedDeck b, String nameA, String nameB) {
        if (a.equals(b)) {
            return nameA + " is equal to " + nameB + "\n";
        }
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < a.tileCounts.length; i++) {
            int diff = a.tileCounts[i] - b.tileCounts[i];
            if (diff > 0) {
                res.append(nameA + " has " + diff + " more of tile: " + indexToTile(i) + " than " + nameB + "\n");
            } else if (diff < 0) {
                res.append(nameB + " has " + -diff + " more of tile: " + indexToTile(i) + " than " + nameA + "\n");
            }
        }
        return res.toString();
    }

    /**
     * Compares this UnorderedDeck instance with another object for equality. Two decks are considered equal
     * if they contain the same number of each tile type.
     *
     * @param other the object to be compared for equality with this deck.
     * @return true if the specified object is equal to this deck; false otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (!(other instanceof UnorderedDeck otherDeck)) {
            return false;
        }
        return Arrays.equals(otherDeck.tileCounts, tileCounts);
    }
}
