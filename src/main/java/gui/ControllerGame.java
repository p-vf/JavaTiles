package gui;

import client.Client;
import game.Tile;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import static org.apache.commons.lang3.StringUtils.substring;
import static utils.NetworkUtils.encodeProtocolMessage;


/**
 * The ControllerGame class controls the game interface.
 * <p>
 * This class manages the interaction between the game GUI and the client.
 * It handles button clicks, updates GUI elements based on server responses,
 * and facilitates communication between players.
 * </p>
 * <p>
 * The game interface consists of buttons (representing tiles) for actions such as switching tiles or putting tiles.
 * </p>
 * <p>
 * Additionally, this class provides methods for updating player information,
 * managing chat functionality, and handling game events.
 * </p>
 * <p>
 * The ControllerGame class implements the Initializable interface to initialize
 * the game interface components when the FXML file is loaded.
 * </p>
 *
 * @author Boran Gökcen
 * @version 1.0
 */
public class ControllerGame implements Initializable {

    public Controller controller;
    @FXML
    private Button broadcastButton;
    @FXML
    private Label gameWarning; // Label for displaying game warnings.


    // Buttons representing the exchangeStacks
    @FXML
    private Button exchangeStack0;


    @FXML
    private Button exchangeStack1;

    @FXML
    private Button exchangeStack2;

    @FXML
    private Button exchangeStack3;

    @FXML
    private Button mainStack; // Button for the main stack

    @FXML
    private Button puttButton; // Button for putting tiles.

    // Buttons representing tiles
    @FXML
    private Button one0;

    @FXML
    private Button one1;

    @FXML
    private Button one2;

    @FXML
    private Button one3;

    @FXML
    private Button one4;

    @FXML
    private Button one5;

    @FXML
    private Button one6;

    @FXML
    private Button one7;

    @FXML
    private Button one8;

    @FXML
    private Button one9;

    @FXML
    private Button one10;

    @FXML
    private Button one11;

    @FXML
    private Button zero0;

    @FXML
    private Button zero1;

    @FXML
    private Button zero2;

    @FXML
    private Button zero3;

    @FXML
    private Button zero4;

    @FXML
    private Button zero5;

    @FXML
    private Button zero6;

    @FXML
    private Button zero7;

    @FXML
    private Button zero8;

    @FXML
    private Button zero9;

    @FXML
    private Button zero10;

    @FXML
    private Button zero11;


    private ArrayList<Button> pressedButtons = new ArrayList<Button>(); // List of currently pressed buttons

    private ArrayList<Button> deck; // List of buttons representing the deck

    private static Client client; // Reference to the client instance

    private Tile[] tiles; // Array of tiles representing the game tiles

    @FXML
    private Button startButton; //Button to start the game

    // Labels displaying player names
    @FXML
    private Label playerName0;


    @FXML
    private Label playerName1;

    @FXML
    private Label playerName2;

    @FXML
    private Label playerName3;

    private ArrayList<Label> playerNames; //list representing the player names.


    @FXML
    private Label winLabel; // Label for displaying the winner of the game

    @FXML
    private Label turnLabel; // Label for displaying the current player's turn

    private String nickname; // Nickname of the player


    @FXML
    private TextArea gameChatArea; // TextArea for displaying game chat messages

    @FXML
    private TextField gameChatField; // TextField for typing game chat messages

    @FXML
    private TextField changeNicknameField; // TextField for changing the player's nickname

    @FXML
    private Label changeNicknameWarning; // Label for displaying warnings related to nickname changes

    private boolean nameIsEmpty = false; // Flag indicating if the nickname field is empty


    private boolean canYouPlayThisMove = false; // Flag indicating if the current player can play a move

    private boolean broadcastPressed = false;
    private boolean startPressed = false;
    private boolean isCheatCode = false;




    /**
     * Sets the client instance for the controller.
     *
     * @param client the client instance
     */
    public static void setClient(Client client) {
        ControllerGame.client = client;
    }


    /**
     * Initializes the controller.
     * <p>
     * Sets up the deck, the labels of the player names, and initial game state.
     * </p>
     *
     * @param location  The location used to resolve relative paths for the root object,
     *                  or {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null}
     *                  if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        deck = new ArrayList<>(Arrays.asList(zero0, zero1, zero2, zero3, zero4, zero5, zero6, zero7, zero8, zero9, zero10, zero11, one0, one1, one2, one3, one4, one5, one6, one7, one8, one9, one10, one11));
        playerNames = new ArrayList<>(Arrays.asList(playerName0, playerName1, playerName2, playerName3));
        puttButton.setDisable(true);
        try {
            client.send(encodeProtocolMessage("RNAM"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        disableStacks(true);

        client.setgameController(this);
    }

    /**
     * Displays the deck of tiles in the game GUI.
     *
     */
    @FXML
    public void showDeck() {
        client.setPressedStart(true);
        startButton.setDisable(true);
        startButton.setVisible(false);
        if(!(isCheatCode)){
        disableStacks(false);}
        startPressed = true;
        this.tiles = client.getTiles();

        for (int i = 0; i < deck.size(); i++) {
            if (tiles[i] == null) {
                deck.get(i).setText("");
            } else {
                deck.get(i).setText("" + tiles[i].getNumber());
                deck.get(i).setTextFill(Paint.valueOf(String.valueOf(tiles[i].getColor())));
            }


        }
    }

    /**
     * Removes a tile from the exchange stack.
     */
    public void takeOffExchangeStack() {
        exchangeStack0.setText("");
    }


    /**
     * Disables or enables the exchange and main stack based on the given boolean value.
     *
     * @param bool {@code true} to disable the stacks, {@code false} to enable them
     */
    public void disableStacks(boolean bool) {
        mainStack.setDisable(bool);
        exchangeStack0.setDisable(bool);
    }

    /**
     * Sets the text of the gameWarning label.
     *
     * @param text the text to be set on the gameWarning label
     */
    public void setTextofGameWarning(String text) {
        gameWarning.setVisible(true);
        gameWarning.setText(text);
    }

    /**
     * Sets whether the current player can play a move.
     *
     * @param canYouPlayThisMove {@code true} if the player can play a move, {@code false} otherwise
     */
    public void setCanYouPlayThisMove(boolean canYouPlayThisMove) {
        this.canYouPlayThisMove = canYouPlayThisMove;
    }


    /**
     * Sets the text of the win label and sends a corresponding message to the server.
     *
     * @param text the text to be set on the win label
     * @throws IOException if an I/O error occurs while sending messages to the server
     */
    public void setWinLabel(String text) throws IOException {
        winLabel.setDisable(false);
        winLabel.setText(text);
        if (text.equals("DRAW!")) {
            client.send(encodeProtocolMessage("+EMPT"));
        } else {
            client.send(encodeProtocolMessage("+PWIN"));
        }
    }

    /**
     * Sets the text of the turn label.
     *
     * @param text the text to be set on the turn label
     * @throws IOException if an I/O error occurs while sending messages to the client
     */
    public void setTurnLabel(String text) throws IOException {
        turnLabel.setDisable(false);
        turnLabel.setText(text);

    }

    /**
     * Sets the nickname of the player.
     *
     * @param nickname the nickname to be set for the player
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;


    }

    /**
     * Sets the tile on the ExchangeStack0.
     *
     * @param tile the tile to be set on the exchange stack
     */
    public void setExchangeStack0(Tile tile) {
        if (tile != null) {
            exchangeStack0.setText("" + tile.getNumber());
            exchangeStack0.setTextFill(Paint.valueOf(String.valueOf(tile.getColor())));
        }


    }

    /**
     * Sets the tiles on the exchange stacks based on the given array of tiles and index.
     *
     * @param tiles the array of tiles to be set on the exchange stacks
     * @param index the index of the exchange stack to be updated
     */
    public void setExchangeStacks(Tile[] tiles, int index) {

        for (int i = 0; i < tiles.length; i++) {
            if (i == index) {
                if (i == 0) {
                    if (tiles[1] != null) {
                        exchangeStack1.setText("" + tiles[1].getNumber());
                        exchangeStack1.setTextFill(Paint.valueOf(String.valueOf(tiles[1].getColor())));
                    }
                    if (tiles[2] != null) {
                        exchangeStack2.setText("" + tiles[2].getNumber());
                        exchangeStack2.setTextFill(Paint.valueOf(String.valueOf(tiles[2].getColor())));
                    }
                    if (tiles[3] != null) {
                        exchangeStack3.setText("" + tiles[3].getNumber());
                        exchangeStack3.setTextFill(Paint.valueOf(String.valueOf(tiles[3].getColor())));
                    }
                }
                if (i == 1) {
                    if (tiles[2] != null) {
                        exchangeStack1.setText("" + tiles[2].getNumber());
                        exchangeStack1.setTextFill(Paint.valueOf(String.valueOf(tiles[2].getColor())));
                    }
                    if (tiles[3] != null) {
                        exchangeStack2.setText("" + tiles[3].getNumber());
                        exchangeStack2.setTextFill(Paint.valueOf(String.valueOf(tiles[3].getColor())));
                    }
                    if (tiles[0] != null) {
                        exchangeStack3.setText("" + tiles[0].getNumber());
                        exchangeStack3.setTextFill(Paint.valueOf(String.valueOf(tiles[0].getColor())));
                    }
                }
                if (i == 2) {
                    if (tiles[3] != null) {
                        exchangeStack1.setText("" + tiles[3].getNumber());
                        exchangeStack1.setTextFill(Paint.valueOf(String.valueOf(tiles[3].getColor())));
                    }
                    if (tiles[0] != null) {
                        exchangeStack2.setText("" + tiles[0].getNumber());
                        exchangeStack2.setTextFill(Paint.valueOf(String.valueOf(tiles[0].getColor())));
                    }
                    if (tiles[1] != null) {
                        exchangeStack3.setText("" + tiles[1].getNumber());
                        exchangeStack3.setTextFill(Paint.valueOf(String.valueOf(tiles[1].getColor())));
                    }

                }
                if (i == 3) {
                    if (tiles[0] != null) {
                        exchangeStack1.setText("" + tiles[0].getNumber());
                        exchangeStack1.setTextFill(Paint.valueOf(String.valueOf(tiles[0].getColor())));
                    }
                    if (tiles[1] != null) {
                        exchangeStack2.setText("" + tiles[1].getNumber());
                        exchangeStack2.setTextFill(Paint.valueOf(String.valueOf(tiles[1].getColor())));
                    }
                    if (tiles[2] != null) {
                        exchangeStack3.setText("" + tiles[2].getNumber());
                        exchangeStack3.setTextFill(Paint.valueOf(String.valueOf(tiles[2].getColor())));
                    }

                }

            }

        }
    }


    /**
     * Determines the position of the given button (representing a tile).
     *
     * @param button the button whose position is to be determined
     * @return an array containing the row and column indices of the button
     */
    int[] TilePosition(Button button) {
        int[] position = new int[2];
        if (button.getId().contains("zero")) {
            position[0] = 0;
            String columnString = button.getId().substring(4);
            if (columnString.matches("\\d+")) {
                position[1] = Integer.valueOf(columnString);
            }
            return position;
        } else {
            position[0] = 1;
            String columnString = button.getId().substring(3);
            if (columnString.matches("\\d+")) {
                position[1] = Integer.valueOf(columnString);
            }
            return position;

        }
    }

    /**
     * Clears the selection and disables the putt button.
     *
     * @param event the MouseEvent triggered by clicking outside the selection area
     */
    @FXML
    void deleteSelection(MouseEvent event) {
        pressedButtons.clear();
        puttButton.setDisable(true);

    }


    /**
     * Handles the action event triggered by pressing Buttons.
     * Is used for putting and switching tiles.
     *
     * @param event the ActionEvent triggered by pressing a tile button
     * @throws IOException if an I/O error occurs while sending messages to the server
     */
    @FXML
    void pressTile(ActionEvent event) throws IOException {

        ArrayList<String> args = new ArrayList<String>();

        Button pressedButton = (Button) event.getTarget();
        pressedButtons.add(pressedButton);

        if (pressedButtons.size() == 1) {
            for (int i = 0; i < deck.size(); i++) {
                if (pressedButtons.get(0).equals(deck.get(i))) {
                    puttButton.setDisable(false);
                }
            }


        }

        if (pressedButtons.size() == 2) {
            Button firstButton = pressedButtons.get(0);
            Button secondButton = pressedButtons.get(1);

            if (firstButton.equals(puttButton) ^ secondButton.equals(puttButton)) {

                if (secondButton.equals(puttButton)) {
                    if (firstButton.getText().isBlank()) {

                        gameWarning.setVisible(true);
                        gameWarning.setText("choose an existing Tile");


                    } else {
                        int[] tilePosition = TilePosition(firstButton);
                        gameWarning.setVisible(true);
                        //gameWarning.setText("Button ist am Ort"+ tilePosition[0]+" "+ tilePosition[1]);
                        args.add("/putt");
                        args.add(tilePosition[0] + "");
                        args.add(tilePosition[1] + "");
                        System.out.println(encodeProtocolMessage(args));
                        String message = client.handleInput(encodeProtocolMessage(args));
                        System.out.println(message);
                        client.send(message);
                        if (canYouPlayThisMove) {
                            firstButton.setText("");
                        }

                    }
                    pressedButtons.clear();
                    puttButton.setDisable(true);
                } else {
                    pressedButtons.clear();
                    puttButton.setDisable(true);
                }
            } else {
                args.add("/swap");
                int[] firstTilePosition = TilePosition(firstButton);
                int[] secondTilePosition = TilePosition(secondButton);
                args.add(firstTilePosition[0] + "");
                args.add(firstTilePosition[1] + "");
                args.add(secondTilePosition[0] + "");
                args.add(secondTilePosition[1] + "");

                String message = client.handleInput(encodeProtocolMessage(args));
                client.send(message);

                String firstTile = firstButton.getText();
                String secondTile = secondButton.getText();
                Paint firstTilePaint = firstButton.getTextFill();
                Paint secondTilePaint = secondButton.getTextFill();

                firstButton.setText(secondTile);
                firstButton.setTextFill(secondTilePaint);
                secondButton.setText(firstTile);
                secondButton.setTextFill(firstTilePaint);

                pressedButtons.clear();
                puttButton.setDisable(true);
            }
            puttButton.setDisable(true);
            pressedButtons.clear();


        }
    }

    /**
     * Handles the action event triggered by drawing a tile from the main stack or exchange stack.
     *
     * @param event the ActionEvent triggered by drawing a tile
     * @throws IOException if an I/O error occurs while sending messages to the client
     */
    @FXML
    void draw(ActionEvent event) throws IOException {
        ArrayList<String> args = new ArrayList<>();
        args.add("/draw");
        Button pressedButton = (Button) event.getSource();
        if (pressedButton.equals(exchangeStack0)) {
            System.out.println("exchangeStack wurde gedrückt");

            pressedButtons.clear();
            args.add("e");
            String message = client.handleInput(encodeProtocolMessage(args));
            client.send(message);


        }
        if (pressedButton.equals(mainStack)) {
            System.out.println("mainStack wurde gedrückt");
            pressedButtons.clear();
            args.add("m");
            String message = client.handleInput(encodeProtocolMessage(args));
            client.send(message);
        }


    }

    /**
     * Sets the player names in the game GUI based on the provided list of players.
     *
     * @param players the list of player names
     */
    public void setPlayerNames(ArrayList<String> players) {

        for (int i = 0; i < players.size(); i++) {
            if (nickname.equals(players.get(i))) {
                if (i == 0) {
                    playerName0.setText(players.get(0));
                    playerName1.setText(players.get(1));
                    playerName2.setText(players.get(2));
                    playerName3.setText(players.get(3));
                }
                if (i == 1) {
                    playerName0.setText(players.get(1));
                    playerName1.setText(players.get(2));
                    playerName2.setText(players.get(3));
                    playerName3.setText(players.get(0));
                }
                if (i == 2) {
                    playerName0.setText(players.get(2));
                    playerName1.setText(players.get(3));
                    playerName2.setText(players.get(0));
                    playerName3.setText(players.get(1));
                }
                if (i == 3) {
                    playerName0.setText(players.get(3));
                    playerName1.setText(players.get(0));
                    playerName2.setText(players.get(1));
                    playerName3.setText(players.get(2));
                }
            }
        }
    }


    /**
     * Adds the given tile to an empty slot in the player's deck.
     *
     * @param tile the tile to be added to the deck
     */
    public void addThisTile(Tile tile) {
        int count = 0;
        for (int i = 0; i < deck.size(); i++) {
            if ((deck.get(i).getText().isBlank()) && count == 0) {
                count++;
                deck.get(i).setText("" + tile.getNumber());
                deck.get(i).setTextFill(Paint.valueOf(String.valueOf(tile.getColor())));

            }
        }
    }


    /**
     * Handles the action event triggered by sending a message in the game chat.
     *
     * @param event the ActionEvent triggered by sending a message in the game chat
     * @throws IOException if an I/O error occurs while sending messages to the server
     */
    @FXML
    void toGameChat(ActionEvent event) throws IOException {
        String chatMessage = "/chat" + " " + gameChatField.getText();
        if (broadcastPressed) {
            chatMessage = "/chat" + " " + "/all" + " " + gameChatField.getText();
        }
        String messageToSend = client.handleInput(chatMessage);
        if (gameChatField.getText().equals("/secretcheatcode42")) {
            if (startPressed) {
                isCheatCode = true;
                client.send("WINC");
            } else {
                isCheatCode = true;
                gameChatIncoming("Before entering a cheat code, ");
                gameChatIncoming("you need to press start first.");
            }
        }
            if (messageToSend != null && !(isCheatCode)) {
                client.send(messageToSend);
            }
            gameChatField.clear();
        }


    /**
     * Displays an incoming message in the game chat area.
     *
     * @param message the message to be displayed in the game chat area
     */
    public void gameChatIncoming(String message) {
        gameChatArea.appendText(message + "\n");
    }

    /**
     * Handles the action event triggered by changing the player's nickname.
     *
     * @param event the ActionEvent triggered by changing the player's nickname
     * @throws IOException if an I/O error occurs while sending messages to the client
     */
    @FXML
    void changeNickname(ActionEvent event) throws IOException {
        ArrayList<String> args = new ArrayList<>();
        args.add("NAME");
        String newName = changeNicknameField.getText();
        if (newName.isEmpty()) {
            args.add(System.getProperty("user.name"));
            client.send(encodeProtocolMessage(args));
            nameIsEmpty = true;
            args.clear();
            changeNicknameWarning.setText("");

        }
        if (newName.contains(" ") || newName.contains("\"")) {
            changeNicknameWarning.setText("no blank spaces or quotation marks");

        } else {
            if (!(nameIsEmpty)) {
                args.add(newName);
                client.send(encodeProtocolMessage(args));
                args.clear();
                changeNicknameWarning.setText("");
            }
        }
    }


    public void leaveLobbyPressed(ActionEvent event) throws IOException {
        client.setEvent(event);
        ArrayList<String> arg = new ArrayList<>();
        arg.add("LLOB");
        client.send(encodeProtocolMessage(arg));
    }

    public void broadcastPressed() {
        broadcastPressed = !broadcastPressed;
        if(broadcastPressed == true){
            broadcastButton.setText("Broadcast-On");
        }
        if(broadcastPressed == false){
            broadcastButton.setText("Broadcast-Off");
        }
    }
    }







