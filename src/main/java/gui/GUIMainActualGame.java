package gui;

public class GUIMainActualGame {
    public static void main(String[] args) {
        GameGUIActualGame gameGUIActualGame = new GameGUIActualGame();
        Thread myThread = new Thread(gameGUIActualGame);
        myThread.start();
    }
}
