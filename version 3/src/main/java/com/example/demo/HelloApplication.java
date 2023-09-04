package com.example.demo;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;


public class HelloApplication extends Application {
    static String[] randomScramble(){
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
                if(!Objects.equals(move.charAt(0), scramble.get(scramble.size()-1).charAt(0))){
                scramble.add(move);
            }} catch(Exception E) {scramble.add(move);}
            }
        return scramble.toArray(new String[0]);
        }

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

    static void updateGui(CubeGUI cubeGUI,int sideIndex, Cube cube,Color[] colours){
        //The easiest way to do this is just using a large switch statement which manually updates each side based on the new state of the cube
        //since this function isn't called when updating the GUI it doesn't need to be super quick
        switch (sideIndex) {
            //top side
            case 0 -> {
                //updates all the faces on the top side
                cubeGUI.cubies[0][0][2].alterFace(1, colours[cube.getColour(0, 0) - 1]);
                cubeGUI.cubies[1][0][2].alterFace(1, colours[cube.getColour(0, 1) - 1]);
                cubeGUI.cubies[2][0][2].alterFace(1, colours[cube.getColour(0, 2) - 1]);
                cubeGUI.cubies[2][0][1].alterFace(1, colours[cube.getColour(0, 3) - 1]);
                cubeGUI.cubies[2][0][0].alterFace(1, colours[cube.getColour(0, 4) - 1]);
                cubeGUI.cubies[1][0][0].alterFace(1, colours[cube.getColour(0, 5) - 1]);
                cubeGUI.cubies[0][0][0].alterFace(1, colours[cube.getColour(0, 6) - 1]);
                cubeGUI.cubies[0][0][1].alterFace(1, colours[cube.getColour(0, 7) - 1]);
                //updates the faces on the sides of the top side
                cubeGUI.cubies[0][0][2].alterFace(5, colours[cube.getColour(4, 2) - 1]);
                cubeGUI.cubies[1][0][2].alterFace(5, colours[cube.getColour(4, 1) - 1]);
                cubeGUI.cubies[2][0][2].alterFace(5, colours[cube.getColour(4, 0) - 1]);
                cubeGUI.cubies[2][0][2].alterFace(4, colours[cube.getColour(3, 2) - 1]);
                cubeGUI.cubies[2][0][1].alterFace(4, colours[cube.getColour(3, 1) - 1]);
                cubeGUI.cubies[2][0][0].alterFace(4, colours[cube.getColour(3, 0) - 1]);
                cubeGUI.cubies[2][0][0].alterFace(3, colours[cube.getColour(2, 2) - 1]);
                cubeGUI.cubies[1][0][0].alterFace(3, colours[cube.getColour(2, 1) - 1]);
                cubeGUI.cubies[0][0][0].alterFace(3, colours[cube.getColour(2, 0) - 1]);
                cubeGUI.cubies[0][0][0].alterFace(2, colours[cube.getColour(1, 2) - 1]);
                cubeGUI.cubies[0][0][1].alterFace(2, colours[cube.getColour(1, 1) - 1]);
                cubeGUI.cubies[0][0][2].alterFace(2, colours[cube.getColour(1, 0) - 1]);
            }
            case 1 -> {
                //updates all the faces on the left side
                cubeGUI.cubies[0][0][2].alterFace(2, colours[cube.getColour(1, 0) - 1]);
                cubeGUI.cubies[0][0][1].alterFace(2, colours[cube.getColour(1, 1) - 1]);
                cubeGUI.cubies[0][0][0].alterFace(2, colours[cube.getColour(1, 2) - 1]);
                cubeGUI.cubies[0][1][0].alterFace(2, colours[cube.getColour(1, 3) - 1]);
                cubeGUI.cubies[0][2][0].alterFace(2, colours[cube.getColour(1, 4) - 1]);
                cubeGUI.cubies[0][2][1].alterFace(2, colours[cube.getColour(1, 5) - 1]);
                cubeGUI.cubies[0][2][2].alterFace(2, colours[cube.getColour(1, 6) - 1]);
                cubeGUI.cubies[0][1][2].alterFace(2, colours[cube.getColour(1, 7) - 1]);
                //updates all the faces on the sides of the left side
                cubeGUI.cubies[0][0][2].alterFace(1, colours[cube.getColour(0, 0) - 1]);
                cubeGUI.cubies[0][0][1].alterFace(1, colours[cube.getColour(0, 7) - 1]);
                cubeGUI.cubies[0][0][0].alterFace(1, colours[cube.getColour(0, 6) - 1]);
                cubeGUI.cubies[0][0][0].alterFace(3, colours[cube.getColour(2, 0) - 1]);
                cubeGUI.cubies[0][1][0].alterFace(3, colours[cube.getColour(2, 7) - 1]);
                cubeGUI.cubies[0][2][0].alterFace(3, colours[cube.getColour(2, 6) - 1]);
                cubeGUI.cubies[0][2][0].alterFace(6, colours[cube.getColour(5, 0) - 1]);
                cubeGUI.cubies[0][2][1].alterFace(6, colours[cube.getColour(5, 7) - 1]);
                cubeGUI.cubies[0][2][2].alterFace(6, colours[cube.getColour(5, 6) - 1]);
                cubeGUI.cubies[0][2][2].alterFace(5, colours[cube.getColour(4, 4) - 1]);
                cubeGUI.cubies[0][1][2].alterFace(5, colours[cube.getColour(4, 3) - 1]);
                cubeGUI.cubies[0][0][2].alterFace(5, colours[cube.getColour(4, 2) - 1]);
            }
            case 2 -> {
                //updates all the faces on the front side
                cubeGUI.cubies[0][0][0].alterFace(3, colours[cube.getColour(2, 0) - 1]);
                cubeGUI.cubies[1][0][0].alterFace(3, colours[cube.getColour(2, 1) - 1]);
                cubeGUI.cubies[2][0][0].alterFace(3, colours[cube.getColour(2, 2) - 1]);
                cubeGUI.cubies[2][1][0].alterFace(3, colours[cube.getColour(2, 3) - 1]);
                cubeGUI.cubies[2][2][0].alterFace(3, colours[cube.getColour(2, 4) - 1]);
                cubeGUI.cubies[1][2][0].alterFace(3, colours[cube.getColour(2, 5) - 1]);
                cubeGUI.cubies[0][2][0].alterFace(3, colours[cube.getColour(2, 6) - 1]);
                cubeGUI.cubies[0][1][0].alterFace(3, colours[cube.getColour(2, 7) - 1]);
                //updates all the faces on the sides of the front side
                cubeGUI.cubies[0][0][0].alterFace(1, colours[cube.getColour(0, 6) - 1]);
                cubeGUI.cubies[1][0][0].alterFace(1, colours[cube.getColour(0, 5) - 1]);
                cubeGUI.cubies[2][0][0].alterFace(1, colours[cube.getColour(0, 4) - 1]);
                cubeGUI.cubies[2][0][0].alterFace(4, colours[cube.getColour(3, 0) - 1]);
                cubeGUI.cubies[2][1][0].alterFace(4, colours[cube.getColour(3, 7) - 1]);
                cubeGUI.cubies[2][2][0].alterFace(4, colours[cube.getColour(3, 6) - 1]);
                cubeGUI.cubies[2][2][0].alterFace(6, colours[cube.getColour(5, 2) - 1]);
                cubeGUI.cubies[1][2][0].alterFace(6, colours[cube.getColour(5, 1) - 1]);
                cubeGUI.cubies[0][2][0].alterFace(6, colours[cube.getColour(5, 0) - 1]);
                cubeGUI.cubies[0][2][0].alterFace(2, colours[cube.getColour(1, 4) - 1]);
                cubeGUI.cubies[0][1][0].alterFace(2, colours[cube.getColour(1, 3) - 1]);
                cubeGUI.cubies[0][0][0].alterFace(2, colours[cube.getColour(1, 2) - 1]);
            }
            case 3 -> {
                //updates all the faces on the right side
                cubeGUI.cubies[2][0][0].alterFace(4, colours[cube.getColour(3, 0) - 1]);
                cubeGUI.cubies[2][0][1].alterFace(4, colours[cube.getColour(3, 1) - 1]);
                cubeGUI.cubies[2][0][2].alterFace(4, colours[cube.getColour(3, 2) - 1]);
                cubeGUI.cubies[2][1][2].alterFace(4, colours[cube.getColour(3, 3) - 1]);
                cubeGUI.cubies[2][2][2].alterFace(4, colours[cube.getColour(3, 4) - 1]);
                cubeGUI.cubies[2][2][1].alterFace(4, colours[cube.getColour(3, 5) - 1]);
                cubeGUI.cubies[2][2][0].alterFace(4, colours[cube.getColour(3, 6) - 1]);
                cubeGUI.cubies[2][1][0].alterFace(4, colours[cube.getColour(3, 7) - 1]);
                //updates all the faces on the sides of the right side
                cubeGUI.cubies[2][0][0].alterFace(1, colours[cube.getColour(0, 4) - 1]);
                cubeGUI.cubies[2][0][1].alterFace(1, colours[cube.getColour(0, 3) - 1]);
                cubeGUI.cubies[2][0][2].alterFace(1, colours[cube.getColour(0, 2) - 1]);
                cubeGUI.cubies[2][0][2].alterFace(5, colours[cube.getColour(4, 0) - 1]);
                cubeGUI.cubies[2][1][2].alterFace(5, colours[cube.getColour(4, 7) - 1]);
                cubeGUI.cubies[2][2][2].alterFace(5, colours[cube.getColour(4, 6) - 1]);
                cubeGUI.cubies[2][2][2].alterFace(6, colours[cube.getColour(5, 4) - 1]);
                cubeGUI.cubies[2][2][1].alterFace(6, colours[cube.getColour(5, 3) - 1]);
                cubeGUI.cubies[2][2][0].alterFace(6, colours[cube.getColour(5, 2) - 1]);
                cubeGUI.cubies[2][2][0].alterFace(3, colours[cube.getColour(2, 4) - 1]);
                cubeGUI.cubies[2][1][0].alterFace(3, colours[cube.getColour(2, 3) - 1]);
                cubeGUI.cubies[2][0][0].alterFace(3, colours[cube.getColour(2, 2) - 1]);
            }
            case 4 -> {
                //updates all the faces on the back side
                cubeGUI.cubies[2][0][2].alterFace(5, colours[cube.getColour(4, 0) - 1]);
                cubeGUI.cubies[1][0][2].alterFace(5, colours[cube.getColour(4, 1) - 1]);
                cubeGUI.cubies[0][0][2].alterFace(5, colours[cube.getColour(4, 2) - 1]);
                cubeGUI.cubies[0][1][2].alterFace(5, colours[cube.getColour(4, 3) - 1]);
                cubeGUI.cubies[0][2][2].alterFace(5, colours[cube.getColour(4, 4) - 1]);
                cubeGUI.cubies[1][2][2].alterFace(5, colours[cube.getColour(4, 5) - 1]);
                cubeGUI.cubies[2][2][2].alterFace(5, colours[cube.getColour(4, 6) - 1]);
                cubeGUI.cubies[2][1][2].alterFace(5, colours[cube.getColour(4, 7) - 1]);
                //updates all the faces on the sides of the back side
                cubeGUI.cubies[2][0][2].alterFace(1, colours[cube.getColour(0, 2) - 1]);
                cubeGUI.cubies[1][0][2].alterFace(1, colours[cube.getColour(0, 1) - 1]);
                cubeGUI.cubies[0][0][2].alterFace(1, colours[cube.getColour(0, 0) - 1]);
                cubeGUI.cubies[0][0][2].alterFace(2, colours[cube.getColour(1, 0) - 1]);
                cubeGUI.cubies[0][1][2].alterFace(2, colours[cube.getColour(1, 7) - 1]);
                cubeGUI.cubies[0][2][2].alterFace(2, colours[cube.getColour(1, 6) - 1]);
                cubeGUI.cubies[0][2][2].alterFace(6, colours[cube.getColour(5, 6) - 1]);
                cubeGUI.cubies[1][2][2].alterFace(6, colours[cube.getColour(5, 5) - 1]);
                cubeGUI.cubies[2][2][2].alterFace(6, colours[cube.getColour(5, 4) - 1]);
                cubeGUI.cubies[2][2][2].alterFace(4, colours[cube.getColour(3, 4) - 1]);
                cubeGUI.cubies[2][1][2].alterFace(4, colours[cube.getColour(3, 3) - 1]);
                cubeGUI.cubies[2][0][2].alterFace(4, colours[cube.getColour(3, 2) - 1]);
            }
            case 5 -> {
                //updates all the faces on the bottom side
                cubeGUI.cubies[0][2][0].alterFace(6, colours[cube.getColour(5, 0) - 1]);
                cubeGUI.cubies[1][2][0].alterFace(6, colours[cube.getColour(5, 1) - 1]);
                cubeGUI.cubies[2][2][0].alterFace(6, colours[cube.getColour(5, 2) - 1]);
                cubeGUI.cubies[2][2][1].alterFace(6, colours[cube.getColour(5, 3) - 1]);
                cubeGUI.cubies[2][2][2].alterFace(6, colours[cube.getColour(5, 4) - 1]);
                cubeGUI.cubies[1][2][2].alterFace(6, colours[cube.getColour(5, 5) - 1]);
                cubeGUI.cubies[0][2][2].alterFace(6, colours[cube.getColour(5, 6) - 1]);
                cubeGUI.cubies[0][2][1].alterFace(6, colours[cube.getColour(5, 7) - 1]);
                //updates all the faces on the side of the bottom side
                cubeGUI.cubies[0][2][0].alterFace(3, colours[cube.getColour(2, 6) - 1]);
                cubeGUI.cubies[1][2][0].alterFace(3, colours[cube.getColour(2, 5) - 1]);
                cubeGUI.cubies[2][2][0].alterFace(3, colours[cube.getColour(2, 4) - 1]);
                cubeGUI.cubies[2][2][0].alterFace(4, colours[cube.getColour(3, 6) - 1]);
                cubeGUI.cubies[2][2][1].alterFace(4, colours[cube.getColour(3, 5) - 1]);
                cubeGUI.cubies[2][2][2].alterFace(4, colours[cube.getColour(3, 4) - 1]);
                cubeGUI.cubies[2][2][2].alterFace(5, colours[cube.getColour(4, 6) - 1]);
                cubeGUI.cubies[1][2][2].alterFace(5, colours[cube.getColour(4, 5) - 1]);
                cubeGUI.cubies[0][2][2].alterFace(5, colours[cube.getColour(4, 4) - 1]);
                cubeGUI.cubies[0][2][2].alterFace(2, colours[cube.getColour(1, 6) - 1]);
                cubeGUI.cubies[0][2][1].alterFace(2, colours[cube.getColour(1, 5) - 1]);
                cubeGUI.cubies[0][2][0].alterFace(2, colours[cube.getColour(1, 4) - 1]);
            }
        }
    }
//updates entire cube (To be applied after scrambles)
    static void updateFull(CubeGUI cubeGUI,Cube cube, Color[] colours){
        cubeGUI.cubies[0][0][2].alterFace(1, colours[cube.getColour(0, 0) - 1]);
        cubeGUI.cubies[1][0][2].alterFace(1, colours[cube.getColour(0, 1) - 1]);
        cubeGUI.cubies[2][0][2].alterFace(1, colours[cube.getColour(0, 2) - 1]);
        cubeGUI.cubies[2][0][1].alterFace(1, colours[cube.getColour(0, 3) - 1]);
        cubeGUI.cubies[2][0][0].alterFace(1, colours[cube.getColour(0, 4) - 1]);
        cubeGUI.cubies[1][0][0].alterFace(1, colours[cube.getColour(0, 5) - 1]);
        cubeGUI.cubies[0][0][0].alterFace(1, colours[cube.getColour(0, 6) - 1]);
        cubeGUI.cubies[0][0][1].alterFace(1, colours[cube.getColour(0, 7) - 1]);
        cubeGUI.cubies[0][0][2].alterFace(2, colours[cube.getColour(1, 0) - 1]);
        cubeGUI.cubies[0][0][1].alterFace(2, colours[cube.getColour(1, 1) - 1]);
        cubeGUI.cubies[0][0][0].alterFace(2, colours[cube.getColour(1, 2) - 1]);
        cubeGUI.cubies[0][1][0].alterFace(2, colours[cube.getColour(1, 3) - 1]);
        cubeGUI.cubies[0][2][0].alterFace(2, colours[cube.getColour(1, 4) - 1]);
        cubeGUI.cubies[0][2][1].alterFace(2, colours[cube.getColour(1, 5) - 1]);
        cubeGUI.cubies[0][2][2].alterFace(2, colours[cube.getColour(1, 6) - 1]);
        cubeGUI.cubies[0][1][2].alterFace(2, colours[cube.getColour(1, 7) - 1]);
        cubeGUI.cubies[0][0][0].alterFace(3, colours[cube.getColour(2, 0) - 1]);
        cubeGUI.cubies[1][0][0].alterFace(3, colours[cube.getColour(2, 1) - 1]);
        cubeGUI.cubies[2][0][0].alterFace(3, colours[cube.getColour(2, 2) - 1]);
        cubeGUI.cubies[2][1][0].alterFace(3, colours[cube.getColour(2, 3) - 1]);
        cubeGUI.cubies[2][2][0].alterFace(3, colours[cube.getColour(2, 4) - 1]);
        cubeGUI.cubies[1][2][0].alterFace(3, colours[cube.getColour(2, 5) - 1]);
        cubeGUI.cubies[0][2][0].alterFace(3, colours[cube.getColour(2, 6) - 1]);
        cubeGUI.cubies[0][1][0].alterFace(3, colours[cube.getColour(2, 7) - 1]);
        cubeGUI.cubies[2][0][0].alterFace(4, colours[cube.getColour(3, 0) - 1]);
        cubeGUI.cubies[2][0][1].alterFace(4, colours[cube.getColour(3, 1) - 1]);
        cubeGUI.cubies[2][0][2].alterFace(4, colours[cube.getColour(3, 2) - 1]);
        cubeGUI.cubies[2][1][2].alterFace(4, colours[cube.getColour(3, 3) - 1]);
        cubeGUI.cubies[2][2][2].alterFace(4, colours[cube.getColour(3, 4) - 1]);
        cubeGUI.cubies[2][2][1].alterFace(4, colours[cube.getColour(3, 5) - 1]);
        cubeGUI.cubies[2][2][0].alterFace(4, colours[cube.getColour(3, 6) - 1]);
        cubeGUI.cubies[2][1][0].alterFace(4, colours[cube.getColour(3, 7) - 1]);
        cubeGUI.cubies[2][0][2].alterFace(5, colours[cube.getColour(4, 0) - 1]);
        cubeGUI.cubies[1][0][2].alterFace(5, colours[cube.getColour(4, 1) - 1]);
        cubeGUI.cubies[0][0][2].alterFace(5, colours[cube.getColour(4, 2) - 1]);
        cubeGUI.cubies[0][1][2].alterFace(5, colours[cube.getColour(4, 3) - 1]);
        cubeGUI.cubies[0][2][2].alterFace(5, colours[cube.getColour(4, 4) - 1]);
        cubeGUI.cubies[1][2][2].alterFace(5, colours[cube.getColour(4, 5) - 1]);
        cubeGUI.cubies[2][2][2].alterFace(5, colours[cube.getColour(4, 6) - 1]);
        cubeGUI.cubies[2][1][2].alterFace(5, colours[cube.getColour(4, 7) - 1]);
        cubeGUI.cubies[0][2][0].alterFace(6, colours[cube.getColour(5, 0) - 1]);
        cubeGUI.cubies[1][2][0].alterFace(6, colours[cube.getColour(5, 1) - 1]);
        cubeGUI.cubies[2][2][0].alterFace(6, colours[cube.getColour(5, 2) - 1]);
        cubeGUI.cubies[2][2][1].alterFace(6, colours[cube.getColour(5, 3) - 1]);
        cubeGUI.cubies[2][2][2].alterFace(6, colours[cube.getColour(5, 4) - 1]);
        cubeGUI.cubies[1][2][2].alterFace(6, colours[cube.getColour(5, 5) - 1]);
        cubeGUI.cubies[0][2][2].alterFace(6, colours[cube.getColour(5, 6) - 1]);
        cubeGUI.cubies[0][2][1].alterFace(6, colours[cube.getColour(5, 7) - 1]);

    }
    private double mouseOldX, mouseOldY;
    //some constants for the window size and the camera positions
    private static final double windowX = 800;
    private static final double windowY = 800;
    private static final double CAMERA_X = -windowX/2;
    private static final double CAMERA_Y = -windowY/2;
    private static final double CAMERA_Z = -100;
    Color[] colours = {Color.YELLOW, Color.RED, Color.GREEN, Color.ORANGE,Color.BLUE,Color.WHITE};
    @Override
    public void start(Stage stage) {
        //creates a new cube object which holds all the actual information about the current state of the cube
        Cube cube = new Cube();
        //creates camera for the scene
        Camera camera = new PerspectiveCamera();
        camera.translateXProperty().set(CAMERA_X);
        camera.translateYProperty().set(CAMERA_Y);
        camera.translateZProperty().set(CAMERA_Z);
        //sets the render distance for the cameras. Any objects between these numbers will be rendered
        camera.setNearClip(0.1);
        camera.setFarClip(1000);
        //creates new cube for the GUI (updated separately to main cube object). No calculations are done with this cube, it is purely for aesthetics
        CubeGUI cubeGUI = new CubeGUI();
        cubeGUI.initialize();
        Group group = new Group();
        group.getChildren().add(cubeGUI.getModel());
        //creates the scene, adds the camera, sets the background
        Scene scene = new Scene(group, windowX, windowY,true, SceneAntialiasing.BALANCED);
        scene.setCamera(camera);
        scene.setFill(Color.AQUA);
        stage.setTitle("Gui Test");
        stage.setScene(scene);
        float duration = 240;
        //Creates event handlers for keyboard inputs. When shift is being held the boolean variable prime is set to true.
        //This allows for anti-clockwise moves to be performed.
        AtomicBoolean prime = new AtomicBoolean(false);
        scene.addEventHandler(KeyEvent.KEY_RELEASED,event ->{
            if(event.getCode() == KeyCode.SHIFT){
                prime.set(false);
            }
        });
        scene.addEventHandler(KeyEvent.KEY_PRESSED,event ->{
            switch (event.getCode()) {
                case SHIFT -> prime.set(true);
                //for each case if prime is true then an anticlockwise turn is made, otherwise a clockwise move is made
                //the gui cube is then updated using updateGUI
                case W -> {
                    if (prime.get()) {
                        cube.antiClockwise(0);
                        cubeGUI.rotateY(-1,1,duration, cube, colours);
                    } else {
                        cube.clockwise(0);
                        cubeGUI.rotateY(-1,-1,duration, cube, colours);
                    }
                }
                case A -> {
                    if (prime.get()) {
                        cube.antiClockwise(1);
                        cubeGUI.rotateX(-1,1,duration, cube, colours);
                    } else {
                        cube.clockwise(1);
                        cubeGUI.rotateX(-1,-1,duration, cube, colours);
                    }
                }
                case S -> {
                    if (prime.get()) {
                        cube.antiClockwise(2);
                        cubeGUI.rotateZ(-1,1,duration, cube, colours);
                    } else {
                        cube.clockwise(2);
                        cubeGUI.rotateZ(-1,-1,duration, cube, colours);
                    }
                }
                case D -> {
                    if (prime.get()) {
                        cube.antiClockwise(3);
                        cubeGUI.rotateX(1,-1,duration, cube, colours);
                    } else {
                        cube.clockwise(3);
                        cubeGUI.rotateX(1,1,duration, cube, colours);
                    }

                }
                case F -> {
                    if (prime.get()) {
                        cube.antiClockwise(4);
                        cubeGUI.rotateZ(1,-1,duration, cube, colours);
                    } else {
                        cube.clockwise(4);
                        cubeGUI.rotateZ(1,1,duration, cube, colours);
                    }
                }
                case X -> {
                    if (prime.get()) {
                        cube.antiClockwise(5);
                        cubeGUI.rotateY(1,-1,duration, cube, colours);
                    } else {
                        cube.clockwise(5);
                        cubeGUI.rotateY(1,1,duration, cube, colours);
                    }
                }
            }

        });

        //event controller for the camera, allowing for the camera to be operated using the dragging of the mouse.
        scene.setOnMousePressed(event -> {
            mouseOldX = event.getSceneX();
            mouseOldY = event.getSceneY();
        });
        scene.setOnMouseDragged(event -> {
            double rotX = event.getSceneY() - mouseOldY;
            double rotY = event.getSceneX() - mouseOldX;
            camera.getTransforms().addAll(new Rotate(-rotX, - CAMERA_X, - CAMERA_Y,  - CAMERA_Z, Rotate.X_AXIS), new Rotate(rotY,  - CAMERA_X,  - CAMERA_Y, - CAMERA_Z, Rotate.Y_AXIS));
            mouseOldX = event.getSceneX();
            mouseOldY = event.getSceneY();
        });
        stage.show();
        CornerPerms cornerPerms = new CornerPerms();
        HashSet<Long> perms = cornerPerms.calculatePerms(new Node(cube,null,null));
        String[] scramble = randomScramble();
        //String[] scramble = {"D"};
        //String[] scramble = {"D2","L2","F'","D'","F","L","U","L2","B2","R","L2","U'","L","B'","L'","U2","F'","U2","L2","B2"};
        System.out.println(Arrays.toString(scramble));
        performMoves(cube,scramble);
        //creates simplified version of the cube for use in thistlethwaite stages 1 to 3. Opposite sides are set to equal
        Cube simplified = new Cube();
        simplified.sides[3] = 0x22222222;
        simplified.sides[4] = 0x33333333;
        simplified.sides[5] = 0x11111111;
        cube.displayCube();
        updateFull(cubeGUI,cube,colours);
        performMoves(simplified,scramble);
        Thistle thistle = new Thistle(cube,perms, simplified, cubeGUI, colours, duration);
        //thistle is operated in a separate thread as this means that you can run the GUI in the background without it pausing
        //thistle.start();
        Beginners beginner = new Beginners(cube,cubeGUI,colours, duration);
        //beginner.start();
    }

    //launches the program
    public static void main(String[] args) {
        launch(args);
    }
}