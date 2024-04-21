package gui;

import javafx.application.Application;

import static javafx.application.Application.launch;

public class GUIMain {

    public static void main(String[] args) {
        GUI gui = new GUI();
        Thread myThread = new Thread(gui);
        myThread.start();

    }
}
