package gui;

import client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

import static utils.NetworkUtils.encodeProtocolMessage;

public class Controller {

    public Button buttonSend;
    public TextField myTextField;
    public static String input;

    public static Client client;

    private Stage stage;
    private Scene scene;
    private Parent root;

    public Controller() {
       //Default constructor with no parameters
    }

    public static void setClient(Client client) {
        Controller.client = client;
    }


    public void handleButtonClick(ActionEvent event) throws IOException {
        System.out.println(input);
        client.send("LOGI " + input);
        switchToLobbyScene(event);
    }

    private void switchToLobbyScene(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/lobby.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void handleTextfield(ActionEvent event) {
        input = myTextField.getText();


    }

    public void createPressed(ActionEvent actionEvent) {

    }

    public void joinPressed(ActionEvent actionEvent) {
    }
}
