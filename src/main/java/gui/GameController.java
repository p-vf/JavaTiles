package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.util.ArrayList;


public class GameController {

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

    private ArrayList<Button> buttons;


    @FXML
    void pressTile(ActionEvent event) {

        Button pressedButton = (Button) event.getTarget();
        pressedButtons.add(pressedButton);

        if (pressedButtons.size() == 1) {

            if (pressedButton.equals(exchangeStack)) {
                System.out.println("exchangeStack wurde gedrückt");
                pressedButtons.clear();
            }
            if (pressedButton.equals(mainStack)) {
                System.out.println("mainStack wurde gedrückt");
                pressedButtons.clear();
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
    }
}


