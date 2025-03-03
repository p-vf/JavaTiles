package game;

/**
 * Represents the color of a tile.
 */
public enum Color {
    /**
     * red color
     */
    RED,
    /**
     * blue color
     */
    BLUE,
    /**
     * yellow color
     */
    YELLOW,
    /**
     * black color
     */
    BLACK;


    static final String ANSI_RESET = "\u001B[0;0m";
    static final String ANSI_BLACK = "\u001B[30;47m";
    static final String ANSI_RED = "\u001B[31m";
    //static final String ANSI_GREEN = "\u001B[32;47m";
    static final String ANSI_YELLOW = "\u001B[33m";
    static final String ANSI_BLUE = "\u001B[34m";
    //static final String ANSI_PURPLE = "\u001B[35;47m";
    //static final String ANSI_CYAN = "\u001B[36;47m";
    //static final String ANSI_WHITE = "\u001B[37;47m";

    /**
     * Returns the ansi escape code corresponding to the color of the tile.
     *
     * @return ansi escape code corresponding to the color of the tile
     */
    public String toAnsiColor() {
        switch (this) {
            case RED -> {
                return ANSI_RED;
            }
            case BLUE -> {
                return ANSI_BLUE;
            }
            case YELLOW -> {
                return ANSI_YELLOW;
            }
            case BLACK -> {
                return ANSI_BLACK;
            }
            default -> {
                return "";
            }
        }
    }

    /**
     * Returns the ansi escape code for clearing the color.
     *
     * @return ansi escape code for clearing the color
     */
    public static String ansiReset() {
        return ANSI_RESET;
    }
}
