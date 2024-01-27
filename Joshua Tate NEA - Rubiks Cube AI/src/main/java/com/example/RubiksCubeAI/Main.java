package com.example.RubiksCubeAI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {

    // Runs at the start of the program
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader ScrambleMenuLoader = new FXMLLoader(getClass().getResource("Scramble-Menu.fxml"));
        Parent ScrambleMenu = ScrambleMenuLoader.load();
        Scene scene = new Scene(ScrambleMenu, 1920, 1080,true, SceneAntialiasing.BALANCED);
        SceneHolder.getInstance().setScene(scene);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.show();
        stage.show();
    }

    //launches the program
    public static void main(String[] args) {
        launch(args);
    }
}