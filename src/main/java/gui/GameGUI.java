package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;

public class GameGUI extends Application implements Runnable {



    @Override
    public void start(Stage primaryStage) throws IOException {
        // Load the FXML file
        URL fxmlLocation = getClass().getResource("/login.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();

        root.setStyle("-fx-background-color: #008000;");
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest((WindowEvent ActionEvent) -> {
            System.out.println("Closing application...");
            System.exit(0);
        });
        primaryStage.show();
        primaryStage.setResizable(false);
    }
    public void run() {
        launch();

    }


}
