package gui;

import client.Client;
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
import javafx.stage.Stage;

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
    private String input;

    public static Client client;
    public TextArea loginWarning;
    public VBox vBoxLobbies;

    public String clientMessage;


    private Stage stage;
    private Scene scene;
    private Parent root;

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
        }
        if(nickname.contains(" ")||nickname.contains("\"")){
            loginWarning.setText("Your nickname mustn't contain blank spaces or quotation marks");
        }
        else {
            arguments.add(nickname);
            client.send(encodeProtocolMessage(arguments));
        }
    }


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
        stage.setScene(scene);
        stage.show();


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

            }

            } catch(IOException e){
                e.printStackTrace();
            }
        }

    public void createPressed(ActionEvent actionEvent) {

    }

    public void joinPressed(ActionEvent actionEvent) throws IOException {
       client.setEvent(actionEvent);
       ArrayList<String> arg = new ArrayList<>();
       arg.add("LGAM");
       arg.add("o");
       client.send(encodeProtocolMessage(arg));
    }


    public void setInput(String message) {
        this.clientMessage = message;

    }


    public void refreshPressed(ActionEvent event) {


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

    public void leaveLobbyPressed(ActionEvent event) {
    }
}

