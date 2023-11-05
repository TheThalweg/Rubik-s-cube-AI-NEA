package com.example.RubiksCubeAI;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class EnterScrambleController {

    @FXML
    private Label ErrorLabel;

    @FXML
    private TextField inputTextField;

    private String scramble;

    private final String[] allowedMoves = {"U","U2","U'","L","L2","L'","F","F2","F'","R","R2","R'","B","B2","B'","D","D2","D'"};

    static void performMoves(Cube cube,String[] moveSet) {
        for(String move:moveSet) {
            switch (move) {
                case "U" -> cube.clockwise(0);
                case "U'" -> cube.antiClockwise(0);
                case "U2" -> cube.doubleTwist(0);
                case "L" -> cube.clockwise(1);
                case "L'" -> cube.antiClockwise(1);
                case "L2" -> cube.doubleTwist(1);
                case "F" -> cube.clockwise(2);
                case "F'" -> cube.antiClockwise(2);
                case "F2" -> cube.doubleTwist(2);
                case "R" -> cube.clockwise(3);
                case "R'" -> cube.antiClockwise(3);
                case "R2" -> cube.doubleTwist(3);
                case "B" -> cube.clockwise(4);
                case "B'" -> cube.antiClockwise(4);
                case "B2" -> cube.doubleTwist(4);
                case "D" -> cube.clockwise(5);
                case "D'" -> cube.antiClockwise(5);
                case "D2" -> cube.doubleTwist(5);
                default -> {
                    System.out.print(move);
                    System.out.println(" is not a valid move");
                }
            }
        }
    }

    public void randomScramble() throws IOException {
        AudioClip click = new AudioClip(Objects.requireNonNull(getClass().getResource("/click.mp3")).toExternalForm());
        click.play();
        ArrayList<String> scramble = new ArrayList<>();
        String move = null;
        int randomNum;
        while(scramble.size()<20){
            randomNum = (int)(Math.random() * 18)+1;
            switch (randomNum) {
                case 1 -> move = "U";
                case 2 -> move = "U'";
                case 3 -> move = "U2";
                case 4 -> move = "L";
                case 5 -> move = "L'";
                case 6 -> move = "L2";
                case 7 -> move = "F";
                case 8 -> move = "F'";
                case 9 -> move = "F2";
                case 10 -> move = "R";
                case 11 -> move = "R'";
                case 12 -> move = "R2";
                case 13 -> move = "B";
                case 14 -> move = "B'";
                case 15 -> move = "B2";
                case 16 -> move = "D";
                case 17 -> move = "D'";
                case 18 -> move = "D2";
            }
            try{
                assert move != null;
                if(!(Objects.equals(move.charAt(0), scramble.get(scramble.size()-1).charAt(0))||(move.charAt(0))=='R'&&scramble.get(scramble.size()-1).charAt(0)=='L'
                ||(move.charAt(0))=='B'&&scramble.get(scramble.size()-1).charAt(0)=='F'||(move.charAt(0))=='D'&&scramble.get(scramble.size()-1).charAt(0)=='U'
                )){
                    scramble.add(move);
                }} catch(Exception E) {scramble.add(move);}
        }
        FXMLLoader AIMenuLoader = new FXMLLoader(getClass().getResource("AI-Menu.fxml"));
        Parent AIMenu = AIMenuLoader.load();
        AiMenuController AIMenuController = AIMenuLoader.getController();
        Cube cube = new Cube();
        performMoves(cube, scramble.toArray(new String[0]));
        Cube simplified = new Cube();
        simplified.sides[3] = 0x22222222;
        simplified.sides[4] = 0x33333333;
        simplified.sides[5] = 0x11111111;
        performMoves(simplified,scramble.toArray(new String[0]));
        AIMenuController.setCube(cube);
        AIMenuController.setSimplified(simplified);
        // Get the Scene from the SceneHolder and change its root node
        Scene scene = SceneHolder.getInstance().getScene();
        scene.setRoot(AIMenu);
        Stage stage = (Stage) scene.getWindow();
        stage.show();
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
    private void retrieveScramble() throws IOException {
        AudioClip click = new AudioClip(Objects.requireNonNull(getClass().getResource("/click.mp3")).toExternalForm());
        click.play();
        scramble = inputTextField.getText();
        if (validationCheck()){
            FXMLLoader AIMenuLoader = new FXMLLoader(getClass().getResource("AI-Menu.fxml"));
            Parent AIMenu = AIMenuLoader.load();
            AiMenuController AIMenuController = AIMenuLoader.getController();
            Cube cube = new Cube();
            performMoves(cube,scramble.split(" "));
            Cube simplified = new Cube();
            simplified.sides[3] = 0x22222222;
            simplified.sides[4] = 0x33333333;
            simplified.sides[5] = 0x11111111;
            performMoves(simplified,scramble.split(" "));
            AIMenuController.setCube(cube);
            AIMenuController.setSimplified(simplified);
            // Get the Scene from the SceneHolder and change its root node
            Scene scene = SceneHolder.getInstance().getScene();
            scene.setRoot(AIMenu);
            Stage stage = (Stage) scene.getWindow();
            stage.show();
        } else {ErrorLabel.setText("You have entered an invalid scramble");}
    }

    private boolean validationCheck(){
        String[] splitScramble = scramble.split(" ");
        for(String move:splitScramble){
            if(!Arrays.asList(allowedMoves).contains(move)){
                return false;
            }
        }
        return true;
    }
}
