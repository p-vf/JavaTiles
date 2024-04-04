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

    public Tile getTile(int column, int row){
        return deck[column][row];
    }


    public void createDeckwithTileArray(Tile[] tileArray){
        Tile[][] newDeck = new Tile[deck.length][deck[0].length];
        int count = 0;
        loop:
        {
            for (int i = 0; i < deck.length; i++) {
                for (int j = 0; j < deck[i].length; j++) {
                    if (count >= tileArray.length) {
                        break loop;
                    }
                    newDeck[i][j] = tileArray[count++];
                }
            }
        }
        this.deck = newDeck;
    }

    public Tile[] DeckToTileArray(){
        Tile[] tileArray = new Tile[24];
        int count = 0;
        for(int i = 0; i<deck.length; i++){
            for(int j = 0; j<deck[i].length; j++){
                tileArray[j+count] = deck[i][j];
            }
            count = deck[0].length;
        }
        return tileArray;
    }


    public void addTheseTiles(Tile... tileArray) {
        int count = 0;
        if (tileArray.length > 0) {
            for (int i = 0; i < deck.length; i++) {
                for (int j = 0; j < deck[0].length && count < tileArray.length; j++) {
                    if (deck[i][j] == null) {
                        deck[i][j] = tileArray[count];
                        count++;
                    }
                }
            }
        }
    }



    public void removeTile(int row, int column){
      Tile[][] newDeck = new Tile[deck.length][deck[0].length];
      for (int i = 0; i<deck.length; i++){
          for(int j = 0; j<deck[i].length; j++){
              if((i != row) || (j !=column)){
                  newDeck[i][j] = deck[i][j];
              }

          }
      }
      for(int k = 0; k<deck.length; k++){
          for(int l = 0; l<deck[k].length; l++){
              deck[k][l] = newDeck[k][l];
          }
      }
    }



    public void swap(int row, int column, int row1, int column1) {

      Tile tileToSwap = deck[row1][column1];
      Tile tileToSwap2 = deck[row][column];

        deck[row][column] = tileToSwap;
        deck[row1][column1] = tileToSwap2;

    }


    @Override
    public String toString() {
        return Arrays.deepToString(deck);
    }

    public static void main(String[] args){
        Tile[] tileArray = new Tile[24];

        tileArray[0] = new Tile(0, Color.BLACK);
        tileArray[1] = new Tile(1, Color.YELLOW);
        tileArray[2] = new Tile(2, Color.BLUE);
        tileArray[3] = new Tile(3, Color.RED);
        tileArray[4] = new Tile(4, Color.BLACK);
        tileArray[5] = new Tile(5, Color.YELLOW);
        tileArray[6] = new Tile(6, Color.BLUE);
        tileArray[7] = new Tile(7, Color.RED);
        tileArray[8] = new Tile(8, Color.BLACK);
        tileArray[9] = new Tile(9, Color.YELLOW);
        tileArray[10] = new Tile(10, Color.BLUE);
        tileArray[11] = new Tile(11, Color.RED);
        tileArray[12] = new Tile(12, Color.BLACK);
        tileArray[13] = new Tile(13, Color.YELLOW);
        tileArray[14] = new Tile(0, Color.BLUE);
        tileArray[15] = new Tile(1, Color.RED);
        tileArray[16] = new Tile(2, Color.BLACK);
        tileArray[17] = new Tile(3, Color.YELLOW);
        tileArray[18] = new Tile(4, Color.BLUE);
        tileArray[19] = new Tile(5, Color.RED);
        tileArray[20] = new Tile(6, Color.BLACK);
        tileArray[21] = new Tile(7, Color.YELLOW);
        tileArray[22] = new Tile(8, Color.BLUE);
        tileArray[23] = new Tile(9, Color.RED);

        //System.out.println(Arrays.deepToString(tileArray));
        ClientDeck newDeck = new ClientDeck();
        newDeck.createDeckwithTileArray(tileArray);
        System.out.println(newDeck);
        newDeck.removeTile(1,1); //should remove 13 YELLOW and replace with null
        System.out.println(newDeck);
        Tile[] tileArray2 = newDeck.DeckToTileArray();
        //System.out.println(Arrays.deepToString(tileArray));
        }

}







