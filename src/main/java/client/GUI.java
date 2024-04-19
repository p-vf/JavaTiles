package client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GUI extends Application implements Runnable  {




    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Simple Window");
        StackPane root = new StackPane();
        Scene scene = new Scene(root, 300, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void run() {
        launch();

    }
}