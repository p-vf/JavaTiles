package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * The GameGUIActualGame class represents the graphical user interface for the actual game portion of the application.
 * It extends the Application class and implements the Runnable interface.
 * This class is responsible for initializing and displaying the GUI for the actual game.
 */
public class GameGUIActualGame extends Application implements Runnable {


    /**
     * The start method is called when the JavaFX application is launched.
     * It loads the FXML file, sets up the scene, and displays the GUI for the actual game.
     *
     * @param primaryStage the primary stage for the JavaFX application
     * @throws IOException if an error occurs while loading the FXML file
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        // Load the FXML file
        URL fxmlLocation = getClass().getResource("/ourgame.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();

        // Set the FXML content as the scene root
        Scene scene = new Scene(root, 500, 300);
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    /**
     * The run method is called when the application is run as a separate thread.
     * It launches the JavaFX application.
     */
    public void run() {
        launch();

    }


}