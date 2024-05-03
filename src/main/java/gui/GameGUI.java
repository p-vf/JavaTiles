package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;

/**
 * The GameGUI class represents the graphical user interface for the game application.
 * It extends the Application class and implements the Runnable interface.
 * This class is responsible for initializing and displaying the main GUI of the game.
 */
public class GameGUI extends Application implements Runnable {


    /**
     * The start method is called when the JavaFX application is launched.
     * It loads the FXML file, sets up the scene, and displays the GUI.
     *
     * @param primaryStage the primary stage for the JavaFX application
     * @throws IOException if an error occurs while loading the FXML file
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        // Load the FXML file
        URL fxmlLocation = getClass().getResource("/welcomeScreen.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();

        root.setStyle("-fx-background-color: #008000;");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest((WindowEvent ActionEvent) -> {
            System.out.println("Closing application...");
            System.exit(0);
        });
        primaryStage.show();
        primaryStage.setResizable(false);
    }

    /**
     * The run method is called when the application is run as a separate thread.
     * It launches the JavaFX application.
     */
    public void run() {
        launch();

    }


}
