package gui;

import client.Client;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ControllerLogin {


    public Button buttonSend;
    public TextField myTextField;
    public static String input;
    public Client client;


    public void handleButtonClick() {
        System.out.println(input);

    }



    public void handleTextfield(ActionEvent event) {
        input = myTextField.getText();


    }
}
