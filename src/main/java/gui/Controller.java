package gui;

import client.Client;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import static utils.NetworkUtils.encodeProtocolMessage;

/**
 * The Controller class manages the user interface and user interactions for the application.
 * It controls the various UI elements such as text fields, buttons, and displays.
 */
public class Controller {

    /** The label for displaying lobby warnings. */
    @FXML
    private Label lobbyWarning;
    /** The text field for user input in the login screen. */
    public TextField myTextField;
    /** The text field for lobby input in lobby screen. */
    public TextField lobbyTextfield;
    /** The button for indicating readiness to join lobby. */
    public Button readyButton;
    /** The client instance used for communication. */

    public static Client client;
    /** The text area for displaying login warnings. */
    public TextArea loginWarning;
    /** The VBox container for lobby views. */
    public VBox vBoxLobbies;
    /**
     * The clientMessage field stores the message received from the client.
     * It is used to display information about players in the lobby.
     */
    public static String clientMessage;
    /** The text area for displaying chat messages. */
    public TextArea chatArea;
    /** The text field for entering chat messages. */
    public TextField chatInput;
    /** The VBox container for displaying players in lobby. */
    public VBox playersLobbyVbox;
    /** The VBox container for displaying highscores. */
    public VBox highscoreVbox;
    /** The text area for displaying lobby selection. */
    @FXML
    private TextArea lobbySelection;
    /** Flag to check if the lobby is empty. */
    private boolean isEmpty = false;

    /**
     * Constructor for the Controller class.
     * Initializes the controller with the given client.
     */
    public Controller() {
       client.setController(this);

    }

    /**
     * Sets the client object for this controller.
     * @param client The client object to be set.
     */
    public static void setClient(Client client) {
        Controller.client = client;
    }


    /**
     * Handles the action event triggered by the login text area.
     * Sends a login message to the server with the entered nickname.
     * If the nickname is empty, uses the system username.
     * @param event The ActionEvent triggered by the login text area.
     * @throws IOException If an I/O error occurs.
     */
    public void loginTextArea(ActionEvent event) throws IOException {
        client.setEvent(event);
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add("LOGI");
        String nickname = myTextField.getText();
        if(nickname.isEmpty()){
            nickname = System.getProperty("user.name");
            arguments.add(nickname);
            client.send(encodeProtocolMessage(arguments));
            isEmpty = true;
            arguments.clear();
        }
        if(nickname.contains(" ")||nickname.contains("\"")){
            loginWarning.setText("Your nickname mustn't contain blank spaces or quotation marks");
        }
        else{
        if (!(isEmpty)){
            arguments.add(nickname);
            client.send(encodeProtocolMessage(arguments));
            arguments.clear();
        }
    }}

    /**
     * Sets the lobby warning text to the specified message.
     * @param text The text to be set as the lobby warning.
     */
    public void setLobbyWarning(String text){
        lobbyWarning.setText(text);

    }


    /**
     * Switches the scene to the one specified by the URL.
     * @param event The ActionEvent that triggers the scene switch.
     * @param url The URL of the scene to be switched to.
     * @throws IOException If an I/O error occurs during scene loading.
     */
    private void sceneSwitcher(ActionEvent event, String url) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(url));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        root.setStyle("-fx-background-color: #008000;");
        if(stage!= null) {

            stage.setScene(scene);
            stage.setOnCloseRequest((WindowEvent ActionEvent) -> {
                System.out.println("Closing application...");
                System.exit(0);
            });
            stage.setResizable(true);
            stage.show();
        }

    }


    /**
     * Switches to the scene specified by the arguments.
     * @param event The ActionEvent that triggers the scene switch.
     * @param arguments The arguments specifying the scene to switch to.
     */
    public void switchToScene(ActionEvent event, String arguments) {
        String[] argumentsarray = arguments.split(" ");
        ArrayList<String> argumentslist = new ArrayList<>(Arrays.asList(argumentsarray));
        String inputCommand = argumentslist.remove(0);
        try {
            switch (inputCommand) {

                case "lobby":
                    sceneSwitcher(event,"/lobby.fxml");
                    break;

                case "lobbySelection":
                    sceneSwitcher(event,"/lobbySelection.fxml");
                    break;

                case "lobbyScreen":
                    sceneSwitcher(event,"/lobbyScreen.fxml");
                    break;


                case "startGame":
                    sceneSwitcher(event,"/ourgame.fxml");
                    break;


            }
            } catch(IOException e){
                e.printStackTrace();
            }
        }


    /**
     * Handles the action event triggered by pressing the chat input.
     * Sends the entered chat message to the server.
     * @param event The ActionEvent triggered by pressing the chat input.
     * @throws IOException If an I/O error occurs during message sending.
     */
    public void chatInputPressed(ActionEvent event) throws IOException {
        String chatMessage = "/chat" + " " + chatInput.getText();
        String messageToSend = client.handleInput(chatMessage);
        if(messageToSend != null){
        client.send(messageToSend);}
        chatInput.clear();
    }

    /**
     * Appends the incoming chat message to the chat area.
     * @param message The chat message to be displayed.
     */
    public void chatIncoming(String message){
        chatArea.appendText(message + "\n");
    }

    /**
     * Handles the action event triggered by pressing the join button.
     * Switches to the lobby selection scene.
     * @param actionEvent The ActionEvent triggered by pressing the join button.
     * @throws IOException If an I/O error occurs during scene switching.
     */
    public void joinPressed(ActionEvent actionEvent) throws IOException {

            switchToScene(actionEvent,"lobbySelection");
            client.setEvent(actionEvent);
    }

    /**
     * Sets the lobby selection text to the specified message.
     * @param message The message to be set as the lobby selection text.
     */
    public void setLobbySelection(String message){
        lobbySelection.setText(message);
    }

    /**
     * Sets the input message to the lobby view.
     * @param message The message to be displayed in the lobby view.
     */
    public void setInput(String message) {

        vBoxLobbies.getChildren().clear();
        Label label = new Label(message);
        label.setFont(Font.font("Bold", FontWeight.BOLD, 13));
        label.setTextFill(Color.WHITE);
        vBoxLobbies.getChildren().add(label);

    }

    /**
     * Handles the action event triggered by pressing the refresh button.
     * Sends a request to refresh the lobby.
     * @throws IOException If an I/O error occurs during message sending.
     */
    public void refreshPressed() throws IOException {
        ArrayList<String> arg = new ArrayList<>();
        arg.add("LGAM");
        arg.add("o");
        client.send(encodeProtocolMessage(arg));

    }

    /**
     * Handles the action event triggered by entering the lobby number.
     * Sends a request to join the specified lobby.
     * @param event The ActionEvent triggered by entering the lobby number.
     * @throws IOException If an I/O error occurs during message sending.
     */
    public void lobbyNumEntered(ActionEvent event) throws IOException {
        client.setEvent(event);
        ArrayList<String> arg = new ArrayList<>();
        arg.add("JLOB");
        String number = lobbyTextfield.getText();
        arg.add(number);
        client.send(encodeProtocolMessage(arg));

    }

    /**
     * Handles the action event triggered by pressing the ready button.
     * Sends a message indicating readiness to the server.
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
     * Stores the message received from the server about players in the lobby.
     * @param message The message received from the server.
     */
    public void showPlayersInLobby(String message){
        clientMessage = message;
    }


    /**
     * Sets the text of the ready button to the specified text.
     * @param text The text to be set on the ready button.
     */
    public void setReadyButton(String text){
        readyButton.setText(text);
    }

    /**
     * Displays the players in the lobby.
     */
    public void showPlayersPressed() {
        playersLobbyVbox.getChildren().clear();
        Label label = new Label(clientMessage);
        label.setFont(Font.font("Bold", FontWeight.BOLD, 14));
        label.setTextFill(Color.WHITE);
        playersLobbyVbox.getChildren().add(label);

    }

    /**
     * Handles the action event triggered by pressing the highscore button.
     * Sends a request for highscores to the server.
     * @throws IOException If an I/O error occurs during message sending.
     */
    public void highscorePressed() throws IOException {
        ArrayList<String> arg = new ArrayList<>();
        arg.add("HIGH");
        client.send(encodeProtocolMessage(arg));
    }

    /**
     * Sets the highscore text to the specified result.
     * @param result The highscore result to be displayed.
     */
    public void setHighscore(String result){
        Platform.runLater(() -> {
            highscoreVbox.getChildren().clear();
            Label label = new Label(result);
            label.setFont(Font.font("Bold", FontWeight.BOLD, 14));
            label.setTextFill(Color.WHITE);
            highscoreVbox.getChildren().add(label);
        });
    }
}

