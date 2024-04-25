package gui;

import client.Client;
import game.Tile;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import static org.apache.commons.lang3.StringUtils.substring;
import static utils.NetworkUtils.encodeProtocolMessage;


public class ControllerGame implements Initializable {

    @FXML
    private TextField gameWarning;

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

    private static Client client;

    private Tile[] tiles;

    private boolean canYouPlayThisMove = false; //falls ja das Deck auf dem GUI updaten sonst nicht

    public ControllerGame(){
        client.setgameController(this);
    }

    public static void setClient(Client client) {
        ControllerGame.client = client;
    }

    public void fillTiles(Tile[] givenTiles) {
        tiles = new Tile[givenTiles.length];
        for (int i = 0; i < givenTiles.length; i++) {
            tiles[i] = givenTiles[i];
        }
    }
    @FXML
    void showDeck(ActionEvent event) throws IOException {
        this.tiles = client.getTiles();
        for (int i = 0; i < deck.size(); i++) {
            if(tiles[i]==null){
                deck.get(i).setText("");
            }
            else{
            deck.get(i).setText("" + tiles[i].getNumber());
            deck.get(i).setTextFill(Paint.valueOf(String.valueOf(tiles[i].getColor())));}
            client.send(encodeProtocolMessage("+STRT"));

        }
    }

    public void setTextofGameWarning(String text){
        gameWarning.setVisible(true);
        gameWarning.setText(text);
    }

    public void setCanYouPlayThisMove(boolean canYouPlayThisMove){
        this.canYouPlayThisMove = canYouPlayThisMove;
    }

    int[] TilePosition (Button button){
        int[] position = new int[2];
        if(button.getId().contains("zero")){
            position[0] = 0;
            String columnString = button.getId().substring(4);
            position[1] = Integer.valueOf(columnString);
            return position;
        }
        else{
            position[0] = 1;
            String columnString = button.getId().substring(3);
            position[1] = Integer.valueOf(columnString);
            return position;
            
        }
    }




    @FXML
    void deleteSelection(MouseEvent event) {
        pressedButtons.clear();

    }

    @FXML
    void pressTile(ActionEvent event) throws IOException {

        Button pressedButton = (Button) event.getTarget();
        pressedButtons.add(pressedButton);

        if (pressedButtons.size() == 2) {
            Button firstButton = pressedButtons.get(0);
            Button secondButton = pressedButtons.get(1);

            if (firstButton.equals(puttButton) ^ secondButton.equals(puttButton)) {
                ArrayList<String> args = new ArrayList<String>();
                if (firstButton.equals(puttButton)) {
                    if(secondButton.getText().isBlank()){

                        gameWarning.setVisible(true);
                        gameWarning.setText("choose an existing Tile");


                    }
                    else{
                        int [] tilePosition = TilePosition(secondButton);
                        gameWarning.setVisible(true);
                        //gameWarning.setText("Button ist am Ort"+ tilePosition[0]+" "+ tilePosition[1]);
                        args.add("/putt");
                        args.add(tilePosition[0]+"");
                        args.add(tilePosition[1]+"");
                        System.out.println(encodeProtocolMessage(args));
                        String message = client.handleInput(encodeProtocolMessage(args));
                        System.out.println(message);
                        client.send(message);
                        if(canYouPlayThisMove){
                            secondButton.setText("");
                        }

                    }
                    pressedButtons.clear();
                } else {
                    gameWarning.setText("Please press on the puttButton first and then the chosen Tile for putting");
                    System.out.println("nichts passiert.");
                    pressedButtons.clear();
                }
            }
            else{
                String firstTile = firstButton.getText();
                String secondTile = secondButton.getText();
                Paint firstTilePaint = firstButton.getTextFill();
                Paint secondTilePaint = secondButton.getTextFill();

                firstButton.setText(secondTile);
                firstButton.setTextFill(secondTilePaint);
                secondButton.setText(firstTile);
                secondButton.setTextFill(firstTilePaint);

                pressedButtons.clear();
            }
            pressedButtons.clear();

        }
    }
    @FXML
    void draw(ActionEvent event) {
        Button pressedButton = (Button) event.getSource();
        if (pressedButton.equals(exchangeStack)) {
            System.out.println("exchangeStack wurde gedrückt");
            pressedButtons.clear();
            //client.handleInput("/draw e");

        }
        if(pressedButton.equals(mainStack)){
            System.out.println("mainStack wurde gedrückt");
            pressedButtons.clear();
            //client.handleInput("/draw m");
        }


    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        deck = new ArrayList<>(Arrays.asList(zero0, zero1, zero2, zero3, zero4,zero5, zero6, zero7, zero8, zero9,zero10, zero11, one0, one1, one2, one3, one4, one5, one6, one7, one8, one9, one10,one11));
        client.setgameController(this);
    }
}


