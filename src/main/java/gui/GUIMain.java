package gui;

import javafx.application.Application;

import static javafx.application.Application.launch;

public class GUIMain {

    public static void main(String[] args) {
        GameGUI gui = new GameGUI();
        Thread myThread = new Thread(gui);
        myThread.start();

    }
}
