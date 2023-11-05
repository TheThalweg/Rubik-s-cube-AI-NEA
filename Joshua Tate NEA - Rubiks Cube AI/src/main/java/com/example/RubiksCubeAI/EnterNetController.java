package com.example.RubiksCubeAI;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class EnterNetController {
    @FXML
    private Pane yellow;
    @FXML
    private Pane red;
    @FXML
    private Pane green;
    @FXML
    private Pane orange;
    @FXML
    private Pane blue;
    @FXML
    private Pane white;

    @FXML
    private Label ErrorLabel;

    private Paint chosenColour = Color.WHITE;

    private final Cube cube = new Cube();

    private final Cube simplified = new Cube();

    final int[][] edgeIndexes = {{1,33},{3,25},{5,17},{7,9},{35,15},{11,23},{19,31},{27,39},{41,21},{43,29},{45,37},{47,13}};
    final int[][] cornerIndexes = {{0,8,34},{2,32,26},{4,24,18},{6,16,10},{40,12,22},{42,20,30},{44,28,38},{46,36,14}};

    //keeps track of all the pieces that are needed for a cube to be solvable
    final private int[] requiredPieces = {
        0x12, 0x13, 0x14, 0x15, 0x52, 0x23, 0x34, 0x45, 0x63, 0x64, 0x65, 0x62, //These are all the edges required
        0x125, 0x154, 0x143, 0x132, 0x623, 0x634, 0x645, 0x652 //These are all the corners required
    };

    @FXML
    public void pickColour(MouseEvent event){
        chosenColour = (((Rectangle)event.getSource()).getFill());
    }

    @FXML
    public void setColour(MouseEvent event){
        ((Rectangle)event.getSource()).setFill(chosenColour);
    }

    static int convertToBit(Paint sticker){
        switch(sticker.toString()){
            case "0xf6ff00ff" -> {return 1;}
            case "0xff0900ff" -> {return 2;}
            case "0x0e7900ff" -> {return 3;}
            case "0xc68400ff" -> {return 4;}
            case "0x02488aff" -> {return 5;}
            case "0xffffffff" -> {return 6;}
        }
        return 0;
    }

    //sets cube side to the values dictated by the net for a given side
    static void convertToCube(int side, Paint[][] array, Cube cube){
        cube.sides[side]=(convertToBit(array[0][0])<<28|convertToBit(array[1][0])<<24|convertToBit(array[2][0])<<20|convertToBit(array[2][1])<<16|
                convertToBit(array[2][2])<<12|convertToBit(array[1][2])<<8|convertToBit(array[0][2])<<4|convertToBit(array[0][1]));
    }

    static int convertToSimplifiedBit(Paint sticker){
        switch(sticker.toString()){
            case "0xf6ff00ff", "0xffffffff" -> {return 1;}
            case "0xff0900ff", "0xc68400ff" -> {return 2;}
            case "0x0e7900ff", "0x02488aff" -> {return 3;}
        }
        return 0;
    }

    //sets cube side to the values dictated by the net for a given side
    static void convertToSimplifiedCube(int side, Paint[][] array, Cube simplified){
        simplified.sides[side]=(convertToSimplifiedBit(array[0][0])<<28|convertToSimplifiedBit(array[1][0])<<24|convertToSimplifiedBit(array[2][0])<<20|convertToSimplifiedBit(array[2][1])<<16|
                convertToSimplifiedBit(array[2][2])<<12|convertToSimplifiedBit(array[1][2])<<8|convertToSimplifiedBit(array[0][2])<<4|convertToSimplifiedBit(array[0][1]));
    }


    private boolean containsPieces(){
        ArrayList<Integer> pieceTracker = new ArrayList<>();
        for(int piece:requiredPieces){
            pieceTracker.add(piece);
        }
        for(int[] edge: edgeIndexes){
            int orientationOne = cube.getColour(edge[0]/8,edge[0]%8)<<4|cube.getColour(edge[1]/8,edge[1]%8);
            int orientationTwo = cube.getColour(edge[1]/8,edge[1]%8)<<4|cube.getColour(edge[0]/8,edge[0]%8);
            if(pieceTracker.contains(orientationOne)){
                pieceTracker.remove(Integer.valueOf(orientationOne));
            } else if(pieceTracker.contains(orientationTwo)){
                pieceTracker.remove(Integer.valueOf(orientationTwo));
            }
        }
        for(int[] corner: cornerIndexes){
            int orientationOne = cube.getColour(corner[0]/8,corner[0]%8)<<8|cube.getColour(corner[1]/8,corner[1]%8)<<4|cube.getColour(corner[2]/8,corner[2]%8);
            int orientationTwo = cube.getColour(corner[0]/8,corner[0]%8)<<4|cube.getColour(corner[1]/8,corner[1]%8)|cube.getColour(corner[2]/8,corner[2]%8)<<8;
            int orientationThree = cube.getColour(corner[0]/8,corner[0]%8)|cube.getColour(corner[1]/8,corner[1]%8)<<8|cube.getColour(corner[2]/8,corner[2]%8)<<4;
            if(pieceTracker.contains(orientationOne)){
                pieceTracker.remove(Integer.valueOf(orientationOne));
            } else if(pieceTracker.contains(orientationTwo)){
                pieceTracker.remove(Integer.valueOf(orientationTwo));
            } else if(pieceTracker.contains(orientationThree)){
                pieceTracker.remove(Integer.valueOf(orientationThree));
            }
        }
        return pieceTracker.size() == 0; //if all the pieces have been accounted for pieceTracker will have a size of 0.
    }
    //checks whether the cornerParity is correct
    private boolean checkCornerParity(){
        int runningTotal = 0;
        //This checks how far each corner is rotated from it's fixed position. A fixed position is the position it is at when white or yellow is on the top or bottom face
        for(int[] corner: cornerIndexes){
            if(simplified.getColour(corner[1]/8,corner[1]%8)==1){
                runningTotal+=1;
            }
            else if((simplified.getColour(corner[2]/8,corner[2]%8)==1)){
                runningTotal+=2;
            }
        }
        return runningTotal%3 == 0;
    }

    //checks whether an even amount of edges are fixed
    private boolean checkEdgeParity(){
        int runningTotal = 0;
        for(int i = 1;i<5;i++) {
            if ((simplified.getColour(0, 2*i-1) != 2) && (simplified.getColour(5-i, 1) != 1)){
                runningTotal++;
            }
            if ((simplified.getColour(5, 2*i-1) != 2) && (simplified.getColour((i)%4+1, 5) != 1)){
                runningTotal++;
            }
        }
        if(simplified.getColour(2,3)!=2&&simplified.getColour(3,7)!=1){
            runningTotal++;
        }
        if(simplified.getColour(2,7)!=2&&simplified.getColour(1,3)!=1){
            runningTotal++;
        }
        if(simplified.getColour(4,7)!=2&&simplified.getColour(3,3)!=1){
            runningTotal++;
        }
        if(simplified.getColour(4,3)!=2&&simplified.getColour(1,7)!=1){
            runningTotal++;
        }
        return runningTotal%2==0;
    }

    //checks whether an even amount of edges have been switched around (Uses disjoint cycles)
    private boolean checkPermutationParity(){
        /*first we need to check the amount of "odd" cycles there are for edges and later for corners.
        Confusingly, odd cycles are odd if they take an even amount of turns to complete.
        At the end if there are an even amount of odd cycles we know the permutation is correct. We can ignore even cycles since even+even=even.
        One can prove by induction that all there can only be an even amount of odd cycles for a cube to be solvable*/
        ArrayList<int[]> visitedEdges = new ArrayList<>();
        int totalParity=0;
        //checks orbits of the edges
        for(int edgeIndex=0; edgeIndex<12 ;edgeIndex++){
            int[] edge = edgeIndexes[edgeIndex];
            if(!visitedEdges.contains(edge)){
                int orbitTracker = 1;
                int orientationOne;
                int orientationTwo;
                do {
                    orbitTracker++;
                    orientationOne = cube.getColour(edge[0] / 8, edge[0] % 8) << 4 | cube.getColour(edge[1] / 8, edge[1] % 8);
                    orientationTwo = cube.getColour(edge[1] / 8, edge[1] % 8) << 4 | cube.getColour(edge[0] / 8, edge[0] % 8);
                    for (int i = 0; i < 12; i++) {
                        if (orientationOne == requiredPieces[i] || orientationTwo == requiredPieces[i]) {
                            edge = edgeIndexes[i];
                            visitedEdges.add(edge);
                        }
                    }
                } while (!(orientationOne==requiredPieces[edgeIndex]||orientationTwo==requiredPieces[edgeIndex]));
                totalParity+=(orbitTracker%2);
            }
        }
        ArrayList<int[]> visitedCorners = new ArrayList<>();
        for(int cornerIndex=0; cornerIndex<8 ;cornerIndex++){
            int[] corner = cornerIndexes[cornerIndex];
            if(!visitedCorners.contains(corner)){
                int orbitTracker = 1;
                int orientationOne;
                int orientationTwo;
                int orientationThree;
                do {
                    orbitTracker++;
                    orientationOne = cube.getColour(corner[0]/8,corner[0]%8)<<8|cube.getColour(corner[1]/8,corner[1]%8)<<4|cube.getColour(corner[2]/8,corner[2]%8);
                    orientationTwo = cube.getColour(corner[0]/8,corner[0]%8)<<4|cube.getColour(corner[1]/8,corner[1]%8)|cube.getColour(corner[2]/8,corner[2]%8)<<8;
                    orientationThree = cube.getColour(corner[0]/8,corner[0]%8)|cube.getColour(corner[1]/8,corner[1]%8)<<8|cube.getColour(corner[2]/8,corner[2]%8)<<4;
                    for (int i = 0; i < 8; i++) {
                        if (orientationOne == requiredPieces[i+12] || orientationTwo == requiredPieces[i+12]|| orientationThree == requiredPieces[i+12]) {
                            corner = cornerIndexes[i];
                            visitedCorners.add(corner);
                        }
                    }
                } while (!(orientationOne==requiredPieces[cornerIndex+12]||orientationTwo==requiredPieces[cornerIndex+12]||orientationThree==requiredPieces[cornerIndex+12]));
                totalParity+=(orbitTracker%2);
            }
        }
        return totalParity%2==0;
    }
    //Used to check whether the cube inputted is actually valid
    private boolean validateCheck(){
        ErrorLabel.setText("");
        if(!containsPieces()){ErrorLabel.setText("You don't have all the required pieces");
            return false;}//if it doesn't contain all the pieces required return false
        boolean satisfiesParity = true;
        if(!checkCornerParity()){ErrorLabel.setText("Corner parity is incorrect");
            satisfiesParity=false;}
        if(!checkEdgeParity()){
            String currentText = ErrorLabel.getText();
            if (currentText == null) {
                ErrorLabel.setText("Edge parity is incorrect"); //adds on error messages. If multiple error messages, append to currentText.
            } else {
                ErrorLabel.setText(currentText + "\nEdge parity is incorrect");
            }
            satisfiesParity=false;}
        if(!checkPermutationParity()){
            String currentText = ErrorLabel.getText();
            if (currentText == null) {
                ErrorLabel.setText("Piece permutation is incorrect");
            } else {
                ErrorLabel.setText(currentText + "\nPiece permutation is incorrect");
            }
            satisfiesParity=false;}
        return satisfiesParity; //if either the corner, edge or permutation parity is incorrect return false
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
    public void retrieveNet() throws IOException {
        AudioClip click = new AudioClip(Objects.requireNonNull(getClass().getResource("/click.mp3")).toExternalForm());
        click.play();
        Paint[][] array = new Paint[3][3];
        for(Node sticker:yellow.getChildren()){
            Rectangle rectangleSticker = (Rectangle)sticker;
            array[(int)(rectangleSticker.getLayoutX()/100)][(int)(rectangleSticker.getLayoutY()/100)] = rectangleSticker.getFill();
        }
        convertToCube(0,array,cube);
        convertToSimplifiedCube(0,array,simplified);
        for(Node sticker:red.getChildren()){
            Rectangle rectangleSticker = (Rectangle)sticker;
            array[(int)(rectangleSticker.getLayoutX()/100)][(int)(rectangleSticker.getLayoutY()/100)] = rectangleSticker.getFill();
        }
        convertToCube(1,array,cube);
        convertToSimplifiedCube(1,array,simplified);
        for(Node sticker:green.getChildren()){
            Rectangle rectangleSticker = (Rectangle)sticker;
            array[(int)(rectangleSticker.getLayoutX()/100)][(int)(rectangleSticker.getLayoutY()/100)] = rectangleSticker.getFill();
        }
        convertToCube(2,array,cube);
        convertToSimplifiedCube(2,array,simplified);
        for(Node sticker:orange.getChildren()){
            Rectangle rectangleSticker = (Rectangle)sticker;
            array[(int)(rectangleSticker.getLayoutX()/100)][(int)(rectangleSticker.getLayoutY()/100)] = rectangleSticker.getFill();
        }
        convertToCube(3,array,cube);
        convertToSimplifiedCube(3,array,simplified);
        for(Node sticker:blue.getChildren()){
            Rectangle rectangleSticker = (Rectangle)sticker;
            array[(int)(rectangleSticker.getLayoutX()/100)][(int)(rectangleSticker.getLayoutY()/100)] = rectangleSticker.getFill();
        }
        convertToCube(4,array,cube);
        convertToSimplifiedCube(4,array,simplified);
        for(Node sticker:white.getChildren()){
            Rectangle rectangleSticker = (Rectangle)sticker;
            array[(int)(rectangleSticker.getLayoutX()/100)][(int)(rectangleSticker.getLayoutY()/100)] = rectangleSticker.getFill();
        }
        convertToCube(5,array,cube);
        convertToSimplifiedCube(5,array,simplified);
        if (validateCheck()){
            FXMLLoader AIMenuLoader = new FXMLLoader(getClass().getResource("AI-Menu.fxml"));
            Parent AIMenu = AIMenuLoader.load();
            AiMenuController AIMenuController = AIMenuLoader.getController();
            AIMenuController.setCube(cube);
            AIMenuController.setSimplified(simplified);
            // Get the Scene from the SceneHolder and change its root node
            Scene scene = SceneHolder.getInstance().getScene();
            scene.setRoot(AIMenu);
            Stage stage = (Stage) scene.getWindow();
            stage.show();
        }
    }
}
