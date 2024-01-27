package com.example.RubiksCubeAI;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.BrokenBarrierException;


public class AiMenuController{

    private boolean inputBlocked = false;

    private Cube cube;
    private Cube simplified;

    private final Color[] colours = {Color.YELLOW, Color.RED, Color.GREEN, Color.ORANGE,Color.BLUE,Color.WHITE};

    public void setCube(Cube cube){
        this.cube = cube;
    }

    public void setSimplified(Cube simplified){
        this.simplified = simplified;
    }


    @FXML
    public void exit(){
        Platform.exit();
    }

    @FXML
    public void homeButton() throws IOException {
        AudioClip click = new AudioClip(Objects.requireNonNull(getClass().getResource("/click.mp3")).toExternalForm());
        click.play();
        FXMLLoader ScrambleMenuLoader = new FXMLLoader(getClass().getResource("Scramble-Menu.fxml"));
        Parent root = ScrambleMenuLoader.load();
        // Get the Scene from the SceneHolder and change its root node
        Scene scene = SceneHolder.getInstance().getScene();
        scene.setRoot(root);
        Stage stage = (Stage) scene.getWindow();
        stage.show();
    }

    @FXML
    public void handleCFOPButtonAction() throws IOException, InterruptedException {
        AudioClip click = new AudioClip(Objects.requireNonNull(getClass().getResource("/click.mp3")).toExternalForm());
        click.play();
        // Load the new FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("cubeScene.fxml"));
        Parent root = loader.load();
        CubeController cubeController = loader.getController();
        cubeController.setCube(cube);
        cubeController.setSimplified(simplified);
        // Get the Scene from the SceneHolder and change its root node
        Scene scene = SceneHolder.getInstance().getScene();
        scene.setOnKeyPressed(e-> {
            try {
                if(!inputBlocked){inputBlocked = true;
                    cubeController.handlePress(e);
                Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1), f -> inputBlocked = false));
                timeline.play();
                }
            } catch (BrokenBarrierException | InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        });
        scene.setOnKeyReleased(cubeController::handleRelease);
        scene.setRoot(root);
        Stage stage = (Stage) scene.getWindow();
        stage.show();
        cubeController.setAI(true,false);
        cubeController.updateFull(cube,colours);
        cubeController.solveCube();
    }

    @FXML
    public void handleBeginnerButtonAction() throws IOException, InterruptedException {
        AudioClip click = new AudioClip(Objects.requireNonNull(getClass().getResource("/click.mp3")).toExternalForm());
        click.play();
        // Load the new FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("cubeScene.fxml"));
        Parent root = loader.load();
        CubeController cubeController = loader.getController();
        cubeController.setCube(cube);
        cubeController.setSimplified(simplified);
        // Get the Scene from the SceneHolder and change its root node
        Scene scene = SceneHolder.getInstance().getScene();
        scene.setOnKeyPressed(e-> {
            try {
                if(!inputBlocked){inputBlocked = true;
                    cubeController.handlePress(e);
                    Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1), f -> inputBlocked = false));
                    timeline.play();
                }
            } catch (BrokenBarrierException | InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        });
        scene.setOnKeyReleased(cubeController::handleRelease);
        scene.setRoot(root);
        Stage stage = (Stage) scene.getWindow();
        stage.show();
        cubeController.setAI(false,true);
        cubeController.updateFull(cube,colours);
        cubeController.solveCube();
    }

    @FXML
    public void handleThistleButtonAction() throws IOException, InterruptedException {
        AudioClip click = new AudioClip(Objects.requireNonNull(getClass().getResource("/click.mp3")).toExternalForm());
        click.play();
        // Load the new FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("cubeScene.fxml"));
        Parent root = loader.load();
        CubeController cubeController = loader.getController();
        cubeController.setCube(cube);
        cubeController.setSimplified(simplified);

        // Get the Scene from the SceneHolder and change its root node
        Scene scene = SceneHolder.getInstance().getScene();
        scene.setRoot(root);
        Stage stage = (Stage) scene.getWindow();
        stage.show();
        cubeController.updateFull(cube,colours);
        cubeController.solveCube();
    }
}
