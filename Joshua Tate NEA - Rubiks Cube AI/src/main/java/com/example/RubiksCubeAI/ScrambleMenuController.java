package com.example.RubiksCubeAI;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

import javafx.scene.media.AudioClip;

public class ScrambleMenuController {

    @FXML
    public void exit(){Platform.exit();}

    @FXML
    public void handleScrambleButton() throws IOException {
        AudioClip click = new AudioClip(Objects.requireNonNull(getClass().getResource("/click.mp3")).toExternalForm());
        click.play();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("EnterScramble.fxml"));
        Parent root = loader.load();
        // Get the Scene from the SceneHolder and change its root node
        Scene scene = SceneHolder.getInstance().getScene();
        scene.setRoot(root);
        Stage stage = (Stage) scene.getWindow();
        stage.show();
    }

    @FXML
    public void handleNetButton() throws IOException {
        AudioClip click = new AudioClip(Objects.requireNonNull(getClass().getResource("/click.mp3")).toExternalForm());
        click.play();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("EnterNet.fxml"));
        Parent root = loader.load();
        // Get the Scene from the SceneHolder and change its root node
        Scene scene = SceneHolder.getInstance().getScene();
        scene.setRoot(root);
        Stage stage = (Stage) scene.getWindow();
        stage.show();
    }
}
