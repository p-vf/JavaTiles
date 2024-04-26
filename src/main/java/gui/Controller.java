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

public class Controller {

    public Button buttonSend;
    public TextField myTextField;
    @FXML
    public VBox lsVBox;
    public TextField lobbyTextfield;
    public Button readyButton;
    private static String input;

    public static Client client;
    public TextArea loginWarning;
    public VBox vBoxLobbies;

    public static String clientMessage;
    public TextArea chatArea;
    public TextField chatInput;
    public VBox playersLobbyVbox;
    public VBox highscoreVbox;


    private Stage stage;
    private Scene scene;
    private Parent root;
    private boolean isEmpty = false;

    public Controller() {
       client.setController(this);

    }


    public static void setClient(Client client) {
        Controller.client = client;
    }



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
     * This method is called by switchToScene to switch to a certain scene given the certain scene.
     * @param url the url of the scene to be switched to
     * @throws IOException
     */
    private void sceneSwitcher(ActionEvent event, String url) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(url));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        root.setStyle("-fx-background-color: #008000;");
        if(stage != null) {
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
     * This method sends the informationto what scene to switch to the sceneSwitcher,
     * based on the arguments given with a switch case.
     * @param arguments the arguments
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


            }
            } catch(IOException e){
                e.printStackTrace();
            }
        }


    public void chatInputPressed(ActionEvent event) throws IOException {
        String chatMessage = "/chat" + " " + chatInput.getText();
        String messageToSend = client.handleInput(chatMessage);
        client.send(messageToSend);
        chatInput.clear();
    }
    public void chatIncoming(String message){
        chatArea.appendText(message + "\n");
    }


    public void joinPressed(ActionEvent actionEvent) throws IOException {
        Platform.runLater(() -> {
            client.setEvent(actionEvent);
            ArrayList<String> arg = new ArrayList<>();
            arg.add("LGAM");
            arg.add("o");
            try {
                client.send(encodeProtocolMessage(arg));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
    public void setInput(String message) {
        Platform.runLater(() -> {
        vBoxLobbies.getChildren().clear();
        Label label = new Label(message);
        label.setFont(Font.font("Bold", FontWeight.BOLD, 13));
        label.setTextFill(Color.WHITE);
        vBoxLobbies.getChildren().add(label);
        });
    }


    public void refreshPressed() throws IOException {
        ArrayList<String> arg = new ArrayList<>();
        arg.add("LGAM");
        arg.add("o");
        client.send(encodeProtocolMessage(arg));

    }

    public void lobbyNumEntered(ActionEvent event) throws IOException {
        client.setEvent(event);
        ArrayList<String> arg = new ArrayList<>();
        arg.add("JLOB");
        String number = lobbyTextfield.getText();
        arg.add(number);
        client.send(encodeProtocolMessage(arg));

    }

    public void readyPressed(ActionEvent event) throws IOException {
        client.setEvent(event);
        ArrayList<String> arg = new ArrayList<>();
        arg.add("REDY");
        client.send(encodeProtocolMessage(arg));
    }

    public void leaveLobbyPressed(ActionEvent event) throws IOException {
        client.setEvent(event);
        ArrayList<String> arg = new ArrayList<>();
        arg.add("LLOB");
        client.send(encodeProtocolMessage(arg));
    }

    public void showPlayersInLobby(String message){
        clientMessage = message;
    }



    public void setReadyButton(String text){
        readyButton.setText(text);
    }

    public void showPlayersPressed() {
        playersLobbyVbox.getChildren().clear();
        Label label = new Label(clientMessage);
        label.setFont(Font.font("Bold", FontWeight.BOLD, 14));
        label.setTextFill(Color.WHITE);
        playersLobbyVbox.getChildren().add(label);

    }

    public void highscorePressed() throws IOException {
        ArrayList<String> arg = new ArrayList<>();
        arg.add("HIGH");
        client.send(encodeProtocolMessage(arg));
    }
    public void setHighscore(String result){
        Platform.runLater(() -> {
            Label label = new Label(result);
            label.setFont(Font.font("Bold", FontWeight.BOLD, 14));
            label.setTextFill(Color.WHITE);
            highscoreVbox.getChildren().add(label);
        });
    }
}

