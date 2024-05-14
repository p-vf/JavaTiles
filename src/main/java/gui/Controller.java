package gui;

import client.Client;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.scene.image.Image;

import static utils.NetworkUtils.encodeProtocolMessage;

/**
 * The Controller class manages the user interface and user interactions for the application.
 * It controls the various UI elements such as text fields, buttons, and displays.
 */
public class Controller implements Initializable {
    @FXML
    private TextArea whosOnlineTextArea;

    @FXML
    private Button broadcastButton;

    @FXML
    private Label lobbyWarning; //The label for displaying lobby warnings.

    @FXML
    private TextField myTextField; // The text field for user input in the login screen.

    @FXML
    private TextField lobbyTextfield; // The text field for user input in the login screen.

    @FXML
    private Button readyButton; // The button for indicating readiness to join a game

    public static Client client; // The client instance of our Client class

    @FXML
    private TextArea loginWarning; // The text area for displaying login warnings.

    @FXML
    private TextArea chatArea; // The text area for displaying chat messages.

    @FXML
    private TextField chatInput; // The text field for entering chat messages.

    @FXML
    private VBox playersLobbyVbox; ///The VBox container for displaying players in lobby.

    public String lobbyNumber; // The number of the lobby

    private boolean isEmpty = false; //Flag to check if the lobby is empty.

    private boolean nameIsEmpty = false; //Flag to check if the given nickname is empty

    @FXML
    private TextField changeNickname; //Text field for changing the nickname.

    @FXML
    private Label nicknameLabel; //Label for displaying the current nickname.

    @FXML
    private Label nicknameWarning; //Label for displaying a warning related to the nickname.

    private boolean broadcastPressed = false; //Flag indicating whether the broadcast button is pressed.

    private static String nickname; //Static variable holding the nickname.

    private static boolean showUsername = false; //Static variable indicating whether to show the username.

    @FXML
    private TextArea highScoreTextField; //TextArea component for displaying the high scores.

    @FXML
    private TextArea areaLobbies; //TextArea component for displaying available lobbies.


    /**
     * Constructor for the Controller class.
     * Initializes the controller with the given client.
     */
    public Controller() {
        client.setController(this);

    }

    /**
     * Sets the client object for this controller.
     *
     * @param client The client object to be set.
     */
    public static void setClient(Client client) {
        Controller.client = client;
    }


    /**
     * Handles the action event triggered by the login text area.
     * Sends a login message to the server with the entered nickname.
     * If the nickname is empty, uses the system username.
     *
     * @param event The ActionEvent triggered by the login text area.
     * @throws IOException If an I/O error occurs.
     */
    public void loginTextArea(ActionEvent event) throws IOException {
        client.setEvent(event);
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add("LOGI");
        String nickname = myTextField.getText();
        if (nickname.isEmpty()) {
            nickname = System.getProperty("user.name");
            arguments.add(nickname);
            client.send(encodeProtocolMessage(arguments));
            isEmpty = true;
            arguments.clear();
        }
        if (nickname.contains(" ") || nickname.contains("\"")) {
            loginWarning.setText("Your nickname mustn't contain blank spaces or quotation marks");
        } else {
            if (!(isEmpty)) {
                arguments.add(nickname);
                client.send(encodeProtocolMessage(arguments));
                arguments.clear();
            }
        }
    }

    /**
     * Sets the lobby warning text to the specified message.
     *
     * @param text The text to be set as the lobby warning.
     */
    public void setLobbyWarning(String text) {
        lobbyWarning.setText(text);

    }


    /**
     * Switches the scene to the one specified by the URL.
     *
     * @param event The ActionEvent that triggers the scene switch.
     * @param url   The URL of the scene to be switched to.
     * @throws IOException If an I/O error occurs during scene loading.
     */
    private void sceneSwitcher(ActionEvent event, String url) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(url));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene;
        if(!(url.contains("/ourgame.fxml"))){
        scene = new Scene(new StackPane(root), 900, 600);}
        else{
            scene = new Scene(new StackPane(root));
        }

        String backgroundImageURL = "/lobbyBackground.png";
        if (url.contains("/ourgame.fxml")) {
            backgroundImageURL = "/gameBackground.png";
        }

        Image backgroundImage = new Image(getClass().getResourceAsStream(backgroundImageURL));
        ImageView backgroundImageView = new ImageView(backgroundImage);

        backgroundImageView.fitWidthProperty().bind(scene.widthProperty());
        backgroundImageView.fitHeightProperty().bind(scene.heightProperty());

        StackPane stackPane = (StackPane) scene.getRoot();
        stackPane.getChildren().add(0, backgroundImageView);

        if (stage != null) {
            stage.setScene(scene);
            stage.setOnCloseRequest((WindowEvent ActionEvent) -> {
                System.out.println("Closing application...");
                System.exit(0);
            });

            stage.setResizable(false);
            if(url.contains("/ourgame.fxml")){
                stage.setResizable(true);
            }

            stage.setTitle("JavaTiles");

            stage.show();
        }
    }



    /**
     * Switches to the scene specified by the arguments.
     *
     * @param event     The ActionEvent that triggers the scene switch.
     * @param arguments The arguments specifying the scene to switch to.
     */
    public void switchToScene(ActionEvent event, String arguments) {
        String[] argumentsarray = arguments.split(" ");
        ArrayList<String> argumentslist = new ArrayList<>(Arrays.asList(argumentsarray));
        String inputCommand = argumentslist.remove(0);
        try {
            switch (inputCommand) {

                case "lobby":
                    showUsername=true;
                    sceneSwitcher(event, "/lobby.fxml");
                    break;

                case "lobbySelection":
                    sceneSwitcher(event, "/lobbySelection.fxml");
                    break;

                case "lobbyScreen":
                    sceneSwitcher(event, "/lobbyScreen.fxml");
                    break;


                case "startGame":
                    sceneSwitcher(event, "/ourgame.fxml");
                    break;


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Handles the action event triggered by pressing the chat input.
     * Sends the entered chat message to the server.
     *
     * @param event The ActionEvent triggered by pressing the chat input.
     * @throws IOException If an I/O error occurs during message sending.
     */
    public void chatInputPressed(ActionEvent event) throws IOException {
        String chatMessage = "/chat" + " " + chatInput.getText();
        if(broadcastPressed == true){
            chatMessage = "/chat" + " " + "/all" + " " + chatInput.getText();
        }
        String messageToSend = client.handleInput(chatMessage);
        if (messageToSend != null) {
            client.send(messageToSend);
        }
        chatInput.clear();
    }

    /**
     * Appends the incoming chat message to the chat area.
     *
     * @param message The chat message to be displayed.
     */
    public void chatIncoming(String message) {
        chatArea.appendText(message + "\n");
    }

    /**
     * Handles the action event triggered by pressing the join button.
     * Switches to the lobby selection scene.
     *
     * @param actionEvent The ActionEvent triggered by pressing the join button.
     * @throws IOException If an I/O error occurs during scene switching.
     */
    public void joinPressed(ActionEvent actionEvent) throws IOException {
        switchToScene(actionEvent, "lobbySelection");
        client.setEvent(actionEvent);
    }

    /**
     * Handles the action event triggered by pressing the refresh button.
     * Sends a request to refresh the lobby.
     *
     * @throws IOException If an I/O error occurs during message sending.
     */

    @FXML
    void openGames(ActionEvent event) throws IOException{
        ArrayList<String> arg = new ArrayList<>();
        arg.add("LGAM");
        arg.add("o");
        client.send(encodeProtocolMessage(arg));

    }
    @FXML
     void finishedGames(ActionEvent event) throws IOException{
        ArrayList<String> args = new ArrayList<>();
        args.add("LGAM");
        args.add("f");
        client.send(encodeProtocolMessage(args));

    }
@FXML
     void runningGames(ActionEvent event) throws IOException{
        ArrayList<String> argus = new ArrayList<>();
        argus.add("LGAM");
        argus.add("r");
        client.send(encodeProtocolMessage(argus));
    }

    /**
     * Updates the area displaying available lobbies with the given message.
     *
     * @param message the message to be displayed in the area
     */
    public void updateAreaLobbies(String message){
        areaLobbies.clear();
        areaLobbies.setVisible(true);
        areaLobbies.appendText(message);
    }

    /**
     * Handles the action event triggered by entering the lobby number.
     * Sends a request to join the specified lobby.
     *
     * @param event The ActionEvent triggered by entering the lobby number.
     * @throws IOException If an I/O error occurs during message sending.
     */
    public void lobbyNumEntered(ActionEvent event) throws IOException {
        client.setEvent(event);
        ArrayList<String> arg = new ArrayList<>();
        arg.add("JLOB");
        String number = lobbyTextfield.getText();
        this.lobbyNumber = number;
        arg.add(number);
        client.send(encodeProtocolMessage(arg));
    }

    /**
     * Handles the action event triggered by pressing the ready button.
     * Sends a message indicating readiness to the server.
     *
     * @param event The ActionEvent triggered by pressing the ready button.
     * @throws IOException If an I/O error occurs during message sending.
     */
    public void readyPressed(ActionEvent event) throws IOException {
        client.setEvent(event);
        ArrayList<String> arg = new ArrayList<>();
        arg.add("REDY");
        client.send(encodeProtocolMessage(arg));
    }

    /**
     * Handles the action event triggered by pressing the leave lobby button.
     * Sends a message indicating leaving the lobby to the server.
     *
     * @param event The ActionEvent triggered by pressing the leave lobby button.
     * @throws IOException If an I/O error occurs during message sending.
     */
    public void leaveLobbyPressed(ActionEvent event) throws IOException {
        client.setEvent(event);
        ArrayList<String> arg = new ArrayList<>();
        arg.add("LLOB");
        client.send(encodeProtocolMessage(arg));
    }



    /**
     * Sets the text of the ready button to the specified text.
     *
     * @param text The text to be set on the ready button.
     */
    public void setReadyButton(String text) {
        readyButton.setText(text);
    }

    /**
     * Displays the players in the lobby.
     */
    public void showPlayersPressed() throws IOException {
        ArrayList<String> args = new ArrayList<>();
        args.add("RNAM");
        client.send(encodeProtocolMessage(args));
    }

    /**
     * Displays the players in the lobby using the provided message.
     *
     * @param message the message containing information about players in the lobby
     */
    public void showPlayersInLobby(String message){
        playersLobbyVbox.getChildren().clear();
        Label label = new Label(message);
        label.setFont(Font.font("Bold", FontWeight.BOLD, 14));
        label.setTextFill(Color.WHITE);
        playersLobbyVbox.getChildren().add(label);
    }

    /**
     * Handles the action event triggered by pressing the highscore button.
     * Sends a request for highscores to the server.
     *
     * @throws IOException If an I/O error occurs during message sending.
     */
    public void highscorePressed() throws IOException {
        ArrayList<String> arg = new ArrayList<>();
        arg.add("HIGH");
        client.send(encodeProtocolMessage(arg));
    }

    /**
     * Sets the highscore text to the specified result.
     *
     * @param result The highscore result to be displayed.
     */
    public void setHighscore(String result) {
        Platform.runLater(() -> {
            highScoreTextField.setVisible(true);
            highScoreTextField.setText(result);
        });
    }

    /**
     * Sends a request to the server to show online players.
     *
     * @param event the ActionEvent triggering the method
     * @throws IOException if an I/O error occurs while sending the request
     */
    public void showOnlinePressed(ActionEvent event) throws IOException {
        ArrayList<String> arg = new ArrayList<>();
        arg.add("LPLA");
        client.send(encodeProtocolMessage(arg));
    }

    /**
     * Displays the online players in the user interface.
     *
     * @param message the message containing online players' information
     */
    public void showOnlinePlayers(String message){
        whosOnlineTextArea.clear();
        whosOnlineTextArea.setVisible(true);
        whosOnlineTextArea.appendText(message);
    }

    /**
     * Sets the nickname of the user.
     *
     * @param name the new nickname to set
     */
    public void setNickname(String name){
        nickname = name;
    }

    /**
     * Sets a new nickname for the user and updates the nickname label in the UI.
     *
     * @param name the new nickname to set
     */
    public void setNewNickname(String name){
        nickname = name;
        nicknameLabel.setText("Nickname: " + name);
    }

    /**
     * Sends a login request to the server when the start button is pressed.
     *
     * @param event the ActionEvent triggering the method
     * @throws IOException if an I/O error occurs while sending the login request
     */
    public void startPressed(ActionEvent event) throws IOException {
        client.setEvent(event);
        client.sendLogin();
    }

    /**
     * Handles the action when the user wants to change their username.
     *
     * @param event the ActionEvent triggering the method
     * @throws IOException if an I/O error occurs while sending the username change request
     */
    @FXML
    void toChangeUsername(ActionEvent event) throws IOException {
        ArrayList<String> args = new ArrayList<>();
        args.add("NAME");
        String newName = changeNickname.getText();
        if (newName.isEmpty()) {
            args.add(System.getProperty("user.name"));
            client.send(encodeProtocolMessage(args));
            nameIsEmpty = true;
            args.clear();
            nicknameWarning.setText("");

        }
        if (newName.contains(" ") || newName.contains("\"")) {
            nicknameWarning.setText("no blank spaces or quotation marks");

        } else {
            if (!(nameIsEmpty)) {
                args.add(newName);
                client.send(encodeProtocolMessage(args));
                args.clear();
                nicknameWarning.setText("");
            }
        }

    }

    /**
     * Toggles the broadcast state and updates the broadcast button text accordingly.
     */
    public void broadcastPressed() {
        broadcastPressed = !broadcastPressed;
        if(broadcastPressed == true){
            broadcastButton.setText("Broadcast-On");
        }
        if(broadcastPressed == false){
            broadcastButton.setText("Broadcast-Off");
        }
    }

    /**
     * Initializes the controller after its root element has been completely processed.
     *
     * @param location the location used to resolve relative paths for the root object
     * @param resources the resources used to localize the root object
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(nickname != null){
            nicknameLabel.setText("Nickname: " + nickname);
        }
    }

    /**
     * Opens the manual in a separate window.
     */
    public void manualOpened() {
    Stage stage = new Stage();
    stage.setTitle("Manual");

    ScrollPane scroll = new ScrollPane();
    VBox box = new VBox();
    Image firstImage = new Image("/Manual_JavaTiles-1.png");
    Image secImage = new Image("/Manual_JavaTiles-2.png");
    Image thirdImage = new Image("/Manual_JavaTiles-3.png");
    Image fourthImage = new Image("/Manual_JavaTiles-4.png");
    Image fifthImage = new Image("/Manual_JavaTiles-5.png");

    ImageView firstImageView = new ImageView();
    ImageView secImageView = new ImageView();
    ImageView thirdImageView = new ImageView();
    ImageView fourthImageView = new ImageView();
    ImageView fifthImageView = new ImageView();

    firstImageView.setImage(firstImage);
    firstImageView.setFitWidth(1200);
    firstImageView.setPreserveRatio(true);
    firstImageView.setSmooth(true);

    secImageView.setImage(secImage);
    secImageView.setFitWidth(1200);
    secImageView.setPreserveRatio(true);
    secImageView.setSmooth(true);

    thirdImageView.setImage(thirdImage);
    thirdImageView.setFitWidth(1200);
    thirdImageView.setPreserveRatio(true);
    thirdImageView.setSmooth(true);

    fourthImageView.setImage(fourthImage);
    fourthImageView.setFitWidth(1200);
    fourthImageView.setPreserveRatio(true);
    fourthImageView.setSmooth(true);

    fifthImageView.setImage(fifthImage);
    fifthImageView.setFitWidth(1200);
    fifthImageView.setPreserveRatio(true);
    fifthImageView.setSmooth(true);

    box.getChildren().addAll(firstImageView, secImageView, thirdImageView, fourthImageView, fifthImageView);
    scroll.setContent(box);
    Scene scene = new Scene(scroll,1280, 720);
    stage.setScene(scene);
    stage.show();
    }
}

