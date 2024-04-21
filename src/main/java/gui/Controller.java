package gui;

import client.Client;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;

import static utils.NetworkUtils.encodeProtocolMessage;

public class Controller {

    public Button buttonSend;
    public TextField myTextField;
    public static String input;

    public static Client client;

    public Controller() {
       //Default constructor with no parameters
    }

    public static void setClient(Client client) {
        Controller.client = client;
    }



    public void handleButtonClick() throws IOException {
        System.out.println(input);
        client.send("LOGI " + input);


    }



    public void handleTextfield(ActionEvent event) {
        input = myTextField.getText();


    }
}
