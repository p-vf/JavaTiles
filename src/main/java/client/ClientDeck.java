package client;

import game.Color;
import game.Tile;

import java.util.Arrays;

public class ClientDeck {

    private Tile[][] deck;

    public ClientDeck(){
        this.deck = new Tile[2][12];
    }

    public Tile[][] getDeck() {
        return deck;
    }

    public Tile[][] createDeckwithTileArray(Tile[] tileArray){
        Tile[][] newDeck = new Tile[deck.length][deck[0].length];
        int count = 0;
        for(int i = 0; i<deck.length; i++){
            for(int j = 0; j<deck[i].length; j++){
                newDeck[i][j] = tileArray[j+count];
            }
            count = deck[0].length;
        }
        this.deck = newDeck;
        return newDeck;
    }

    public Tile[][] addTheseTiles(Tile... tileArray){
        int count = 0;
        if(tileArray.length>0){
            for(int i = 0; i <deck.length; i++){
                for(int j = 0; j < deck[0].length && count < tileArray.length; j++ ){
                    if(deck[i][j]==null){
                        deck[i][j] = tileArray[count];
                        count++;
                    }
                }
            }}
        return deck;}





    public void swap(int[] swapTile1, int[] swapTile2) {

      Tile tileToSwap = deck[swapTile1[0]][swapTile1[1]];
      Tile tileToSwap2 = deck[swapTile2[0]][swapTile2[1]];

        deck[swapTile2[0]][swapTile2[1]] = tileToSwap;
        deck[swapTile1[0]][swapTile1[1]] = tileToSwap2;

    }


    @Override
    public String toString() {
        return Arrays.deepToString(deck);
    }}





