package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class GUI extends Application implements Runnable {



    @Override
    public void start(Stage primaryStage) throws IOException {
        // Load the FXML file
        URL fxmlLocation = getClass().getResource("/login.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();

        // Set the FXML content as the scene root
        Scene scene = new Scene(root, 500, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public void run() {
        launch();

    }


}
