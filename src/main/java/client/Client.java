package client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

import gui.ControllerGame;
import javafx.application.Platform;
import javafx.event.ActionEvent;

import game.Tile;
import gui.Controller;
import gui.GameGUI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static game.Tile.*;
import static utils.NetworkUtils.*;
import static utils.NetworkUtils.Protocol.ClientRequest;
import static utils.NetworkUtils.Protocol.ServerRequest;


/**
 * The Client class represents a client in the application. It connects to a server,
 * handles input/output operations, and communicates with the server.
 * <p>
 * This class includes attributes for managing the client's connection to the server,
 * handling user input, maintaining game state, and interacting with the GUI.
 * </p>
 * <p>
 * The client communicates with the server using a socket connection and implements a basic
 * protocol for exchanging messages.
 * </p>
 * <p>
 * It also interacts with a GUI for user interaction and provides methods to update
 * the GUI based on server responses.
 * </p>
 *
 * @author Boran Gökcen
 * @author Robin Gökcen
 * @author Pascal von Fellenberg
 * @version 1.0
 */
public class Client {

    private final Socket socket; // The socket for communication with the server

    private final OutputStream out; // Output stream to send messages to the server
    private final InputStream in; // Input stream to receive messages from the server
    private static final BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
    // Buffered reader for user input

    private Thread pingThread;// Thread responsible for sending periodic PING messages to the server.

    private String nickname; // Nickname of the player

    private int playerID = 4; //playerId in a particular game


    private int currentPlayerID = 5; //playerID of the player whose turn it is

    public static final Logger LOGGER = LogManager.getLogger(Client.class); //LOGGER for debugging

    private ClientDeck yourDeck = new ClientDeck(); //Deck of the player

    private Tile[] exchangeStacks; //Exchange stacks of the player for this particular round

    private Tile[] deckTiles; // Tiles representing the deck of the player


    private GameGUI gui; // GUI for the game

    private boolean pressedStart = false; // Flag indicating whether the player has pressed start

    private boolean lobby = false; //Whether the client is in a lobby or not

    private static Controller controller; //Controller for the GUI before the game starts

    private static ControllerGame gameController; //Controller for the GUI of the game


    private ActionEvent event; // Event for GUI interaction
    private ArrayList<String> playersInLobby = new ArrayList<>(); // List of players in the lobby


    /**
     * Constructs a new EchoClient with the given socket.
     *
     * @param socket the socket for communication with the server
     * @throws IOException if an I/O error occurs when creating the client
     */
    public Client(Socket socket) throws IOException {
        this.socket = socket;
        this.out = socket.getOutputStream();
        this.in = socket.getInputStream();
        this.gui = new GameGUI();
        Controller.setClient(this);
        ControllerGame.setClient(this);

    }


    /**
     * The main method to start the client.
     * It connects to the server, sets up input/output streams,
     * handles user input, and communicates with the server.
     *
     * @param args the command-line arguments to specify the server's hostname and port
     */
    public static void main(String[] args) {
        try {

            Socket sock = new Socket(args[0], Integer.parseInt(args[1]));
            Client client = new Client(sock);
            Thread thisThread = new Thread(client.gui);
            thisThread.start();
            InThread th = new InThread(client.in, client);
            Thread iT = new Thread(th);
            iT.start();


            client.ping(client);

            String loginData;

            if (args.length == 3) {
                boolean isValid = !(args[2].contains(" ") || args[2].contains("\""));
                if (isValid) {
                    loginData = "LOGI " + args[2];
                } else {
                    System.out.println("Invalid argument for username: must not contain any spaces or double quotes");
                    loginData = "LOGI " + client.login();
                }
            } else {
                loginData = "LOGI " + client.login();
            }
            client.send(loginData);


            String line = " ";
            while (true) {
                line = bReader.readLine();
                if (line.equalsIgnoreCase("QUIT")) {
                    break;
                }

                String messageToSend = client.handleInput(line);
                if (messageToSend == null || messageToSend.isEmpty()) {
                    continue;
                }
                // LOGGER.debug("sent: " + messageToSend);
                client.send(messageToSend);

            }

            System.out.println("terminating...");
            System.exit(0);
        } catch (IOException e) {
            System.out.println("Your connection to the server has been lost");
            System.out.println(e.getMessage());
            System.exit(0);


        }


    }

    /**
     * Prompts the user to enter a username and validates it.
     * If the entered username is empty, it defaults to the system username.
     * Ensures that the username does not contain blank spaces or quotation marks.
     *
     * @return The validated username entered by the user.
     * @throws IOException If an I/O error occurs while reading input.
     */
    private String login() throws IOException {
        while (true) {
            System.out.println("Enter username:");
            String username = bReader.readLine();
            if (username.isEmpty()) {
                username = System.getProperty("user.name");
                return username;
            }
            if (username.contains(" ") || username.contains("\"")) {
                System.out.println("Your nickname mustn't contain blank spaces or quotation marks");

            } else {
                return username;
            }
        }
    }


    /**
     * Sets the ActionEvent for GUI interaction.
     *
     * @param event the ActionEvent to be set
     */
    public void setEvent(ActionEvent event) {
        this.event = event;
    }


    /**
     * Sets the Controller for GUI interaction.
     *
     * @param controller the Controller to be set
     */
    public static void setController(Controller controller) {
        Client.controller = controller;
    }


    /**
     * Initiates a new PingThread for the specified EchoClient.
     * This method creates a new thread responsible for sending periodic PING messages
     * to the server to check for responsiveness.
     *
     * @param client the EchoClient for which to start the ping thread
     */
    private void ping(Client client) {
        client.pingThread = new ClientPingThread(client, 10000);
        client.pingThread.start();
    }


    /**
     * Sends a message to the server.
     *
     * @param str the message to send
     * @throws IOException if an I/O error occurs while sending the encoded message
     */
    public synchronized void send(String str) throws IOException {
        if (str != null) {
            out.write((str + "\r\n").getBytes());
        }
    }

    /**
     * Parses the user input and performs corresponding actions.
     *
     * @param input the input string provided by the user
     * @return a string representing the message to be sent to the server
     */
    public String handleInput(String input) throws IOException {
        try {
            String[] argumentsarray = input.split(" ");
            //LOGGER.debug(Arrays.toString(argumentsarray));
            ArrayList<String> arguments = new ArrayList<>(Arrays.asList(argumentsarray));
            String inputCommand = arguments.remove(0);
            String[] todebug = arguments.toArray(new String[0]);

            switch (inputCommand) {

                case "/nickname": //müsste man noch ändern falls man doch auf dem Terminal spielen möchte;
                    return encodeProtocolMessage("NAME", arguments.get(0));


                case "/chat":
                    if (arguments.get(0).equals("/all")) {
                        if (nickname == null) {
                            return null;
                        }
                        String allMessage = concatenateWords(1, arguments);

                        //LOGGER.debug(allMessage);
                        String allMessageForServer = encodeProtocolMessage("CATC", "b", allMessage);
                        return allMessageForServer;
                    }
                    if (arguments.get(0).equals("/w")) {
                        String whisperMessage = concatenateWords(2, arguments);
                        //LOGGER.debug(whisperMessage);
                        String whisperMessageForServer = encodeProtocolMessage("CATC", "w", whisperMessage, arguments.get(1));
                        return whisperMessageForServer;

                    } else {
                        if (lobby == true) {
                            String message = concatenateWords(0, arguments);
                            String messageForServer = encodeProtocolMessage("CATC", "l", message);
                            return messageForServer;
                        } else {
                            return null;
                        }
                    }

                case "/swap":
                    if (arguments.get(0).matches("\\d+") && arguments.get(1).matches("\\d+") && arguments.get(2).matches("\\d+") && arguments.get(3).matches("\\d+")) {
                        int row = Integer.parseInt(arguments.get(0));
                        int col = Integer.parseInt(arguments.get(1));
                        int row2 = Integer.parseInt(arguments.get(2));
                        int col2 = Integer.parseInt(arguments.get(3));

                        if ((row > 1) || (row2 > 1) || (col > 11) || (col2 > 11)) {
                            System.out.println("The max indices are: row:1 and column:11");
                            return null;
                        } else {
                            yourDeck.swap(row, col, row2, col2);
                            showDeck();

                            return null;
                        }
                    } else {
                        System.out.println("invalid command");
                        return null;
                    }

                case "/logout":
                    return encodeProtocolMessage("LOGO");

                case "/ready":
                    if (lobby == true) {
                        return encodeProtocolMessage("REDY");
                    } else {
                        System.out.println("You are not in a lobby right now. Please join a lobby first");
                        return null;
                    }

                case "/joinlobby":
                    if (arguments.size() > 0) {
                        String number = arguments.get(0);
                        try {
                            int num = Integer.parseInt(number);
                            return encodeProtocolMessage("JLOB", String.valueOf(num));
                        } catch (NumberFormatException e) {
                            Platform.runLater(() -> {
                                controller.setLobbyWarning("Please enter a number.");
                            });
                            System.out.println("Please enter a number.");
                            return null;
                        }
                    } else {
                        Platform.runLater(() -> {
                            controller.setLobbyWarning("You must provide a number to enter a lobby.");
                        });
                        System.out.println("You must provide a number to enter a lobby.");
                        return null;
                    }


                case "/draw":
                    if (currentPlayerID != playerID) {
                        System.out.println("You can only draw on your turn");
                        return null;
                    }
                    if (yourDeck.countTiles() >= 15) {
                        System.out.println("You've already drawn a tile");
                        return null;
                    }
                    if (arguments.get(0).equals("m")) {
                        gameController.disableStacks(true);
                        return encodeProtocolMessage("DRAW", "m");
                    }
                    if (arguments.get(0).equals("e")) {
                        gameController.disableStacks(true);
                        gameController.takeOffExchangeStack();

                        return encodeProtocolMessage("DRAW", "e");
                    } else {
                        System.out.println("Your draw command should look like /draw m or /draw e");
                        return null;
                    }

                case "/putt":
                    if (playerID != currentPlayerID) {
                        System.out.println("It's not your turn.");
                        gameController.setTextofGameWarning("It's currently not your turn.");
                        gameController.setCanYouPlayThisMove(false);
                        return null;
                    }
                    if (yourDeck.countTiles() < 15) {
                        System.out.println("You need to draw first.");
                        gameController.setTextofGameWarning("You need to draw first.");
                        gameController.setCanYouPlayThisMove(false);
                        return null;
                    }
                    if (!(arguments.get(0).matches("\\d+") && arguments.get(1).matches("\\d+"))) {
                        System.out.println("The indices should be numbers.");
                        return null;
                    }
                    int row = Integer.parseInt(arguments.get(0));
                    int column = Integer.parseInt(arguments.get(1));
                    if ((row > 1) || (column > 11)) {
                        System.out.println("The max indices are: row:1 and column:11");
                        return null;
                    }
                    if (yourDeck.getTile(row, column) == null) {
                        System.out.println("Please choose an existing Tile.");
                        return null;
                    }
                    Tile tileToPut = yourDeck.getTile(row, column);
                    yourDeck.removeTile(row, column); //removes the Tile from the deck;
                    String tileString = tileToPut.toString();
                    Tile[] tileArray = yourDeck.DeckToTileArray();
                    String DeckToBeSent = tileArrayToProtocolArgument(tileArray);
                    gameController.setCanYouPlayThisMove(true);

                    showDeck();
                    return encodeProtocolMessage("PUTT", tileString, DeckToBeSent);

                case "/listplayers":
                    return encodeProtocolMessage("LPLA");

                case "/listlobbies":
                    return encodeProtocolMessage("LLPL");

                case "/deck":
                    if (yourDeck != null) {
                        showDeck();
                    }
                    return null;

                case "/listgames":
                    if (arguments.get(0).equals("o")) {
                        return encodeProtocolMessage("LGAM", "o");

                    }
                    if (arguments.get(0).equals("r")) {
                        return encodeProtocolMessage("LGAM", "r");
                    }
                    if (arguments.get(0).equals("f")) {
                        return encodeProtocolMessage("LGAM", "f");
                    } else {
                        System.out.println("try: /listgames o, /listgames r or /listgames f");
                        return null;
                    }

                case "/secretcheatcode42":
                    if (playerID == currentPlayerID) {
                        return encodeProtocolMessage("WINC");
                    } else {
                        System.out.println("Shh... I know you want to use a cheat code but wait for your turn first.");
                        return null;
                    }

                case "/leavelobby":
                    return encodeProtocolMessage("LLOB");

                case "/highscore":
                    return encodeProtocolMessage("HIGH");


                default:
                    System.out.println(input);
                    return null;
            }
        } catch (IndexOutOfBoundsException e) {
            System.out.println("invalid command");
            return null;
        }
    }


    /**
     * Handles incoming requests from the server and performs corresponding actions.
     *
     * @param request the request received from the server
     */
    void handleRequest(String request) throws IOException {
        String requestCommand;
        ArrayList<String> arguments = decodeProtocolMessage(request);
        requestCommand = arguments.remove(0);
        try {
            ServerRequest requestType = ServerRequest.valueOf(requestCommand);

            switch (requestType) {

                case CATS:
                    if (nickname != null) {
                        String name = arguments.get(2);
                        if (arguments.get(0).equals("b")) {
                            controller.chatIncoming(name + " sent to all: " + arguments.get(1));
                            if (gameController != null) {
                                Platform.runLater(() -> {
                                    gameController.gameChatIncoming(name + " sent to all: " + arguments.get(1));
                                });
                            }
                        }
                        if (arguments.get(0).equals("w")) {
                            controller.chatIncoming(name + " whispered: " + arguments.get(1));
                            if (gameController != null) {
                                Platform.runLater(() -> {
                                    gameController.gameChatIncoming(name + " whispered: " + arguments.get(1));
                                });
                            }
                        }
                        if (arguments.get(0).equals("l")) {
                            controller.chatIncoming(name + ": " + arguments.get(1));
                            if (gameController != null) {
                                Platform.runLater(() -> {
                                    gameController.gameChatIncoming(name + ": " + arguments.get(1));
                                });
                            }
                        }
                    }
                    //hier handeln ob whisper broadcast etc mit case distinction


                    //
                    break;

                case PING:
                    send(encodeProtocolMessage("+PING"));
                    break;

                case STRT:
                    Platform.runLater(() -> {
                        changeScene("startGame");
                    });

                    Platform.runLater(() -> {
                        gameController.setNickname(nickname);
                    });

                    send(encodeProtocolMessage("RNAM"));
                    if (arguments.get(1).matches("\\d+")) {
                        playerID = Integer.parseInt(arguments.get(1));
                    }
                    ArrayList<String> tilesStrt = decodeProtocolMessage(arguments.get(0));
                    int tileCount = 0;
                    for (String tileElement : tilesStrt) {
                        if (tileElement.isEmpty()) {
                        } else {
                            tileCount++;
                        }

                    }
                    if (tileCount == 15) {
                        System.out.println("It's your turn.");
                        Platform.runLater(() -> {
                            try {
                                gameController.setTurnLabel("It's your turn.");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });

                        currentPlayerID = playerID;

                    } else {
                        try {
                            if (gameController != null) {
                                gameController.setTurnLabel("It's " + this.playersInLobby.get(Integer.parseInt(arguments.get(1))) + "'s turn.");
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    deckTiles = stringsToTileArray(tilesStrt);


                    yourDeck.setDeck(yourDeck.createDeckwithTileArray(deckTiles));
                    showDeck();

                    Platform.runLater(() -> {
                        gameController.disableStacks(true);
                    });


                    send(encodeProtocolMessage("+STRT"));
                    break;


                case PWIN:
                    System.out.println(arguments.get(0) + " won.");
                    Platform.runLater(() -> {
                        try {
                            gameController.setWinLabel(arguments.get(0) + " won.");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    break;

                case EMPT:
                    System.out.println("The game ended with a draw:");
                    Platform.runLater(() -> {
                        try {
                            gameController.setWinLabel("DRAW!");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    break;

                case NAMS:
                    System.out.println("Ich habe NAMS bekommen");
                    String thePlayers = arguments.get(0);
                    ArrayList<String> currentPlayers = decodeProtocolMessage(thePlayers);
                    playersInLobby.clear();
                    this.playersInLobby.addAll(currentPlayers);
                    if (gameController != null) {
                        Platform.runLater(() -> {
                            gameController.setPlayerNames(playersInLobby);
                        });
                    }

                    String[] nameArray = currentPlayers.toArray(new String[0]);

                    StringBuilder sb = new StringBuilder();
                    sb.append("The following players are in the lobby:\n");
                    for (int i = 0; i < nameArray.length; i++) {
                        if (nameArray[i].isEmpty()) {
                            sb.append("-----\n");
                        } else {
                            sb.append(nameArray[i]).append("\n");
                        }
                    }
                    String messageClient = sb.toString();

                    controller.showPlayersInLobby(messageClient);

                    System.out.println("The following players are in the lobby:");

                    for (int i = 0; i < nameArray.length; i++) {
                        if (nameArray[i].isEmpty()) {
                            System.out.println("-----");
                        } else {
                            System.out.println(nameArray[i]);
                        }
                    }

                    send(encodeProtocolMessage("+NAMS"));
                    break;

                case JOND:
                    send(encodeProtocolMessage("RNAM"));
                    System.out.println(arguments.get(0) + " joined the lobby");
                    Platform.runLater(() -> {
                        controller.chatIncoming(arguments.get(0) + " joined the lobby");
                    });

                    send(encodeProtocolMessage("+JOND"));
                    break;

                case LEFT:
                    send(encodeProtocolMessage("RNAM"));
                    System.out.println(arguments.get(0) + " left the lobby");
                    String message = arguments.get(0) + " left the lobby";
                    gameController.gameChatIncoming(message);
                    send(encodeProtocolMessage("+LEFT"));
                    break;

                case STAT:
                    ArrayList<String> tileList = decodeProtocolMessage(arguments.get(0));
                    exchangeStacks = stringsToTileArray(tileList);

                    showExchangeStacks();
                    if (Integer.parseInt(arguments.get(1)) == playerID) {
                        Tile tile = parseTile(tileList.get(playerID));
                        if (pressedStart) {
                            gameController.disableStacks(false);
                        }
                        Platform.runLater(() -> {
                            gameController.setExchangeStack0(tile);
                        });

                        System.out.println("It's your turn.");
                        currentPlayerID = playerID;
                        Platform.runLater(() -> {
                            try {
                                gameController.setTurnLabel("It's your turn.");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });

                    } else {
                        gameController.disableStacks(true);
                        Platform.runLater(() -> {
                            try {
                                if (arguments.get(1).matches("\\d+")) {
                                    gameController.setTurnLabel("It's " + this.playersInLobby.get(Integer.parseInt(arguments.get(1))) + "'s turn.");
                                }
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            Platform.runLater(() -> {
                                gameController.setExchangeStacks(exchangeStacks, playerID);
                            });
                        });
                        Platform.runLater(() -> {
                            gameController.setPlayerNames(playersInLobby);
                        });


                        send(encodeProtocolMessage("+STAT"));
                    }
                    break;


                default:
                    break;
            }
        } catch (IllegalArgumentException e) {
            LOGGER.error("request from server: \"" + request + "\" caused following Exception: " + e.toString());
        }
    }

    /**
     * Handles incoming responses from the server based on the specified protocol.
     * Decodes the received protocol message and performs corresponding actions
     * based on the type of response received.
     *
     * @param request The incoming protocol message received from the server.
     * @throws IOException If an I/O error occurs during message processing.
     */
    void handleResponse(String request) throws IOException {
        try {
            ArrayList<String> arguments = decodeProtocolMessage(request);
            String responsecommand = arguments.remove(0);
            String requestWithoutPlus = responsecommand.substring(1);
            ClientRequest responseType = ClientRequest.valueOf(requestWithoutPlus);
            switch (responseType) {

                case PING:
                    synchronized (pingThread) {
                        pingThread.notify();
                        //System.out.println("PING");
                    }
                    break;

                case LOGI:
                    Platform.runLater(() -> {
                        changeScene("lobby");
                    });

                    nickname = arguments.get(0);
                    System.out.println("You have been logged in as: " + arguments.get(0));
                    break;

                case NAME:
                    nickname = arguments.get(0);
                    gameController.setNickname(nickname);
                    System.out.println("Your nickname has been changed to: " + nickname);
                    break;

                case LOGO:
                    System.out.println("You have been logged out.");
                    logout();
                    break;

                case LGAM:

                    if (arguments.get(1).isEmpty()) {
                        System.out.println("No lobbies with this status");
                        String message = "No open lobbies available";
                        Platform.runLater(() -> {
                            controller.setInput(message);
                        });

                        break;
                    }


                    if (arguments.get(0).equals("o")) {


                        String argList = arguments.get(1);
                        String[] status = argList.split(" ");
                        String infos = String.join(":", status);
                        String[] splitString = infos.split(":");
                        int[] intArray = new int[splitString.length];

                        for (int i = 0; i < splitString.length; i++) {
                            intArray[i] = Integer.parseInt(splitString[i]);

                        }

                        int[] lobbies = new int[intArray.length / 2];
                        int[] players = new int[intArray.length / 2];

                        int indexLobby = 0;
                        int indexPlayer = 0;

                        for (int i = 0; i < intArray.length; i++) {
                            if (i % 2 == 0) {
                                lobbies[indexLobby] = intArray[i];
                                indexLobby++;
                            } else {
                                players[indexPlayer] = intArray[i];
                                indexPlayer++;
                            }
                        }

                        System.out.println("Open Lobbies");
                        System.out.println("Lobbynumber: \tNumber of players:");
                        for (int i = 0; i < lobbies.length; i++) {
                            System.out.println(lobbies[i] + "\t\t\t\t" + players[i]);
                        }

                        StringBuilder sb = new StringBuilder();

                        sb.append("Lobbynumber: \tNumber of players:\n");

                        for (int i = 0; i < lobbies.length; i++) {
                            sb.append(lobbies[i]).append("\t\t\t\t").append(players[i]).append("\n");
                        }
                        String message = sb.toString();
                        Platform.runLater(() -> {
                            controller.setInput(message);
                        });
                    }

                    if (arguments.get(0).equals("r")) {


                        String argList = arguments.get(1);
                        String[] status = argList.split(",");
                        String infos = String.join(":", status);
                        String[] splitString = infos.split(":");
                        int[] intArray = new int[splitString.length];

                        for (int i = 0; i < splitString.length; i++) {
                            intArray[i] = Integer.parseInt(splitString[i]);

                        }


                        System.out.println("Ongoing games");
                        System.out.println("Lobbynumber:");
                        for (int i = 0; i < intArray.length; i++) {
                            System.out.println(intArray[i]);
                        }
                    }

                    if (arguments.get(0).equals("f")) {

                        String argList = arguments.get(1);
                        String[] status = argList.split(",");
                        String infos = String.join(":", status);
                        String[] splitString = infos.split(":");
                        String[] StringArray = new String[splitString.length];

                        for (int i = 0; i < splitString.length; i++) {
                            StringArray[i] = splitString[i];

                        }

                        String[] lobbies = new String[StringArray.length / 2];
                        String[] winners = new String[StringArray.length / 2];

                        int indexLobby = 0;
                        int indexPlayer = 0;

                        for (int i = 0; i < StringArray.length; i++) {
                            if (i % 2 == 0) {
                                lobbies[indexLobby] = StringArray[i];
                                indexLobby++;
                            } else {
                                winners[indexPlayer] = StringArray[i];
                                indexPlayer++;
                            }
                        }

                        System.out.println("Finished games");
                        System.out.println("Lobbynumber: \tWinners:");
                        for (int i = 0; i < lobbies.length; i++) {
                            System.out.println(lobbies[i] + "\t\t\t\t" + winners[i]);
                        }
                    }
                    break;

                case JLOB:


                    String confirmation = arguments.get(0);
                    if (confirmation.equals("t")) {
                        Platform.runLater(() -> {
                            changeScene("lobbyScreen");
                        });
                        System.out.println("Joined lobby successfully");
                        lobby = true;
                    } else {
                        Platform.runLater(() -> {
                            controller.setLobbyWarning("The lobby with this lobby number is already full.");
                        });
                        System.out.println("Unsuccessful lobby connection");
                    }

                    break;

                case CATC:
                    if (arguments.get(0).equals("l")) {
                        Platform.runLater(() -> {
                            controller.chatIncoming("You: " + arguments.get(1));
                        });
                        if (gameController != null) {
                            Platform.runLater(() -> {
                                gameController.gameChatIncoming("You: " + arguments.get(1));
                            });
                        }
                        break;


                    }
                    if (arguments.get(0).equals("w")) {
                        Platform.runLater(() -> {
                            controller.chatIncoming("You whispered to " + arguments.get(2) + ": " + arguments.get(1));
                        });
                        if (gameController != null) {
                            Platform.runLater(() -> {
                                gameController.gameChatIncoming("You whispered to " + arguments.get(2) + ": " + arguments.get(1));
                            });
                        }
                        break;


                    }
                    if (arguments.get(0).equals("b")) {
                        Platform.runLater(() -> {
                            controller.chatIncoming("You sent to all: " + arguments.get(1));
                        });
                        if (gameController != null) {
                            Platform.runLater(() -> {
                                gameController.gameChatIncoming("You sent to all: " + arguments.get(1));
                            });
                        }
                        break;


                    }
                    break;


                case DRAW:
                    yourDeck.addTheseTiles(parseTile(arguments.get(0)));
                    Tile tile = parseTile(arguments.get(0));
                    Platform.runLater(() -> {
                        gameController.addThisTile(tile);
                    });


                    System.out.println("You have drawn: " + tile.toStringPretty());
                    Platform.runLater(() -> {
                        gameController.disableStacks(true);
                    });
                    showDeck();
                    break;

                case PUTT:
                    if (arguments.get(0).equals("t")) {
                        System.out.println("Valid input");

                        if (arguments.get(1).equals("t")) {
                            System.out.println("You won!");
                        }
                    } else {
                        System.out.println("Stop cheating!!");
                    }
                    break;

                case REDY:
                    System.out.println("ready to play!");
                    Platform.runLater(() -> {
                        controller.setReadyButton("Ready");
                    });
                    break;

                case LPLA:

                    ArrayList<String> playerList = decodeProtocolMessage(arguments.get(0));
                    for (int i = 0; i < playerList.size(); i++) {
                        if (!(arguments.get(0).equals("null"))) {
                            System.out.println(playerList.get(i));
                        }
                    }
                    StringBuilder concatenatedString = new StringBuilder();

                    for (String player : playerList) {
                        if (!player.equals("null")) {
                            concatenatedString.append(player).append("\n");
                        }
                    }
                    String onlinePlayers = concatenatedString.toString();
                    controller.showOnlinePlayers(onlinePlayers);
                    break;

                case LLPL:
                    System.out.println(getBeautifullyFormattedDecodedLobbiesWithPlayerList(arguments.get(0)));
                    break;

                case LLOB:
                    Platform.runLater(() -> {
                        changeScene("lobbySelection");
                    });

                    if (arguments.get(0).equals("t")) {
                        System.out.println("Lobby left successfully");
                    }
                    if (arguments.get(0).equals("f")) {
                        System.out.println("You have to be in a lobby to leave");
                    }
                    break;


                case WINC:
                    ArrayList<String> cheatTiles = decodeProtocolMessage(arguments.get(0));
                    Tile[] tilesArray = stringsToTileArray(cheatTiles);
                    Tile[][] newDeck = yourDeck.createDeckwithTileArray(tilesArray);
                    yourDeck.setDeck(newDeck);
                    showDeck();
                    break;

                case HIGH:
                    ArrayList<String> players = decodeProtocolMessage(arguments.get(0));
                    for (int i = 0; i < players.size(); i++) {
                        System.out.println(i + 1 + ". " + players.get(i));
                    }
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < players.size(); i++) {
                        stringBuilder.append(i + 1).append(". ").append(players.get(i)).append("\n");
                    }
                    String result = stringBuilder.toString();
                    if (!result.isEmpty()) {
                        controller.setHighscore("Highscore-List: " + "\n" + result);
                    } else {
                        controller.setHighscore("No highscores were made yet");
                    }
                    break;

                case RNAM:
                    System.out.println("Ich habe RNAM bekommen.");
                    String thePlayers = arguments.get(0);
                    ArrayList<String> currentPlayers = decodeProtocolMessage(thePlayers);
                    playersInLobby.clear();
                    this.playersInLobby.addAll(currentPlayers);
                    if (gameController != null) {
                        Platform.runLater(() -> {
                            gameController.setPlayerNames(playersInLobby);
                        });
                    }
                    break;


                default:
                    break;
            }
        } catch (IllegalArgumentException e) {
            LOGGER.error("response from server: \"" + request + "\" caused the following Exception: " + e.toString());
        }
    }

    /**
     * Displays the contents of the player's deck in a formatted manner.
     * This method prints out the player's deck to the console in a visually
     * readable format using the `toStringPretty` method of the `yourDeck` object.
     * The deck contents are printed as a list of cards or items contained within
     * the deck.
     */
    private void showDeck() {
        System.out.println("your Deck:");
        System.out.println(yourDeck.toStringPretty());
    }

    /**
     * Changes the scene in the GUI based on the provided argument.
     * <p>
     * This method invokes the {@code switchToScene} method in the {@code controller} object,
     * passing the {@code event} and {@code argument} as parameters to facilitate the scene change.
     * </p>
     *
     * @param argument the argument specifying the scene to switch to
     */
    private void changeScene(String argument) {
        controller.switchToScene(event, argument);
    }

    /**
     * Displays the current state of the exchange stacks in a formatted manner.
     * This method constructs and prints a visual representation of the exchange stacks,
     * which are used in a game scenario involving multiple players.
     * <p>
     * The method generates a textual representation consisting of rows and columns:
     * - The top row indicates the active player's position with "vvvv" underneath their stack.
     * - Each exchange stack is displayed within vertical bars ('|') in the middle row.
     * If a stack is empty (null), it's represented as empty spaces ('  ').
     * Non-empty stacks display their contents using the `toStringPretty` method.
     * - The bottom row again highlights the active player's position with "^^^^" above their stack.
     * <p>
     * This representation helps visualize the state of exchange stacks during gameplay,
     * providing information about the contents of each stack and indicating the current player's turn.
     */
    private void showExchangeStacks() {
        StringBuilder res = new StringBuilder();
        res.append("Exchange stacks:\n");
        for (int i = 0; i < 4; i++) {
            if (i == playerID) {
                res.append("vvvv");
            } else {
                res.append("   ");
            }
        }
        res.append("\n");
        res.append("|");
        for (int i = 0; i < 4; i++) {
            if (exchangeStacks[i] == null) {
                res.append("  ");
            } else {
                res.append(exchangeStacks[i].toStringPretty());
            }
            res.append("|");
        }
        res.append("\n");
        for (int i = 0; i < 4; i++) {
            if (i == playerID) {
                res.append("^^^^");
            } else {
                res.append("   ");
            }
        }
        System.out.println(res.toString());
    }

    /**
     * Concatenates words from the specified index in the provided list of arguments.
     * <p>
     * This method concatenates words starting from the specified {@code start} index in the {@code arguments} list,
     * combining them into a single string separated by spaces.
     * </p>
     *
     * @param start     the starting index from which to concatenate words
     * @param arguments the list of arguments containing words to concatenate
     * @return the concatenated string of words
     */
    private String concatenateWords(int start, ArrayList<String> arguments) {
        String message = arguments.get(start);
        for (int i = start + 1; i < arguments.size(); i++) {
            message += " " + arguments.get(i);
        }
        return message;

    }

    /**
     * Logs out the client from the server.
     * Closes the socket, input and output streams.
     */
    void logout() {
        try {
            System.exit(0);
            socket.close();
            bReader.close();
            out.close();
            System.exit(0);
            System.out.println("You have been logged out.");

        } catch (IOException e) {
            System.out.println("You have been logged out.");
            System.exit(0);
        }
    }

    /**
     * Sets the controller of the game for the client.
     * <p>
     * This method sets the {@code gameController} attribute of the client to the provided {@code gameController} object.
     * </p>
     *
     * @param gameController the controller of the game to be set
     */

    public void setgameController(ControllerGame gameController) {
        this.gameController = gameController;
    }

    /**
     * Retrieves the deck of the client.
     *
     * @return an array of tiles representing the deck.
     */
    public Tile[] getTiles() {
        return deckTiles;
    }

    /**
     * Sets the flag indicating whether the player has pressed start.
     * <p>
     * This method sets the {@code pressedStart} attribute of the client to the specified boolean value.
     * </p>
     *
     * @param bool the boolean value indicating whether the game has started
     */
    public void setPressedStart(boolean bool) {
        pressedStart = bool;
    }
}


