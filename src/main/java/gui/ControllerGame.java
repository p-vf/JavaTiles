package gui;

import client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;


public class ControllerGame implements Initializable {

    @FXML
    private Button exchangeStack;

    @FXML
    private Button mainStack;

    @FXML
    private Button puttButton;

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
    private ArrayList<Button> pressedButtons = new ArrayList<Button>();

    private ArrayList<Button> deck;

    private Client client;


    public static void setClient(Client client) {
        Controller.client = client;
    }


    @FXML
    void deleteSelection(MouseEvent event) {
        pressedButtons.clear();

    }

    @FXML
    void pressTile(ActionEvent event) {
        try {

            Button pressedButton = (Button) event.getTarget();
            pressedButtons.add(pressedButton);

            if (pressedButtons.size() == 1) {

                if (pressedButton.equals(exchangeStack)) {
                    System.out.println("exchangeStack wurde gedrückt");
                    pressedButtons.clear();
                    client.handleInput("/draw e");
                }
                if (pressedButton.equals(mainStack)) {
                    System.out.println("mainStack wurde gedrückt");
                    pressedButtons.clear();
                    client.handleInput("/draw m");
                }
            }
            if (pressedButtons.size() == 2) {
                Button firstButton = pressedButtons.get(0);
                Button secondButton = pressedButtons.get(1);

                if (firstButton.equals(puttButton) ^ secondButton.equals(puttButton)) {
                    if (firstButton.equals(puttButton)) {
                        System.out.println(secondButton.getText() + " wurde geputtet");
                    } else {
                        System.out.println(firstButton.getText() + " wurde geputtet");
                    }
                    pressedButtons.clear();
                }
                if (!(pressedButtons.contains(puttButton)) && pressedButtons.size() == 2) {
                    String firstTile = firstButton.getText();
                    String secondTile = secondButton.getText();

                    firstButton.setText(secondTile);
                    secondButton.setText(firstTile);

                    pressedButtons.clear();
                }
                pressedButtons.clear();

            }
        } catch (IOException e) {
            System.out.println("an IOException occurred");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        deck = new ArrayList<>(Arrays.asList(zero0, zero1, zero2, zero3, zero4,zero5, zero6, zero7, zero8, zero9,zero10, zero11, one0, one1, one2, one3, one4, one5, one6, one7, one8, one9, one10,one11));
    }
}


