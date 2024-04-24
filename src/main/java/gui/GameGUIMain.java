package gui;

public class GameGUIMain {
    public static void main(String[] args) {
        MyGameGUI myGameGUI = new MyGameGUI();
        Thread myThread = new Thread(myGameGUI);
        myThread.start();
    }
}
