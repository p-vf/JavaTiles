package client;

public class MainGUI {

    public static void main(String[] args){
        GUIThread myThread = new GUIThread();
        Thread myThready = new Thread(myThread);
        myThready.start();
    }
}
